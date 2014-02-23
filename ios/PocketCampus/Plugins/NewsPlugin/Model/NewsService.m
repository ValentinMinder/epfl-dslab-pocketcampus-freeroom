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

- (void)getNewsItemsForLanguage:(NSString*)language delegate:(id)delegate {
    if (![language isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad language" reason:@"language is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
    operation.skipCache = YES;
    operation.serviceClientSelector = @selector(getNewsItems:);
    operation.delegateDidReturnSelector = @selector(newsItemsForLanguage:didReturn:);
    operation.delegateDidFailSelector = @selector(newsItemsFailedForLanguage:);
    [operation addObjectArgument:language];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getNewsItemContentForId:(int64_t)newsItemId delegate:(id)delegate {
    //cannot check int
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
    operation.cacheValidityInterval = 2419200; //seconds = 4 weeks. Could theoritically put more as News item is not likely to change
    operation.serviceClientSelector = @selector(getNewsItemContent:);
    operation.delegateDidReturnSelector = @selector(newsItemContentForId:didReturn:);
    operation.delegateDidFailSelector = @selector(newsItemContentFailedForId:);
    [operation addLongLongArgument:newsItemId];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getFeedUrlsForLanguage:(NSString*)language delegate:(id)delegate {
    if (![language isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad language" reason:@"language is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getFeedUrls:);
    operation.delegateDidReturnSelector = @selector(feedUrlsForLanguage:didReturn:);
    operation.delegateDidFailSelector = @selector(feedUrlsFailedForLanguage:);
    [operation addObjectArgument:language];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getFeedsForLanguage:(NSString*)language delegate:(id)delegate {
    if (![language isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad language" reason:@"language is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getFeeds:);
    operation.delegateDidReturnSelector = @selector(feedsForLanguage:didReturn:);
    operation.delegateDidFailSelector = @selector(feedsFailedForLanguage:);
    [operation addObjectArgument:language];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

#pragma mark - Cached

- (NSArray*)getFromCacheNewsItemsForLanguage:(NSString*)language {
    if (![language isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad language" reason:@"language is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getNewsItems:);
    operation.delegateDidReturnSelector = @selector(newsItemsForLanguage:didReturn:);
    operation.delegateDidFailSelector = @selector(newsItemsFailedForLanguage:);
    [operation addObjectArgument:language];
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
