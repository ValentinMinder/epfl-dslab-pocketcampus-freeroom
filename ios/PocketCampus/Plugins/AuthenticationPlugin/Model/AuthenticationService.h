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

#pragma mark - AuthenticationServiceDelegate definition

@protocol AuthenticationServiceDelegate <ServiceDelegate>

@optional

- (void)loginToTequilaDidSuceedWithTequilaCookie:(NSHTTPCookie*)tequilaCookie;
- (void)loginToTequilaFailedWithReason:(AuthenticationTequilaLoginFailureReason)reason;
- (void)authenticateDidSucceedForToken:(NSString*)token tequilaCookie:(NSHTTPCookie*)tequilaCookie;
- (void)authenticateFailedForToken:(NSString*)token tequilaCookie:(NSHTTPCookie*)tequilaCookie;

#pragma mark - Service methods delegation

- (void)getAuthTequilaTokenDidReturn:(AuthTokenResponse*)response;
- (void)getAuthTequilaTokenFailed;
- (void)getAuthSessionForRequest:(AuthSessionRequest*)request didReturn:(AuthSessionResponse*)response;
- (void)getAuthSessionFailedForRequest:(AuthSessionRequest*)request;
- (void)getAuthSessionIdWithToken:(NSString*)tequilaToken didReturn:(AuthSessionResponse*)response __attribute__((deprecated));
- (void)getAuthSessionIdFailedForToken:(NSString*)tequilaToken __attribute__((deprecated));

@end

#pragma mark - AuthenticationService definition

@interface AuthenticationService : Service<ServiceProtocol>


+ (BOOL)areCredentialsSaved;
+ (NSString*)savedUsername;
+ (BOOL)saveUsername:(NSString*)username;
+ (NSString*)savedPasswordForUsername:(NSString*)username;
+ (BOOL)savePassword:(NSString*)password forUsername:(NSString*)username;
+ (BOOL)deleteSavedPasswordForUsername:(NSString*)username;
+ (void)enqueueLogoutNotification;

- (void)loginToTequilaWithUser:(NSString*)user password:(NSString*)password delegate:(id<AuthenticationServiceDelegate>)delegate;
- (void)authenticateToken:(NSString*)token withTequilaCookie:(NSHTTPCookie*)tequilaCookie delegate:(id<AuthenticationServiceDelegate>)delegate;

#pragma mark - Service methods

/*
 - (AuthTokenResponse *) getAuthTequilaToken;
 - (AuthSessionResponse *) getAuthSession: (AuthSessionRequest *) req;
 - (AuthSessionResponse *) getAuthSessionId: (NSString *) tequilaToken; //deprecated
 */

- (void)getAuthTequilaTokenWithDelegate:(id<AuthenticationServiceDelegate>)delegate;
- (void)getAuthSessionWithRequest:(AuthSessionRequest*)request delegate:(id<AuthenticationServiceDelegate>)delegate;
- (void)getAuthSessionIdWithTequilaToken:(NSString*)tequilaToken delegate:(id<AuthenticationServiceDelegate>)delegate __attribute__((deprecated));


@end

typedef enum {
    AuthenticationFailureReasonInvalidToken,
    AuthenticationFailureReasonUserCancelled,
    AuthenticationFailureReasonInternalError
} AuthenticationFailureReason;

@protocol AuthenticationDelegate

@required
- (void)authenticationSucceededUserChoseToSavePassword:(BOOL)userChoseToRememberPassword;
- (void)authenticationFailedWithReason:(AuthenticationFailureReason)reason;

//- (void)userCancelledAuthentication __attribute__((deprecated));
//- (void)invalidToken __attribute__((deprecated));

@end
