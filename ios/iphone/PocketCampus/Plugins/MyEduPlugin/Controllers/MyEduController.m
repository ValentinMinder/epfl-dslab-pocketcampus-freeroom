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

static MyEduController* instance = nil;

static BOOL initObserversDone = NO;
static NSString* kDeleteSessionAtInitKey = @"DeleteSessionAtInit";

@interface MyEduController ()

@property (nonatomic, strong) AuthenticationController* authController;
@property (nonatomic, strong) MyEduService* myEduService;
@property (nonatomic, strong) MyEduTequilaToken* tequilaToken;
@property (nonatomic, strong) NSMutableArray* loginObservers; //array of PCLoginObservers (def. in AuthenticationController)

@end

@implementation MyEduController

- (id)initWithMainController:(MainController2 *)mainController_
{
    self = [super init];
    if (self) {
        [[self class] deleteSessionIfNecessary];
        mainController = mainController_;
        _loginObservers = [NSMutableArray array];
        MyEduCourseListViewController* courseListViewController = [[MyEduCourseListViewController alloc] init];
        courseListViewController.title = NSLocalizedStringFromTable(@"MyCourses", @"MyEduPlugin", nil);
        
        UINavigationController* masterNavigationController = [[UINavigationController alloc] initWithRootViewController:courseListViewController];
        UIViewController* detailViewController = [[UIViewController alloc] init]; //detail view controller will be set by PluginSplitViewController that will ask to master view controller
        
        PluginSplitViewController* splitViewController = [[PluginSplitViewController alloc] initWithMasterViewController:masterNavigationController detailViewController:detailViewController];
        splitViewController.delegate = self;
        
        mainSplitViewController = splitViewController;
        mainSplitViewController.pluginIdentifier = [[self class] identifierName];
    }
    instance = self;
    return self;
}

+ (MyEduController*)currentInstance {
    return instance;
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
            }
            NSLog(@"!! Warning: TODO, MyEdu logout => delete cache + leave plugin if direct notification");
        }];
        initObserversDone = YES;
    }
}

- (void)addLoginObserver:(id)observer operationIdentifier:(NSString*)identifier successBlock:(void (^)(void))successBlock
    userCancelledBlock:(void (^)(void))userCancelledblock failureBlock:(void (^)(void))failureBlock {
    
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
            self.authController = [[AuthenticationController alloc] init];
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
                [self.loginObservers removeObject:observer];
            }
        }
        if ([self.loginObservers count] == 0) {
            [self.myEduService cancelOperationsForDelegate:self]; //abandon login attempt if no more observer interested
            self.myEduService = nil;
            self.authController = nil;
        }
    }
}

- (void)invalidToken {
    [self.myEduService getTequilaTokenForMyEduWithDelegate:self]; //restart to get new token
}

#pragma mark - MyEduServiceDelegate

- (void)getTequilaTokenForMyEduDidReturn:(MyEduTequilaToken *)tequilaToken {
    self.tequilaToken = tequilaToken;
    if (mainSplitViewController) {
        [self.authController authToken:tequilaToken.iTequilaKey presentationViewController:mainSplitViewController delegate:self];
    } else {
        [self.authController authToken:tequilaToken.iTequilaKey presentationViewController:mainNavigationController delegate:self];
    }
}

- (void)getTequilaTokenForMyEduFailed {
    self.tequilaToken = nil;
    self.authController = nil;
    self.myEduService = nil;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in self.loginObservers) {
            loginObserver.failureBlock();
        }
    }
}

- (void)getMyEduSessionForTequilaToken:(MyEduTequilaToken *)tequilaToken didReturn:(MyEduSession *)myEduSession {
    [self.myEduService saveSession:myEduSession];
    self.tequilaToken = nil;
    self.authController = nil;
    self.myEduService = nil;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in [self.loginObservers copy]) {
            loginObserver.successBlock();
            [self.loginObservers removeObject:loginObserver];
        }
    }
}

- (void)getMyEduSessionFailedForTequilaToken:(MyEduTequilaToken *)tequilaToken {
    self.tequilaToken = nil;
    self.authController = nil;
    self.myEduService = nil;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in [self.loginObservers copy]) {
            loginObserver.failureBlock();
            [self.loginObservers removeObject:loginObserver];
        }
    }
}

- (void)serviceConnectionToServerTimedOut {
    self.authController = nil;
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
    self.tequilaToken = nil;
    self.authController = nil;
    self.myEduService = nil;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in self.loginObservers) {
            loginObserver.userCancelledBlock();
        }
    }
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
    [self.myEduService cancelOperationsForDelegate:self];
    instance = nil;
}

@end
