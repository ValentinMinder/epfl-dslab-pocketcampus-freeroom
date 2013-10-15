//
//  EventPoolViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "EventsService.h"

@interface EventPoolViewController : UITableViewController

- (id)initWithEventPool:(EventPool*)pool;

- (id)initAndLoadRootPool;
- (id)initAndLoadEventPoolWithId:(int64_t)poolId;

- (int64_t)poolId;
- (void)refresh;

@end
