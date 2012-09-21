//
//  MapService.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 12.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MapService.h"

#import "map.h"

@implementation MapService

static MapService* instance = nil;

+ (id)sharedInstanceToRetain {
    if (instance != nil) {
        return instance;
    }
    @synchronized(self) {
        if (instance == nil) {
            instance = [[[self class] alloc] initWithServiceName:@"map"];
        }
    }
    return [instance autorelease];
}

- (id)thriftServiceClientInstance {
    return [[[MapServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]] autorelease];
}

- (void)getLayerListWithDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getLayerList);
    operation.delegateDidReturnSelector = @selector(getLayerListDidReturn:);
    operation.delegateDidFailSelector = @selector(getLayerListFailed:);
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getLayerItemsForLayerId:(Id)layerId delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getLayerItems:);
    operation.delegateDidReturnSelector = @selector(getLayerItemsForLayerId:didReturn:);
    operation.delegateDidFailSelector = @selector(getLayerItemsFailedForLayerId:);
    [operation addIntArgument:layerId];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)searchFor:(NSString*)query delegate:(id)delegate {
    if (![query isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad query" reason:@"query is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    //operation.keepInCache = YES;
    //operation.cacheValidity = 4*24*3600.0; //4 weeks
    operation.serviceClientSelector = @selector(search:);
    operation.delegateDidReturnSelector = @selector(searchMapFor:didReturn:);
    operation.delegateDidFailSelector = @selector(searchMapFailedFor:);
    [operation addObjectArgument:query];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)dealloc
{
    instance = nil;
    [super dealloc];
}

@end