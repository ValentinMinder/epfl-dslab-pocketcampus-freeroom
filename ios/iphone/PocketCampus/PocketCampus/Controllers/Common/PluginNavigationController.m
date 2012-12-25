//
//  PluginNavigationController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PluginNavigationController.h"

#import "PCValues.h"

@interface PluginNavigationController ()

@end

@implementation PluginNavigationController

@synthesize pluginIdentifier;

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

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation { //<= iOS 5
    if ([self.topViewController respondsToSelector:@selector(shouldAutorotateToInterfaceOrientation:)]) {
        return [self.topViewController shouldAutorotateToInterfaceOrientation:interfaceOrientation];
    }
    return NO; //default portait only on idiom phone
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.layer.cornerRadius = [PCValues defaultCornerRadius];
    self.view.layer.masksToBounds = YES;
}

@end
