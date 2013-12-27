//
//  PCValues.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 10.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PCValues.h"

#import "PCUtils.h"

#import "PCTableViewSectionHeader.h"

#import <QuartzCore/QuartzCore.h>

@implementation PCValues

+ (void)applyAppearenceProxy {
    //nothing for now
}

+ (UIImage*)imageForFavoriteNavBarButtonLandscapePhone:(BOOL)landscapePhone glow:(BOOL)glow {
    NSString* imageName = landscapePhone ? (glow ? @"FavoriteGlowNavBarButtonLandscape" : @"FavoriteNavBarButtonLandscape") : (glow ? @"FavoriteGlowNavBarButton" : @"FavoriteNavBarButton");
    return [UIImage imageNamed:imageName];
}

+ (UIColor*)pocketCampusRed {
    //return [UIColor redColor];
    return [UIColor colorWithRed:0.858824 green:0.062745 blue:0.062745 alpha:1.0]; //220, 16, 16
    //return [UIColor colorWithRed:0.66666 green:0 blue:0.101960 alpha:1.0]; //170, 0, 26
}

+ (UIColor*)backgroundColor1 {
    return [UIColor whiteColor];
    //return [UIColor colorWithWhite:0.93 alpha:1.0];
}

+ (UIColor*)textColor1 {
    return [UIColor colorWithWhite:0.25 alpha:1.0];;
}

+ (UIColor*)textColorLocationBlue {
    return [UIColor colorWithRed:0.141176 green:0.376471 blue:0.733333 alpha:1.0];
}

+ (CGSize)shadowOffset1 {
    return CGSizeMake(0, 0);
    //return CGSizeMake(0.0, 1.0);
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
    return [PCTableViewSectionHeader preferredHeight];
}

@end
