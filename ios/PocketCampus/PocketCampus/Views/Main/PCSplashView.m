//
//  PCSplashView.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 30.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "PCSplashView.h"

@interface PCSplashView ()

@property (nonatomic, strong) UIImageView* imageView;

@end

@implementation PCSplashView

#pragma mark - Init

- (id)initWithSuperview:(UIView*)superview {
    self = [super initWithFrame:CGRectMake(0, 0, 1, 1)];
    if (self) {
        self.translatesAutoresizingMaskIntoConstraints = NO;
        [superview addSubview:self];
        [superview addConstraints:[NSLayoutConstraint constraintsToSuperview:superview forView:self edgeInsets:UIEdgeInsetsMake(0, 0, 0, 0)]];
        self.imageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 1, 1)];
        self.imageView.translatesAutoresizingMaskIntoConstraints = NO;
        self.imageView.image = [self imageForDevice];
        [self addSubview:self.imageView];
        [self addConstraints:[NSLayoutConstraint constraintsToSuperview:self forView:self.imageView edgeInsets:UIEdgeInsetsMake(0, 0, 0, 0)]];
        
        
    }
    return self;
}

- (id)initWithFrame:(CGRect)frame
{
    return [self init];
}

#pragma mark - Public methods

- (void)hideWithAnimationDuration:(NSTimeInterval)duration completion:(VoidBlock)completion {
    [UIView animateWithDuration:duration animations:^{
        self.alpha = 0.0;
    } completion:^(BOOL finished) {
        if (completion) {
            completion();
        }
    }];
}

#pragma mark - Image

- (UIImage*)imageForDevice {
    if ([PCUtils is4inchDevice]) {
        return [UIImage imageNamed:@"SplashImage4inch"];
    } else {
        return [UIImage imageNamed:@"SplashImage"];
    }
}

@end
