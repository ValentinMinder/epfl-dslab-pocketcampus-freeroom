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

- (void)addFavoriteRestaurant:(Restaurant*)restaurant {
    [self initFavorites];
    [self.favoriteRestaurantIds addObject:[self nsNumberForRestaurantId:restaurant.restaurantId]];
    [self persistFavorites];
    NSNotification* notif = [NSNotification notificationWithName:kFavoritesRestaurantsUpdatedNotificationName object:self];
    [[NSNotificationCenter defaultCenter] postNotification:notif];

}

- (void)removeFavoritRestaurant:(Restaurant*)restaurant {
    [self initFavorites];
    [self.favoriteRestaurantIds removeObject:[self nsNumberForRestaurantId:restaurant.restaurantId]];
    [self persistFavorites];
    NSNotification* notif = [NSNotification notificationWithName:kFavoritesRestaurantsUpdatedNotificationName object:self];
    [[NSNotificationCenter defaultCenter] postNotification:notif];
}

- (NSArray*)allFavoriteRestaurantIds {
    [self initFavorites];
    return [self.favoriteRestaurantIds allObjects];
}

- (BOOL)isRestaurantFavorite:(Restaurant*)restaurant {
    [self initFavorites];
    return [self.favoriteRestaurantIds containsObject:[self nsNumberForRestaurantId:restaurant.restaurantId]];
}

#pragma mark - Thrift requests

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

- (void)getRestaurantsWithDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getRestaurants);
    operation.delegateDidReturnSelector = @selector(getRestaurantsDidReturn:);
    operation.delegateDidFailSelector = @selector(getRestaurantsFailed);
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)getSandwichesWithDelegate:(id)delegate; {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getSandwiches);
    operation.delegateDidReturnSelector = @selector(getSandwichesDidReturn:);
    operation.delegateDidFailSelector = @selector(getSandwichesFailed);
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)getRating:(Meal*)meal delegate:(id)delegate {
    if (![meal isKindOfClass:[Meal class]]) {
        @throw [NSException exceptionWithName:@"bad meal" reason:@"meal is either nil or not of class Meal" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getRating:);
    operation.delegateDidReturnSelector = @selector(getRatingFor:didReturn:);
    operation.delegateDidFailSelector = @selector(getRatingFailedFor:);
    [operation addObjectArgument:meal];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)hasVoted:(NSString*)deviceId delegate:(id)delegate {
    if (![deviceId isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad deviceId" reason:@"deviceId is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(hasVoted:);
    operation.delegateDidReturnSelector = @selector(hasVotedFor:didReturn:);
    operation.delegateDidFailSelector = @selector(hasVotedFailedFor:);
    [operation addObjectArgument:deviceId];
    operation.returnType = ReturnTypeBool;
    [operationQueue addOperation:operation];
}

- (void)getRatingsWithDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getRatings);
    operation.delegateDidReturnSelector = @selector(getRatingsDidReturn:);
    operation.delegateDidFailSelector = @selector(getRatingsFailed);
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)setRatingForMeal:(Id)mealId rating:(double)rating deviceId:(NSString*)deviceId delegate:(id)delegate {
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