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



#import "PluginController.h"

#import "PushNotifService.h"

#import "AuthenticationController.h"

typedef enum {
    PushNotifRegistrationErrorInternal = 0,
    PushNotifRegistrationErrorUserDeniedBufferAlert = 1
} PushNotifDeviceRegistrationError;

typedef void (^PushNotifDeviceRegistrationSuccessBlock)(BOOL alertAllowed, BOOL badgeAllowed, BOOL soundAllowed);
typedef void (^PushNotifDeviceRegistrationFailureBlock)(PushNotifDeviceRegistrationError error);


@interface PushNotifController : PluginController<PluginControllerProtocol>

/**
 * Same as sharedInstanceToRetain
 * Only indicates that sharedInstanceToRetain actually does not
 * need to be retained (singleton).
 */
+ (instancetype)sharedInstance;

/**
 * @return token of the device, when notifications were accepted
 * nil otherwise (or in simulator)
 */
+ (NSString*)notificationsDeviceToken;

/**
 * Each plugin that needs push notifications should call this method to be sure that the
 * device is registered for push notifications. If the plugin is the first one to register
 * and <reason> will be displayed with possibilty for user to cancel, before prompting the
 * accept/reject iOS notifications message. If plugin is not the first, the device is already
 * registered and success or failure is immediately executed, user having accepted or 
 * rejected the first time respectively.
 * You may display a message telling the user what he is missing in your failure message.
 * Note: if the user has denied the "buffert" alert for notifications, it will be displayed again
 * when you call this method. It is *your* responsability to not spam user multiple times
 * if failure is PushNotifRegistrationErrorUserDeniedBufferAlert.
 * To know wether you have to register, you can check if [PushNotifController notificationsDeviceToken] is nil.
 */
- (void)registerDeviceForPushNotificationsWithPluginLowerIdentifier:(NSString*)pluginLowerIdentifier reason:(NSString*)reason success:(PushNotifDeviceRegistrationSuccessBlock)success failure:(PushNotifDeviceRegistrationFailureBlock)failure NS_EXTENSION_UNAVAILABLE_IOS("You cannot register for push notifications from extensions.");

/**
 * Any class from <pluginLowerIdentifier> can observe arrival of new notifications destinated
 * to <pluginLowerIdentifier>.
 * observer and pluginLowerIdentifier cannot be nil.
 * WARNING: classes must remove themselves with removeObserver:forPluginLowerIdentifier:
 * when being deallocated.
 */
- (void)addPushNotificationObserver:(id)observer forPluginLowerIdentifier:(NSString*)pluginLowerIdentifier newNotificationBlock:(void (^)(NSString* notifMessage, NSDictionary* notifFullDictionary))newNotificationBlock NS_EXTENSION_UNAVAILABLE_IOS("You cannot observe push notifications from extensions.");

/**
 * Classes that observe arrival of notifications (previous method) must use this method
 * to remove themselves as observers before being deallocated.
 */
- (void)removeObserver:(id)observer forPluginLowerIdentifier:(NSString*)pluginLowerIdentifier NS_EXTENSION_UNAVAILABLE_IOS("You cannot observe push notifications from extensions.");

@end
