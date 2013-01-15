//
//  MainMenuViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MainMenuItemCell.h"

@class MainController;

@interface MainMenuViewController : UIViewController<UITableViewDataSource, UITableViewDelegate, EyeButtonDelegate>

@property (nonatomic, strong) IBOutlet UITableView* tableView;

/*
 * Array of MenuItems of plugins in main menu.
 * Can be accessed by MainController after main menu ends editing
 * to save order of plugins in this array
 * WARNING: array must not be modified
 */
@property (nonatomic, readonly, strong) NSMutableArray* pluginsMenuItems;

@property (nonatomic, weak, readonly) MainController* mainController;

- (id)initWithMenuItems:(NSArray*)menuItems mainController:(MainController*)mainController;
- (void)setSelectedPluginWithIdentifier:(NSString*)pluginIdentifier animated:(BOOL)animated; //pass nil to select no cell

- (void)setEditing:(BOOL)editing;
- (void)reloadWithMenuItems:(NSArray*)menuItems;

@end
