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

//  Created by Loïc Gardiol on 01.03.13.

#import "EventItem+Additions.h"

#import "NSDate+Addtions.h"

@implementation EventItem (Additions)

- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    return [self isEqualToEventItem:object];
}

- (BOOL)isEqualToEventItem:(EventItem*)eventItem {
    return self.eventId == eventItem.eventId;
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    hash += self.eventId;
    return hash;
}

- (NSComparisonResult)compare:(EventItem*)object {
    if (![object isKindOfClass:[self class]]) {
        @throw [NSException exceptionWithName:@"Illegal argument" reason:[NSString stringWithFormat:@"cannot compare EventItem with %@", [object class]] userInfo:nil];
    }
    
    if (self.endDate) {
        if (self.endDate < object.endDate) {
            return NSOrderedAscending;
        } else if (self.endDate > object.endDate) {
            return NSOrderedDescending;
        } else {
            return NSOrderedSame;
        }
    } else if (self.startDate) {
        if (self.startDate < object.startDate) {
            return NSOrderedAscending;
        } else if (self.startDate > object.startDate) {
            return NSOrderedDescending;
        } else {
            return NSOrderedSame;
        }
    } else {
        return [self.eventTitle compare:object.eventTitle];
    }
}

- (NSComparisonResult)inverseCompare:(EventItem*)object {
    NSComparisonResult result = [self compare: object];
    if (result == NSOrderedAscending) {
        return NSOrderedDescending;
    } else if (result == NSOrderedDescending) {
        return NSOrderedAscending;
    } else {
        return NSOrderedSame;
    }
}

- (NSString*)shortDateString {
    return [self dateStringShort:YES];
}

- (NSString*)dateString {
    return [self dateStringShort:NO];
}

- (NSString*)dateStringShort:(BOOL)shortBool {
    if (self.startDate == 0) {
        return nil;
    }
    
    static NSDateFormatter* formatter = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        formatter = [NSDateFormatter new];
    });
    
    NSDate* startDate = [NSDate dateWithTimeIntervalSince1970:self.startDate/1000];
    NSDate* endDate = [NSDate dateWithTimeIntervalSince1970:self.endDate/1000];
    
    BOOL oneDayEvent = [startDate isSameDayAsDate:endDate countMidnightAsSameDay:YES];
    
    formatter.dateStyle = NSDateFormatterShortStyle;
    formatter.timeStyle = self.fullDay || (!oneDayEvent && shortBool) ? NSDateFormatterNoStyle : NSDateFormatterShortStyle;
    
    NSString* startDateString = [formatter stringFromDate:startDate];
    
    formatter.dateStyle = oneDayEvent ? NSDateFormatterNoStyle : NSDateFormatterShortStyle;
    formatter.timeStyle = self.fullDay || (!oneDayEvent && shortBool) ? NSDateFormatterNoStyle : formatter.dateStyle;
    
    NSString* endDateString = [formatter stringFromDate:endDate];
    
    NSString* finalDateString = endDateString.length > 0 ? [NSString stringWithFormat:@"%@ – %@", startDateString, endDateString] : startDateString;
    
    return finalDateString;
}

- (BOOL)isNow {
    NSDate* startDate = [NSDate dateWithTimestampInt64_t:self.startDate];
    NSDate* endDate = [NSDate dateWithTimestampInt64_t:self.endDate];
    NSDate* now = [NSDate date];
    if (self.startDate && self.endDate) {
        return [now isBetweenEarlyDate:startDate andLateDate:endDate];
    }
    if (self.startDate) {
        return [now isSameDayAsDate:startDate];
    }
    //might only have endDate but that's weird, return NO
    return NO;
}


@end
