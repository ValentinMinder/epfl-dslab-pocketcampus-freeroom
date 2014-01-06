//
//  UIViewController+Additions.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 06.01.14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

#import "UIViewController+Additions.h"

#import <objc/objc-runtime.h>

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
    if (!action) {
        NSLog(@"!! WARNING: cannot trackAction if action is nil. Returning.");
        return;
    }
    NSString* screenName = self.gaiScreenName;
    if (!screenName) {
        NSLog(@"!! WARNING: cannot trackScreen if self.gaiScreenName is nil. Returning.");
        return;
    }
    [[PCGAITracker sharedTracker] trackAction:action inScreenWithName:screenName];
}

@end
