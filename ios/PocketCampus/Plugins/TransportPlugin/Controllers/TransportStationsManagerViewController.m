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
        self.transportService = [TransportService sharedInstanceToRetain];
        self.stations = [self.transportService.userTransportStations mutableCopy];
        self.title = NSLocalizedStringFromTable(@"MyStations", @"TransportPlugin", nil);
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	self.tableView.editing = YES;
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addPressed)];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(donePressed)];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshFromModel) name:kUserTransportStationsModifiedNotificationName object:self.transportService];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    self.transportService.userTransportStations = self.stations;
}

#pragma mark - Actions

- (void)donePressed {
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

- (void)addPressed {
    TransportAddStationViewController* viewController = [TransportAddStationViewController new];
    [self presentViewController:[[PCNavigationController alloc] initWithRootViewController:viewController] animated:YES completion:NULL];
}

#pragma mark - Data load

- (void)refreshFromModel {
    self.stations = [self.transportService.userTransportStations mutableCopy];
    [self.tableView reloadData];
}

#pragma mark - UITableViewDelegate

/*- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
}*/

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    return UITableViewCellEditingStyleDelete;
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    TransportStation* station = self.stations[indexPath.row];
    static NSString* identifier = @"StationCell";
    UITableViewCell* cell = [self.tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
    }
    cell.textLabel.text = [TransportUtils nicerName:station.name];
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
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle != UITableViewCellEditingStyleDelete) {
        return;
    }
    [self.stations removeObjectAtIndex:indexPath.row];
    [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationAutomatic];
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
