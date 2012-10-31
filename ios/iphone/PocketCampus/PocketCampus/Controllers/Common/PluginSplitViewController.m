//
//  PluginSplitViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 25.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PluginSplitViewController.h"

@interface PluginSplitViewController ()

@end

@implementation PluginSplitViewController


@synthesize pluginIdentifier;

/*- (NSUInteger)supportedInterfaceOrientations {
    return [self.topViewController supportedInterfaceOrientations];
}*/

/*- (BOOL)shouldAutorotate {
    return [self.topViewController shouldAutorotate];
}*/

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.layer.cornerRadius = 5;
    self.view.layer.masksToBounds = YES;
}

@end
