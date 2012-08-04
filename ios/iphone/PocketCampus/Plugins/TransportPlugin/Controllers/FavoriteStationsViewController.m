//
//  FavoriteStationsViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "FavoriteStationsViewController.h"

#import "AddStationViewController.h"

#import "PCValues.h"

#import "TransportUtils.h"

static NSString* kTransportStationNameCellIdentifier = @"StationNameCell";

@implementation FavoriteStationsViewController

@synthesize tableView, touchAddInstructionsLabel;

@synthesize dev_location_test; //DEV, to remove

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
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
    tableView.editing = NO;
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
    UIBarButtonItem* addButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addButtonPressed)];
    [self.navigationItem setLeftBarButtonItem:addButton animated:animated];
    [addButton release];
    
    UIBarButtonItem* doneButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneButtonPressed)];
    [self.navigationItem setRightBarButtonItem:doneButton animated:animated];
    [doneButton release];
    
}

- (void)setNavBarEditingModeAnimated:(BOOL)animated  {
    UIBarButtonItem* saveButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(saveButtonPressed)];
    [self.navigationItem setLeftBarButtonItem:saveButton animated:animated];
    [saveButton release];
    
    UIBarButtonItem* addButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addButtonPressed)];
    [self.navigationItem setRightBarButtonItem:addButton animated:animated];
    [addButton release];

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
}

- (void)addButtonPressed {
    tableView.editing = YES;
    [self setNavBarEditingModeAnimated:YES];
    AddStationViewController* viewController = [[AddStationViewController alloc] initWithNibName:@"AddStationView" bundle:nil];
    if([self respondsToSelector:@selector(presentingViewController)]) {
        [self presentViewController:viewController animated:YES completion:NULL]; //only available in iOS 5.0
    } else {
        [self presentModalViewController:viewController animated:YES];
    }
    [viewController release];
}

- (void)editButtonPressed {
    [self setNavBarEditingModeAnimated:YES];
    [tableView setEditing:YES animated:YES];
}

- (void)doneButtonPressed {
    [transportService saveUserFavoriteTransportStations:favStations];
    if ([self.presentingViewController respondsToSelector:@selector(dismissViewControllerAnimated:completion:)]) { // >= iOS 5.0
        [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
    } else {
        [self.presentingViewController dismissModalViewControllerAnimated:YES];
    }
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

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
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
    [[tableView cellForRowAtIndexPath:[self indexPathOfSelectedCell]] setAccessoryType:UITableViewCellAccessoryNone];
    [[tableView cellForRowAtIndexPath:indexPath] setAccessoryType:UITableViewCellAccessoryCheckmark];
    [selectedStation release];
    selectedStation = [[favStations objectAtIndex:indexPath.row] retain];
    
    if (indexPath.section == 0) { //user has selected automatic nearest station
        [transportService saveUserManualDepartureStation:nil];
        [selectedStation release];
        selectedStation = nil;
    } else if (indexPath.section == 1) {
        TransportStation* station = [favStations objectAtIndex:indexPath.row];
        [transportService saveUserManualDepartureStation:station];
        [selectedStation release];
        selectedStation = [station retain];
    } else {
        //should not happen
    }
    
}


/* UITableViewDataSource delegation */

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        UITableViewCell* newCell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
        newCell.textLabel.textColor = [PCValues textColorLocationBlue];
        newCell.textLabel.text = NSLocalizedStringFromTable(@"Automatic", @"TransportPlugin", nil);
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
            [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationLeft];
            if ([indexPath isEqual:selectedIndexPath]) {
                [selectedStation release];
                selectedStation = nil;
                [transportService saveUserManualDepartureStation:nil];
                [tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationNone];
            }
        }
        [transportService saveUserFavoriteTransportStations:favStations];
    }
}

- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 1) {
        return YES;
    }
    return NO;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        NSString* explanation = NSLocalizedStringFromTable(@"FavoriteStationsExplanations", @"TransportPlugin", nil);
        UIFont* font = [UIFont systemFontOfSize:16.0];
        CGSize reqSize = [explanation sizeWithFont:font constrainedToSize:CGSizeMake(260.0, 600.0)];
        return reqSize.height+20.0;
    }
    if (section == 1) {
        return 40.0;
    }
    return 0.0;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    if(section == 0) {
        UILabel* label = [[UILabel alloc] init];
        NSString* explanation = NSLocalizedStringFromTable(@"FavoriteStationsExplanations", @"TransportPlugin", nil);
        UIFont* font = [UIFont systemFontOfSize:16.0];
        CGSize reqSize = [explanation sizeWithFont:font constrainedToSize:CGSizeMake(260.0, 600.0)];
        
        label.frame = CGRectMake(0, 0, 260.0, reqSize.height+20.0);
        label.numberOfLines = 5;
        label.textAlignment = UITextAlignmentCenter;
        label.textColor = [UIColor colorWithWhite:1.0 alpha:0.85];
        label.backgroundColor = [UIColor clearColor];
        label.font = font;
        label.adjustsFontSizeToFitWidth = NO;
        label.text = explanation;
        return [label autorelease];
    }
    
    if (section == 1) {
        UILabel* label = [[UILabel alloc] init];
        label.text = NSLocalizedStringFromTable(@"ManualDepartureSelection", @"TransportPlugin", nil);
        label.textAlignment = UITextAlignmentCenter;
        label.textColor = [UIColor colorWithWhite:1.0 alpha:0.85];
        label.backgroundColor = [UIColor clearColor];
        label.font = [UIFont systemFontOfSize:16.0];;
        label.adjustsFontSizeToFitWidth = NO;
        return [label autorelease];
    }
    
    return nil;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (favStations == nil || favStations.count == 0) {
        touchAddInstructionsLabel.hidden = NO;
        [self setNavBarNoStationMode:NO];
        return 0;
    }
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (favStations == nil || favStations.count == 0) {
        touchAddInstructionsLabel.hidden = NO;
        [self setNavBarNoStationMode:NO];
        return 0;
    }
    touchAddInstructionsLabel.hidden = YES;
    if (section == 0) {
        return 1;
    } else if (section == 1) {
        return favStations.count;
    } else {
        return 0; //should not happen
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
