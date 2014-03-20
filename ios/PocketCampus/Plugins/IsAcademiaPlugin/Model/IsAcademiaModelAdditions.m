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

//  Copyright (c) 2014 EPFL. All rights reserved.

#import "IsAcademiaModelAdditions.h"

#import "NSDate+Addtions.h"

@implementation ScheduleResponse (Additions)

- (StudyDay*)studyDayForDate:(NSDate*)date {
    for (StudyDay* studyDay in self.days) {
        if ([date isSameDayAsDate:[NSDate dateWithTimeIntervalSince1970:studyDay.day/1000]]) {
            return studyDay;
        }
    }
    return nil;
}

@end

@implementation StudyPeriod (Additions)

#pragma mark - Public

- (NSString*)startTimeString {
    return [self.class timeStringForTimeInterval:self.startTime/1000];
}

- (NSString*)endTimeString {
    return [self.class timeStringForTimeInterval:self.endTime/1000];
}

- (NSString*)startAndEndTimeString {
    return [NSString stringWithFormat:@"%@ - %@", self.startTimeString, self.endTimeString];
}

- (NSString*)roomsString {
    NSString* ret __block = @"";
    [self.rooms enumerateObjectsUsingBlock:^(NSString* unit, NSUInteger index, BOOL *stop) {
        if (index < self.rooms.count - 1) {
            ret = [ret stringByAppendingFormat:@"%@, ", unit];
        } else {
            ret = [ret stringByAppendingString:unit];
        }
    }];
    return ret;
}

- (NSString*)periodTypeString {
    switch (self.periodType) {
        case StudyPeriodType_LECTURE:
            return NSLocalizedStringFromTable(@"Lecture", @"IsAcademiaPlugin", nil);
        case StudyPeriodType_EXERCISES:
            return NSLocalizedStringFromTable(@"Exercises", @"IsAcademiaPlugin", nil);
        case StudyPeriodType_LAB:
            return NSLocalizedStringFromTable(@"Lab", @"IsAcademiaPlugin", nil);
        case StudyPeriodType_PROJECT:
            return NSLocalizedStringFromTable(@"Project", @"IsAcademiaPlugin", nil);
        default:
            return @"";
    }
}

#pragma mark - Private

+ (NSString*)timeStringForTimeInterval:(NSTimeInterval)timestamp {
    static NSDateFormatter* formatter = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        formatter = [NSDateFormatter new];
        formatter.locale = [NSLocale currentLocale];
        formatter.timeZone = [NSTimeZone systemTimeZone];
        formatter.dateStyle = NSDateFormatterNoStyle;
        formatter.timeStyle = NSDateFormatterShortStyle;
    });
    return [formatter stringFromDate:[NSDate dateWithTimeIntervalSince1970:timestamp]];
}

@end