//
//  PCNavigationController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.09.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "PCNavigationController.h"

@interface PCNavigationController ()

@end

@implementation PCNavigationController

- (NSUInteger)supportedInterfaceOrientations {
    if ([self.topViewController respondsToSelector:@selector(supportedInterfaceOrientations)]) {
        return [self.topViewController supportedInterfaceOrientations];
    }
    return UIInterfaceOrientationMaskPortrait; //default portait only on idiom phone
}

- (BOOL)shouldAutorotate {
    if ([self.topViewController respondsToSelector:@selector(shouldAutorotate)]) {
        return [self.topViewController shouldAutorotate];
    }
    return NO; //default portait only on idiom phone
}

- (UIStatusBarStyle)preferredStatusBarStyle {
    if ([self.topViewController respondsToSelector:@selector(preferredStatusBarStyle)]) {
        return [self.topViewController preferredStatusBarStyle];
    }
    return UIStatusBarStyleDefault;
}

- (BOOL)prefersStatusBarHidden {
    if ([self.topViewController respondsToSelector:@selector(prefersStatusBarHidden)]) {
        return [self.topViewController prefersStatusBarHidden];
    }
    return NO;
}

@end
