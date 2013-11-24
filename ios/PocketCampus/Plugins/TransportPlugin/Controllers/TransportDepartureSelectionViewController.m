//
//  TransportDepartureSelectionViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "TransportDepartureSelectionViewController.h"

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
        self.title = NSLocalizedStringFromTable(@"DepartureStation", @"TransportPlugin", nil);
        self.transportService = [TransportService sharedInstanceToRetain];
        self.stations = [self.transportService.userTransportStations copy];
        self.appCouldNotAccessLocation = ![PCUtils hasAppAccessToLocation];
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(dismiss)];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    if (self.appCouldNotAccessLocation) {
        self.appCouldNotAccessLocation = ![PCUtils hasAppAccessToLocation];
        [self.tableView reloadData];
    }
}

#pragma mark - Others

- (void)dismiss {
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case kAutomaticSection:
            if (![PCUtils hasAppAccessToLocation]) {
                [self.tableView deselectRowAtIndexPath:indexPath animated:NO];
                return;
            }
            self.transportService.userManualDepartureStation = nil;
            [self dismiss];
            break;
        case kStationsSection:
        {
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
            cell.detailTextLabel.text = NSLocalizedStringFromTable(@"NearestStation", @"TransportPlugin", nil);
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
            static NSString* identifier = @"StationCell";
            cell = [self.tableView dequeueReusableCellWithIdentifier:identifier];
            if (!cell) {
                cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
            }
            cell.textLabel.text = station.shortName;
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
