//
//  EventsUtil.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 02.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "EventsUtils.h"

static NSNumber* favoriteCategNSNumber = nil;
static NSNumber* featuredCategNSNumber = nil;

@implementation EventsUtils

+ (NSDictionary*)sectionsOfEventItem:(NSArray*)eventItems forCategories:(NSDictionary*)categs andTags:(NSDictionary*)tags {
    NSMutableDictionary* itemsForCategs = [NSMutableDictionary dictionaryWithCapacity:[categs count]];
    NSSet* tagSet = [NSSet setWithArray:[tags allKeys]]; //set of NSString (tags shortname)
    
    for (EventItem* event in eventItems) {
        NSSet* eventTagSet = [NSSet setWithArray:event.eventTags];
        NSNumber* eventCategNumber = [NSNumber numberWithInt:event.eventCateg];
        NSString* eventCategName = categs[eventCategNumber];
        if ([eventTagSet intersectsSet:tagSet] //returns YES if at least one tag in common
            && eventCategName //if != nil, then categ with ID event.eventCateg is present is categs
            ) {
            //From that point, event should be kept
            NSMutableOrderedSet* items = itemsForCategs[eventCategNumber];
            if (!items) {
                items = [NSMutableOrderedSet orderedSetWithCapacity:10]; //Estimate of avg. number of events per category
                itemsForCategs[eventCategNumber] = items;
            }
            [items addObject:event];
        }
    }
    
    [[itemsForCategs copy] enumerateKeysAndObjectsUsingBlock:^(NSNumber* categNumber, NSMutableOrderedSet* items, BOOL *stop) {
        //NSLog(@"%d -> %@", [categNumber intValue], NSStringFromClass([items class]));
        itemsForCategs[categNumber] = [NSArray arrayWithArray:[items array]]; //[items array] returns a proxy object (see doc.), I prefer to have a real array for debugging (console).
    }];
    
   /* NSLog(@"AFTER");
    
    [itemsForCategs enumerateKeysAndObjectsUsingBlock:^(NSNumber* categNumber, NSArray* items, BOOL *stop) {
        NSLog(@"%d -> %@", [categNumber intValue], NSStringFromClass([items class]));
    }];*/
    
    return [itemsForCategs copy]; //return non-mutable copy
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

@end
