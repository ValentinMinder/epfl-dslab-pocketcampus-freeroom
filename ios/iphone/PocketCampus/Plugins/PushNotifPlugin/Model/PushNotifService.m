//
//  PushNotifService.m
//  PocketCampus
//
//

#import "PushNotifService.h"

@implementation PushNotifService

static PushNotifService* instance __weak = nil;

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"PushNotifService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"pushnotif"];
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
    return [[PushNotifServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]];
#else
    return [[[PushNotifServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]] autorelease];
#endif
}

- (void)getTequilaTokenForPushNotifWithDelegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getTequilaTokenForPushNotif);
    operation.delegateDidReturnSelector = @selector(getTequilaTokenForPushNotifDidReturn:);
    operation.delegateDidFailSelector = @selector(getTequilaTokenForPushNotifFailed);
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
}

- (void)registerPushNotif:(PushNotifRegReq*)request delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(registerPushNotif:);
    operation.delegateDidReturnSelector = @selector(registerPushNotifForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(registerPushNotifFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
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
