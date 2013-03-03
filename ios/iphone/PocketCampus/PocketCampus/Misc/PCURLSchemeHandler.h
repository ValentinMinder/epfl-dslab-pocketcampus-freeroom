//
//  PCURLSchemeHandler.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 03.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

@class MainController;

@interface PCURLSchemeHandler : NSObject


/*
 * Returns YES if url has the valid format
 * i.e. pocketcampus://<pluginIdentifier>.plugin.pocketcampus.org/(<action>?<parameters>)
 * and <pluginIdentifier> exists
 */
- (BOOL)isSupportedPocketCampusURLScheme:(NSURL*)url;

/*
 * Returns viewcontroller executing action described in:
 * pocketcampus://<pluginIdentifier>.plugin.pocketcampus.org/<action>?<parameters>
 * Returns nil if <pluginIdentifier> does not exist or action / parameters not supported
 */
- (UIViewController*)viewControllerForPocketCampusURLScheme:(NSURL*)url;

/*
 * Private. Do not use from plugins. See MainControllerPublic to get instance.
 */
- (id)initWithMainController:(MainController*)mainController;

@end
