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

//  Created by Loïc Gardiol on 07.10.12.

@import Foundation;

#pragma mark - MainControllerPublic

typedef enum {
    PluginWillLoseForegroundNotification = 1,
    PluginDidEnterForegroundNotification = 2
} PluginStateNotification;

@class PluginController;
@protocol PluginControllerProtocol;
@class PCURLSchemeHandler;

@class MainMenuViewController;

/**
 * Note: see MainController interface to get instance of MainControllerPublic
 */
@protocol MainControllerPublic <NSObject>

/**
 * A plugin can call this method on [MainController publicController] to tell that it wants to become active (foreground), after receiving
 * a notification for example. Returns YES if plugin will go foreground, NO if current plugin replied NO to canBeReleased
 */
- (BOOL)requestPluginToForeground:(NSString*)pluginIdentifierName;

/**
 * A plugin can call this method on [MainController publicController] to tell that it should not be kept into foreground
 * and should be released, for example when user has logged out. 
 * Returns YES if plugin will be deallocated, NO if [plugin canBeReleased] returns NO.
 */
- (BOOL)requestLeavePlugin:(NSString*)pluginIdentifierName;


/**
 * Plugins can observe their state changes via this method.
 * This method is an alternative to the methods defined by PluginControllerProtocol.
 * If a plugin both implements the latter and register to these notifications,
 * both will be triggered.
 */
- (void)addPluginStateObserver:(id)observer selector:(SEL)selector notification:(PluginStateNotification)notification pluginIdentifierName:(NSString*)pluginIdentifierName;

/**
 * Use this method to unregister to state changes notifications.
 * Every obsever that is register should unregister before being deallocated.
 * Observer cannot be nil.
 */
- (void)removePluginStateObserver:(id)observer;

/**
 * @return the view controller that is currently visible,
 * i.e. on which you can safely present a view controller and it be visible.
 * @discussion WARNING: you must check the returned view controller's presentedViewController
 * proprety. If it is not nil, it means that a view controller is currently being dismissed
 * and you must delay you presentation by at least 0.5 seconds.
 */
- (UIViewController*)currentTopMostViewController;

/**
 * Disables the screen edge pan gesture that allows to reveal the main menu
 * The gesture is automatically and always enabled back when changing plugin.
 */
- (void)beginIgnoringRevealMainMenuGesture;

/**
 * Re-enables the screen edge pan gesture that allows to reveal the main menu
 */
- (void)endIgnoringRevealMainMenuGesture;

- (PCURLSchemeHandler*)urlSchemeHandlerSharedInstance;

- (BOOL)handlePocketCampusURL:(NSURL*)url;

/**
 * Calls +viewControllerForWebURL: on all plugin controllers and returns
 * the first view controller returned, or nil.
 * See PluginController protocol definition for more information.
 * Returns nil in case of error.
 */
- (UIViewController*)viewControllerForWebURL:(NSURL*)url;


/**
 * @return wether plugin identifier (case insenstive) is valid
 * i.e. exists in this PocketCampus implementation
 */
- (BOOL)isPluginAnycaseIdentifierValid:(NSString*)anycaseIdentifier;


/**
 * @return localized plugin identifier for anycaseIdentifier (i.e. ignoring case)
 * nil if not valid
 */
- (NSString*)localizedPluginIdentifierForAnycaseIdentifier:(NSString*)anycaseIdentifier;

@end


#pragma mark - MainController

@interface MainController : NSObject<MainControllerPublic>

/**
 * Plugins can get instance of MainController with this class method to have access
 * to public methods of MainControllerPublic protocol (above)
 */
+ (id<MainControllerPublic>)publicController NS_EXTENSION_UNAVAILABLE_IOS("MainController is not available from extensions.");


/*
 * Private.
 * Should not be used by plugins. 
 */
- (id)initWithWindow:(UIWindow*)window;
- (void)setActivePluginWithIdentifier:(NSString*)identifier;
- (void)showGlobalSettings;

- (void)mainMenuStartedEditing;
- (void)mainMenuEndedEditing;
- (void)restoreDefaultMainMenu;

- (PluginController<PluginControllerProtocol>*)pluginControllerForPluginIdentifier:(NSString*)identifier;
- (BOOL)existsPluginWithIdentifier:(NSString*)identifier;

@property (nonatomic, readonly, strong) MainMenuViewController* mainMenuViewController;

@end
