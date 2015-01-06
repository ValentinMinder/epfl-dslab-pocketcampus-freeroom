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

//  Created by Loïc Gardiol on 30.11.13.

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
        self.backgroundImageView.isAccessibilityElement = NO;
        self.backgroundImageView.translatesAutoresizingMaskIntoConstraints = NO;
        self.backgroundImageView.image = [self imageForDevice];
        [self addSubview:self.backgroundImageView];
        [self addConstraints:[NSLayoutConstraint constraintsToSuperview:self forView:self.backgroundImageView edgeInsets:UIEdgeInsetsMake(0, 0, 0, 0)]];
        
        self.drawingImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"PocketCampusDrawing"]];
        self.drawingImageView.isAccessibilityElement = NO;
        self.drawingImageView.translatesAutoresizingMaskIntoConstraints = NO;
        [self.backgroundImageView addSubview:self.drawingImageView];
        self.drawingImageViewCenterYConstraint = [NSLayoutConstraint constraintForCenterYtoSuperview:self.backgroundImageView forView:self.drawingImageView constant:0.0];
        [self.backgroundImageView addConstraint:self.drawingImageViewCenterYConstraint];
        [self.backgroundImageView addConstraint:[NSLayoutConstraint constraintForCenterXtoSuperview:self.backgroundImageView forView:self.drawingImageView constant:0.0]];
        
        UIImageView* institutionLogoImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"InstitutionLogo"]];
        institutionLogoImageView.translatesAutoresizingMaskIntoConstraints = NO;
        [self.backgroundImageView addSubview:institutionLogoImageView];
        [self.backgroundImageView addConstraint:[NSLayoutConstraint constraintForCenterXtoSuperview:self.backgroundImageView forView:institutionLogoImageView constant:0.0]];
        [self.backgroundImageView addConstraints:[NSLayoutConstraint constraintsToSuperview:self.backgroundImageView forView:institutionLogoImageView edgeInsets:UIEdgeInsetsMake(kNoInsetConstraint, kNoInsetConstraint, -10.0, kNoInsetConstraint)]];
        
    }
    return self;
}

- (id)initWithFrame:(CGRect)frame
{
    return [self init];
}

- (BOOL)isAccessibilityElement {
    return NO;
}

#pragma mark - Public methods

- (void)moveToSuperview:(UIView*)superview {
    [superview addSubview:self];
    [superview addConstraints:[NSLayoutConstraint constraintsToSuperview:superview forView:self edgeInsets:UIEdgeInsetsMake(0, 0, 0, 0)]];
}

- (void)hideWithAnimationDelay:(NSTimeInterval)delay duration:(NSTimeInterval)duration completion:(VoidBlock)completion {
    //duration = 5.0;
    
    /*CAMediaTimingFunction* easeIn = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseOut];
    for (size_t index = 0; index<=3; index++) {
        float values[2];
        [easeIn getControlPointAtIndex:index values:values];
        NSLog(@"EaseIN %ld: (%f, %f)", index, values[0], values[1]);
    }*/
    
    CGFloat originalHeight = self.drawingImageView.frame.size.height;
    CGFloat compressedHeight = self.drawingImageView.frame.size.height*0.85;
    CGFloat originalCenterYConstraintConstant = self.drawingImageViewCenterYConstraint.constant;
    NSLayoutConstraint* compressedHeightConstraint = [NSLayoutConstraint heightConstraint:compressedHeight forView:self.drawingImageView];
    CGFloat m2LogoTopDistance = originalHeight-compressedHeight;
    CGFloat m2LogoCenterDistance = m2LogoTopDistance/2.0;
    CGFloat finalCenterYConstraintConstant = -500.0;
    CGFloat m3Distance = fabsf(finalCenterYConstraintConstant-originalCenterYConstraintConstant);
    
    NSTimeInterval m1TimeFrac = 0.5;
    
    /*
     * m2TimeFrac can be found by resolving following system:
     *
     * m1TimeFrac = constant (above)
     * m3TimeFrac = 1 - m1TimeFrac - m2TimeFrac
     *
     * (m2LogoTopDistance / m2TimeFrac) = m3Distance / (1 - m1TimeFrac - m2TimeFrac)   //meaning speed M2 == speed M3
     * As M2 is EaseIn and M3 EaseOut, the slope at end of M2 and beginning of M3 are the same
     * so even though the computed speed are the average overall each mouvements, the fact that the
     * slope is the same makes that constants are simply cleared out of both sides if the equation.
     */
    NSTimeInterval m2TimeFrac = (m2LogoTopDistance * (1.0 - m1TimeFrac)) / (m2LogoTopDistance + m3Distance);
    
    m2TimeFrac = fabs(m2TimeFrac);
    
    //NSTimeInterval m3TimeFrac = 1.0 - m1TimeFrac - m2TimeFrac; //correct be used only by NSLogs
    
    NSTimeInterval m1Duration = duration*m1TimeFrac;
    NSTimeInterval m2Duration = duration*m2TimeFrac;
    NSTimeInterval m3Duration = duration-m1Duration-m2Duration;
    
    /*NSLog(@"Total duration: %lf + %lf + %lf = %lf", m1Duration, m2Duration, m3Duration, m1Duration+m2Duration+m3Duration);
    NSLog(@"M1 %lf", m1TimeFrac);
    NSLog(@"M2 %lf avg speed: %lf, final speed: %lf", m2TimeFrac, m2LogoTopDistance/m2Duration, m2LogoTopDistance/m2Duration);
    NSLog(@"M3 %lf avg speed: %lf", m3TimeFrac, m3Distance/m3Duration);*/
    
    [self.drawingImageView addConstraint:compressedHeightConstraint];
    self.drawingImageViewCenterYConstraint.constant = m2LogoCenterDistance;
    
    [UIView animateWithDuration:m1Duration delay:delay options:UIViewAnimationOptionCurveEaseInOut animations:^{
        [self.backgroundImageView layoutIfNeeded];
    } completion:^(BOOL finished) {
        
        [self.drawingImageView removeConstraint:compressedHeightConstraint];
        self.drawingImageViewCenterYConstraint.constant = originalCenterYConstraintConstant;
        
        [UIView animateWithDuration:m2Duration delay:0.0 options:UIViewAnimationOptionCurveEaseIn animations:^{
            [self.backgroundImageView layoutIfNeeded];
        } completion:^(BOOL finished) {
            
            self.drawingImageViewCenterYConstraint.constant = finalCenterYConstraintConstant;
            [UIView animateWithDuration:m3Duration delay:0.0 options:UIViewAnimationOptionCurveEaseOut animations:^{
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
    } else if ([PCUtils is4_7inchDevice]) {
        return [UIImage imageNamed:@"SplashImage4_7inch"];
    } else if ([PCUtils is5_5inchDevice]) {
        return [UIImage imageNamed:@"SplashImage5_5inch"];
    } else {
        return [UIImage imageNamed:@"SplashImage"];
    }
}

@end
