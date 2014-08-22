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

@end

static NSString* const kNotificationsDeviceTokenKey = @"NotificationsDeviceToken";
static NSString* notificationsDeviceTokenCache __strong = nil;

static PushNotifController* instance __strong = nil;

@interface PushNotifController ()

@property (nonatomic, strong) UIAlertView* pushNotifsReasonAlert;
@property (nonatomic, strong) NSMutableArray* regObservers; //array of PushNotifDeviceRegistrationObserver

@property (nonatomic, strong) id remoteSuccessObserver;
@property (nonatomic, strong) id remoteFailureObserver;
@property (nonatomic, strong) id notifSettingsObserver;

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

+ (instancetype)sharedInstance {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

#pragma mark - PluginController

+ (id)sharedInstanceToRetain {
    return [self sharedInstance];
}

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

- (void)registerDeviceForPushNotificationsWithPluginLowerIdentifier:(NSString*)pluginLowerIdentifier reason:(NSString*)reason success:(PushNotifDeviceRegistrationSuccessBlock)success failure:(PushNotifDeviceRegistrationFailureBlock)failure {
    @synchronized(self) {
        NSString* token = [PushNotifController notificationsDeviceToken];
#warning test if pop-up already presented
        if (!token && reason && !self.pushNotifsReasonAlert) {
            //first plugin to ask will be the one that will get his reason poped-up
            NSString* localizedIdentifier = [[MainController publicController] localizedPluginIdentifierForAnycaseIdentifier:pluginLowerIdentifier];

            self.pushNotifsReasonAlert = [[UIAlertView alloc] initWithTitle:[NSString stringWithFormat:NSLocalizedStringFromTable(@"PushNotifsAlertTitleWithFormat", @"PushNotifPlugin", nil), localizedIdentifier] message:reason delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
        }
        
		PushNotifDeviceRegistrationObserver* regObserver = [PushNotifDeviceRegistrationObserver new];
		regObserver.successBlock = success;
		regObserver.failureBlock = failure;
        [self.regObservers addObject:regObserver];
		if (self.regObservers.count == 1) { //first observer, need to start device registration procedure
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


- (void)addPushNotificationObserver:(id)observer forPluginLowerIdentifier:(NSString*)pluginLowerIdentifier newNotificationBlock:(void (^)(NSString* notifMessage, NSDictionary* notifFullDictionary))newNotificationBlock {
#ifndef TARGET_IS_EXTENSION
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
#endif
}

- (void)removeObserver:(id)observer forPluginLowerIdentifier:(NSString*)pluginLowerIdentifier {
#ifndef TARGET_IS_EXTENSION
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
#endif
}

#pragma mark - UIAlertViewDelegate

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (alertView == self.pushNotifsReasonAlert) {
#ifndef TARGET_IS_EXTENSION
        [self observeAndStartDeviceRegistrationProcessOnOS];
#endif
        //now starting registration to iOS
        self.pushNotifsReasonAlert = nil;
    }
}

#pragma mark - Private utils

- (void)observeAndStartDeviceRegistrationProcessOnOS {
    UIApplication* sharedApplication = nil;
    NSString* remoteSuccessNotifName = nil;
    NSString* remoteFailureNotifName = nil;
    NSString* didRegisterSettingsNotifName = nil;
    NSString* tokenKey = nil;
#ifndef TARGET_IS_EXTENSION
    sharedApplication = [UIApplication sharedApplication];
    remoteSuccessNotifName = kAppDelegateAppDidSucceedToRegisterForRemoteNotificationsNotification;
    remoteFailureNotifName = kAppDelegateAppFailedToRegisterForRemoteNotificationsNotification;
    didRegisterSettingsNotifName = kAppDelegateAppDidRegisterUserNotificationSettingsNotification;
    tokenKey = kAppDelegatePushDeviceTokenStringUserInfoKey;
#endif
    if (!sharedApplication) {
        return;
    }
    
    __weak __typeof(self) welf = self;
    
    if ([UIUserNotificationSettings class]) { // >= iOS 8
        // Need to both register for remote notifs (not permission required from user) and notifs settings (permission required)
        welf.remoteSuccessObserver = [[NSNotificationCenter defaultCenter] addObserverForName:remoteSuccessNotifName object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *notif) {
            
            NSString* token = notif.userInfo[tokenKey];
            [welf.class saveNotificationsDeviceToken:token];
            
            CLSNSLog(@"-> (iOS 8) PushNotif device token registration success (%@). Now registering UIUserNotificationSettings...", token);
            
            welf.notifSettingsObserver = [[NSNotificationCenter defaultCenter] addObserverForName:didRegisterSettingsNotifName object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *notif) {
                
                [[NSNotificationCenter defaultCenter] removeObserver:welf.notifSettingsObserver];
                
                CLSNSLog(@"-> (iOS 8) UIUserNotificationSettings registration done. Calling success on observers.");
                
                UIUserNotificationSettings* currentSettings = [sharedApplication currentUserNotificationSettings];
                for (PushNotifDeviceRegistrationObserver* observer in welf.regObservers) {
                    if (observer.successBlock) {
                        observer.successBlock(currentSettings.types & UIUserNotificationTypeAlert, currentSettings.types & UIUserNotificationTypeBadge, currentSettings.types & UIUserNotificationTypeSound);
                    }
                }
                [welf cleanupAfterRegistration];
            }];
            [sharedApplication registerUserNotificationSettings:[UIUserNotificationSettings settingsForTypes:UIUserNotificationTypeAlert|UIUserNotificationTypeBadge|UIUserNotificationTypeSound categories:nil]];
        }];
        self.remoteFailureObserver = [[NSNotificationCenter defaultCenter] addObserverForName:remoteFailureNotifName object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *note) {
            
            [welf.class deleteNotificationsDeviceToken];
            
            CLSNSLog(@"-> (iOS 8) ERROR: PushNotif device token registration failure. Calling failure on observers.");
            
            for (PushNotifDeviceRegistrationObserver* observer in welf.regObservers) {
                if (observer.failureBlock) {
                    observer.failureBlock(PushNotifRegistrationErrorInternal);
                }
            }
            [welf cleanupAfterRegistration];
        }];
        [sharedApplication registerForRemoteNotifications];
    } else {
        // Prior to iOS 8, register for remote notifs and notifs UI is done in one shot
        self.remoteSuccessObserver = [[NSNotificationCenter defaultCenter] addObserverForName:remoteSuccessNotifName object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *notif) {
            
            NSString* token = notif.userInfo[tokenKey];
            [welf.class saveNotificationsDeviceToken:token];
            
            CLSNSLog(@"-> (iOS 7) PushNotif device token registration success. Calling success on observers.");
            
            for (PushNotifDeviceRegistrationObserver* observer in welf.regObservers) {
                if (observer.successBlock) {
                    observer.successBlock(YES, YES, YES); // before iOS 8, success in registring remote notifs ensured all registered types to be accepted.
                }
            }
            [welf cleanupAfterRegistration];
            
        }];
        self.remoteFailureObserver = [[NSNotificationCenter defaultCenter] addObserverForName:remoteFailureNotifName object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *note) {
            
            [welf.class deleteNotificationsDeviceToken];
            
            CLSNSLog(@"-> (iOS 7) ERROR: PushNotif device token registration failure. Calling failure on observers.");
            
            for (PushNotifDeviceRegistrationObserver* observer in welf.regObservers) {
                if (observer.failureBlock) {
                    observer.failureBlock(PushNotifRegistrationErrorInternal);
                }
            }
            [welf cleanupAfterRegistration];
            
        }];
#ifndef __IPHONE_8_0
        [sharedApplication registerForRemoteNotificationTypes:UIRemoteNotificationTypeAlert|UIRemoteNotificationTypeBadge|UIRemoteNotificationTypeSound];
#endif
    }
}

- (void)cleanupAfterRegistration {
    if (self.remoteSuccessObserver) {
        [[NSNotificationCenter defaultCenter] removeObserver:self.remoteSuccessObserver];
    }
    if (self.remoteFailureObserver) {
        [[NSNotificationCenter defaultCenter] removeObserver:self.remoteFailureObserver];
    }
    if (self.notifSettingsObserver) {
        [[NSNotificationCenter defaultCenter] removeObserver:self.notifSettingsObserver];
    }
    self.remoteSuccessObserver = nil;
    self.remoteFailureObserver = nil;
    self.notifSettingsObserver = nil;
    [self.regObservers removeAllObjects];
    self.pushNotifsReasonAlert = nil; //should be done already
}

- (BOOL)notificationsEnabled {
#ifdef TARGET_IS_EXTENSION
    return NO;
#else
    UIRemoteNotificationType enabledTypes = [[UIApplication sharedApplication] enabledRemoteNotificationTypes];
    BOOL enabled = ((enabledTypes & UIRemoteNotificationTypeAlert));
    return enabled;
#endif
}

+ (void)saveNotificationsDeviceToken:(NSString*)token {
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
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}

@end
