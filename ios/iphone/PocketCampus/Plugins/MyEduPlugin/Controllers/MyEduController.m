//
//  MyEduController.m
//  PocketCampus
//
//  ARC enabled
//

#import "MyEduController.h"

#import "MyEduCourseListViewController.h"

#import "ObjectArchiver.h"

#import "AuthenticationController.h"

#import "PushNotifController.h"

static BOOL initObserversDone = NO;
static NSString* kDeleteSessionAtInitKey = @"DeleteSessionAtInit";

@interface MyEduController ()

@property (nonatomic, strong) AuthenticationController* authController;
@property (nonatomic, strong) MyEduService* myEduService;
@property (nonatomic, strong) MyEduTequilaToken* tequilaToken;
@property (nonatomic, strong) NSMutableArray* loginObservers; //array of PCLoginObservers (def. in AuthenticationController)

@end

static MyEduController* instance __weak = nil;

@implementation MyEduController

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"MyEduController cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super init];
        if (self) {
            [[self class] deleteSessionIfNecessary];
            _loginObservers = [NSMutableArray array];
            MyEduCourseListViewController* courseListViewController = [[MyEduCourseListViewController alloc] init];
            courseListViewController.title = NSLocalizedStringFromTable(@"MyCourses", @"MyEduPlugin", nil);
            
            UINavigationController* masterNavigationController = [[UINavigationController alloc] initWithRootViewController:courseListViewController];
            UIViewController* detailViewController = [[UIViewController alloc] init]; //detail view controller will be set by PluginSplitViewController that will ask to master view controller
            
            PluginSplitViewController* splitViewController = [[PluginSplitViewController alloc] initWithMasterViewController:masterNavigationController detailViewController:detailViewController];
            splitViewController.delegate = self;
            
            self.mainSplitViewController = splitViewController;
            self.mainSplitViewController.pluginIdentifier = [[self class] identifierName];
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
    NSNumber* deleteSession = (NSNumber*)[ObjectArchiver objectForKey:kDeleteSessionAtInitKey andPluginName:@"myedu"];
    if (deleteSession && [deleteSession boolValue]) {
        NSLog(@"-> Delayed logout notification on MyeEdu now applied : deleting session");
        [[MyEduService sharedInstanceToRetain] deleteSession];
        [ObjectArchiver saveObject:nil forKey:kDeleteSessionAtInitKey andPluginName:@"myedu"];
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
                NSLog(@"-> MyEdu received %@ notification delayed", [AuthenticationService logoutNotificationName]);
                [ObjectArchiver saveObject:[NSNumber numberWithBool:YES] forKey:kDeleteSessionAtInitKey andPluginName:@"myedu"];
            } else {
                NSLog(@"-> MyEdu received %@ notification", [AuthenticationService logoutNotificationName]);
                [[MyEduService sharedInstanceToRetain] deleteSession];
                [[MainController publicController] requestLeavePlugin:@"MyEdu"];
            }
        }];
        
        [[PushNotifController sharedInstance] addNotificationObserverWithPluginLowerIdentifier:@"myedu" newNotificationBlock:^(NSString *notificationMessage) {
            [[MainController publicController] requestPluginToForeground:@"MyEdu"];
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
            self.myEduService = [MyEduService sharedInstanceToRetain];
            self.authController = [AuthenticationController sharedInstance];
            [self.myEduService getTequilaTokenForMyEduWithDelegate:self];
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
            [self.myEduService cancelOperationsForDelegate:self]; //abandon login attempt if no more observer interested
            self.myEduService = nil;
            self.authController = nil;
        }
    }
}

- (void)cleanAndNotifySuccessToObservers {
    self.tequilaToken = nil;
    self.authController = nil;
    self.myEduService = nil;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in self.loginObservers) {
            loginObserver.successBlock();
        }
    }
}

- (void)cleanAndNotifyFailureToObservers {
    self.tequilaToken = nil;
    self.authController = nil;
    self.myEduService = nil;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in self.loginObservers) {
            loginObserver.failureBlock();
        }
    }
}

- (void)cleanAndNotifyUserCancelledToObservers {
    self.tequilaToken = nil;
    self.authController = nil;
    self.myEduService = nil;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in self.loginObservers) {
            loginObserver.userCancelledBlock();
        }
    }
}

#pragma mark - MyEduServiceDelegate

- (void)getTequilaTokenForMyEduDidReturn:(MyEduTequilaToken *)tequilaToken {
    self.tequilaToken = tequilaToken;
    if (self.mainSplitViewController) {
        [self.authController authToken:tequilaToken.iTequilaKey presentationViewController:self.mainSplitViewController delegate:self];
    } else {
        [self.authController authToken:tequilaToken.iTequilaKey presentationViewController:self.mainNavigationController delegate:self];
    }
}

- (void)getTequilaTokenForMyEduFailed {
    [self cleanAndNotifyFailureToObservers];
}

- (void)getMyEduSessionForTequilaToken:(MyEduTequilaToken *)tequilaToken didReturn:(MyEduSession *)myEduSession {
    [self.myEduService saveSession:myEduSession];
    [self cleanAndNotifySuccessToObservers];
}

- (void)getMyEduSessionFailedForTequilaToken:(MyEduTequilaToken *)tequilaToken {
    [self cleanAndNotifyFailureToObservers];
}

- (void)serviceConnectionToServerTimedOut {
    self.authController = nil;
    self.tequilaToken = nil;
    self.myEduService = nil;
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
    [self.myEduService getMyEduSessionForTequilaToken:self.tequilaToken delegate:self];
}

- (void)userCancelledAuthentication {
    [self.myEduService deleteSession];
    [self cleanAndNotifyUserCancelledToObservers];
}

- (void)invalidToken {
    [self.myEduService getTequilaTokenForMyEduWithDelegate:self]; //restart to get new token
}

#pragma mark - PluginControllerProtocol

- (void)refresh {
    //TODO: refresh infos displayed by plugin if necessary
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"MyEduPlugin", @"");
}

+ (NSString*)identifierName {
    return @"MyEdu";
}

- (void)pluginDidBecomePassive {
    //TODO
}

- (void)pluginWillLoseFocus {
    //TODO
}
- (void)pluginDidRegainActive {
    if ([self.mainSplitViewController.viewControllers[0] respondsToSelector:@selector(refresh)]) {
        [self.mainSplitViewController.viewControllers[0] refresh];
    }
}

#pragma mark - UISplitViewControllerDelegate

- (BOOL)splitViewController:(UISplitViewController *)svc shouldHideViewController:(UIViewController *)vc inOrientation:(UIInterfaceOrientation)orientation {
    /*if (orientation == UIInterfaceOrientationMaskPortrait) {
        return YES;
    }*/
    return NO;
}

#pragma mark - dealloc

- (void)dealloc
{
    [[self class] deleteSessionIfNecessary];
    [self.myEduService cancelOperationsForDelegate:self];
    @synchronized(self) {
        instance = nil;
    }
}

@end
