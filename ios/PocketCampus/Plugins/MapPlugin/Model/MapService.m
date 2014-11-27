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


//  Created by Loïc Gardiol on 12.04.12.

#import "MapService.h"

NSString* const kMapRecentSearchesModifiedNotification = @"kMapRecentSearchesModifiedNotification";

NSString* const kMapSelectedMapLayerIdsModifiedNotificaiton = @"MapSelectedMapLayerIdsModifiedNotificaiton";

static NSString* const kRecentSearchesKey = @"recentSearches";
static NSUInteger const kMaxRecentSearches = 15;

static NSString* const kSelectedLayerIdsKey = @"selectedLayerIds";

@interface MapService ()

@property (nonatomic, strong) NSSet* selectedLayerIdsInternal;
@property (nonatomic, strong) NSMutableOrderedSet* recentSearchesInternal;

@end

@implementation MapService

static MapService* instance __weak = nil;

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"MapService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"map" thriftServiceClientClassName:NSStringFromClass(MapServiceClient.class)];
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

- (void)getLayersWithDelegate:(id)delegate {
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];    
    operation.keepInCache = YES;
    operation.keepInCacheBlock = ^BOOL(void* result) {
        MapLayersResponse* response = (__bridge id)result;
        return (response.statusCode == MapStatusCode_OK);
    };
    operation.cacheValidityInterval = 86400; //1 day
    operation.returnEvenStaleCacheIfNoInternetConnection = YES;
    operation.serviceClientSelector = @selector(getLayers);
    operation.delegateDidReturnSelector = @selector(getLayersDidReturn:);
    operation.delegateDidFailSelector = @selector(getLayersFailed);
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)searchFor:(NSString*)query delegate:(id)delegate {
    if (![query isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad query" reason:@"query is either nil or not of class NSString" userInfo:nil];
    }
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
    operation.cacheValidityInterval = 86400; //1 day
    operation.returnEvenStaleCacheIfNoInternetConnection = YES;
    operation.serviceClientSelector = @selector(search:);
    operation.delegateDidReturnSelector = @selector(searchMapFor:didReturn:);
    operation.delegateDidFailSelector = @selector(searchMapFailedFor:);
    [operation addObjectArgument:query];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

#pragma mark - MapLayers

- (NSSet*)selectedLayerIdsInternal {
    if (!_selectedLayerIdsInternal) {
        _selectedLayerIdsInternal = (NSSet*)[PCPersistenceManager objectForKey:kSelectedLayerIdsKey pluginName:@"map"];
    }
    if (!_selectedLayerIdsInternal) {
        _selectedLayerIdsInternal = [NSSet set];
    }
    return _selectedLayerIdsInternal;
}

- (NSSet*)selectedMapLayerIds {
    return self.selectedLayerIdsInternal;
}

- (void)setSelectedMapLayerIds:(NSSet *)selectedMapLayerIds {
    if (selectedMapLayerIds) { // selectedMapLayerIds is allower to be nil
        [PCUtils throwExceptionIfObject:selectedMapLayerIds notKindOfClass:[NSSet class]];
    }
    if ([self.selectedMapLayerIds isEqualToSet:selectedMapLayerIds]) {
        return;
    }
    self.selectedLayerIdsInternal = [selectedMapLayerIds copy];
    [PCPersistenceManager saveObject:self.selectedMapLayerIds forKey:kSelectedLayerIdsKey pluginName:@"map"];
    [[NSNotificationCenter defaultCenter] postNotificationName:kMapSelectedMapLayerIdsModifiedNotificaiton object:self];
}

#pragma mark - Recent searches

- (NSMutableOrderedSet*)recentSearchesInternal {
    if (!_recentSearchesInternal) {
        _recentSearchesInternal = [(NSOrderedSet*)[PCPersistenceManager objectForKey:kRecentSearchesKey pluginName:@"map" isCache:YES] mutableCopy]; //archived object are always returned as copy => immutable
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
    [PCPersistenceManager saveObject:self.recentSearchesInternal forKey:kRecentSearchesKey pluginName:@"map" isCache:YES];
    [[NSNotificationCenter defaultCenter] postNotificationName:kMapRecentSearchesModifiedNotification object:self];
}

- (void)clearRecentSearches {
    [self.recentSearchesInternal removeAllObjects];
    [PCPersistenceManager saveObject:nil forKey:kRecentSearchesKey pluginName:@"map" isCache:YES]; //deleted cached recent searches
    [[NSNotificationCenter defaultCenter] postNotificationName:kMapRecentSearchesModifiedNotification object:self];
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