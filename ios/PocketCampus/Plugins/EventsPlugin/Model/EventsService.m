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


#import "EventsService.h"

#import "EventsUtils.h"

NSString* const kEventsURLActionShowEventPool = @"showEventPool";
NSString* const kEventsURLActionShowEventItem = @"showEventItem";

NSString* const kEventsURLParameterEventPoolId = @"eventPoolId";
NSString* const kEventsURLParameterEventItemId = @"eventItemId";
NSString* const kEventsURLParameterMarkFavoriteEventItemId = @"markFavorite";
NSString* const kEventsURLParameterUserTicket = @"userTicket";
NSString* const kEventsURLParameterExchangeToken = @"exchangeToken";

NSString* const kEventsFavoritesEventItemsUpdatedNotification = @"kFavoritesEventItemsUpdatedNotification";

@interface EventsService ()

@property (nonatomic, strong) NSMutableSet* userTickets;
@property (nonatomic, strong) NSMutableSet* favoriteEventItemIds; //set of NSNumber int64_t

@end

static NSString* const kUserTicketsKey = @"userTickets";

static NSString* const kFavoriteEventItemIds = @"favoriteEventItemIds";

static NSString* const kPoolPeriodKey = @"poolPeriod";

@implementation EventsService

static EventsService* instance __weak = nil;

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"EventsService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"events" thriftServiceClientClassName:NSStringFromClass(EventsServiceClient.class)];
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

#pragma mark - User tickets

static NSString* const kUserTokenOldKey = @"userToken";

- (void)initUserTickets {
    if (!self.userTickets) { //first try to get it from persistent storage
        self.userTickets = [(NSSet*)[PCPersistenceManager objectForKey:kUserTicketsKey pluginName:@"events"] mutableCopy];
    }
    if (!self.userTickets) { //if not present in persistent storage, create set
        self.userTickets = [NSMutableSet set];
    }
    
    static NSString* const kEventsTransitionToUserTicketsDone = @"EventsTransitionToUserTicketsDone";
    NSUserDefaults* defaults = [PCPersistenceManager userDefaultsForPluginName:@"events"];
    if (![defaults boolForKey:kEventsTransitionToUserTicketsDone]) {
        NSString* userToken = (NSString*)[PCPersistenceManager objectForKey:kUserTokenOldKey pluginName:@"events"];
        if (userToken) {
            //transition period, get back old tokens
            [self.userTickets addObject:userToken];
            [PCPersistenceManager saveObject:nil forKey:kUserTokenOldKey pluginName:@"events"];
        }
        [defaults setBool:YES forKey:kEventsTransitionToUserTicketsDone];
    }
}

- (void)addUserTicket:(NSString*)ticket {
    CLSNSLog(@"-> Add user ticket: %@", ticket);
    [self initUserTickets];
    [self.userTickets addObject:ticket];
    [self persistUserTickets];
}

- (void)removeUserTicket:(NSString*)ticket {
    CLSNSLog(@"-> Remove user ticket: %@", ticket);
    [self initUserTickets];
    [self.userTickets removeObject:ticket];
    [self persistUserTickets];
}

- (NSArray*)allUserTickets {
    [self initUserTickets];
    return [self.userTickets allObjects];
}

- (BOOL)persistUserTickets {
    if (!self.userTickets) {
        return YES;
    }
    return [PCPersistenceManager saveObject:self.userTickets forKey:kUserTicketsKey pluginName:@"events"];
}

#pragma mark - Favorites

- (void)initFavorites {
    if (!self.favoriteEventItemIds) { //first try to get it from persistent storage
        self.favoriteEventItemIds = [(NSSet*)[PCPersistenceManager objectForKey:kFavoriteEventItemIds pluginName:@"events"] mutableCopy];
    }
    if (!self.favoriteEventItemIds) { //if not present in persistent storage, create set
        self.favoriteEventItemIds = [NSMutableSet set];
    }
}

- (BOOL)persistFavorites {
    if (!self.favoriteEventItemIds) {
        return YES;
    }
    return [PCPersistenceManager saveObject:self.favoriteEventItemIds forKey:kFavoriteEventItemIds pluginName:@"events"];
}

- (void)addFavoriteEventItemId:(int64_t)itemId {
    [self initFavorites];
    [self.favoriteEventItemIds addObject:[EventsUtils nsNumberForEventId:itemId]];
    [self persistFavorites];
    NSNotification* notif = [NSNotification notificationWithName:kEventsFavoritesEventItemsUpdatedNotification object:self];
    [[NSNotificationCenter defaultCenter] postNotification:notif];
}

- (void)removeFavoriteEventItemId:(int64_t)itemId {
    [self initFavorites];
    [self.favoriteEventItemIds removeObject:[EventsUtils nsNumberForEventId:itemId]];
    [self persistFavorites];
    NSNotification* notif = [NSNotification notificationWithName:kEventsFavoritesEventItemsUpdatedNotification object:self];
    [[NSNotificationCenter defaultCenter] postNotification:notif];
}

- (NSArray*)allFavoriteEventItemIds {
    [self initFavorites];
    return [self.favoriteEventItemIds allObjects];
}

- (BOOL)isEventItemIdFavorite:(int64_t)itemId {
    [self initFavorites];
    return [self.favoriteEventItemIds containsObject:[EventsUtils nsNumberForEventId:itemId]];
}

#pragma mark - EventItem picture saving


/*- (NSString*)keyForPictureDataForEventItem:(EventItem*)eventItem {
    return [NSString stringWithFormat:kEventItemPictureKeyWithFormat, eventItem.eventId];
}

- (NSData*)pictureDataForEventItem:(EventItem*)eventItem {
    return (NSData*)[ObjectArchiver objectForKey:[self keyForPictureDataForEventItem:eventItem] andPluginName:@"events" isCache:YES];
}

- (BOOL)savePictureData:(NSData*)imageData forEventItem:(EventItem*)eventItem {
    NSString* savePath = [ObjectArchiver pathForKey:[self keyForPictureDataForEventItem:eventItem] pluginName:@"events" customFileExtension:@"jpg" isCache:YES];
    NSData* jpgData = UIImageJPEGRepresentation([UIImage imageWithData:imageData], 1.0);
    return [jpgData writeToFile:savePath atomically:NO];
}*/

#pragma mark - Events period to display

- (int32_t)lastSelectedPoolPeriod {
    NSNumber* period = (NSNumber*)[PCPersistenceManager objectForKey:kPoolPeriodKey pluginName:@"events"];
    if (period) {
        return [period intValue];
    }
    return 0;
}
- (BOOL)saveSelectedPoolPeriod:(int32_t)period {
    return [PCPersistenceManager saveObject:[NSNumber numberWithInt:period] forKey:kPoolPeriodKey pluginName:@"events"];
}

#pragma mark - Service methods

- (void)getEventItemForRequest:(EventItemRequest*)request delegate:(id)delegate {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[EventItemRequest class]];
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
    operation.keepInCacheBlock = ^BOOL(void* result) {
        EventItemReply* reply = (__bridge id)result;
        return reply.status == 200;
    };
    operation.returnEvenStaleCacheIfNoInternetConnection = YES;
    operation.cacheValidityInterval = 600; //10 min
    operation.serviceClientSelector = @selector(getEventItem:);
    operation.delegateDidReturnSelector = @selector(getEventItemForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getEventItemFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getEventPoolForRequest:(EventPoolRequest*)request delegate:(id)delegate {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[EventPoolRequest class]];
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
    operation.keepInCacheBlock = ^BOOL(void* result) {
        EventPoolReply* reply = (__bridge id)result;
        return reply.status == 200;
    };
    operation.skipCache = YES;
    operation.serviceClientSelector = @selector(getEventPool:);
    operation.delegateDidReturnSelector = @selector(getEventPoolForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getEventPoolFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)exchangeContactsForRequest:(ExchangeRequest*)request delegate:(id)delegate {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[ExchangeRequest class]];
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(exchangeContacts:);
    operation.delegateDidReturnSelector = @selector(exchangeContactsForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(exchangeContactsFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)sendStarredItemsByEmail:(SendEmailRequest *)request delegate:(id)delegate {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[SendEmailRequest class]];
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(sendStarredItemsByEmail:);
    operation.delegateDidReturnSelector = @selector(sendStarredItemsByEmailForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(sendStarredItemsByEmailFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

#pragma mark Cached

- (EventPoolReply*)getFromCacheEventPoolForRequest:(EventPoolRequest*)request {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[EventPoolRequest class]];
    PCServiceRequest* operation = [[PCServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getEventPool:);
    operation.delegateDidReturnSelector = @selector(getEventPoolForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getEventPoolFailedForRequest:);
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
