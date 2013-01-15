//
//  PluginControllerAuthentified.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 26.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PluginController.h"

#import "AuthenticationController.h"

@interface PluginControllerAuthentified : PluginController

#pragma mark - Login observation

/*
 * If not already started, starts the login procedure with Authentication plugin and MoodleService to get final MoodleSession.
 * Observer uniquely identified by combination of observer address and operationIdentifier string comparison.
 * For practicity, observer is removed when any  of the events occur (success, userCancelled, failure)
 * IMPORTANT: plugins MUST override this method and call super. Typically, start request for tequila token on plugin's service.
 */
- (void)addLoginObserver:(id)observer successBlock:(VoidBlock)successBlock userCancelledBlock:(VoidBlock)userCancelledblock failureBlock:(VoidBlock)failureBlock;

/*
 * Removes observer.
 * Cancels login operation if it was alone to observe.
 * IMPORTANT: plugins MUST override this method and call super. Typically, cancel login on service if no more observers.
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
