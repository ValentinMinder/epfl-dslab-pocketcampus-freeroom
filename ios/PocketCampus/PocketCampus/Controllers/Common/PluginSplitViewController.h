//
//  PluginSplitViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 25.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import <QuartzCore/QuartzCore.h>

#import "PCMasterSplitDelegate.h"

@interface PluginSplitViewController : UISplitViewController<UINavigationControllerDelegate>

@property (nonatomic, strong) UIViewController* masterViewController;
@property (nonatomic, strong) UIViewController* detailViewController;
@property (nonatomic, copy) NSString* pluginIdentifier;
@property (nonatomic, readonly, weak) UINavigationController* masterNavigationController; //nil if master view controller is not kind of class UINavigationController
@property (nonatomic, getter = isMasterViewControllerHidden) BOOL masterViewControllerHidden;

- (id)initWithMasterViewController:(UIViewController*)masterViewController detailViewController:(UIViewController*)detailViewController;
- (void)setMasterViewControllerHidden:(BOOL)hidden animated:(BOOL)animated;

/*
 * Detail view controllers can set their left bar button item to such a button
 * Tapping it with show/hide the master view controller so that detail view controller is full screen
 * The action of the button is already set.
 * Example of use:
 * if ([PCUtils isIdiomPad]) {
       self.navigationItem.leftBarButtonItem = [(PluginSplitViewController*)(self.splitViewController) toggleMasterViewBarButtonItem];
 * }
 *
 */
- (UIBarButtonItem*)toggleMasterViewBarButtonItem;

/*
 * UIViewController override
 * Returns YES if both master view and controller and detail view controller return YES
 * If masterViewControllerHidden is YES, returns result of detail view controller only
 */
- (BOOL)prefersStatusBarHidden;

/*
 * UIViewController override
 * Returns detail view controller preferredStatusBarStyle if isMasterViewControllerHidden is YES
 * returns master view controller preferredStatusBarStyle otherwise
 */
- (UIStatusBarStyle)preferredStatusBarStyle;

/*
 * UIViewController override
 * Returns detail view controller preferredStatusBarUpdateAnimation if isMasterViewControllerHidden is YES
 * returns master view controller preferredStatusBarUpdateAnimation otherwise
 */
- (UIStatusBarAnimation)preferredStatusBarUpdateAnimation;

@end
