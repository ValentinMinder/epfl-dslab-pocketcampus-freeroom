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




//  Created by Loïc Gardiol on 17.05.12.


#import "CamiproService.h"

#import "PCPersistenceManager.h"

static CamiproService* instance __weak = nil;

@interface CamiproService ()

@property (nonatomic, strong) CamiproSession* camiproSession;

@end

@implementation CamiproService

#pragma mark - Init

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"CamiproService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"camipro" thriftServiceClientClassName:NSStringFromClass(CamiproServiceClient.class)];
        if (self) {
            instance = self;
        }
        return self;
    }
}

#pragma mark - ServiceProtocol

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

#pragma mark - Properties

static NSString* const kCamiproSession = @"camiproSession";

- (CamiproSession*)camiproSession {
    if (_camiproSession) {
        return _camiproSession;
    }
    _camiproSession = (CamiproSession*)[PCPersistenceManager objectForKey:kCamiproSession pluginName:@"camipro"];
    return _camiproSession;
}

- (void)setCamiproSession:(CamiproSession *)camiproSession persist:(BOOL)persist {
    _camiproSession = camiproSession;
    if (persist) {
        [PCPersistenceManager saveObject:camiproSession forKey:kCamiproSession pluginName:@"camipro"];
    } else {
        [PCPersistenceManager saveObject:nil forKey:kCamiproSession pluginName:@"camipro"];
    }
}

- (void)deleteCamiproSession {
    [self setCamiproSession:nil persist:YES];
}

#pragma mark - Service methods

- (void)getTequilaTokenForCamiproDelegate:(id)delegate {
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getTequilaTokenForCamipro);
    operation.delegateDidReturnSelector = @selector(getTequilaTokenForCamiproDidReturn:);
    operation.delegateDidFailSelector = @selector(getTequilaTokenForCamiproFailed);
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken*)tequilaKey delegate:(id)delegate {
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getCamiproSession:);
    operation.delegateDidReturnSelector = @selector(getSessionIdForServiceWithTequilaKey:didReturn:);
    operation.delegateDidFailSelector = @selector(getSessionIdForServiceFailedForTequilaKey:);
    [operation addObjectArgument:tequilaKey];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getBalanceAndTransactions:(CamiproRequest*)camiproRequest delegate:(id)delegate {
    if (![camiproRequest isKindOfClass:[CamiproRequest class]]) {
        @throw [NSException exceptionWithName:@"bad camiproRequest" reason:@"camiproRequest is either nil or not of class CamiproRequest" userInfo:nil];
    }
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getBalanceAndTransactions:);
    operation.delegateDidReturnSelector = @selector(getBalanceAndTransactionsForCamiproRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getBalanceAndTransactionsFailedForCamiproRequest:);
    [operation addObjectArgument:camiproRequest];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getStatsAndLoadingInfo:(CamiproRequest*)camiproRequest delegate:(id)delegate {
    if (![camiproRequest isKindOfClass:[CamiproRequest class]]) {
        @throw [NSException exceptionWithName:@"bad camiproRequest" reason:@"camiproRequest is either nil or not of class CamiproRequest" userInfo:nil];
    }
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getStatsAndLoadingInfo:);
    operation.delegateDidReturnSelector = @selector(getStatsAndLoadingInfoForCamiproRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getStatsAndLoadingInfoFailedForCamiproRequest:);
    [operation addObjectArgument:camiproRequest];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)sendLoadingInfoByEmail:(CamiproRequest*)camiproRequest delegate:(id)delegate {
    if (![camiproRequest isKindOfClass:[CamiproRequest class]]) {
        @throw [NSException exceptionWithName:@"bad camiproRequest" reason:@"camiproRequest is either nil or not of class CamiproRequest" userInfo:nil];
    }
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(sendLoadingInfoByEmail:);
    operation.delegateDidReturnSelector = @selector(sendLoadingInfoByEmailForCamiproRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(sendLoadingInfoByEmailFailedForCamiproRequest:);
    [operation addObjectArgument:camiproRequest];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
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
