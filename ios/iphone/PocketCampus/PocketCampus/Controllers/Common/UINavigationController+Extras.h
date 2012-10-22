//
//  UINavigationController+SupportedOrientations.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 19.09.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UINavigationController (SupportedOrientations)

- (NSUInteger)supportedInterfaceOrientations;
- (BOOL)shouldAutorotate;

@end
