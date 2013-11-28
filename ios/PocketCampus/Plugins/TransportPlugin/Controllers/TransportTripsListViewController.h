//
//  TransportTripsListViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 10.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

@class QueryTripsResult;

@interface TransportTripsListViewController : UITableViewController

- (id)initWithQueryTripResult:(QueryTripsResult*)tripResult;

@property (nonatomic, readonly, strong) QueryTripsResult* tripResult;

@end
