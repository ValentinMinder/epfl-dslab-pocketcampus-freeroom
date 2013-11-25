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

@interface TransportStationsManagerViewController ()

@property (nonatomic, strong) TransportService* transportService;
@property (nonatomic, strong) NSMutableOrderedSet* stations;

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
	self.tableView.editing = YES;
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addPressed)];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(donePressed)];
    self.navigationItem.rightBarButtonItem.style = UIBarButtonItemStylePlain;
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshFromModel) name:kUserTransportStationsModifiedNotificationName object:self.transportService];
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
        [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationAutomatic];
    }
    @catch (NSException *exception) {
        [self.tableView reloadData];
    }
}

#pragma mark - UITableViewDelegate

/*- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
}*/

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    return UITableViewCellEditingStyleDelete;
}

#pragma mark - UITableViewDataSource

- (NSString*)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section {
    if (self.stations && self.stations.count < 2) {
        return NSLocalizedStringFromTable(@"Need2StationsClickPlusToAdd", @"TransportPlugin", nil);
    }
    return nil;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    TransportStation* station = self.stations[indexPath.row];
    static NSString* identifier = @"StationCell";
    UITableViewCell* cell = [self.tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
    }
    cell.textLabel.text = station.shortName;
    return cell;
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
    return self.stations.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

#pragma mark - Dealloc

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
