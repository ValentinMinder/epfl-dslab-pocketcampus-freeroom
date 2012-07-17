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

static CamiproService* instance = nil;

+ (id)sharedInstanceToRetain {
    if (instance != nil) {
        return instance;
    }
    @synchronized(self) {
        if (instance == nil) {
            instance = [[[self class] alloc] initWithServiceName:@"camipro"];
        }
    }
    return [instance autorelease];
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
    instance = nil;
    [super dealloc];
}

@end
