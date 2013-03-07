//
//  EventsController.m
//  PocketCampus
//
//

#import "EventsController.h"

#import "EventPoolViewController.h"

#import "EventItemViewController.h"

static EventsController* instance __weak = nil;

@implementation EventsController

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"EventsController cannot be instancied more than once at a time, use sharedInstance instead." userInfo:nil];
        }
        self = [super init];
        if (self) {
        
            EventPoolViewController* rootPoolViewController = [[EventPoolViewController alloc] initAndLoadRootPool];
            
            PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:rootPoolViewController];
            navController.pluginIdentifier = [[self class] identifierName];
            self.mainNavigationController = navController;
            
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

- (UIViewController*)viewControllerForURLQueryAction:(NSString*)action parameters:(NSDictionary*)parameters {
    if ([action isEqualToString:@"showEventPool"]) {
        NSString* poolId = parameters[@"eventPoolId"];
        if (poolId) {
            //TODO support parameters userToken, exchangeToken and markAsFavorite
            
            return [[EventPoolViewController alloc] initAndLoadEventPoolWithId:[poolId longLongValue]];
        }
    } else if ([action isEqualToString:@"showEventItem"]) {
        NSString* eventId = parameters[@"eventItemId"];
        if (eventId) {
            return [[EventItemViewController alloc] initAndLoadEventItemWithId:[eventId longLongValue]];
        }
    } else {
        //no other supported actions
    }
    return nil;
}


+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"EventsPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Events";
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
