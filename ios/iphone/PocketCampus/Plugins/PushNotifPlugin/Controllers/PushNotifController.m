//
//  PushNotifController.m
//  PocketCampus
//
//

#import "AuthenticationController.h"

#import "PushNotifController.h"

#import "PushNotifService.h"

#import "AppDelegate.h"

@implementation PushNotifDeviceRegistrationObserver

@end

@interface PushNotifController ()

@property (nonatomic, strong) AuthenticationController* authController;
@property (nonatomic, strong) PushNotifService* pushNotifService;
@property (nonatomic, strong) TequilaToken* tequilaToken;
@property (nonatomic, weak) UIViewController* authPresentationController;
@property (nonatomic, strong) NSMutableArray* regObservers; //array of PushNotifDeviceRegistrationObserver

@end

static PushNotifController* instance __weak = nil;

@implementation PushNotifController

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"PushNotifController cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super init];
        if (self) {
            self.regObservers = [NSMutableArray array];
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

- (void)addDeviceRegistrationObserver:(id)observer successBlock:(VoidBlock)successBlock failureBlock:(PushNotifDeviceRegistrationFailureBlock)failureBlock {
    @throw [NSException exceptionWithName:@"Unsupported feature" reason:@"Only authenticated registration is currently supported" userInfo:nil];
}

- (void)addAuthentifiedUserDeviceRegistrationObserver:(id)observer presentationViewControllerForAutentication:(UIViewController*)presController successBlock:(VoidBlock)successBlock failureBlock:(PushNotifDeviceRegistrationFailureBlock)failureBlock {
    @synchronized(self) {
        PushNotifDeviceRegistrationObserver* regObserver = [[PushNotifDeviceRegistrationObserver alloc] init];
        regObserver.observer = observer;
        regObserver.authentified = NO;
        regObserver.successBlock = successBlock;
        regObserver.failureBlock = failureBlock;
        self.authPresentationController = presController;
        if ([self.regObservers count] == 0) { //need to start device registration procedure
            self.pushNotifService = [PushNotifService sharedInstanceToRetain];
            self.authController = [AuthenticationController sharedInstanceToRetain];
        }
        [self.regObservers addObject:regObserver];
        [self.pushNotifService getTequilaTokenForPushNotifWithDelegate:self];
    }
}

- (void)addDeviceUnregistrationObserver:(id)observer successBlock:(VoidBlock)successBlock failureBlock:(PushNotifDeviceRegistrationFailureBlock)failureBlock {
    @throw [NSException exceptionWithName:@"Unsupported feature" reason:@"Unregistration registration is not currently supported" userInfo:nil];
}

- (void)removeObserver:(id)observer {
    @synchronized(self) {
        for (PushNotifDeviceRegistrationObserver* regObserver in [self.regObservers copy]) {
            if (regObserver.observer == observer) {
                [self.regObservers removeObject:regObserver];
            }
        }
        if ([self.regObservers count] == 0) {
            [self.pushNotifService cancelOperationsForDelegate:self]; //abandon (cancel) device registration attempt if no more observer interested
            self.pushNotifService = nil;
            self.authController = nil;
            self.tequilaToken = nil;
            self.authPresentationController = nil;
        }
    }
}

- (void)cleanAndNotifySuccessToObservers {
    self.tequilaToken = nil;
    self.authController = nil;
    self.pushNotifService = nil;
    self.authPresentationController = nil;
    @synchronized (self) {
        for (PushNotifDeviceRegistrationObserver* regObserver in self.regObservers) {
            regObserver.successBlock();
        }
    }
}

- (void)cleanAndNotifiyFailureToObserversWithError:(PushNotifDeviceRegistrationError)error {
    self.tequilaToken = nil;
    self.authController = nil;
    self.pushNotifService = nil;
    self.authPresentationController = nil;
    @synchronized (self) {
        for (PushNotifDeviceRegistrationObserver* regObserver in self.regObservers) {
            regObserver.failureBlock(error);
        }
    }
}

- (void)addNotificationObserverWithPluginLowerIdentifier:(NSString*)pluginLowerIdentifier newNotificationBlock:(NewNotificationBlock)newNotificationBlock {
    [[NSNotificationCenter defaultCenter] addObserverForName:[AppDelegate nsNotificationNameForPluginLowerIdentifier:pluginLowerIdentifier] object:nil queue:nil usingBlock:^(NSNotification *notif) {
        newNotificationBlock(notif.userInfo[@"aps"][@"alert"]);
    }];
}

#pragma mark - PushNotifServiceDelegate

- (void)getTequilaTokenForPushNotifDidReturn:(TequilaToken*)token {
    self.tequilaToken = token;
    [self.authController authToken:token.iTequilaKey presentationViewController:self.authPresentationController delegate:self];
}

- (void)getTequilaTokenForPushNotifFailed {
    [self cleanAndNotifiyFailureToObserversWithError:PushNotifDeviceRegistrationErrorUnknown];
}

- (void)registerPushNotifForRequest:(PushNotifRegReq*)request didReturn:(PushNotifReply*)reply {
    [self cleanAndNotifySuccessToObservers];
}

- (void)registerPushNotifFailedForRequest:(PushNotifRegReq*)request {
    [self cleanAndNotifiyFailureToObserversWithError:PushNotifDeviceRegistrationErrorServerCommunication];
}

- (void)serviceConnectionToServerTimedOut {
    [self cleanAndNotifiyFailureToObserversWithError:PushNotifDeviceRegistrationErrorServerCommunication];
}

#pragma mark - AuthenticationCallbackDelegate

- (void)authenticationSucceeded {
    if (!self.tequilaToken) {
        NSLog(@"-> ERROR : no tequilaToken saved after successful authentication");
        return;
    }
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(registrationSuccessNotification:) name:AppDidSucceedToRegisterToNotifications object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(registrationFailureNotification:) name:AppDidFailToRegisterToNotifications object:nil];
    
    [[UIApplication sharedApplication] registerForRemoteNotificationTypes:UIRemoteNotificationTypeAlert|UIRemoteNotificationTypeBadge|UIRemoteNotificationTypeSound];
}

- (void)userCancelledAuthentication {
    [self cleanAndNotifiyFailureToObserversWithError:PushNotifDeviceRegistrationErrorUserCancelledAuthentication];
}

- (void)invalidToken {
    self.tequilaToken = nil;
    [self.pushNotifService getTequilaTokenForPushNotifWithDelegate:self]; //restart to get new token
}

#pragma mark - Push notifs registration callback from AppDelegate
     
- (void)registrationSuccessNotification:(NSNotification*)notif {
    NSString* deviceToken = notif.userInfo[kPushDeviceTokenStringKey];
    PushNotifRegReq* request = [[PushNotifRegReq alloc] initWithIAuthenticatedToken:self.tequilaToken iPlatformType:PlatformType_PC_PLATFORM_IOS RegistrationId:deviceToken];
    [self.pushNotifService registerPushNotif:request delegate:self];
}

- (void)registrationFailureNotification:(NSNotification*)notif {
    [self cleanAndNotifiyFailureToObserversWithError:PushNotifDeviceRegistrationErrorUserCancelledAuthentication];
}



+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"PushNotifPlugin", @"");
}

+ (NSString*)identifierName {
    return @"PushNotif";
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
