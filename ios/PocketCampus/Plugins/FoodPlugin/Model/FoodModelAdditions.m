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



#import "FoodModelAdditions.h"
#import "FoodService.h"

#import <objc/runtime.h>

@implementation EpflRestaurant (Additions)

- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    return [self isEqualToEpflRestaurant:object];
}

- (BOOL)isEqualToEpflRestaurant:(EpflRestaurant*)otherRestaurant {
    [PCUtils throwExceptionIfObject:otherRestaurant notKindOfClass:[EpflRestaurant class]];
    return (self.rId == otherRestaurant.rId);
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    hash += self.rId;
    return hash;
}

- (NSComparisonResult)compareToEpflRestaurant:(EpflRestaurant*)otherRestaurant {
    [PCUtils throwExceptionIfObject:otherRestaurant notKindOfClass:[EpflRestaurant class]];
    if ([self isEqualToEpflRestaurant:otherRestaurant]) {
        return NSOrderedSame;
    }
    FoodService* foodService = [FoodService sharedInstanceToRetain];
    BOOL fav1 = [foodService isRestaurantFavorite:self];
    BOOL fav2 = [foodService isRestaurantFavorite:otherRestaurant];
    if (fav1 && !fav2) {
        return NSOrderedAscending;
    } else if (!fav1 && fav2) {
        return NSOrderedDescending;
    } else {
        return [self.rName compare:otherRestaurant.rName];
    }
}

- (NSOrderedSet*)rUniqueMeals {
    static NSString* key;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        key = NSStringFromSelector(_cmd);
    });
    id value = objc_getAssociatedObject(self, (__bridge const void *)(key));
    if (!value) {
        value = [NSOrderedSet orderedSetWithArray:self.rMeals];
        objc_setAssociatedObject(self, (__bridge const void *)(key), value, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    }
    return value;
}

@end


@implementation EpflMeal (Additions)

+ (NSArray*)allMealTypes {
    static NSArray* mealTypes;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        mealTypes = @[@(MealType_UNKNOWN), @(MealType_FISH), @(MealType_MEAT), @(MealType_POULTRY), @(MealType_VEGETARIAN), @(MealType_GREEN_FORK), @(MealType_PASTA), @(MealType_PIZZA), @(MealType_THAI), @(MealType_INDIAN), @(MealType_LEBANESE)];
    });
    return mealTypes;
}

+ (NSString*)localizedNameForMealType:(NSInteger)mealType {
    switch (mealType) {
        case MealType_UNKNOWN:
            return NSLocalizedStringFromTable(@"MealType_UNKNOWN", @"FoodPlugin", nil);
        case MealType_FISH:
            return NSLocalizedStringFromTable(@"MealType_FISH", @"FoodPlugin", nil);
        case MealType_MEAT:
            return NSLocalizedStringFromTable(@"MealType_MEAT", @"FoodPlugin", nil);
        case MealType_POULTRY:
            return NSLocalizedStringFromTable(@"MealType_POULTRY", @"FoodPlugin", nil);
        case MealType_VEGETARIAN:
            return NSLocalizedStringFromTable(@"MealType_VEGETARIAN", @"FoodPlugin", nil);
        case MealType_GREEN_FORK:
            return NSLocalizedStringFromTable(@"MealType_GREEN_FORK", @"FoodPlugin", nil);
        case MealType_PASTA:
            return NSLocalizedStringFromTable(@"MealType_PASTA", @"FoodPlugin", nil);
        case MealType_PIZZA:
            return NSLocalizedStringFromTable(@"MealType_PIZZA", @"FoodPlugin", nil);
        case MealType_THAI:
            return NSLocalizedStringFromTable(@"MealType_THAI", @"FoodPlugin", nil);
        case MealType_INDIAN:
            return NSLocalizedStringFromTable(@"MealType_INDIAN", @"FoodPlugin", nil);
        case MealType_LEBANESE:
            return NSLocalizedStringFromTable(@"MealType_LEBANESE", @"FoodPlugin", nil);
        default:
            return NSLocalizedStringFromTable(@"Unknown", @"PocketCampus", nil);
    }
}


+ (NSString*)enumNameForMealType:(NSInteger)mealType {
    switch (mealType) {
        case MealType_UNKNOWN:
            return @"UNKNOWN";
        case MealType_FISH:
            return @"FISH";
        case MealType_MEAT:
            return @"MEAT";
        case MealType_POULTRY:
            return @"POULTRY";
        case MealType_VEGETARIAN:
            return @"VEGETARIAN";
        case MealType_GREEN_FORK:
            return @"GREEN_FORK";
        case MealType_PASTA:
            return @"PASTA";
        case MealType_PIZZA:
            return @"PIZZA";
        case MealType_THAI:
            return @"THAI";
        case MealType_INDIAN:
            return @"INDIAN";
        case MealType_LEBANESE:
            return @"LEBANESE";
        default:
            return @"UNKNOWN";
    }
}

+ (UIImage*)imageForMealType:(NSInteger)mealType {
    switch (mealType) {
        case MealType_UNKNOWN:
            return [UIImage imageNamed:@"MealType_UNKNOWN"];
        case MealType_FISH:
            return [UIImage imageNamed:@"MealType_FISH"];
        case MealType_MEAT:
            return [UIImage imageNamed:@"MealType_MEAT"];
        case MealType_POULTRY:
            return [UIImage imageNamed:@"MealType_POULTRY"];
        case MealType_VEGETARIAN:
            return [UIImage imageNamed:@"MealType_VEGETARIAN"];
        case MealType_GREEN_FORK:
            return [UIImage imageNamed:@"MealType_GREEN_FORK"];
        case MealType_PASTA:
            return [UIImage imageNamed:@"MealType_PASTA"];
        case MealType_PIZZA:
            return [UIImage imageNamed:@"MealType_PIZZA"];
        case MealType_THAI:
            return [UIImage imageNamed:@"MealType_THAI"];
        case MealType_INDIAN:
            return [UIImage imageNamed:@"MealType_INDIAN"];
        case MealType_LEBANESE:
            return [UIImage imageNamed:@"MealType_LEBANESE"];
        default:
            return nil;
    }
}

- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    return [self isEqualToEpflMeal:object];
}

- (BOOL)isEqualToEpflMeal:(EpflMeal*)otherMeal {
    [PCUtils throwExceptionIfObject:otherMeal notKindOfClass:[EpflMeal class]];
    return self.mId == otherMeal.mId;
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    hash += self.mId;
    return hash;
}

@end
