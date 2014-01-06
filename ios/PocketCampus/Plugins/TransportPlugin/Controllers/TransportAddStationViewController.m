//
//  TransportAddStationViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//


#import "TransportAddStationViewController.h"

#import "TransportUtils.h"

@interface TransportAddStationViewController ()<TransportServiceDelegate, UISearchBarDelegate, UITableViewDelegate, UITableViewDataSource>

@property (nonatomic, strong) IBOutlet PCTableViewAdditions* tableView;
@property (nonatomic, strong) IBOutlet UILabel* messageLabel;
@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* barActivityIndicator;
@property (nonatomic, strong) IBOutlet UISearchBar* searchBar;

@property (nonatomic, strong) TransportService* transportService;
@property (nonatomic, strong) NSOrderedSet* userStationsAtLoad;

@property (nonatomic, strong) NSArray* stations;
@property (nonatomic, strong) NSTimer* typingTimer;

@end

@implementation TransportAddStationViewController

#pragma mark - Init

- (id)init
{
    self = [super initWithNibName:@"TransportAddStationView" bundle:nil];
    if (self) {
        self.gaiScreenName = @"/transport/addStation";
        self.title = NSLocalizedStringFromTable(@"AddStation", @"TransportPlugin", nil);
        self.transportService = [TransportService sharedInstanceToRetain];
        self.userStationsAtLoad = [self.transportService.userTransportStations copy];
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.tableView.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return [PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleSubtitle];
    };
    self.tableView.contentInset = UIEdgeInsetsMake(64.0+self.searchBar.frame.size.height, 0, 0, 0);
    self.tableView.scrollIndicatorInsets = self.tableView.contentInset;
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(dismiss)];
    self.searchBar.placeholder = NSLocalizedStringFromTable(@"AddStationSearchFieldPlaceholder", @"TransportPlugin", nil);
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self trackScreen];
    TransportAddStationViewController* weakSelf __weak = self;
    [NSTimer scheduledTimerWithTimeInterval:0.0 block:^{
        [weakSelf.searchBar becomeFirstResponder]; //if done inline, it was slowing down the presentation of the view controller for some reason
    } repeats:NO];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Requests & utils

- (void)startAutocompleteRequest {
    [self.transportService cancelOperationsForDelegate:self];
    if (self.searchBar.text.length == 0) {
        return;
    }
    [self.barActivityIndicator startAnimating];
    [self.transportService autocomplete:self.searchBar.text delegate:self];
}

- (void)showNoResultMessage {
    [self.barActivityIndicator stopAnimating];
    self.tableView.hidden = YES;
    self.messageLabel.text = NSLocalizedStringFromTable(@"SearchStationNoResult", @"TransportPlugin", nil);
}

- (void)resultsError {
    [self.transportService cancelOperationsForDelegate:self];
    [self.barActivityIndicator stopAnimating];
    self.tableView.hidden = YES;
    self.messageLabel.text = NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil);
}

#pragma mark - Actions

- (void)dismiss {
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

#pragma mark - UISearchBarDelegate

- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText {
    self.messageLabel.text = nil;
    if (searchText.length == 0) {
        [self.barActivityIndicator stopAnimating];
        [self.transportService cancelOperationsForDelegate:self];
        self.tableView.hidden = YES;
        self.messageLabel.text = nil;
        [self.tableView reloadData];
        return;
    }
    [self.typingTimer invalidate];
    self.typingTimer = nil;
    if (searchText.length > 1) {
        self.typingTimer = [NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(startAutocompleteRequest) userInfo:nil repeats:NO];
    }
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar {
    [self.searchBar resignFirstResponder];
}

#pragma mark - TransportServiceDelegate

- (void)autocompleteFor:(NSString*)constraint didReturn:(NSArray*)results {
    [self.barActivityIndicator stopAnimating];
    if (results.count == 0) {
        [self showNoResultMessage];
        return;
    }
    
    if (self.searchBar.text.length == 0) { //result from previous non-empty search returned => return to initial state
        self.tableView.hidden = YES;
        self.messageLabel.text = nil;
        return;
    }
    
    self.tableView.hidden = NO;
    self.messageLabel.text = nil;
    
    self.stations = results;
    [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationAutomatic];
    
}

- (void)autocompleteFailedFor:(NSString*)constraint {
    [self resultsError];
}

- (void)serviceConnectionToServerFailed {
    [self.transportService cancelOperationsForDelegate:self];
    [self.barActivityIndicator stopAnimating];
    self.tableView.hidden = YES;
    self.messageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", @"Message that says that connection to server is impossible and that internet connection must be checked.");
}

#pragma mark - UIScrollViewDelegate

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
    [self.searchBar resignFirstResponder];
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView_ didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row >= self.stations.count) {
        return;
    }
    if ([self.tableView cellForRowAtIndexPath:indexPath].selectionStyle == UITableViewCellSelectionStyleNone) {
        //means already in stations, see tableView:cellForRowAtIndexPath: implementation
        [self.tableView deselectRowAtIndexPath:indexPath animated:NO];
        return;
    }
    TransportStation* newStation = self.stations[indexPath.row];
    NSMutableOrderedSet* userStations = [self.transportService.userTransportStations mutableCopy];
    [userStations addObject:newStation];
    self.transportService.userTransportStations = userStations;
    [self dismiss];
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    TransportStation* station = self.stations[indexPath.row];
    NSString* const identifier = [self.tableView autoInvalidatingReuseIdentifierForIdentifier:@"StationNameAutocompleteCell"];
    UITableViewCell* cell =  [self.tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:identifier];
        cell.textLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultTextLabelTextStyle];
        cell.detailTextLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultDetailTextLabelTextStyle];
        cell.detailTextLabel.textColor = [UIColor lightGrayColor];
    }
    if ([self.userStationsAtLoad containsObject:station]) {
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.textLabel.textColor = [UIColor lightGrayColor];
        cell.detailTextLabel.text = NSLocalizedStringFromTable(@"alreadyInYourStations", @"TransportPlugin", nil);
    } else {
        cell.selectionStyle = UITableViewCellSelectionStyleDefault;
        cell.textLabel.textColor = [UIColor blackColor];
        cell.detailTextLabel.text = nil;
    }
    cell.textLabel.text = station.shortName;
    
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.stations.count;
}

#pragma mark - Dealloc

- (void)dealloc
{
    [self.transportService cancelOperationsForDelegate:self];
}

@end
