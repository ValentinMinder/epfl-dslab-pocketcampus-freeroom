//
//  IsAcademiaModelAdditions.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 18.03.14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

#import "IsAcademiaModelAdditions.h"

#import "NSDate+Addtions.h"

@implementation ScheduleResponse (Additions)

- (StudyDay*)studyDayForDate:(NSDate*)date {
    for (StudyDay* studyDay in self.days) {
        if ([date isSameDayAsDate:[NSDate dateWithTimeIntervalSince1970:studyDay.day]]) {
            return studyDay;
        }
    }
    return nil;
}

@end
