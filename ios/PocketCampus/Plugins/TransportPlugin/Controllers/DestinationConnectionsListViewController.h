//
//  DestinationConnectionsListViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 10.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "transport.h"

@interface DestinationConnectionsListViewController : UIViewController<UITableViewDelegate, UITableViewDataSource> {
    UITableView* tableView;
    QueryTripsResult* queryTripResult;
}

- (id)initWithQueryTripResult:(QueryTripsResult*)tripResult;

@property (nonatomic, assign) IBOutlet UITableView* tableView;

@end
