//
//  MainMenuViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@class MainController;

@interface MainMenuViewController : UIViewController<UITableViewDataSource, UITableViewDelegate>

@property (nonatomic, strong) IBOutlet UITableView* tableView;
@property (nonatomic, strong, readonly) NSArray* menuItems; //array of MainMenuItem
@property (nonatomic, weak, readonly) MainController* mainController;

- (id)initWithMenuItems:(NSArray*)menuItems mainController:(MainController*)mainController;
- (void)setSelectedPluginWithIdentifier:(NSString*)pluginIdentifier animated:(BOOL)animated; //pass nil to select no cell

@end
