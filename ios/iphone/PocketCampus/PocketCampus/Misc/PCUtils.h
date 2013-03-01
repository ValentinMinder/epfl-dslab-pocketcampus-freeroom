//
//  PCUtils.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.07.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface PCUtils : NSObject

+ (BOOL)isRetinaDevice;
+ (BOOL)is4inchDevice;
+ (BOOL)isIdiomPad;
+ (BOOL)isOSVersionSmallerThan:(CGFloat)version;
+ (NSString*)userLanguageCode;
+ (NSString*)lastUpdateNowString;
+ (void)reloadTableView:(UITableView*)tableView withFadingDuration:(NSTimeInterval)duration;
+ (void)printFrame:(CGRect)frame;
+ (NSString*)stringFromFileSize:(unsigned long long)size;
+ (BOOL)double:(double)d1 isEqualToDouble:(double)d2 epsilon:(double)epsilon;

+ (void)showServerErrorAlert;
+ (void)showConnectionToServerTimedOutAlert;

/*
 * Returns [[Reachability reachabilityForInternetConnection] isReachable], i.e. whether internet is reachable
 */
+ (BOOL)hasDeviceInternetConnection;

@end
