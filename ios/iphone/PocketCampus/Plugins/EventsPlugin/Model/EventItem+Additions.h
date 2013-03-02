//
//  EventItem+Additions.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "events.h"

@interface EventItem (Additions)

- (NSString*)shortDateString;

- (BOOL)isEqual:(id)object;
- (NSUInteger)hash;
- (NSComparisonResult)compare:(EventItem*)object;

@end
