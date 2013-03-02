//
//  EventItemViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 02.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "EventsService.h"

@interface EventItemViewController : UIViewController<EventsServiceDelegate>

- (id)initWithEventItem:(EventItem*)item;
- (id)initAndLoadEventItemWithId:(int64_t)eventId;

@property (nonatomic, weak) IBOutlet UIWebView* webView;
@property (nonatomic, weak) IBOutlet UITableView* tableView;

@end
