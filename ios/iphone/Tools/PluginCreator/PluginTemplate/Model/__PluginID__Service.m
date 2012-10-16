//
//  __PluginID__Service.m
//  PocketCampus
//
//

#import "__PluginID__Service.h"

@implementation __PluginID__Service

static __PluginID__Service* instance = nil;

+ (id)sharedInstanceToRetain {
    if (instance != nil) {
        return instance;
    }
    @synchronized(self) {
        if (instance == nil) {
            instance = [[[self class] alloc] initWithServiceName:@"__PluginID_low__"];
        }
    }
    return [instance autorelease];
}

- (id)thriftServiceClientInstance {
    return [[[__PluginID__ServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]] autorelease];
}

//TODO: implement service methods defined in header

- (void)dealloc
{
    instance = nil;
    [super dealloc];
}

@end
