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

#import <QuartzCore/QuartzCore.h>

static NSString* kMenuItemButtonIdentifier = @"MenuItemButton";
static NSString* kMenuItemThinSeparatorIdentifier = @"MenuItemSeparator";

static const int kPluginsSection = 0;

@interface MainMenuViewController ()

@property (nonatomic, weak) MainController* mainController;
@property (nonatomic, strong) NSMutableArray* menuItems;
@property (nonatomic, strong) NSMutableDictionary* cellForIndexPath; //key: NSIndexPath, value:corresponding MainMenuItemCell

@property (nonatomic, strong) UIBarButtonItem* settingsButton;
@property (nonatomic, strong) UIBarButtonItem* doneButton;

@end

@implementation MainMenuViewController

- (id)initWithMenuItems:(NSArray*)menuItems mainController:(MainController*)mainController;
{
    self = [super initWithNibName:@"MainMenuView" bundle:nil];
    if (self) {
        // Custom initialization
        self.mainController = mainController;
        [self fillCollectionsWithMenuItems:menuItems];
        self.cellForIndexPath = [NSMutableDictionary dictionary];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[PCGAITracker sharedTracker] trackScreenWithName:@"/dashboard"];
    self.navigationController.navigationBar.translucent = NO;
    self.tableView.allowsMultipleSelectionDuringEditing = NO;
    self.tableView.scrollsToTop = NO; //if not set to NO, front view controllers cannot be scrolled to top by tapping the status bar
    
    //self.navigationItem.leftBarButtonItems = [NSArray arrayWithObjects:self.settingsButton, self.pocketCampusTitle, nil];
    self.navigationItem.leftBarButtonItem = self.settingsButton;
    self.navigationItem.titleView = self.pocketCampusLabel;
    [self.mainController mainMenuIsReady];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSUInteger)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Public

- (void)reloadWithMenuItems:(NSArray*)menuItems {
    [self fillCollectionsWithMenuItems:menuItems];
    self.cellForIndexPath = [NSMutableDictionary dictionary];
    [PCUtils reloadTableView:self.tableView withFadingDuration:0.5];
}

- (void)setEditing:(BOOL)editing {
    if (self.tableView.editing == editing) {
        return;
    }
    self.tableView.editing = editing;
    NSArray* items = nil;
    if (editing) {
        UIBarButtonItem* space = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace target:nil action:NULL];
        space.width = 13.0;
        items = @[space, self.doneButton];
    } else {
        items = @[self.settingsButton];
    }
    [self.navigationItem setLeftBarButtonItems:items animated:YES];
    self.navigationItem.titleView = [self pocketCampusLabel];
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

- (NSArray*)pluginsMenuItems {
    return [self.menuItems copy];
}

- (void)setSelectedPluginWithIdentifier:(NSString*)pluginIdentifier animated:(BOOL)animated {
    if (![self isViewLoaded]) {
        return;
    }
    if (!pluginIdentifier) {
        [self.tableView deselectRowAtIndexPath:[self.tableView indexPathsForSelectedRows][0] animated:animated];
        return;
    }
    [self.cellForIndexPath enumerateKeysAndObjectsUsingBlock:^(NSIndexPath* indexPath, MainMenuItemCell* cell, BOOL *stop) {
        if ([cell.menuItem.identifier isEqualToString:pluginIdentifier]) {
            [self.tableView selectRowAtIndexPath:indexPath animated:animated scrollPosition:UITableViewScrollPositionNone];
        }
    }];
}

#pragma mark - Utilities

- (void)fillCollectionsWithMenuItems:(NSArray*)menuItems {
    if (!menuItems || [menuItems count] == 0) {
        return;
    }
    self.menuItems = [menuItems mutableCopy];
}

#pragma mark - Buttons

- (UILabel*)pocketCampusLabel {
    UILabel* label = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 120.0, 40.0)];
    label.backgroundColor = [UIColor clearColor];
#ifdef DEBUG
    label.text = @"PocketCampus";
#else
    label.text = @"PocketCampus";
#endif
    label.textAlignment = NSTextAlignmentCenter;
    label.textColor = self.tableView.editing ? [PCValues textColor1] : [PCValues pocketCampusRed];
    label.font = [UIFont systemFontOfSize:23.0];
    [label sizeToFit];
    return label;
}

- (UIBarButtonItem*)settingsButton {
    if (_settingsButton) {
        return _settingsButton;
    }
    _settingsButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"SettingsNavBarButton"] style:UIBarButtonItemStyleBordered target:self action:@selector(settingsButtonPressed)];
    return _settingsButton;
}

- (UIBarButtonItem*)doneButton {
    if (_doneButton) {
        return _doneButton;
    }
    _doneButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneButtonPressed)];
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

- (BOOL)tableView:(UITableView *)tableView shouldIndentWhileEditingRowAtIndexPath:(NSIndexPath *)indexPath {
    return NO;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    MainMenuItem* item = self.menuItems[indexPath.row];
    [self.mainController setActivePluginWithIdentifier:item.identifier];
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    return  UITableViewCellEditingStyleNone;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    MainMenuItem* item = self.menuItems[indexPath.row];
    if (!self.tableView.editing && item.hidden) {
        return 0.0;
    } else {
        return [MainMenuItemCell height];
    }
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    MainMenuItem* menuItem = self.menuItems[indexPath.row];
    MainMenuItemCell* cell = [self.cellForIndexPath objectForKey:indexPath];
    if (!cell) {
        cell = [MainMenuItemCell cellWithMainMenuItem:menuItem reuseIdentifier:kMenuItemButtonIdentifier];
        self.cellForIndexPath[indexPath] = cell;
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
        return [NSIndexPath indexPathForRow:self.menuItems.count-1 inSection:kPluginsSection];
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
    MainMenuItem* pluginMenuItem = self.menuItems[sourceIndexPath.row];
    [self.menuItems removeObjectAtIndex:sourceIndexPath.row];
    [self.menuItems insertObject:pluginMenuItem atIndex:destinationIndexPath.row];
    [self.cellForIndexPath removeAllObjects]; //need to refill collection
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.menuItems.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

@end
