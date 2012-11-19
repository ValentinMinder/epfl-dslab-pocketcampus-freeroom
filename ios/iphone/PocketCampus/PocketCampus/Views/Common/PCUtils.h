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

@end
