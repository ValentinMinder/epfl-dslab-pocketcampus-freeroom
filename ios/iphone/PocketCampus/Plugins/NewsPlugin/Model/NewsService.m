//
//  NewsService.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "NewsService.h"

@implementation NewsService

static NewsService* instance __weak = nil;

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"NewsService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"news"];
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
    return [[[NewsServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]] autorelease];
}

- (void)getNewsItemsForLanguage:(NSString*)language delegate:(id)delegate {
    if (![language isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad language" reason:@"language is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.customTimeout = 25.0; //fetch news can take much time
    operation.serviceClientSelector = @selector(getNewsItems:);
    operation.delegateDidReturnSelector = @selector(newsItemsForLanguage:didReturn:);
    operation.delegateDidFailSelector = @selector(newsItemsFailedForLanguage:);
    [operation addObjectArgument:language];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getNewsItemContentForId:(Id)newsItemId delegate:(id)delegate {
    //cannot check int
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
    operation.cacheValidity = 2419200; //seconds = 4 weeks. Could theoritically put more as News item is not likely to change
    operation.serviceClientSelector = @selector(getNewsItemContent:);
    operation.delegateDidReturnSelector = @selector(newsItemContentForId:didReturn:);
    operation.delegateDidFailSelector = @selector(newsItemContentFailedForId:);
    [operation addLongLongArgument:newsItemId];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
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
    [operationQueue addOperation:operation];
    [operation release];
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
