



//  Created by Loïc Gardiol on 02.03.13.



#import <Foundation/Foundation.h>

#import "events.h"

@interface EventsUtils : NSObject

/*
 * Key = NSNumber of category id, value = array of EventItem, sorted by date (or inverse), that are in this category and have one or more tags
 */
+ (NSDictionary*)sectionsOfEventItem:(NSArray*)eventItems forCategories:(NSDictionary*)categs andTags:(NSDictionary*)tags inverseSort:(BOOL)inverseSort;

+ (NSNumber*)nsNumberForEventId:(int64_t)eventId;
+ (NSNumber*)favoriteCategory;
+ (NSNumber*)featuredCategory;

+ (NSString*)periodStringForEventsPeriod:(int)period selected:(BOOL)selected;

@end
