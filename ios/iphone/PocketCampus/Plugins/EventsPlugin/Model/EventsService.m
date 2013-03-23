//
//  EventsService.m
//  PocketCampus
//
//

#import "EventsService.h"

#import "ObjectArchiver.h"

#import "EventsUtils.h"

#import "PCUtils.h"

@interface EventsService ()

@property (nonatomic, strong) NSString* userToken;
@property (nonatomic, strong) NSMutableSet* favoriteEventItemIds; //set of NSNumber int64_t

@end

static NSString* kUserTokenKey = @"userToken";

static NSString* kFavoriteEventItemIds = @"favoriteEventItemIds";

//static NSString* kEventItemPictureKeyWithFormat = @"picture-eventItem-%ld";

static NSString* kPoolPeriodKey = @"poolPeriod";

@implementation EventsService

static EventsService* instance __weak = nil;

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"EventsService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"events"];
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
#if __has_feature(objc_arc)
    return [[EventsServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]];
#else
    return [[[EventsServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]] autorelease];
#endif
}

#pragma mark - User token

- (NSString*)lastUserToken {
    if (!self.userToken) {
        self.userToken = (NSString*)[ObjectArchiver objectForKey:kUserTokenKey andPluginName:@"events"];
    }
    return self.userToken;
}

- (BOOL)saveUserToken:(NSString*)token {
    self.userToken = token;
    return [ObjectArchiver saveObject:token forKey:kUserTokenKey andPluginName:@"events"];
}

- (BOOL)deleteUserToken {
    self.userToken = nil;
    return [ObjectArchiver saveObject:nil forKey:kUserTokenKey andPluginName:@"events"];
}

#pragma mark - Favorites

- (void)initFavorites {
    if (!self.favoriteEventItemIds) { //first try to get it from persistent storage
        self.favoriteEventItemIds = [(NSSet*)[ObjectArchiver objectForKey:kFavoriteEventItemIds andPluginName:@"events"] mutableCopy];
    }
    if (!self.favoriteEventItemIds) { //if not present in persistent storage, create set
        self.favoriteEventItemIds = [NSMutableSet set];
    }
}

- (BOOL)persistFavorites {
    if (!self.favoriteEventItemIds) {
        return YES;
    }
    return [ObjectArchiver saveObject:self.favoriteEventItemIds forKey:kFavoriteEventItemIds andPluginName:@"events"];
}

- (void)addFavoriteEventItemId:(int64_t)itemId {
    [self initFavorites];
    [self.favoriteEventItemIds addObject:[EventsUtils nsNumberForEventId:itemId]];
    [self persistFavorites];
    NSNotification* notif = [NSNotification notificationWithName:kFavoritesEventItemsUpdatedNotification object:self];
    [[NSNotificationCenter defaultCenter] postNotification:notif];
}

- (void)removeFavoriteEventItemId:(int64_t)itemId {
    [self initFavorites];
    [self.favoriteEventItemIds removeObject:[EventsUtils nsNumberForEventId:itemId]];
    [self persistFavorites];
    NSNotification* notif = [NSNotification notificationWithName:kFavoritesEventItemsUpdatedNotification object:self];
    [[NSNotificationCenter defaultCenter] postNotification:notif];
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
    NSNumber* period = (NSNumber*)[ObjectArchiver objectForKey:kPoolPeriodKey andPluginName:@"events"];
    if (period) {
        return [period intValue];
    }
    return 0;
}
- (BOOL)saveSelectedPoolPeriod:(int32_t)period {
    return [ObjectArchiver saveObject:[NSNumber numberWithInt:period] forKey:kPoolPeriodKey andPluginName:@"events"];
}

#pragma mark - Service methods

- (void)getEventItemForRequest:(EventItemRequest*)request delegate:(id)delegate {
    [PCUtils throughExceptionIfObject:request notKindOfClass:[EventItemRequest class]];
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
    operation.returnEvenStaleCacheIfServerIsUnreachable = YES;
    operation.cacheValidity = 43200; //half-day
    operation.serviceClientSelector = @selector(getEventItem:);
    operation.delegateDidReturnSelector = @selector(getEventItemForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getEventItemFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)getEventPoolForRequest:(EventPoolRequest*)request delegate:(id)delegate {
    [PCUtils throughExceptionIfObject:request notKindOfClass:[EventPoolRequest class]];
    //NSLog(@"original: %@", request);
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
    operation.skipCache = YES;
    operation.serviceClientSelector = @selector(getEventPool:);
    operation.delegateDidReturnSelector = @selector(getEventPoolForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getEventPoolFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)exchangeContactsForRequest:(ExchangeRequest*)request delegate:(id)delegate {
    [PCUtils throughExceptionIfObject:request notKindOfClass:[ExchangeRequest class]];
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(exchangeContacts:);
    operation.delegateDidReturnSelector = @selector(exchangeContactsForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(exchangeContactsFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (EventPoolReply*)getFromCacheEventPoolForRequest:(EventPoolRequest*)request {
    [PCUtils throughExceptionIfObject:request notKindOfClass:[EventPoolRequest class]];
    //NSLog(@"cached: %@", request);
    ServiceRequest* operation = [[ServiceRequest alloc] initForCachedResponseOnlyWithService:self];
    operation.serviceClientSelector = @selector(getEventPool:);
    operation.delegateDidReturnSelector = @selector(getEventPoolForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getEventPoolFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    return [operation cachedResponseObjectEvenIfStale:YES];
}

#pragma mark - dealloc

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
