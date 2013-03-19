//
//  EventsUtil.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 02.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "EventsUtils.h"

#import "EventsService.h"

static NSNumber* favoriteCategNSNumber = nil;
static NSNumber* featuredCategNSNumber = nil;

@implementation EventsUtils

+ (NSDictionary*)sectionsOfEventItem:(NSArray*)eventItems forCategories:(NSDictionary*)categs andTags:(NSDictionary*)tags {
    NSMutableDictionary* itemsForCategs = [NSMutableDictionary dictionaryWithCapacity:[categs count]];
    NSSet* tagSet = [NSSet setWithArray:[tags allKeys]]; //set of NSString (tags shortname)
    
    EventsService* eventsService = [EventsService sharedInstanceToRetain];
    
    for (EventItem* event in eventItems) {
        
        NSSet* eventTagSet = [NSSet setWithArray:event.eventTags];
        NSNumber* eventCategNumber = [NSNumber numberWithInt:event.eventCateg];
        if ([eventsService isEventItemIdFavorite:event.eventId]) {
            eventCategNumber = [NSNumber numberWithInt:-2];
        }
        NSString* eventCategName = categs[eventCategNumber];
        if ([eventTagSet intersectsSet:tagSet] //returns YES if at least one tag in common
            && eventCategName //if != nil, then categ with ID event.eventCateg is present is categs
            ) {
            //From that point, event should be kept
            NSMutableSet* items = itemsForCategs[eventCategNumber];
            if (!items) {
                items = [NSMutableSet setWithCapacity:10]; //Estimate of avg. number of events per category
                itemsForCategs[eventCategNumber] = items;
            }
            [items addObject:event];
        }
    }
    
    [[itemsForCategs copy] enumerateKeysAndObjectsUsingBlock:^(NSNumber* categNumber, NSMutableSet* items, BOOL *stop) {
        //NSLog(@"%d -> %@", [categNumber intValue], NSStringFromClass([items class]));
        itemsForCategs[categNumber] = [[items allObjects] sortedArrayUsingSelector:@selector(compare:)]; //[items array] returns a proxy object (see doc.), I prefer to have a real array for debugging (console).
    }];
    
   /* NSLog(@"AFTER");
    
    [itemsForCategs enumerateKeysAndObjectsUsingBlock:^(NSNumber* categNumber, NSArray* items, BOOL *stop) {
        NSLog(@"%d -> %@", [categNumber intValue], NSStringFromClass([items class]));
    }];*/
    
    return [itemsForCategs copy]; //return non-mutable copy
}

+ (NSNumber*)nsNumberForEventId:(int64_t)eventId {
    return [NSNumber numberWithLong:eventId];
}

+ (NSNumber*)favoriteCategory {
    if (!favoriteCategNSNumber) {
        favoriteCategNSNumber = [NSNumber numberWithInt:-2];
    }
    return favoriteCategNSNumber;
}

+ (NSNumber*)featuredCategory {
    if (!featuredCategNSNumber) {
        featuredCategNSNumber = [NSNumber numberWithInt:-1];
    }
    return featuredCategNSNumber;
}

+ (NSString*)periodStringForEventsPeriod:(int)period selected:(BOOL)selected {
    NSString* string = nil;
    switch (period) {
        case EventsPeriods_ONE_DAY:
            string = NSLocalizedStringFromTable(@"OneDay", @"EventsPlugin", nil);
            break;
        case EventsPeriods_ONE_WEEK:
            string = NSLocalizedStringFromTable(@"OneWeek", @"EventsPlugin", nil);
            break;
        case EventsPeriods_TWO_DAYS:
            string = NSLocalizedStringFromTable(@"TwoDays", @"EventsPlugin", nil);
            break;
        case EventsPeriods_ONE_MONTH:
            string = NSLocalizedStringFromTable(@"OneMonth", @"EventsPlugin", nil);
            break;
        case EventsPeriods_TWO_WEEKS:
            string = NSLocalizedStringFromTable(@"TwoWeeks", @"EventsPlugin", nil);
            break;
        case EventsPeriods_SIX_MONTHS:
            string = NSLocalizedStringFromTable(@"SixMonths", @"EventsPlugin", nil);
            break;
        case EventsPeriods_ONE_YEAR:
            string = NSLocalizedStringFromTable(@"OneYear", @"EventsPlugin", nil);
            break;
        default:
            string = [NSString stringWithFormat:@"%d %@",period ,[NSLocalizedStringFromTable(@"Days", @"EventsPlugin", nil) lowercaseString]];
            break;
    }
    if (selected) {
        string = [NSString stringWithFormat:@"    %@ ✓", string];
    }
    return string;
}

@end
