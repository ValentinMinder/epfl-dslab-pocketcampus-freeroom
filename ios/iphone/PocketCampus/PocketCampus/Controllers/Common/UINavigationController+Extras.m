//
//  UINavigationController+Extras.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 19.09.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "UINavigationController+Extras.h"

@implementation UINavigationController (Extras)

- (NSUInteger)supportedInterfaceOrientations {
    return [self.topViewController supportedInterfaceOrientations];
}

- (BOOL)shouldAutorotate {
    return [self.topViewController shouldAutorotate];
}

- (void)viewDidLoad {
    self.view.layer.cornerRadius = 5;
    self.view.layer.masksToBounds = YES;
}


@end
