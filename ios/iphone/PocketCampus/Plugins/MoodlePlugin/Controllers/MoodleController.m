
#import "MoodleController.h"

#import "CoursesListViewController.h"

#import "ObjectArchiver.h"

@interface MoodleController ()

@property (nonatomic, strong) AuthenticationController* authController;
@property (nonatomic, strong) MoodleService* moodleService;
@property (nonatomic, strong) TequilaToken* tequilaToken;
@property (nonatomic, strong) NSMutableArray* loginObservers; //array of PCLoginObservers (def. in AuthenticationController)

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
            self.loginObservers = [NSMutableArray array];
            [[self class] deleteSessionIfNecessary];
            CoursesListViewController* coursesListViewController = [[CoursesListViewController alloc] init];
            coursesListViewController.title = [[self class] localizedName];
            PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:coursesListViewController];
            navController.pluginIdentifier = [[self class] identifierName];
            self.mainNavigationController = navController;
            instance = self;
        }
        return self;
    }
}

+ (id)sharedInstance {
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
                [[MoodleService sharedInstanceToRetain] deleteSession]; //removing stored session
                [[MainController publicController] requestLeavePlugin:@"Moodle"];
            }
        }];
        initObserversDone = YES;
    }
}

#pragma mark - Login observers management

- (void)addLoginObserver:(id)observer operationIdentifier:(NSString*)identifier successBlock:(VoidBlock)successBlock
      userCancelledBlock:(VoidBlock)userCancelledblock failureBlock:(VoidBlock)failureBlock {
    
    @synchronized(self) {
        PCLoginObserver* loginObserver = [[PCLoginObserver alloc] init];
        loginObserver.observer = observer;
        loginObserver.operationIdentifier = identifier;
        loginObserver.successBlock = successBlock;
        loginObserver.userCancelledBlock = userCancelledblock;
        loginObserver.failureBlock = failureBlock;
        [self.loginObservers addObject:loginObserver];
        if(!self.authController) {
            self.moodleService = [MoodleService sharedInstanceToRetain];
            self.authController = [AuthenticationController sharedInstance];
            [self.moodleService getTequilaTokenForMoodleDelegate:self];
        }
    }
}

- (void)removeLoginObserver:(id)observer {
    [self removeLoginObserver:observer operationIdentifier:nil];
}

- (void)removeLoginObserver:(id)observer operationIdentifier:(NSString*)identifier { //pass nil identifier to remove all from observer
    @synchronized(self) {
        for (PCLoginObserver* loginObserver in [self.loginObservers copy]) {
            if (loginObserver.observer == observer && (!identifier || [loginObserver.operationIdentifier isEqualToString:identifier])) {
                [self.loginObservers removeObject:loginObserver];
            }
        }
        if ([self.loginObservers count] == 0) {
            [self.moodleService cancelOperationsForDelegate:self]; //abandon login attempt if no more observer interested
            self.moodleService = nil;
            self.tequilaToken = nil;
            self.authController = nil;
        }
    }
}

- (void)cleanAndNotifySuccessToObservers {
    self.tequilaToken = nil;
    self.authController = nil;
    self.moodleService = nil;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in self.loginObservers) {
            loginObserver.successBlock();
        }
    }
}

- (void)cleanAndNotifyFailureToObservers {
    self.tequilaToken = nil;
    self.authController = nil;
    self.moodleService = nil;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in self.loginObservers) {
            loginObserver.failureBlock();
        }
    }
}

- (void)cleanAndNotifyUserCancelledToObservers {
    self.tequilaToken = nil;
    self.authController = nil;
    self.moodleService = nil;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in self.loginObservers) {
            loginObserver.userCancelledBlock();
        }
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
    self.authController = nil;
    self.tequilaToken = nil;
    self.moodleService = nil;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in [self.loginObservers copy]) {
            if ([loginObserver.observer respondsToSelector:@selector(serviceConnectionToServerTimedOut)]) {
                [loginObserver.observer serviceConnectionToServerTimedOut];
            }
            [self.loginObservers removeObject:loginObserver];
        }
    }
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
