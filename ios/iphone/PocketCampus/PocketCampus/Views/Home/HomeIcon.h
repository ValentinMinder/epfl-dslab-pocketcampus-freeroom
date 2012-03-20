//
//  HomeIcon.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 02.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "HomeViewController.h"

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
