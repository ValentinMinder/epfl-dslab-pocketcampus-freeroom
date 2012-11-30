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

static MapService* instance __weak = nil;

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"MapService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"map"];
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
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}


@end