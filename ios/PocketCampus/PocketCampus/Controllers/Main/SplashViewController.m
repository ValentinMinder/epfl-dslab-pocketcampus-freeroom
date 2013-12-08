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

- (id)initWithRightHiddenOffset:(CGFloat)rightHiddenOffset {
    self = [super init];
    if (self) {
        _rightHiddenOffset = rightHiddenOffset;
    }
    return self;
}

- (void)loadView {
    [super loadView];
    self.view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 1, 1)];
    self.view.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    self.view.autoresizesSubviews = YES;
    self.view.backgroundColor = [UIColor colorWithRed:0.972549 green:0.972549 blue:0.972549 alpha:1.0];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.splashViewImage = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"PocketCampusDrawing"]];
    self.splashViewImage.translatesAutoresizingMaskIntoConstraints = NO;
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

- (void)willMoveToRightWithDuration:(NSTimeInterval)duration hideDrawingOnIdiomPhone:(BOOL)hideDrawingOnIdiomPhone {
    if ([PCUtils isIdiomPad]) { //adapt drawing's position
        [UIView animateWithDuration:duration delay:0.0 options:UIViewAnimationOptionCurveEaseInOut animations:^{
            self.view.frame = CGRectMake(0, 0, self.view.frame.size.width - self.rightHiddenOffset, self.view.frame.size.height);
            [self.view layoutIfNeeded];
        } completion:NULL];
        
    } else { //hide drawing
        if (hideDrawingOnIdiomPhone) {
            self.splashViewImage.alpha = 0.0;
        }
    }
}
@end
