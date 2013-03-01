//
//  EventsService.m
//  PocketCampus
//
//

#import "EventsService.h"

#import "ObjectArchiver.h"

@interface EventsService ()

@property (nonatomic, strong) NSString* userToken;

@end

static NSString* kUserTokenKey = @"userToken";

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
    return [ObjectArchiver saveObject:kUserTokenKey forKey:kUserTokenKey andPluginName:@"events"];
}

- (BOOL)deleteUserToken {
    self.userToken = nil;
    return [ObjectArchiver saveObject:nil forKey:kUserTokenKey andPluginName:@"events"];
}

#pragma mark - Service methods

- (void)getEventItemForRequest:(EventItemRequest*)request delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getEventItem:);
    operation.delegateDidReturnSelector = @selector(getEventItemForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getEventItemFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}


- (void)getEventPoolForRequest:(EventPoolRequest*)request delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getEventPool:);
    operation.delegateDidReturnSelector = @selector(getEventPoolForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getEventPooolFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)exchangeContactsForRequest:(ExchangeRequest*)request delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(exchangeContacts:);
    operation.delegateDidReturnSelector = @selector(exchangeContactsForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(exchangeContactsFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
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
