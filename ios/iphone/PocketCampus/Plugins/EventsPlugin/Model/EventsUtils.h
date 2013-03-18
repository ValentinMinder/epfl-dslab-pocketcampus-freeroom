//
//  EventsUtil.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 02.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "events.h"

@interface EventsUtils : NSObject

/*
 * Key = NSNumber of category id, value = array of EventItem, sorted by date, that are in this category and have one or more tags
 */
+ (NSDictionary*)sectionsOfEventItem:(NSArray*)eventItems forCategories:(NSDictionary*)categs andTags:(NSDictionary*)tags;

+ (NSNumber*)nsNumberForEventId:(int64_t)eventId;
+ (NSNumber*)favoriteCategory;
+ (NSNumber*)featuredCategory;

+ (NSString*)periodStringForEventsPeriod:(int)period selected:(BOOL)selected;

@end
