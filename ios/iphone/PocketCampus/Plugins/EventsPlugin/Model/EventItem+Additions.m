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
    
    if (self.endDate < object.endDate) {
        return NSOrderedDescending;
    } else if (self.endDate > object.endDate) {
        return NSOrderedAscending;
    } else {
        return [self.eventTitle compare:object.eventTitle];
    }
}

- (NSString*)shortDateString {
    
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    
    NSDate* startDate = [NSDate dateWithTimeIntervalSince1970:self.startDate/1000];
    NSDate* endDate = [NSDate dateWithTimeIntervalSince1970:self.endDate/1000];
    
    if (self.fullDay) {
        
        formatter.dateStyle = NSDateFormatterShortStyle;
        
        
        NSString* startDateString = nil;
        NSString* endDateString = nil;
        
        if (self.startDate) {
            startDateString = [formatter stringFromDate:startDate];
        }
        
        if (self.endDate) {
            endDateString = [formatter stringFromDate:endDate];
        }
        
        return [NSString stringWithFormat:@"%@ - %@", startDateString, endDateString];
    } else {
        
        NSString* startDateString = nil;
        NSString* endDateString = nil;
        
        NSDateComponents* componentsStartDate = [[NSCalendar currentCalendar] components:NSDayCalendarUnit | NSMonthCalendarUnit | NSYearCalendarUnit fromDate:startDate];

        NSDateComponents* componentsEndDate = [[NSCalendar currentCalendar] components:NSDayCalendarUnit | NSMonthCalendarUnit | NSYearCalendarUnit fromDate:endDate];
        
        if ([componentsStartDate day] == [componentsEndDate day] && [componentsStartDate month] == [componentsEndDate month] && [componentsStartDate year] == [componentsEndDate year]) { //same day
            formatter.dateStyle = NSDateFormatterShortStyle;
            formatter.timeStyle = NSDateFormatterShortStyle;
            
            startDateString = [formatter stringFromDate:startDate];
            
            formatter.dateStyle = NSDateFormatterNoStyle;
            formatter.timeStyle = NSDateFormatterShortStyle;
            
            endDateString = [formatter stringFromDate:endDate];
            
            return [NSString stringWithFormat:@"%@ - %@", startDateString, endDateString];
            
        } else {
            formatter.dateStyle = NSDateFormatterShortStyle;
            formatter.timeStyle = NSDateFormatterNoStyle;
            
            startDateString = [formatter stringFromDate:startDate];
            
            formatter.dateStyle = NSDateFormatterShortStyle;
            formatter.timeStyle = NSDateFormatterNoStyle;
            
            endDateString = [formatter stringFromDate:endDate];
            
            return [NSString stringWithFormat:@"%@ - %@", startDateString, endDateString];
        }
        
    }
    

}


@end
