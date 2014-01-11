

//  Created by Loïc Gardiol on 08.03.12.


#import "FoodController.h"
#import "FoodRestaurantsListViewController.h"
#import "FoodSplashDetailViewController.h"

static FoodController* instance __weak = nil;

@interface FoodController ()<UISplitViewControllerDelegate>

@end

@implementation FoodController

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"FoodController cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super init];
        if (self) {
            FoodRestaurantsListViewController* restaurantsListViewController = [[FoodRestaurantsListViewController alloc] init];
            restaurantsListViewController.title = [[self class] localizedName];
            if ([PCUtils isIdiomPad]) {
                PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:restaurantsListViewController];
                FoodSplashDetailViewController* splashDetailViewController = [FoodSplashDetailViewController new];
                PluginSplitViewController* splitViewController = [[PluginSplitViewController alloc] initWithMasterViewController:navController detailViewController:[[PCNavigationController alloc] initWithRootViewController:splashDetailViewController]];
                splitViewController.pluginIdentifier = [[self class] identifierName];
                splitViewController.delegate = self;
                self.mainSplitViewController = splitViewController;
            } else {
                PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:restaurantsListViewController];
                navController.pluginIdentifier = [[self class] identifierName];
                self.mainNavigationController = navController;
            }
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
    return NSLocalizedStringFromTable(@"PluginName", @"FoodPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Food";
}

- (NSString*)localizedStringForKey:(NSString*)key {
    return NSLocalizedStringFromTable(key, [[self class] identifierName], @"");
}

#pragma mark - UISplitViewControllerDelegate

- (BOOL)splitViewController:(UISplitViewController *)svc shouldHideViewController:(UIViewController *)vc inOrientation:(UIInterfaceOrientation)orientation {
    return NO;
}

#pragma mark - Dealloc

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
