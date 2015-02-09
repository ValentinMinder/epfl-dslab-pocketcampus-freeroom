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

//  Created by Loïc Gardiol on 19.11.13.

#import "food.h"

#pragma mark - EpflRestaurant (Additions)

@interface EpflRestaurant (Additions)

- (BOOL)isEqual:(id)object;

/**
 * @return YES if self and otherRestaurant have same rId, NO otherwise
 */
- (BOOL)isEqualToEpflRestaurant:(EpflRestaurant*)otherRestaurant;
- (NSUInteger)hash;

/**
 * Sorted on favorite first (according to FoodService) then rName
 */
- (NSComparisonResult)compareToEpflRestaurant:(EpflRestaurant*)otherRestaurant;

@property (nonatomic, readonly) NSOrderedSet* rUniqueMeals;

@end

#pragma mark - EpflMeal (Additions)

@interface EpflMeal (Additions)

/**
 * @return NSArray of NSNumber representing all existing meal types
 */
+ (NSArray*)allMealTypes;

/**
 * @return localized name for mealType if known, "Unknown" otherwise
 */
+ (NSString*)localizedNameForMealType:(NSInteger)mealType;

/**
 * @return name derived from the enum name for mealType (e.g. MEAT), "UNKNOWN" otherwise
 */
+ (NSString*)enumNameForMealType:(NSInteger)mealType;

/**
 * @return a 80x80 points image for the meal type, nil if mealType is unknown
 */
+ (UIImage*)imageForMealType:(NSInteger)mealType;

- (BOOL)isEqual:(id)object;

/**
 * @return YES if self and otherMeal have same rId, NO otherwise
 */
- (BOOL)isEqualToEpflMeal:(EpflMeal*)otherMeal;
- (NSUInteger)hash;

@end