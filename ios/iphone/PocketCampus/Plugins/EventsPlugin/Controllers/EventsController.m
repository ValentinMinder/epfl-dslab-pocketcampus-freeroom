//
//  EventsController.m
//  PocketCampus
//
//

#import "EventsController.h"

#import "EventPoolViewController.h"

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
            /*
             * TODO: init mainNavigationController OR mainSplitViewController.
             * On iPad, mainSplitViewController will be used if not nil, otherwise, mainNavigationController will be used
             *
             * Example: a plugin only providing a navigation controller
             *
             * MapViewController* mapViewController = [[MapViewController alloc] init];
             * PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:mapViewController];
             * navController.pluginIdentifier = [[self class] identifierName];
             * self.mainNavigationController = navController;
             *
             * Example: a plugin using a split view controller (=> iPad optimized)
             *
             * UINavigationController* masterNavigationController = ...
             * UIViewController* detailViewController = ...
             *
             * PluginSplitViewController* splitViewController = [[PluginSplitViewController alloc] initWithMasterViewController:masterNavigationController detailViewController:detailViewController];
             * splitViewController.delegate = self;
             *
             * self.mainSplitViewController = splitViewController;
             * self.mainSplitViewController.pluginIdentifier = [[self class] identifierName];
             *
             */
            
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
