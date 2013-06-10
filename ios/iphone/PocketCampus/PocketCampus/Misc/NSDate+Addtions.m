//
//  NSDate+Addtions.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 09.06.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "NSDate+Addtions.h"

#import "PCUtils.h"

@implementation NSDate (Addtions)

+ (NSDate*)dateWithTimestampInt64_t:(int64_t)timestamp {
    return [NSDate dateWithTimeIntervalSince1970:timestamp/1000];
}

- (BOOL)isSameDayAsDate:(NSDate*)date {
    NSCalendar* calendar = [NSCalendar currentCalendar];
    
    unsigned unitFlags = NSYearCalendarUnit | NSMonthCalendarUnit |  NSDayCalendarUnit;
    NSDateComponents* comp1 = [calendar components:unitFlags fromDate:self];
    NSDateComponents* comp2 = [calendar components:unitFlags fromDate:date];
    
    return ([comp1 day]   == [comp2 day] &&
            [comp1 month] == [comp2 month] &&
            [comp1 year]  == [comp2 year]);
}

- (BOOL)isBetweenEarlyDate:(NSDate*)earlyDate andLateDate:(NSDate*)lateDate {
    //NSLog(@"%@ ?< %@ ?< %@", earlyDate, self, lateDate);
    [PCUtils throwExceptionIfObject:earlyDate notKindOfClass:[NSDate class]];
    [PCUtils throwExceptionIfObject:lateDate notKindOfClass:[NSDate class]];
    NSComparisonResult earlyResult = [self compare:earlyDate];
    NSComparisonResult lateResult = [self compare:lateDate];
    if (earlyResult == NSOrderedAscending) {
        return NO; //date is earlier than earlyDate
    }
    if (lateResult == NSOrderedDescending) {
        return NO; //date is later than lateDate
    }
    return YES;
}

@end
