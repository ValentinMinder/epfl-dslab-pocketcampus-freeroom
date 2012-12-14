//
//  FavoriteStationsViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "FavoriteStationsViewController.h"

#import "AddStationViewController.h"

#import "PCValues.h"

#import "TransportUtils.h"

#import "TransportHelpViewController.h"

static int MAX_NB_FAV_STATIONS = 10;

static NSString* kTransportStationNameCellIdentifier = @"StationNameCell";

@implementation FavoriteStationsViewController

@synthesize tableView, touchAddInstructionsLabel, addButton;

@synthesize dev_location_test; //DEV, to remove

- (id)init
{
    self = [super initWithNibName:@"FavoriteStationsView" bundle:nil];
    if (self) {
        
        transportService = [[TransportService sharedInstanceToRetain] retain];
        selectedStation = [[transportService userManualDepartureStation] retain];
        favStations = nil;
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/transport/mystations" withError:NULL];
    tableView.editing = NO;
    tableView.sectionHeaderHeight = 0.0;
    tableView.sectionFooterHeight = 10.0;
    touchAddInstructionsLabel.text = NSLocalizedStringFromTable(@"TouchAddStationInstructions", @"TransportPlugin", nil);
    self.title = NSLocalizedStringFromTable(@"FavoriteStationsTitle", @"TransportPlugin", nil);
    [self setNavBarNormalModeAnimated:NO];
    tableView.backgroundColor = [UIColor clearColor];
    UIView* backgroundView = [[UIView alloc] initWithFrame:tableView.frame];
    backgroundView.backgroundColor = [UIColor viewFlipsideBackgroundColor];
    tableView.backgroundView = backgroundView;
    [backgroundView release];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [favStations release];
    favStations = [[transportService userFavoriteTransportStations] mutableCopy];
    if (tableView.editing) {
        [self setNavBarEditingModeAnimated:NO];
    } else {
        [self setNavBarNormalModeAnimated:NO];
    }
    [tableView reloadData];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    //done here instead of viewDidLoad because otherwise, nav buttons move during flip
}

- (void)setNavBarNoStationMode:(BOOL)animated  {
    [self.navigationItem setLeftBarButtonItem:nil animated:animated];
    
    UIBarButtonItem* doneButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Done", @"PocketCampus", nil) style:UIBarButtonItemStylePlain target:self action:@selector(doneButtonPressed)];
    [self.navigationItem setRightBarButtonItem:doneButton animated:animated];
    [doneButton release];
}

- (void)setNavBarEditingModeAnimated:(BOOL)animated  {
    UIBarButtonItem* saveButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(saveButtonPressed)];
    [self.navigationItem setLeftBarButtonItem:saveButton animated:animated];
    [saveButton release];
    
    UIBarButtonItem* clearAllButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"ClearAll", @"TransportPlugin", nil) style:UIBarButtonItemStylePlain target:self action:@selector(clearAllButtonPressed)];
    [self.navigationItem setRightBarButtonItem:clearAllButton animated:animated];
    [clearAllButton release];
}

- (void)setNavBarNormalModeAnimated:(BOOL)animated {
    UIBarButtonItem* editButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemEdit target:self action:@selector(editButtonPressed)];
    [self.navigationItem setLeftBarButtonItem:editButton animated:animated];
    [editButton release];
    
    UIBarButtonItem* doneButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Done", @"PocketCampus", nil) style:UIBarButtonItemStylePlain target:self action:@selector(doneButtonPressed)];
    [self.navigationItem setRightBarButtonItem:doneButton animated:animated];
    [doneButton release];
}

- (void)saveButtonPressed{
    [self setNavBarNormalModeAnimated:YES];
    [tableView setEditing:NO animated:YES];
    [tableView reloadSections:[NSIndexSet indexSetWithIndexesInRange:NSMakeRange(0, 2)] withRowAnimation:UITableViewRowAnimationFade];
}

- (void)clearAllButtonPressed {
    [favStations removeAllObjects];
    [transportService saveUserFavoriteTransportStations:favStations]; //empty array. Not saving nil because nil will make NextDeparturesViewController download defaults stations
    [selectedStation release];
    selectedStation = nil;
    [transportService saveUserManualDepartureStation:nil];
    NSIndexSet* sectionsSet = [NSIndexSet indexSetWithIndexesInRange:NSMakeRange(0, 2)];
    [tableView deleteSections:sectionsSet withRowAnimation:UITableViewRowAnimationFade];
}

- (IBAction)addButtonPressed {
    AddStationViewController* viewController = [[AddStationViewController alloc] init];
    if([self respondsToSelector:@selector(presentingViewController)]) {
        [self presentViewController:viewController animated:YES completion:NULL]; //only available in iOS 5.0
    } else {
        [self presentModalViewController:viewController animated:YES];
    }
    [viewController release];
}

- (IBAction)presentHelpViewController:(id)sender {
    TransportHelpViewController* viewController = [[TransportHelpViewController alloc] initWithHTMLFilePath:[[NSBundle mainBundle] pathForResource:@"MyStationsHelp" ofType:@"html"]];
    UINavigationController* navController = [[UINavigationController alloc] initWithRootViewController:viewController];
    if ([self.navigationController respondsToSelector:@selector(presentViewController:animated:completion:)]) { // >= iOS 5.0
        [self presentViewController:navController animated:YES completion:NULL];
    } else {
        [self.navigationController presentModalViewController:navController animated:YES];
    }
    [viewController release];
    [navController release];
}


- (void)editButtonPressed {
    [self setNavBarEditingModeAnimated:YES];
    [tableView setEditing:YES animated:YES];
    [tableView reloadSections:[NSIndexSet indexSetWithIndexesInRange:NSMakeRange(0, 2)] withRowAnimation:UITableViewRowAnimationFade];
}

- (void)doneButtonPressed {
    [transportService saveUserFavoriteTransportStations:favStations];
    if ([self.presentingViewController respondsToSelector:@selector(dismissViewControllerAnimated:completion:)]) { // >= iOS 5.0
        [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
    } else {
        [self.presentingViewController dismissModalViewControllerAnimated:YES];
    }
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (NSIndexPath*)indexPathOfSelectedCell {
    if (selectedStation == nil) {
        return [NSIndexPath indexPathForRow:0 inSection:0];
    }
    NSUInteger index = [favStations indexOfObjectPassingTest:^(id object, NSUInteger idx, BOOL * stop){
        TransportStation* station = (TransportStation*)object;
        if (station.id == selectedStation.id) {
            *stop = YES;
            return YES;
        }
        return NO;
    }];
    return [NSIndexPath indexPathForRow:index inSection:1];
}

/* UITableViewDelegate delegation */

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView_ editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return UITableViewCellEditingStyleNone;
    }
    return UITableViewCellEditingStyleDelete;
}

- (BOOL)tableView:(UITableView *)tableView shouldIndentWhileEditingRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return NO;
    }
    return YES;
}

- (NSIndexPath *)tableView:(UITableView *)tableView targetIndexPathForMoveFromRowAtIndexPath:(NSIndexPath *)sourceIndexPath toProposedIndexPath:(NSIndexPath *)proposedDestinationIndexPath {
    if (proposedDestinationIndexPath.section == 0) {
        return [NSIndexPath indexPathForRow:0 inSection:1];//should not put cells in section 0
    }
    return proposedDestinationIndexPath;
}

- (void)tableView:(UITableView *)tableView_ didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if ([[tableView cellForRowAtIndexPath:indexPath] accessoryType] == UITableViewCellAccessoryCheckmark) {
        if (indexPath.section == 0) {
            /* if user tries to tap "Automatic" when it's already selected, help him understand he can also select manually another station => select the first one */
            [transportService saveUserManualDepartureStation:nil];
            [selectedStation release];
            selectedStation = [[favStations objectAtIndex:0] retain];
            [[tableView cellForRowAtIndexPath:indexPath] setAccessoryType:UITableViewCellAccessoryNone];
            [[tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:1]] setAccessoryType:UITableViewCellAccessoryCheckmark];
            [tableView reloadSections:[NSIndexSet indexSetWithIndex:1] withRowAnimation:UITableViewRowAnimationFade];
        }
        return;
    }
    [[tableView cellForRowAtIndexPath:[self indexPathOfSelectedCell]] setAccessoryType:UITableViewCellAccessoryNone];
    [[tableView cellForRowAtIndexPath:indexPath] setAccessoryType:UITableViewCellAccessoryCheckmark];
    [selectedStation release];
    selectedStation = [[favStations objectAtIndex:indexPath.row] retain];
    
    if (indexPath.section == 0) { //user has selected automatic nearest station
        [transportService saveUserManualDepartureStation:nil];
        [selectedStation release];
        selectedStation = nil;
        [tableView reloadSections:[NSIndexSet indexSetWithIndex:1] withRowAnimation:UITableViewRowAnimationFade];
    } else if (indexPath.section == 1) {
        TransportStation* station = [favStations objectAtIndex:indexPath.row];
        [transportService saveUserManualDepartureStation:station];
        [selectedStation release];
        selectedStation = [station retain];
        [tableView reloadSections:[NSIndexSet indexSetWithIndex:1] withRowAnimation:UITableViewRowAnimationFade];
    } else {
        //should not happen
    }
    
}


/* UITableViewDataSource delegation */

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        UITableViewCell* newCell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
        newCell.textLabel.textColor = [PCValues textColorLocationBlue];
        newCell.textLabel.text = NSLocalizedStringFromTable(@"NearestFavoriteStation", @"TransportPlugin", nil);
        newCell.textLabel.adjustsFontSizeToFitWidth = YES;
        if (selectedStation == nil) {
            newCell.accessoryType = UITableViewCellAccessoryCheckmark;
        }
        return newCell;
    }
    TransportStation* station = [favStations objectAtIndex:indexPath.row];
    UITableViewCell* newCell = [tableView dequeueReusableCellWithIdentifier:kTransportStationNameCellIdentifier];
    if (newCell == nil) {
        newCell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kTransportStationNameCellIdentifier] autorelease];
    }
    if (selectedStation != nil && selectedStation.id == station.id) {
        newCell.accessoryType = UITableViewCellAccessoryCheckmark;
    } else {
        newCell.accessoryType = UITableViewCellAccessoryNone;
    }
    newCell.textLabel.text = [TransportUtils nicerName:station.name];
    return newCell;
}

- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)sourceIndexPath toIndexPath:(NSIndexPath *)destinationIndexPath {
    TransportStation* movedStation = [[favStations objectAtIndex:sourceIndexPath.row] retain];
    [favStations removeObjectAtIndex:sourceIndexPath.row];
    [favStations insertObject:movedStation atIndex:destinationIndexPath.row];
    [movedStation release];
    [transportService saveUserFavoriteTransportStations:favStations];
}

- (void)tableView:(UITableView *)tableView_ commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 1 && editingStyle == UITableViewCellEditingStyleDelete) {
        NSIndexPath* selectedIndexPath = [self indexPathOfSelectedCell];
        [favStations removeObjectAtIndex:indexPath.row];
        if (favStations.count == 0) {
            NSIndexSet* sectionsSet = [NSIndexSet indexSetWithIndexesInRange:NSMakeRange(0, 2)];
            [tableView deleteSections:sectionsSet withRowAnimation:UITableViewRowAnimationFade];
            [selectedStation release];
            selectedStation = nil;
            [transportService saveUserManualDepartureStation:nil];
        } else {
            if ([tableView numberOfRowsInSection:0] == 1) { /* ugly trick to make deletion work both in edit mode or if the user swipes a row in normal mode. Otherwise the math is wrong (see exception) */
                tableView.editing = NO;
            }
            [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationLeft];
            if ([indexPath isEqual:selectedIndexPath]) {
                [selectedStation release];
                selectedStation = nil;
                [transportService saveUserManualDepartureStation:nil];
                [tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationNone];
                [tableView reloadSections:[NSIndexSet indexSetWithIndex:1] withRowAnimation:UITableViewRowAnimationFade];
            }
        }
        [transportService saveUserFavoriteTransportStations:favStations];
        [self enableAndDisableAddButtonIfNecessary];
    }
}

- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 1) {
        return YES;
    }
    return NO;
}

- (CGFloat)tableView:(UITableView *)tableView_ heightForHeaderInSection:(NSInteger)section {
    if (section == 0 && !tableView.editing) {
        return 42.0;
    }
    return 0.0;
}

- (CGFloat)tableView:(UITableView *)tableView_ heightForFooterInSection:(NSInteger)section {
    
    if (section == 0) {
        return 0.0;
    }
    
    if (section == 1) {
        return [self tableView:tableView viewForFooterInSection:section].frame.size.height;
    }
    
    return 0.0;
}

- (UIView*)tableView:(UITableView *)tableView_ viewForHeaderInSection:(NSInteger)section {
    if (section == 0 && !tableView.editing) {
        UILabel* titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(17.0, 5.0, 280.0, 35.0)];
        titleLabel.text = NSLocalizedStringFromTable(@"DepartureStation", @"TransportPlugin", nil);
        titleLabel.textColor = [UIColor colorWithWhite:1.0 alpha:0.9];
        titleLabel.backgroundColor = [UIColor clearColor];
        titleLabel.font = [UIFont boldSystemFontOfSize:17.0];
        titleLabel.shadowColor = [UIColor blackColor];
        titleLabel.shadowOffset = CGSizeMake(0.0, -1.0);
        UIView* containerView = [[UIView alloc] init];
        [containerView addSubview:titleLabel]; //need container view because frame of returned view is not taken into account (=> width is tableview width, height is one returned by heightForHeaderInSection)
        [titleLabel release];
        return [containerView autorelease];
    }
    return nil;
}

- (UIView*)tableView:(UITableView *)tableView_ viewForFooterInSection:(NSInteger)section {
    if (section == 1 && !tableView.editing) {
        NSString* explanations1;
                
        if (selectedStation) {
            explanations1 = [NSString stringWithFormat:NSLocalizedStringFromTable(@"FavoriteStationsExplanations_ManualWithFormat", @"TransportPlugin", nil), [TransportUtils nicerName:selectedStation.name]];
        } else {
            explanations1 = NSLocalizedStringFromTable(@"FavoriteStationsExplanations_NearestSelected", @"TransportPlugin", nil);
        }
        UIFont* font1 = [UIFont systemFontOfSize:15.0];
        CGSize reqSize1 = [explanations1 sizeWithFont:font1 constrainedToSize:CGSizeMake(280.0, 600.0)];
        UILabel* label1 = [[UILabel alloc] initWithFrame:CGRectMake(20.0, 10.0, 280.0, reqSize1.height)];
        label1.numberOfLines = 0;
        label1.textAlignment = UITextAlignmentCenter;
        label1.textColor = [UIColor colorWithWhite:1.0 alpha:0.85];
        label1.backgroundColor = [UIColor clearColor];
        label1.font = font1;
        label1.text = explanations1;
        
        /*NSString* explanations2 = NSLocalizedStringFromTable(@"FavoriteStationsManagementExplanations", @"TransportPlugin", nil);
        UIFont* font2 = [UIFont systemFontOfSize:15.0];
        CGSize reqSize2 = [explanations2 sizeWithFont:font2 constrainedToSize:CGSizeMake(290.0, 600.0)];
        UILabel* label2 = [[UILabel alloc] initWithFrame:CGRectMake(15.0, label1.frame.origin.y + reqSize1.height + 16.0, reqSize2.width, reqSize2.height)];
        label2.numberOfLines = 0;
        label2.textColor = [UIColor colorWithWhite:1.0 alpha:0.85];
        label2.backgroundColor = [UIColor clearColor];
        label2.font = font2;
        label2.text = explanations2;*/
        
        UIView* containerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320.0, label1.frame.origin.y+label1.frame.size.height+20.0)]; //gives some margin
        //containerView.backgroundColor = [UIColor blueColor];
        [containerView addSubview:label1];
        //[containerView addSubview:label2];
        [label1 release];
        //[label2 release];
        return [containerView autorelease];
        
    }
    return nil;
}

- (NSInteger)tableView:(UITableView *)tableView_ numberOfRowsInSection:(NSInteger)section {
    [self enableAndDisableAddButtonIfNecessary];
    if (favStations == nil || favStations.count == 0) {
        touchAddInstructionsLabel.hidden = NO;
        [self setNavBarNoStationMode:NO];
        return 0;
    }
    touchAddInstructionsLabel.hidden = YES;
    if (section == 0) {
        if (tableView.editing) {
            return 0;
        }
        return 1;
    } else if (section == 1) {
        return favStations.count;
    } else {
        return 0; //should not happen
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (favStations == nil || favStations.count == 0) {
        touchAddInstructionsLabel.hidden = NO;
        [self setNavBarNoStationMode:NO];
        return 0;
    }
    return 2;
}

- (void)enableAndDisableAddButtonIfNecessary {
    if ([favStations count] == MAX_NB_FAV_STATIONS) {
        addButton.enabled = NO;
    } else {
        addButton.enabled = YES;
    }
}

- (void)dealloc
{
    [transportService release];
    [favStations release];
    [selectedStation release];
    [super dealloc];
}

@end
