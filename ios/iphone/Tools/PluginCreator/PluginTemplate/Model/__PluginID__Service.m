//
//  __PluginID__Service.m
//  PocketCampus
//
//

#import "__PluginID__Service.h"

@implementation __PluginID__Service

static __PluginID__Service* instance __weak = nil;

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"__PluginID__Service cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"__PluginID_low__"];
        if (self) {
            instance = self;
        }
        return self;
    }
}

+ (id)sharedInstance {
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
    return [[__PluginID__ServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]];
#else
    return [[[__PluginID__ServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]] autorelease];
#endif
}

//TODO: implement service methods defined in header

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
