/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

//  Created by Loïc Gardiol on 01.03.12.

@import Foundation;
@import UIKit;

#import "MainController.h"

#import "PluginNavigationController.h"
#import "PluginTabBarController.h"
#import "PluginSplitViewController.h"

/*Each plugin must have a controller named <plugin_name>Controller, and that subclasses PluginController. Is it NOT an instance of UIViewController*/

@interface PluginController : NSObject

/*
 * Either mainNavigationController, mainTabBarController, or mainSplitViewController can be instantiated.
 */
@property (strong) PluginNavigationController* mainNavigationController;
@property (strong) PluginTabBarController* mainTabBarController;
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
 * If either or both of these method(s) is/are implemented, this means that plugin supports
 * actions from URLs of the form:
 * 
 * pocketcampus://<pluginIdentifier>.plugin.pocketcampus.org/<action>?<parameters>
 *
 * Example: pocketcampus://map.plugin.pocketcampus.org/search?q=BC410
 * action = "search", parameters = {"q" => "BC410" }
 *
 * Some plugin might implement one or both method.
 * 
 * viewControllerForURLQueryAction:parameters:
 * Implement this method to support returning a view controller directly from the action.
 * This method is typically used internally to PocketCampus, between plugins.
 * Should return nil if the action/the parameters are not supported.
 *
 * handleURLQueryAction:parameters:
 * Implement this method to support getting actions and parameters being passed to the plugin
 * controller when the app is opened with a URL.
 * You can assume that your plugin is already getting foreground (no need to request it).
 * Should return YES if action was successfully handled, NO otherwise.
 */
- (UIViewController*)viewControllerForURLQueryAction:(NSString*)action parameters:(NSDictionary*)parameters;

- (BOOL)handleURLQueryAction:(NSString*)action parameters:(NSDictionary*)parameters;

/**
 * This method is *different* from viewControllerForURLQueryAction:parameters:
 * In this case, webURL can be any HTTP(S) URL, and if the plugin has a way to display the data
 * pointed by this URL via a view controller, it should return one, or nil otherwise.
 */
+ (UIViewController*)viewControllerForWebURL:(NSURL*)webURL;

/* 
 * Should return whether the plugin can be deallocated. 
 * Do not return NO unless there is an operation currently in progress that cannot be stopped.
 */
- (BOOL)canBeReleased;

/*
 * Called when menu is revealed and plugin shifted to the right.
 */
- (void)pluginWillLoseForeground;

/*
 * Called when plugin is presented or when it is shifted back
 * to the left after main menu was reaveled
 */
- (void)pluginDidEnterForeground;


@end