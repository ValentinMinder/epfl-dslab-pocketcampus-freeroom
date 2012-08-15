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

@interface NewsListViewController : UIViewController<NewsServiceDelegate, ASIHTTPRequestDelegate, UITableViewDelegate, UITableViewDataSource> {
    UITableView* tableView;
    UIActivityIndicatorView* centerActivityIndicator;
    UILabel* centerMessageLabel;
    NewsService* newsService;
    NSArray* newsItems;
    ASINetworkQueue* networkQueue;
    NSMutableDictionary* thumbnails; //key : NSIndexPath , value : UIImage
    BOOL shouldRefresh;
    Reachability* reachability;
    NSMutableSet* failedThumbsIndexPaths;
}

- (void)refresh;

@property (readonly) BOOL shouldRefresh;
@property (nonatomic, assign) IBOutlet UITableView* tableView;
@property (nonatomic, assign) IBOutlet UIActivityIndicatorView* centerActivityIndicator;
@property (nonatomic, assign) IBOutlet UILabel* centerMessageLabel;

@end
