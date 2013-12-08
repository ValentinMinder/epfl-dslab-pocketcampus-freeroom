//
//  PCSplashView.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 30.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PCSplashView : UIView

- (id)initWithSuperview:(UIView*)superview;
- (void)moveToSuperview:(UIView*)superview;
- (void)hideWithAnimationDuration:(NSTimeInterval)duration completion:(VoidBlock)completion;

@end
