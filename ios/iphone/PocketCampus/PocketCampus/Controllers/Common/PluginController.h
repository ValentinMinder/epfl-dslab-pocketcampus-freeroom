//
//  PluginController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import "MainController.h"

#import "PluginNavigationController.h"
#import "PluginSplitViewController.h"

/*Each plugin must have a controller named <plugin_name>Controller, and that subclasses PluginController. Is it NOT an instance of UIViewController*/

@interface PluginController : NSObject {
    UIViewController* mainViewController __attribute__ ((deprecated));
    //PluginNavigationController* mainNavigationController;
    //PluginSplitViewController* mainSplitViewController;
}

@property (readonly) NSArray* toolbarItems;
/* Either mainNavigationController or mainSplitViewController can be instantiated. Not both. */
@property (readonly) UIViewController* mainViewController; //Deprecated
@property (strong) PluginNavigationController* mainNavigationController;
@property (strong) PluginSplitViewController* mainSplitViewController;

@property (strong) NSArray* menuRevealingGesureRecognizers;

@end

/* Protocol that each PluginController subclass should conform to */

@protocol PluginControllerProtocol <NSObject>

@required
+ (id)sharedInstance;
+ (NSString*)localizedName;
+ (NSString*)identifierName;

@optional
+ (void)initObservers;

/* 
 * Should return whether the plugin can be deallocated. 
 * Do not return NO unless there is an operation currently in progress that cannot be stopped.
 */
@optional
- (BOOL)canBeReleased;

/*
 * Called when menu is revealed and plugin shifted to the right.
 */
@optional
- (void)pluginWillLoseForeground;

/*
 * Called when plugin is presented or when it is shifted back
 * to the left after main menu was reaveled
 */
@optional
- (void)pluginDidEnterForeground;


@end