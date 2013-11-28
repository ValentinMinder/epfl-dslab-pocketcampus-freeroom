//
//  TransportTripsListViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 10.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "TransportTripsListViewController.h"

#import "TransportUtils.h"

#import "TransportTripCell.h"

#import "ConnectionPartsListViewController.h"

#import "TransportService.h"

@interface TransportTripsListViewController ()

@property (nonatomic, readwrite, strong) QueryTripsResult* tripResult;
@property (nonatomic, strong) NSArray* nonLeftTrips;

@end

@implementation TransportTripsListViewController

#pragma mark - Init

- (id)initWithQueryTripResult:(QueryTripsResult*)tripResult
{
    [PCUtils throwExceptionIfObject:tripResult notKindOfClass:[QueryTripsResult class]];
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.title = NSLocalizedStringFromTable(@"Trips", @"TransportPlugin", nil);
        self.tripResult = tripResult;
        self.nonLeftTrips = self.tripResult.nonLeftTrips;
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.tableView.separatorInset = UIEdgeInsetsZero;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[PCGAITracker sharedTracker] trackScreenWithName:@"/transport/trips"];
}

- (NSUInteger)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskPortrait;
}

#pragma mark - UITableViewDelegate

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    UILabel* topStationsLabel = [[UILabel alloc] initWithFrame:CGRectMake(10.0, 0, 300.0, 80.0)];
    topStationsLabel.text = [NSString stringWithFormat:@"%@ ➝ %@", [self.tripResult.from.shortName uppercaseString], [self.tripResult.to.shortName uppercaseString]];
    topStationsLabel.textColor = [UIColor colorWithWhite:0.0 alpha:0.6];
    topStationsLabel.textAlignment = NSTextAlignmentCenter;
    topStationsLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:19.0];
    topStationsLabel.adjustsFontSizeToFitWidth = YES;
    return topStationsLabel;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 80.0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if(indexPath.row == 0) {
        return 34.0;
    }
    return 40.0;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (self.nonLeftTrips.count <= indexPath.row-1) { //should not happen, (-1 because header cell)
        return;
    }
    TransportTrip* trip = self.nonLeftTrips[indexPath.row-1];
    ConnectionPartsListViewController* viewController = [[ConnectionPartsListViewController alloc] initWithTransportTrip:trip];
    [self.navigationController pushViewController:viewController animated:YES];
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 0) { //header cell
        UITableViewCell* headerCell = [[[NSBundle mainBundle] loadNibNamed:@"TransportTripsHeaderCell" owner:self options:nil] objectAtIndex:0];
        headerCell.selectionStyle = UITableViewCellSelectionStyleNone;
        headerCell.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.04];
        return headerCell;
    }
    
    if (self.nonLeftTrips.count <= indexPath.row-1) { //should not happen, (-1 because header cell)
        return [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
    }
    
    TransportTrip* trip = self.nonLeftTrips[indexPath.row-1];
    static NSString* identifier = @"TripCell";
    TransportTripCell* cell = [self.tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[TransportTripCell alloc] initWithReuseIdentifier:identifier];
    }
    cell.trip = trip;
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (!self.nonLeftTrips.count) {
        return 0;
    }
    return self.nonLeftTrips.count+1; //+1 because header row (from | to | time | ...)
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

@end
