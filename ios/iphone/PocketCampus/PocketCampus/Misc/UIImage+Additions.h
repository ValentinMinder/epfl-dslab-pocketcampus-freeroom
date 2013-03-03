//
//  UIImage+Additions.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 02.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIImage (Additions)

- (UIImage*)imageByScalingAndCroppingForSize:(CGSize)targetSize;

@end
