//
//  AppDelegate.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MainController.h"

#import "GANTracker.h"

/* __unsued put to remove warnings, Xcode is just stupid and does not see that they are used */

static NSString* AppDidSucceedToRegisterToNotifications __unused = @"AppDidSucceedToRegisterToNotifications";
static NSString* kPushDeviceTokenStringKey __unused = @"PushDeviceTokenString";
static NSString* AppDidFailToRegisterToNotifications __unused = @"AppDidFailToRegisterToNotifications";

static NSString* RemoteNotifForPluginName __unused = @"AppDidReceiveRemoteNotificationForPlugin";

@interface AppDelegate : UIResponder <UIApplicationDelegate, GANTrackerDelegate>

@property (strong, nonatomic) UIWindow *window;

+ (NSString*)nsNotificationNameForPluginLowerIdentifier:(NSString*)pluginLowerIdentifier;

@end
