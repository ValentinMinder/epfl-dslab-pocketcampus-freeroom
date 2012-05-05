//
//  AddStationViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "TransportService.h"

@interface AddStationViewController : UIViewController<TransportServiceDelegate, UISearchBarDelegate, UITableViewDelegate, UITableViewDataSource> {
    UITableView* tableView;
    UILabel* messageLabel;
    UIActivityIndicatorView* barActivityIndicator;
    UISearchBar* searchBar;
    NSTimer* typingTimer;
    TransportService* transportService;
    NSArray* stations;
}

@property (nonatomic, assign) IBOutlet UITableView* tableView;
@property (nonatomic, assign) IBOutlet UILabel* messageLabel;
@property (nonatomic, assign) IBOutlet UIActivityIndicatorView* barActivityIndicator;
@property (nonatomic, assign) IBOutlet UISearchBar* searchBar;

@end
