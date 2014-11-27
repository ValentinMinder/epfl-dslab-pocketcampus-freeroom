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

#import "PluginController.h"

#import "AuthenticationService.h"

#import "AuthenticationViewController.h"

/**
 * Delegation protocol for standard (old-style) authentication (see below)
 */
typedef enum {
    AuthenticationFailureReasonInvalidToken,
    AuthenticationFailureReasonUserCancelled,
    AuthenticationFailureReasonCannotAskForCredentials, //typically when authentication would require (non-saved) credentials but those cannot be asked to the user because target is running in an extension that does not support user input.
    AuthenticationFailureReasonInternalError
} AuthenticationFailureReason;

@protocol AuthenticationControllerDelegate

@required
- (void)authenticationSucceeded;
- (void)authenticationFailedWithReason:(AuthenticationFailureReason)reason;

@end

/**
 * Domain and codes used for NSError in addLoginObserver:... failure block
 */
extern NSString* kAuthenticationErrorDomain;
extern NSInteger kAuthenticationErrorCodeCouldNotAskForCredentials; //login was required but crendentials could not asked to the user (for e.g. in extension that does not support user input)
extern NSInteger kAuthenticationErrorCodeOther;

@interface PCLoginObserver : NSObject

@property (nonatomic, weak) id observer;
@property (nonatomic, copy) NSString* operationIdentifier;
@property (nonatomic, copy) VoidBlock successBlock;
@property (nonatomic, copy) VoidBlock userCancelledBlock;
@property (nonatomic, copy) void (^failureBlock)(NSError* error);

@end

@interface AuthenticationController : PluginController<PluginControllerProtocol>

/**
 * Same as sharedInstanceToRetain
 * Only indicates that sharedInstanceToRetain actually does not
 * need to be retained (singleton).
 */
+ (instancetype)sharedInstance;

/**
 * @return the view controller that shows which user is connected if so,
 * and allows to login/logout without the goal of authenticating a token.
 */
- (AuthenticationViewController*)statusViewController;


/**
 * @return the username that is currently logged in, or
 * nil of it does not exist.
 */
- (NSString*)loggedInUsername;

/**
 * ######### Standard authentication #########
 * (for plugins that do NOT authenticate using PocketCampus server.
 *  The plugin controller of these plugins can subclass PluginControllerAuthentified
 *  to benefit from easy support of login observeration management).
 *
 * Use this method to authenticate a tequila token.
 * Delegte MUST implement AuthenticationDelegate methods.
 * 
 * WARNING: this method cannot be called by multiple instances 
 * at the same time (1 delegate at a time). CRASH might occur if so.
 */
- (void)authenticateToken:(NSString*)token delegate:(id<AuthenticationControllerDelegate>)delegate;

/**
 * ######### New-style authentication #########
 * (for services that authenticate using PocketCampus server).
 *
 * Starts authentication procedure to PocketCampus server if not done already
 * and add observer to list of observers. On success, the PocketCampus session is
 * renewed and accessible via the property pocketCampusAuthSessionId.
 * All services that rely on PocketCampus authentication can then start their
 * requests (they should NOT access this property though, ServiceRequest does it
 * automatically). Observers are removed on success/userCancelled/failure.
 * 
 * This method ALWAYS starts the authentication process for the first observer,
 * i.e. that does check whether a PocketCampus session already exists. It is
 * responsability of plugins to call it only when necessary.
 * 
 * @param observer is unified by address. Meaning that if you already have registered observer with this method,
 * calling it again with the same observer will not do anything. You have to call removeLoginObserver: first
 * if you want to change the success/userCancelled/failure blocks.
 *
 * @param failure error is one of the kAuthenticationErrorCode
 */
- (void)addLoginObserver:(id)observer success:(VoidBlock)success userCancelled:(VoidBlock)userCancelled failure:(void (^)(NSError* error))failure;

/**
 * Removes observer from list of observers.
 * Does NOT cancel the authentication if currently in progress.
 * (finishes silently).
 */
- (void)removeLoginObserver:(id)observer;

/**
 * Renewed when addLoginObserver:... authentication succeeds.
 * You should though typically NOT access this property directly.
 * PCServiceRequest (thrift calls) automatically attaches this session to all requests.
 */
@property (nonatomic, readonly) NSString* pocketCampusAuthSessionId;

@end
