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

//  Created by Loïc Gardiol on 18.03.14.

#import "isacademia.h"

@interface IsAcademiaModelAdditions : NSObject

/**
 * @return the Monday at 00:00 (Morning) that directly precedes date
 * or date if it is it already.
 * Eg.  2015-01-04 6:38 p.m. returns 2014-12-29 00:00 CET
 *      2015-01-05 11:00 a.m returns 2015-01-05 00:00 CET
 * @discussion returned values are cached for each date
 */
+ (NSDate*)mondayReferenceDateForDate:(NSDate*)date;

@end

@interface ScheduleResponse (Additions)

- (StudyDay*)studyDayForDate:(NSDate*)date;

@end

@interface StudyPeriod (Additions)

@property (nonatomic, readonly) NSString* startTimeString;

@property (nonatomic, readonly) NSString* endTimeString;

@property (nonatomic, readonly) NSString* startAndEndTimeString;

@property (nonatomic, readonly) NSString* roomsString;

@property (nonatomic, readonly) NSString* periodTypeString;

@end

@interface SemesterGrades (Additions)<NSCopying> //shallow copy, grades property keeps same pointer

/**
 * @return a sorted array of the keys of the grades property
 * Sort order is first, in order: where a grade exists, alphabetically
 */
@property (nonatomic, readonly) NSArray* sortedGradesKeys;

/**
 * @return YES if there exists a key that has 0-length value in the grades property
 */
@property (nonatomic, readonly) BOOL existsCourseWithNoGrade;

@end