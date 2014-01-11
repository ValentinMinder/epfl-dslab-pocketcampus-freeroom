





#import "IsAcademiaController.h"

#import "IsAcademiaService.h"

static IsAcademiaController* instance __weak = nil;

@interface IsAcademiaController ()

@property (nonatomic, strong) IsAcademiaService* moodleService;

@end

@implementation IsAcademiaController

#pragma mark - Init

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"IsAcademiaController cannot be instancied more than once at a time, use sharedInstance instead." userInfo:nil];
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
            instance = self;
        }
        return self;
    }
}

#pragma mark - PluginControllerProtocol

+ (id)sharedInstanceToRetain {
    @synchronized (self) {
        if (instance) {
            return instance;
        }
        return [[[self class] alloc] init];
    }
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"IsAcademiaPlugin", @"");
}

+ (NSString*)identifierName {
    return @"IsAcademia";
}

#pragma mark - Dealloc

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
}

@end
