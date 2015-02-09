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




//  Created by Loïc Gardiol on 26.12.12.


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
- (void)addLoginObserver:(id)observer successBlock:(VoidBlock)successBlock userCancelledBlock:(VoidBlock)userCancelledblock failureBlock:(void (^)(NSError* error))failureBlock;

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
- (void)cleanAndNotifyUserCancelledToObservers;
- (void)cleanAndNotifyFailureToObserversWithAuthenticationErrorCode:(NSInteger)errorCode; //one of the kAuthenticationErrorCode defined in AuthenticationController
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
