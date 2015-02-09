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

//  Created by Loïc Gardiol on 02.03.13.

#import "EventsUtils.h"

#import "EventsService.h"

#import "EventItem+Additions.h"

@implementation EventsUtils

+ (NSDictionary*)sectionsOfEventItem:(NSArray*)eventItems forCategories:(NSDictionary*)categs andTags:(NSDictionary*)tags inverseSort:(BOOL)inverseSort {
    NSMutableDictionary* itemsForCategs = [NSMutableDictionary dictionaryWithCapacity:[categs count]];
    NSSet* tagSet = [NSSet setWithArray:[tags allKeys]]; //set of NSString (tags shortname)
    
    EventsService* eventsService = [EventsService sharedInstanceToRetain];
    
    for (EventItem* event in eventItems) {
        
        NSSet* eventTagSet = [NSSet setWithArray:event.eventTags];
        
        NSNumber* eventCategNumber = [NSNumber numberWithInt:event.eventCateg];
        if ([eventsService isEventItemIdFavorite:event.eventId]) {
            eventCategNumber = kEventItemCategoryFavorite;
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
        if (inverseSort) {
            itemsForCategs[categNumber] = [[items allObjects] sortedArrayUsingSelector:@selector(inverseCompare:)];
        } else {
            itemsForCategs[categNumber] = [[items allObjects] sortedArrayUsingSelector:@selector(compare:)];
        }
        //[items array] returns a proxy object (see doc.), I prefer to have a real array for debugging (console).
    }];
    
   /* NSLog(@"AFTER");
    
    [itemsForCategs enumerateKeysAndObjectsUsingBlock:^(NSNumber* categNumber, NSArray* items, BOOL *stop) {
        NSLog(@"%d -> %@", [categNumber intValue], NSStringFromClass([items class]));
    }];*/
    
    return [itemsForCategs copy]; //return non-mutable copy
}

+ (NSNumber*)nsNumberForEventId:(int64_t)eventId {
    return [NSNumber numberWithLong:(long)eventId];
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
