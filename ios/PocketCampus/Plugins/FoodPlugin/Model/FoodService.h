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




//  Created by Loïc Gardiol on 05.03.12.


#import "Service.h"

#import "food.h"

#import "FoodModelAdditions.h"

extern NSString* const kFoodFavoritesRestaurantsUpdatedNotification;

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

/*
 * Any instance getting a FoodResponse (after calling getFoodForRequest:) is responsible for setting this property that
 * acts as a central point to get access to the URLs for other instances that don't have a pointer to the food response.
 */
@property (nonatomic, strong) NSDictionary* pictureUrlForMealType;

/*
 * Any instance getting a FoodResponse (after calling getFoodForRequest:) is responsible for setting this property that
 * acts as a central point to get access to userPriceTarget for other instances that don't have a pointer to the food response.
 * Default: PriceTarget_ALL
 */
@property (nonatomic) NSInteger userPriceTarget; //PriceTarget enum value


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