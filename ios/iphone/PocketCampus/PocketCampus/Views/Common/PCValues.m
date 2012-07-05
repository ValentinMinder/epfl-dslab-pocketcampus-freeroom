//
//  PCValues.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 10.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PCValues.h"

@implementation PCValues

+ (UIColor*)pocketCampusRed {
    return [UIColor colorWithRed:0.66666 green:0 blue:0.101960 alpha:1.0]; //170, 0, 26
}

+ (UIColor*)backgroundColor1 {
    return [UIColor colorWithWhite:0.93 alpha:1.0];
}

+ (UIColor*)textColor1 {
    return [UIColor colorWithWhite:0.2 alpha:1.0];;
}

+ (UIColor*)textColorLocationBlue {
    return [UIColor colorWithRed:0.141176 green:0.376471 blue:0.733333 alpha:1.0];
}

+ (CGSize)shadowOffset1 {
    return CGSizeMake(0.0, 1.0);
}

+ (UIColor*)shadowColor1 {
    return [UIColor whiteColor];
}

+ (CGFloat)totalSubviewsHeight:(UIView*)view {
    CGFloat height = 0.0;
    for (UIView* subview in view.subviews) {
        height += subview.frame.size.height;
    }
    return height;
}

+ (CGFloat)tableViewSectionHeaderHeight {
    return 28.0;
}

@end
