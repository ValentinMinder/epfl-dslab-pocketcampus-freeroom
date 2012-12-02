//
//  UIBarButtonItem+CustomViewImage.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIBarButtonItem (CustomViewImage)

+ (UIBarButtonItem*)barItemWithImage:(UIImage*)image target:(id)target action:(SEL)action;

@end
