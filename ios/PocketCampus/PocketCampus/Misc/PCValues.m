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




//  Created by Loïc Gardiol on 10.03.12.


#import "PCValues.h"

#import "PCUtils.h"

#import "PCTableViewSectionHeader.h"

#import <QuartzCore/QuartzCore.h>

@implementation PCValues

+ (void)applyAppearenceProxy {
    [[UICollectionView appearanceWhenContainedIn:[UIAlertController class], nil] setTintColor:[self defaultBlueTintColor]];
}

+ (UIImage*)imageForFavoriteNavBarButtonLandscapePhone:(BOOL)landscapePhone glow:(BOOL)glow {
    NSString* imageName = landscapePhone ? (glow ? @"FavoriteGlowNavBarButtonLandscape" : @"FavoriteNavBarButtonLandscape") : (glow ? @"FavoriteGlowNavBarButton" : @"FavoriteNavBarButton");
    return [UIImage imageNamed:imageName];
}

+ (UIImage*)imageForPrintBarButtonLandscapePhone:(BOOL)landscapePhone {
    return [UIImage imageNamed:landscapePhone ? @"PrintBarButtonLandscape" : @"PrintBarButton"];
}

+ (UIColor*)defaultBlueTintColor {
    return [UIColor colorWithRed:0.0 green:122.0/255.0 blue:1.0 alpha:1.0];
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

@end
