/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

//  Created by Loïc Gardiol on 23.03.12.

#import "TransportNextDeparturesViewController.h"

#import "TransportService.h"

#import "TransportUtils.h"

#import "TransportController.h"

#import "PCPersistenceManager.h"

#import "TransportNextDeparturesCell.h"

#import "TransportTripsListViewController.h"

#import "TransportHelpViewController.h"

#import "TransportStationsManagerViewController.h"

#import "TransportAddStationViewController.h"

#import "TransportDepartureSelectionViewController.h"


typedef enum {
    UserStationsStateOK = 0,
    UserStationsStateLoadingDefault,
    UserStationsStateErrorLoadingDefault,
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
@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* centerLoadingIndicator;
@property (nonatomic, strong) IBOutlet UILabel* centerMessageLabel;
@property (nonatomic, strong) IBOutlet UIToolbar* toolbar;
@property (nonatomic, strong) IBOutlet UIBarButtonItem* helpButton;
@property (nonatomic, strong) IBOutlet UIBarButtonItem* addStationButton;
@property (nonatomic, strong) IBOutlet UIBarButtonItem* stationsListButton;

@property (nonatomic, strong) UITableViewController* tableViewController;
@property (nonatomic, strong) LGARefreshControl* lgRefreshControl;

@property (nonatomic, strong) TransportService* transportService;
@property (nonatomic, strong) NSOrderedSet* usersStations;
@property (nonatomic, strong) NSMutableDictionary* tripResults; //key : destination station name, value : QueryTripResult
@property (nonatomic, strong) NSMutableDictionary* cellForDestinationName;
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
        self.gaiScreenName = @"/transport";
        self.transportService = [TransportService sharedInstanceToRetain];
    }
    
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.tableViewController = [[UITableViewController alloc] initWithStyle:self.tableView.style];
    [self addChildViewController:self.tableViewController];
    self.lgRefreshControl = [[LGARefreshControl alloc] initWithTableViewController:self.tableViewController refreshedDataIdentifier:nil];
    [self.lgRefreshControl setTarget:self selector:@selector(refresh)];
    self.tableViewController.tableView = self.tableView;
    self.tableView.hidden = YES;
    self.tableView.rowHeight = 74.0;
    self.tableView.contentInset = UIEdgeInsetsMake(64.0+self.topContainerView.frame.size.height, 0, self.toolbar.frame.size.height, 0);
    self.tableView.scrollIndicatorInsets = self.tableView.contentInset;
    UIBarButtonItem* refreshButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(refresh)];
    self.navigationItem.rightBarButtonItem = refreshButton;
    
    self.locationButton.tintColor = [PCValues pocketCampusRed];
    self.locationButton.accessibilityLabel = NSLocalizedStringFromTable(@"NearestStationButton", @"TransportPlugin", nil);
    self.locationButton.accessibilityHint = NSLocalizedStringFromTable(@"SelectsNearestStationAsDeparture", @"TransportPlugin", nil);
    
    UITapGestureRecognizer* tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(fromLabelPressed)];
    [self.fromLabel addGestureRecognizer:tapGesture];
    self.fromLabel.accessibilityTraits ^= UIAccessibilityTraitButton;
    self.fromLabel.accessibilityHint = NSLocalizedStringFromTable(@"ShowsDepartureSelectionScreen", @"TransportPlugin", nil);
    
    self.helpButton.accessibilityLabel = NSLocalizedStringFromTable(@"Help", @"PocketCampus", nil);
    self.helpButton.accessibilityHint = NSLocalizedStringFromTable(@"ShowsHelpForTransport", @"TransportPlugin", nil);
    
    self.addStationButton.accessibilityLabel = NSLocalizedStringFromTable(@"AddStation", @"TransportPlugin", nil);
    self.addStationButton.accessibilityHint = NSLocalizedStringFromTable(@"ShowsScreenToSearchForAStation", @"TransportPlugin", nil);
    
    self.stationsListButton.accessibilityLabel = NSLocalizedStringFromTable(@"ManageMyStations", @"TransportPlugin", nil);
    self.stationsListButton.accessibilityHint = NSLocalizedStringFromTable(@"ShowsScreenThatListsMyStationsToManageThem", @"TransportPlugin", nil);
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(appDidBecomeActive) name:UIApplicationDidBecomeActiveNotification object:[UIApplication sharedApplication]];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(userTransportStationsModified) name:kTransportUserTransportStationsModifiedNotification object:self.transportService];
    [self.transportService addObserver:self forKeyPath:NSStringFromSelector(@selector(userManualDepartureStation)) options:0 context:nil];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
    [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:animated];
    [self refreshIfNeeded];
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

#pragma mark - Notifications listening

- (void)userTransportStationsModified {
    self.lastRefreshTimestamp = nil; //then next call to refreshIsNeeded will pass
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    if ([keyPath isEqualToString:NSStringFromSelector(@selector(userManualDepartureStation))] && object == self.transportService) {
        self.lastRefreshTimestamp = nil; //then next call to refreshIsNeeded will pass
    }
}

- (void)appDidBecomeActive {
    if (self.navigationController.topViewController == self) {
        [self refreshIfNeeded];
    }
}

#pragma mark - Refresh & requests start

- (void)refreshIfNeeded {
    if (self.lastRefreshTimestamp && fabs([self.lastRefreshTimestamp timeIntervalSinceNow]) < kSchedulesValidy) {
        if (!(self.locationState == LocationStateErrorUserDenied && [PCUtils hasAppAccessToLocation])) {
            //last refresh date is recent enough that we don't need to refresh
            //AND
            //it is NO the case that locationState is in user denied while app atually has access to location, in which case one should refresh
            return;
        }
    }
    [self refresh];
}

- (void)refresh {
    [self.lgRefreshControl endRefreshing];
    
    self.usersStations = [self.transportService.userTransportStations copy];
    
    if (self.transportService.userManualDepartureStation && self.usersStations && ![self.usersStations containsObject:self.transportService.userManualDepartureStation]) {
        //If manual departure station is no longer in userStations,
        //unset it (removes from persistence)
        self.transportService.userManualDepartureStation = nil;
    }
    
    self.departureStation = self.transportService.userManualDepartureStation;

    self.userStationsState = self.usersStations ? (self.usersStations.count > 1 ? UserStationsStateOK : UserStationsStateErrorNeedTwo) : UserStationsStateLoadingDefault;
    self.locationState = self.departureStation ? LocationStateManualSelection : LocationStateWaiting;
    self.schedulesState = SchedulesStateWaiting;
    
    [self.transportService cancelOperationsForDelegate:self]; //cancel previous refresh if exists
    
    self.lastRefreshTimestamp = [NSDate date];
    
    
    if (self.userStationsState == UserStationsStateOK) {
        self.cellForDestinationName = [NSMutableDictionary dictionaryWithCapacity:self.usersStations.count-1];
        if (self.locationState == LocationStateManualSelection) {
            [self startGetTripsRequests];
        } else {
            CLSNSLog(@"-> Requesting nearest transport station...");
            self.locationState = LocationStateLocating;
            [self.transportService nearestUserTransportStationWithDelegate:self];
        }
    } else if (self.userStationsState == UserStationsStateLoadingDefault) {
        self.cellForDestinationName = nil;
        CLSNSLog(@"-> No previously saved user stations. Requesting default stations...");
        [self.transportService getLocationsForNames:@[@"EPFL", @"Lausanne-Flon"] delegate:self];
    }
    
    [self updateAll];
}

- (void)startGetTripsRequests {
    if (!self.usersStations) {
        CLSNSLog(@"-> WARNING : tried to startGetTripsRequests when userStations nil. Returning.");
        return;
    }
    if (!self.departureStation) {
        CLSNSLog(@"-> WARNING : tried to startGetTripsRequests when departureStation nil. Returning.");
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
    [self trackAction:@"NearestUserStationAsDeparture"];
    if (self.locationState == LocationStateManualSelection) {
        self.transportService.userManualDepartureStation = nil;
        [self refresh];
    }
}

- (void)fromLabelPressed {
    [self trackAction:@"SelectDeparture"];
    TransportDepartureSelectionViewController* viewController = [TransportDepartureSelectionViewController new];
    PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:viewController];
    [self presentViewController:navController animated:YES completion:NULL];
}

- (IBAction)addStationButtonPressed {
    [self trackAction:@"AddStation"];
    TransportAddStationViewController* viewController = [TransportAddStationViewController new];
    PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:viewController];
    [self presentViewController:navController animated:YES completion:NULL];
}

- (IBAction)stationsButtonsPressed {
    [self trackAction:@"UserStations"];
    TransportStationsManagerViewController* viewController = [TransportStationsManagerViewController new];
    PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:viewController];
    [self presentViewController:navController animated:YES completion:NULL];
}

- (IBAction)presentHelpViewController:(id)sender {
    [self trackAction:@"Help"];
    TransportHelpViewController* viewController = [TransportHelpViewController new];
    PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:viewController];
    [self presentViewController:navController animated:YES completion:NULL];
}

#pragma mark - UI update

// Initialize and/or update all infos of the UI, according to current states.
// Call this method everytime location might have changed or when user stations have changed
- (void)updateAll {
    
    switch (self.userStationsState) {
        case UserStationsStateLoadingDefault:
            self.locationButton.enabled = NO;
            self.fromLabel.text = nil;
            self.tableView.hidden = YES;
            [self.centerLoadingIndicator startAnimating];
            self.centerMessageLabel.text = NSLocalizedStringFromTable(@"LoadingDefaultStations...", @"TransportPlugin", nil);
            return; //everything is set
            break;
        case UserStationsStateErrorLoadingDefault:
            self.locationButton.enabled = NO;
            self.fromLabel.text = nil;
            self.tableView.hidden = YES;
            [self.centerLoadingIndicator stopAnimating];
            self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
            return; //everything is set
            break;
        case UserStationsStateErrorNeedTwo:
            self.locationButton.enabled = NO;
            self.fromLabel.text = nil;
            self.tableView.hidden = YES;
            [self.centerLoadingIndicator stopAnimating];
            self.centerMessageLabel.text = NSLocalizedStringFromTable(@"Need2StationsClickPlusToAdd", @"TransportPlugin", nil);
            return; //everything is set
            break;
        default:
            [self.centerLoadingIndicator stopAnimating];
            break;
    }
    
    
    NSString* locationArrowImageName = @"LocationArrow2";
    NSString* locationProblemMessage = nil;
    NSMutableAttributedString* fromLabelAttrString = nil;
    NSString* fromLabelString = nil;
    NSString* fromString = NSLocalizedStringFromTable(@"From:", @"TransportPlugin", nil);
    switch (self.locationState) {
        case LocationStateLocating:
        {
            self.tableView.hidden = YES;
            [self.centerLoadingIndicator startAnimating];
            self.centerMessageLabel.text = NSLocalizedStringFromTable(@"SearchingNearestStation", @"TransportPlugin", nil);
            self.locationButton.enabled = YES;
            NSString* locatingString = NSLocalizedStringFromTable(@"locating...", @"TransportPlugin", nil);
            fromLabelString = [NSString stringWithFormat:@"%@ %@", fromString, locatingString];
            fromLabelAttrString = [[NSMutableAttributedString alloc] initWithString:fromLabelString];
            [fromLabelAttrString addAttributes:@{NSForegroundColorAttributeName:[UIColor lightGrayColor]} range:[fromLabelString rangeOfString:locatingString]];
            break;
        }
        case LocationStateLocated:
        {
            [self.centerLoadingIndicator stopAnimating];
            locationArrowImageName = @"LocationArrow2Active";
            self.locationButton.enabled = YES;
            NSString* stationName = self.departureStation.shortName;
            if (stationName) {
                fromLabelString = [NSString stringWithFormat:@"%@ %@", fromString, stationName];
                fromLabelAttrString = [[NSMutableAttributedString alloc] initWithString:fromLabelString];
                [fromLabelAttrString addAttribute:NSForegroundColorAttributeName value:[PCValues pocketCampusRed] range:[fromLabelString rangeOfString:stationName]];
            }
            break;
        }
        case LocationStateManualSelection:
        {
            [self.centerLoadingIndicator stopAnimating];
            self.locationButton.enabled = YES;
            NSString* stationName = self.departureStation.shortName;
            if (stationName) {
                fromLabelString = [NSString stringWithFormat:@"%@ %@", fromString, stationName];
                fromLabelAttrString = [[NSMutableAttributedString alloc] initWithString:fromLabelString];
                [fromLabelAttrString addAttribute:NSForegroundColorAttributeName value:[PCValues pocketCampusRed] range:[fromLabelString rangeOfString:stationName]];
            }
            break;
        }
        case LocationStateErrorUserDenied:
            locationProblemMessage = NSLocalizedStringFromTable(@"ImpossibleLocateUserDenied", @"TransportPlugin", nil);
            break;
        case LocationStateErrorTimeout:
            locationProblemMessage = NSLocalizedStringFromTable(@"ImpossibleLocateUnknown", @"TransportPlugin", nil);
            break;
        case LocationStateErrorUnknown:
            locationProblemMessage = NSLocalizedStringFromTable(@"ImpossibleLocateUnknown", @"TransportPlugin", nil);
            break;
        default:
            break;
    }
    
    [self.locationButton setImage:[UIImage imageNamed:locationArrowImageName] forState:UIControlStateNormal];
    self.fromLabel.attributedText = fromLabelAttrString;
    
    if (locationProblemMessage) {
        self.locationButton.enabled = NO;
        NSString* chooseManuallyString = NSLocalizedStringFromTable(@"ChooseManually", @"TransportPlugin", nil);
        fromLabelString = [NSString stringWithFormat:@"%@ %@", fromString, chooseManuallyString];
        fromLabelAttrString = [[NSMutableAttributedString alloc] initWithString:fromLabelString];
        [fromLabelAttrString addAttribute:NSForegroundColorAttributeName value:[PCValues pocketCampusRed] range:[fromLabelString rangeOfString:chooseManuallyString]];
        self.fromLabel.attributedText = fromLabelAttrString;
        self.tableView.hidden = YES;
        self.centerMessageLabel.text = locationProblemMessage;
        return;
    }
    
    switch (self.schedulesState) {
        case SchedulesStateWaiting:
            break;
        default:
            self.tableView.hidden = NO;
            break;
    }
    
}

#pragma mark - TransportServiceDelegate

- (void)locationsForNames:(NSArray*)names didReturn:(NSArray*)locations {
    if (names.count == 2 && [names[0] isEqualToString:@"EPFL"] && [names[1] isEqualToString:@"Lausanne-Flon"]) {
        //default stations request returned
        self.transportService.userTransportStations = [NSOrderedSet orderedSetWithArray:locations];
        CLSNSLog(@"-> Default stations returned and saved in user settings. Refreshing.");
        [self refresh];
    }
}

- (void)locationsFailedForNames:(NSArray *)names {
    if (self.userStationsState == UserStationsStateLoadingDefault) {
        self.userStationsState = UserStationsStateErrorLoadingDefault;
    }
    [self updateAll];
}

- (void)nearestUserTransportStationDidReturn:(TransportStation*)nearestStation {
    CLSLog(@"-> Nearest station found : %@", nearestStation.name);
    self.departureStation = nearestStation;
    self.locationState = LocationStateLocated;
    [self startGetTripsRequests];
}

- (void)nearestUserTransportStationFailed:(LocationFailureReason)reason {
    switch (reason) {
        case LocationFailureReasonUserDeniedBufferAlert:
            [self trackAction:@"UserDeniedAccessToLocationBufferAlert"];
            self.locationState = LocationStateManualSelection;
            self.transportService.userManualDepartureStation = [self.transportService.userTransportStations firstObject];
            [self refresh];
            break;
        case LocationFailureReasonUserDeniedSystem:
            [self trackAction:@"UserDeniedAccessToLocationSystem"];
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
    CLSLog(@"-> Trip returned : (from: %@ to: %@)", tripResult.from.name, tripResult.to.name);
    if (!self.usersStations || !self.departureStation) {
        return; //should not happen
    }
    self.tripResults[to] = tripResult;
    
    if (self.schedulesState != SchedulesStateError) {
        TransportNextDeparturesCell* cell = self.cellForDestinationName[to];
        cell.tripResult = tripResult;
    }
    if (self.tripResults.count == self.usersStations.count - 1) { //all results have arrived
        CLSLog(@"-> All trips returned => SchedulesStateLoaded");
        self.schedulesState = SchedulesStateLoaded;
        [self updateAll];
    }
}

- (void)tripsFailedFrom:(NSString*)from to:(NSString*)to {
    self.tripResults[to] = [NSNull null]; //indicates error
    TransportNextDeparturesCell* cell = self.cellForDestinationName[to];
    cell.state = TransportNextDeparturesCellStateError;
    if (self.tripResults.count == self.usersStations.count - 1) { //all results have arrived
        CLSLog(@"-> All trips returned (some with error) => SchedulesStateLoaded");
        self.schedulesState = SchedulesStateLoaded;
        [self updateAll];
    }
}

- (void)serviceConnectionToServerFailed {
    if (self.userStationsState == UserStationsStateLoadingDefault) {
        self.userStationsState = UserStationsStateErrorLoadingDefault;
    } else if (self.schedulesState == SchedulesStateError) {
        return; //timeout message already displayed
    } else {
        self.schedulesState = SchedulesStateError;
        for (TransportStation* station in self.usersStations) {
            if (![station isEqualToTransportStation:self.departureStation] && !self.tripResults[station.name]) {
                TransportNextDeparturesCell* cell = self.cellForDestinationName[station.name];
                cell.state = TransportNextDeparturesCellStateError;
            }
        }
        [PCUtils showConnectionToServerTimedOutAlert];
    }
    [self updateAll];
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    TransportNextDeparturesCell* cell = (TransportNextDeparturesCell*)[self.tableView cellForRowAtIndexPath:indexPath];
    if (cell.state != TransportNextDeparturesCellStateLoaded) {
        [self.tableView deselectRowAtIndexPath:indexPath animated:NO];
        return;
    }
    
    QueryTripsResult* queryTripResult = self.tripResults[cell.destinationStation.name];
    if (!queryTripResult) {
        [PCUtils showUnknownErrorAlertTryRefresh:YES];
        [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
        return;
    }
    TransportTripsListViewController* viewController = [[TransportTripsListViewController alloc] initWithQueryTripResult:queryTripResult];
    [self.navigationController pushViewController:viewController animated:YES];
    [self trackAction:@"ViewTrips" contentInfo:cell.destinationStation.name];
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSIndexPath* biasedIndexPath = [self biasedIndexPathForIndexPath:indexPath];
    TransportStation* station = self.usersStations[biasedIndexPath.row];
    QueryTripsResult* trip = self.tripResults[station.name];
        
    static NSString* const identifier = @"TransportNextDeparturesCell";
    
    TransportNextDeparturesCell* cell = self.cellForDestinationName[station.name];
    if (!cell) {
        cell = [[TransportNextDeparturesCell alloc] initWithReuseIdentifier:identifier];
        self.cellForDestinationName[station.name] = cell;
    }
    if (!trip) { //common case, still loading, as requests were just started
        cell.departureStation = self.departureStation;
        cell.destinationStation = station;
        cell.state = TransportNextDeparturesCellStateLoading;
    } else {
        /*
         * Following cases are not expected to happen (though they are correct), as when reloadData is called, refresh just started.
         * When responses arrive, respective methods update cells using self.cellForDestinationName directly, without reloading the table
         */
        if ([trip isEqual:[NSNull null]]) { //means error
            cell.departureStation = self.departureStation;
            cell.destinationStation = station;
            cell.state = TransportNextDeparturesCellStateError;
        } else { //means trip != nil and not NSNull => is a QueryTripResult
            cell.tripResult = trip;
        }
    }
    return cell;
    
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
    [self.transportService removeObserver:self forKeyPath:NSStringFromSelector(@selector(userManualDepartureStation))];
    [self.refreshTimer invalidate];
}

@end
