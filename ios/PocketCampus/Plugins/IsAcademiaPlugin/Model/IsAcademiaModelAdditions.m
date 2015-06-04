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

#import "IsAcademiaModelAdditions.h"

#import "NSDate+Addtions.h"

#import <objc/runtime.h>

@implementation IsAcademiaModelAdditions

+ (NSDate*)mondayReferenceDateForDate:(NSDate*)date {
    [PCUtils throwExceptionIfObject:date notKindOfClass:[NSDate class]];
    
    static NSCache* mondayForDate = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        mondayForDate = [NSCache new];
    });
    
    NSDate* cachedValue = mondayForDate[date];
    if (cachedValue) {
        return cachedValue;
    }
    
    NSCalendar* gregorianCalendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    gregorianCalendar.locale = [NSLocale currentLocale];
    NSDateComponents* comps = [gregorianCalendar components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit | NSWeekdayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit | NSSecondCalendarUnit fromDate:date];
    [comps setHour:0];
    [comps setMinute:0];
    [comps setSecond:0];
    NSDate* normalizedDate = [gregorianCalendar dateFromComponents:comps];
    static NSInteger const kMonday = 2;
    if (comps.weekday == kMonday) {
        mondayForDate[date] = normalizedDate;
        return normalizedDate; //already Monday
    }
    
    NSDate* monday = nil;
    NSDateComponents* backToMondayComps = [NSDateComponents new];
    backToMondayComps.day = kMonday - comps.weekday;
    monday = [gregorianCalendar dateByAddingComponents:backToMondayComps toDate:normalizedDate options:0];
    if ([date compare:monday] == NSOrderedAscending) {
        // Means monday is after date, meaning coming Monday was computed
        // instead of previous one. => need to decrement 1 week
        NSDateComponents* minusOneWeekComps = [NSDateComponents new];
        [minusOneWeekComps setWeekOfYear:-1];
        monday = [gregorianCalendar dateByAddingComponents:minusOneWeekComps toDate:monday options:0];
    }
    mondayForDate[date] = monday;
    return monday;
}

@end

@implementation ScheduleResponse (Additions)

- (StudyDay*)studyDayForDate:(NSDate*)date {
    NSCalendar* calendar = [NSCalendar currentCalendar];
    NSDateComponents* dateComps = [calendar components:NSYearCalendarUnit|NSMonthCalendarUnit|NSDayCalendarUnit fromDate:date];
    calendar.timeZone = [NSTimeZone timeZoneWithName:@"Europe/Zurich"];
    for (StudyDay* studyDay in self.days) {
        NSDateComponents* studyDayComps = [calendar components:NSYearCalendarUnit|NSMonthCalendarUnit|NSDayCalendarUnit fromDate:[NSDate dateWithTimeIntervalSince1970:studyDay.day/1000]];
        
        if (dateComps.year == studyDayComps.year
            && dateComps.month == studyDayComps.month
            && dateComps.day == studyDayComps.day) {
            
            return studyDay;
        }
    }
    return nil;
}

@end

@implementation StudyPeriod (Additions)

#pragma mark - Public

- (NSString*)startTimeString {
    NSString* string = [self.class timeStringForTimeInterval:self.startTime/1000];
    if ([PCUtils systemIsOutsideEPFLTimeZone]) {
        string = [string stringByAppendingFormat:@" (%@ swiss time)", [self.class timeStringForTimeInterval:self.startTime/1000 EPFLTimeZone:YES]];
    }
    return string;
}

- (NSString*)endTimeString {
    NSString* string = [self.class timeStringForTimeInterval:self.endTime/1000];
    if ([PCUtils systemIsOutsideEPFLTimeZone]) {
        string = [string stringByAppendingFormat:@" (%@ swiss time)", [self.class timeStringForTimeInterval:self.endTime/1000 EPFLTimeZone:YES]];
    }
    return string;
}

- (NSString*)startAndEndTimeString {
    NSString* string = [NSString stringWithFormat:@"%@ - %@", [self.class timeStringForTimeInterval:self.startTime/1000], [self.class timeStringForTimeInterval:self.endTime/1000]];
    if ([PCUtils systemIsOutsideEPFLTimeZone]) {
        string = [string stringByAppendingFormat:@" (%@ - %@ swiss time)", [self.class timeStringForTimeInterval:self.startTime/1000 EPFLTimeZone:YES], [self.class timeStringForTimeInterval:self.endTime/1000 EPFLTimeZone:YES]];
    }
    return string;
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
        case StudyPeriodType_ORAL_EXAM:
            return NSLocalizedStringFromTable(@"OralExam", @"IsAcademiaPlugin", nil);
        case StudyPeriodType_WRITTEN_EXAM:
            return NSLocalizedStringFromTable(@"WrittenExam", @"IsAcademiaPlugin", nil);
        default:
            return @"";
    }
}

#pragma mark - Private

+ (NSString*)timeStringForTimeInterval:(NSTimeInterval)timestamp {
    return [self timeStringForTimeInterval:timestamp EPFLTimeZone:NO];
}

+ (NSString*)timeStringForTimeInterval:(NSTimeInterval)timestamp EPFLTimeZone:(BOOL)epflZimeZone {
    static NSDateFormatter* formatter = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        formatter = [NSDateFormatter new];
        formatter.locale = [NSLocale currentLocale];
        formatter.timeZone = [NSTimeZone timeZoneWithName:@"Europe/Zurich"];
        formatter.dateStyle = NSDateFormatterNoStyle;
        formatter.timeStyle = NSDateFormatterShortStyle;
    });
    formatter.timeZone = epflZimeZone ? [NSTimeZone timeZoneWithName:@"Europe/Zurich"] : [NSTimeZone systemTimeZone];
    return [formatter stringFromDate:[NSDate dateWithTimeIntervalSince1970:timestamp]];
}

@end

@implementation SemesterGrades (Additions)

- (NSArray*)sortedGradesKeys {
    static NSString* key;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        key = NSStringFromSelector(_cmd);
    });
    id value = objc_getAssociatedObject(self, (__bridge const void *)(key));
    if (!value) {
        value = [self.grades.allKeys sortedArrayUsingComparator:^NSComparisonResult(NSString* courseName1, NSString* courseName2) {
            NSString* grade1 = self.grades[courseName1];
            NSString* grade2 = self.grades[courseName2];
            if ((grade1.length > 0 && grade2.length > 0) || (grade1.length == 0 && grade2.length == 0)) {
                return [courseName1 localizedCaseInsensitiveCompare:courseName2];
            } else if (grade1.length > 0) {
                return NSOrderedAscending;
            } else {
                return NSOrderedDescending;
            }
        }];
        objc_setAssociatedObject(self, (__bridge const void *)(key), value, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    }
    return value;
}

- (BOOL)existsCourseWithNoGrade {
    static NSString* key;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        key = NSStringFromSelector(_cmd);
    });
    id value = objc_getAssociatedObject(self, (__bridge const void *)(key));
    if (!value) {
        value = @NO;
        for (NSString* course in self.grades) {
            NSString* grade = self.grades[course];
            if (grade.length == 0) {
                value = @YES;
                break;
            }
        }
        objc_setAssociatedObject(self, (__bridge const void *)(key), value, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    }
    return [value boolValue];
}

@end