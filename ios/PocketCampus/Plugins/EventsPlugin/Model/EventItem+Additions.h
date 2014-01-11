



//  Created by Loïc Gardiol on 01.03.13.



#import "events.h"

typedef enum {
    EventItemDateStyleShort = 0,
    EventItemDateStyleMedium = 1,
    EventItemDateStyleLong = 2
} EventItemDateStyle;

@interface EventItem (Additions)

- (BOOL)isEqual:(id)object;
- (BOOL)isEqualToEventItem:(EventItem*)eventItem;
- (NSUInteger)hash;

/*
 * Compares on endDate if exists, then startDate if exists, then title
 * Date comparison is asending
 */
- (NSComparisonResult)compare:(EventItem*)object;

/*
 * Returns inversed result of compare:
 */
- (NSComparisonResult)inverseCompare:(EventItem*)object;

- (NSString*)dateString:(EventItemDateStyle)dateStyle;

/*
 * Returns YES if now is in range [startDate, endDate], NO otherwise
 */
- (BOOL)isNow;

@end
