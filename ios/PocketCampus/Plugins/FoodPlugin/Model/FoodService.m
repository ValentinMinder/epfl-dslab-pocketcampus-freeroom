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

#import "FoodService.h"

#import "PCPersistenceManager.h"

NSString* const kFoodFavoritesRestaurantsUpdatedNotification = @"kFavoritesRestaurantsUpdatedNotification";

NSInteger kFoodDefaultUnknownUserPriceTarget = PriceTarget_ALL;

static NSTimeInterval kFoodRequestCacheValidity = 10800.0; //3 hours;

static NSString* const kFavoriteRestaurantIds = @"favoriteRestaurantIds";

@interface FoodService ()

@property (nonatomic, strong) NSMutableSet* favoriteRestaurantIds; //set of NSNumber int64_t

@end

@implementation FoodService

static FoodService* instance __weak = nil;

#pragma mark - Init

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"FoodService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"food" thriftServiceClientClassName:NSStringFromClass(FoodServiceClient.class)];
        if (self) {
            instance = self;
            instance.userPriceTarget = PriceTarget_ALL;
        }
        return self;
    }
}

+ (id)sharedInstanceToRetain {
    @synchronized (self) {
        if (instance) {
            return instance;
        }
#if __has_feature(objc_arc)
        return [[[self class] alloc] init];
#else
        return [[[[self class] alloc] init] autorelease];
#endif
    }
}

#pragma mark - Favorite restaurants

- (void)initFavorites {
    if (!self.favoriteRestaurantIds) { //first try to get it from persistent storage
        self.favoriteRestaurantIds = [(NSSet*)[PCPersistenceManager objectForKey:kFavoriteRestaurantIds pluginName:@"food"] mutableCopy];
    }
    if (!self.favoriteRestaurantIds) { //if not present in persistent storage, create set
        self.favoriteRestaurantIds = [NSMutableSet set];
    }
}

- (BOOL)persistFavorites {
    if (!self.favoriteRestaurantIds) {
        return YES;
    }
    return [PCPersistenceManager saveObject:self.favoriteRestaurantIds forKey:kFavoriteRestaurantIds pluginName:@"food"];
}

- (NSNumber*)nsNumberForRestaurantId:(int64_t)restaurantId {
    return [NSNumber numberWithInteger:(NSInteger)restaurantId];
}

- (void)addFavoriteRestaurant:(EpflRestaurant*)restaurant {
    [PCUtils throwExceptionIfObject:restaurant notKindOfClass:[EpflRestaurant class]];
    [self initFavorites];
    [self.favoriteRestaurantIds addObject:[self nsNumberForRestaurantId:restaurant.rId]];
    [self persistFavorites];
    NSNotification* notif = [NSNotification notificationWithName:kFoodFavoritesRestaurantsUpdatedNotification object:self];
    [[NSNotificationCenter defaultCenter] postNotification:notif];

}

- (void)removeFavoritRestaurant:(EpflRestaurant*)restaurant {
    [PCUtils throwExceptionIfObject:restaurant notKindOfClass:[EpflRestaurant class]];
    [self initFavorites];
    [self.favoriteRestaurantIds removeObject:[self nsNumberForRestaurantId:restaurant.rId]];
    [self persistFavorites];
    NSNotification* notif = [NSNotification notificationWithName:kFoodFavoritesRestaurantsUpdatedNotification object:self];
    [[NSNotificationCenter defaultCenter] postNotification:notif];
}

- (NSSet*)allFavoriteRestaurantIds {
    [self initFavorites];
    return self.favoriteRestaurantIds;
}

- (BOOL)isRestaurantFavorite:(EpflRestaurant*)restaurant {
    [self initFavorites];
    return [self.favoriteRestaurantIds containsObject:[self nsNumberForRestaurantId:restaurant.rId]];
}

#pragma mark - Service methods

- (void)getFoodForRequest:(FoodRequest*)request delegate:(id)delegate {
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
    __weak __typeof(self) welf = self;
    operation.keepInCacheBlock = ^BOOL(void* result) {
        FoodResponse* response = (__bridge id)result;
        if (response.statusCode == FoodStatusCode_OK) {
            welf.userPriceTarget = response.userStatus;
            welf.pictureUrlForMealType = response.mealTypePictureUrls;
            return YES;
        }
        return NO;
    };
    operation.cacheValidityInterval = kFoodRequestCacheValidity;
    operation.skipCache = YES; //use getFoodFromCacheForRequest:
    operation.serviceClientSelector = @selector(getFood:);
    operation.delegateDidReturnSelector = @selector(getFoodForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getFoodFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)voteForRequest:(VoteRequest*)request delegate:(id)delegate {
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = NO;
    operation.serviceClientSelector = @selector(vote:);
    operation.delegateDidReturnSelector = @selector(voteForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(voteFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

#pragma Cached versions

- (FoodResponse*)getFoodFromCacheForRequest:(FoodRequest*)request {
    PCServiceRequest* operation = [[PCServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.cacheValidityInterval = kFoodRequestCacheValidity;
    operation.serviceClientSelector = @selector(getFood:);
    operation.delegateDidReturnSelector = @selector(getFoodForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getFoodFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    FoodResponse* response = [operation cachedResponseObjectEvenIfStale:NO];
    if (response) {
        self.pictureUrlForMealType = response.mealTypePictureUrls;
        self.userPriceTarget = response.userStatus;
    }
    return response;
}

#pragma mark - Dealloc

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}

@end