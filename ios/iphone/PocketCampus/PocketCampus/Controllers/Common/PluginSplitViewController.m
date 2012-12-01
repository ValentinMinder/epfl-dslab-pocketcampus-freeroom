//
//  PluginSplitViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 25.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PluginSplitViewController.h"

@interface PluginSplitViewController ()

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
    self.view.layer.cornerRadius = 5;
    self.view.layer.masksToBounds = YES;
}

#pragma mark UINavigationControllerDelegate

- (void)navigationController:(UINavigationController *)navigationController didShowViewController:(UIViewController *)viewController animated:(BOOL)animated {
    UIViewController* masterViewController = viewController;
    
    if ([masterViewController conformsToProtocol:@protocol(PCMasterSplitDelegate)] && [masterViewController respondsToSelector:@selector(detailViewControllerThatShouldBeDisplayed)]) {
        UIViewController* detailViewController = [(id<PCMasterSplitDelegate>)masterViewController detailViewControllerThatShouldBeDisplayed];
        self.viewControllers = @[navigationController, detailViewController];
    }
    
    /*if ([masterViewController conformsToProtocol:@protocol(PCMasterSplitDelegate)] && [masterViewController respondsToSelector:@selector(shouldHideMasterViewController)]) {
        BOOL shouldHide = [(id<PCMasterSplitDelegate>)masterViewController shouldHideMasterViewController];
        self.shouldHideMasterViewController = shouldHide;
        id prevDegate = self.delegate;
        self.delegate = self;
        [self willRotateToInterfaceOrientation:self.interfaceOrientation duration:1.0];
        self.delegate = prevDegate;
    }*/
}

- (void)setMasterViewControllerHidden:(BOOL)hidden animated:(BOOL)animated {
    /*_masterViewControllerHidden = hidden;
    id prevDegate = self.delegate;
    self.delegate = self;
    [self willRotateToInterfaceOrientation:self.interfaceOrientation duration:1.0];
    [self.view setNeedsLayout];
    self.delegate = prevDegate;*/
    
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
    
    /*[UIView animateWithDuration:0.2 animations:^{
        if (hidden) {
            self.view.frame = CGRectMake(newFrameX, newFrameY, newFrameWidth, newFrameHeight);
        } else
            self.view.frame = CGRectMake(newFrameX, newFrameY, newFrame.size.width, newFrameHeight);
        }
    }];*/
    
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

- (void)setMasterViewControllerHidden:(BOOL)hidden {
    [self setMasterViewControllerHidden:hidden animated:NO];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskAll; //always force support of all orientations on iPad (split view controller)
    
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation //iOS 5
{
    return YES; //always force support of all orientations on iPad (split view controller)
}

@end
