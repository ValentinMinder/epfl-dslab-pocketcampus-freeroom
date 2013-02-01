//
//  PluginSplitViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 25.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

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
    self.view.layer.cornerRadius = [PCValues defaultCornerRadius];
    self.view.layer.masksToBounds = YES;
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskAll; //always force support of all orientations on iPad (split view controller)
    
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation //iOS<=5
{
    return YES; //always force support of all orientations on iPad (split view controller)
}

#pragma mark - Toggle button generation

- (UIBarButtonItem*)toggleMasterViewBarButtonItem {
    UIButton* button = [[UIButton alloc] initWithFrame:CGRectMake(0.0, 0.0, 25.0, 25.0)];
    if (self.masterViewControllerHidden) {
        [button setImage:[UIImage imageNamed:@"ArrowShow"] forState:UIControlStateNormal];
    } else {
        [button setImage:[UIImage imageNamed:@"ArrowHide"] forState:UIControlStateNormal];
    }
    button.adjustsImageWhenHighlighted = NO;
    button.showsTouchWhenHighlighted = YES;
    [button addTarget:self action:@selector(toggleMasterVideoControllerHidden:) forControlEvents:UIControlEventTouchUpInside];
    return [[UIBarButtonItem alloc] initWithCustomView:button];
}

#pragma mark - Master view controller visibility management

- (void)toggleMasterVideoControllerHidden:(UIButton*)sender {
    if (self.masterViewControllerHidden) {
        [sender setImage:[UIImage imageNamed:@"ArrowHide"] forState:UIControlStateNormal];
        [self setMasterViewControllerHidden:NO animated:YES];
    } else {
        [sender setImage:[UIImage imageNamed:@"ArrowShow"] forState:UIControlStateNormal];
        [self setMasterViewControllerHidden:YES animated:YES];
    }
}

- (void)setMasterViewControllerHidden:(BOOL)hidden {
    [self setMasterViewControllerHidden:hidden animated:NO];
}


- (void)setMasterViewControllerHidden:(BOOL)hidden animated:(BOOL)animated {
    if ((_masterViewControllerHidden && hidden) || (!_masterViewControllerHidden && !hidden)) {
        return;
    }
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
    
    CGFloat duration = 0.0;
    
    if (animated) {
        duration = 0.3;
    }
    
    [UIView animateWithDuration:duration delay:0.0 options:UIViewAnimationOptionCurveEaseInOut animations:^{
        self.view.frame = CGRectMake(newFrameX, newFrameY, newFrameWidth, newFrameHeight);
        detailViewControler.view.frame = CGRectMake(detailFrame.origin.x, detailFrame.origin.y, detailNewWidth, detailFrame.size.height);
    } completion:NULL];
    
    _masterViewControllerHidden = hidden;
    
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
