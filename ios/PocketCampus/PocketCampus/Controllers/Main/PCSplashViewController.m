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




//  Created by Loïc Gardiol on 21.10.12.


#import "PCSplashViewController.h"

#import <QuartzCore/QuartzCore.h>

@interface PCSplashViewController ()

@property (nonatomic, retain) UIImageView* splashViewImage;

@property (nonatomic, retain) NSLayoutConstraint* leadingSpaceToSuperViewConstraint;

@end

@implementation PCSplashViewController

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
        [self.view layoutIfNeeded];
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
