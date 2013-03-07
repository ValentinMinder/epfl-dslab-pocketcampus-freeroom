//
//  NewsListViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "NewsService.h"

#import "ASIHTTPRequest.h"

#import "ASINetworkQueue.h"

#import "Reachability.h"

@interface NewsListViewController : UITableViewController<NewsServiceDelegate, ASIHTTPRequestDelegate, UITableViewDelegate, UITableViewDataSource>

@end
