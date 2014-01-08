//
//  PCSplashView.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 30.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "PCSplashView.h"

@interface PCSplashView ()

@property (nonatomic, strong) UIImageView* backgroundImageView;

@property (nonatomic, strong) UIImageView* drawingImageView;

@property (nonatomic, strong) NSLayoutConstraint* drawingImageViewCenterYConstraint;

@end

@implementation PCSplashView

#pragma mark - Init

- (id)initWithSuperview:(UIView*)superview {
    self = [super initWithFrame:CGRectMake(0, 0, 1, 1)];
    if (self) {
        self.translatesAutoresizingMaskIntoConstraints = NO;
        [self moveToSuperview:superview];
        self.backgroundImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 1, 1)];
        self.backgroundImageView.translatesAutoresizingMaskIntoConstraints = NO;
        self.backgroundImageView.image = [self imageForDevice];
        [self addSubview:self.backgroundImageView];
        [self addConstraints:[NSLayoutConstraint constraintsToSuperview:self forView:self.backgroundImageView edgeInsets:UIEdgeInsetsMake(0, 0, 0, 0)]];
        
        self.drawingImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"PocketCampusDrawing"]];
        self.drawingImageView.translatesAutoresizingMaskIntoConstraints = NO;
        [self.backgroundImageView addSubview:self.drawingImageView];
        self.drawingImageViewCenterYConstraint = [NSLayoutConstraint constraintForCenterYtoSuperview:self.backgroundImageView forView:self.drawingImageView constant:0.0];
        [self.backgroundImageView addConstraint:self.drawingImageViewCenterYConstraint];
        [self.backgroundImageView addConstraint:[NSLayoutConstraint constraintForCenterXtoSuperview:self.backgroundImageView forView:self.drawingImageView constant:0.0]];
        
        
    }
    return self;
}

- (id)initWithFrame:(CGRect)frame
{
    return [self init];
}

#pragma mark - Public methods

- (void)moveToSuperview:(UIView*)superview {
    [superview addSubview:self];
    [superview addConstraints:[NSLayoutConstraint constraintsToSuperview:superview forView:self edgeInsets:UIEdgeInsetsMake(0, 0, 0, 0)]];
}

- (void)hideWithAnimationDelay:(NSTimeInterval)delay duration:(NSTimeInterval)duration completion:(VoidBlock)completion; {
    //duration = 1.0;
    
    /*CAMediaTimingFunction* easeIn = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseIn];
    for (size_t index = 0; index<=3; index++) {
        float values[2];
        [easeIn getControlPointAtIndex:index values:values];
        NSLog(@"EaseIN %ld: (%f, %f)", index, values[0], values[1]);
    }*/
    
    static CGFloat kEaseInFinalSlope = 50.0/29.0;
    
    CGFloat originalHeight = self.drawingImageView.frame.size.height;
    CGFloat compressedHeight = self.drawingImageView.frame.size.height*0.85;
    CGFloat originalCenterYConstraintConstant = self.drawingImageViewCenterYConstraint.constant;
    NSLayoutConstraint* compressedHeightConstraint = [NSLayoutConstraint heightConstraint:compressedHeight forView:self.drawingImageView];
    CGFloat m2LogoTopDistance = originalHeight-compressedHeight;
    CGFloat m2LogoCenterDistance = m2LogoTopDistance/2.0;
    CGFloat finalCenterYConstraintConstant = -500.0;
    CGFloat m3Distance = finalCenterYConstraintConstant-originalCenterYConstraintConstant;
    
    float m1TimeFrac = 0.65;
    
    /*
     * m2TimeFrac can be found by resolving following system:
     *
     * m1TimeFrac = constant (above)
     * m3TimeFrac = 1 - m1TimeFrac - m2TimeFrac
     * 
     * (m2LogoTopDistance / m2TimeFrac) * kEaseInFinalSlope = m3Distance / (1 - m1TimeFrac - m2TimeFrac)
     */
    float m2TimeFrac = (1 - m1TimeFrac) / ( (m3Distance / (m2LogoTopDistance * kEaseInFinalSlope) + 1) );
    
    NSTimeInterval m1Duration = duration*m1TimeFrac;
    NSTimeInterval m2Duration = duration*m2TimeFrac;
    NSTimeInterval m3Duration = duration-m1Duration-m2Duration;
    
    //NSLog(@"M2 avg speed: %lf, final speed: %lf", m2LogoTopDistance/m2Duration, (m2LogoTopDistance/m2Duration)*kEaseInFinalSlope);
    //NSLog(@"M3 avg speed: %lf", m3Distance/m3Duration);
    
    [self.drawingImageView addConstraint:compressedHeightConstraint];
    self.drawingImageViewCenterYConstraint.constant = m2LogoCenterDistance;
    
    [UIView animateWithDuration:m1Duration delay:delay options:UIViewAnimationOptionCurveEaseOut animations:^{
        [self.backgroundImageView layoutIfNeeded];
    } completion:^(BOOL finished) {
        
        [self.drawingImageView removeConstraint:compressedHeightConstraint];
        self.drawingImageViewCenterYConstraint.constant = originalCenterYConstraintConstant;
        
        [UIView animateWithDuration:m2Duration delay:0.0 options:UIViewAnimationOptionCurveEaseIn animations:^{
            [self.backgroundImageView layoutIfNeeded];
        } completion:^(BOOL finished) {
            
            self.drawingImageViewCenterYConstraint.constant = finalCenterYConstraintConstant;
            [UIView animateWithDuration:m3Duration delay:0.0 options:UIViewAnimationOptionCurveLinear animations:^{
                self.alpha = 0.0;
                [self.backgroundImageView layoutIfNeeded];
            } completion:^(BOOL finished) {
                if (completion) {
                    completion();
                }
            }];
        }];
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
