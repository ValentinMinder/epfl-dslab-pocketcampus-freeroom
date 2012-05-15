//
//  AppDelegate.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MainController.h"

#import "AuthenticationService.h" //TEST

@interface AppDelegate : UIResponder <UIApplicationDelegate, AuthenticationCallbackDelegate>

@property (strong, nonatomic) UIWindow *window;
@property (assign) MainController* mainController;

@end
