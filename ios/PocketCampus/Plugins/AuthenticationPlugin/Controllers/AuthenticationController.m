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


#import "AuthenticationController.h"

#import "AuthenticationViewController2.h"

#pragma mark - PCLoginObserver implementation

@implementation PCLoginObserver

@synthesize observer, operationIdentifier, successBlock, userCancelledBlock, failureBlock;

- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    return [self isEqualToPCLoginObserver:object];
}

- (BOOL)isEqualToPCLoginObserver:(PCLoginObserver*)loginObserver {
    return self.observer == loginObserver.observer && (!self.operationIdentifier || [self.operationIdentifier isEqual:loginObserver.operationIdentifier]);
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    hash += [self.observer hash];
    if (self.operationIdentifier) {
        hash += [self.operationIdentifier hash];
    }
    return hash;
}

@end

#pragma mark - AuthenticationController implementation

static AuthenticationController* instance __weak = nil;
static AuthenticationController* instanceStrong __strong = nil;

@interface AuthenticationController ()<AuthenticationServiceDelegate, AuthenticationDelegate>

@property (nonatomic, strong) AuthenticationViewController2* authenticationViewController;
@property (nonatomic, strong) PCNavigationController* authenticationNavigationController;
@property (nonatomic, strong) AuthenticationService* authService;
@property (nonatomic, strong) NSMutableSet* loginObservers;

@property (nonatomic, strong) NSString* tequilaToken;

@end

@implementation AuthenticationController

@synthesize pocketCampusAuthSessionId = _pocketCampusAuthSessionId;

#pragma mark - Init

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"AuthenticationController cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super init];
        if (self) {
            instance = self;
        }
        return self;
    }
}

+ (instancetype)sharedInstance {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instanceStrong = [self sharedInstanceToRetain];
    });
    return instanceStrong;
}

#pragma mark - PluginController

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

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"AuthenticationPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Authentication";
}

+ (void)initObservers {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [[NSNotificationCenter defaultCenter] addObserverForName:kAuthenticationLogoutNotification object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *notification) {
            CLSNSLog(@"-> Authentication received %@ notification. Deleting auth sessionId.", kAuthenticationLogoutNotification);
            [[AuthenticationController sharedInstance] deletePocketCampusAuthSessionId];
        }];
    });
}

#pragma mark - Standard authentication

/*- (void)authToken:(NSString*)token presentationViewController:(UIViewController*)presentationViewController delegate:(id<AuthenticationDelegate>)delegate {
    NSString* savedPassword = [AuthenticationService savedPasswordForUsername:[AuthenticationService savedUsername]];
    self.authenticationViewController = [[AuthenticationViewController alloc] init];
    if (savedPassword) {
        self.authenticationViewController.presentationMode = PresentationModeTryHidden;
        self.authenticationViewController.viewControllerForPresentation = presentationViewController;
        self.authenticationViewController.showSavePasswordSwitch = YES;
        self.authenticationViewController.hideGasparUsageAccountMessage = YES;
        [self.authenticationViewController authenticateSilentlyToken:token delegate:delegate];
    } else {
        self.authenticationViewController.presentationMode = PresentationModeModal;
        self.authenticationViewController.viewControllerForPresentation = presentationViewController;
        self.authenticationViewController.showSavePasswordSwitch = YES;
        self.authenticationViewController.hideGasparUsageAccountMessage = YES;
        self.authenticationViewController.delegate = delegate;
        self.authenticationViewController.token = token;
        PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:self.authenticationViewController]; //so that nav bar is shown
        navController.modalPresentationStyle = UIModalPresentationFormSheet;
        [presentationViewController presentViewController:navController animated:YES completion:^{
            [self.authenticationViewController focusOnInput];
        }];
    }
    
}*/

- (void)authToken:(NSString*)token presentationViewController:(UIViewController*)presentationViewController delegate:(id<AuthenticationDelegate>)delegate {
    NSString* savedUsername = [AuthenticationService savedUsername];
    NSString* savedPassword = [AuthenticationService savedPasswordForUsername:savedUsername];
    if (savedUsername && savedPassword) {
        [self.authService loginToTequilaWithUser:savedUsername password:savedPassword delegate:self];
    } else {
        self.authenticationViewController = [[AuthenticationViewController2 alloc] init];
        self.authenticationViewController.state = AuthenticationViewControllerStateAskCredentials;
        self.authenticationViewController.showCancelButton = YES;
        self.authenticationViewController.showSavePasswordSwitch = YES;
#warning set savepassword switch value
        self.authenticationViewController.username = savedUsername;
        self.authenticationViewController.password = savedUsername ? savedPassword : nil; //don't set password if unknown username. Should actually never happen.
        __weak __typeof(self) welf = self;
        [self.authenticationViewController setLoginBlock:^(NSString* username, NSString* password, BOOL savePassword) {
            [welf.authService loginToTequilaWithUser:username password:password delegate:welf];
            welf.authenticationViewController.state = AuthenticationViewControllerStateLoading;
#warning save savepassword switch value
        }];
        
        self.authenticationNavigationController = [[PCNavigationController alloc] initWithRootViewController:self.authenticationViewController];
        [presentationViewController presentViewController:self.authenticationNavigationController animated:YES completion:^{
            [self.authenticationViewController focusOnInput];
        }];
    }
}

#pragma mark - New-style authentication

- (void)addLoginObserver:(id)observer success:(VoidBlock)success userCancelled:(VoidBlock)userCancelled failure:(VoidBlock)failure {
    if (!instanceStrong) {
        [NSException raise:@"Illegal use" format:@"you must use [AuthenticationController sharedInstance] instead of [AuthenticationController sharedInstanceToRetain] to be able to use addLoginObserver:..."];
    }
    @synchronized(self) {
        PCLoginObserver* loginObserver = [[PCLoginObserver alloc] init];
        loginObserver.observer = observer;
        loginObserver.successBlock = success;
        loginObserver.operationIdentifier = nil;
        loginObserver.userCancelledBlock = userCancelled;
        loginObserver.failureBlock = failure;
        if (!self.loginObservers) {
            self.loginObservers = [NSMutableSet set];
        }
        [self.loginObservers addObject:loginObserver];
        if (!self.authService) {
            self.authService = [AuthenticationService sharedInstanceToRetain];
            [self.authService getAuthTequilaTokenWithDelegate:self];
        }
    }
}

- (void)removeLoginObserver:(id)observer {
    @synchronized(self) {
        for (PCLoginObserver* loginObserver in [self.loginObservers copy]) {
            if (loginObserver.observer == observer) {
                [self.loginObservers removeObject:loginObserver];
                break;
            }
        }
    }
}

#pragma mark Private

- (void)cleanAndNotifySuccessToObservers {
    self.authService = nil;
    self.tequilaToken = nil;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in self.loginObservers) {
            loginObserver.successBlock();
        }
        [self.loginObservers removeAllObjects];
    }
}

- (void)cleanAndNotifyUserCancelledToObservers {
    self.authService = nil;
    self.tequilaToken = nil;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in self.loginObservers) {
            loginObserver.userCancelledBlock();
        }
        [self.loginObservers removeAllObjects];
    }
}

- (void)cleanAndNotifyFailureToObservers {
    self.authService = nil;
    self.tequilaToken = nil;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in self.loginObservers) {
            loginObserver.failureBlock();
        }
        [self.loginObservers removeAllObjects];
    }
}

#pragma mark - AuthenticationServiceDelegate

- (void)loginToTequilaDidSuceedWithTequilaCookie:(NSHTTPCookie*)tequilaCookie {
    if (self.authenticationViewController) {
        NSString* username = self.authenticationViewController.username;
        NSString* password = self.authenticationViewController.password;
        if (!username || !password) {
            CLSNSLog(@"!! ERROR: cannot save credentials because self.authenticationViewController username/passowrd is nil");
        } else {
            [AuthenticationService saveUsername:username];
            if (self.authenticationViewController.savePasswordSwitchValue) {
                [AuthenticationService savePassword:password forUsername:username];
            } else {
                [AuthenticationService deleteSavedPasswordForUsername:username];
            }
        }
    }
    
    if (self.token && self.authenticationNavigationController) { //means was presented for in-plugin login
        [self.authService authenticateToken:self.tequilaToken withTequilaCookie:tequilaCookie delegate:self];
    } else { //mean user just wanted to login to tequila without loggin in to service. From settings for example.
        [self.authenticationViewController setState:AuthenticationViewControllerStateLoggedIn animated:YES];
    }
}

- (void)loginToTequilaFailedWithReason:(AuthenticationTequilaLoginFailureReason)reason {
    switch (reason) {
        case AuthenticationTequilaLoginFailureReasonBadCredentials:
        {
            if (self.authenticationViewController) {
                NSString* username = self.authenticationViewController.username;
                if (username) {
                    [AuthenticationService deleteSavedPasswordForUsername:username];
                }
                self.authenticationViewController.state = AuthenticationTequilaLoginFailureReasonBadCredentials;
                [self.authenticationViewController focusOnInput];
            } else {
                NSString* username = [AuthenticationService savedUsername];
                if (username) {
                    [AuthenticationService deleteSavedPasswordForUsername:username];
                }
                UIViewController* rootViewController = [[[[UIApplication sharedApplication] windows] firstObject] rootViewController];
                [self authToken:self.tequilaToken presentationViewController:rootViewController delegate:self];
                self.authenticationViewController.state = AuthenticationViewControllerStateWrongCredentials;
            }
            break;
        }
        default:
#warning error handling
            break;
    }
}

- (void)authenticateDidSucceedForToken:(NSString*)token tequilaCookie:(NSHTTPCookie*)tequilaCookie {
#error WAS HERE
}

- (void)authenticateFailedForToken:(NSString*)token tequilaCookie:(NSHTTPCookie*)tequilaCookie {
    
}

- (void)getAuthTequilaTokenDidReturn:(AuthTokenResponse*)response {
    switch (response.statusCode) {
        case AuthStatusCode_OK:
        {
            self.tequilaToken = response.tequilaToken;
            UIViewController* rootViewController = [[[[UIApplication sharedApplication] windows] firstObject] rootViewController];
            [self authToken:response.tequilaToken presentationViewController:rootViewController delegate:self];
            break;
        }
        default:
            [self cleanAndNotifyFailureToObservers];
            break;
    }
}

- (void)getAuthTequilaTokenFailed {
    [self cleanAndNotifyFailureToObservers];
}

- (void)getAuthSessionForRequest:(AuthSessionRequest *)request didReturn:(AuthSessionResponse *)response {
    switch (response.statusCode) {
        case AuthStatusCode_OK:
            [self setPocketCampusAuthSessionId:response.sessionId persist:YES];
            [self cleanAndNotifySuccessToObservers];
            break;
        case AuthStatusCode_NETWORK_ERROR:
            [self cleanAndNotifyFailureToObservers];
            break;
        case AuthStatusCode_INVALID_SESSION:
            [self deletePocketCampusAuthSessionId];
            [self cleanAndNotifyFailureToObservers];
            break;
        default:
            [self cleanAndNotifyFailureToObservers];
            break;
    }
}

- (void)getAuthSessionFailedForRequest:(AuthSessionRequest *)request {
    [self cleanAndNotifyFailureToObservers];
}

- (void)serviceConnectionToServerFailed {
    [self cleanAndNotifyFailureToObservers];
}

#pragma mark - AuthenticationDelegate

- (void)authenticationSucceededUserChoseToSavePassword:(BOOL)userChoseToRememberPassword {
    if (!self.tequilaToken) {
        CLSNSLog(@"!! ERROR: authentication succeeded but no saved tequila token. Notifying failure to observers.");
        [self cleanAndNotifyFailureToObservers];
        return;
    }
    AuthSessionRequest* request = [[AuthSessionRequest alloc] initWithTequilaToken:self.tequilaToken rememberMe:userChoseToRememberPassword];
    [self.authService getAuthSessionWithRequest:request delegate:self];
}

- (void)authenticationFailedWithReason:(AuthenticationFailureReason)reason {
    switch (reason) {
        case AuthenticationFailureReasonUserCancelled:
            [self cleanAndNotifyUserCancelledToObservers];
            break;
        case AuthenticationFailureReasonInvalidToken:
            [self cleanAndNotifyFailureToObservers];
            break;
        case AuthenticationFailureReasonInternalError:
            [self cleanAndNotifyFailureToObservers];
            break;
        default:
            [self cleanAndNotifyFailureToObservers];
            break;
    }
}

#pragma mark - SessionId persistence
#pragma mark Private

static NSString* const kAuthSessionIdPCConfigKey = @"PocketCampusAuthSessionId";

- (NSString*)pocketCampusAuthSessionId {
    if (_pocketCampusAuthSessionId) {
        return _pocketCampusAuthSessionId;
    }
    _pocketCampusAuthSessionId = [[PCConfig defaults] objectForKey:kAuthSessionIdPCConfigKey];
    return _pocketCampusAuthSessionId;
}

- (void)setPocketCampusAuthSessionId:(NSString*)sessionId persist:(BOOL)persist {
    if (sessionId) {
        [PCUtils throwExceptionIfObject:sessionId notKindOfClass:[NSString class]];
    }
    _pocketCampusAuthSessionId = sessionId;
    if (persist) {
        [[PCConfig defaults] setObject:sessionId forKey:kAuthSessionIdPCConfigKey];
    } else {
        [[PCConfig defaults] removeObjectForKey:kAuthSessionIdPCConfigKey];
    }
    [[PCConfig defaults] synchronize];
}

- (void)deletePocketCampusAuthSessionId {
    _pocketCampusAuthSessionId = nil;
    [[PCConfig defaults] removeObjectForKey:kAuthSessionIdPCConfigKey];
    [[PCConfig defaults] synchronize];
}

#pragma mark - Dealloc

- (void)dealloc
{
    @try {
        [[NSNotificationCenter defaultCenter] removeObserver:self];
    }
    @catch (NSException *exception) {}
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}

@end
