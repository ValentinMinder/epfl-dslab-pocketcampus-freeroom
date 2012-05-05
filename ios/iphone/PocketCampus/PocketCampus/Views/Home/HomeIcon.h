//
//  HomeIcon.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 02.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "HomeViewController.h"

//images should be of size 65 x 65 pixels (130 x 130 for @2x versions)

@interface HomeIcon : UIControl {
    NSUInteger index;
    HomeViewController* controller;
    UIImageView* imageView;
    UIImage* normalImage;
    UIImage* highlightedImage;
}

- (id)initWithController:(HomeViewController*)controller_ index:(NSUInteger)index_ title:(NSString*)title normalStateImageName:(NSString*)normalImage andHighlightedStateImageName:(NSString*)highlightedImage;
- (void)touchDown;
- (void)touchCancel;

@end
