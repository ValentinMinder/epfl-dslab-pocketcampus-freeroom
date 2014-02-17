//
//  LGRefreshControl.h
//
//  Created by Loïc Gardiol on 30.10.12.
//  Copyright (c) 2013. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface LGRefreshControl : NSObject

@property (nonatomic, weak, readonly) UITableViewController* tableViewController;
@property (nonatomic, readonly, copy) NSString* refreshedDataIdentifier;
@property (nonatomic, copy) UIColor* tintColor; //will be used for activitiy view and message default state color
@property (nonatomic, strong) UIColor* errorMessageColor;
@property (nonatomic, copy) NSString* message;
@property (nonatomic) BOOL showsDefaultRefreshingMessage; //Default: YES
@property (nonatomic, readonly) BOOL isVisible;

/*
 * Readonly. Use method markRefreshSuccessful to update it.
 */
@property (nonatomic, readonly) NSDate* lastSuccessfulRefreshDate;

/*
 * refreshedDataIdentifier will be use to save last refresh timestamp. Pass nil if you don't want to keep track of refresh date.
 * See markRefreshSuccessful method for more explanations.
 */
- (id)initWithTableViewController:(UITableViewController*)tableViewController refreshedDataIdentifier:(NSString*)dataIdentifier;

/*
 * Use this method if you don't use a tableview controller. A silent one will be created to host the UIRefreshControl
 * and will be added as a child view controller of viewcontroller parameter (cannot be nil)
 */

//- (id)initWithTableView:(UITableView*)tableView parentViewController:(UIViewController*)viewController refreshedDataIdentifier:(NSString*)dataIdentifier;

/*
 * [target selector] will be called when user pulls to refresh
 */
- (void)setTarget:(id)target selector:(SEL)selector;

/*
 * Trigger refresh manually
 */
- (void)startRefreshing;
- (void)startRefreshingWithMessage:(NSString*)message;
- (void)endRefreshing;
- (void)endRefreshingAndMarkSuccessful;
- (void)endRefreshingWithDelay:(NSTimeInterval)delay indicateErrorWithMessage:(NSString*)message;

/*
 * Call this method just after endRefresh to signal that the last refresh was successful.
 * This will set the default text to "last refresh <date>". 
 * This feature is only supported if refreshedDataIdentifier was indicated at init.
 */
- (void)markRefreshSuccessful;


/*
 * This method can be used to know wether the data managed by the refresh control
 * should be refreshed, for a specified validity (seconds)
 * If device is not connected to the internet, this method returns NO
 * If refresh control was initiated without data identifier, this method always returns YES
 */
- (BOOL)shouldRefreshDataForValidity:(NSTimeInterval)validitySeconds;

/*
 * Will delete saved info concerning last successful refresh date for specific identifier
 */
+ (void)deleteRefreshDateInfoForDataIdentifier:(NSString*)dataIdentifier;

/*
 * Same as previous method, with self.refreshedDataIdentifier as dataIdentifier
 */
- (void)deleteRefreshDateInfo;

@end

