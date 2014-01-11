



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
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addPressed)];
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
            [self trackAction:@"AutomaticModeSelected"];
            self.transportService.userManualDepartureStation = nil;
            [self dismiss];
            break;
        case kStationsSection:
        {
            [self trackAction:@"StationManuallySelected"];
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
