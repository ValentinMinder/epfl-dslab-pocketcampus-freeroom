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




//  Created by Loïc Gardiol on 26.12.12.


#import "PluginControllerAuthentified.h"

@implementation PluginControllerAuthentified

#pragma mark - Login observation

- (void)addLoginObserver:(id)observer successBlock:(VoidBlock)successBlock userCancelledBlock:(VoidBlock)userCancelledblock failureBlock:(void (^)(NSError* error))failureBlock {
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

- (void)cleanAndNotifyFailureToObserversWithAuthenticationErrorCode:(NSInteger)errorCode {
    self.authController = nil;
    self.authenticationStarted = NO;
    @synchronized (self) {
        NSError* error = [NSError errorWithDomain:kAuthenticationErrorDomain code:errorCode userInfo:nil];
        for (PCLoginObserver* loginObserver in [self.loginObservers copy]) {
            loginObserver.failureBlock(error);
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
        NSError* error = [NSError errorWithDomain:kAuthenticationErrorDomain code:kAuthenticationErrorCodeOther userInfo:nil];
        for (PCLoginObserver* loginObserver in [self.loginObservers copy]) {
            if ([loginObserver.observer respondsToSelector:@selector(serviceConnectionToServerFailed)]) {
                [loginObserver.observer serviceConnectionToServerFailed];
            } else {
                loginObserver.failureBlock(error);
            }
            [self.loginObservers removeObject:loginObserver];
        }
    }
}


@end
