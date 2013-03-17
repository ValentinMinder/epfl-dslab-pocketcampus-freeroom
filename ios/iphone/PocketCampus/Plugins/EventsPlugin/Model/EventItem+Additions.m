//
//  EventItem+Additions.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "EventItem+Additions.h"

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

- (NSString*)dateString:(EventItemDateStyle)dateStyle {
    
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    
    NSDate* startDate = [NSDate dateWithTimeIntervalSince1970:self.startDate/1000];
    NSDate* endDate = [NSDate dateWithTimeIntervalSince1970:self.endDate/1000];
    
    if (self.fullDay) {
        
        formatter.dateStyle = NSDateFormatterShortStyle;
        if (dateStyle == EventItemDateStyleLong) {
            NSDateComponents* componentsStartDate = [[NSCalendar currentCalendar] components:NSMinuteCalendarUnit | NSHourCalendarUnit fromDate:startDate];
            
            if (!([componentsStartDate minute] == 0 && [componentsStartDate hour] == 0)) {
                formatter.timeStyle = NSDateFormatterShortStyle; //will show time
            }
        }
        
        
        NSString* startDateString = @"";
        NSString* endDateString = @"";
        
        if (self.startDate) {
            startDateString = [formatter stringFromDate:startDate];
        }
        
        if (self.endDate && self.endDate != self.startDate) {
            endDateString = [NSString stringWithFormat:@" - %@", [formatter stringFromDate:endDate]];
        }
        
        return [NSString stringWithFormat:@"%@%@", startDateString, endDateString];
    } else {
        
        NSString* startDateString = @"";
        NSString* endDateString = @"";
        
        NSDateComponents* componentsStartDate = [[NSCalendar currentCalendar] components:NSDayCalendarUnit | NSMonthCalendarUnit | NSYearCalendarUnit fromDate:startDate];

        NSDateComponents* componentsEndDate = [[NSCalendar currentCalendar] components:NSDayCalendarUnit | NSMonthCalendarUnit | NSYearCalendarUnit fromDate:endDate];
        
        if ([componentsStartDate day] == [componentsEndDate day] && [componentsStartDate month] == [componentsEndDate month] && [componentsStartDate year] == [componentsEndDate year]) { //same day
            formatter.dateStyle = NSDateFormatterShortStyle;
            
            if (dateStyle == EventItemDateStyleLong) {
                formatter.timeStyle = NSDateFormatterShortStyle; //will show time
            }
            
            if (self.startDate) {
                startDateString = [formatter stringFromDate:startDate];
            }
            
            NSCalendar* gmtCal = [NSCalendar currentCalendar];
            [gmtCal setTimeZone:[NSTimeZone timeZoneForSecondsFromGMT:0]]; //GMT
            NSDateComponents* gmtStartDateComp = [gmtCal components:NSDayCalendarUnit fromDate:startDate];
            NSDateComponents* gmtEndDateComp = [gmtCal components:NSDayCalendarUnit fromDate:endDate];
            BOOL endTimeValid = ([gmtEndDateComp day] >= [gmtStartDateComp day]);
            
            if (endTimeValid && (dateStyle == EventItemDateStyleLong) && self.endDate && self.endDate != self.startDate) {
            
                formatter.dateStyle = NSDateFormatterNoStyle;
                formatter.timeStyle = NSDateFormatterShortStyle;
            
                endDateString = [NSString stringWithFormat:@" - %@", [formatter stringFromDate:endDate]];
            
            }
            
            return [NSString stringWithFormat:@"%@%@", startDateString, endDateString];
            
        } else {
            formatter.dateStyle = NSDateFormatterShortStyle;
            formatter.timeStyle = NSDateFormatterNoStyle;
            
            if (self.startDate) {
                startDateString = [formatter stringFromDate:startDate];
            }
            
            if (self.endDate && self.endDate != self.startDate) {
                
                formatter.dateStyle = NSDateFormatterShortStyle;
                formatter.timeStyle = NSDateFormatterNoStyle;
                
                endDateString = [NSString stringWithFormat:@" - %@", [formatter stringFromDate:endDate]];
                
            }
            
            return [NSString stringWithFormat:@"%@%@", startDateString, endDateString];
        }
        
    }
    

}


@end
