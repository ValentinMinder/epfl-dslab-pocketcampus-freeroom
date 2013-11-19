//
//  EpflRestaurant+Additions.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 19.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "food.h"

@interface EpflRestaurant (Additions)

- (BOOL)isEqual:(id)object;
/*
 * YES if self and otherRestaurant have same rId
 * NO otherwise
 */
- (BOOL)isEqualToEpflRestaurant:(EpflRestaurant*)otherRestaurant;
- (NSUInteger)hash;

/*
 * Sorted on favorite first (according to FoodService) then rName
 */
- (NSComparisonResult)compareToEpflRestaurant:(EpflRestaurant*)otherRestaurant;

@end
