/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */


#import "AuthenticationController.h"

#import "PushNotifController.h"

#import "PushNotifService.h"

#import "AppDelegate.h"

static NSMutableDictionary* observerInstanceForNSNotificationCenterObserver __strong = nil;

@interface PushNotifController ()<UIAlertViewDelegate>

@end

@implementation PushNotifDeviceRegistrationObserver

- (void)deleteMappingForDummy:(NSString*)dummy didReturn:(int32_t)status {
    //we consider that if it did return, it's ok
    self.successBlock();
}

- (void)deleteMappingFailedForDummy:(NSString*)dummy {
    self.failureBlock(0);
}

- (void)serviceConnectionToServerFailed {
    self.failureBlock(0);
}

@end

static NSString* const kNotificationsDeviceTokenKey = @"NotificationsDeviceToken";
static NSString* notificationsDeviceTokenCache __strong = nil;

static PushNotifController* instance __weak = nil;

static PushNotifService* pushNotifService __strong = nil; //used to retain service during unregistration
static PushNotifDeviceRegistrationObserver* unregistrationDelegate __strong = nil;

@interface PushNotifController ()

@property (nonatomic, strong) UIAlertView* pushNotifsReasonAlert;
@property (nonatomic, strong) NSMutableArray* regObservers; //array of PushNotifDeviceRegistrationObserver

@end

@implementation PushNotifController

#pragma mark - Init

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

//new push-notif system makes that plugins are responsible for user-to-device mapping, so no
//obvious relation with authentication logout. Might need to keep token even after logout.
/*+ (void)initObservers {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [[NSNotificationCenter defaultCenter] addObserverForName:kAuthenticationLogoutNotification object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *notification) {
            if ([self notificationsDeviceToken] == nil) {
                CLSNSLog(@"-> PushNotif received %@ notification. No saved device token to unregister, returning.", kAuthenticationLogoutNotification);
                return;
            }
            CLSNSLog(@"-> PushNotif received %@ notification. Now unregistrating from push notifs...", kAuthenticationLogoutNotification);
            [self unregisterAfterLogout];
        }];
    });
}*/

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"PushNotifPlugin", @"");
}

+ (NSString*)identifierName {
    return @"PushNotif";
}

#pragma mark - Public

+ (NSString*)notificationsDeviceToken {
    if (!notificationsDeviceTokenCache) {
        notificationsDeviceTokenCache = (NSString*)[[PCPersistenceManager userDefaultsForPluginName:@"pushnotif"] objectForKey:kNotificationsDeviceTokenKey];
    }
    return notificationsDeviceTokenCache;
}

- (void)registerDeviceForPushNotificationsWithPluginLowerIdentifier:(NSString*)pluginLowerIdentifier reason:(NSString*)reason success:(VoidBlock)success failure:(PushNotifDeviceRegistrationFailureBlock)failure {
    
    @synchronized(self) {
        
        NSString* token = [PushNotifController notificationsDeviceToken];
#warning test if pop-up already presented
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
    NSString* token = notification.userInfo[kAppDelegatePushDeviceTokenStringUserInfoKey];
    [self saveNotificationsDeviceToken:token];
    CLSNSLog(@"-> Registration to push notifications succeeded. Device token has been saved.");
    for (PushNotifDeviceRegistrationObserver* observer in self.regObservers) {
        if (observer.successBlock) {
            observer.successBlock();
        }
    }
    [self cleanUpAfterRegistrationProcess];
}

- (void)registrationFailureNotification:(NSNotification*)notification {
    CLSNSLog(@"!! ERROR: registration to push notifications failed.");
    [PushNotifController deleteNotificationsDeviceToken];
    for (PushNotifDeviceRegistrationObserver* observer in self.regObservers) {
        if (observer.failureBlock) {
            observer.failureBlock(PushNotifRegistrationErrorInternal);
        }
    }
    [self cleanUpAfterRegistrationProcess];
}

#pragma mark - Private utils

/*+ (void)unregisterAfterLogout {
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
                CLSNSLog(@"-> PushNotif device token was successfully unregistered on server and locally after logout");
            };
            unregistrationDelegate.failureBlock = ^(PushNotifDeviceRegistrationError error){
                //we absolutely need to keep going, add just a time to not be too fast
                CLSNSLog(@"!! ERROR: PushNotif device token unregistration request to server failed, retrying in 2 seconds...");
                [NSTimer scheduledTimerWithTimeInterval:2.0 target:self selector:@selector(unregisterAfterLogout) userInfo:nil repeats:NO];
            };
        }
        CLSNSLog(@"-> PushNotif: starting unregistration request to server (token: %@).....", tokenToUnregister);
        [pushNotifService deleteMappingWithDummy:@"dummy" delegate:unregistrationDelegate];
    }
}*/

- (void)observeAndStartDeviceRegistrationProcessOnOS {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(registrationSuccessNotification:) name:kAppDelegateAppDidSucceedToRegisterForRemoteNotificationsNotification object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(registrationFailureNotification:) name:kAppDelegateAppFailedToRegisterForRemoteNotificationsNotification object:nil];
    [[UIApplication sharedApplication] registerForRemoteNotificationTypes:UIRemoteNotificationTypeAlert|UIRemoteNotificationTypeBadge|UIRemoteNotificationTypeSound];
}

- (void)cleanUpAfterRegistrationProcess {
    [[NSNotificationCenter defaultCenter] removeObserver:self name:kAppDelegateAppDidSucceedToRegisterForRemoteNotificationsNotification object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:kAppDelegateAppFailedToRegisterForRemoteNotificationsNotification object:nil];
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
    [[PCPersistenceManager userDefaultsForPluginName:@"pushnotif"] setObject:token forKey:kNotificationsDeviceTokenKey];
    [[PCPersistenceManager userDefaultsForPluginName:@"pushnotif"] synchronize];
    notificationsDeviceTokenCache = [token copy];
}

+ (void)deleteNotificationsDeviceToken {
    [[PCPersistenceManager userDefaultsForPluginName:@"pushnotif"] removeObjectForKey:kNotificationsDeviceTokenKey];
    [[PCPersistenceManager userDefaultsForPluginName:@"pushnotif"] synchronize];
    notificationsDeviceTokenCache = nil;
}

#pragma mark - Dealloc

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
