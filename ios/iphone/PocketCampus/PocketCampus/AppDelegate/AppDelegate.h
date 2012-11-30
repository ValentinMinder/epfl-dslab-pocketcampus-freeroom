//
//  AppDelegate.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MainController.h"

#import "MainController2.h"

#import "GANTracker.h"

static NSString* AppDidSucceedToRegisterToNotifications __unused = @"AppDidSucceedToRegisterToNotifications";
static NSString* kPushDeviceTokenStringKey __unused = @"PushDeviceTokenString";
static NSString* AppDidFailToRegisterToNotifications __unused = @"AppDidFailToRegisterToNotifications";

@interface AppDelegate : UIResponder <UIApplicationDelegate, GANTrackerDelegate> {
    MainController* mainController;
}

@property (strong, nonatomic) UIWindow *window;
@property (retain) MainController* mainController;
@property (retain) MainController2* mainController2;

@end
