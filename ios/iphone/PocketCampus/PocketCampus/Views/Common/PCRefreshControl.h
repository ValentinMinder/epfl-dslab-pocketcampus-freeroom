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
@property (nonatomic, copy) NSString* message;
@property (nonatomic, readonly) BOOL isVisible;

/*
 * Readonly. Use method markRefreshSuccessful to update it.
 */
@property (nonatomic, readonly) NSDate* lastSuccessfullRefreshDate;


- (id)initWithTableViewController:(UITableViewController*)tableViewController;

/*
 * Enables "last refresh <date>" feature. See method markRefreshSuccessful for How-To.
 * Task identifier will be use to save last refresh timestamp.
 */
- (id)initWithTableViewController:(UITableViewController*)tableViewController pluginName:(NSString*)pluginName refreshedDataIdentifier:(NSString*)dataIdentifier;

/*
 * [target selector] will be called when user pulls to refresh
 */
- (void)setTarget:(id)target selector:(SEL)selector;

/*
 * Triggers refresh manually
 */
- (void)startRefreshingWithMessage:(NSString*)message;
- (void)endRefreshing;

/*
 * Call this method just after endRefresh to signal that the last refresh was successfull.
 * This will set the default text to "last refresh <date>". 
 * This feature is only supported if refreshedDataIdentifier was indicated at init.
 * Note: last refresh date is only displayed in iOS>=6
 */
- (void)markRefreshSuccessful;


/*
 * This method can be used to know wether the data managed by the refresh control
 * should be refreshed, for a specified validity (seconds)
 * If refresh control was initiated without data identifier, this method always returns YES
 */
- (BOOL)shouldRefreshDataForValidity:(NSTimeInterval)validitySeconds;

- (void)show;
- (void)showForTimeInterval:(NSTimeInterval)timeInterval;
- (void)hide;
- (void)hideInTimeInterval:(NSTimeInterval)timeInterval;

@end

@interface CustomMSPullToRefresh : NSObject

@end
