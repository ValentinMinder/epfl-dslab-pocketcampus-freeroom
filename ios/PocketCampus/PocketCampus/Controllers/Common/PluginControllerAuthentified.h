//
//  PluginControllerAuthentified.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 26.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PluginController.h"

#import "AuthenticationController.h"

/*
 * PluginController for plugins that require a tequila authentication should subclass PluginControllerAuthentified.
 * This class provides a way to manage authentication using the observer pattern.
 * For example, if both class A and B of plugin P require the user to authenticate, they can both add themselves
 * as observers, so that when authentication finishes for the plugin, the code-blocks they gave as parameters are be executed.
 * See MoodleController for an example.
 */

@interface PluginControllerAuthentified : PluginController

#pragma mark - Login observation

/*
 * This superclass method will add the observer to the observers list and instanciate the iVar authController.
 * Observers are uniquely identified by combination of observer address and operationIdentifier.
 * For practicity, observers are removed when any of the events occur (success, userCancelled, failure), i.e. cleanAndNotify* methods are called
 * IMPORTANT: plugins MUST override this method and call super. Typically, start request for tequila token on plugin's service.
 * See MoodleController for an example.
 */
- (void)addLoginObserver:(id)observer successBlock:(VoidBlock)successBlock userCancelledBlock:(VoidBlock)userCancelledblock failureBlock:(VoidBlock)failureBlock;

/*
 * Removes observer.
 * Cancels login operation if it was alone to observe.
 * IMPORTANT: plugins MUST override this method and call super. Typically, cancel login on service if no more observers.
 * See MoodleController for an example.
 */
- (void)removeLoginObserver:(id)observer;


/* 
 * Protected.
 */
- (void)cleanAndNotifySuccessToObservers;
- (void)cleanAndNotifyFailureToObservers;
- (void)cleanAndNotifyUserCancelledToObservers;
- (void)cleanAndNotifyConnectionToServerTimedOutToObservers;

#pragma mark - Properties
/*
 * Protected properties. 
 * Should only be used by sublcasses
 */
@property (nonatomic, strong) AuthenticationController* authController;
@property (nonatomic, strong) NSMutableArray* loginObservers;
@property (nonatomic) BOOL authenticationStarted;

@end
