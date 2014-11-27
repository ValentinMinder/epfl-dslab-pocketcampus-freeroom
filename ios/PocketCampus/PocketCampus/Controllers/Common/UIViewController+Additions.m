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


// Created by Loïc Gardiol on 06.01.14.


#import "UIViewController+Additions.h"

#import <objc/runtime.h>

@implementation UIViewController (Additions)

static NSString* const kGAIScreenNameKey = @"GAIScreenName";

- (void)setGaiScreenName:(NSString *)gaiScreenName {
    objc_setAssociatedObject(self, (__bridge const void *)(kGAIScreenNameKey), gaiScreenName, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (NSString*)gaiScreenName {
    return objc_getAssociatedObject(self, (__bridge const void *)(kGAIScreenNameKey));
}

- (void)trackScreen {
    NSString* screenName = self.gaiScreenName;
    if (!screenName) {
        NSLog(@"!! WARNING: cannot trackScreen if self.gaiScreenName is nil. Returning.");
        return;
    }
    [[PCGAITracker sharedTracker] trackScreenWithName:screenName];
}

- (void)trackAction:(NSString*)action {
    [self trackAction:action contentInfo:nil];
}

- (void)trackAction:(NSString*)action contentInfo:(NSString*)contentInfo {
    if (!action) {
        NSLog(@"!! WARNING: cannot trackAction if action is nil. Returning.");
        return;
    }
    NSString* screenName = self.gaiScreenName;
    if (!screenName) {
        NSLog(@"!! WARNING: cannot trackScreen if self.gaiScreenName is nil. Returning.");
        return;
    }
    [[PCGAITracker sharedTracker] trackAction:action inScreenWithName:screenName contentInfo:contentInfo];
}

- (BOOL)isDisappearingBecausePopped {
    return ([self navControllerStackDirection] == -1);
}

- (BOOL)isDisappearingBecauseOtherPushed {
    return ([self navControllerStackDirection] == 1);
}

#pragma mark - Private

/**
 * Returns 1 if self is being pushed, -1 is self is being popped
 * 0 otherwise
 */
- (int)navControllerStackDirection {
    // http://stackoverflow.com/a/1816682/1423774
    NSArray* viewControllers = self.navigationController.viewControllers;
    if (viewControllers.count > 1 && [viewControllers objectAtIndex:viewControllers.count-2] == self) {
        // View is disappearing because a new view controller was pushed onto the stack
        return 1;
    } else if ([viewControllers indexOfObject:self] == NSNotFound) {
        // View is disappearing because it was popped from the stack
        return -1;
    }
    return 0;
}

@end
