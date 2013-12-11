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

- (void)deleteMappingWithDummy:(NSString*)dummy delegate:(id)delegate; {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(deleteMapping:);
    operation.delegateDidReturnSelector = @selector(deleteMappingForDummy:didReturn:);
    operation.delegateDidFailSelector = @selector(deleteMappingFailedForDummy:);
    [operation addObjectArgument:dummy];
    operation.returnType = ReturnTypeInt;
    [self.operationQueue addOperation:operation];
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
