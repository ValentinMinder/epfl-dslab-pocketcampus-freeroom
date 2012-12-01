//
//  CamiproService.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 17.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "CamiproService.h"

#import "ObjectArchiver.h"

@implementation CamiproService

static NSString* kLastSessionIdKey = @"lastSessionId";

static CamiproService* instance __weak = nil;

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"CamiproService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"camipro"];
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
    return [[[CamiproServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]] autorelease];
}

+ (CamiproSession*)lastSessionId {
    return (CamiproSession*)[ObjectArchiver objectForKey:kLastSessionIdKey andPluginName:@"camipro"];
}

+ (BOOL)saveSessionId:(CamiproSession*)sessionId {
    return [ObjectArchiver saveObject:sessionId forKey:kLastSessionIdKey andPluginName:@"camipro"];
}

- (void)getTequilaTokenForCamiproDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getTequilaTokenForCamipro);
    operation.delegateDidReturnSelector = @selector(getTequilaTokenForCamiproDidReturn:);
    operation.delegateDidFailSelector = @selector(getTequilaTokenForCamiproFailed);
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken*)tequilaKey delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getCamiproSession:);
    operation.delegateDidReturnSelector = @selector(getSessionIdForServiceWithTequilaKey:didReturn:);
    operation.delegateDidFailSelector = @selector(getSessionIdForServiceFailedForTequilaKey:);
    [operation addObjectArgument:tequilaKey];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getBalanceAndTransactions:(CamiproRequest*)camiproRequest delegate:(id)delegate {
    if (![camiproRequest isKindOfClass:[CamiproRequest class]]) {
        @throw [NSException exceptionWithName:@"bad camiproRequest" reason:@"camiproRequest is either nil or not of class CamiproRequest" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getBalanceAndTransactions:);
    operation.delegateDidReturnSelector = @selector(getBalanceAndTransactionsForCamiproRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getBalanceAndTransactionsFailedForCamiproRequest:);
    [operation addObjectArgument:camiproRequest];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getStatsAndLoadingInfo:(CamiproRequest*)camiproRequest delegate:(id)delegate {
    if (![camiproRequest isKindOfClass:[CamiproRequest class]]) {
        @throw [NSException exceptionWithName:@"bad camiproRequest" reason:@"camiproRequest is either nil or not of class CamiproRequest" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getStatsAndLoadingInfo:);
    operation.delegateDidReturnSelector = @selector(getStatsAndLoadingInfoForCamiproRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getStatsAndLoadingInfoFailedForCamiproRequest:);
    [operation addObjectArgument:camiproRequest];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)sendLoadingInfoByEmail:(CamiproRequest*)camiproRequest delegate:(id)delegate {
    if (![camiproRequest isKindOfClass:[CamiproRequest class]]) {
        @throw [NSException exceptionWithName:@"bad camiproRequest" reason:@"camiproRequest is either nil or not of class CamiproRequest" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(sendLoadingInfoByEmail:);
    operation.delegateDidReturnSelector = @selector(sendLoadingInfoByEmailForCamiproRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(sendLoadingInfoByEmailFailedForCamiproRequest:);
    [operation addObjectArgument:camiproRequest];
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
