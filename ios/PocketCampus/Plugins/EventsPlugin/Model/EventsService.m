//
//  EventsService.m
//  PocketCampus
//
//

#import "EventsService.h"

#import "PCObjectArchiver.h"

#import "EventsUtils.h"

#import "PCUtils.h"

@interface EventsService ()

@property (nonatomic, strong) NSMutableSet* userTickets;
@property (nonatomic, strong) NSMutableSet* favoriteEventItemIds; //set of NSNumber int64_t

@end

static NSString* kUserTicketsKey = @"userTickets";

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

static NSString* kUserTokenKey = @"userToken";

- (void)initUserTickets {
    if (!self.userTickets) { //first try to get it from persistent storage
        self.userTickets = [(NSSet*)[PCObjectArchiver objectForKey:kUserTicketsKey andPluginName:@"events"] mutableCopy];
    }
    if (!self.userTickets) { //if not present in persistent storage, create set
        self.userTickets = [NSMutableSet set];
    }
    
    static NSString* kEventsTransitionToUserTicketsDone = @"EventsTransitionToUserTicketsDone";
    NSUserDefaults* defaults = [NSUserDefaults standardUserDefaults];
    if (![defaults boolForKey:kEventsTransitionToUserTicketsDone]) {
        NSString* userToken = (NSString*)[PCObjectArchiver objectForKey:kUserTokenKey andPluginName:@"events"];
        if (userToken) {
            //transition period, get back old tokens
            [self.userTickets addObject:userToken];
            [PCObjectArchiver saveObject:nil forKey:kUserTokenKey andPluginName:@"events"];
        }
        [defaults setBool:YES forKey:kEventsTransitionToUserTicketsDone];
    }
}

- (void)addUserTicket:(NSString*)ticket {
    NSLog(@"-> Add user ticket: %@", ticket);
    [self initUserTickets];
    [self.userTickets addObject:ticket];
    [self persistUserTickets];
}

- (void)removeUserTicket:(NSString*)ticket {
    NSLog(@"-> Remove user ticket: %@", ticket);
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
    return [PCObjectArchiver saveObject:self.userTickets forKey:kUserTicketsKey andPluginName:@"events"];
}

#pragma mark - Favorites

- (void)initFavorites {
    if (!self.favoriteEventItemIds) { //first try to get it from persistent storage
        self.favoriteEventItemIds = [(NSSet*)[PCObjectArchiver objectForKey:kFavoriteEventItemIds andPluginName:@"events"] mutableCopy];
    }
    if (!self.favoriteEventItemIds) { //if not present in persistent storage, create set
        self.favoriteEventItemIds = [NSMutableSet set];
    }
}

- (BOOL)persistFavorites {
    if (!self.favoriteEventItemIds) {
        return YES;
    }
    return [PCObjectArchiver saveObject:self.favoriteEventItemIds forKey:kFavoriteEventItemIds andPluginName:@"events"];
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
    NSNumber* period = (NSNumber*)[PCObjectArchiver objectForKey:kPoolPeriodKey andPluginName:@"events"];
    if (period) {
        return [period intValue];
    }
    return 0;
}
- (BOOL)saveSelectedPoolPeriod:(int32_t)period {
    return [PCObjectArchiver saveObject:[NSNumber numberWithInt:period] forKey:kPoolPeriodKey andPluginName:@"events"];
}

#pragma mark - Service methods

- (void)getEventItemForRequest:(EventItemRequest*)request delegate:(id)delegate {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[EventItemRequest class]];
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
    operation.returnEvenStaleCacheIfNoInternetConnection = YES;
    operation.cacheValidityInterval = 43200; //half-day
//#warning TO REMOVE
    //operation.skipCache = YES;
    //operation.cacheValidityInterval = 432000;
    operation.serviceClientSelector = @selector(getEventItem:);
    operation.delegateDidReturnSelector = @selector(getEventItemForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getEventItemFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getEventPoolForRequest:(EventPoolRequest*)request delegate:(id)delegate {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[EventPoolRequest class]];
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
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
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(exchangeContacts:);
    operation.delegateDidReturnSelector = @selector(exchangeContactsForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(exchangeContactsFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)sendStarredItemsByEmail:(SendEmailRequest *)request delegate:(id)delegate {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[SendEmailRequest class]];
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(sendStarredItemsByEmail:);
    operation.delegateDidReturnSelector = @selector(sendStarredItemsByEmailForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(sendStarredItemsByEmailFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (EventPoolReply*)getFromCacheEventPoolForRequest:(EventPoolRequest*)request {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[EventPoolRequest class]];
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
