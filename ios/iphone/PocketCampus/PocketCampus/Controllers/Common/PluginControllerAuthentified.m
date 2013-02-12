//
//  PluginControllerAuthentified.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 26.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PluginControllerAuthentified.h"

@implementation PluginControllerAuthentified

#pragma mark - Login observation

- (void)addLoginObserver:(id)observer successBlock:(VoidBlock)successBlock userCancelledBlock:(VoidBlock)userCancelledblock failureBlock:(VoidBlock)failureBlock {
    @synchronized(self) {
        PCLoginObserver* loginObserver = [[PCLoginObserver alloc] init];
        loginObserver.observer = observer;
        loginObserver.successBlock = successBlock;
        loginObserver.operationIdentifier = nil;
        loginObserver.userCancelledBlock = userCancelledblock;
        loginObserver.failureBlock = failureBlock;
        if (!self.loginObservers) {
            self.loginObservers = [NSMutableArray array];
        }
        [self.loginObservers addObject:loginObserver];
        if(!self.authController) {
            self.authController = [AuthenticationController sharedInstanceToRetain];
        }
    }
}

- (void)removeLoginObserver:(id)observer {
    @synchronized(self) {
        for (PCLoginObserver* loginObserver in [self.loginObservers copy]) {
            if (loginObserver.observer == observer) {
                [self.loginObservers removeObject:loginObserver];
            }
        }
        if ([self.loginObservers count] == 0) {
            self.authController = nil;
        }
    }
}

- (void)cleanAndNotifySuccessToObservers {
    self.authController = nil;
    self.authenticationStarted = NO;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in [self.loginObservers copy]) {
            loginObserver.successBlock();
            [self.loginObservers removeObject:loginObserver];
        }
    }
}

- (void)cleanAndNotifyFailureToObservers {
    self.authController = nil;
    self.authenticationStarted = NO;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in [self.loginObservers copy]) {
            loginObserver.failureBlock();
            [self.loginObservers removeObject:loginObserver];
        }
    }
}

- (void)cleanAndNotifyUserCancelledToObservers {
    self.authController = nil;
    self.authenticationStarted = NO;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in [self.loginObservers copy]) {
            loginObserver.userCancelledBlock();
            [self.loginObservers removeObject:loginObserver];
        }
    }
}

- (void)cleanAndNotifyConnectionToServerTimedOutToObservers {
    self.authController = nil;
    self.authenticationStarted = NO;
    @synchronized (self) {
        for (PCLoginObserver* loginObserver in [self.loginObservers copy]) {
            if ([loginObserver.observer respondsToSelector:@selector(serviceConnectionToServerTimedOut)]) {
                [loginObserver.observer serviceConnectionToServerTimedOut];
            } else {
                loginObserver.failureBlock();
            }
            [self.loginObservers removeObject:loginObserver];
        }
    }
}


@end
