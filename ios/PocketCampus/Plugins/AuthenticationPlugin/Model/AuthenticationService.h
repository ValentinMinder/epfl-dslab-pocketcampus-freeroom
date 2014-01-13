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



#import "Service.h"

#import "authentication.h"

typedef enum {
    AuthenticationTequilaLoginFailureReasonBadCredentials,
    AuthenticationTequilaLoginFailureReasonOtherError
} AuthenticationTequilaLoginFailureReason;


extern NSString* const kAuthenticationTequilaCookieName;
extern NSString* const kAuthenticationLogoutNotification;
extern NSString* const kAuthenticationLogoutNotificationDelayedBoolUserInfoKey;

@interface AuthenticationService : Service<ServiceProtocol>

/*
 authentication service methods
 */

+ (BOOL)isLoggedIn;
+ (NSString*)savedUsername;
+ (BOOL)saveUsername:(NSString*)username;
+ (NSString*)savedPasswordForUsername:(NSString*)username;
+ (BOOL)savePassword:(NSString*)password forUsername:(NSString*)username;
+ (BOOL)deleteSavedPasswordForUsername:(NSString*)username;
+ (NSNumber*)savePasswordSwitchWasOn;
+ (BOOL)savePasswordSwitchState:(BOOL)isOn;
+ (void)enqueueLogoutNotificationDelayed:(BOOL)delayed; //set delayed to YES to inform the receiver of the notif. that it should logout only when user has finished (leaving plugin)

- (void)loginToTequilaWithUser:(NSString*)user password:(NSString*)password delegate:(id)delegate;
- (void)authenticateToken:(NSString*)token withTequilaCookie:(NSHTTPCookie*)tequilaCookie delegate:(id)delegate;

@end

@protocol AuthenticationServiceDelegate <ServiceDelegate>

@optional
- (void)loginToTequilaDidSuceedWithTequilaCookie:(NSHTTPCookie*)tequilaCookie;
- (void)loginToTequilaFailedWithReason:(AuthenticationTequilaLoginFailureReason)reason;

- (void)authenticateDidSucceedForToken:(NSString*)token tequilaCookie:(NSHTTPCookie*)tequilaCookie;
- (void)authenticateFailedForToken:(NSString*)token tequilaCookie:(NSHTTPCookie*)tequilaCookie;

@end

@protocol AuthenticationCallbackDelegate

@required
- (void)authenticationSucceeded;
- (void)userCancelledAuthentication;
- (void)invalidToken;

@end
