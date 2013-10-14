//
//  GlobalSettingsViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 13.07.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@class MainController;

@interface GlobalSettingsViewController : UIViewController<UITableViewDelegate, UITableViewDataSource, UITextFieldDelegate>

@property (nonatomic, assign) IBOutlet UITableView* tableView;

- (id)initWithMainController:(MainController*)mainController;

@end
