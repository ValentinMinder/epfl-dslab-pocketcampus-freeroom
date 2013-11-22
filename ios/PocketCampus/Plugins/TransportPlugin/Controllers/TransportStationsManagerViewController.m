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

@interface TransportStationsManagerViewController ()

@property (nonatomic, strong) TransportService* transportService;
@property (nonatomic, strong) NSMutableArray* stations;

@end

@implementation TransportStationsManagerViewController

- (id)init
{
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.transportService = [TransportService sharedInstanceToRetain];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	self.tableView.editing = YES;
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
    
}

@end
