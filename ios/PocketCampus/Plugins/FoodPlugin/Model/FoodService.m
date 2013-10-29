//
//  FoodService.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "FoodService.h"

#import "ObjectArchiver.h"

static NSString* kFavoriteRestaurantIds = @"favoriteRestaurantIds";

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
        self = [super initWithServiceName:@"food"];
        if (self) {
            instance = self;
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

- (id)thriftServiceClientInstance {
    return [[FoodServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]];
}

#pragma mark - Favorite restaurants

- (void)initFavorites {
    if (!self.favoriteRestaurantIds) { //first try to get it from persistent storage
        self.favoriteRestaurantIds = [(NSSet*)[ObjectArchiver objectForKey:kFavoriteRestaurantIds andPluginName:@"food"] mutableCopy];
    }
    if (!self.favoriteRestaurantIds) { //if not present in persistent storage, create set
        self.favoriteRestaurantIds = [NSMutableSet set];
    }
}

- (BOOL)persistFavorites {
    if (!self.favoriteRestaurantIds) {
        return YES;
    }
    return [ObjectArchiver saveObject:self.favoriteRestaurantIds forKey:kFavoriteRestaurantIds andPluginName:@"food"];
}

- (NSNumber*)nsNumberForRestaurantId:(int64_t)restaurantId {
    return [NSNumber numberWithInt:restaurantId];
}

- (void)addFavoriteRestaurant:(EpflRestaurant*)restaurant {
    [self initFavorites];
    [self.favoriteRestaurantIds addObject:[self nsNumberForRestaurantId:restaurant.rId]];
    [self persistFavorites];
    NSNotification* notif = [NSNotification notificationWithName:kFavoritesRestaurantsUpdatedNotificationName object:self];
    [[NSNotificationCenter defaultCenter] postNotification:notif];

}

- (void)removeFavoritRestaurant:(EpflRestaurant*)restaurant {
    [self initFavorites];
    [self.favoriteRestaurantIds removeObject:[self nsNumberForRestaurantId:restaurant.rId]];
    [self persistFavorites];
    NSNotification* notif = [NSNotification notificationWithName:kFavoritesRestaurantsUpdatedNotificationName object:self];
    [[NSNotificationCenter defaultCenter] postNotification:notif];
}

- (NSSet*)allFavoriteRestaurantIds {
    [self initFavorites];
    return self.favoriteRestaurantIds;
}

- (BOOL)isRestaurantFavorite:(Restaurant*)restaurant {
    [self initFavorites];
    return [self.favoriteRestaurantIds containsObject:[self nsNumberForRestaurantId:restaurant.restaurantId]];
}

#pragma mark - Service methods

- (void)getFoodForRequest:(FoodRequest*)request delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
    operation.cacheValidity = 10800.0; //3 hours
    operation.skipCache = YES; //use getFoodFromCacheForRequest:
    operation.serviceClientSelector = @selector(getFood:);
    operation.delegateDidReturnSelector = @selector(getFoodForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getFoodFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)voteForRequest:(VoteRequest*)request delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = NO;
    operation.serviceClientSelector = @selector(vote:);
    operation.delegateDidReturnSelector = @selector(voteForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(voteFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

#pragma Cached versions

- (FoodResponse*)getFoodFromCacheForRequest:(FoodRequest*)request {
    ServiceRequest* operation = [[ServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getFood:);
    operation.delegateDidReturnSelector = @selector(getFoodForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getFoodFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    return [operation cachedResponseObjectEvenIfStale:NO];
}

#pragma mark Deprecated

- (void)getMealsWithDelegate:(id)delegate {    
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
    operation.skipCache = YES;
    operation.cacheValidity = 3600*24;
    operation.serviceClientSelector = @selector(getMeals);
    operation.delegateDidReturnSelector = @selector(getMealsDidReturn:);
    operation.delegateDidFailSelector = @selector(getMealsFailed);
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (NSArray*)getFromCacheMeals {
    ServiceRequest* operation = [[ServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getMeals);
    operation.delegateDidReturnSelector = @selector(getMealsDidReturn:);
    operation.delegateDidFailSelector = @selector(getMealsFailed);
    operation.returnType = ReturnTypeObject;
    return [operation cachedResponseObjectEvenIfStale:YES];
}

- (void)getRatingsWithDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getRatings);
    operation.delegateDidReturnSelector = @selector(getRatingsDidReturn:);
    operation.delegateDidFailSelector = @selector(getRatingsFailed);
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)setRatingForMeal:(int64_t)mealId rating:(double)rating deviceId:(NSString*)deviceId delegate:(id)delegate {
    //Id is primitive type (long long), rating is primitive (double) => cannot be checked
    if (![deviceId isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad deviceId" reason:@"deviceId is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(setRating:::);
    operation.delegateDidReturnSelector = @selector(setRatingForMeal:rating:deviceId:didReturn:);
    operation.delegateDidFailSelector = @selector(setRatingFailedForMeal:rating:deviceId:);
    [operation addLongLongArgument:mealId];
    [operation addDoubleArgument:rating];
    [operation addObjectArgument:deviceId];
    operation.returnType = ReturnTypeInt;
    [operationQueue addOperation:operation];
}

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