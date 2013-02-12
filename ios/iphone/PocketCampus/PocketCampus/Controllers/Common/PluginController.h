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

@interface PluginController : NSObject

@property (readonly) NSArray* toolbarItems;
/* Either mainNavigationController or mainSplitViewController can be instantiated. Not both. */
@property (strong) PluginNavigationController* mainNavigationController;
@property (strong) PluginSplitViewController* mainSplitViewController;

@property (strong) NSArray* menuRevealingGesureRecognizers;

@end

/* Protocol that each PluginController subclass should conform to */

@protocol PluginControllerProtocol <NSObject>

@required
/*
 * Must return an instance of the PluginController. This instance is a semi-singleton, meaning
 * that only 1 instance can live at a time, but the instance is released when not pointed to anymore.
 * This is why the caller should retain the instance (strong pointer in ARC or retain in manual ref. counting) 
 */
+ (id)sharedInstanceToRetain;

/*
 * Must return the localized name of the plugin for device language. 
 * Example: "Annuaire" in french for DirectoryController.
 */
+ (NSString*)localizedName;

/*
 * Must return the official name <name> of the plugin.
 * <name> is then used to find the <name>Controller class.
 * Examples: Directory, MyEdu, PushNotif.
 */
+ (NSString*)identifierName;

@optional

/*
 * This method is called for each plugin at app startup.
 * Plugins can implement this method to register observers
 * that are the only way to execute plugin code when the
 * PluginController is not instancied.
 * See CamiproController for an example.
 */
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