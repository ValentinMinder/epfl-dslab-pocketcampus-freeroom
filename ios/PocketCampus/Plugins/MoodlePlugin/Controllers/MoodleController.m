
#import "MoodleController.h"

#import "MoodleCoursesListViewController.h"

#import "PCObjectArchiver.h"

#import "AuthenticationController.h"

#import "MoodleService.h"

@interface MoodleController ()<UISplitViewControllerDelegate, AuthenticationCallbackDelegate, MoodleServiceDelegate>

@property (nonatomic, strong) MoodleService* moodleService;
@property (nonatomic, strong) TequilaToken* tequilaToken;

@end

@implementation MoodleController

static MoodleController* instance __weak = nil;

static NSString* const kDeleteSessionAtInitKey = @"DeleteSessionAtInit";

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
                PCNavigationController* navController =  [[PCNavigationController alloc] initWithRootViewController:coursesListViewController];
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

#pragma mark - PluginController

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
    NSNumber* deleteSession = (NSNumber*)[PCObjectArchiver objectForKey:kDeleteSessionAtInitKey andPluginName:@"moodle"];
    if (deleteSession && [deleteSession boolValue]) {
        NSLog(@"-> Delayed logout notification on Moodle now applied : deleting sessionId");
        [[MoodleService sharedInstanceToRetain] deleteSession];
        [PCObjectArchiver saveObject:nil forKey:kDeleteSessionAtInitKey andPluginName:@"moodle"];
    }
}

+ (void)initObservers {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [[NSNotificationCenter defaultCenter] addObserverForName:kAuthenticationLogoutNotification object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *notification) {
            NSNumber* delayed = [notification.userInfo objectForKey:kAuthenticationLogoutNotificationDelayedBoolUserInfoKey];
            if ([delayed boolValue]) {
                NSLog(@"-> Moodle received %@ notification delayed", kAuthenticationLogoutNotification);
                [PCObjectArchiver saveObject:[NSNumber numberWithBool:YES] forKey:kDeleteSessionAtInitKey andPluginName:@"moodle"];
            } else {
                NSLog(@"-> Moodle received %@ notification", kAuthenticationLogoutNotification);
                MoodleService* moodleService = [MoodleService sharedInstanceToRetain];
                [moodleService deleteSession]; //removing stored session
                [moodleService deleteAllDownloadedResources]; //removing all downloaded Moodle files
                moodleService = nil;
                [PCObjectArchiver deleteAllCachedObjectsForPluginName:@"moodle"];
                [[MainController publicController] requestLeavePlugin:@"Moodle"];
            }
        }];
    });
}

#pragma mark - PluginControllerAuthentified

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

#pragma mark - MoodleServiceDelegate

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

- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken *)aTequilaKey didReturn:(MoodleSession *)session {
    [self.moodleService saveSession:session];
    [self cleanAndNotifySuccessToObservers];
}

- (void)getSessionIdForServiceFailedForTequilaKey:(TequilaToken *)aTequilaKey {
    [self cleanAndNotifyFailureToObservers];
}

- (void)serviceConnectionToServerFailed {
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
    [self.moodleService cancelOperationsForDelegate:self];
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
