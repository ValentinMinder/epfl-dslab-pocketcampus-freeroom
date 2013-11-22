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


typedef enum {
    FavStationsStateEmpty,
    FavStationsStateNeedTwo,
    FavStationsStateLoading,
    FavStationsStateOK,
    FavStationsStateError,
    FavStationsStateUnset
} FavStationsState;

typedef enum {
    LocationStateManualSelection,
    LocationStateLocated,
    LocationStateLoading,
    LocationStateErrorUserDenied,
    LocationStateErrorTimeout,
    LocationStateErrorUnknown,
    LocationStateUnset
} LocationState;

typedef enum {
    SchedulesStateLoaded,
    SchedulesStateLoading,
    SchedulesStateError,
    SchedulesStateUnset
} SchedulesState;

static double kSchedulesValidy = 20.0; //number of seconds that a schedule is considered valid and thus refresh is not necessary

@interface TransportNextDeparturesViewController ()<TransportServiceDelegate>

@property (nonatomic, strong) IBOutlet UIView* topContainerView;
@property (nonatomic, strong) IBOutlet UIButton* locationButton;
@property (nonatomic, strong) IBOutlet UILabel* fromLabel;
@property (nonatomic, strong) IBOutlet UIButton* infoButton;
@property (nonatomic, strong) IBOutlet UITableView* tableView;
@property (nonatomic, strong) IBOutlet UILabel* centerMessageLabel;
@property (nonatomic, strong) IBOutlet UIToolbar* toolbar;

@property (nonatomic, strong) TransportService* transportService;
@property (nonatomic, strong) NSArray* favStations;
@property (nonatomic, strong) NSMutableDictionary* tripResults; //key : destination station name, value : QueryTripResult
@property (nonatomic) LocationState locationState;
@property (nonatomic) SchedulesState schedulesState;
@property (nonatomic) TransportStation* departureStation;
@property (nonatomic) FavStationsState favStationsState;
@property (nonatomic, strong) NSTimer* refreshTimer;
@property (nonatomic) BOOL isRefreshing;
@property (nonatomic, strong) NSDate* lastRefreshTimestamp;
@property (nonatomic) BOOL needToRefresh;

- (IBAction)presentFavoriteStationsViewController:(id)sender;

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
    UITapGestureRecognizer* tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(presentFavoriteStationsViewController:)];
    [self.fromLabel addGestureRecognizer:tapGesture];
    self.infoButton.accessibilityIdentifier = @"BookmarksButton";
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshIfNeeded) name:UIApplicationDidBecomeActiveNotification object:[UIApplication sharedApplication]];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:animated];
    self.favStations = [self.transportService userFavoriteTransportStations];
    self.departureStation = [self.transportService userManualDepartureStation];
    if (self.departureStation) {
        self.locationState = LocationStateManualSelection;
    } else {
        self.locationState = LocationStateUnset;
    }
    self.schedulesState = SchedulesStateUnset;
    self.favStationsState = FavStationsStateUnset;
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
    if (!self.needToRefresh && self.lastRefreshTimestamp && abs([self.lastRefreshTimestamp timeIntervalSinceNow]) < kSchedulesValidy) {
        return;
    }
    [self refresh];
}

- (void)refresh {
    NSLog(@"-> Refresh...");
    /*if (isRefreshing) {
        NSLog(@"-> Already refreshing. Returning.");
        return;
    }*/
    if (self.favStationsState == FavStationsStateError) { //mean user has denied acces to location => no sense to reload, same error would appear
        NSLog(@"-> FavStationsStateError, will not refresh.");
        return;
    }
    
    if (self.locationState == LocationStateErrorUserDenied && ![self.transportService appHasAccessToLocation]) { //previously denied location, need to check whether now has accepted
        NSLog(@"-> Access to location is still denied, will not refresh");
        return;
    }
    
    //will refresh
    
    [self.transportService cancelOperationsForDelegate:self]; //cancel previous refresh if exists
    
    self.lastRefreshTimestamp = [NSDate date];
    self.needToRefresh = NO;
    
    //isRefreshing = YES;
    self.schedulesState = SchedulesStateUnset;
    self.favStationsState = FavStationsStateUnset;
    
    if (self.favStations.count > 1) {
        self.favStationsState = FavStationsStateOK;
        if (self.locationState == LocationStateManualSelection) {
            [self startGetTripsRequests];
        } else {
            NSLog(@"-> Requesting nearest location...");
            [self.transportService nearestFavoriteTransportStationWithDelegate:self];
            self.locationState = LocationStateLoading;
        }
        self.schedulesState = SchedulesStateLoading;
    } else if (self.favStations.count == 1) {
        self.favStationsState = FavStationsStateNeedTwo;
    } else if (self.favStations && self.favStations.count == 0) {
        self.favStationsState = FavStationsStateEmpty;
    } else { //means self.favStations == nil
        NSLog(@"-> No previously saved favorite stations. Requesting default stations...");
        [self.transportService getLocationsForNames:@[@"EPFL", @"Lausanne-Flon"] delegate:self];
        self.favStationsState = FavStationsStateLoading;
    }
    
    [self updateAll];
}

- (void)startGetTripsRequests {
    if (!self.favStations) {
        NSLog(@"-> WARNING : tried to startGetTripsRequests when favStations nil. Returning.");
        return;
    }
    if (!self.departureStation) {
        NSLog(@"-> WARNING : tried to startGetTripsRequests when departureStation nil. Returning.");
        return;
    }
    self.tripResults = [NSMutableDictionary dictionaryWithCapacity:self.favStations.count-1];

    [self.favStations enumerateObjectsUsingBlock:^(TransportStation* station, NSUInteger index, BOOL *stop) {
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
        if (station.id != self.departureStation.id) {
            [self.transportService getTripsFrom:self.departureStation.name to:station.name delegate:self priority:priority];
        }
    }];
    self.schedulesState = SchedulesStateLoading;
    [self updateAll];
    [self.tableView reloadData];
}

#pragma mark - Actions

- (IBAction)locationButtonPressed {
    [self.transportService saveUserManualDepartureStation:nil];
    [self viewWillAppear:NO];
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
    self.needToRefresh = YES;
}

#pragma mark - UI update

// Initialize and/or update all infos of the UI, according to current states.
// Call this method everytime location might have changed or when favorite user stations have changed
- (void)updateAll {
    
    //NSLog(@"-> updateAll with states (%d, %d, %d)", favStationsState, locationState, schedulesState);
    
    /*if (isRefreshing) {
     self.navigationItem.rightBarButtonItem.enabled = NO;
     } else {
     self.navigationItem.rightBarButtonItem.enabled = YES;
     }*/
    
    switch (self.favStationsState) {
        case FavStationsStateEmpty:
            //NSLog(@"FavStationsStateEmpty");
            self.locationButton.enabled = NO;
            self.fromLabel.text = NSLocalizedStringFromTable(@"NoStation", @"TransportPlugin", nil);
            //[self.locationActivityIndicator stopAnimating];
            self.infoButton.enabled = YES;
            self.tableView.hidden = YES;
            self.centerMessageLabel.text = NSLocalizedStringFromTable(@"WelcomeTouchInfoInstructions", @"TransportPlugin", nil);
            return; //everything is set
            break;
        case FavStationsStateNeedTwo:
            //NSLog(@"FavStationsStateNeedTwo");
            self.locationButton.enabled = NO;
            self.fromLabel.text = nil;
            //[self.locationActivityIndicator stopAnimating];
            self.infoButton.enabled = YES;
            self.tableView.hidden = YES;
            self.centerMessageLabel.text = NSLocalizedStringFromTable(@"Need2StationsTouchInfoInstructions", @"TransportPlugin", nil);
            return; //everything is set
            break;
        case FavStationsStateLoading:
            //NSLog(@"FavStationsStateLoading");
            self.locationButton.enabled = YES;
            self.fromLabel.hidden = NO;
            self.fromLabel.text = NSLocalizedStringFromTable(@"FromLabel", @"TransportPlugin", nil);
            //[self.locationActivityIndicator startAnimating];
            self.infoButton.enabled = NO;
            self.tableView.hidden = YES;
            self.centerMessageLabel.text = nil;
            break;
        case FavStationsStateOK:
            //NSLog(@"FavStationsStateOK");
            self.locationButton.enabled = YES;
            self.fromLabel.hidden = NO;
            self.fromLabel.text = NSLocalizedStringFromTable(@"FromLabel", @"TransportPlugin", nil);
            //[self.locationActivityIndicator stopAnimating];
            self.infoButton.enabled = YES;
            self.centerMessageLabel.text = nil;
            break;
        case FavStationsStateError:
            //NSLog(@"FavStationsStateError");
            self.locationButton.enabled = NO;
            self.fromLabel.text = NSLocalizedStringFromTable(@"NoStation", @"TransportPlugin", nil);
            self.fromLabel.hidden = NO;
            //[self.locationActivityIndicator stopAnimating];
            self.infoButton.enabled = YES;
            self.tableView.hidden = YES;
            self.centerMessageLabel.text = nil;
            return; //error => all UI has been updated to reflect error. Return from update method
            break;
        case FavStationsStateUnset:
            //NSLog(@"FavStationsStateUnset");
            self.locationButton.enabled = NO;
            self.fromLabel.hidden = YES;
            //[self.locationActivityIndicator stopAnimating];
            self.infoButton.enabled = NO;
            break;
        default:
            NSLog(@"!! Unsupported FavStationsState %d", self.favStationsState);
            break;
    }
    
    //fromValueLabel.textColor = [UIColor colorWithRed:0.5411 green:0.1568 blue:0.8078 alpha:1.0];
    
    NSString* locationAlertMessage = nil;
    NSMutableAttributedString* fromLabelAttrString = nil;
    NSString* fromLabelString = nil;
    NSString* fromString = NSLocalizedStringFromTable(@"From:", @"TransportPlugin", nil);
    switch (self.locationState) {
        case LocationStateManualSelection:
        {
            self.locationButton.enabled = YES;
            //[self.locationActivityIndicator stopAnimating];
            NSString* stationName = [TransportUtils nicerName:self.departureStation.name];
            fromLabelString = [NSString stringWithFormat:@"%@ %@", fromString, stationName];
            fromLabelAttrString = [[NSMutableAttributedString alloc] initWithString:fromLabelString];
            [fromLabelAttrString addAttribute:NSForegroundColorAttributeName value:[PCValues pocketCampusRed] range:[fromLabelString rangeOfString:stationName]];
            break;
        }
        case LocationStateLocated:
        {
            //NSLog(@"LocationStateLocated");
            self.locationButton.enabled = YES;
            //[self.locationActivityIndicator stopAnimating];
            NSString* stationName = [TransportUtils nicerName:self.departureStation.name];
            fromLabelString = [NSString stringWithFormat:@"%@ %@", fromString, stationName];
            fromLabelAttrString = [[NSMutableAttributedString alloc] initWithString:fromLabelString];
            [fromLabelAttrString addAttribute:NSForegroundColorAttributeName value:[PCValues pocketCampusRed] range:[fromLabelString rangeOfString:stationName]];
            break;
        }
        case LocationStateLoading:
        {
            //NSLog(@"LocationStateLoading");
            self.locationButton.enabled = YES;
            NSString* locatingString = NSLocalizedStringFromTable(@"locating...", @"TransportPlugin", nil);
            fromLabelString = [NSString stringWithFormat:@"%@ %@", fromString, locatingString];
            fromLabelAttrString = [[NSMutableAttributedString alloc] initWithString:fromLabelString];
            [fromLabelAttrString addAttributes:@{NSForegroundColorAttributeName:[UIColor lightGrayColor]} range:[fromLabelString rangeOfString:locatingString]];
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
        case LocationStateUnset:
            //nothing to do
            break;
        default:
            //NSLog(@"!! Unsupported LocationState %d", locationState);
            break;
    }
    
    [self updateLocationButtonForState:self.locationState];
    
    if (locationAlertMessage) {
        self.locationButton.enabled = NO;
        NSString* cannotLocateString = NSLocalizedStringFromTable(@"ImpossibleLocateShort", @"TransportPlugin", nil);
        fromLabelString = [NSString stringWithFormat:@"%@ %@", fromString, cannotLocateString];
        fromLabelAttrString = [[NSMutableAttributedString alloc] initWithString:fromLabelString];
        [fromLabelAttrString addAttribute:NSForegroundColorAttributeName value:[UIColor orangeColor] range:[fromLabelString rangeOfString:cannotLocateString]];
        self.fromLabel.attributedText = fromLabelAttrString;
        //[self.locationActivityIndicator stopAnimating];
        self.infoButton.enabled = YES; //so that user can switch to manual selection
        self.tableView.hidden = YES;
        self.centerMessageLabel.text = nil;
        UIAlertView* alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"ImpossibleLocateShort", @"TransportPlugin", nil) message:locationAlertMessage delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
        return;
    }
    
    self.fromLabel.attributedText = fromLabelAttrString;
    
    switch (self.schedulesState) {
        case SchedulesStateLoaded:
            //NSLog(@"SchedulesStateLoaded");
            self.tableView.hidden = NO;
            self.centerMessageLabel.text = nil;
            break;
        case SchedulesStateLoading:
            //NSLog(@"SchedulesStateLoading");
            self.tableView.hidden = NO;
            self.centerMessageLabel.text = nil;
            break;
        case SchedulesStateError: //when timeout from server
            //NSLog(@"SchedulesStateError");
            self.tableView.hidden = YES;
            self.centerMessageLabel.text = nil;
            return; //error => all UI has been updated to reflect error. Return from update method
            break;
        case SchedulesStateUnset:
            //NSLog(@"SchedulesStateUnset");
            self.tableView.hidden = YES;
            self.centerMessageLabel.text = nil;
            break;
        default:
            //NSLog(@"!! Unsupported SchedulesState %d", schedulesState);
            break;
    }
    
}

- (void)updateLocationButtonForState:(LocationState)locationState {
    /*
     LocationStateManualSelection,
     LocationStateLocated,
     LocationStateLoading,
     LocationStateErrorUserDenied,
     LocationStateErrorTimeout,
     LocationStateErrorUnknown,
     LocationStateUnset
     */
    NSString* imageName;
    switch (locationState) {
        case LocationStateLocated:
            imageName = @"LocationArrow2Active";
            break;
        default:
            imageName = @"LocationArrow2";
            break;
    }
    [self.locationButton setImage:[UIImage imageNamed:imageName] forState:UIControlStateNormal];
}

#pragma mark - TransportServiceDelegate

- (void)locationsForNames:(NSArray*)names didReturn:(NSArray*)locations {
    if (names.count == 2 && [names[0] isEqualToString:@"EPFL"] && [names[1] isEqualToString:@"Lausanne-Flon"]) {
        //default stations request returned
        self.favStations = locations;
        [self.transportService saveUserFavoriteTransportStations:self.favStations];
        self.favStationsState = FavStationsStateOK;
        NSLog(@"-> Default stations returned and saved in user settings.");
        [self.transportService nearestFavoriteTransportStationWithDelegate:self];
        self.locationState = LocationStateLoading;
        [self updateAll];
    }
}

- (void)nearestFavoriteTransportStationDidReturn:(TransportStation*)nearestStation {
    NSLog(@"-> Nearest station found : %@", nearestStation.name);
    self.departureStation = nearestStation;
    self.locationState = LocationStateLocated;
    [self startGetTripsRequests];
    self.schedulesState = SchedulesStateLoading;
    [self updateAll];
}

- (void)nearestFavoriteTransportStationFailed:(LocationFailureReason)reason {
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
    if (!self.favStations || !self.departureStation) {
        return; //should not happen
    }
    self.tripResults[to] = tripResult;
    
    if (self.schedulesState != SchedulesStateError) {
        [self.tableView reloadRowsAtIndexPaths:[NSArray arrayWithObject:[self biasedIndexPathForStationName:to]] withRowAnimation:UITableViewRowAnimationFade];
    }
    if (self.tripResults.count == self.favStations.count - 1) { //all results have arrived
        NSLog(@"-> All trips returned => SchedulesStateLoaded");
        self.schedulesState = SchedulesStateLoaded;
        [self updateAll];
    }
}

- (void)tripsFailedFrom:(NSString*)from to:(NSString*)to {
    self.tripResults[to] = [NSNull null]; //indicates error
    [self.tableView reloadRowsAtIndexPaths:[NSArray arrayWithObject:[self biasedIndexPathForStationName:to]] withRowAnimation:UITableViewRowAnimationFade];
    if (self.tripResults.count == self.favStations.count - 1) { //all results have arrived
        NSLog(@"-> All trips returned (some with error) => SchedulesStateLoaded");
        self.schedulesState = SchedulesStateLoaded;
        //isRefreshing = NO;
        [self updateAll];
    }
}

- (void)serviceConnectionToServerTimedOut {
    if (self.schedulesState == SchedulesStateError) {
        return; //timeout message already displayed
    }
    self.schedulesState = SchedulesStateError;
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
    //isRefreshing = NO;
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

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    //
    NSIndexPath* biasedIndexPath = [self biasedIndexPathForIndexPath:indexPath];
    TransportStation* station = self.favStations[biasedIndexPath.row];
    
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
    return self.favStations.count-1; //minus 1 because favStations also contains from station
}

#pragma mark - Utils

- (NSIndexPath*)biasedIndexPathForStationName:(NSString*)stationName {
    NSUInteger stationIndex = [self.favStations indexOfObjectPassingTest:^(id object, NSUInteger idx, BOOL * stop){
        TransportStation* stationTmp = (TransportStation*)object;
        if ([stationTmp.name isEqualToString:stationName]) {
            *stop = YES;
            return YES;
        }
        return NO;
    }];
    
    NSUInteger departureStationIndex = [self.favStations indexOfObjectPassingTest:^(id object, NSUInteger idx, BOOL * stop){
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

//as favStations contains all favorite stations, including departure, must avoid the departure station in the array when populating the table view
- (NSIndexPath*)biasedIndexPathForIndexPath:(NSIndexPath*)indexPath {
    
    NSUInteger departureStationIndex = [self.favStations indexOfObjectPassingTest:^(id object, NSUInteger idx, BOOL * stop){
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
