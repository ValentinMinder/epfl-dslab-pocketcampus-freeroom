//
//  FoodModelAdditions.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 19.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

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
    return self.rId == otherRestaurant.rId;
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
    static NSString* const krUniqueMealsKey = @"rUniqueMeals";
    NSOrderedSet* uniqueMeals = objc_getAssociatedObject(self, (__bridge const void *)(krUniqueMealsKey));
    if (!uniqueMeals) {
        uniqueMeals = [NSOrderedSet orderedSetWithArray:self.rMeals];
        objc_setAssociatedObject(self, (__bridge const void *)(krUniqueMealsKey), uniqueMeals, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    }
    return uniqueMeals;
}

@end


@implementation EpflMeal (Additions)

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
