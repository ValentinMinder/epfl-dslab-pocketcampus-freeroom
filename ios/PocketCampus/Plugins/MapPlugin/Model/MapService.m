//
//  MapService.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 12.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MapService.h"

#import "map.h"

#import "ObjectArchiver.h"

static NSString* kRecentSearchesKey = @"recentSearches";
static NSUInteger kMaxRecentSearches = 15;

@interface MapService ()

@property (nonatomic, strong) NSMutableOrderedSet* recentSearchesInternal;

@end

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
    return [[MapServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]];
}

- (void)getLayerListWithDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getLayerList);
    operation.delegateDidReturnSelector = @selector(getLayerListDidReturn:);
    operation.delegateDidFailSelector = @selector(getLayerListFailed:);
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)getLayerItemsForLayerId:(int64_t)layerId delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getLayerItems:);
    operation.delegateDidReturnSelector = @selector(getLayerItemsForLayerId:didReturn:);
    operation.delegateDidFailSelector = @selector(getLayerItemsFailedForLayerId:);
    [operation addIntArgument:layerId];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
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
}


#pragma mark - Recent searches

- (NSMutableOrderedSet*)recentSearchesInternal {
    if (!_recentSearchesInternal) {
        _recentSearchesInternal = [(NSOrderedSet*)[ObjectArchiver objectForKey:kRecentSearchesKey andPluginName:@"map" isCache:YES] mutableCopy]; //archived object are always returned as copy => immutable
    }
    if (!_recentSearchesInternal) {
        _recentSearchesInternal = [NSMutableOrderedSet orderedSet]; //guarentee non-nil empty ordered set
    }
    return _recentSearchesInternal;
}

- (NSOrderedSet*)recentSearches {
    return self.recentSearchesInternal;
}

- (void)addOrPromoteRecentSearch:(NSString*)pattern {
    NSUInteger currentIndex = [self.recentSearches indexOfObject:pattern];
    if (currentIndex == NSNotFound) { //this stupid logic needs to be done because there is no way to do in one step: add the object to top if it's not in the set already or move it if it is.
        [self.recentSearchesInternal insertObject:pattern atIndex:0]; // adding to top (works only if object not in set)
    } else {
        [self.recentSearchesInternal moveObjectsAtIndexes:[NSIndexSet indexSetWithIndex:currentIndex] toIndex:0]; //moving to top
    }
    
    /* Cleaning old results */
    if (self.recentSearches.count > kMaxRecentSearches) {
        [self.recentSearchesInternal removeObjectsInRange:NSMakeRange(kMaxRecentSearches, self.recentSearches.count - kMaxRecentSearches)];
    }
    [ObjectArchiver saveObject:self.recentSearchesInternal forKey:kRecentSearchesKey andPluginName:@"map" isCache:YES];
    [[NSNotificationCenter defaultCenter] postNotificationName:kMapRecentSearchesModifiedNotificationName object:self];
}

- (void)clearRecentSearches {
    [self.recentSearchesInternal removeAllObjects];
    [ObjectArchiver saveObject:nil forKey:kRecentSearchesKey andPluginName:@"map" isCache:YES]; //deleted cached recent searches
    [[NSNotificationCenter defaultCenter] postNotificationName:kMapRecentSearchesModifiedNotificationName object:self];
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