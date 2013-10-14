//
//  EventsTagsViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 18.05.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface EventsTagsViewController : UITableViewController

- (id)initWithTags:(NSArray*)allTags selectedInitially:(NSSet*)selectedInitially userValidatedSelectionBlock:(void (^)(NSSet* newlySelected))userValidatedSelectionBlock;

@property (nonatomic, copy) void (^userValidatedSelectionBlock)(NSSet*);

@end
