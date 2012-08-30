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

@interface AppDelegate : UIResponder <UIApplicationDelegate, GANTrackerDelegate> {
    MainController* mainController;
}

@property (strong, nonatomic) UIWindow *window;
@property (retain) MainController* mainController;

@end
