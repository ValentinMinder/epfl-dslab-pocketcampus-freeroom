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




//  Created by Loïc Gardiol on 05.05.12.


#import "NewsService.h"

@implementation NewsService

static NewsService* instance __weak = nil;

#pragma mark - Init

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"NewsService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"news" thriftServiceClientClassName:NSStringFromClass(NewsServiceClient.class)];
        if (self) {
            instance = self;
        }
        return self;
    }
}

#pragma mark - Service overrides

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

- (void)getAllFeedsForRequest:(NewsFeedsRequest*)request delegate:(id<NewsServiceDelegate>)delegate {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[NewsFeedsRequest class]];
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.skipCache = YES;
    operation.keepInCache = YES;
    operation.keepInCacheBlock = ^BOOL(void* returnedValue) {
        NewsFeedsResponse* response = (__bridge id)returnedValue;
        return (response.statusCode == NewsStatusCode_OK);
    };
    operation.serviceClientSelector = @selector(getAllFeeds:);
    operation.delegateDidReturnSelector = @selector(getAllFeedsForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getAllFeedsFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getFeedItemContentForRequest:(NewsFeedItemContentRequest*)request delegate:(id<NewsServiceDelegate>)delegate {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[NewsFeedItemContentRequest class]];
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
    operation.keepInCacheBlock = ^BOOL(void* returnedValue) {
        NewsFeedItemContentResponse* response = (__bridge id)returnedValue;
        return (response.statusCode == NewsStatusCode_OK);
    };
    operation.cacheValidityInterval = 2419200; //seconds = 4 weeks. Could theoritically put more as content is not likely to change
    operation.serviceClientSelector = @selector(getFeedItemContent:);
    operation.delegateDidReturnSelector = @selector(getFeedItemContentForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getFeedItemContentFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}


#pragma mark - Cached versions

- (NSArray*)getFromCacheAllFeedsForRequest:(NewsFeedsRequest*)request {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[NewsFeedsRequest class]];
    ServiceRequest* operation = [[ServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getAllFeeds:);
    operation.delegateDidReturnSelector = @selector(getAllFeedsForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getAllFeedsFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
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
