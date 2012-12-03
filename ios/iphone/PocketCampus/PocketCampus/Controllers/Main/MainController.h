//
//  MainController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "ZUUIRevealController.h"

@class PluginController;

@protocol MainControllerPublic <NSObject>

/* 
 * A plugin can call this method on [MainController publicController] to tell that it wants to become active (foreground), after receiving
 * a notification for example. Returns YES if plugin will go foreground, NO if current plugin replied NO to canBePassivated
 */
- (BOOL)requestPluginToForeground:(NSString*)pluginIdentifierName;

/*
 * A plugin can call this method on [MainController publicController] to tell that it should not be kept into foreground
 * for example, user has logged out. Returns YES if plugin will be deallocated, NO if caller is not allocated.
 */
- (BOOL)requestLeavePlugin:(NSString*)pluginIdentifierName;

@end

@interface MainController : NSObject<ZUUIRevealControllerDelegate, MainControllerPublic>

+ (id<MainControllerPublic>)publicController;

/* should not be used by plugins */

- (id)initWithWindow:(UIWindow*)window;
- (void)refreshDisplayedPlugin;
- (void)setActivePluginWithIdentifier:(NSString*)identifier;
- (void)mainMenuIsReady;
- (void)adaptInitializedNavigationOrSplitViewControllerOfPluginController:(PluginController*)pluginController;
- (void)showGlobalSettings;


@end
