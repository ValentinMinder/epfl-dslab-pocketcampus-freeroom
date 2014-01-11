

//  Created by Loïc Gardiol on 23.03.12.


#import "TransportController.h"

#import "TransportNextDeparturesViewController.h"

#import "PCObjectArchiver.h"

@implementation TransportController

static TransportController* instance __weak = nil;


- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"TransportController cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super init];
        if (self) {
            TransportNextDeparturesViewController* nextDeparturesListViewController = [[TransportNextDeparturesViewController alloc] init];
            nextDeparturesListViewController.title = [[self class] localizedName];
            PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:nextDeparturesListViewController];
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
    return NSLocalizedStringFromTable(@"PluginName", @"TransportPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Transport";
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
