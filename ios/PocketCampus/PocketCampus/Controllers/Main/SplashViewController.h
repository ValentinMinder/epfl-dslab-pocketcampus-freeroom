//
//  SplashViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 21.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SplashViewController : UIViewController

@property (nonatomic) CGFloat rightHiddenOffset;

@property (nonatomic, weak) UIView* superviewOfView;

- (id)initWithRightHiddenOffset:(CGFloat)rightHiddenOffset;
- (void)willMoveToRightWithDuration:(NSTimeInterval)duration hideDrawingOnIdiomPhone:(BOOL)hideDrawingOnIdiomPhone;

@end
