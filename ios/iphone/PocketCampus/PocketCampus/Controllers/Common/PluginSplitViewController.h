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

@property (nonatomic, copy) NSString* pluginIdentifier;
@property (nonatomic, weak) UINavigationController* masterNavigationController; //nil if master view controller is not kind of class UINavigationController
@property (nonatomic) BOOL masterViewControllerHidden;

- (id)initWithMasterViewController:(UIViewController*)masterViewController detailViewController:(UIViewController*)detailViewController;
- (void)setMasterViewControllerHidden:(BOOL)hidden animated:(BOOL)animated;

@end
