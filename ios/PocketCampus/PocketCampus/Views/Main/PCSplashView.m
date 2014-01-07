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
    //duration = 5.0;
    
    CGFloat originalHeight = self.drawingImageView.frame.size.height;
    CGFloat compressedHeight = self.drawingImageView.frame.size.height*0.85;
    CGFloat originalCenterYConstraintConstant = self.drawingImageViewCenterYConstraint.constant;
    NSLayoutConstraint* compressedHeightConstraint = [NSLayoutConstraint heightConstraint:compressedHeight forView:self.drawingImageView];
    NSTimeInterval m1Duration = duration*0.55;
    NSTimeInterval m2Duration = duration*0.12;
    NSTimeInterval m3Duration = duration*0.33;
    
    [self.drawingImageView addConstraint:compressedHeightConstraint];
    self.drawingImageViewCenterYConstraint.constant = (originalHeight-compressedHeight)/2.0;
    
    [UIView animateWithDuration:m1Duration delay:delay options:UIViewAnimationOptionCurveEaseInOut animations:^{
        [self.backgroundImageView layoutIfNeeded];
    } completion:^(BOOL finished) {
        
        [self.drawingImageView removeConstraint:compressedHeightConstraint];
        self.drawingImageViewCenterYConstraint.constant = originalCenterYConstraintConstant;
        
        [UIView animateWithDuration:m2Duration delay:0.0 options:UIViewAnimationOptionCurveEaseIn animations:^{
            [self.backgroundImageView layoutIfNeeded];
        } completion:^(BOOL finished) {
            
            self.drawingImageViewCenterYConstraint.constant = [PCUtils is4inchDevice] ? -500.0 : -412.0;
            //NSLog(@"M3 avg speed: %lf", self.drawingImageViewCenterYConstraint.constant/m3Duration);
            [UIView animateWithDuration:duration*m3Duration delay:0.0 options:UIViewAnimationOptionCurveLinear animations:^{
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
