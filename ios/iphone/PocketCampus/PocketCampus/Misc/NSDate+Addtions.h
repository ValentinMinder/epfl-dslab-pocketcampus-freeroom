//
//  NSDate+Addtions.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 09.06.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSDate (Addtions)

/*
 * Shortcupt to [NSDate dateWithTimeIntervalSince1970:timestamp/1000];
 */
+ (NSDate*)dateWithTimestampInt64_t:(int64_t)timestamp;

- (BOOL)isSameDayAsDate:(NSDate*)date;

/*
 * Returns whether self is in the range [earlyDate, lateDate] (=> bounds included)
 */
- (BOOL)isBetweenEarlyDate:(NSDate*)earlyDate andLateDate:(NSDate*)lateDate;

@end
