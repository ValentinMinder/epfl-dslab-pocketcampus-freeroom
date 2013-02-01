//
//  FoodService.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "Service.h"

#import "food.h"

@interface FoodService : Service<ServiceProtocol>

/*
 food service methods
 
 - (NSArray *) getMeals;  // throws TException
 - (NSArray *) getRestaurants;  // throws TException
 - (NSArray *) getSandwiches;  // throws TException
 - (Rating *) getRating: (Meal *) meal;  // throws TException
 - (BOOL) hasVoted: (NSString *) deviceId;  // throws TException
 - (NSDictionary *) getRatings;  // throws TException
 - (int) setRating: (Id) mealId : (double) rating : (NSString *) deviceId;  // throws TException
*/

- (void)getMealsWithDelegate:(id)delegate;
- (void)getRestaurantsWithDelegate:(id)delegate;
- (void)getSandwichesWithDelegate:(id)delegate;
- (void)getRating:(Meal*)meal delegate:(id)delegate;
- (void)hasVoted:(NSString*)deviceId delegate:(id)delegate;
- (void)getRatingsWithDelegate:(id)delegate;
- (void)setRatingForMeal:(Id)mealId rating:(double)rating deviceId:(NSString*)deviceId delegate:(id)delegate;

/* Cached versions */

- (NSArray*)getFromCacheMeals;

@end

@protocol FoodServiceDelegate <ServiceDelegate>

@optional
- (void)getMealsDidReturn:(NSArray*)meals;
- (void)getMealsFailed;
- (void)getRestaurantsDidReturn:(NSArray*)restaurants;
- (void)getRestaurantsFailed;
- (void)getSandwichesDidReturn:(NSArray*)sandwiches;
- (void)getSandwichesFailed;
- (void)getRatingFor:(Meal*)meal didReturn:(Rating*)rating;
- (void)getRatingFailedFor:(Meal*)meal;
- (void)hasVotedFor:(NSString*)deviceId didReturn:(BOOL)hasVoted;
- (void)hasVotedFailedFor:(NSString*)deviceId;
- (void)getRatingsDidReturn:(NSDictionary*)ratings;
- (void)getRatingsFailed;
- (void)setRatingForMeal:(Id)mealId rating:(double)rating deviceId:(NSString*)deviceId didReturn:(int)status;
- (void)setRatingFailedForMeal:(Id)mealId rating:(double)rating deviceId:(NSString*)deviceId;

@end