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

#pragma mark - PCLoginObserver implementation

NSString* kAuthenticationErrorDomain = @"PocketCampus-Authentication";
NSInteger kAuthenticationErrorCodeCouldNotAskForCredentials = 1;
NSInteger kAuthenticationErrorCodeOther = 10;

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

static AuthenticationController* instance __strong = nil;

@interface AuthenticationController ()<AuthenticationServiceDelegate>

@property (nonatomic, strong) AuthenticationViewController* authenticationViewController;
@property (nonatomic, strong) PCNavigationController* authenticationNavigationController;
@property (nonatomic, strong) AuthenticationService* authService;
@property (nonatomic, weak) id<AuthenticationControllerDelegate> delegate;
@property (nonatomic, strong) NSMutableSet* loginObservers;
@property (nonatomic, strong) NSString* tequilaToken;
@property (atomic) BOOL observerAuthenticationStarted;

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
            instance.authService = [AuthenticationService sharedInstanceToRetain];
        }
        return self;
    }
}

+ (instancetype)sharedInstance {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

#pragma mark - PluginController

+ (id)sharedInstanceToRetain {
    return [self sharedInstance];
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

#pragma mark - Public

#pragma mark Standard authentication

- (AuthenticationViewController*)statusViewController {
    self.tequilaToken = nil;
    NSString* savedUsername = [AuthenticationService savedUsername];
    NSString* savedPassword = [AuthenticationService savedPasswordForUsername:savedUsername];
#ifndef TARGET_IS_EXTENSION
    self.authenticationViewController = [[AuthenticationViewController alloc] init];
#endif
    if (savedUsername && savedPassword) {
        self.authenticationViewController.state = AuthenticationViewControllerStateLoggedIn;
    } else if (self.pocketCampusAuthSessionId) {
        self.authenticationViewController.state = AuthenticationViewControllerStateLoggedIn;
    } else {
        self.authenticationViewController.state = AuthenticationViewControllerStateAskCredentials;
    }
    
    self.authenticationViewController.showCancelButton = NO;
    self.authenticationViewController.showSavePasswordSwitch = NO;
    self.authenticationViewController.username = savedUsername;
    self.authenticationViewController.password = savedUsername ? savedPassword : nil; //don't set password if unknown username. Should actually never happen.
    __weak __typeof(self) welf = self;
    [self.authenticationViewController setLoginBlock:^(NSString* username, NSString* password, BOOL savePassword) {
        if (!welf.authService) {
            welf.authService = [AuthenticationService sharedInstanceToRetain];
        }
        [welf.authService loginToTequilaWithUser:username password:password delegate:welf];
        welf.authenticationViewController.state = AuthenticationViewControllerStateLoading;
        [welf saveSavePasswordSwitchValue:savePassword];
    }];
    [self.authenticationViewController setLogoutBlock:^{
        [AuthenticationService deleteSavedPasswordForUsername:[AuthenticationService savedUsername]];
        [AuthenticationService saveUsername:nil];
        [AuthenticationService enqueueLogoutNotification];
        welf.authenticationViewController.username = nil;
        welf.authenticationViewController.password = nil;
        [welf.authenticationViewController setState:AuthenticationViewControllerStateAskCredentials animated:YES];
    }];
    [self.authenticationViewController setUserClearedUsernameBlock:^{
        [AuthenticationService saveUsername:nil];
    }];
    [self.authenticationViewController setBottomMessageBlock:^NSString *(AuthenticationViewController* authViewController) {
        NSString* message = nil;
        if (authViewController.state == AuthenticationViewControllerStateLoggedIn) {
            if ([AuthenticationService areCredentialsSaved]) {
                message = NSLocalizedStringFromTable(@"PasswordSavedAndPolicy", @"AuthenticationPlugin", nil);
            } else {
                message = NSLocalizedStringFromTable(@"PasswordNotSavedAndPolicy", @"AuthenticationPlugin", nil);
            }
        } else {
            if (authViewController.savePasswordSwitchValue) {
                message = [NSString stringWithFormat:@"%@\n\n%@", NSLocalizedStringFromTable(@"GasparAccountRequiredFor", @"AuthenticationPlugin", nil), NSLocalizedStringFromTable(@"WillSavePasswordAndPolicy", @"AuthenticationPlugin", nil)];
            } else {
                message = [NSString stringWithFormat:@"%@\n\n%@", NSLocalizedStringFromTable(@"GasparAccountRequiredFor", @"AuthenticationPlugin", nil), NSLocalizedStringFromTable(@"WillNotSavePasswordAndPolicy", @"AuthenticationPlugin", nil)];
            }
        }
        return message;
    }];
    return self.authenticationViewController;
}

- (NSString*)loggedInUsername {
    NSString* savedUsername = [AuthenticationService savedUsername];
    NSString* savedPassword = [AuthenticationService savedPasswordForUsername:savedUsername];
    if (self.pocketCampusAuthSessionId || (savedUsername && savedPassword)) {
        return savedUsername;
    }
    return nil;
}

- (void)authenticateToken:(NSString*)token delegate:(id<AuthenticationControllerDelegate>)delegate {
    self.delegate = delegate;
    [self startAuthenticationForToken:token];
}

#pragma mark New-style authentication

- (void)addLoginObserver:(id)observer success:(VoidBlock)success userCancelled:(VoidBlock)userCancelled failure:(void (^)(NSError* error))failure {
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
        [self restartAuthenticationProcessIfNeeded];
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

#pragma mark - AuthenticationServiceDelegate

/**
 * Step 0 (new-style authentication only)
 * Called when new tequila token was successfully requested
 */
- (void)getAuthTequilaTokenDidReturn:(AuthTokenResponse*)response {
    switch (response.statusCode) {
        case AuthStatusCode_OK:
        {
            if (response.tequilaToken) {
                [self startAuthenticationForToken:response.tequilaToken];
            } else {
                [self dismissAuthenticationViewControllerCompletion:^{
                    [self cleanAndNotifyFailureToObserversWithErrorCode:kAuthenticationErrorCodeOther];
                }];
            }
            break;
        }
        default:
            [self dismissAuthenticationViewControllerCompletion:^{
                [self cleanAndNotifyFailureToObserversWithErrorCode:kAuthenticationErrorCodeOther];
            }];
            break;
    }
}

/**
 * Step 0 (new-style authentication only)
 * Called when new tequila token request failed
 */
- (void)getAuthTequilaTokenFailed {
    [self dismissAuthenticationViewControllerCompletion:^{
        [self cleanAndNotifyFailureToObserversWithErrorCode:kAuthenticationErrorCodeOther];
    }];
}

/**
 * Step 1
 * Called after Tequila has been requested with username & password
 * and those were correct.
 */
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
    
    if (self.tequilaToken) { //means was presented for in-plugin login
        [self.authService authenticateToken:self.tequilaToken withTequilaCookie:tequilaCookie delegate:self];
    } else { //mean user just wanted to login to tequila without loggin in to service. From settings for example.
        [self.authenticationViewController setState:AuthenticationViewControllerStateLoggedIn animated:YES];
    }
}

/**
 * Step 1
 * Called after Tequila has been requested with username & password
 * but the request failed for some reason.
 */
- (void)loginToTequilaFailedWithReason:(AuthenticationTequilaLoginFailureReason)reason {
    switch (reason) {
        case AuthenticationTequilaLoginFailureReasonBadCredentials:
        {
            if (self.authenticationViewController) {
                NSString* username = self.authenticationViewController.username;
                if (username) {
                    [AuthenticationService deleteSavedPasswordForUsername:username];
                }
                self.authenticationViewController.state = AuthenticationViewControllerStateWrongCredentials;
                self.authenticationViewController.password = nil;
                [self.authenticationViewController focusOnInput];
            } else {
                NSString* username = [AuthenticationService savedUsername];
                if (username) {
                    [AuthenticationService deleteSavedPasswordForUsername:username];
                }
                if (self.tequilaToken) {
                    [self startAuthenticationForToken:self.tequilaToken]; //Delete wrong credentials and start again
                }
                self.authenticationViewController.password = nil;
                self.authenticationViewController.state = AuthenticationViewControllerStateWrongCredentials;
            }
            break;
        }
        default:
            [self serviceConnectionToServerFailed];
            break;
    }
}

/**
 * Step 2
 * Called when self.tequilaToken was successfully authenticated for user
 * (marked as valid for this user)
 */
- (void)authenticateDidSucceedForToken:(NSString*)token tequilaCookie:(NSHTTPCookie*)tequilaCookie {
    if (self.delegate) { //old-style authentication
        dispatch_async(dispatch_get_main_queue(), ^{
            id<AuthenticationControllerDelegate> delegate = self.delegate;
            [self cleanAndDismissAuthenticationViewControllerCompletion:NULL];
            if ([(NSObject*)delegate respondsToSelector:@selector(authenticationSucceeded)]) {
                [delegate authenticationSucceeded];
            }
        });
    } else { //new-style (PocketCampus session) authentication
        if (!self.tequilaToken) {
            CLSNSLog(@"!! ERROR: authentication succeeded but no saved tequila token. Notifying failure to observers.");
            [self dismissAuthenticationViewControllerCompletion:^{
                [self cleanAndNotifyFailureToObserversWithErrorCode:kAuthenticationErrorCodeOther];
            }];
            return;
        }
        BOOL savePassword = self.authenticationViewController ? self.authenticationViewController.savePasswordSwitchValue : YES;
        AuthSessionRequest* request = [[AuthSessionRequest alloc] initWithTequilaToken:self.tequilaToken rememberMe:savePassword];
        [self.authService getAuthSessionWithRequest:request delegate:self];
    }
}

/**
 * Step 2
 * Called when self.tequilaToken could NOT be authenticated for user
 * (network problem or invalid token)
 */
- (void)authenticateFailedForToken:(NSString*)token tequilaCookie:(NSHTTPCookie*)tequilaCookie {
    if (self.delegate) { //old-style authentication
        dispatch_async(dispatch_get_main_queue(), ^{
            id<AuthenticationControllerDelegate> delegate = self.delegate;
            [self cleanAndDismissAuthenticationViewControllerCompletion:^{
                if ([(NSObject*)delegate respondsToSelector:@selector(authenticationFailedWithReason:)]) {
                    [delegate authenticationFailedWithReason:AuthenticationFailureReasonInvalidToken];
                }
            }];
        });
    } else { //new-style (PocketCampus session) authentication
        [self dismissAuthenticationViewControllerCompletion:^{
            [self cleanAndNotifyFailureToObserversWithErrorCode:kAuthenticationErrorCodeOther];
        }];
    }
}

/**
 * Step 3 (new-style authentication only)
 * Called when a PocketCampus session was successfully returned by PC server
 */
- (void)getAuthSessionForRequest:(AuthSessionRequest *)request didReturn:(AuthSessionResponse *)response {
    switch (response.statusCode) {
        case AuthStatusCode_OK:
        {
            [self setPocketCampusAuthSessionId:response.sessionId persist:YES];
            [self dismissAuthenticationViewControllerCompletion:^{
                [self cleanAndNotifySuccessToObservers];
            }];
            break;
        }
        case AuthStatusCode_NETWORK_ERROR:
        {
            [self dismissAuthenticationViewControllerCompletion:^{
                [self cleanAndNotifyFailureToObserversWithErrorCode:kAuthenticationErrorCodeOther];
            }];
            break;
        }
        case AuthStatusCode_INVALID_SESSION:
        {
            [self deletePocketCampusAuthSessionId];
            [self dismissAuthenticationViewControllerCompletion:^{
                [self cleanAndNotifyFailureToObserversWithErrorCode:kAuthenticationErrorCodeOther];
            }];
            break;
        }
        default:
        {
            [self dismissAuthenticationViewControllerCompletion:^{
                [self cleanAndNotifyFailureToObserversWithErrorCode:kAuthenticationErrorCodeOther];
            }];
            break;
        }
    }
}

/**
 * Step 3 (new-style authentication only)
 * Called when PocketCampus session request failed
 */
- (void)getAuthSessionFailedForRequest:(AuthSessionRequest *)request {
    [self dismissAuthenticationViewControllerCompletion:^{
        [self cleanAndNotifyFailureToObserversWithErrorCode:kAuthenticationErrorCodeOther];
    }];
}

/**
 * All cases
 */
- (void)serviceConnectionToServerFailed {
    if (self.delegate) {
        [PCUtils showConnectionToServerTimedOutAlert];
        dispatch_async(dispatch_get_main_queue(), ^{
            id<AuthenticationControllerDelegate> delegate = self.delegate;
            [self cleanAndDismissAuthenticationViewControllerCompletion:^{
                if ([(NSObject*)delegate respondsToSelector:@selector(serviceConnectionToServerFailed)]) {
                    [(NSObject*)delegate performSelectorOnMainThread:@selector(serviceConnectionToServerFailed) withObject:nil waitUntilDone:YES];
                }
            }];
        });
    } else {
        if (self.tequilaToken) {
            [self dismissAuthenticationViewControllerCompletion:^{
                [self cleanAndNotifyFailureToObserversWithErrorCode:kAuthenticationErrorCodeOther];
            }];
        } else {
            [PCUtils showConnectionToServerTimedOutAlert];
            self.authenticationViewController.state = AuthenticationViewControllerStateAskCredentials;
        }
    }
}

/*#pragma mark - AuthenticationDelegate

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
}*/

#pragma mark - Private

- (void)restartAuthenticationProcessIfNeeded {
    if (!self.observerAuthenticationStarted && self.loginObservers.count > 0) {
        self.observerAuthenticationStarted = YES;
        [self.authService getAuthTequilaTokenWithDelegate:self];
    }
}

- (void)startAuthenticationForToken:(NSString*)token {
    [PCUtils throwExceptionIfObject:token notKindOfClass:[NSString class]];
    [PCUtils throwExceptionIfObject:self.authService notKindOfClass:[AuthenticationService class]]; //must be initialized at this point
    self.tequilaToken = token;
    NSString* savedUsername = [AuthenticationService savedUsername];
    NSString* savedPassword = [AuthenticationService savedPasswordForUsername:savedUsername];
    if (savedUsername && savedPassword) {
        [self.authService loginToTequilaWithUser:savedUsername password:savedPassword delegate:self];
    } else {
#ifndef TARGET_IS_EXTENSION
        self.authenticationViewController = [[AuthenticationViewController alloc] init];
        self.authenticationViewController.state = AuthenticationViewControllerStateAskCredentials;
        self.authenticationViewController.showCancelButton = YES;
        self.authenticationViewController.showSavePasswordSwitch = YES;
        self.authenticationViewController.savePasswordSwitchValue = [self savePasswordSwitchValue];
        self.authenticationViewController.username = savedUsername;
        self.authenticationViewController.password = savedUsername ? savedPassword : nil; //don't set password if unknown username. Should actually never happen.
        __weak __typeof(self) welf = self;
        [self.authenticationViewController setLoginBlock:^(NSString* username, NSString* password, BOOL savePassword) {
            [welf.authService loginToTequilaWithUser:username password:password delegate:welf];
            welf.authenticationViewController.state = AuthenticationViewControllerStateLoading;
            [welf saveSavePasswordSwitchValue:savePassword];
        }];
        [self.authenticationViewController setUserTappedCancelBlock:^{
            [AuthenticationService deleteSavedPasswordForUsername:[AuthenticationService savedUsername]];
            if (welf.delegate) { //old-style authentication
                dispatch_async(dispatch_get_main_queue(), ^{
                    id<AuthenticationControllerDelegate> delegate = welf.delegate;
                    [welf cleanAndDismissAuthenticationViewControllerCompletion:^{
                        if ([(NSObject*)delegate respondsToSelector:@selector(authenticationFailedWithReason:)]) {
                            [delegate authenticationFailedWithReason:AuthenticationFailureReasonUserCancelled];
                        }
                    }];
                });
            } else { //new-style (PocketCampus session) authentication
                [welf dismissAuthenticationViewControllerCompletion:^{
                    [welf cleanAndNotifyUserCancelledToObservers];
                }];
            }
        }];
        [self.authenticationViewController setUserClearedUsernameBlock:^{
            [AuthenticationService saveUsername:nil];
        }];
        [self.authenticationViewController setBottomMessageBlock:^NSString *(AuthenticationViewController* authViewController) {
            NSString* message = nil;
            if (authViewController.savePasswordSwitchValue) {
                message = NSLocalizedStringFromTable(@"WillSavePasswordAndPolicy", @"AuthenticationPlugin", nil);
            } else {
                message = NSLocalizedStringFromTable(@"WillNotSavePasswordAndInstructionsAndPolicy", @"AuthenticationPlugin", nil);
            }
            return message;
        }];
        self.authenticationNavigationController = [[PCNavigationController alloc] initWithRootViewController:self.authenticationViewController];
        self.authenticationNavigationController.modalPresentationStyle = UIModalPresentationFormSheet;
        

        UIViewController* topViewController = [[MainController publicController] currentTopMostViewController];
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)((topViewController.presentedViewController ? 0.5 : 0.0) * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [topViewController presentViewController:self.authenticationNavigationController animated:YES completion:^{
                [self.authenticationViewController focusOnInput];
            }];
        });
#else
        // Cannot present AuthenticationViewController in exentsion
        // => auth fails if no or wrong credentials. User should open main app.
        
        if (self.delegate) { //old-style authentication
            dispatch_async(dispatch_get_main_queue(), ^{
                id<AuthenticationControllerDelegate> delegate = self.delegate;
                [self cleanAndDismissAuthenticationViewControllerCompletion:^{
                    if ([(NSObject*)delegate respondsToSelector:@selector(authenticationFailedWithReason:)]) {
                        [delegate authenticationFailedWithReason:AuthenticationFailureReasonCannotAskForCredentials];
                    }
                }];
            });
        } else { //new-style (PocketCampus session) authentication
            [self dismissAuthenticationViewControllerCompletion:^{
                [self cleanAndNotifyFailureToObserversWithErrorCode:kAuthenticationErrorCodeCouldNotAskForCredentials];
            }];
        }
#endif
    }
}

- (void)dismissAuthenticationViewControllerCompletion:(void (^)())completion {
    if (self.authenticationNavigationController) {
        [self.authenticationNavigationController.presentingViewController dismissViewControllerAnimated:YES completion:completion];
        self.authenticationNavigationController = nil;
        self.authenticationViewController = nil;
    } else {
        if (completion) {
            completion();
        }
    }
}

- (void)clean {
    [self.authService cancelOperationsForDelegate:self];
    self.tequilaToken = nil;
    self.delegate = nil;
}

- (void)cleanAndDismissAuthenticationViewControllerCompletion:(void (^)())completion {
    [self clean];
    [self dismissAuthenticationViewControllerCompletion:completion];
}

- (void)cleanAndNotifySuccessToObservers {
    [self clean];
    self.observerAuthenticationStarted = NO;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in self.loginObservers) {
            loginObserver.successBlock();
        }
        [self.loginObservers removeAllObjects];
    }
}

- (void)cleanAndNotifyUserCancelledToObservers {
    [self clean];
    self.observerAuthenticationStarted = NO;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in self.loginObservers) {
            loginObserver.userCancelledBlock();
        }
        [self.loginObservers removeAllObjects];
    }
}

- (void)cleanAndNotifyFailureToObserversWithErrorCode:(NSInteger)errorCode {
    [self clean];
    self.observerAuthenticationStarted = NO;
    @synchronized (self) {
        NSError* error = [NSError errorWithDomain:kAuthenticationErrorDomain code:errorCode userInfo:nil];
        for (PCLoginObserver* loginObserver in self.loginObservers) {
            loginObserver.failureBlock(error);
        }
        [self.loginObservers removeAllObjects];
    }
}

#pragma mark PocketCamous SessionId persistence

static NSString* const kAuthSessionIdPCConfigKey = @"PocketCampusAuthSessionId";

- (NSString*)pocketCampusAuthSessionId {
    if (_pocketCampusAuthSessionId) {
        return _pocketCampusAuthSessionId;
    }
    _pocketCampusAuthSessionId = [[PCPersistenceManager userDefaultsForPluginName:@"authentication"] objectForKey:kAuthSessionIdPCConfigKey];
    return _pocketCampusAuthSessionId;
}

- (void)setPocketCampusAuthSessionId:(NSString*)sessionId persist:(BOOL)persist {
//#warning REMOVE
    //persist = NO;
    if (sessionId) {
        [PCUtils throwExceptionIfObject:sessionId notKindOfClass:[NSString class]];
    }
    _pocketCampusAuthSessionId = sessionId;
    NSUserDefaults* defaults = [PCPersistenceManager userDefaultsForPluginName:@"authentication"];
    if (persist) {
        [defaults setObject:sessionId forKey:kAuthSessionIdPCConfigKey];
    } else {
        [defaults removeObjectForKey:kAuthSessionIdPCConfigKey];
    }
    [defaults synchronize];
}

- (void)deletePocketCampusAuthSessionId {
    _pocketCampusAuthSessionId = nil;
    NSUserDefaults* defaults = [PCPersistenceManager userDefaultsForPluginName:@"authentication"];
    [defaults removeObjectForKey:kAuthSessionIdPCConfigKey];
    [defaults synchronize];
}

#pragma mark Save password switch value persistence

static NSString* const kSavePasswordBoolKey = @"AuthenticationSavePassword";
static NSString* const kSavePasswordSwitchStateOldKey = @"savePasswordSwitch"; //old key

- (BOOL)savePasswordSwitchValue {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        //check if transferred from PCObjectArchiver to PCConfig default
        static NSString* const kAuthTransferedPasswordSwitchStateKey = @"AuthTransferedPasswordSwitchState";
        NSUserDefaults* defaults = [PCPersistenceManager userDefaultsForPluginName:@"authentication"];
        if (![defaults boolForKey:kAuthTransferedPasswordSwitchStateKey]) {
            NSNumber* nsBool = (NSNumber*)[PCPersistenceManager objectForKey:kSavePasswordSwitchStateOldKey pluginName:@"authentication"];
            if (nsBool) {
                [defaults setBool:[nsBool boolValue] forKey:kSavePasswordBoolKey];
            }
            [defaults setBool:YES forKey:kAuthTransferedPasswordSwitchStateKey];
            [defaults synchronize];
        }
    });
    NSNumber* boolNb = [[PCPersistenceManager userDefaultsForPluginName:@"authentication"] objectForKey:kSavePasswordBoolKey];
    if (boolNb) {
        return [boolNb boolValue];
    }
    return YES; //default if not specified yet
}

- (void)saveSavePasswordSwitchValue:(BOOL)savePassword {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        //delete old saved state if any
        [PCPersistenceManager saveObject:nil forKey:kSavePasswordSwitchStateOldKey pluginName:@"authentication"];
    });
    NSUserDefaults* defaults = [PCPersistenceManager userDefaultsForPluginName:@"authentication"];
    [defaults setBool:savePassword forKey:kSavePasswordBoolKey];
    [defaults synchronize];
}

#pragma mark - Dealloc

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}

@end
