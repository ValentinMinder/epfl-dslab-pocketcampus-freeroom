//
//  EventItem+Additions.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "EventItem+Additions.h"

@implementation EventItem (Additions)

- (NSString*)shortDateString {
    
    return @"TODO";
    
    /*NSDate* date = [NSDate dateWithTimeIntervalSince1970:timestamp];
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    formatter.dateStyle = NSDateFormatterMediumStyle;
    return [formatter stringFromDate:date];*/
}

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
        return NSOrderedSame;
    }
}

@end
