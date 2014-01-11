



//  Created by Loïc Gardiol on 19.11.13.



#import "food.h"

#pragma mark - EpflRestaurant (Additions)

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

@property (nonatomic, readonly) NSOrderedSet* rUniqueMeals;

@end

#pragma mark - EpflMeal (Additions)

@interface EpflMeal (Additions)

- (BOOL)isEqual:(id)object;
/*
 * YES if self and otherMeal have same rId
 * NO otherwise
 */
- (BOOL)isEqualToEpflMeal:(EpflMeal*)otherMeal;
- (NSUInteger)hash;

@end