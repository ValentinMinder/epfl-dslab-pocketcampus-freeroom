//
//  PushNotifController.m
//  PocketCampus
//
//

#import "AuthenticationController.h"

#import "PushNotifController.h"

#import "PushNotifService.h"

#import "AppDelegate.h"

#import "PCUtils.h"

static NSMutableDictionary* observerInstanceForNSNotificationCenterObserver __strong = nil;

@implementation PushNotifDeviceRegistrationObserver

- (void)deleteMappingForDummy:(NSString*)dummy didReturn:(int32_t)status {
    //we consider that if it did return, it's ok
    self.successBlock();
}

- (void)deleteMappingFailedForDummy:(NSString*)dummy {
    self.failureBlock(0);
}

- (void)serviceConnectionToServerTimedOut {
    self.failureBlock(0);
}

@end

static NSString* kNotificationsDeviceTokenKey = @"NotificationsDeviceToken";
static NSString* notificationsDeviceTokenCache __strong = nil;

static BOOL initObserversDone = NO;

static PushNotifController* instance __weak = nil;

static PushNotifService* pushNotifService __strong = nil; //used to retain service during unregistration
static PushNotifDeviceRegistrationObserver* unregistrationDelegate __strong = nil;

@interface PushNotifController ()

@property (nonatomic, strong) UIAlertView* pushNotifsReasonAlert;
@property (nonatomic, strong) NSMutableArray* regObservers; //array of PushNotifDeviceRegistrationObserver

@end

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
            if (!observerInstanceForNSNotificationCenterObserver) {
                observerInstanceForNSNotificationCenterObserver = [NSMutableDictionary dictionary];
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

+ (void)initObservers {
    @synchronized(self) {
        if (initObserversDone) {
            return;
        }
        [[NSNotificationCenter defaultCenter] addObserverForName:[AuthenticationService logoutNotificationName] object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *notification) {
            if ([self notificationsDeviceToken] == nil) {
                NSLog(@"-> PushNotif received %@ notification. No saved device token to unregister, returning.", [AuthenticationService logoutNotificationName]);
                return;
            }
            NSNumber* delayed = [notification.userInfo objectForKey:[AuthenticationService delayedUserInfoKey]];
            if ([delayed boolValue]) {
                NSLog(@"-> PushNotif received %@ notification delayed", [AuthenticationService logoutNotificationName]);
                NSLog(@"WARNING: delayed logout is not supported in PushNotif. Unregistration for push notifs will be immediate. Users that login with a non-persitent state (not saving credentials) will thus not receive notifications.");
            } else {
                NSLog(@"-> PushNotif received %@ notification. Now unregistrating from push notifs...", [AuthenticationService logoutNotificationName]);
            }
            [self unregisterAfterLogout];
        }];
        initObserversDone = YES;
    }
}

+ (NSString*)notificationsDeviceToken {
    if (!notificationsDeviceTokenCache) {
        notificationsDeviceTokenCache = (NSString*)[[NSUserDefaults standardUserDefaults] objectForKey:kNotificationsDeviceTokenKey];
    }
    return notificationsDeviceTokenCache;
}

- (void)registerDeviceForPushNotificationsWithPluginLowerIdentifier:(NSString*)pluginLowerIdentifier reason:(NSString*)reason success:(VoidBlock)success failure:(PushNotifDeviceRegistrationFailureBlock)failure {
    
    @synchronized(self) {
        
        NSString* token = [PushNotifController notificationsDeviceToken];
#warning TEST IF POPUP ALREADY PRESENTED
        if (!token && reason && !self.pushNotifsReasonAlert) {
            //first plugin to ask will be the one that will get his reason poped-up
            NSString* localizedIdentifier = [[MainController publicController] localizedPluginIdentifierForAnycaseIdentifier:pluginLowerIdentifier];
            self.pushNotifsReasonAlert = [[UIAlertView alloc] initWithTitle:[NSString stringWithFormat:NSLocalizedStringFromTable(@"PushNotifsAlertTitleWithFormat", @"PushNotifPlugin", nil), localizedIdentifier] message:reason delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
        }
        
		PushNotifDeviceRegistrationObserver* regObserver = [[PushNotifDeviceRegistrationObserver alloc] init];
		regObserver.successBlock = success;
		regObserver.failureBlock = failure;
        [self.regObservers addObject:regObserver];
		if ([self.regObservers count] == 1) { //first observer, need to start device registration procedure
            if (self.pushNotifsReasonAlert && !self.pushNotifsReasonAlert.visible) {
                //registration to OS will be started when ok pressed (see delegate method)
                [self.pushNotifsReasonAlert show];
            } else {
                //should register directly then
                //possibly just updating or re-registering for the same token to iOS
                //re-registration is not bad practice and is even advised in doc
                [self observeAndStartDeviceRegistrationProcessOnOS];
            }
		}
	}
}


- (void)addPushNotificationObserver:(id)observer forPluginLowerIdentifier:(NSString*)pluginLowerIdentifier newNotificationBlock:(NewNotificationBlock)newNotificationBlock; {
    if (!observer) {
        [NSException raise:@"Illegal argument" format:@"observer parameter cannot be nil"];
    }
    [PCUtils throwExceptionIfObject:pluginLowerIdentifier notKindOfClass:[NSString class]];
    if (![[MainController publicController] isPluginAnycaseIdentifierValid:pluginLowerIdentifier]) {
        [NSException raise:@"Illegal argument" format:@"%@ is not a valid plugin identifier", pluginLowerIdentifier];
    }
    
    id nsObserver = [[NSNotificationCenter defaultCenter] addObserverForName:[AppDelegate nsNotificationNameForPluginLowerIdentifier:pluginLowerIdentifier] object:nil queue:nil usingBlock:^(NSNotification *notif) {
        newNotificationBlock(notif.userInfo[@"aps"][@"alert"], notif.userInfo);
    }];
    
    observerInstanceForNSNotificationCenterObserver[[NSString stringWithFormat:@"%p", observer]] = nsObserver;
    
}

- (void)removeObserver:(id)observer forPluginLowerIdentifier:(NSString*)pluginLowerIdentifier {
    if (!observer) {
        [NSException raise:@"Illegal argument" format:@"observer parameter cannot be nil"];
    }
    [PCUtils throwExceptionIfObject:pluginLowerIdentifier notKindOfClass:[NSString class]];
    if (![[MainController publicController] isPluginAnycaseIdentifierValid:pluginLowerIdentifier]) {
        [NSException raise:@"Illegal argument" format:@"%@ is not a valid plugin identifier", pluginLowerIdentifier];
    }
    NSString* key = [NSString stringWithFormat:@"%p", observer];
    id nsObserver = observerInstanceForNSNotificationCenterObserver[key];

    [[NSNotificationCenter defaultCenter] removeObserver:nsObserver name:[AppDelegate nsNotificationNameForPluginLowerIdentifier:pluginLowerIdentifier] object:nil];
    
    [observerInstanceForNSNotificationCenterObserver removeObjectForKey:key];
}

#pragma mark - UIAlertViewDelegate

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (alertView == self.pushNotifsReasonAlert) {
        [self observeAndStartDeviceRegistrationProcessOnOS];
        //now starting registration to iOS
        self.pushNotifsReasonAlert = nil;
    }
}

#pragma mark - AppDelegate registration notifications

- (void)registrationSuccessNotification:(NSNotification*)notification {
    NSString* token = notification.userInfo[kPushDeviceTokenStringKey];
    [self saveNotificationsDeviceToken:token];
    NSLog(@"-> Registration to push notifications succeeded. Device token has been saved.");
    for (PushNotifDeviceRegistrationObserver* observer in self.regObservers) {
        if (observer.successBlock) {
            observer.successBlock();
        }
    }
    [self cleanUpAfterRegistrationProcess];
}

- (void)registrationFailureNotification:(NSNotification*)notification {
    NSLog(@"!! ERROR: registration to push notifications failed.");
    [PushNotifController deleteNotificationsDeviceToken];
    for (PushNotifDeviceRegistrationObserver* observer in self.regObservers) {
        if (observer.failureBlock) {
            observer.failureBlock(PushNotifRegistrationErrorInternal);
        }
    }
    [self cleanUpAfterRegistrationProcess];
}

#pragma mark - Private utils

+ (void)unregisterAfterLogout {
    [[UIApplication sharedApplication] unregisterForRemoteNotifications]; //even though doc says it should be used in rare occasions only, we really want to prevent newly logged in user from receiving old notifications. This is a good way (if unregistraton to server fails).
    NSString* tokenToUnregister = [self notificationsDeviceToken];
    if (tokenToUnregister) {
        [pushNotifService cancelOperationsForDelegate:unregistrationDelegate];
        if (!pushNotifService) {
            pushNotifService = [PushNotifService sharedInstanceToRetain];
        }
        if (!unregistrationDelegate) {
            unregistrationDelegate = [PushNotifDeviceRegistrationObserver new];
            unregistrationDelegate.successBlock = ^{
                //token has been successfully demapped server-side, we can delete the token
                //and release service
                [self deleteNotificationsDeviceToken];
                pushNotifService = nil;
                unregistrationDelegate = nil;
                NSLog(@"-> PushNotif device token was successfully unregistered on server and locally after logout");
            };
            unregistrationDelegate.failureBlock = ^(PushNotifDeviceRegistrationError error){
                //we absolutely need to keep going, add just a time to not be too fast
                NSLog(@"!! ERROR: PushNotif device token unregistration request to server failed, retrying in 2 seconds...");
                [NSTimer scheduledTimerWithTimeInterval:2.0 target:self selector:@selector(unregisterAfterLogout) userInfo:nil repeats:NO];
            };
        }
        NSLog(@"-> PushNotif: starting unregistration request to server (token: %@).....", tokenToUnregister);
        [pushNotifService deleteMappingWithDummy:@"dummy" delegate:unregistrationDelegate];
    }
}

- (void)observeAndStartDeviceRegistrationProcessOnOS {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(registrationSuccessNotification:) name:AppDidSucceedToRegisterToNotifications object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(registrationFailureNotification:) name:AppDidFailToRegisterToNotifications object:nil];
    [[UIApplication sharedApplication] registerForRemoteNotificationTypes:UIRemoteNotificationTypeAlert|UIRemoteNotificationTypeBadge|UIRemoteNotificationTypeSound];
}

- (void)cleanUpAfterRegistrationProcess {
    [[NSNotificationCenter defaultCenter] removeObserver:self name:AppDidSucceedToRegisterToNotifications object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:AppDidFailToRegisterToNotifications object:nil];
    [self.regObservers removeAllObjects];
    self.pushNotifsReasonAlert = nil; //should be done already
}

- (BOOL)notificationsEnabled {
    UIRemoteNotificationType enabledTypes = [[UIApplication sharedApplication] enabledRemoteNotificationTypes];
    BOOL enabled = ((enabledTypes & UIRemoteNotificationTypeAlert));
    return enabled;
}

- (void)saveNotificationsDeviceToken:(NSString*)token {
    [PCUtils throwExceptionIfObject:token notKindOfClass:[NSString class]];
    [[NSUserDefaults standardUserDefaults] setObject:token forKey:kNotificationsDeviceTokenKey];
    [[NSUserDefaults standardUserDefaults] synchronize];
    notificationsDeviceTokenCache = [token copy];
}

+ (void)deleteNotificationsDeviceToken {
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:kNotificationsDeviceTokenKey];
    [[NSUserDefaults standardUserDefaults] synchronize];
    notificationsDeviceTokenCache = nil;
}

#pragma mark - PluginController overrides

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"PushNotifPlugin", @"");
}

+ (NSString*)identifierName {
    return @"PushNotif";
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [pushNotifService cancelOperationsForDelegate:unregistrationDelegate];
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}

@end
