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

static NSString* kMenuItemButtonIdentifier = @"MenuItemButton";
static NSString* kMenuItemThinSeparatorIdentifier = @"MenuItemSeparator";

@interface MainMenuViewController ()

@property (nonatomic, strong) NSArray* menuItems; //array of MainMenuItem
@property (nonatomic, weak) MainController* mainController;
@property (nonatomic, copy) NSArray* sections;
@property (nonatomic, copy) NSArray* rowsForSection;
@property (nonatomic, strong) NSMutableDictionary* cells; //key: NSIndexPath, value:corresponding MainMenuItemCell

@end

@implementation MainMenuViewController

- (id)initWithMenuItems:(NSArray*)menuItems mainController:(MainController*)mainController;
{
    self = [super initWithNibName:@"MainMenuView" bundle:nil];
    if (self) {
        // Custom initialization
        self.menuItems = menuItems;
        self.mainController = mainController;
        [self fillCollections];
        self.cells = [NSMutableDictionary dictionary];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    UIView* backgroundView = [[UIView alloc] initWithFrame:self.tableView.frame];
    backgroundView.backgroundColor = [PCValues backgroundColor1];
    backgroundView.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin
    | UIViewAutoresizingFlexibleWidth
    | UIViewAutoresizingFlexibleRightMargin
    | UIViewAutoresizingFlexibleTopMargin
    | UIViewAutoresizingFlexibleHeight
    | UIViewAutoresizingFlexibleBottomMargin;
    self.tableView.backgroundView = backgroundView;
    [self.mainController mainMenuIsReady];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)fillCollections {
    if (!self.menuItems || [self.menuItems count] == 0) {
        return;
    }
    NSUInteger sectionIndex = -1;
    NSMutableArray* sections = [NSMutableArray array];
    NSMutableArray* rowsForSection = [NSMutableArray array];
    for (MainMenuItem* item in self.menuItems) {
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

#pragma mark - Actions

- (IBAction)settingsButtonPressed {
    [self.mainController showGlobalSettings];
}

#pragma mark UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    MainMenuItem* item = self.rowsForSection[indexPath.section][indexPath.row];
    if (item.type == MainMenuItemTypeButton) {
        [self.mainController setActivePluginWithIdentifier:item.identifier];
    }
}


#pragma mark UITableViewDataSource

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    MainMenuItem* item = self.rowsForSection[indexPath.section][indexPath.row];
    return [MainMenuItemCell heightForMainMenuItemType:item.type];
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return [self tableView:tableView viewForHeaderInSection:section].frame.size.height;
}

- (UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    //TODO
    return nil;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    MainMenuItem* menuItem = self.rowsForSection[indexPath.section][indexPath.row];
    MainMenuItemCell* cell = [self.cells objectForKey:indexPath];
    if (!cell) {
        if (menuItem.type == MainMenuItemTypeThinSeparator) {
            cell = [MainMenuItemCell cellWithMainMenuItemType:MainMenuItemTypeThinSeparator reuseIdentifier:kMenuItemThinSeparatorIdentifier];
        } else if (menuItem.type == MainMenuItemTypeButton) {
            cell = [MainMenuItemCell cellWithMainMenuItemType:MainMenuItemTypeButton reuseIdentifier:kMenuItemButtonIdentifier];
            cell.titleLabel.text = menuItem.title;
            cell.leftImageView.image = menuItem.leftImage;
        } else {
            //No other supported types
            NSLog(@"-> ERROR: Unsupported menu item type (%d)", menuItem.type);
        }
    }
    [self.cells setObject:cell forKey:indexPath];
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [self.rowsForSection[section] count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return [self.sections count];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return [self.parentViewController supportedInterfaceOrientations];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return [self.parentViewController shouldAutorotateToInterfaceOrientation:interfaceOrientation];
}


/*- (MainMenuItem*)menuItemWithType:(MainMenuItemType)type position:(NSUInteger)position {
    MainMenuItem* __block item = nil;
    NSUInteger __block pos = 0;
    [self.menuItems enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        if ([obj isKindOfClass:[MainMenuItem class]]) {
            MainMenuItem* obj = (MainMenuItem*)obj;
            if (obj.type == type && pos == position) {
                if (pos == position) {
                    item = obj;
                    *stop = YES;
                }
                pos++;
            }
        }
    }];
    return item;
}

- (MainMenuItem*)menuItemWithIndexPath:(NSIndexPath*)indexPath {
    MainMenuItem* sectionItem = [self menuItemWithType:MainMenuItemTypeSectionHeader position:indexPath.section];
    if (!sectionItem) {
        return nil;
    }
    NSUInteger sectionIndex = [self.menuItems indexOfObject:sectionItem];
    NSUInteger itemIndex = sectionIndex+indexPath.row+1;
    if ([self.menuItems count] >= itemIndex) {
        return nil;
    }
    return self.menuItems[itemIndex];
}

- (NSUInteger)numberOfRowsInSection:(NSUInteger)sectionIndex {
    if (![self menuItemWithType:MainMenuItemTypeSectionHeader position:sectionIndex]) { //means section does not exist
        return 0;
    }
    NSUInteger nbRows = 0;
    for (NSUInteger rowIndex = sectionIndex+1; section < [self.menuItems count]; section++) {
        MainMenuItem* item = self.menuItems[rowIndex];
        if ()
    }
}*/

@end
