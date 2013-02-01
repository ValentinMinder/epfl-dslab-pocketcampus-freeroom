//
//  PCMasterSplitDelegate.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//
//
//  Master (right) view controllers of PluginSplitViewController can conform to this protocol to be called by respective PluginController subclasses
//
//
//

#import <Foundation/Foundation.h>

#import "PCMasterSplitDelegate.h"

@protocol PCMasterSplitDelegate <NSObject>


/*
 * PluginSplitViewController will call this method on viewcontrollers of master view every time they are pushed on masterNavigationController
 * to know which viewcontroller should be displayed on the detail (right) view.
 */
@optional
- (UIViewController*)detailViewControllerThatShouldBeDisplayed;

@end
