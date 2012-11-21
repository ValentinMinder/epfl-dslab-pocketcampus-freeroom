//
//  MainController2.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "ZUUIRevealController.h"

@class PluginController;

@interface MainController2 : NSObject<ZUUIRevealControllerDelegate>

- (id)initWithWindow:(UIWindow*)window;
- (void)refreshDisplayedPlugin;

- (BOOL)setActivePluginWithIdentifier:(NSString*)identifier animated:(BOOL)animated; //returns YES if plugin must be indeed marked as selected. NO otherwise

- (void)mainMenuIsReady;

- (void)adaptInitializedNavigationOrSplitViewControllerOfPluginController:(PluginController*)pluginController;

- (void)showGlobalSettings;

@end
