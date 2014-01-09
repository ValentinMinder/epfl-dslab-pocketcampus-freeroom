//
//  UIView+Additions.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 09.01.14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIView (Additions)

/*
 * Returns all subviews of self in breath search first order
 * Note: stops if too many recursions.
 */
- (NSArray*)allSubviews;

@end
