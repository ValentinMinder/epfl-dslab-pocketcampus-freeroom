//
//  MyEduController.m
//  PocketCampus
//
//  ARC enabled
//

#import "MyEduController.h"

#import "MyEduCourseListViewController.h"

#import "MyEduSplashDetailViewController.h"

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

@property (nonatomic, strong) PushNotifController* pushController;

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
            MyEduCourseListViewController* courseListViewController = [[MyEduCourseListViewController alloc] init];
            courseListViewController.title = NSLocalizedStringFromTable(@"MyCourses", @"MyEduPlugin", nil);
            
            UINavigationController* masterNavigationController = [[UINavigationController alloc] initWithRootViewController:courseListViewController];
            UIViewController* detailViewController = [[MyEduSplashDetailViewController alloc] init]; //detail view controller will be set by PluginSplitViewController that will ask to master view controller
            
            PluginSplitViewController* splitViewController = [[PluginSplitViewController alloc] initWithMasterViewController:masterNavigationController detailViewController:detailViewController];
            splitViewController.delegate = self;
            
            self.mainSplitViewController = splitViewController;
            self.mainSplitViewController.pluginIdentifier = [[self class] identifierName];
            
            /* TEST */
            /*
             self.pushController = [PushNotifController sharedInstance];
            [self.pushController addAuthentifiedUserDeviceRegistrationObserver:self presentationViewControllerForAutentication:courseListViewController successBlock:^{
                NSLog(@"OK");
            } failureBlock:^(PushNotifDeviceRegistrationError error) {
                NSLog(@"Failed");
            }];
             */
            /* */
            
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
                [ObjectArchiver deleteAllCachedObjectsForPluginName:@"MyEdu"];
                [[MainController publicController] requestLeavePlugin:@"MyEdu"];
            }
        }];
        
        initObserversDone = YES;
        //[NSTimer scheduledTimerWithTimeInterval:3.0 target:self selector:@selector(test) userInfo:nil repeats:NO];
    }
}


#pragma mark - Login observers management

- (void)addLoginObserver:(id)observer successBlock:(VoidBlock)successBlock
    userCancelledBlock:(VoidBlock)userCancelledblock failureBlock:(VoidBlock)failureBlock {
    
    [super addLoginObserver:observer successBlock:successBlock userCancelledBlock:userCancelledblock failureBlock:failureBlock];
    if(!super.authenticationStarted) {
        super.authenticationStarted = YES;
        self.myEduService = [MyEduService sharedInstanceToRetain];
        [self.myEduService getTequilaTokenForMyEduWithDelegate:self];
    }
}

- (void)removeLoginObserver:(id)observer {
    [super removeLoginObserver:observer];
    if ([self.loginObservers count] == 0) {
        [self.myEduService cancelOperationsForDelegate:self]; //abandon login attempt if no more observer interested
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
    
    if (!myEduSession.iMyEduCookie) {
        [self cleanAndNotifyFailureToObservers];
        return;
    }
    
    if ([myEduSession.iMyEduCookie isEqualToString:@"FORBIDDEN"]) {
        //means user has successfully authentified but is NOT allowed to access MyEdu
        [self.myEduService deleteSession];
        [self cleanAndNotifyUserCancelledToObservers];
        [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"NotAllowedAccessToMyEdu", @"MyEduPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
        return;
    }
    
    [self.myEduService saveSession:myEduSession];
    [self cleanAndNotifySuccessToObservers];
}

- (void)getMyEduSessionFailedForTequilaToken:(MyEduTequilaToken *)tequilaToken {
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

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"MyEduPlugin", @"");
}

+ (NSString*)identifierName {
    return @"MyEdu";
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
    [[self class] deleteSessionIfNecessary];
    @synchronized(self) {
        instance = nil;
    }
}

@end
