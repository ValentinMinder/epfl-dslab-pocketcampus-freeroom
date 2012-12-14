//
//  PCRefreshControl.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 30.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MSPullToRefreshController.h"

typedef enum {
    RefreshControlTypeDefault = 0,
    RefreshControlTypeRefreshing,
    RefreshControlTypeProblem
} RefreshControlType;

@interface PCRefreshControl : NSObject<MSPullToRefreshDelegate>

@property (nonatomic, weak, readonly) UITableViewController* tableViewController;
@property (nonatomic) RefreshControlType type;
@property (nonatomic, copy) NSString* message; //WARNING: changing message will set RefreshControlType type back to none. => must call setMessage THEN setType if needed
@property (nonatomic, readonly) BOOL isVisible;


- (id)initWithTableViewController:(UITableViewController*)tableViewController;
- (id)initWithTableViewController:(UITableViewController*)tableViewController refreshedDataIdentifier:(NSString*)dataIdentifier; //will show "last refresh <date>". Task identifier will be use to save last refresh timestamp
- (void)setTarget:(id)target selector:(SEL)selector;

- (void)startRefreshingWithMessage:(NSString*)message;
- (void)endRefreshing;
- (void)markRefreshSuccessful; //call this method just after endRefresh to signal that the last refresh was successfull. This will set the default text to "last refresh <date>". This feature is only supported if refreshedDataIdentifier was indicated at construction.
- (void)show;
- (void)showForTimeInterval:(NSTimeInterval)timeInterval;
- (void)hide;
- (void)hideInTimeInterval:(NSTimeInterval)timeInterval;

@end

@interface CustomMSPullToRefresh : NSObject

@end
