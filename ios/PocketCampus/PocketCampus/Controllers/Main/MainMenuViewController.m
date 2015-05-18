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

@import QuartzCore;

#import "MainController.h"

#import "MainMenuItem.h"

#import "MainMenuItemCell.h"

#import "PCAboutViewController.h"

#import "UIBarButtonItem+LGAAdditions.h"

#import "PCInfoCell.h"

#import "PCWhatsNewViewController.h"

static NSString* const kMenuItemButtonIdentifier = @"MenuItemButton";
static NSString* const kMenuItemThinSeparatorIdentifier = @"MenuItemSeparator";

static CGFloat const kTableViewFooterHeight = 54.0;

static const int kWhatsNewSection = 0;
static const int kPluginsSection = 1;

@interface MainMenuViewController ()

@property (nonatomic, weak) IBOutlet UIImageView* institutionLogoImageView;

@property (nonatomic, weak) MainController* mainController;
@property (nonatomic, strong) NSMutableArray* menuItems;
@property (nonatomic, strong) NSMutableDictionary* cellForIndexPath; //key: NSIndexPath, value:corresponding MainMenuItemCell

@property (nonatomic, strong) UIBarButtonItem* settingsButton;
@property (nonatomic, strong) UIBarButtonItem* doneButton;

@property (readonly, strong) PCInfoCell* whatsNewCell;

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
    self.navigationController.navigationBar.translucent = NO;
    self.tableView.allowsMultipleSelectionDuringEditing = NO;
    self.tableView.scrollsToTop = NO; //if not set to NO, front view controllers cannot be scrolled to top by tapping the status bar
    
    self.navigationItem.leftBarButtonItem = self.settingsButton;
    self.navigationItem.titleView = self.pocketCampusLabel;
    
    UITapGestureRecognizer* tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(institutionLogoTapped)];
    self.institutionLogoImageView.userInteractionEnabled = YES;
    [self.institutionLogoImageView addGestureRecognizer:tapGesture];
    
    self.tableView.tableFooterView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.tableView.frame.size.width, kTableViewFooterHeight)];

    self.navigationController.view.autoresizingMask = self.navigationController.view.autoresizingMask & ~UIViewAutoresizingFlexibleWidth; //remove flexible width from mask, so that when device rotates, main menu keeps its "normal" width
    if ([PCUtils isIdiomPad]) {
        CGRect frame = self.navigationController.view.frame;
        frame.size.width = 320.0;
        self.navigationController.view.frame = frame;
    }
    
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self.tableView flashScrollIndicators];
    });
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    [self adjustInstitutionLogoAlpha];
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

- (void)adjustInstitutionLogoAlpha {
    if (self.tableView.contentOffset.y == 0.0 && self.tableView.contentSize.height < (self.tableView.bounds.size.height - self.tableView.contentInset.top - self.tableView.contentInset.top)) {
        self.institutionLogoImageView.alpha = 1.0;
        return;
    }
    CGFloat offsetMax = self.tableView.contentSize.height + self.tableView.contentInset.bottom - self.tableView.bounds.size.height;
    CGFloat diff = offsetMax - self.tableView.contentOffset.y;
    static CGFloat const kOffsetAlphaStart = kTableViewFooterHeight / 2.0;
    if (diff < 0.0) {
        diff = 0.0;
    }
    if (diff > kOffsetAlphaStart) {
        diff = kOffsetAlphaStart;
    }
    self.institutionLogoImageView.alpha = (kOffsetAlphaStart - diff) / kOffsetAlphaStart;
}

static NSString* kMainMenuWhatsNewCellLastHiddenVersionStringKey = @"MainMenuWhatsNewCellLastHiddenVersionString";

- (BOOL)shouldHideWhatsNewCell {
    NSString* lastHiddenVersion = [[PCPersistenceManager userDefaultsForPluginName:@"pocketcampus"] objectForKey:kMainMenuWhatsNewCellLastHiddenVersionStringKey];
    return [lastHiddenVersion isEqualToString:[PCUtils appVersion]];
}

- (void)hideWhatsNewCell {
    [[PCPersistenceManager userDefaultsForPluginName:@"pocketcampus"] setObject:[PCUtils appVersion] forKey:kMainMenuWhatsNewCellLastHiddenVersionStringKey];
}

#pragma mark - Buttons and cells

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
    if (!_settingsButton) {
        __weak __typeof(self) welf = self;
        _settingsButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"SettingsBarButton"] style:UIBarButtonItemStyleBordered lga_actionBlock:^(UIBarButtonItem* button) {
            [welf.mainController showGlobalSettings];
        }];
        _settingsButton.accessibilityLabel = NSLocalizedStringFromTable(@"Settings", @"PocketCampus", nil);
        
        // Bar items placement differs depending on device. We shift the settings icon left/right
        // so that it is aligned with the plugin icons in the menu
        if ([PCUtils is4inchDevice] || [PCUtils is3_5inchDevice] || [PCUtils is4_7inchDevice]) {
            _settingsButton.imageInsets = UIEdgeInsetsMake(0.0, 1.0, 0.0, -1.0);
        } else if ([PCUtils isIdiomPad] || [PCUtils is5_5inchDevice]) {
            _settingsButton.imageInsets = UIEdgeInsetsMake(0.0, -3.0, 0.0, 3.0);
        }
    }
    return _settingsButton;
}

- (UIBarButtonItem*)doneButton {
    if (!_doneButton) {
        __weak __typeof(self) welf = self;
        _doneButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Done", @"PocketCampus", nil) style:UIBarButtonItemStylePlain lga_actionBlock:^(UIBarButtonItem* button) {
            [welf setEditing:NO];
        }];
    }
    return _doneButton;
}

@synthesize whatsNewCell = _whatsNewCell;

- (PCInfoCell*)whatsNewCell {
    @synchronized (self) {
        if (!_whatsNewCell) {
            NSString* title = [NSString stringWithFormat:@"%@", NSLocalizedStringFromTable(@"WhatsNewInUpdate", @"PocketCampus", nil)];
            NSString* body = NSLocalizedStringFromTable(@"WhatsNewInVersionContentShort", @"PocketCampus", nil);
            NSString* tapForMore = NSLocalizedStringFromTable(@"TapForMoreInfo", @"PocketCampus", nil);
            NSString* fullString = [NSString stringWithFormat:@"%@\n%@\n%@", title, body, tapForMore];
            NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:fullString];
            UIFont* titleFont = [UIFont boldSystemFontOfSize:[UIFont preferredFontForTextStyle:UIFontTextStyleFootnote].pointSize+1.0];
            [attrString addAttribute:NSFontAttributeName value:titleFont range:[fullString rangeOfString:title]];
            NSMutableParagraphStyle* paraStyle = [NSMutableParagraphStyle new];
            paraStyle.paragraphSpacingBefore = 3.0;
            [attrString addAttributes:@{NSFontAttributeName:[UIFont preferredFontForTextStyle:UIFontTextStyleFootnote], NSParagraphStyleAttributeName:paraStyle} range:[fullString rangeOfString:body]];
            [attrString addAttributes:@{NSFontAttributeName: [UIFont preferredFontForTextStyle:UIFontTextStyleFootnote], NSForegroundColorAttributeName: [UIColor darkGrayColor]} range:[fullString rangeOfString:tapForMore]];
            _whatsNewCell = [[PCInfoCell alloc] initWithAttributedString:attrString];
            __weak __typeof(self) welf = self;
            [_whatsNewCell setCloseButtonTapped:^{
                [welf hideWhatsNewCell];
                [welf.tableView reloadSections:[NSIndexSet indexSetWithIndex:kWhatsNewSection] withRowAnimation:UITableViewRowAnimationAutomatic];
            }];
        }
    }
    return _whatsNewCell;
}

#pragma mark - Actions

- (void)institutionLogoTapped {
    PCAboutViewController* aboutViewController = [PCAboutViewController new];
    __weak __typeof(self) welf = self;
    aboutViewController.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone lga_actionBlock:^(UIBarButtonItem* button) {
        [welf dismissViewControllerAnimated:YES completion:NULL];
    }];
    PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:aboutViewController];
    navController.modalPresentationStyle = UIModalPresentationFormSheet;
    [self presentViewController:navController animated:YES completion:NULL];
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

#pragma mark - UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    [self adjustInstitutionLogoAlpha];
}

#pragma mark - UITableViewDelegate

- (BOOL)tableView:(UITableView *)tableView shouldIndentWhileEditingRowAtIndexPath:(NSIndexPath *)indexPath {
    return NO;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case kWhatsNewSection:
        {
            PCWhatsNewViewController* viewController = [PCWhatsNewViewController new];
            __weak __typeof(self) welf = self;
            [viewController setDoneTappedBlock:^{
                [welf dismissViewControllerAnimated:YES completion:NULL];
            }];
            [self.navigationController presentViewController:[[PCNavigationController alloc] initWithRootViewController:viewController] animated:YES completion:^{
                [welf.tableView deselectRowAtIndexPath:indexPath animated:NO];
            }];
            break;
        }
        case kPluginsSection:
        {
            MainMenuItem* item = self.menuItems[indexPath.row];
            NSString* lowercaseIdentifier = [item.identifier lowercaseString];
            [self trackAction:@"OpenPlugin" contentInfo:lowercaseIdentifier];
            [self.mainController setActivePluginWithIdentifier:item.identifier];
            break;
        }
        default:
            break;
    }
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    return  UITableViewCellEditingStyleNone;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case kWhatsNewSection:
            return [self.whatsNewCell preferredHeightInTableView:tableView];
        case kPluginsSection:
        {
            MainMenuItem* item = self.menuItems[indexPath.row];
            if (!self.tableView.editing && item.hidden) {
                return 0.0;
            } else {
                return [MainMenuItemCell height];
            }
        }
    }
    return 0.0;
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case kWhatsNewSection:
        {
            return self.whatsNewCell;
        }
        case kPluginsSection:
        {
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
    }
    return nil;
}

- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
    return indexPath.section == kPluginsSection;
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
    switch (section) {
        case kWhatsNewSection:
            return [self shouldHideWhatsNewCell] ? 0 : 1;
        case kPluginsSection:
            return self.menuItems.count;
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

@end
