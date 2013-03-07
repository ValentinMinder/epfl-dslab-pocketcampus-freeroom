//
//  EventItemViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 02.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "EventsService.h"

#import "ZBarSDK.h"

@interface EventItemViewController : UIViewController<EventsServiceDelegate, UIWebViewDelegate>

- (id)initWithEventItem:(EventItem*)item;
- (id)initAndLoadEventItemWithId:(int64_t)eventId;

@property (nonatomic, weak) IBOutlet UIScrollView* scrollView;
@property (nonatomic, weak) IBOutlet UIWebView* webView;
@property (nonatomic, weak) IBOutlet UITableView* tableView;

@end
