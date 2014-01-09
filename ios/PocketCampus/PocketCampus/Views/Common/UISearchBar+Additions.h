//
//  UISearchBar+Additions.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 09.01.14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UISearchBar (Additions)

/*
 * Sets the background view layer opacity to 0.0.
 * Hack to a bug of iOS 7.1 b3 where if barTintColor is set to clearColor,
 * background becomes black.
 */
- (void)setBackgroundViewTransparent;

@end
