//
//  FoodService.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "Service.h"

#import "food.h"

//actually used, Xcode deos not simply see it, do not remove.
static NSString* kFavoritesRestaurantsUpdatedNotificationName __unused = @"FavoritesRestaurantsUpdated";

@interface FoodService : Service<ServiceProtocol>

- (void)addFavoriteRestaurant:(EpflRestaurant*)restaurant;
- (void)removeFavoritRestaurant:(EpflRestaurant*)restaurant;
- (NSSet*)allFavoriteRestaurantIds; //set of NSNumber of int64_t of restaurant that are favorite
- (BOOL)isRestaurantFavorite:(EpflRestaurant*)restaurant;

/*
 food service methods
 
 - (NSArray *) getMeals;  // throws TException
 - (NSDictionary *) getRatings;  // throws TException
 - (int) setRating: (int64_t) mealId : (double) rating : (NSString *) deviceId;  // throws TException
 - (FoodResponse *) getFood: (FoodRequest *) foodReq;  // throws TException
 - (VoteResponse *) vote: (VoteRequest *) voteReq;  // throws TException
*/

- (void)getFoodForRequest:(FoodRequest*)request delegate:(id)delegate;
- (void)voteForRequest:(VoteRequest*)request delegate:(id)delegate;


- (void)getMealsWithDelegate:(id)delegate __attribute__((deprecated)); //use getFoodForRequest:delegate:
- (void)getRatingsWithDelegate:(id)delegate __attribute__((deprecated)); //rating are actually in the Meal objects
- (void)setRatingForMeal:(int64_t)mealId rating:(double)rating deviceId:(NSString*)deviceId delegate:(id)delegate __attribute__((deprecated)); //use voteForRequest:delegate:


/* Cached versions */

- (FoodResponse*)getFoodFromCacheForRequest:(FoodRequest*)request;

- (NSArray*)getFromCacheMeals __attribute__((deprecated));


@end

@protocol FoodServiceDelegate <ServiceDelegate>

@optional

- (void)getFoodForRequest:(FoodRequest*)request didReturn:(FoodResponse*)response;
- (void)getFoodFailedForRequest:(FoodRequest *)request;
- (void)voteForRequest:(VoteRequest*)request didReturn:(VoteResponse*)response;
- (void)voteFailedForRequest:(VoteRequest*)request;


- (void)getRatingsDidReturn:(NSDictionary*)ratings __attribute__((deprecated));
- (void)getRatingsFailed __attribute__((deprecated));
- (void)getMealsDidReturn:(NSArray*)meals __attribute__((deprecated));
- (void)getMealsFailed __attribute__((deprecated));
- (void)setRatingForMeal:(int64_t)mealId rating:(double)rating deviceId:(NSString*)deviceId didReturn:(int)status __attribute__((deprecated));
- (void)setRatingFailedForMeal:(int64_t)mealId rating:(double)rating deviceId:(NSString*)deviceId __attribute__((deprecated));

@end