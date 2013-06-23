//
//  QAForumController.m
//  PocketCampus
//
//

#import "QAForumController.h"

#import "MainController.h"

#import "AuthenticationController.h"

#import "ObjectArchiver.h"



static QAForumController* instance __weak = nil;
static BOOL initObserversDone = NO;
static NSString* kDeleteSessionAtInitKey = @"DeleteSessionAtInit";


@implementation QAForumController

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"QAForumController cannot be instancied more than once at a time, use sharedInstance instead." userInfo:nil];
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
            [[self class] deleteSessionIfNecessary];
            QAForumViewController* qaforumViewController = [[QAForumViewController alloc] init];
            qaforumViewController.title = [[self class] localizedName];
            PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:qaforumViewController];
            navController.pluginIdentifier = [[self class] identifierName];
            self.mainNavigationController = navController;
            instance = self;
        }
        return self;
    }
}

+ (void)deleteSessionIfNecessary {
    NSNumber* deleteSession = (NSNumber*)[ObjectArchiver objectForKey:kDeleteSessionAtInitKey andPluginName:@"qaforum"];
    if (deleteSession && [deleteSession boolValue]) {
        NSLog(@"-> Delayed logout notification on QAForum now applied : deleting sessionId");
        [QAForumService saveSessionId:nil];
        [ObjectArchiver saveObject:nil forKey:kDeleteSessionAtInitKey andPluginName:@"qaforum"];
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

+ (void)initObservers {
    @synchronized(self) {
        if (initObserversDone) {
            return;
        }
        
        [[PushNotifController sharedInstanceToRetain] addPushNotificationObserver:self forPluginLowerIdentifier:@"qaforum" newNotificationBlock:^(NSString *notifMessage, NSDictionary *notifFullDictionary) {
            [[MainController publicController] requestPluginToForeground:@"QAForum"];
            
            //login and invalid notification
            if ([QAForumService lastSessionId].sessionid!=nil) {
                NSString* notificaionid = notifFullDictionary[@"notificationid"];
                NSDictionary* aps = [notifFullDictionary objectForKey:@"aps"];
                NSString* notificationMessage = aps[@"alert"];
                AcceptViewController* viewController = [AcceptViewController alloc];
                viewController.data = notificationMessage;
                viewController.notificationid = [notificaionid intValue];
                [[[self.class sharedInstanceToRetain] mainNavigationController] pushViewController:viewController animated:YES];
            }
            else {
                //not login, so save the message
                [QAForumService saveLastNotif:notifFullDictionary];
            }
        }];
        
        
        [[NSNotificationCenter defaultCenter] addObserverForName:[AuthenticationService logoutNotificationName] object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *notification) {
            NSNumber* delayed = [notification.userInfo objectForKey:[AuthenticationService delayedUserInfoKey]];
            if ([delayed boolValue]) {
                NSLog(@"-> QAForum received %@ notification delayed", [AuthenticationService logoutNotificationName]);
                [ObjectArchiver saveObject:[NSNumber numberWithBool:YES] forKey:kDeleteSessionAtInitKey andPluginName:@"qaforum"];
            } else {
                [[MainController publicController] requestLeavePlugin:@"QAForum"]; //leave plugin because user has logged out
                NSLog(@"-> QAForum received %@ notification", [AuthenticationService logoutNotificationName]);
                [QAForumService saveSessionId:nil]; //removing stored session
            }
        }];
        initObserversDone = YES;
    }
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"QAForumPlugin", @"");
}

+ (NSString*)identifierName {
    return @"QAForum";
}

- (void)refresh {
    NSLog(@"%@",@"Refresh");
}

- (void)serviceConnectionToServerTimedOut {
    [PCUtils showConnectionToServerTimedOutAlert];
    
}

- (void)dealloc
{
    [[self class] deleteSessionIfNecessary];
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}
@end
