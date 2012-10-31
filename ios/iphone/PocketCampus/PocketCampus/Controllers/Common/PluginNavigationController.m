//
//  PluginNavigationController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PluginNavigationController.h"

@interface PluginNavigationController ()

@end

@implementation PluginNavigationController

@synthesize pluginIdentifier;

- (NSUInteger)supportedInterfaceOrientations {
    return [self.topViewController supportedInterfaceOrientations];
}

- (BOOL)shouldAutorotate {
    return [self.topViewController shouldAutorotate];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.layer.cornerRadius = 3;
    self.view.layer.masksToBounds = YES;
}

@end
