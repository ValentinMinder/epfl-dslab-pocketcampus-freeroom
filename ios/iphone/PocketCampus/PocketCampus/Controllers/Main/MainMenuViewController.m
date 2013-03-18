//
//  MainMenuViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MainMenuViewController.h"

#import "MainController.h"

#import "MainMenuItem.h"

#import "MainMenuItemCell.h"

#import "PCValues.h"

#import "PCUtils.h"

#import "GANTracker.h"

#import <QuartzCore/QuartzCore.h>

static NSString* kMenuItemButtonIdentifier = @"MenuItemButton";
static NSString* kMenuItemThinSeparatorIdentifier = @"MenuItemSeparator";

static const int kPluginsSection = 0;

@interface MainMenuViewController ()

@property (nonatomic, weak) MainController* mainController;
@property (nonatomic, copy) NSArray* sections; //array of MenuItem* of type section
@property (nonatomic, copy) NSMutableArray* rowsForSection; //index:section, value:array of MenuItems
@property (nonatomic, readwrite, strong) NSMutableArray* pluginsMenuItems;
@property (nonatomic, strong) NSMutableDictionary* cells; //key: NSIndexPath, value:corresponding MainMenuItemCell

@property (nonatomic, strong) UIBarButtonItem* settingsButton;
@property (nonatomic, strong) UIBarButtonItem* doneButton;
@property (nonatomic, strong) UIBarButtonItem* pocketCampusTitle;

@end

@implementation MainMenuViewController

- (id)initWithMenuItems:(NSArray*)menuItems mainController:(MainController*)mainController;
{
    self = [super initWithNibName:@"MainMenuView" bundle:nil];
    if (self) {
        // Custom initialization
        self.mainController = mainController;
        [self fillCollectionsWithMenuItems:menuItems];
        self.cells = [NSMutableDictionary dictionary];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/dashboard" withError:NULL];
    UIView* backgroundView = [[UIView alloc] initWithFrame:self.tableView.frame];
    backgroundView.backgroundColor = [PCValues backgroundColor1];
    backgroundView.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin
    | UIViewAutoresizingFlexibleWidth
    | UIViewAutoresizingFlexibleRightMargin
    | UIViewAutoresizingFlexibleTopMargin
    | UIViewAutoresizingFlexibleHeight
    | UIViewAutoresizingFlexibleBottomMargin;
    self.tableView.backgroundView = backgroundView;
    self.tableView.allowsMultipleSelectionDuringEditing = NO;
    self.view.backgroundColor = [PCValues backgroundColor1];
    self.navigationController.view.layer.cornerRadius = [PCValues defaultCornerRadius];
    self.navigationController.view.layer.masksToBounds = YES;
    self.tableView.scrollsToTop = NO; //if not set to NO, front view controllers cannot be scrolled to top by tapping the status bar
    self.navigationItem.leftBarButtonItems = [NSArray arrayWithObjects:self.settingsButton, self.pocketCampusTitle, nil];
    [self.mainController mainMenuIsReady];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return [self.parentViewController supportedInterfaceOrientations];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

#pragma mark - Public

- (void)reloadWithMenuItems:(NSArray*)menuItems {
    [self fillCollectionsWithMenuItems:menuItems];
    self.cells = [NSMutableDictionary dictionary];
    [PCUtils reloadTableView:self.tableView withFadingDuration:0.5];
}

- (void)setEditing:(BOOL)editing {
    if (self.tableView.editing == editing) {
        return;
    }
    if (editing) {
        self.tableView.editing = YES;
        [self.navigationItem setLeftBarButtonItems:[NSArray arrayWithObjects:self.doneButton, self.pocketCampusTitle, nil] animated:NO];
    } else {
        self.tableView.editing = NO;
        [self.navigationItem setLeftBarButtonItems:[NSArray arrayWithObjects:self.settingsButton, self.pocketCampusTitle, nil] animated:NO];
    }
    [PCUtils reloadTableView:self.tableView withFadingDuration:0.5];
    
    /* 
     * Must be done in two separate steps because mainController need to see
     * reloaded tableview, but tableview must be reloaded with toggled editing state.
     * Cannot be called directly because table view reload is not done otherwise
     */
    [NSTimer scheduledTimerWithTimeInterval:0.0 target:self selector:@selector(callMainController) userInfo:nil repeats:NO];
}

- (void)callMainController {
    if (self.tableView.editing) {
        [self.mainController mainMenuStartedEditing];
    } else {
        [self.mainController mainMenuEndedEditing];
    }
}

- (void)setSelectedPluginWithIdentifier:(NSString*)pluginIdentifier animated:(BOOL)animated {
    if (![self isViewLoaded]) {
        return;
    }
    if (!pluginIdentifier) {
        [self.tableView deselectRowAtIndexPath:[self.tableView indexPathsForSelectedRows][0] animated:animated];
        return;
    }
    [self.cells enumerateKeysAndObjectsUsingBlock:^(NSIndexPath* indexPath, MainMenuItemCell* cell, BOOL *stop) {
        MainMenuItem* menuItem = self.rowsForSection[indexPath.section][indexPath.row];
        if ([menuItem.identifier isEqualToString:pluginIdentifier]) {
            [self.tableView selectRowAtIndexPath:indexPath animated:animated scrollPosition:UITableViewScrollPositionNone];
        }
    }];
}

#pragma mark - Utilities

- (void)fillCollectionsWithMenuItems:(NSArray*)menuItems {
    if (!menuItems || [menuItems count] == 0) {
        return;
    }
    NSUInteger sectionIndex = -1;
    NSMutableArray* sections = [NSMutableArray array];
    NSMutableArray* rowsForSection = [NSMutableArray array];
    for (MainMenuItem* item in menuItems) {
        if (![item isKindOfClass:[MainMenuItem class]]) {
            @throw [NSException exceptionWithName:@"Array element exception" reason:@"found an element of type different from MainMenuItem in menuItems" userInfo:nil];
        }
        if (item.type == MainMenuItemTypeSectionHeader) {
            sectionIndex++;
            [sections addObject:item];
            [rowsForSection insertObject:[NSMutableArray array] atIndex:sectionIndex]; //preparing for rows
        } else {
            if (sectionIndex == -1) {
                @throw [NSException exceptionWithName:@"Bad menuItems structure" reason:@"structure must begin with at least one item with type MainMenuItemSectionHead must be present (set hidden YES to hide it)" userInfo:nil];
            }
            NSMutableArray* rows = rowsForSection[sectionIndex];
            [rows addObject:item];
        }
    }
    
    self.sections = sections;
    self.rowsForSection = rowsForSection;
    self.pluginsMenuItems = [rowsForSection[kPluginsSection] mutableCopy];
}

#pragma mark - Buttons

- (UIBarButtonItem*)pocketCampusTitle {
    if (_pocketCampusTitle) {
        return _pocketCampusTitle;
    }
    UILabel* pocketCampusLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 188.0, 40.0)];
    pocketCampusLabel.backgroundColor = [UIColor clearColor];
#ifdef DEBUG
    pocketCampusLabel.text = @"PocketCampus X";
#else
    pocketCampusLabel.text = @"PocketCampus";
#endif
    pocketCampusLabel.textAlignment = UITextAlignmentRight;
    pocketCampusLabel.textColor = [UIColor whiteColor];
    pocketCampusLabel.font = [UIFont boldSystemFontOfSize:21.0];
    pocketCampusLabel.shadowColor = [UIColor colorWithWhite:0.2 alpha:1.0];
    pocketCampusLabel.shadowOffset = CGSizeMake(0.0, -1.0);
    UIBarButtonItem* pocketCampusTitle = [[UIBarButtonItem alloc] initWithCustomView:pocketCampusLabel];
    _pocketCampusTitle = pocketCampusTitle;
    return _pocketCampusTitle;
}

- (UIBarButtonItem*)settingsButton {
    if (_settingsButton) {
        return _settingsButton;
    }
    UIButton* button = [[UIButton alloc] initWithFrame:CGRectMake(0.0, 0.0, 42.0, 42.0)];
    [button setImage:[UIImage imageNamed:@"SettingsNavBarButton"] forState:UIControlStateNormal];
    button.adjustsImageWhenHighlighted = NO;
    button.showsTouchWhenHighlighted = YES;
    [button addTarget:self action:@selector(settingsButtonPressed) forControlEvents:UIControlEventTouchUpInside];
    _settingsButton = [[UIBarButtonItem alloc] initWithCustomView:button];
    return _settingsButton;
}

- (UIBarButtonItem*)doneButton {
    if (_doneButton) {
        return _doneButton;
    }
    _doneButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneButtonPressed)];;
    return _doneButton;
}

#pragma mark - Actions

- (void)settingsButtonPressed {
    [self.mainController showGlobalSettings];
}

- (void)doneButtonPressed {
    [self setEditing:NO];
}

#pragma mark EyeButtonDelegate (MainMenuItemCell)

- (void)eyeButtonPressedForMenuItemCell:(MainMenuItemCell *)cell {
    /* cell.menuItem is same as menuItem is self collections (strong pointer) */
    if (cell.menuItem.hidden) {
        cell.menuItem.hidden = NO;
        [cell setEyeButtonState:EyeButtonStateDataVisible];
    } else {
        cell.menuItem.hidden = YES;
        [cell setEyeButtonState:EyeButtonStateDataHidden];
    }
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    MainMenuItem* item = self.rowsForSection[indexPath.section][indexPath.row];
    if (item.type == MainMenuItemTypeButton) {
        [self.mainController setActivePluginWithIdentifier:item.identifier];
    }
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    return  UITableViewCellEditingStyleNone;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    MainMenuItem* item = self.rowsForSection[indexPath.section][indexPath.row];
    if (!self.tableView.editing && item.hidden) {
        return 0.0;
    } else {
        return [MainMenuItemCell heightForMainMenuItemType:item.type];
    }
}

#pragma mark - UITableViewDataSource

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return [self tableView:tableView viewForHeaderInSection:section].frame.size.height;
}

- (UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    //MainMenuItemTypeSectionHeader not supported yet
    return nil;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    MainMenuItem* menuItem = self.rowsForSection[indexPath.section][indexPath.row];
    MainMenuItemCell* cell = [self.cells objectForKey:indexPath];
    if (!cell) {
        if (menuItem.type == MainMenuItemTypeThinSeparator) {
            cell = [MainMenuItemCell cellWithMainMenuItem:menuItem reuseIdentifier:kMenuItemThinSeparatorIdentifier];
        } else if (menuItem.type == MainMenuItemTypeButton) {
            cell = [MainMenuItemCell cellWithMainMenuItem:menuItem reuseIdentifier:kMenuItemButtonIdentifier];
            cell.titleLabel.text = menuItem.title;
            cell.leftImageView.image = menuItem.leftImage;
        } else {
            //No other supported types
            NSLog(@"-> ERROR: Unsupported menu item type (%d)", menuItem.type);
        }
        [self.cells setObject:cell forKey:indexPath];
    }
    cell.eyeButtonDelegate = self;
    if (menuItem.hidden) {
        [cell setEyeButtonState:EyeButtonStateDataHidden];
    } else {
        [cell setEyeButtonState:EyeButtonStateDataVisible];
    }
    if (!self.tableView.editing) {
        cell.hidden = menuItem.hidden;
    }
    if (cell.hidden && self.tableView.editing) {
        cell.hidden = NO;
    }
    return cell;
}

- (NSIndexPath*)tableView:(UITableView *)tableView targetIndexPathForMoveFromRowAtIndexPath:(NSIndexPath *)sourceIndexPath toProposedIndexPath:(NSIndexPath *)proposedDestinationIndexPath {
    if (proposedDestinationIndexPath.section > kPluginsSection) {
        return [NSIndexPath indexPathForRow:[self.rowsForSection[kPluginsSection] count]-1 inSection:kPluginsSection];
    } else if (proposedDestinationIndexPath.section < kPluginsSection) {
        return [NSIndexPath indexPathForRow:0 inSection:kPluginsSection];
    } else { //plugins section
        return proposedDestinationIndexPath;
    }
}

- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)sourceIndexPath toIndexPath:(NSIndexPath *)destinationIndexPath {
    if (destinationIndexPath.section != kPluginsSection) {
        return; //should not happen, by tableView:targetIndexPathForMoveFromRowAtIndexPath:toProposedIndexPath:
    }
    
    MainMenuItem* pluginMenuItem = self.rowsForSection[kPluginsSection][sourceIndexPath.row];
    
    [self.rowsForSection[kPluginsSection] removeObjectAtIndex:sourceIndexPath.row];
    [self.rowsForSection[kPluginsSection] insertObject:pluginMenuItem atIndex:destinationIndexPath.row];
    [self.pluginsMenuItems removeObjectAtIndex:sourceIndexPath.row];
    [self.pluginsMenuItems insertObject:pluginMenuItem atIndex:destinationIndexPath.row];
    [self.cells removeAllObjects]; //need to refill collection
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [self.rowsForSection[section] count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return [self.sections count];
}

@end
