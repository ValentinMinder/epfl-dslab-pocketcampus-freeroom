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




//  Created by Loïc Gardiol on 10.04.12.


#import "TransportTripsListViewController.h"

#import "TransportUtils.h"

#import "TransportTripCell.h"

#import "TransportTripPartsViewController.h"

#import "TransportService.h"

@interface TransportTripsListViewController ()

@property (nonatomic, readwrite, strong) QueryTripsResult* tripResult;
@property (nonatomic, strong) NSArray* trips;

@end

@implementation TransportTripsListViewController

#pragma mark - Init

- (id)initWithQueryTripResult:(QueryTripsResult*)tripResult
{
    [PCUtils throwExceptionIfObject:tripResult notKindOfClass:[QueryTripsResult class]];
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.gaiScreenName = @"/transport/trips";
        self.title = NSLocalizedStringFromTable(@"Trips", @"TransportPlugin", nil);
        self.tripResult = tripResult;
        self.trips = self.tripResult.connections;
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
    [self trackScreen];
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
    if (self.trips.count <= indexPath.row-1) { //should not happen, (-1 because header cell)
        return;
    }
    TransportTrip* trip = self.trips[indexPath.row-1];
    TransportTripPartsViewController* viewController = [[TransportTripPartsViewController alloc] initWithTransportTrip:trip];
    [self.navigationController pushViewController:viewController animated:YES];
    [self trackAction:@"ViewTripDetails"];
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 0) { //header cell
        PCTableViewCellAdditions* headerCell = [[NSBundle mainBundle] loadNibNamed:@"TransportTripsHeaderCell" owner:self options:nil][0];
        headerCell.selectionStyle = UITableViewCellSelectionStyleNone;
        headerCell.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.04];
        headerCell.accessibilityDisabled = YES;
        return headerCell;
    }
    
    if (self.trips.count <= indexPath.row-1) { //should not happen, (-1 because header cell)
        return [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
    }
    
    TransportTrip* trip = self.trips[indexPath.row-1];
    static NSString* const identifier = @"TripCell";
    TransportTripCell* cell = [self.tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[TransportTripCell alloc] initWithReuseIdentifier:identifier];
    }
    cell.trip = trip;
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (!self.trips.count) {
        return 0;
    }
    return self.trips.count+1; //+1 because header row (from | to | time | ...)
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

@end
