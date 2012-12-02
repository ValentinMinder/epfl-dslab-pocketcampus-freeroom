//
//  UIBarButtonItem+CustomViewImage.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "UIBarButtonItem+CustomViewImage.h"

@implementation UIBarButtonItem (CustomViewImage)

+ (UIBarButtonItem*)barItemWithImage:(UIImage*)image target:(id)target action:(SEL)action {
    UIButton* button = [[[UIButton alloc] initWithFrame:CGRectMake(0.0, 0.0, 25.0, 25.0)] autorelease];
    [button setImage:image forState:UIControlStateNormal];
    button.adjustsImageWhenHighlighted = NO;
    button.showsTouchWhenHighlighted = YES;
    [button addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
    return [[UIBarButtonItem alloc] initWithCustomView:button];
}

@end
