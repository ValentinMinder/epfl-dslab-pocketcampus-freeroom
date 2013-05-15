//
//  EventsController.m
//  PocketCampus
//
//

#import "EventsController.h"

#import "EventPoolViewController.h"

#import "EventItemViewController.h"

#import "PCUtils.h"

#import "EventsSplashDetailViewController.h"

#import "EventsService.h"

static EventsController* instance __weak = nil;

@interface EventsController ()

@property (nonatomic, strong) EventsService* eventsService;

@end

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
            
            if ([PCUtils isIdiomPad]) {
                UINavigationController* navController =  [[UINavigationController alloc] initWithRootViewController:rootPoolViewController];
                EventsSplashDetailViewController* splashDetailViewController = [[EventsSplashDetailViewController alloc] init];
                PluginSplitViewController* splitViewController = [[PluginSplitViewController alloc] initWithMasterViewController:navController detailViewController:splashDetailViewController];
                splitViewController.pluginIdentifier = [[self class] identifierName];
                splitViewController.delegate = self;
                self.mainSplitViewController = splitViewController;
            } else {
                PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:rootPoolViewController];
                navController.pluginIdentifier = [[self class] identifierName];
                self.mainNavigationController = navController;
            }
            
            self.eventsService = [EventsService sharedInstanceToRetain];
            
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

- (BOOL)handleURLQueryAction:(NSString *)action parameters:(NSDictionary *)parameters {
    
    BOOL foundSilent = [self handleSilentParameters:parameters];
    
    UIViewController* viewController = [self viewControllerForURLQueryAction:action parameters:parameters handleSilent:NO];
    
    if (!viewController) {
        if (foundSilent) {
            return YES;
        }
        return NO;
    }
    
    UINavigationController* navController = nil;
    
    if ([PCUtils isIdiomPad]) {
        navController = self.mainSplitViewController.viewControllers[1];
        if ([navController isKindOfClass:[UINavigationController class]]) {
            navController = [[UINavigationController alloc] initWithRootViewController:viewController];
            self.mainSplitViewController.viewControllers = @[self.mainSplitViewController.viewControllers[0], navController];
        }
    } else {
        navController = self.mainNavigationController;
    }
    if (!viewController.navigationController) {
        [navController pushViewController:viewController animated:YES];
    }
    return YES;
}

- (UIViewController*)viewControllerForURLQueryAction:(NSString*)action parameters:(NSDictionary*)parameters {
    return [self viewControllerForURLQueryAction:action parameters:parameters handleSilent:YES];
}


+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"EventsPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Events";
}

#pragma mark - EventsServiceDelegate

- (void)exchangeContactsForRequest:(ExchangeRequest *)request didReturn:(ExchangeReply *)reply {
    switch (reply.status) {
        case 200:
            //perfect
            break;
        case 400:
            [self showExchangeContactError];
            break;
        case 500:
            [self exchangeContactsFailedForRequest:request];
        default:
            break;
    }
}

- (void)exchangeContactsFailedForRequest:(ExchangeRequest *)request {
    [PCUtils showServerErrorAlert];
}

#pragma mark - Private

- (void)showExchangeContactError {
    [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ExchangeContactErrorMessage", @"EventsPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
}


- (UIViewController*)viewControllerForURLQueryAction:(NSString*)action parameters:(NSDictionary*)parameters handleSilent:(BOOL)handleSilent {
    
    UIViewController* viewController = nil;
    
    if (handleSilent) {
        [self handleSilentParameters:parameters];
    }
    
    if ([action isEqualToString:@"showEventPool"]) {
        NSString* poolId = parameters[@"eventPoolId"];
        if (poolId) {
            viewController = [[EventPoolViewController alloc] initAndLoadEventPoolWithId:[poolId longLongValue]];
        }
    } else if ([action isEqualToString:@"showEventItem"]) {
        NSString* eventId = parameters[@"eventItemId"];
        if (eventId) {
            viewController = [[EventItemViewController alloc] initAndLoadEventItemWithId:[eventId longLongValue]];
        }
    } else {
        //no other supported actions
    }
    
    NSString* eventItemIdToMarkFavorite = parameters[@"markFavorite"];
    if (eventItemIdToMarkFavorite) {
        int64_t itemId = [eventItemIdToMarkFavorite longLongValue];
        [self.eventsService addFavoriteEventItemId:itemId];
        viewController = [[EventItemViewController alloc] initAndLoadEventItemWithId:itemId];
    }
    
    return viewController;
}

- (BOOL)handleSilentParameters:(NSDictionary*)parameters {
    
    BOOL found = NO;
    NSString* userToken = parameters[@"userTicket"];
    if (userToken) {
        found = YES;
        [self.eventsService addUserTicket:userToken];
    }
    
    NSString* exchangeToken = parameters[@"exchangeToken"];
    if (exchangeToken) {
        found = YES;
        ExchangeRequest* req = [[ExchangeRequest alloc] initWithExchangeToken:exchangeToken userToken:nil userTickets:[self.eventsService allUserTickets]];
        [self.eventsService exchangeContactsForRequest:req delegate:self];
    }
    
    return found;
}

#pragma mark - UISplitViewControllerDelegate

- (BOOL)splitViewController:(UISplitViewController *)svc shouldHideViewController:(UIViewController *)vc inOrientation:(UIInterfaceOrientation)orientation {
    return NO;
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
