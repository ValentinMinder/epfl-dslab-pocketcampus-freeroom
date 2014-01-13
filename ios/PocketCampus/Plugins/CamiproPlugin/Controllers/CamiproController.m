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

#import "PCObjectArchiver.h"

#import "CamiproService.h"

#import "AuthenticationService.h"

@interface CamiproController ()<CamiproServiceDelegate, AuthenticationCallbackDelegate>

@property (nonatomic, strong) CamiproService* camiproService;
@property (nonatomic, strong) TequilaToken* tequilaToken;

@end

@implementation CamiproController

static NSString* const kDeleteSessionAtInitKey = @"DeleteSessionAtInit";

static CamiproController* instance __weak = nil;

#pragma mark - Init

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"CamiproController cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super init];
        if (self) {
            [[self class] deleteSessionIfNecessary];
            CamiproViewController* camiproViewController = [[CamiproViewController alloc] init];
            camiproViewController.title = [[self class] localizedName];
            PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:camiproViewController];
            navController.pluginIdentifier = [[self class] identifierName];
            self.mainNavigationController = navController;
            instance = self;
        }
        return self;
    }
}

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

+ (void)deleteSessionIfNecessary {
    NSNumber* deleteSession = (NSNumber*)[PCObjectArchiver objectForKey:kDeleteSessionAtInitKey andPluginName:@"camipro"];
    if (deleteSession && [deleteSession boolValue]) {
        NSLog(@"-> Delayed logout notification on Camipro now applied : deleting sessionId");
        [[CamiproService sharedInstanceToRetain] setCamiproSession:nil];
        [PCObjectArchiver saveObject:nil forKey:kDeleteSessionAtInitKey andPluginName:@"camipro"];
    }
}

+ (void)initObservers {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [[NSNotificationCenter defaultCenter] addObserverForName:kAuthenticationLogoutNotification object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *notification) {
            NSNumber* delayed = notification.userInfo[kAuthenticationLogoutNotificationDelayedBoolUserInfoKey];
            if ([delayed boolValue]) {
                NSLog(@"-> Camipro received %@ notification delayed", kAuthenticationLogoutNotification);
                [PCObjectArchiver saveObject:@YES forKey:kDeleteSessionAtInitKey andPluginName:@"camipro"];
            } else {
                NSLog(@"-> Camipro received %@ notification", kAuthenticationLogoutNotification);
                [[CamiproService sharedInstanceToRetain] setCamiproSession:nil]; //removing stored session
                [PCObjectArchiver deleteAllCachedObjectsForPluginName:@"camipro"];
                [[MainController publicController] requestLeavePlugin:@"Camipro"];
            }
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
      userCancelledBlock:(VoidBlock)userCancelledblock failureBlock:(VoidBlock)failureBlock {
    
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
    }
}

#pragma mark - CamiproServiceDelegate

- (void)getTequilaTokenForCamiproDidReturn:(TequilaToken *)tequilaKey {
    self.tequilaToken = tequilaKey;
    [self.authController authToken:tequilaKey.iTequilaKey presentationViewController:self.mainNavigationController delegate:self];
}

- (void)getTequilaTokenForCamiproFailed {
    [self cleanAndNotifyFailureToObservers];
}

- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken *)tequilaKey didReturn:(CamiproSession *)session {
    self.camiproService.camiproSession = session;
    [self cleanAndNotifySuccessToObservers];
}

- (void)getSessionIdForServiceFailedForTequilaKey:(TequilaToken *)aTequilaKey {
    [self cleanAndNotifyFailureToObservers];
}

- (void)serviceConnectionToServerFailed {
    [super cleanAndNotifyConnectionToServerTimedOutToObservers];
}

#pragma mark - AuthenticationCallbackDelegate

- (void)authenticationSucceeded {
    if (!self.tequilaToken) {
        NSLog(@"-> ERROR : no tequilaToken saved after successful authentication");
        return;
    }
    [self.camiproService getSessionIdForServiceWithTequilaKey:self.tequilaToken delegate:self];
}

- (void)userCancelledAuthentication {
    [self.camiproService cancelOperationsForDelegate:self];
    self.camiproService.camiproSession = nil;
    [self cleanAndNotifyUserCancelledToObservers];
}

- (void)invalidToken {
    [self.camiproService getTequilaTokenForCamiproDelegate:self]; //restart to get new token
}

#pragma mark - Dealloc

- (void)dealloc
{
    [self.camiproService cancelOperationsForDelegate:self];
    [[self class] deleteSessionIfNecessary];
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}


@end
