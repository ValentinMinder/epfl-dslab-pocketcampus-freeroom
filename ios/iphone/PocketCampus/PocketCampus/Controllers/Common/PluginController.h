//
//  PluginController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import "MainController2.h"

#import "PluginNavigationController.h"
#import "PluginSplitViewController.h"

/*Each plugin must have a controller named <plugin_name>Controller, and that subclasses PluginController. Is it NOT an instance of UIViewController*/

@interface PluginController : NSObject {
    MainController2* mainController;
    UIViewController* mainViewController;
    PluginNavigationController* mainNavigationController;
    PluginSplitViewController* mainSplitViewController;
}

@property (readonly) NSArray* toolbarItems;
/* Either mainNavigationController or mainSplitViewController can be instantiated. Not both. */
@property (readonly) UIViewController* mainViewController; //Deprecated
@property (readonly) PluginNavigationController* mainNavigationController;
@property (readonly) PluginSplitViewController* mainSplitViewController;

@property (strong) NSArray* menuRevealingGesureRecognizers;

@end

@interface PluginController (PluginControllerWithObservers)

+ (void)initObservers;

@end

/* Protocol that each PluginController subclass should conform to */

@protocol PluginControllerProtocol <NSObject>

@required
- (id)initWithMainController:(MainController2*)mainController_;
+ (NSString*)localizedName;
+ (NSString*)identifierName;

@optional
- (void)pluginWillLoseFocus; //called when menu is revealed and plugin shiften to the right
- (void)pluginDidRegainActive; //called when plugin switches from background (passive) state to front (active) again or when menu is hidden again (=> plugin shifted back to the left)

@optional
- (void)refresh;
- (void)pluginDidBecomePassive; //called when user will switch to other plugin2. Plugin should stop any non-necessary activity

@end