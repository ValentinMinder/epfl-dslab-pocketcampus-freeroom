/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */




//  Created by Loïc Gardiol on 25.10.12.


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

/**
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
