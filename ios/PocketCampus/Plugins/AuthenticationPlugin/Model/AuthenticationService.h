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


#import "PCService.h"

#import "authentication.h"

typedef enum {
    AuthenticationTequilaLoginFailureReasonBadCredentials,
    AuthenticationTequilaLoginFailureReasonOtherError
} AuthenticationTequilaLoginFailureReason;


extern NSString* const kAuthenticationTequilaCookieName;
extern NSString* const kAuthenticationLogoutNotification;

extern NSString* const kAuthenticationEmailUserAttributeName;
extern NSString* const kAuthenticationGasparUserAttributeName;
extern NSString* const kAuthenticationSciperUserAttributeName;
extern NSString* const kAuthenticationFirstnameUserAttributeName;
extern NSString* const kAuthenticationLastnameUserAttributeName;

#pragma mark - AuthenticationServiceDelegate definition

@protocol AuthenticationServiceDelegate <PCServiceDelegate>

@optional

- (void)getOAuth2TequilaTokenDidReturn:(NSString*)token;
- (void)getOAuth2TequilaTokenFailed;
- (void)loginToTequilaDidSuceedWithTequilaCookie:(NSHTTPCookie*)tequilaCookie;
- (void)loginToTequilaFailedWithReason:(AuthenticationTequilaLoginFailureReason)reason;
- (void)getOAuth2CodeForTequilaToken:(NSString*)tequilaToken didReturn:(NSString*)codeString;
- (void)getOAuth2CodeFailedForTequilaToken:(NSString*)tequilaToken;

- (void)authenticateDidSucceedForToken:(NSString*)token tequilaCookie:(NSHTTPCookie*)tequilaCookie;
- (void)authenticateFailedForToken:(NSString*)token tequilaCookie:(NSHTTPCookie*)tequilaCookie;

#pragma mark - Service methods delegation

- (void)getOAuth2TokensFromCodeForRequest:(AuthSessionRequest*)request didReturn:(AuthSessionResponse*)response;
- (void)getOAuth2TokensFromCodeFailedForRequest:(AuthSessionRequest*)request;
- (void)getUserAttributesForRequest:(UserAttributesRequest*)request didReturn:(UserAttributesResponse*)response;
- (void)getUserAttributesFailedForRequest:(UserAttributesRequest*)request;


- (void)getAuthTequilaTokenDidReturn:(AuthTokenResponse*)response __attribute__((deprecated));
- (void)getAuthTequilaTokenFailed __attribute__((deprecated));
- (void)getAuthSessionForRequest:(AuthSessionRequest*)request didReturn:(AuthSessionResponse*)response __attribute__((deprecated));
- (void)getAuthSessionFailedForRequest:(AuthSessionRequest*)request __attribute__((deprecated));

@end

#pragma mark - AuthenticationService definition

@interface AuthenticationService : PCService<PCServiceProtocol>


+ (BOOL)areCredentialsSaved;
+ (NSString*)savedUsername;
+ (BOOL)saveUsername:(NSString*)username;
+ (NSString*)savedPasswordForUsername:(NSString*)username;
+ (BOOL)savePassword:(NSString*)password forUsername:(NSString*)username;
+ (BOOL)deleteSavedPasswordForUsername:(NSString*)username;
+ (void)enqueueLogoutNotification;


- (void)getOAuth2TequilaTokenWithDelegate:(id<AuthenticationServiceDelegate>)delegate;
- (void)loginToTequilaWithUser:(NSString*)user password:(NSString*)password delegate:(id<AuthenticationServiceDelegate>)delegate;
- (void)getOAuth2CodeForTequilaToken:(NSString*)tequilaToken delegate:(id<AuthenticationServiceDelegate>)delegate;

- (void)authenticateToken:(NSString*)token withTequilaCookie:(NSHTTPCookie*)tequilaCookie delegate:(id<AuthenticationServiceDelegate>)delegate;

#pragma mark - Service methods

/*
 - (AuthSessionResponse *) getOAuth2TokensFromCode: (AuthSessionRequest *) req;  // throws TException
 - (AuthTokenResponse *) getAuthTequilaToken;  // throws TException
 - (AuthSessionResponse *) getAuthSession: (AuthSessionRequest *) req;  // throws TException
 - (LogoutResponse *) destroyAllUserSessions: (LogoutRequest *) req;  // throws TException
 - (UserAttributesResponse *) getUserAttributes: (UserAttributesRequest *) req;  // throws TException
 - (AuthSessionResponse *) getAuthSessionId: (NSString *) tequilaToken;  // throws TException
 */

- (void)getOAuth2TokensFromCodeWithRequest:(AuthSessionRequest*)request delegate:(id<AuthenticationServiceDelegate>)delegate;
- (void)getUserAttributesWithRequest:(UserAttributesRequest*)request delegate:(id<AuthenticationServiceDelegate>)delegate;

- (void)getAuthTequilaTokenWithDelegate:(id<AuthenticationServiceDelegate>)delegate __attribute__((deprecated));
- (void)getAuthSessionWithRequest:(AuthSessionRequest*)request delegate:(id<AuthenticationServiceDelegate>)delegate __attribute__((deprecated));


@end
