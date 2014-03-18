//
//  IsAcademiaModelAdditions.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 18.03.14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

#import "isacademia.h"

@interface ScheduleResponse (Additions)

- (StudyDay*)studyDayForDate:(NSDate*)date;

@end
