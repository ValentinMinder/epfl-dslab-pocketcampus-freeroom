//
//  UINavigationController+Extras.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 19.09.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import <QuartzCore/QuartzCore.h>

@interface UINavigationController (Extras)

- (NSUInteger)supportedInterfaceOrientations;
- (BOOL)shouldAutorotate;
- (void)viewDidLoad;

@end
