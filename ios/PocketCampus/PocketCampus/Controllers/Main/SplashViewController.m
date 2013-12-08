//
//  SplashViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 21.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "SplashViewController.h"

#import <QuartzCore/QuartzCore.h>

@interface SplashViewController ()

@property (nonatomic, retain) UIImageView* splashViewImage;

@property (nonatomic, retain) NSLayoutConstraint* leadingSpaceToSuperViewConstraint;

@end

@implementation SplashViewController

- (id)initWithRightHiddenOffset:(CGFloat)rightHiddenOffset
{
    self = [super init];
    if (self) {
        _rightHiddenOffset = rightHiddenOffset;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	self.view.translatesAutoresizingMaskIntoConstraints = NO;
    self.view.autoresizesSubviews = YES;
    self.view.backgroundColor = [UIColor colorWithRed:0.972549 green:0.972549 blue:0.972549 alpha:1.0];
    self.splashViewImage = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"PocketCampusDrawing"]];
    [self.view addSubview:self.splashViewImage];
    [self.view addConstraints:[NSLayoutConstraint constraintsForCenterXYtoSuperview:self.view forView:self.splashViewImage]];
}

- (NSUInteger)supportedInterfaceOrientations {
    if ([PCUtils isIdiomPad]) {
        return UIInterfaceOrientationMaskAll;
    } else {
        return UIInterfaceOrientationMaskPortrait;
    }
}

- (void)setSuperviewOfView:(UIView *)superviewOfView {
    _superviewOfView = superviewOfView;
    self.leadingSpaceToSuperViewConstraint = [NSLayoutConstraint constraintsToSuperview:self.superviewOfView forView:self.view edgeInsets:UIEdgeInsetsMake(kNoInsetConstraint, kNoInsetConstraint, kNoInsetConstraint, 0)][0];
    [self.superviewOfView addConstraints:[NSLayoutConstraint constraintsToSuperview:self.superviewOfView forView:self.view edgeInsets:UIEdgeInsetsMake(0, 0, 0, kNoInsetConstraint)]];
}

- (void)willMoveToRightWithDuration:(NSTimeInterval)duration hideDrawingOnIdiomPhone:(BOOL)hideDrawingOnIdiomPhone {
    if ([PCUtils isIdiomPad]) { //adapt drawing's position
        self.leadingSpaceToSuperViewConstraint.constant = self.rightHiddenOffset;
        [UIView animateWithDuration:duration delay:0.0 options:UIViewAnimationOptionCurveEaseInOut animations:^{
            [self.view.superview layoutIfNeeded];
        } completion:NULL];
        
    } else { //hide drowing
        if (hideDrawingOnIdiomPhone) {
            self.splashViewImage.alpha = 0.0;
        }
    }
}
@end
