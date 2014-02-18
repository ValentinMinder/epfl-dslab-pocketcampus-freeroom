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

NSString* const kAuthSessionIdPCConfigKey = @"PocketCampusAuthSessionId";

static NSString* kDeleteAuthSessionAtInitBoolKey = @"AuthenticationDeleteAuthSessionAtInit";

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

@interface AuthenticationController ()<AuthenticationServiceDelegate, AuthenticationDelegate>

@property (nonatomic, strong) AuthenticationViewController* gasparViewController;
@property (nonatomic, strong) AuthenticationService* authService;
@property (nonatomic, strong) NSMutableSet* loginObservers;

@property (nonatomic, strong) NSString* tequilaToken;

@end

static AuthenticationController* instance __weak = nil;

static AuthenticationController* instanceStrong __strong = nil;

@implementation AuthenticationController

#pragma mark - Init

+ (void)initialize {
    [self.class deleteAuthSessionIdIfNecessary];
}

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
            NSNumber* delayed = notification.userInfo[kAuthenticationLogoutNotificationDelayedBoolUserInfoKey];
            if ([delayed boolValue]) {
                CLSNSLog(@"-> Authentication received %@ notification delayed", kAuthenticationLogoutNotification);
                [[PCConfig defaults] setBool:YES forKey:kDeleteAuthSessionAtInitBoolKey];
            } else {
                CLSNSLog(@"-> Authentication received %@ notification. Deleting auth sessionId.", kAuthenticationLogoutNotification);
                [self deleteAuthSessionId];
            }
        }];
    });
}

#pragma mark - Standard authentication

- (void)authToken:(NSString*)token presentationViewController:(UIViewController*)presentationViewController delegate:(id<AuthenticationDelegate>)delegate; {
    NSString* savedPassword = [AuthenticationService savedPasswordForUsername:[AuthenticationService savedUsername]];
    self.gasparViewController = [[AuthenticationViewController alloc] init];
    if (savedPassword) {
        self.gasparViewController.presentationMode = PresentationModeTryHidden;
        self.gasparViewController.viewControllerForPresentation = presentationViewController;
        self.gasparViewController.showSavePasswordSwitch = YES;
        self.gasparViewController.hideGasparUsageAccountMessage = YES;
        [self.gasparViewController authenticateSilentlyToken:token delegate:delegate];
    } else {
        self.gasparViewController.presentationMode = PresentationModeModal;
        self.gasparViewController.viewControllerForPresentation = presentationViewController;
        self.gasparViewController.showSavePasswordSwitch = YES;
        self.gasparViewController.hideGasparUsageAccountMessage = YES;
        self.gasparViewController.delegate = delegate;
        self.gasparViewController.token = token;
        UINavigationController* tmpNavController = [[UINavigationController alloc] initWithRootViewController:self.gasparViewController]; //so that nav bar is shown
        tmpNavController.modalPresentationStyle = UIModalPresentationFormSheet;
        
        [presentationViewController presentViewController:tmpNavController animated:YES completion:^{
            [self.gasparViewController focusOnInput];
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

- (void)getAuthSessionIdWithToken:(NSString*)tequilaToken didReturn:(AuthSessionResponse*)response {
    switch (response.statusCode) {
        case AuthStatusCode_OK:
            [self.class saveAuthSessionId:response.sessionId];
            [self cleanAndNotifySuccessToObservers];
            break;
        case AuthStatusCode_NETWORK_ERROR:
            [self cleanAndNotifyFailureToObservers];
            break;
        case AuthStatusCode_INVALID_SESSION:
            [self.class deleteAuthSessionId];
            [self cleanAndNotifyFailureToObservers];
            break;
        default:
            [self cleanAndNotifyFailureToObservers];
            break;
    }
}

- (void)getAuthSessionIdFailedForToken:(NSString*)tequilaToken {
    [self cleanAndNotifyFailureToObservers];
}

- (void)serviceConnectionToServerFailed {
    [self cleanAndNotifyFailureToObservers];
}

#pragma mark - AuthenticationDelegate

- (void)authenticationSucceeded {
    if (!self.tequilaToken) {
        CLSNSLog(@"!! ERROR: authentication succeeded but no saved tequila token. Notifying failure to observers.");
        [self cleanAndNotifyFailureToObservers];
        return;
    }
    [self.authService getAuthSessionIdWithTequilaToken:self.tequilaToken delegate:self];
}

- (void)userCancelledAuthentication {
    [self cleanAndNotifyUserCancelledToObservers];
}

- (void)invalidToken {
    [self cleanAndNotifyFailureToObservers];
}

#pragma mark - SessionId persistence
#pragma mark Private

+ (void)deleteAuthSessionIdIfNecessary {
    if ([[PCConfig defaults] boolForKey:kDeleteAuthSessionAtInitBoolKey]) {
        CLSNSLog(@"-> Delayed logout notification on Authentication now applied : deleting auth sessionId");
        [self deleteAuthSessionId];
        [[PCConfig defaults] setBool:NO forKey:kDeleteAuthSessionAtInitBoolKey];
        [[PCConfig defaults] synchronize];
    }
}

+ (void)saveAuthSessionId:(NSString*)sessionId {
    [PCUtils throwExceptionIfObject:sessionId notKindOfClass:[NSString class]];
    if (!sessionId) {
        [self deleteAuthSessionId];
        return;
    }
    [[PCConfig defaults] setObject:sessionId forKey:kAuthSessionIdPCConfigKey];
    [[PCConfig defaults] synchronize];
}

+ (void)deleteAuthSessionId {
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
        instanceStrong = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}

@end
