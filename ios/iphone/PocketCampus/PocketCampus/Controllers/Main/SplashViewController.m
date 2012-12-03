//
//  SplashViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 21.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "SplashViewController.h"

#import "PCUtils.h"

#import <QuartzCore/QuartzCore.h>

@interface SplashViewController ()

@property (retain, nonatomic) UIImageView* splashViewImage;

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

- (void)loadView {
    [super loadView];
    self.view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 1, 1)]; //any non-null size. AutoresizingMask will stretch to full screen size
    self.view.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    self.view.autoresizesSubviews = YES;
    self.view.layer.cornerRadius = 5;
    self.view.layer.masksToBounds = YES;
    
    self.splashViewImage = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"PocketCampusDrawing"]];
    self.splashViewImage.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin
    | UIViewAutoresizingFlexibleTopMargin
    | UIViewAutoresizingFlexibleRightMargin
    | UIViewAutoresizingFlexibleBottomMargin;
    self.splashViewImage.center = self.view.center;
    self.view.backgroundColor = [UIColor colorWithRed:0.961 green:0.957 blue:0.941 alpha:1.0];

    [self.view addSubview:self.splashViewImage];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
}

- (void)viewWillAppear:(BOOL)animated {
    self.splashViewImage.center = self.view.center;
}

- (void)willMoveToRightWithDuration:(NSTimeInterval)duration hideDrawingOnIdiomPhone:(BOOL)hideDrawingOnIdiomPhone {
    if ([PCUtils isIdiomPad]) { //adapt drawing's position
        [UIView animateWithDuration:duration animations:^{
            self.view.frame = CGRectMake(0, 0, self.view.frame.size.width - self.rightHiddenOffset, self.view.frame.size.height);
        }];
    } else { //hide drowing
        if (hideDrawingOnIdiomPhone) {
            self.splashViewImage.alpha = 0.0;
        }
    }
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation {
    if ([PCUtils isIdiomPad]) {
        return YES;
    } else {
        return UIInterfaceOrientationIsPortrait(toInterfaceOrientation);
    }
}

- (NSUInteger)supportedInterfaceOrientations {
    if ([PCUtils isIdiomPad]) {
        return UIInterfaceOrientationMaskAll;
    } else {
        return UIInterfaceOrientationMaskPortrait;
    }
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
