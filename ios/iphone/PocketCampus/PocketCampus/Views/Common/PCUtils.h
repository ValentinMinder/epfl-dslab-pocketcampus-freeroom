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
+ (BOOL)isOSVersionSmallerThan:(CGFloat)version;
+ (void)reloadTableView:(UITableView*)tableView withFadingDuration:(NSTimeInterval)duration;

@end
