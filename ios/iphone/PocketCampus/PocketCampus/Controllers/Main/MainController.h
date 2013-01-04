//
//  MainController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "ZUUIRevealController.h"

#pragma mark - MainControllerPublic

typedef enum {
    PluginWillLoseForegroundNotification = 1,
    PluginDidEnterForegroundNotification = 2
} PluginStateNotification;

@class PluginController;
@class MainMenuViewController;

/*
 * Note: see MainController interface to get instance of MainControllerPublic
 */
@protocol MainControllerPublic <NSObject>

/* 
 * A plugin can call this method on [MainController publicController] to tell that it wants to become active (foreground), after receiving
 * a notification for example. Returns YES if plugin will go foreground, NO if current plugin replied NO to canBePassivated
 */
- (BOOL)requestPluginToForeground:(NSString*)pluginIdentifierName;

/*
 * A plugin can call this method on [MainController publicController] to tell that it should not be kept into foreground
 * for example, when user has logged out. Returns YES if plugin will be deallocated, NO if pluginIdentifierName is not allocated.
 */
- (BOOL)requestLeavePlugin:(NSString*)pluginIdentifierName;


/*
 * Plugins can observe their state changes via this method.
 * This method is an alternative to the methods defined by PluginControllerProtocol.
 * If a plugin both implements the latter and register to these notifications,
 * both will be triggered.
 */
- (void)addPluginStateObserver:(id)observer selector:(SEL)selector notification:(PluginStateNotification)notification pluginIdentifierName:(NSString*)pluginIdentifierName;

/*
 * Use this method to unregister to state changes notifications.
 * Every obsever that is register should unregister before being deallocated.
 * Observer cannot be nil.
 */
- (void)removePluginStateObserver:(id)observer;

@end


#pragma mark - MainController

@interface MainController : NSObject<ZUUIRevealControllerDelegate, MainControllerPublic>

/*
 * Plugins can get instance of MainController with this class method to have access
 * to public methods of MainControllerPublic protocol (above)
 */
+ (id<MainControllerPublic>)publicController;


/* 
 * Private.
 * Should not be used by plugins. 
 */
- (id)initWithWindow:(UIWindow*)window;
- (void)appDidReceiveMemoryWarning;
- (void)setActivePluginWithIdentifier:(NSString*)identifier;
- (void)mainMenuIsReady;
- (void)showGlobalSettings;

- (void)mainMenuStartedEditing;
- (void)mainMenuEndedEditing;

@property (nonatomic, readonly, strong) MainMenuViewController* mainMenuViewController;

@end
