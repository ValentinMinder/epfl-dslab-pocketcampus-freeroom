//
//  PCNavigationController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.09.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PCNavigationController : UINavigationController

/*
 * UIViewController override
 * Returns top view controller prefersStatusBarHidden if top view controller implements it
 * Returns super otherwise
 */
- (BOOL)prefersStatusBarHidden;

/*
 * UIViewController override
 * Returns top view controller preferredStatusBarStyle if top view controller implements it
 * Returns super otherwise
 */
- (UIStatusBarStyle)preferredStatusBarStyle;

/*
 * UIViewController override
 * Returns top view controller preferredStatusBarUpdateAnimation if top view controller implements it
 * Returns super otherwise
 */
- (UIStatusBarAnimation)preferredStatusBarUpdateAnimation;

@end
