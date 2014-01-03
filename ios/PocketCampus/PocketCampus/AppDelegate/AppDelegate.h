//
//  AppDelegate.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MainController.h"

extern NSString* const kAppDelegateAppDidSucceedToRegisterForRemoteNotificationsNotification;
extern NSString* const kAppDelegatePushDeviceTokenStringUserInfoKey;
extern NSString* const kAppDelegateAppFailedToRegisterForRemoteNotificationsNotification;

@interface AppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;

+ (NSString*)nsNotificationNameForPluginLowerIdentifier:(NSString*)pluginLowerIdentifier;

@end
