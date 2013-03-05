//
//  EventItem+Additions.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "events.h"

typedef enum {
    EventItemDateStyleShort = 0,
    EventItemDateStyleLong = 1
} EventItemDateStyle;

@interface EventItem (Additions)

- (BOOL)isEqual:(id)object;
- (NSUInteger)hash;
- (NSComparisonResult)compare:(EventItem*)object;

- (NSString*)dateString:(EventItemDateStyle)dateStyle;

@end
