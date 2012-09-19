//
//  UINavigationController+SupportedOrientations.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 19.09.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "UINavigationController+SupportedOrientations.h"

@implementation UINavigationController (SupportedOrientations)

- (NSUInteger)supportedInterfaceOrientations {
    return [self.topViewController supportedInterfaceOrientations];
}

- (BOOL)shouldAutorotate {
    return [self.topViewController shouldAutorotate];
}


@end
