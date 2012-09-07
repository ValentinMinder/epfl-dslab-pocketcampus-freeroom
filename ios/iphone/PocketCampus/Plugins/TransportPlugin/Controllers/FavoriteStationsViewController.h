//
//  FavoriteStationsViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "TransportService.h"

@interface FavoriteStationsViewController : UIViewController<UITableViewDelegate, UITableViewDataSource> {
    UITableView* tableView;
    UILabel* touchAddInstructionsLabel;
    UIBarButtonItem* addButton;
    NSMutableArray* favStations;
    TransportService* transportService;
    TransportStation* selectedStation;
}

@property (nonatomic, assign) IBOutlet UITableView* tableView;
@property (nonatomic, assign) IBOutlet UILabel* touchAddInstructionsLabel;
@property (nonatomic, assign) IBOutlet UIBarButtonItem* addButton;

/* DEV */
@property (nonatomic, retain) NSString* dev_location_test;

@end
