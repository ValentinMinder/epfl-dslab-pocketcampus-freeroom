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


#import "PluginSplitViewController.h"

#import "PCValues.h"

@interface PluginSplitViewController ()

@property (nonatomic, weak) UINavigationController* masterNavigationController; //nil if master view controller is not kind of class UINavigationController

@end

@implementation PluginSplitViewController


- (id)initWithMasterViewController:(UIViewController*)masterViewController detailViewController:(UIViewController*)detailViewController {
    self = [super init];
    if (self) {
        if ([masterViewController isKindOfClass:[UINavigationController class]]) {
            _masterNavigationController = (UINavigationController*)masterViewController;
            _masterNavigationController.delegate = self;
        }
        self.viewControllers = @[masterViewController, detailViewController];
        
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskAll; //always force support of all orientations on iPad (split view controller)
    
}

#pragma mark - Master and detail properties

- (UIViewController*)masterViewController {
    return self.viewControllers[0];
}

- (void)setMasterViewController:(UIViewController *)masterViewController {
    self.viewControllers = @[masterViewController, self.detailViewController];
}

- (UIViewController*)detailViewController {
    return self.viewControllers[1];
}

- (void)setDetailViewController:(UIViewController *)detailViewController {
    self.viewControllers = @[self.masterViewController, detailViewController];
}

#pragma mark - Toggle button generation

- (UIBarButtonItem*)toggleMasterViewBarButtonItem {
    UIImage* image = [UIImage imageNamed:self.isMasterViewControllerHidden ? @"MasterHidden" : @"MasterVisible"];
    UIBarButtonItem* button = [[UIBarButtonItem alloc] initWithImage:image style:UIBarButtonItemStyleBordered target:self action:@selector(toggleMasterViewControllerHidden:)];
    button.accessibilityLabel = self.isMasterViewControllerHidden ? NSLocalizedStringFromTable(@"ShowMaster", @"PocketCampus", nil) : NSLocalizedStringFromTable(@"HideMaster", @"PocketCampus", nil);
    return button;
}

#pragma mark - Master view controller visibility management

- (void)toggleMasterViewControllerHidden:(UIBarButtonItem*)button {
    [self trackAction:@"ToggleMasterViewControllerHidden"];
    button.image = [UIImage imageNamed:self.isMasterViewControllerHidden ? @"MasterVisible" : @"MasterHidden"];
    button.accessibilityLabel = self.isMasterViewControllerHidden ? NSLocalizedStringFromTable(@"HideMaster", @"PocketCampus", nil) : NSLocalizedStringFromTable(@"ShowMaster", @"PocketCampus", nil);
    [self setMasterViewControllerHidden:!self.isMasterViewControllerHidden animated:YES];
}

- (void)setMasterViewControllerHidden:(BOOL)hidden {
    [self setMasterViewControllerHidden:hidden animated:NO];
}


- (void)setMasterViewControllerHidden:(BOOL)hidden animated:(BOOL)animated {
    if ((_masterViewControllerHidden && hidden) || (!_masterViewControllerHidden && !hidden)) {
        return;
    }
    [self willChangeValueForKey:NSStringFromSelector(@selector(isMasterViewControllerHidden))];
    _masterViewControllerHidden = hidden;
    [self didChangeValueForKey:NSStringFromSelector(@selector(isMasterViewControllerHidden))];
    
    CGFloat duration = animated ? 0.3 : 0.0;
    
    if ([self respondsToSelector:@selector(preferredDisplayMode)]) {
        // >= iOS 8.0
        [UIView animateWithDuration:duration delay:0.0 options:UIViewAnimationOptionCurveEaseInOut animations:^{
            self.preferredDisplayMode = hidden ? UISplitViewControllerDisplayModePrimaryHidden : UISplitViewControllerDisplayModeAllVisible;
        } completion:NULL];
        return;
    } else {
        CGRect newFrame = self.view.frame;
        UIViewController* masterViewController = self.viewControllers[0];
        UIViewController* detailViewControler = self.viewControllers[1];
        CGRect masterFrame = masterViewController.view.frame;
        CGRect detailFrame = detailViewControler.view.frame;
        
        CGFloat newFrameX;
        CGFloat newFrameY;
        CGFloat newFrameWidth;
        CGFloat newFrameHeight;
        
        CGFloat detailNewWidth;
        
        if (hidden) {
            newFrameX = -masterFrame.size.width;
            newFrameY = 0.0;
            newFrameWidth = newFrame.size.width + masterFrame.size.width;
            newFrameHeight = newFrame.size.height;
            detailNewWidth = detailFrame.size.width + masterFrame.size.width;
        } else {
            newFrameX = 0.0;
            newFrameY = 0.0;
            newFrameWidth = newFrame.size.width - masterFrame.size.width;
            newFrameHeight = newFrame.size.height;
            detailNewWidth = detailFrame.size.width - masterFrame.size.width;
        }
        
        [UIView animateWithDuration:duration delay:0.0 options:UIViewAnimationOptionCurveEaseInOut animations:^{
            self.view.frame = CGRectMake(newFrameX, newFrameY, newFrameWidth, newFrameHeight);
            detailViewControler.view.frame = CGRectMake(detailFrame.origin.x, detailFrame.origin.y, detailNewWidth, detailFrame.size.height);
        } completion:NULL];
    }
}

- (BOOL)prefersStatusBarHidden {
    return self.masterViewControllerHidden ? [self.detailViewController prefersStatusBarHidden] : [self.masterViewController prefersStatusBarHidden] && [self.detailViewController prefersStatusBarHidden];
}

- (UIStatusBarStyle)preferredStatusBarStyle {
    return self.masterViewControllerHidden ? [self.detailViewController preferredStatusBarStyle] : [self.masterViewController preferredStatusBarStyle];
}

- (UIStatusBarAnimation)preferredStatusBarUpdateAnimation {
    return self.masterViewControllerHidden ? [self.detailViewController preferredStatusBarUpdateAnimation] : [self.masterViewController preferredStatusBarUpdateAnimation];
}

#pragma mark - UINavigationControllerDelegate

- (void)navigationController:(UINavigationController *)navigationController didShowViewController:(UIViewController *)viewController animated:(BOOL)animated {
    UIViewController* masterViewController = viewController;
    
    if ([masterViewController conformsToProtocol:@protocol(PCMasterSplitDelegate)] && [masterViewController respondsToSelector:@selector(detailViewControllerThatShouldBeDisplayed)]) {
        UIViewController* detailViewController = [(id<PCMasterSplitDelegate>)masterViewController detailViewControllerThatShouldBeDisplayed];
        self.viewControllers = @[navigationController, detailViewController];
    }
}

@end
