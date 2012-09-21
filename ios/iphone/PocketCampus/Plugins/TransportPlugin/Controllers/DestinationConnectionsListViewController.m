//
//  DestinationConnectionsListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 10.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "DestinationConnectionsListViewController.h"

#import "PCValues.h"

#import "TransportUtils.h"

#import "ConnectionSummaryCell.h"

#import "ConnectionPartsListViewController.h"

@implementation DestinationConnectionsListViewController

static NSString* kConnectionCellIdentifier = @"ConnectionCell";

@synthesize tableView;

- (id)initWithQueryTripResult:(QueryTripsResult*)tripResult
{
    self = [super initWithNibName:@"DestinationConnectionsListView" bundle:nil];
    if (self) {
        self.title = NSLocalizedStringFromTable(@"Connections", @"TransportPlugin", nil);
        queryTripResult = [tripResult retain];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/transport/connections" withError:NULL];
    tableView.backgroundColor = [UIColor clearColor];
    UIView* backgroundView = [[UIView alloc] initWithFrame:tableView.frame];
    backgroundView.backgroundColor = [UIColor scrollViewTexturedBackgroundColor];
    tableView.backgroundView = backgroundView;
    [backgroundView release];
    
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void)viewWillAppear:(BOOL)animated {
    [tableView deselectRowAtIndexPath:[tableView indexPathForSelectedRow] animated:YES];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

/*UITableViewDelegate delegation */

- (void)tableView:(UITableView *)tableView_ didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (queryTripResult.connections.count <= indexPath.row-1) { //should not happen, (-1 because header cell)
        return;
    }
    TransportTrip* trip = [queryTripResult.connections objectAtIndex:indexPath.row-1];
    ConnectionPartsListViewController* viewController = [[ConnectionPartsListViewController alloc] initWithTransportTrip:trip];
    [self.navigationController pushViewController:viewController animated:YES];
    [viewController release];
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    UILabel* topStationsLabel = [[UILabel alloc] initWithFrame:CGRectMake(10.0, 0, 300.0, 70.0)];
    NSString* from = [TransportUtils nicerName:queryTripResult.from.name];
    NSString* to = [TransportUtils nicerName:queryTripResult.to.name];
    topStationsLabel.text = [NSString stringWithFormat:@"%@ ➔ %@", from, to];
    topStationsLabel.textColor = [UIColor whiteColor];
    topStationsLabel.shadowColor = [UIColor colorWithWhite:0.0 alpha:0.7];
    topStationsLabel.backgroundColor = [UIColor clearColor];
    topStationsLabel.textAlignment = UITextAlignmentCenter;
    topStationsLabel.font = [UIFont boldSystemFontOfSize:19.0];
    topStationsLabel.adjustsFontSizeToFitWidth = YES;
    //stationsTopLabel.shadowOffset = [PCValues shadowOffset1];
    topStationsLabel.shadowOffset = CGSizeMake(0.0, -1.0);
    return [topStationsLabel autorelease];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        return 70.0;
    }
    return 0.0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if(indexPath.row == 0) {
        return 34.0;
    }
    return 40.0;
}

/*UITableViewDataSource delegation */

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 0) { //header cell
        UITableViewCell* headerCell = [[[NSBundle mainBundle] loadNibNamed:@"DestinationConnectionsHeaderCell" owner:self options:nil] objectAtIndex:0];
        headerCell.selectionStyle = UITableViewCellSelectionStyleNone;
        headerCell.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.3];
        return headerCell;
    }
    
    if (queryTripResult.connections.count <= indexPath.row-1) { //should not happen, (-1 because header cell)
        return [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
    }
    
    TransportTrip* trip = [queryTripResult.connections objectAtIndex:indexPath.row-1];
    
    ConnectionSummaryCell* cell = [tableView dequeueReusableCellWithIdentifier:kConnectionCellIdentifier];
    
    if (cell == nil) {
        cell = [[[ConnectionSummaryCell alloc] initWithTransportTrip:trip reuseIdentifier:kConnectionCellIdentifier] autorelease];
    } else {
        [cell setTransportTrip:trip];
    }

    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (queryTripResult == nil || queryTripResult.connections == nil) {
        //TODO show message no connections
        return 0;
    }
    return queryTripResult.connections.count+1; //+1 because header row (from | to | time | ...)
}

- (void)dealloc
{
    [queryTripResult release];
    [super dealloc];
}

@end
