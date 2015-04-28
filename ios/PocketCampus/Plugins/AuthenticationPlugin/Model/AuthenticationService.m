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



#import "AuthenticationService.h"

#import "AFNetworking.h"

#import "PCPersistenceManager.h"

#import "SSKeychain.h"

@implementation AuthenticationService

NSString* const kAuthenticationTequilaCookieName = @"tequila_key";
NSString* const kAuthenticationLogoutNotification = @"kAuthenticationLogoutNotification";

NSString* const kAuthenticationEmailUserAttributeName = @"email";
NSString* const kAuthenticationGasparUserAttributeName = @"gaspar";
NSString* const kAuthenticationSciperUserAttributeName = @"sciper";
NSString* const kAuthenticationFirstnameUserAttributeName = @"firstname";
NSString* const kAuthenticationLastnameUserAttributeName = @"lastname";

static NSString* kTequilaOAuth2AuthURL = nil;
static NSString* kTequilaOAuth2RequestKey = @"requestkey";
static NSString* kTequilaOAuth2AuthApproveURL = nil;
static NSString* kTequilaOAuth2ReqCookieName = @"Tequila_req_OAuth2IdP";
static NSString* kTequilaOAuth2CodeKey = @"code";
static NSString* const kTequilaLoginURL = @"https://tequila.epfl.ch/cgi-bin/tequila/login";
static NSString* const kTequilaAuthURL = @"https://tequila.epfl.ch/cgi-bin/tequila/requestauth";

static NSString* const kLastUsedUseramesKey = @"lastUsedUsernames";
static NSString* const kKeychainServiceKey = @"PCGasparPassword";
static NSString* const kSavedUsernameKey = @"savedUsername";

static NSString* const kOperationUserInfoDelegateKey = @"delegate";

static AuthenticationService* instance __weak = nil;

#pragma mark - Init

+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        NSString* commaSeparatedScopes = [[authenticationConstants OAUTH2_SCOPES] componentsJoinedByString:@","];
        kTequilaOAuth2AuthURL = [@"https://tequila.epfl.ch/cgi-bin/OAuth2IdP/auth?response_type=code&redirect_uri=https%3A%2F%2Fpocketcampus.epfl.ch%2F&client_id=1b74e3837e50e21afaf2005f%40epfl.ch&scope=" stringByAppendingString:commaSeparatedScopes];
        kTequilaOAuth2AuthApproveURL = [kTequilaOAuth2AuthURL stringByAppendingString:@"&doauth=Approve"];
    });
}

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"AuthenticationService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"authentication" thriftServiceClientClassName:NSStringFromClass(AuthenticationServiceClient.class)];
        if (self) {
            instance = self;
        }
        return self;
    }
}

#pragma mark - ServiceProtocol

+ (id)sharedInstanceToRetain {
    @synchronized (self) {
        if (instance) {
            return instance;
        }
#if __has_feature(objc_arc)
        return [[[self class] alloc] init];
#else
        return [[[[self class] alloc] init] autorelease];
#endif
    }
}

#pragma mark -

+ (BOOL)areCredentialsSaved {
    return ([self savedPasswordForUsername:[self savedUsername]] != nil);
}

+ (NSString*)savedUsername {
    return (NSString*)[PCPersistenceManager objectForKey:kSavedUsernameKey pluginName:@"authentication"];
}

+ (BOOL)saveUsername:(NSString*)username {
    return [PCPersistenceManager saveObject:username forKey:kSavedUsernameKey pluginName:@"authentication"];
}

+ (NSString*)savedPasswordForUsername:(NSString*)username {
    NSError* error = nil;
    NSString* password = [SSKeychain passwordForService:kKeychainServiceKey account:username error:&error];
    if (error) {
        return nil;
    }
    return password;
}

+ (BOOL)savePassword:(NSString*)password forUsername:(NSString*)username {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        // Fixes an iOS 7 and 8 bug that makes keychain elements sometimes not accessible
        // in background/from extensions even after the device has been unlocked once.
        // With this accessibility value, the elements are always accessible after the user has
        // unlocked his phone once after restart. This is recommended value for background apps / extensions.
        // see http://stackoverflow.com/questions/10536859/ios-keychain-not-retrieving-values-from-background?answertab=votes#tab-top
        [SSKeychain setAccessibilityType:kSecAttrAccessibleAfterFirstUnlock];
    });
    return [SSKeychain setPassword:password forService:kKeychainServiceKey account:username];
}

+ (BOOL)deleteSavedPasswordForUsername:(NSString*)username {
    return [SSKeychain deletePasswordForService:kKeychainServiceKey account:username];
}

+ (void)enqueueLogoutNotification {
    NSNotification* notification = [NSNotification notificationWithName:kAuthenticationLogoutNotification object:nil userInfo:nil];
    [[NSNotificationQueue defaultQueue] enqueueNotification:notification postingStyle:NSPostASAP coalesceMask:NSNotificationCoalescingOnName forModes:nil]; //NSNotificationCoalescingOnName so that only 1 notif is added
}

- (void)getOAuth2TequilaTokenWithDelegate:(id<AuthenticationServiceDelegate>)delegate {
    NSMutableURLRequest* request = [[AFHTTPRequestSerializer serializer] requestWithMethod:@"GET" URLString:kTequilaOAuth2AuthURL parameters:nil error:nil];
    request.cachePolicy = NSURLRequestReloadIgnoringLocalAndRemoteCacheData;
    request.HTTPShouldHandleCookies = NO;
    AFHTTPRequestOperation* operation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
    id weakDelegate __weak = delegate;
    operation.userInfo = @{kOperationUserInfoDelegateKey:weakDelegate};
    void (^failedBlock)() = ^void () {
        if ([weakDelegate respondsToSelector:@selector(getOAuth2TequilaTokenFailed)]) {
            [weakDelegate getOAuth2TequilaTokenFailed];
        }
    };
    /*
     * We need the request to return 302 to succeed (=> we got a token)
     * Any other code is considered as failed.
     */
    [operation setRedirectResponseBlock:^NSURLRequest *(NSURLConnection *connection, NSURLRequest *request2, NSURLResponse *redirectResponse) {
        /*
         * Redirect block is actually called for original request as well.
         * This logic allows the first request to pursue but not the second one
         * => prevents redirect
         */
        if (!redirectResponse && connection.originalRequest == connection.currentRequest) {
            // Means request2 is the original request
            return request2;
        }
        return nil;
    }];
    [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
        /*
         * Completion block handles all responses with status code in the range 2xx,
         * which is a failure in our case (see above)
         */
        failedBlock();
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        switch (operation.response.statusCode) {
            case 302:
            {
                NSString* redirectURLString = operation.response.allHeaderFields[@"Location"];
                if (!redirectURLString) {
                    // Should not be possible if statusCode is 302
                    failedBlock();
                }
                NSString* requestkey = [PCUtils parametersDictionaryForURLString:redirectURLString][kTequilaOAuth2RequestKey];
                if (![requestkey isKindOfClass:[NSString class]]) {
                    failedBlock();
                }
                if ([weakDelegate respondsToSelector:@selector(getOAuth2TequilaTokenDidReturn:)]) {
                    [weakDelegate getOAuth2TequilaTokenDidReturn:requestkey];
                }
                break;
            }
            default:
                failedBlock();
                break;
        }
    }];
    [self.operationQueue addOperation:operation];
}

- (void)loginToTequilaWithUser:(NSString*)user password:(NSString*)password delegate:(id)delegate {    
    [PCUtils throwExceptionIfObject:user notKindOfClass:[NSString class]];
    [PCUtils throwExceptionIfObject:password notKindOfClass:[NSString class]];
    NSMutableURLRequest* request = [[AFHTTPRequestSerializer serializer] requestWithMethod:@"POST" URLString:kTequilaLoginURL parameters:@{@"username":user, @"password":password} error:nil];
    request.cachePolicy = NSURLRequestReloadIgnoringLocalCacheData;
    AFHTTPRequestOperation* operation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
    id weakDelegate __weak = delegate;
    operation.userInfo = @{kOperationUserInfoDelegateKey:weakDelegate};
    void (^failedBlock)(AuthenticationTequilaLoginFailureReason) = ^void (AuthenticationTequilaLoginFailureReason reason) {
        if ([weakDelegate respondsToSelector:@selector(loginToTequilaFailedWithReason:)]) {
            [weakDelegate loginToTequilaFailedWithReason:reason];
        }
    };
    [operation setRedirectResponseBlock:^NSURLRequest *(NSURLConnection *connection, NSURLRequest *request2, NSURLResponse *redirectResponse) {
        /*
         * Redirect block is actually called for original request as well.
         * This logic allows the first request to pursue but not the second one
         * => prevents redirect
         */
        if (!redirectResponse && connection.originalRequest == connection.currentRequest) {
            return request2;
        }
        return nil;
    }];
    [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSArray* allCookies = [NSHTTPCookie cookiesWithResponseHeaderFields:[operation.response allHeaderFields] forURL:operation.request.URL];
        NSHTTPCookie* tequilaCookie = nil;
        for (NSHTTPCookie* cookie in allCookies) {
            if ([cookie.name isEqualToString:kAuthenticationTequilaCookieName]) {
                tequilaCookie = cookie;
            }
        }
        if (tequilaCookie) {
            if ([weakDelegate respondsToSelector:@selector(loginToTequilaDidSuceedWithTequilaCookie:)]) {
                [weakDelegate loginToTequilaDidSuceedWithTequilaCookie:tequilaCookie];
            }
        } else {
            failedBlock(AuthenticationTequilaLoginFailureReasonBadCredentials);
        }
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        failedBlock(AuthenticationTequilaLoginFailureReasonOtherError);
    }];
    [self.operationQueue addOperation:operation];
}

- (void)getOAuth2CodeForTequilaToken:(NSString*)tequilaToken delegate:(id<AuthenticationServiceDelegate>)delegate {
    [PCUtils throwExceptionIfObject:tequilaToken notKindOfClass:[NSString class]];
    NSMutableURLRequest* request = [[AFHTTPRequestSerializer serializer] requestWithMethod:@"GET" URLString:kTequilaOAuth2AuthApproveURL parameters:nil error:nil];
    [request addValue:[NSString stringWithFormat:@"%@=%@", kTequilaOAuth2ReqCookieName, tequilaToken] forHTTPHeaderField:@"Cookie"];
    request.cachePolicy = NSURLRequestReloadIgnoringLocalCacheData;
    request.HTTPShouldHandleCookies = NO;
    AFHTTPRequestOperation* operation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
    id weakDelegate __weak = delegate;
    operation.userInfo = @{kOperationUserInfoDelegateKey:weakDelegate};
    void (^failedBlock)() = ^void () {
        if ([weakDelegate respondsToSelector:@selector(getOAuth2CodeFailedForTequilaToken:)]) {
            [weakDelegate getOAuth2CodeFailedForTequilaToken:tequilaToken];
        }
    };
    /*
     * We need the request to return 302 to succeed (=> we got a code)
     * Any other code is considered as failed.
     */
    [operation setRedirectResponseBlock:^NSURLRequest *(NSURLConnection *connection, NSURLRequest *request2, NSURLResponse *redirectResponse) {
        /*
         * Redirect block is actually called for original request as well.
         * This logic allows the first request to pursue but not the second one
         * => prevents redirect
         */
        if (!redirectResponse && connection.originalRequest == connection.currentRequest) {
            // Means request2 is the original request
            return request2;
        }
        return nil;
    }];
    [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
        /*
         * Completion block handles all responses with status code in the range 2xx,
         * which is a failure in our case (see above)
         */
        failedBlock();
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        switch (operation.response.statusCode) {
            case 302:
            {
                NSString* redirectURLString = operation.response.allHeaderFields[@"Location"];
                if (!redirectURLString) {
                    // Should not be possible if statusCode is 302
                    failedBlock();
                }
                NSString* codeString = [PCUtils parametersDictionaryForURLString:redirectURLString][kTequilaOAuth2CodeKey];
                if (![codeString isKindOfClass:[NSString class]]) {
                    failedBlock();
                }
                if ([weakDelegate respondsToSelector:@selector(getOAuth2CodeForTequilaToken:didReturn:)]) {
                    [weakDelegate getOAuth2CodeForTequilaToken:tequilaToken didReturn:codeString];
                }
                break;
            }
            default:
                failedBlock();
                break;
        }
    }];
    [self.operationQueue addOperation:operation];
}

- (void)authenticateToken:(NSString*)token withTequilaCookie:(NSHTTPCookie*)tequilaCookie delegate:(id)delegate {
    [PCUtils throwExceptionIfObject:token notKindOfClass:[NSString class]];
    [PCUtils throwExceptionIfObject:tequilaCookie notKindOfClass:[NSHTTPCookie class]];
    
    NSMutableURLRequest* request = [[AFHTTPRequestSerializer serializer] requestWithMethod:@"GET" URLString:kTequilaAuthURL parameters:@{@"requestkey":token} error:nil];
    request.cachePolicy = NSURLRequestReloadIgnoringLocalCacheData;
    [request addValue:[NSString stringWithFormat:@"%@=%@", kAuthenticationTequilaCookieName, tequilaCookie.value] forHTTPHeaderField:@"Cookie"];
    AFHTTPRequestOperation* operation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
    id weakDelegate __weak = delegate;
    operation.userInfo = @{kOperationUserInfoDelegateKey:weakDelegate};
    VoidBlock failedBlock = ^() {
        if ([weakDelegate respondsToSelector:@selector(authenticateFailedForToken:tequilaCookie:)]) {
            [weakDelegate authenticateFailedForToken:token tequilaCookie:tequilaCookie];
        }
    };
    
    /*
     * We need the request to return 302 to succeed (=> token is authenticated and valid)
     * Any other code is considered as failed. Particularly, 200 means redirect to credentials page => failure
     */
    [operation setRedirectResponseBlock:^NSURLRequest *(NSURLConnection *connection, NSURLRequest *request2, NSURLResponse *redirectResponse) {
        /*
         * Redirect block is actually called for original request as well.
         * This logic allows the first request to pursue but not the second one
         * => prevents redirect
         */
        if (!redirectResponse && connection.originalRequest == connection.currentRequest) {
            return request2;
        }
        return nil;
    }];
    [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
        /*
         * Completion block handles all responses with status code in the range 2xx,
         * which is a failure in our case (see above)
         */
        failedBlock();
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        switch (operation.response.statusCode) {
            case 302:
                if ([weakDelegate respondsToSelector:@selector(authenticateDidSucceedForToken:tequilaCookie:)]) {
                    [weakDelegate authenticateDidSucceedForToken:token tequilaCookie:tequilaCookie];
                }
                break;
            default:
                failedBlock();
                break;
        }
    }];
    [self.operationQueue addOperation:operation];
}

#pragma mark - Service methods

- (void)getOAuth2TokensFromCodeWithRequest:(AuthSessionRequest*)request delegate:(id<AuthenticationServiceDelegate>)delegate {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[AuthSessionRequest class]];
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getOAuth2TokensFromCode:);
    operation.delegateDidReturnSelector = @selector(getOAuth2TokensFromCodeForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getOAuth2TokensFromCodeFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getUserAttributesWithRequest:(UserAttributesRequest*)request delegate:(id<AuthenticationServiceDelegate>)delegate {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[UserAttributesRequest class]];
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getUserAttributes:);
    operation.delegateDidReturnSelector = @selector(getUserAttributesForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getUserAttributesFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getAuthTequilaTokenWithDelegate:(id<AuthenticationServiceDelegate>)delegate {
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getAuthTequilaToken);
    operation.delegateDidReturnSelector = @selector(getAuthTequilaTokenDidReturn:);
    operation.delegateDidFailSelector = @selector(getAuthTequilaTokenFailed);
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getAuthSessionWithRequest:(AuthSessionRequest*)request delegate:(id<AuthenticationServiceDelegate>)delegate {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[AuthSessionRequest class]];
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getAuthSession:);
    operation.delegateDidReturnSelector = @selector(getAuthSessionForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(getAuthSessionFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

#pragma mark - Service overrides

- (void)cancelOperationsForDelegate:(id<PCServiceDelegate>)delegate {
    for (NSOperation* operation in self.operationQueue.operations) {
        if ([operation isKindOfClass:[AFHTTPRequestOperation class]]) {
            AFHTTPRequestOperation* httpOperation = (AFHTTPRequestOperation*)operation;
            if (httpOperation.userInfo[kOperationUserInfoDelegateKey] == delegate) {
                [httpOperation setCompletionBlockWithSuccess:NULL failure:NULL];
                [httpOperation cancel];
            }
        }
    }
    [super cancelOperationsForDelegate:delegate];
}

#pragma mark - Dealloc

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}

@end