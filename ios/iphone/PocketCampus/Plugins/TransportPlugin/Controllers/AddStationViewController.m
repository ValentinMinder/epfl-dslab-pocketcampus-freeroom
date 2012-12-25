//
//  AddStationViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "AddStationViewController.h"

#import "TransportUtils.h"

static NSString* kStationNameAutocompleteResult = @"StationNameAutocompleteCell";

@implementation AddStationViewController

@synthesize tableView, messageLabel, barActivityIndicator, searchBar;

- (id)init
{
    self = [super initWithNibName:@"AddStationView" bundle:nil];
    if (self) {
        // Custom initialization
        transportService = [[TransportService sharedInstanceToRetain] retain];
        stations = nil;
        typingTimer = nil;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/transport/mystations/add" withError:NULL];
    searchBar.prompt = NSLocalizedStringFromTable(@"SearchBarStationPrompt", @"TransportPlugin", nil);
    searchBar.isAccessibilityElement = YES;
    searchBar.accessibilityIdentifier = @"SearchBar";
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [searchBar becomeFirstResponder];
}

- (void)dismiss {
    if ([self.presentingViewController respondsToSelector:@selector(dismissViewControllerAnimated:completion:)]) {
        [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL]; //only available in iOS 5.0
    } else {
        [self.presentingViewController dismissModalViewControllerAnimated:YES];
    }
}

- (void)startAutocompleteRequest {
    [transportService cancelOperationsForDelegate:self];
    if (searchBar.text.length == 0) {
        return;
    }
    [barActivityIndicator startAnimating];
    [transportService autocomplete:searchBar.text delegate:self];
}

- (void)showNoResultMessage {
    [barActivityIndicator stopAnimating];
    tableView.hidden = YES;
    messageLabel.text = NSLocalizedStringFromTable(@"SearchStationNoResult", @"TransportPlugin", @"Message that says the autocomplete returned empty result.");
    messageLabel.hidden = NO;
}

- (void)resultsError {
    [transportService cancelOperationsForDelegate:self];
    [barActivityIndicator stopAnimating];
    tableView.hidden = YES;
    messageLabel.text = NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", @"Message that says that connection to server is impossible and that internet connection must be checked.");
    messageLabel.hidden = NO;
}

- (void)enableCancelButton {
    for (UIView* view in searchBar.subviews) {
        if ([view isKindOfClass:[UIButton class]]) {
            ((UIButton*)view).enabled = YES;
        }
    }
}

/* TransportServiceDelegate delegation */

- (void)autocompleteFor:(NSString*)constraint didReturn:(NSArray*)results {
    [barActivityIndicator stopAnimating];
    if (results.count == 0) {
        [self showNoResultMessage];
        return;
    }
    
    if (searchBar.text.length == 0) { //result from previous non-empty search returned => return to initial state
        tableView.hidden = YES;
        messageLabel.hidden = YES;
        return;
    }
    
    tableView.hidden = NO;
    messageLabel.hidden = YES;
    
    [stations release];
    stations = [results retain];
    [tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationFade];
    
}

- (void)autocompleteFailedFor:(NSString*)constraint {
    [self resultsError];
}

- (void)serviceConnectionToServerTimedOut {
    [transportService cancelOperationsForDelegate:self];
    [barActivityIndicator stopAnimating];
    tableView.hidden = YES;
    messageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", @"Message that says that connection to server is impossible and that internet connection must be checked.");
    messageLabel.hidden = NO;
}

/* Search bar delegation */

- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText {
    messageLabel.text = @"";
    if (searchText.length == 0) {
        [barActivityIndicator stopAnimating];
        [transportService cancelOperationsForDelegate:self];
        tableView.hidden = YES;
        messageLabel.hidden = YES;
        [tableView reloadData];
        return;
    }
    [typingTimer invalidate];
    [typingTimer release];
    typingTimer = nil;
    if (searchText.length > 1) {
        typingTimer = [[NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(startAutocompleteRequest) userInfo:nil repeats:NO] retain];
    }
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar_ {
    [searchBar resignFirstResponder];
    [self enableCancelButton];
}

- (void)searchBarCancelButtonClicked:(UISearchBar *)searchBar {
    [self dismiss];
}

/* UIScrollView delegation */

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
    [searchBar resignFirstResponder];
    [self enableCancelButton];
}

/* UITableViewDelegate delegation */

- (void)tableView:(UITableView *)tableView_ didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row >= stations.count) {
        return;
    }
    TransportStation* newStation = [stations objectAtIndex:indexPath.row];
    NSMutableArray* favStations = [[transportService userFavoriteTransportStations] mutableCopy];
    for (TransportStation* station in favStations) {
        if (newStation.id == station.id) {
            [tableView deselectRowAtIndexPath:indexPath animated:YES];
            UIAlertView* alertView = [[UIAlertView alloc] initWithTitle:@"" message:NSLocalizedStringFromTable(@"StationAlreadyFavorite", @"TransportPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [alertView show];
            [alertView release];
            [favStations release];
            return;
        }
    }
    [favStations addObject:newStation]; //inserting at end of list
    [transportService saveUserFavoriteTransportStations:favStations];
    [favStations release];
    [self dismiss];
}

/* UITableViewDataSource delegation */

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    TransportStation* station = [stations objectAtIndex:indexPath.row];
    UITableViewCell* newCell =  [tableView dequeueReusableCellWithIdentifier:kStationNameAutocompleteResult];
    if (newCell == nil) {
        newCell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kStationNameAutocompleteResult] autorelease];
    }
    newCell.textLabel.text = [TransportUtils nicerName:station.name];
    return newCell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (stations == nil) {
        return 0;
    }
    return stations.count;
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)dealloc
{
    [transportService cancelOperationsForDelegate:self];
    tableView.delegate = nil;
    tableView.dataSource = nil;
    [stations release];
    [transportService release];
    [typingTimer release];
    [super dealloc];
}

@end
