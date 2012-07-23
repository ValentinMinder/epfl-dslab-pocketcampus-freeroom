//
//  FoodService.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "FoodService.h"

@implementation FoodService

static FoodService* instance = nil;

+ (id)sharedInstanceToRetain {
    if (instance != nil) {
        return instance;
    }
    @synchronized(self) {
        if (instance == nil) {
            instance = [[[self class] alloc] initWithServiceName:@"food"];
        }
    }
    return [instance autorelease];
}

- (id)thriftServiceClientInstance {
    return [[[FoodServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]] autorelease];
}

- (void)getMealsWithDelegate:(id)delegate {    
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    //operation.keepInCache = YES;
    //operation.cacheValidity = 3*3600; //3 hours
    operation.serviceClientSelector = @selector(getMeals);
    operation.delegateDidReturnSelector = @selector(getMealsDidReturn:);
    operation.delegateDidFailSelector = @selector(getMealsFailed);
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getRestaurantsWithDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getRestaurants);
    operation.delegateDidReturnSelector = @selector(getRestaurantsDidReturn:);
    operation.delegateDidFailSelector = @selector(getRestaurantsFailed);
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getSandwichesWithDelegate:(id)delegate; {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getSandwiches);
    operation.delegateDidReturnSelector = @selector(getSandwichesDidReturn:);
    operation.delegateDidFailSelector = @selector(getSandwichesFailed);
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
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
    [operation release];

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
    [operation release];
}

- (void)getRatingsWithDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getRatings);
    operation.delegateDidReturnSelector = @selector(getRatingsDidReturn:);
    operation.delegateDidFailSelector = @selector(getRatingsFailed);
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)setRatingForMeal:(Id)mealId rating:(double)rating deviceId:(NSString*)deviceId delegate:(id)delegate; {
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
    [operation release];
}

- (void)dealloc
{
    instance = nil;
    [super dealloc];
}

@end