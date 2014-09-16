/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

//  Created by Loïc Gardiol on 09.06.13.

#import "NSDate+Addtions.h"

#import "PCUtils.h"

@implementation NSDate (Addtions)

+ (NSDate*)dateWithTimestampInt64_t:(int64_t)timestamp {
    return [NSDate dateWithTimeIntervalSince1970:timestamp/1000];
}

- (BOOL)isSameDayAsDate:(NSDate*)date {
    return [self isSameDayAsDate:date countMidnightAsSameDay:NO];
}

- (BOOL)isSameDayAsDate:(NSDate*)date countMidnightAsSameDay:(BOOL)countMidnightAsSameDay {
    NSCalendar* calendar = [NSCalendar currentCalendar];
    unsigned unitFlags = NSCalendarUnitYear | NSCalendarUnitMonth |  NSCalendarUnitDay | NSCalendarUnitHour | NSCalendarUnitMinute;
    NSDateComponents* comp1 = [calendar components:unitFlags fromDate:self];
    if (countMidnightAsSameDay) {
        NSDateComponents* minus1Min = [NSDateComponents new];
        minus1Min.minute = -1;
        date = [calendar dateByAddingComponents:minus1Min toDate:date options:0];
    }
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
