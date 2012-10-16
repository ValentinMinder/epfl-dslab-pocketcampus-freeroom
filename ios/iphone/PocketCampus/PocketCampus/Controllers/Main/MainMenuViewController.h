//
//  MainMenuViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@class MainController2;

@interface MainMenuViewController : UIViewController<UITableViewDataSource, UITableViewDelegate>

@property (nonatomic, assign) IBOutlet UITableView* tableView;
@property (nonatomic, readonly) NSArray* menuItems; //array of MainMenuItem
@property (nonatomic, assign) MainController2* mainController;

- (id)initWithMenuItems:(NSArray*)menuItems mainController:(MainController2*)mainController;

@end
