//
//  NextDeparturesListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "TransportNextDeparturesViewController.h"

#import "TransportService.h"

#import "FavoriteStationsViewController.h"

#import "TransportUtils.h"

#import "NextDeparturesCell.h"

#import "DestinationConnectionsListViewController.h"

#import "TransportHelpViewController.h"

#import "TransportSettingsViewController.h"

#import "TransportController.h"

#import "ObjectArchiver.h"

#import "TransportStationsManagerViewController.h"

#import "TransportAddStationViewController.h"


typedef enum {
    UserStationsStateOK = 0,
    UserStationsStateLoadingDefault,
    UserStationsStateErrorNeedTwo,
} UserStationsState;

typedef enum {
    LocationStateWaiting = 0, //when LocationState is not applicable because user stations are not OK yet for example
    LocationStateLocating,
    LocationStateLocated,
    LocationStateManualSelection,
    LocationStateErrorUserDenied,
    LocationStateErrorTimeout,
    LocationStateErrorUnknown,
} LocationState;

typedef enum {
    SchedulesStateWaiting = 0, //when SchedulesState is not applicable because UserStationsState or LocationState are not OK/determined yet for example
    SchedulesStateLoading,
    SchedulesStateLoaded,
    SchedulesStateError,
} SchedulesState;

static double kSchedulesValidy = 20.0; //number of seconds that a schedule is considered valid and thus refresh is not necessary

@interface TransportNextDeparturesViewController ()<TransportServiceDelegate>

@property (nonatomic, strong) IBOutlet UIView* topContainerView;
@property (nonatomic, strong) IBOutlet UIButton* locationButton;
@property (nonatomic, strong) IBOutlet UILabel* fromLabel;
@property (nonatomic, strong) IBOutlet UITableView* tableView;
@property (nonatomic, strong) IBOutlet UILabel* centerMessageLabel;
@property (nonatomic, strong) IBOutlet UIToolbar* toolbar;

@property (nonatomic, strong) TransportService* transportService;
@property (nonatomic, strong) NSOrderedSet* usersStations;
@property (nonatomic, strong) NSMutableDictionary* tripResults; //key : destination station name, value : QueryTripResult
@property (nonatomic) LocationState locationState;
@property (nonatomic) SchedulesState schedulesState;
@property (nonatomic) TransportStation* departureStation;
@property (nonatomic) UserStationsState userStationsState;
@property (nonatomic, strong) NSTimer* refreshTimer;
@property (nonatomic, strong) NSDate* lastRefreshTimestamp;

@end

@implementation TransportNextDeparturesViewController

#pragma mark - Init

- (id)init
{
    self = [super initWithNibName:@"TransportNextDeparturesView" bundle:nil];
    if (self) {
        self.transportService = [TransportService sharedInstanceToRetain];
    }
    
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[PCGAITracker sharedTracker] trackScreenWithName:@"/v3r1/transport"];
    self.locationButton.tintColor = [PCValues pocketCampusRed];
    self.tableView.hidden = YES;
    self.tableView.rowHeight = 60.0;
    self.tableView.contentInset = UIEdgeInsetsMake(64.0+self.topContainerView.frame.size.height, 0, self.toolbar.frame.size.height, 0);
    NSLog(@"%lf", self.topLayoutGuide.length);
    UIBarButtonItem* refreshButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(refresh)];
    self.navigationItem.rightBarButtonItem = refreshButton;
#warning TODO
    //UITapGestureRecognizer* tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(presentFavoriteStationsViewController:)];
    //[self.fromLabel addGestureRecognizer:tapGesture];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshIfNeeded) name:UIApplicationDidBecomeActiveNotification object:[UIApplication sharedApplication]];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:animated];
    [self refresh];
    //[NSTimer scheduledTimerWithTimeInterval:0.85 target:self selector:@selector(refresh) userInfo:nil repeats:NO];
    //refreshTimer = [[NSTimer scheduledTimerWithTimeInterval:15.0 target:self selector:@selector(refreshButtonPressed) userInfo:nil repeats:YES] retain];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.refreshTimer invalidate];
    self.refreshTimer = nil;
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Refresh & requests start

- (void)refreshIfNeeded {
    if (![PCUtils hasDeviceInternetConnection]) {
        return;
    }
    if (self.lastRefreshTimestamp && abs([self.lastRefreshTimestamp timeIntervalSinceNow]) < kSchedulesValidy) {
        return;
    }
    [self refresh];
}

- (void)refresh {
    NSLog(@"-> Refresh...");
    
    self.usersStations = [self.transportService.userTransportStations copy];
    self.departureStation = self.transportService.userManualDepartureStation;

    self.userStationsState = self.usersStations ? (self.usersStations.count > 1 ? UserStationsStateOK : UserStationsStateErrorNeedTwo) : UserStationsStateLoadingDefault;
    self.locationState = self.departureStation ? LocationStateManualSelection : LocationStateWaiting;
    self.schedulesState = SchedulesStateWaiting;
    
    [self.transportService cancelOperationsForDelegate:self]; //cancel previous refresh if exists
    
    self.lastRefreshTimestamp = [NSDate date];
    
    if (self.userStationsState == UserStationsStateOK) {
        if (self.locationState == LocationStateManualSelection) {
            [self startGetTripsRequests];
        } else {
            NSLog(@"-> Requesting nearest transport station...");
            self.locationState = LocationStateLocating;
            [self.transportService nearestUserTransportStationWithDelegate:self];
        }
    } else if (self.userStationsState == UserStationsStateLoadingDefault) {
        NSLog(@"-> No previously saved user stations. Requesting default stations...");
        [self.transportService getLocationsForNames:@[@"EPFL", @"Lausanne-Flon"] delegate:self];
    }
    
    [self updateAll];
}

- (void)startGetTripsRequests {
    if (!self.usersStations) {
        NSLog(@"-> WARNING : tried to startGetTripsRequests when userStations nil. Returning.");
        return;
    }
    if (!self.departureStation) {
        NSLog(@"-> WARNING : tried to startGetTripsRequests when departureStation nil. Returning.");
        return;
    }
    self.tripResults = [NSMutableDictionary dictionaryWithCapacity:self.usersStations.count-1];

    [self.usersStations enumerateObjectsUsingBlock:^(TransportStation* station, NSUInteger index, BOOL *stop) {
        if (![station isEqualToTransportStation:self.departureStation]) {
            NSInteger priority; // see NSOperationQueuePriority
            if (index <= 1) {
                priority = 8;
            } else if (index == 2) {
                priority = 4;
            } else if (index <= 3) {
                priority = 0;
            } else if (index <= 5) {
                priority = -4;
            } else {
                priority = -8;
            }
            [self.transportService getTripsFrom:self.departureStation.name to:station.name delegate:self priority:priority];
        }
    }];
    self.schedulesState = SchedulesStateLoading;
    [self updateAll];
    [self.tableView reloadData];
}

#pragma mark - Actions

- (IBAction)locationButtonPressed {
    if (self.locationState == LocationStateManualSelection) {
        self.transportService.userManualDepartureStation = nil;
        [self refresh];
    }
}

- (IBAction)addStationButtonPressed {
    TransportAddStationViewController* viewController = [TransportAddStationViewController new];
    PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:viewController];
    [self presentViewController:navController animated:YES completion:NULL];
}

- (IBAction)stationsButtonsPressed {
    TransportStationsManagerViewController* viewController = [TransportStationsManagerViewController new];
    PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:viewController];
    [self presentViewController:navController animated:YES completion:NULL];
}

- (IBAction)presentHelpViewController:(id)sender {
    TransportHelpViewController* viewController = [[TransportHelpViewController alloc] initWithHTMLFilePath:[[NSBundle mainBundle] pathForResource:@"TransportHelp" ofType:@"html"]];
    PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:viewController];
    [self presentViewController:navController animated:YES completion:NULL];
}

- (IBAction)presentSettingsViewController:(id)sender {
    TransportSettingsViewController* viewController = [[TransportSettingsViewController alloc] init];
    PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:viewController];
    [self presentViewController:navController animated:YES completion:NULL];
}

#pragma mark - UI update

// Initialize and/or update all infos of the UI, according to current states.
// Call this method everytime location might have changed or when user stations have changed
- (void)updateAll {
    
    //NSLog(@"-> updateAll with states (%d, %d, %d)", userStationsState, locationState, schedulesState);
    
    switch (self.userStationsState) {
        case UserStationsStateLoadingDefault:
            self.locationButton.enabled = NO;
            self.fromLabel.text = nil;
            self.tableView.hidden = YES;
            self.centerMessageLabel.text = NSLocalizedStringFromTable(@"LoadingDefaultStations...", @"TransportPlugin", nil);
            return; //everything is set
            break;
        case UserStationsStateErrorNeedTwo:
            self.locationButton.enabled = NO;
            self.fromLabel.text = nil;
            self.tableView.hidden = YES;
            self.centerMessageLabel.text = NSLocalizedStringFromTable(@"Need2StationsClickPlusToAdd", @"TransportPlugin", nil);
            return; //everything is set
            break;
        default:
            break;
    }
    
    
    NSString* locationArrowImageName = @"LocationArrow2";
    NSString* locationAlertMessage = nil;
    NSMutableAttributedString* fromLabelAttrString = nil;
    NSString* fromLabelString = nil;
    NSString* fromString = NSLocalizedStringFromTable(@"From:", @"TransportPlugin", nil);
    switch (self.locationState) {
        case LocationStateLocating:
        {
            self.locationButton.enabled = YES;
            NSString* locatingString = NSLocalizedStringFromTable(@"locating...", @"TransportPlugin", nil);
            fromLabelString = [NSString stringWithFormat:@"%@ %@", fromString, locatingString];
            fromLabelAttrString = [[NSMutableAttributedString alloc] initWithString:fromLabelString];
            [fromLabelAttrString addAttributes:@{NSForegroundColorAttributeName:[UIColor lightGrayColor]} range:[fromLabelString rangeOfString:locatingString]];
            break;
        }
        case LocationStateLocated:
        {
            locationArrowImageName = @"LocationArrow2Active";
            self.locationButton.enabled = YES;
            NSString* stationName = self.departureStation.shortName;
            fromLabelString = [NSString stringWithFormat:@"%@ %@", fromString, stationName];
            fromLabelAttrString = [[NSMutableAttributedString alloc] initWithString:fromLabelString];
            [fromLabelAttrString addAttribute:NSForegroundColorAttributeName value:[PCValues pocketCampusRed] range:[fromLabelString rangeOfString:stationName]];
            break;
        }
        case LocationStateManualSelection:
        {
            self.locationButton.enabled = YES;
            NSString* stationName = self.departureStation.shortName;
            fromLabelString = [NSString stringWithFormat:@"%@ %@", fromString, stationName];
            fromLabelAttrString = [[NSMutableAttributedString alloc] initWithString:fromLabelString];
            [fromLabelAttrString addAttribute:NSForegroundColorAttributeName value:[PCValues pocketCampusRed] range:[fromLabelString rangeOfString:stationName]];
            break;
        }
        case LocationStateErrorUserDenied:
            locationAlertMessage = NSLocalizedStringFromTable(@"ImpossibleLocateUserDenied", @"TransportPlugin", nil);
            break;
        case LocationStateErrorTimeout:
            locationAlertMessage = NSLocalizedStringFromTable(@"ImpossibleLocateUnknown", @"TransportPlugin", nil);
            break;
        case LocationStateErrorUnknown:
            locationAlertMessage = NSLocalizedStringFromTable(@"ImpossibleLocateUnknown", @"TransportPlugin", nil);
            break;
        default:
            break;
    }
    
    [self.locationButton setImage:[UIImage imageNamed:locationArrowImageName] forState:UIControlStateNormal];
    self.fromLabel.attributedText = fromLabelAttrString;
    
    if (locationAlertMessage) {
        self.locationButton.enabled = NO;
        NSString* cannotLocateString = NSLocalizedStringFromTable(@"ImpossibleLocateShort", @"TransportPlugin", nil);
        fromLabelString = [NSString stringWithFormat:@"%@ %@", fromString, cannotLocateString];
        fromLabelAttrString = [[NSMutableAttributedString alloc] initWithString:fromLabelString];
        [fromLabelAttrString addAttribute:NSForegroundColorAttributeName value:[UIColor orangeColor] range:[fromLabelString rangeOfString:cannotLocateString]];
        self.fromLabel.attributedText = fromLabelAttrString;
        //[self.locationActivityIndicator stopAnimating];
        self.tableView.hidden = YES;
        self.centerMessageLabel.text = nil;
        UIAlertView* alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"ImpossibleLocateShort", @"TransportPlugin", nil) message:locationAlertMessage delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
        return;
    }
    
    switch (self.schedulesState) {
        default:
            self.tableView.hidden = NO;
            break;
    }
    
}

#pragma mark - TransportServiceDelegate

- (void)locationsForNames:(NSArray*)names didReturn:(NSArray*)locations {
    if (names.count == 2 && [names[0] isEqualToString:@"EPFL"] && [names[1] isEqualToString:@"Lausanne-Flon"]) {
        //default stations request returned
        self.transportService.userTransportStations = self.usersStations;
        NSLog(@"-> Default stations returned and saved in user settings. Refreshing.");
        [self refresh];
    }
}

- (void)nearestUserTransportStationDidReturn:(TransportStation*)nearestStation {
    NSLog(@"-> Nearest station found : %@", nearestStation.name);
    self.departureStation = nearestStation;
    self.locationState = LocationStateLocated;
    [self startGetTripsRequests];
}

- (void)nearestUserTransportStationFailed:(LocationFailureReason)reason {
    switch (reason) {
        case LocationFailureReasonUserDenied:
            self.locationState = LocationStateErrorUserDenied;
            break;
        case LocationFailureReasonTimeout:
            self.locationState = LocationStateErrorTimeout;
            break;
        default:
            self.locationState = LocationStateErrorUnknown;
            break;
    }
    [self updateAll];
}

- (void)tripsFrom:(NSString*)from to:(NSString*)to didReturn:(QueryTripsResult*)tripResult {
    NSLog(@"-> Trip returned : (from: %@ to: %@)", tripResult.from.name, tripResult.to.name);
    if (!self.usersStations || !self.departureStation) {
        return; //should not happen
    }
    self.tripResults[to] = tripResult;
    
    if (self.schedulesState != SchedulesStateError) {
        [self.tableView reloadRowsAtIndexPaths:@[[self biasedIndexPathForStationName:to]] withRowAnimation:UITableViewRowAnimationFade];
    }
    if (self.tripResults.count == self.usersStations.count - 1) { //all results have arrived
        NSLog(@"-> All trips returned => SchedulesStateLoaded");
        self.schedulesState = SchedulesStateLoaded;
        [self updateAll];
    }
}

- (void)tripsFailedFrom:(NSString*)from to:(NSString*)to {
    self.tripResults[to] = [NSNull null]; //indicates error
    [self.tableView reloadRowsAtIndexPaths:@[[self biasedIndexPathForStationName:to]] withRowAnimation:UITableViewRowAnimationFade];
    if (self.tripResults.count == self.usersStations.count - 1) { //all results have arrived
        NSLog(@"-> All trips returned (some with error) => SchedulesStateLoaded");
        self.schedulesState = SchedulesStateLoaded;
        [self updateAll];
    }
}

- (void)serviceConnectionToServerTimedOut {
    if (self.schedulesState == SchedulesStateError) {
        return; //timeout message already displayed
    }
    self.schedulesState = SchedulesStateError;
    NSMutableArray* timedOutIndexPaths = [NSMutableArray array];
    for (TransportStation* station in self.usersStations) {
        if (![station isEqualToTransportStation:self.departureStation] && !self.tripResults[station.name]) {
            [timedOutIndexPaths addObject:[self biasedIndexPathForStationName:station.name]];
        }
    }
    [self.tableView reloadRowsAtIndexPaths:timedOutIndexPaths withRowAnimation:UITableViewRowAnimationFade];
    [self updateAll];
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView_ didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NextDeparturesCell* cell = (NextDeparturesCell*)[self.tableView cellForRowAtIndexPath:indexPath];
    if (cell.loadingState != LoadingStateLoaded) {
        return;
    }
    
    QueryTripsResult* queryTripResult = self.tripResults[cell.transportStation.name];
    DestinationConnectionsListViewController* viewController = [[DestinationConnectionsListViewController alloc] initWithQueryTripResult:queryTripResult];
    [self.navigationController pushViewController:viewController animated:YES];
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSIndexPath* biasedIndexPath = [self biasedIndexPathForIndexPath:indexPath];
    TransportStation* station = self.usersStations[biasedIndexPath.row];
    QueryTripsResult* trip = self.tripResults[station.name];
    
    if (!trip) { //still loading
        return [[NextDeparturesCell alloc] initWithDestinationStation:station loadingState:LoadingStateLoading];;
    }
    
    if ([trip isEqual:[NSNull null]]) { //indicates error
        return [[NextDeparturesCell alloc] initWithDestinationStation:station loadingState:LoadingStateError];;
    }
    
    NSNumber* bestResultSetting = (NSNumber*)[ObjectArchiver objectForKey:kTransportSettingsKeyBestResult andPluginName:@"transport"];
    
    if (!bestResultSetting  || [bestResultSetting boolValue]) { //if nil, means not set => default => YES.
        NSArray* redundantConnections = [TransportUtils nextRedundantDeparturesFromMessyResult:trip];
        redundantConnections = [TransportUtils connectionsWithoutAlreadyLeft:redundantConnections];
        return [[NextDeparturesCell alloc] initWithQueryTripResult:trip redundantConnections:redundantConnections];
    }
    
    return [[NextDeparturesCell alloc] initWithQueryTripResult:trip redundantConnections:nil];
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (self.schedulesState == SchedulesStateError) {
        return 0;
    }
    return self.usersStations.count-1; //minus 1 because userStations also contains from station
}

#pragma mark - Utils

- (NSIndexPath*)biasedIndexPathForStationName:(NSString*)stationName {
    NSUInteger stationIndex = [self.usersStations indexOfObjectPassingTest:^(id object, NSUInteger idx, BOOL * stop){
        TransportStation* stationTmp = (TransportStation*)object;
        if ([stationTmp.name isEqualToString:stationName]) {
            *stop = YES;
            return YES;
        }
        return NO;
    }];
    
    NSUInteger departureStationIndex = [self.usersStations indexOfObjectPassingTest:^(id object, NSUInteger idx, BOOL * stop){
        TransportStation* station = (TransportStation*)object;
        if (station.id == self.departureStation.id) {
            *stop = YES;
            return YES;
        }
        return NO;
    }];
    
    if (stationIndex < departureStationIndex) {
        return [NSIndexPath indexPathForRow:stationIndex inSection:0]; //no need to correct
    } else {
        return [NSIndexPath indexPathForRow:stationIndex-1 inSection:0];
    }
}

//as userStations contains all user stations, including departure, must avoid the departure station in the array when populating the table view
- (NSIndexPath*)biasedIndexPathForIndexPath:(NSIndexPath*)indexPath {
    
    NSUInteger departureStationIndex = [self.usersStations indexOfObjectPassingTest:^(id object, NSUInteger idx, BOOL * stop){
        TransportStation* station = (TransportStation*)object;
        if (station.id == self.departureStation.id) {
            *stop = YES;
            return YES;
        }
        return NO;
    }];
    
    if (indexPath.row < departureStationIndex) {
        return indexPath; //no need to correct
    } else {
        return [NSIndexPath indexPathForRow:indexPath.row+1 inSection:0];
    }
    
}

#pragma mark - Dealloc

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [self.transportService cancelOperationsForDelegate:self];
    [self.refreshTimer invalidate];
}

@end
