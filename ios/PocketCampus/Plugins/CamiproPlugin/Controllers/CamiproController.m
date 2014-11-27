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


//  Created by Loïc Gardiol on 16.05.12.


#import "CamiproController.h"

#import "CamiproViewController.h"

#import "CamiproService.h"

#import "AuthenticationController.h"

@interface CamiproController ()<CamiproServiceDelegate, AuthenticationControllerDelegate>

@property (nonatomic, strong) CamiproService* camiproService;
@property (nonatomic, strong) TequilaToken* tequilaToken;

@end

@implementation CamiproController

static CamiproController* instance __weak = nil;

#pragma mark - Init

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"CamiproController cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super init];
        if (self) {
#ifdef TARGET_IS_MAIN_APP
            CamiproViewController* camiproViewController = [[CamiproViewController alloc] init];
            camiproViewController.title = [[self class] localizedName];
            PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:camiproViewController];
            navController.pluginIdentifier = [[self class] identifierName];
            self.mainNavigationController = navController;
            instance = self;
#endif
        }
        return self;
    }
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
+ (void)initObservers {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [[NSNotificationCenter defaultCenter] addObserverForName:kAuthenticationLogoutNotification object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *notification) {
            CLSNSLog(@"-> Camipro received %@ notification", kAuthenticationLogoutNotification);
            [[CamiproService sharedInstanceToRetain] deleteCamiproSession]; //removing stored session
            [PCPersistenceManager deleteCacheForPluginName:@"camipro"];
#ifndef TARGET_IS_EXTENSION
            [[MainController publicController] requestLeavePlugin:@"Camipro"];
#endif
        }];
    });
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"CamiproPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Camipro";
}

#pragma mark - PluginControllerAuthentified

- (void)addLoginObserver:(id)observer successBlock:(VoidBlock)successBlock
      userCancelledBlock:(VoidBlock)userCancelledblock failureBlock:(void (^)(NSError* error))failureBlock {
    
    [super addLoginObserver:observer successBlock:successBlock userCancelledBlock:userCancelledblock failureBlock:failureBlock];
    if (!super.authenticationStarted) {
        super.authenticationStarted = YES;
        self.camiproService = [CamiproService sharedInstanceToRetain];
        [self.camiproService getTequilaTokenForCamiproDelegate:self];
    }
}

- (void)removeLoginObserver:(id)observer {
    [super removeLoginObserver:observer];
    if ([self.loginObservers count] == 0) {
        [self.camiproService cancelOperationsForDelegate:self]; //abandon login attempt if no more observer interested
        self.authenticationStarted = NO;
    }
}

#pragma mark - CamiproServiceDelegate

- (void)getTequilaTokenForCamiproDidReturn:(TequilaToken *)tequilaKey {
    self.tequilaToken = tequilaKey;
    [[AuthenticationController sharedInstance] authenticateToken:tequilaKey.iTequilaKey delegate:self];
}

- (void)getTequilaTokenForCamiproFailed {
    [self cleanAndNotifyFailureToObserversWithAuthenticationErrorCode:kAuthenticationErrorCodeOther];
}

- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken *)tequilaKey didReturn:(CamiproSession *)session {
    [self.camiproService setCamiproSession:session persist:YES];
    [self cleanAndNotifySuccessToObservers];
}

- (void)getSessionIdForServiceFailedForTequilaKey:(TequilaToken *)aTequilaKey {
    [self cleanAndNotifyFailureToObserversWithAuthenticationErrorCode:kAuthenticationErrorCodeOther];
}

- (void)serviceConnectionToServerFailed {
    [super cleanAndNotifyConnectionToServerTimedOutToObservers];
}

#pragma mark - AuthenticationControllerDelegate

- (void)authenticationSucceeded {
    if (!self.tequilaToken) {
        CLSNSLog(@"-> ERROR : no tequilaToken saved after successful authentication");
        return;
    }
    [self.camiproService getSessionIdForServiceWithTequilaKey:self.tequilaToken delegate:self];
}

- (void)authenticationFailedWithReason:(AuthenticationFailureReason)reason {
    switch (reason) {
        case AuthenticationFailureReasonUserCancelled:
            [self.camiproService cancelOperationsForDelegate:self];
            [self cleanAndNotifyUserCancelledToObservers];
            break;
        case AuthenticationFailureReasonCannotAskForCredentials:
            [self.camiproService cancelOperationsForDelegate:self];
            [self cleanAndNotifyFailureToObserversWithAuthenticationErrorCode:kAuthenticationErrorCodeCouldNotAskForCredentials];
            break;
        case AuthenticationFailureReasonInvalidToken:
            [self.camiproService getTequilaTokenForCamiproDelegate:self]; //restart to get new token
            break;
        case AuthenticationFailureReasonInternalError:
            [self.camiproService getTequilaTokenForCamiproDelegate:self]; //restart to get new token
            break;
        default:
            [self.camiproService getTequilaTokenForCamiproDelegate:self]; //restart to get new token
            break;
    }
}

#pragma mark - Dealloc

- (void)dealloc
{
    [self.camiproService cancelOperationsForDelegate:self];
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}


@end
