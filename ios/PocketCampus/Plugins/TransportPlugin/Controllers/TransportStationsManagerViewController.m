//
//  TransportStationsManagerViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 22.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "TransportStationsManagerViewController.h"

#import "TransportService.h"

#import "TransportUtils.h"

#import "TransportAddStationViewController.h"

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
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshFromModel) name:kTransportUserTransportStationsModifiedNotification object:self.transportService];
    [self refreshFromModel];
}

#pragma mark - Actions

- (void)donePressed {
    self.transportService.userTransportStations = self.stations; //saving changes
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

- (void)addPressed {
    self.transportService.userTransportStations = self.stations; //saving changes first
    TransportAddStationViewController* viewController = [TransportAddStationViewController new];
    [self presentViewController:[[PCNavigationController alloc] initWithRootViewController:viewController] animated:YES completion:NULL];
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

#pragma mark - UIAlertViewDelegate

- (void)alertView:(UIAlertView *)alertView willDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (alertView == self.restoreAlertView && buttonIndex == 1) { //OK
        self.transportService.userTransportStations = nil;
        [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
    } else {
        [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:YES];
    }
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == kStationsSection) {
        [self.tableView deselectRowAtIndexPath:indexPath animated:NO];
        return;
    }
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
