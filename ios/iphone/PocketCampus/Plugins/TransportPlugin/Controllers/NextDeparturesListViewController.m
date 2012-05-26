//
//  NextDeparturesListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "NextDeparturesListViewController.h"

#import "NextDeparturesCell.h"

#import "DestinationConnectionsListViewController.h"

#import "HelpViewController.h"

@implementation NextDeparturesListViewController

static double kSchedulesValidy = 20.0; //number of seconds that a schedule is considered valid and thus refresh is not necessary

@synthesize locationArrow, fromLabel, fromValueLabel, locationActivityIndicator, infoButton, tableView, welcomeTouchInfoInstructionsLabel, connectionErrorLabel, toolbar, helpButton, settingsButton;

- (id)init
{
    self = [super initWithNibName:@"NextDeparturesListView" bundle:nil];
    if (self) {
        transportService = [[TransportService sharedInstanceToRetain] retain];
        favStations = nil;
        departureStation = nil;
        tripResults = nil;
        locationState = LocationStateUnset;
        schedulesState = SchedulesStateUnset;
        favStationsState = FavStationsStateUnset;
        refreshTimer = nil;
        //isRefreshing = NO;
        lastRefreshTimestamp = nil;
        needToRefresh = NO;
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    tableView.hidden = YES;
    tableView.rowHeight = 60.0;
    tableView.contentInset = tableView.contentInset = UIEdgeInsetsMake(0, 0, toolbar.frame.size.height, 0);
    UIBarButtonItem* refreshButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(refresh)];
    self.navigationItem.rightBarButtonItem = refreshButton;
    [refreshButton release];
    helpButton.title = NSLocalizedStringFromTable(@"Help", @"PocketCampus", nil);
    settingsButton.title = NSLocalizedStringFromTable(@"Settings", @"PocketCampus", nil);
    UITapGestureRecognizer* gestureRecognizer1 = [[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(presentFavoriteStationsViewController:)] autorelease];
    UITapGestureRecognizer* gestureRecognizer2 = [[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(presentFavoriteStationsViewController:)] autorelease];
    [fromValueLabel addGestureRecognizer:gestureRecognizer1];
    [fromLabel addGestureRecognizer:gestureRecognizer2];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [favStations release];
    favStations = [[transportService userFavoriteTransportStations] retain];
    [departureStation release];
    departureStation = [[transportService userManualDepartureStation] retain];
    if (departureStation != nil) {
        locationState = LocationStateManualSelection;
    } else {
        locationState = LocationStateUnset;
    }
    schedulesState = SchedulesStateUnset;
    favStationsState = FavStationsStateUnset;
    if (!needToRefresh && lastRefreshTimestamp != nil && abs([lastRefreshTimestamp timeIntervalSinceNow]) < kSchedulesValidy) {
        return;
    }
    [self refresh];
    //[NSTimer scheduledTimerWithTimeInterval:0.85 target:self selector:@selector(refresh) userInfo:nil repeats:NO];
    //refreshTimer = [[NSTimer scheduledTimerWithTimeInterval:15.0 target:self selector:@selector(refreshButtonPressed) userInfo:nil repeats:YES] retain];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    if (refreshTimer != nil) {
        [refreshTimer invalidate];
        [refreshTimer release];
        refreshTimer = nil;
    }
}

- (void)viewDidAppear:(BOOL)animated {
    [tableView deselectRowAtIndexPath:[tableView indexPathForSelectedRow] animated:YES];
}

- (void)refresh {
    NSLog(@"-> Refresh...");
    /*if (isRefreshing) {
        NSLog(@"-> Already refreshing. Returning.");
        return;
    }*/
    if (favStationsState == FavStationsStateError) { //mean user has denied acces to location => no sense to reload, same error would appear
        NSLog(@"-> FavStationsStateError, will not refresh.");
        return;
    }
    
    if (locationState == LocationStateError && ![transportService appHasAccessToLocation]) { //previously denied location, need to check whether now has accepted
        NSLog(@"-> Access to location is still denied, will not refresh");
        return;
    }
    
    //will refresh
    
    [transportService cancelOperationsForDelegate:self]; //cancel previous refresh if exists
    
    [lastRefreshTimestamp release];
    lastRefreshTimestamp = [[NSDate date] retain];
    needToRefresh = NO;
    
    //isRefreshing = YES;
    schedulesState = SchedulesStateUnset;
    favStationsState = FavStationsStateUnset;
    
    if (favStations == nil) { //request for default favorite stations
        NSLog(@"-> No previously saved favorite stations. Requesting default stations...");
        [transportService getLocationsForNames:[NSArray arrayWithObjects:@"EPFL", @"Lausanne-Flon", nil] delegate:self];
        favStationsState = FavStationsStateLoading;
        //schedulesState = SchedulesStateLoading;
    } else if (favStations.count == 0) {
        //isRefreshing = NO;
        favStationsState = FavStationsStateEmpty;
    } else if (favStations.count == 1) {
        //isRefreshing = NO;
        favStationsState = FavStationsStateNeedTwo;
        //Must have 2 stations, message will be shown by updateAll
    } else { //common case
        favStationsState = FavStationsStateOK;
        if (locationState == LocationStateManualSelection) {
            [self startGetTripsRequests];
        } else {
            NSLog(@"-> Requesting nearest location...");
            [transportService nearestFavoriteTransportStationWithDelegate:self];
            locationState = LocationStateLoading;
        }
        schedulesState = SchedulesStateLoading;
    }
    [self updateAll];
}

- (void)startGetTripsRequests {
    if (favStations == nil) {
        NSLog(@"-> WARNING : tried to startGetTripsRequests when favStations nil. Returning.");
        return;
    }
    if (departureStation == nil) {
        NSLog(@"-> WARNING : tried to startGetTripsRequests when departureStation nil. Returning.");
        return;
    }
    [tripResults release];
    tripResults = [[NSMutableDictionary dictionaryWithCapacity:favStations.count-1] retain];
    for (TransportStation* station in favStations) {
        if (station.id != departureStation.id) {
            [transportService getTripsFrom:departureStation.name to:station.name delegate:self];
        }
    }
    schedulesState = SchedulesStateLoading;
    [self updateAll];
    [tableView reloadData];
}

/* TransportServiceDelegate delegation */

- (void)locationsForNames:(NSArray*)names didReturn:(NSArray*)locations {
    if (names != nil && names.count == 2 && [[names objectAtIndex:0] isEqualToString:@"EPFL"] && [[names objectAtIndex:1] isEqualToString:@"Lausanne-Flon"]) {
        //default stations request returned
        [favStations release];
        favStations = [locations retain];
        [transportService saveUserFavoriteTransportStations:favStations];
        favStationsState = FavStationsStateOK;
        NSLog(@"-> Default stations returned and saved in user settings.");
        [transportService nearestFavoriteTransportStationWithDelegate:self];
        locationState = LocationStateLoading;
        [self updateAll];
    }
}

- (void)nearestFavoriteTransportStationDidReturn:(TransportStation*)nearestStation_ {
    NSLog(@"-> Nearest station found : %@", nearestStation_.name);
    [departureStation release];
    departureStation = [nearestStation_ retain];
    locationState = LocationStateLocated;
    [self startGetTripsRequests];
    schedulesState = SchedulesStateLoading;
    [self updateAll];
}

- (void)nearestFavoriteTransportStationFailed:(NSString*)reason {
    locationState = LocationStateError;
    //isRefreshing = NO;
    [self updateAll];
}

- (void)tripsFrom:(NSString*)from to:(NSString*)to didReturn:(QueryTripsResult*)tripResult {
    NSLog(@"-> Trip returned : (from: %@ to: %@)", tripResult.from.name, tripResult.to.name);
    if (favStations == nil || departureStation == nil) {
        return; //should not happen
    }
    
    [tripResults setObject:tripResult forKey:to];
    if (schedulesState != SchedulesStateError) {
        [tableView reloadRowsAtIndexPaths:[NSArray arrayWithObject:[self biasedIndexPathForStationName:to]] withRowAnimation:UITableViewRowAnimationFade];
    }
    if (tripResults.count == favStations.count - 1) { //all results have arrived
        NSLog(@"-> All trips returned => SchedulesStateLoaded");
        schedulesState = SchedulesStateLoaded;
        //isRefreshing = NO;
        [self updateAll];
    }
}

- (void)tripsFailedFrom:(NSString*)from to:(NSString*)to {
    [tripResults setObject:@"error" forKey:to];
    [tableView reloadRowsAtIndexPaths:[NSArray arrayWithObject:[self biasedIndexPathForStationName:to]] withRowAnimation:UITableViewRowAnimationFade];
    if (tripResults.count == favStations.count - 1) { //all results have arrived
        NSLog(@"-> All trips returned (some with error) => SchedulesStateLoaded");
        schedulesState = SchedulesStateLoaded;
        //isRefreshing = NO;
        [self updateAll];
    }
}

- (void)serviceConnectionToServerTimedOut {
    if (schedulesState == SchedulesStateError) {
        return; //timeout message already displayed
    }
    schedulesState = SchedulesStateError;
    connectionErrorLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
    //isRefreshing = NO;
    [self updateAll];
}


// Initialize and/or update all infos of the UI, according to current states.
// Call this method everytime location might have changed or when favorite user stations have changed 
- (void)updateAll {
    
    //NSLog(@"-> updateAll with states (%d, %d, %d)", favStationsState, locationState, schedulesState);

    /*if (isRefreshing) {
        self.navigationItem.rightBarButtonItem.enabled = NO;
    } else {
        self.navigationItem.rightBarButtonItem.enabled = YES;
    }*/
    
    switch (favStationsState) {
        case FavStationsStateEmpty:
            //NSLog(@"FavStationsStateEmpty");
            locationArrow.hidden = YES;
            fromLabel.text = NSLocalizedStringFromTable(@"NoStation", @"TransportPlugin", nil);
            fromValueLabel.hidden = YES;
            [locationActivityIndicator stopAnimating];
            infoButton.enabled = YES;
            tableView.hidden = YES;
            welcomeTouchInfoInstructionsLabel.text = NSLocalizedStringFromTable(@"WelcomeTouchInfoInstructions", @"TransportPlugin", nil);
            welcomeTouchInfoInstructionsLabel.hidden = NO;
            connectionErrorLabel.hidden = YES;
            return; //everything is set
            break;
        case FavStationsStateNeedTwo:
            //NSLog(@"FavStationsStateNeedTwo");
            locationArrow.hidden = YES;
            fromLabel.text = @"";
            fromValueLabel.hidden = YES;
            [locationActivityIndicator stopAnimating];
            infoButton.enabled = YES;
            tableView.hidden = YES;
            welcomeTouchInfoInstructionsLabel.text = NSLocalizedStringFromTable(@"Need2StationsTouchInfoInstructions", @"TransportPlugin", nil);
            welcomeTouchInfoInstructionsLabel.hidden = NO;
            connectionErrorLabel.hidden = YES;
            return; //everything is set
            break;
        case FavStationsStateLoading:
            //NSLog(@"FavStationsStateLoading");
            locationArrow.hidden = NO;
            fromLabel.hidden = NO;
            fromLabel.text = NSLocalizedStringFromTable(@"FromLabel", @"TransportPlugin", nil);
            fromValueLabel.hidden = YES;
            [locationActivityIndicator startAnimating];
            infoButton.enabled = NO;
            tableView.hidden = YES;
            welcomeTouchInfoInstructionsLabel.hidden = YES;
            connectionErrorLabel.hidden = YES;
            break;
        case FavStationsStateOK:
            //NSLog(@"FavStationsStateOK");
            locationArrow.hidden = NO;
            fromLabel.hidden = NO;
            fromLabel.text = NSLocalizedStringFromTable(@"FromLabel", @"TransportPlugin", nil);
            [locationActivityIndicator stopAnimating];
            infoButton.enabled = YES;
            welcomeTouchInfoInstructionsLabel.hidden = YES;
            connectionErrorLabel.hidden = YES;
            break;
        case FavStationsStateError:
            //NSLog(@"FavStationsStateError");
            locationArrow.hidden = YES;
            fromLabel.text = NSLocalizedStringFromTable(@"NoStation", @"TransportPlugin", nil);
            fromLabel.hidden = NO;
            fromValueLabel.hidden = YES;
            [locationActivityIndicator stopAnimating];
            infoButton.enabled = YES;
            tableView.hidden = YES;
            welcomeTouchInfoInstructionsLabel.hidden = YES;
            connectionErrorLabel.hidden = NO;
            return; //error => all UI has been updated to reflect error. Return from update method
            break;
        case FavStationsStateUnset:
            //NSLog(@"FavStationsStateUnset");
            locationArrow.hidden = YES;
            fromLabel.hidden = YES;
            fromValueLabel.hidden = YES;
            [locationActivityIndicator stopAnimating];
            infoButton.enabled = NO;
            break;
        default:
            NSLog(@"!! Unsupported FavStationsState %d", favStationsState);
            break;
    }
    
    fromValueLabel.textColor = [UIColor colorWithRed:0.5411 green:0.1568 blue:0.8078 alpha:1.0];
    
    switch (locationState) {
        case LocationStateManualSelection:
            locationArrow.hidden = YES;
            [locationActivityIndicator stopAnimating];
            fromLabel.hidden = NO;
            fromValueLabel.text = [TransportUtils nicerName:departureStation.name];
            fromValueLabel.textColor = [PCValues textColor1];
            fromValueLabel.hidden = NO;
            break;
        case LocationStateLocated:
            //NSLog(@"LocationStateLocated");
            locationArrow.hidden = NO;
            [locationActivityIndicator stopAnimating];
            fromLabel.hidden = NO;
            fromValueLabel.text = [TransportUtils nicerName:departureStation.name];
            fromValueLabel.textColor = [PCValues textColorLocationBlue];
            fromValueLabel.hidden = NO;
            break;
        case LocationStateLoading:
            //NSLog(@"LocationStateLoading");
            locationArrow.hidden = NO;
            fromLabel.hidden = NO;
            fromValueLabel.hidden = YES;
            [locationActivityIndicator startAnimating];
            tableView.hidden = YES;
            welcomeTouchInfoInstructionsLabel.hidden = YES;
            connectionErrorLabel.hidden = YES;
            break;
        case LocationStateError:
            //NSLog(@"LocationStateError");
            locationArrow.hidden = YES;
            fromLabel.text = NSLocalizedStringFromTable(@"FromLabel", @"TransportPlugin", nil);
            fromLabel.hidden = NO;
            fromValueLabel.textColor = [UIColor redColor];
            fromValueLabel.text = NSLocalizedStringFromTable(@"ImpossibleLocateShort", @"TransportPlugin", nil);
            fromValueLabel.hidden = NO;
            [locationActivityIndicator stopAnimating];
            infoButton.enabled = YES; //so that user can switch to manual selection
            tableView.hidden = YES;
            welcomeTouchInfoInstructionsLabel.hidden = YES;
            connectionErrorLabel.hidden = YES;
            UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"" message:NSLocalizedStringFromTable(@"ImpossibleLocateLong", @"TransportPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [alert show];
            [alert release];
            break;
        case LocationStateUnset:
            //NSLog(@"LocationStateUnset");
            //nothing to do
            break;
        default:
            //NSLog(@"!! Unsupported LocationState %d", locationState);
            break;
    }
    
    switch (schedulesState) {
        case SchedulesStateLoaded:
            //NSLog(@"SchedulesStateLoaded");
            tableView.hidden = NO;
            welcomeTouchInfoInstructionsLabel.hidden = YES;
            connectionErrorLabel.hidden = YES;
            break;
        case SchedulesStateLoading:
            //NSLog(@"SchedulesStateLoading");
            tableView.hidden = NO;
            welcomeTouchInfoInstructionsLabel.hidden = YES;
            connectionErrorLabel.hidden = YES;
            break;
        case SchedulesStateError: //when timeout from server
            //NSLog(@"SchedulesStateError");
            tableView.hidden = YES;
            welcomeTouchInfoInstructionsLabel.hidden = YES;
            connectionErrorLabel.hidden = NO;
            return; //error => all UI has been updated to reflect error. Return from update method
            break;
        case SchedulesStateUnset:
            //NSLog(@"SchedulesStateUnset");
            tableView.hidden = YES;
            welcomeTouchInfoInstructionsLabel.hidden = YES;
            connectionErrorLabel.hidden = YES;
            break;
        default:
            //NSLog(@"!! Unsupported SchedulesState %d", schedulesState);
            break;
    }
    
}

- (IBAction)presentFavoriteStationsViewController:(id)sender {
    
    FavoriteStationsViewController* favStationsViewController = [[FavoriteStationsViewController alloc] initWithNibName:@"FavoriteStationsView" bundle:nil];
    favStationsViewController.modalTransitionStyle = UIModalTransitionStyleFlipHorizontal;
    UINavigationController* modalNavController = [[UINavigationController alloc] initWithRootViewController:favStationsViewController];
    modalNavController.navigationBar.tintColor = [PCValues pocketCampusRed];
    
    if ([self.navigationController respondsToSelector:@selector(presentViewController:animated:completion:)]) { // >= iOS 5.0
        [self presentViewController:modalNavController animated:YES completion:NULL];
    } else {
        [self.navigationController presentModalViewController:modalNavController animated:YES];
    }
    
    [favStationsViewController release];
    [modalNavController release];
    needToRefresh = YES;
}

- (IBAction)presentHelpViewController:(id)sender {
    HelpViewController* modalViewController = [[HelpViewController alloc] init];
    if ([self.navigationController respondsToSelector:@selector(presentViewController:animated:completion:)]) { // >= iOS 5.0
        [self presentViewController:modalViewController animated:YES completion:NULL];
    } else {
        [self.navigationController presentModalViewController:modalViewController animated:YES];
    }
}


/* UITableViewDelegate delegation */

- (void)tableView:(UITableView *)tableView_ didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NextDeparturesCell* cell = (NextDeparturesCell*)[tableView cellForRowAtIndexPath:indexPath];
    if (cell.loadingState != LoadingStateLoaded) {
        return;
    }
    
    QueryTripsResult* queryTripResult = [tripResults objectForKey:cell.transportStation.name];
    DestinationConnectionsListViewController* viewController = [[DestinationConnectionsListViewController alloc] initWithQueryTripResult:queryTripResult];
    [self.navigationController pushViewController:viewController animated:YES];
    [viewController release];
}

/* UITableViewDataSource delegation */

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    //
    NSIndexPath* biasedIndexPath = [self biasedIndexPathForIndexPath:indexPath];
    TransportStation* station = [favStations objectAtIndex:biasedIndexPath.row];
    
    if (tripResults == nil || [tripResults objectForKey:station.name] == nil) {
        NextDeparturesCell* newCell = [[NextDeparturesCell alloc] initWithDestinationStation:station loadingState:LoadingStateLoading];
        return [newCell autorelease];
    }
    
    if ([[tripResults objectForKey:station.name] isKindOfClass:[NSString class]] && [[tripResults objectForKey:station.name] isEqualToString:@"error"]) {
        NextDeparturesCell* newCell = [[NextDeparturesCell alloc] initWithDestinationStation:station loadingState:LoadingStateError];
        return [newCell autorelease];
    }
    
    QueryTripsResult* trip = [tripResults objectForKey:station.name];
    NSArray* redundantConnections = [TransportUtils nextRedundantDeparturesFromMessyResult:trip];
    
    return [[[NextDeparturesCell alloc] initWithQueryTripResult:trip redundantConnections:redundantConnections] autorelease];
   
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (schedulesState == SchedulesStateError) {
        return 0;
    }
    return favStations.count-1; //minus 1 because favStations also contains from station
}

- (NSIndexPath*)biasedIndexPathForStationName:(NSString*)stationName {
    NSUInteger stationIndex = [favStations indexOfObjectPassingTest:^(id object, NSUInteger idx, BOOL * stop){
        TransportStation* stationTmp = (TransportStation*)object;
        if ([stationTmp.name isEqualToString:stationName]) {
            *stop = YES;
            return YES;
        }
        return NO;
    }];
    
    NSUInteger departureStationIndex = [favStations indexOfObjectPassingTest:^(id object, NSUInteger idx, BOOL * stop){
        TransportStation* station = (TransportStation*)object;
        if (station.id == departureStation.id) {
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
    
    NSUInteger departureStationIndex = [favStations indexOfObjectPassingTest:^(id object, NSUInteger idx, BOOL * stop){
        TransportStation* station = (TransportStation*)object;
        if (station.id == departureStation.id) {
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

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)dealloc
{
    tableView.delegate = nil;
    tableView.dataSource = nil;
    [transportService cancelOperationsForDelegate:self];
    [transportService release];
    [favStations release];
    [departureStation release];
    [tripResults release];
    if (refreshTimer != nil) {
        [refreshTimer invalidate];
    }
    [refreshTimer release];
    [lastRefreshTimestamp release];
    [super dealloc];
}

@end
