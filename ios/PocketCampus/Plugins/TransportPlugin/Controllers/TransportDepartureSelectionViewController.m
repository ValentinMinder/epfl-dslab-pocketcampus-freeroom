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

//  Created by Loïc Gardiol on 24.11.13.

#import "TransportDepartureSelectionViewController.h"

#import "TransportAddStationViewController.h"

#import "TransportService.h"

static const NSUInteger kAutomaticSection = 0;
static const NSUInteger kStationsSection = 1;

@interface TransportDepartureSelectionViewController ()

@property (nonatomic, strong) TransportService* transportService;
@property (nonatomic, strong) NSOrderedSet* stations;

@property (nonatomic) BOOL appCouldNotAccessLocation;

@end

@implementation TransportDepartureSelectionViewController

#pragma mark - Init

- (id)init
{
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.gaiScreenName = @"/transport/departureSelection";
        self.title = NSLocalizedStringFromTable(@"DepartureStation", @"TransportPlugin", nil);
        self.transportService = [TransportService sharedInstanceToRetain];
        self.appCouldNotAccessLocation = ![PCUtils hasAppAccessToLocation];
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
    PCTableViewAdditions* tableViewAdditions = [[PCTableViewAdditions alloc] initWithFrame:self.tableView.frame style:self.tableView.style];
    self.tableView = tableViewAdditions;
    tableViewAdditions.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return [PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleSubtitle];
    };
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Close", @"PocketCampus", nil) style:UIBarButtonItemStylePlain target:self action:@selector(dismiss)];
    UIBarButtonItem* addButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addPressed)];
    self.navigationItem.leftBarButtonItem = addButton;
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshFromModel) name:kTransportUserTransportStationsModifiedNotification object:self.transportService];
    [self refreshFromModel];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
    if (self.appCouldNotAccessLocation) {
        self.appCouldNotAccessLocation = ![PCUtils hasAppAccessToLocation];
        [self.tableView reloadData];
    }
}

#pragma mark - Data load

- (void)refreshFromModel {
    self.stations = [self.transportService.userTransportStations mutableCopy];
    @try {
        [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:kStationsSection] withRowAnimation:UITableViewRowAnimationAutomatic];
    }
    @catch (NSException *exception) {
        [self.tableView reloadData];
    }

}

#pragma mark - Actions

- (void)dismiss {
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

- (void)addPressed {
    [self trackAction:@"AddStation"];
    TransportAddStationViewController* viewController = [TransportAddStationViewController new];
    PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:viewController];
    [self presentViewController:navController animated:YES completion:NULL];
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case kAutomaticSection:
            if (![PCUtils hasAppAccessToLocation]) {
                [self.tableView deselectRowAtIndexPath:indexPath animated:NO];
                return;
            }
            [self trackAction:@"SelectAutomaticMode"];
            self.transportService.userManualDepartureStation = nil;
            [self dismiss];
            break;
        case kStationsSection:
        {
            [self trackAction:@"SelectManualStation"];
            TransportStation* station = self.stations[indexPath.row];
            self.transportService.userManualDepartureStation = station;
            [self dismiss];
            break;
        }
        default:
            break;
    }
}

#pragma mark - UITableViewDataSource

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    switch (section) {
        case kAutomaticSection:
            break;
        case kStationsSection:
            return NSLocalizedStringFromTable(@"ChooseManually", @"TransportPlugin", nil);
            break;
    }
    return nil;
}

- (NSString*)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section {
    switch (section) {
        case kAutomaticSection:
            if (![PCUtils hasAppAccessToLocation]) {
                return NSLocalizedStringFromTable(@"AllowPocketCampusLocationToUserAutomaticDepartureFeature", @"TransportPlugin", nil);
            }
            break;
        case kStationsSection:
            break;
    }
    return nil;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell = nil;
    switch (indexPath.section) {
        case kAutomaticSection:
        {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
            cell.textLabel.text = NSLocalizedStringFromTable(@"Automatic", @"TransportPlugin", nil);
            cell.textLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultTextLabelTextStyle];
            cell.detailTextLabel.text = NSLocalizedStringFromTable(@"NearestStation", @"TransportPlugin", nil);
            cell.detailTextLabel.textColor = [UIColor lightGrayColor];
            cell.detailTextLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultDetailTextLabelTextStyle];
            if ([PCUtils hasAppAccessToLocation]) {
                cell.textLabel.textColor = [UIColor blackColor];
                cell.selectionStyle = UITableViewCellSelectionStyleDefault;
                cell.accessoryType = self.transportService.userManualDepartureStation ? UITableViewCellAccessoryNone : UITableViewCellAccessoryCheckmark;
            } else {
                cell.textLabel.textColor = [UIColor lightGrayColor];
                cell.selectionStyle = UITableViewCellSelectionStyleNone;
                cell.accessoryType = UITableViewCellAccessoryNone;
            }
            break;
        }
        case kStationsSection:
        {
            TransportStation* station = self.stations[indexPath.row];
            NSString* const identifier = [(PCTableViewAdditions*)tableView autoInvalidatingReuseIdentifierForIdentifier:@"StationCell"];
            cell = [self.tableView dequeueReusableCellWithIdentifier:identifier];
            if (!cell) {
                cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
            }
            cell.textLabel.text = station.shortName;
            cell.textLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultTextLabelTextStyle];
            cell.accessoryType = [self.transportService.userManualDepartureStation isEqualToTransportStation:station] ? UITableViewCellAccessoryCheckmark : UITableViewCellAccessoryNone;
            break;
        }
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (!self.stations.count) {
        return 0;
    }
    switch (section) {
        case kAutomaticSection:
            return 1;
        case kStationsSection:
            return self.stations.count;
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2; //automatic + stations
}

@end
