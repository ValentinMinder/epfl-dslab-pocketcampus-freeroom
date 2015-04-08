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

//  Created by Loïc Gardiol on 22.11.13.

#import "TransportStationsManagerViewController.h"

#import "TransportService.h"

#import "TransportUtils.h"

#import "TransportAddStationViewController.h"

#import "NSNotificationCenter+LGAAdditions.h"

static const NSUInteger kStationsSection = 0;
static const NSUInteger kRestoreDefaultSection = 1;

@interface TransportStationsManagerViewController ()<UIAlertViewDelegate>

@property (nonatomic, strong) TransportService* transportService;
@property (nonatomic, strong) NSMutableOrderedSet* stations;

@property (nonatomic, strong) UIAlertView* restoreAlertView;

@end

@implementation TransportStationsManagerViewController

- (id)init
{
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.gaiScreenName = @"/transport/userStations";
        self.title = NSLocalizedStringFromTable(@"MyStations", @"TransportPlugin", nil);
        self.transportService = [TransportService sharedInstanceToRetain];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    PCTableViewAdditions* tableViewAdditions = [[PCTableViewAdditions alloc] initWithFrame:self.tableView.frame style:self.tableView.style];
    self.tableView = tableViewAdditions;
    tableViewAdditions.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return [PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleDefault];
    };
	self.tableView.editing = YES;
    self.tableView.allowsSelectionDuringEditing = YES;
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addPressed)];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(donePressed)];
    self.navigationItem.rightBarButtonItem.style = UIBarButtonItemStylePlain;
    __weak __typeof(self) welf = self;
    [[NSNotificationCenter defaultCenter] lga_addObserver:self name:kTransportUserTransportStationsModifiedNotification object:self.transportService block:^(NSNotification *notif) {
        [welf refreshFromModelAnimated:YES];
    }];
    [self refreshFromModelAnimated:NO];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
}

#pragma mark - Actions

- (void)donePressed {
    self.transportService.userTransportStations = self.stations; //saving changes
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

- (void)addPressed {
    [self trackAction:PCGAITrackerActionAdd];
    self.transportService.userTransportStations = self.stations; //saving changes first
    TransportAddStationViewController* viewController = [TransportAddStationViewController new];
    [self presentViewController:[[PCNavigationController alloc] initWithRootViewController:viewController] animated:YES completion:NULL];
}

#pragma mark - Data load

- (void)refreshFromModelAnimated:(BOOL)animated {
    self.stations = [self.transportService.userTransportStations mutableCopy];
    @try {
        if (animated) {
            [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:kStationsSection] withRowAnimation:UITableViewRowAnimationAutomatic];
        } else {
            [self.tableView reloadData];
        }
    }
    @catch (NSException *exception) {
        [self.tableView reloadData];
    }
}

#pragma mark - UIAlertViewDelegate

- (void)alertView:(UIAlertView *)alertView willDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (alertView == self.restoreAlertView && buttonIndex == 1) { //OK
        [self trackAction:@"RestoreDefaultStations"];
        self.transportService.userTransportStations = nil;
        [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
    } else {
        [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:YES];
    }
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case kStationsSection:
            [self.tableView deselectRowAtIndexPath:indexPath animated:NO];
            break;
        case kRestoreDefaultSection:
        {
            self.restoreAlertView = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Reset", @"TransportPlugin", nil) message:NSLocalizedStringFromTable(@"ResetToDefaultStationsExplanations", @"TransportPlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) otherButtonTitles:@"OK", nil];
            self.restoreAlertView.delegate = self;
            [self.restoreAlertView show];
            break;
        }
        default:
            break;
    }
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case kStationsSection:
            return UITableViewCellEditingStyleDelete;
        default:
            return UITableViewCellEditingStyleNone;
    }
}

- (BOOL)tableView:(UITableView *)tableView shouldIndentWhileEditingRowAtIndexPath:(NSIndexPath *)indexPath {
    return (indexPath.section == kStationsSection);
}

- (NSIndexPath*)tableView:(UITableView *)tableView targetIndexPathForMoveFromRowAtIndexPath:(NSIndexPath *)sourceIndexPath toProposedIndexPath:(NSIndexPath *)proposedDestinationIndexPath {
    if (proposedDestinationIndexPath.section == kRestoreDefaultSection) {
        return [NSIndexPath indexPathForRow:self.stations.count-1 inSection:kStationsSection];
    }
    return proposedDestinationIndexPath;
}

#pragma mark - UITableViewDataSource

- (NSString*)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section {
    if (section == kStationsSection && self.stations && self.stations.count < 2) {
        return NSLocalizedStringFromTable(@"Need2StationsClickPlusToAdd", @"TransportPlugin", nil);
    }
    return nil;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell = nil;
    switch (indexPath.section) {
        case kStationsSection:
        {
            TransportStation* station = self.stations[indexPath.row];
            NSString* const identifier = [(PCTableViewAdditions*)tableView autoInvalidatingReuseIdentifierForIdentifier:@"StationCell"];
            cell = [self.tableView dequeueReusableCellWithIdentifier:identifier];
            if (!cell) {
                cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
                cell.selectionStyle = UITableViewCellSelectionStyleNone;
                cell.textLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultTextLabelTextStyle];
            }
            cell.textLabel.text = station.shortName;
            break;
        }
        case kRestoreDefaultSection:
        {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.textLabel.text = NSLocalizedStringFromTable(@"ResetToDefaultStations", @"TransportPlugin", nil);
            cell.textLabel.textAlignment = NSTextAlignmentCenter;
            cell.textLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleBody];
            break;
        }
        default:
        break;
    }
    
    return cell;
}

- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
    return (indexPath.section == kStationsSection);
}

- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)sourceIndexPath toIndexPath:(NSIndexPath *)destinationIndexPath {
    if (sourceIndexPath.row == destinationIndexPath.row) {
        return;
    }
    [self trackAction:PCGAITrackerActionReorder];
    TransportStation* station = self.stations[sourceIndexPath.row];
    [self.stations removeObjectAtIndex:sourceIndexPath.row];
    if (destinationIndexPath.row > [self.stations count]) {
        [self.stations addObject:station];
    } else {
        [self.stations insertObject:station atIndex:destinationIndexPath.row];
    }
    [self.tableView moveRowAtIndexPath:sourceIndexPath toIndexPath:destinationIndexPath];
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle != UITableViewCellEditingStyleDelete) {
        return;
    }
    [self trackAction:PCGAITrackerActionDelete];
    [self.stations removeObjectAtIndex:indexPath.row];
    self.transportService.userTransportStations = self.stations;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case kStationsSection:
            return self.stations.count;
        case kRestoreDefaultSection:
            return 1;
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2; //stations + restore default
}

#pragma mark - Dealloc

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
