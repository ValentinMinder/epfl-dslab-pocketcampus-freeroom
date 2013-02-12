
#import "MoodleController.h"

#import "MoodleCoursesListViewController.h"

#import "ObjectArchiver.h"

#import "PluginSplitViewController.h"

#import "PCUtils.h"

@interface MoodleController ()

@property (nonatomic, strong) MoodleService* moodleService;
@property (nonatomic, strong) TequilaToken* tequilaToken;

@end

@implementation MoodleController

static MoodleController* instance __weak = nil;

static BOOL initObserversDone = NO;
static NSString* kDeleteSessionAtInitKey = @"DeleteSessionAtInit";

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"MoodleController cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super init];
        if (self) {
            [[self class] deleteSessionIfNecessary];
            MoodleCoursesListViewController* coursesListViewController = [[MoodleCoursesListViewController alloc] init];
            
            if ([PCUtils isIdiomPad]) {
                UINavigationController* navController =  [[UINavigationController alloc] initWithRootViewController:coursesListViewController];
                UIViewController* emptyDetailViewController = [[UIViewController alloc] init]; //splash view controller will be returned by coursesListViewController as PluginSplitViewControllerDelegate
                PluginSplitViewController* splitViewController = [[PluginSplitViewController alloc] initWithMasterViewController:navController detailViewController:emptyDetailViewController];
                splitViewController.pluginIdentifier = [[self class] identifierName];
                splitViewController.delegate = self;
                self.mainSplitViewController = splitViewController;
            } else {
                PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:coursesListViewController];
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

+ (void)deleteSessionIfNecessary {
    NSNumber* deleteSession = (NSNumber*)[ObjectArchiver objectForKey:kDeleteSessionAtInitKey andPluginName:@"moodle"];
    if (deleteSession && [deleteSession boolValue]) {
        NSLog(@"-> Delayed logout notification on Moodle now applied : deleting sessionId");
        [[MoodleService sharedInstanceToRetain] deleteSession];
        [ObjectArchiver saveObject:nil forKey:kDeleteSessionAtInitKey andPluginName:@"moodle"];
    }
}

+ (void)initObservers {
    @synchronized(self) {
        if (initObserversDone) {
            return;
        }
        [[NSNotificationCenter defaultCenter] addObserverForName:[AuthenticationService logoutNotificationName] object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *notification) {
            NSNumber* delayed = [notification.userInfo objectForKey:[AuthenticationService delayedUserInfoKey]];
            if ([delayed boolValue]) {
                NSLog(@"-> Moodle received %@ notification delayed", [AuthenticationService logoutNotificationName]);
                [ObjectArchiver saveObject:[NSNumber numberWithBool:YES] forKey:kDeleteSessionAtInitKey andPluginName:@"moodle"];
            } else {
                NSLog(@"-> Moodle received %@ notification", [AuthenticationService logoutNotificationName]);
                MoodleService* moodleService = [MoodleService sharedInstanceToRetain];
                [moodleService deleteSession]; //removing stored session
                [moodleService deleteAllDownloadedResources]; //removing all downloaded Moodle files
                moodleService = nil;
                [ObjectArchiver deleteAllCachedObjectsForPluginName:@"moodle"];
                [[MainController publicController] requestLeavePlugin:@"Moodle"];
            }
        }];
        initObserversDone = YES;
    }
}

#pragma mark - Login observers management

- (void)addLoginObserver:(id)observer successBlock:(VoidBlock)successBlock
      userCancelledBlock:(VoidBlock)userCancelledblock failureBlock:(VoidBlock)failureBlock {
    
    [super addLoginObserver:observer successBlock:successBlock userCancelledBlock:userCancelledblock failureBlock:failureBlock];
    if (!super.authenticationStarted) {
        super.authenticationStarted = YES;
        self.moodleService = [MoodleService sharedInstanceToRetain];
        [self.moodleService getTequilaTokenForMoodleDelegate:self];
    }
}

- (void)removeLoginObserver:(id)observer {
    [super removeLoginObserver:observer];
    if ([self.loginObservers count] == 0) {
        [self.moodleService cancelOperationsForDelegate:self]; //abandon login attempt if no more observer interested
    }
}

#pragma mark - MyEduServiceDelegate

- (void)getTequilaTokenForMoodleDidReturn:(TequilaToken *)tequilaKey {
    self.tequilaToken = tequilaKey;
    if (self.mainSplitViewController) {
        [self.authController authToken:tequilaKey.iTequilaKey presentationViewController:self.mainSplitViewController delegate:self];
    } else {
        [self.authController authToken:tequilaKey.iTequilaKey presentationViewController:self.mainNavigationController delegate:self];
    }
}

- (void)getTequilaTokenForMoodleFailed {
    [self cleanAndNotifyFailureToObservers];
}

- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken *)aTequilaKey didReturn:(MoodleSession *)aSessionId {
    MoodleSession* session = [[MoodleSession alloc] initWithMoodleCookie:aSessionId.moodleCookie];
    [self.moodleService saveSession:session];
    [self cleanAndNotifySuccessToObservers];
}

- (void)getSessionIdForServiceFailedForTequilaKey:(TequilaToken *)aTequilaKey {
    [self cleanAndNotifyFailureToObservers];
}

- (void)serviceConnectionToServerTimedOut {
    [super cleanAndNotifyConnectionToServerTimedOutToObservers];
}

#pragma mark - AuthenticationCallbackDelegate

- (void)authenticationSucceeded {
    if (!self.tequilaToken) {
        NSLog(@"-> ERROR : no tequilaToken saved after successful authentication");
        return;
    }
    [self.moodleService getSessionIdForServiceWithTequilaKey:self.tequilaToken delegate:self];;
}

- (void)userCancelledAuthentication {
    [self.moodleService deleteSession];
    [self cleanAndNotifyUserCancelledToObservers];
}

- (void)invalidToken {
    [self.moodleService getTequilaTokenForMoodleDelegate:self]; //restart to get new token
}

#pragma mark - PluginControllerProtocol

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"MoodlePlugin", @"");
}

+ (NSString*)identifierName {
    return @"Moodle";
}

- (NSString*)localizedStringForKey:(NSString*)key {
    return NSLocalizedStringFromTable(key, [[self class] identifierName], @"");
}

#pragma mark - UISplitViewControllerDelegate

- (BOOL)splitViewController:(UISplitViewController *)svc shouldHideViewController:(UIViewController *)vc inOrientation:(UIInterfaceOrientation)orientation {
    /*if (orientation == UIInterfaceOrientationMaskPortrait) {
     return YES;
     }*/
    return NO;
}

- (void)dealloc
{
    [self.moodleService cancelOperationsForDelegate:self];
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
