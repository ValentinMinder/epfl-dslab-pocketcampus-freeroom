//
//  TransportSettingsViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 27.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TransportSettingsViewController : UIViewController<UITableViewDataSource, UITableViewDelegate, UIAlertViewDelegate> {
    UITableView* tableView;
}

@property (nonatomic, assign) IBOutlet UITableView* tableView;

@end
