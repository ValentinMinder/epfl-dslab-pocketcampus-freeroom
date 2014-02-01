/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */




//  Created by Loïc Gardiol on 07.10.12.


#import "MainMenuViewController.h"

#import "MainController.h"

#import "MainMenuItem.h"

#import "MainMenuItemCell.h"

#import "PCValues.h"

#import "PCUtils.h"

#import <QuartzCore/QuartzCore.h>

static NSString* const kMenuItemButtonIdentifier = @"MenuItemButton";
static NSString* const kMenuItemThinSeparatorIdentifier = @"MenuItemSeparator";

static const int kPluginsSection = 0;

@interface MainMenuViewController ()

@property (nonatomic, weak) MainController* mainController;
@property (nonatomic, strong) NSMutableArray* menuItems;
@property (nonatomic, strong) NSMutableDictionary* cellForIndexPath; //key: NSIndexPath, value:corresponding MainMenuItemCell

@property (nonatomic, strong) UIBarButtonItem* settingsButton;
@property (nonatomic, strong) UIBarButtonItem* doneButton;

@end

@implementation MainMenuViewController

#pragma mark - Init

- (id)initWithMenuItems:(NSArray*)menuItems mainController:(MainController*)mainController;
{
    self = [super initWithNibName:@"MainMenuView" bundle:nil];
    if (self) {
        self.gaiScreenName = @"/dashboard";
        self.mainController = mainController;
        [self fillCollectionsWithMenuItems:menuItems];
        self.cellForIndexPath = [NSMutableDictionary dictionary];
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self trackScreen];
    self.navigationController.navigationBar.translucent = NO;
    self.tableView.allowsMultipleSelectionDuringEditing = NO;
    self.tableView.scrollsToTop = NO; //if not set to NO, front view controllers cannot be scrolled to top by tapping the status bar
    
    //self.navigationItem.leftBarButtonItems = [NSArray arrayWithObjects:self.settingsButton, self.pocketCampusTitle, nil];
    self.navigationItem.leftBarButtonItem = self.settingsButton;
    self.navigationItem.titleView = self.pocketCampusLabel;
    CGRect frame = self.navigationController.view.frame;
    frame.size.width = 320.0;
    self.navigationController.view.frame = frame;
    self.navigationController.view.autoresizingMask = self.navigationController.view.autoresizingMask & ~UIViewAutoresizingFlexibleWidth; //remove flexible width from mask (we want constant 320.0 width)
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
    if (editing) {
        UIBarButtonItem* space = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace target:nil action:NULL];
        space.width = [PCUtils isIdiomPad] ? 6.0 : 13.0;
        [self.navigationItem setRightBarButtonItems:@[self.doneButton] animated:YES];
        [self.navigationItem setLeftBarButtonItems:nil animated:YES];
    } else {
        [self.navigationItem setRightBarButtonItems:nil animated:YES];
        [self.navigationItem setLeftBarButtonItems:@[self.settingsButton] animated:YES];
    }
    
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
    _settingsButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"SettingsBarButton"] style:UIBarButtonItemStyleBordered target:self action:@selector(settingsButtonPressed)];
    _settingsButton.accessibilityLabel = NSLocalizedStringFromTable(@"Settings", @"PocketCampus", nil);
    return _settingsButton;
}

- (UIBarButtonItem*)doneButton {
    if (_doneButton) {
        return _doneButton;
    }
    _doneButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Done", @"PocketCampus", nil) style:UIBarButtonItemStylePlain target:self action:@selector(doneButtonPressed)];
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
    [self trackAction:@"OpenPlugin"];
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
    MainMenuItemCell* cell = self.cellForIndexPath[indexPath];
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
