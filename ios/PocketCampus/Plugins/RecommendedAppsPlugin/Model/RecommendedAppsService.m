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

//  Created by Silviu Andrica on 18.08.2014.
 
#import "RecommendedAppsService.h"

static RecommendedAppsService* instance __weak = nil;

@implementation RecommendedAppsService

#pragma mark - Init

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"RecommendedAppsService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"recommendedapps" thriftServiceClientClassName:NSStringFromClass(RecommendedAppsServiceClient.class)];
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

#pragma mark - Service methods

- (void)getRecommendedApps:(RecommendedAppsRequest*)request delegate:(id)delegate{
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.skipCache = YES;
    operation.keepInCache = YES;
    operation.keepInCacheBlock = ^BOOL(void* result) {
        RecommendedAppsResponse* response = (__bridge id)result;
        return (response.status == RecommendedAppsResponseStatus_OK);
    };
    operation.serviceClientSelector = @selector(getRecommendedApps:); //corresponds to Thrift method definition
    operation.delegateDidReturnSelector = @selector(getRecommendedAppsForRequest:didReturn:); //corresponding *didReturn* definition
    operation.delegateDidFailSelector = @selector(getRecommendedAppsFailedForRequest:); //corresponding *Failed* definition
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject; //result type. Can be object or any standard primitive types (ReturnTypeInt, ...)
    [self.operationQueue addOperation:operation]; //schedule operation in background
}

#pragma mark - Cache versions

- (RecommendedAppsResponse*)getFromCacheRecommendedAppsForRequest:(RecommendedAppsRequest*)request {
    PCServiceRequest* operation = [[PCServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getRecommendedApps:); //corresponds to Thrift method definition
    operation.delegateDidReturnSelector = @selector(getRecommendedAppsForRequest:didReturn:); //corresponding *didReturn* definition
    operation.delegateDidFailSelector = @selector(getRecommendedAppsFailedForRequest:); //corresponding *Failed* definition
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject; //result type. Can be object or any standard primitive types (ReturnTypeInt, ...)
    return [operation cachedResponseObjectEvenIfStale:YES];
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
