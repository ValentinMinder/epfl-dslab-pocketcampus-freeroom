//
//  NSLayoutConstraint+Extras.m
//  HEALP.me
//
//  Created by Loïc Gardiol on 28.06.13.
//  Copyright (c) 2013 UsefulWeb.ch. All rights reserved.
//

#import "NSLayoutConstraint+Extras.h"

@implementation NSLayoutConstraint (Extras)

+ (NSLayoutConstraint*)widthConstraint:(CGFloat)width forView:(UIView*)view {
    return [NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:width];
}

+ (NSLayoutConstraint*)heightConstraint:(CGFloat)height forView:(UIView*)view {
    return [NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:height];
}

+ (NSArray*)width:(CGFloat)width height:(CGFloat)height constraintsForView:(UIView*)view {
    return @[[self widthConstraint:width forView:view], [self heightConstraint:height forView:view]];
}

+ (NSArray*)constraintsToSuperview:(UIView*)superview forView:(UIView*)view edgeInsets:(UIEdgeInsets)edgeInsets {
    NSMutableArray* constraints = [NSMutableArray arrayWithCapacity:4];
    if (edgeInsets.top != kNoInsetConstraint) {
        [constraints addObject:[NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:superview attribute:NSLayoutAttributeTop multiplier:1.0 constant:edgeInsets.top]];
    }
    if (edgeInsets.left != kNoInsetConstraint) {
        [constraints addObject:[NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:superview attribute:NSLayoutAttributeLeft multiplier:1.0 constant:edgeInsets.left]];
    }
    if (edgeInsets.bottom != kNoInsetConstraint) {
        [constraints addObject:[NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:superview attribute:NSLayoutAttributeBottom multiplier:1.0 constant:edgeInsets.bottom]];
    }
    if (edgeInsets.right != kNoInsetConstraint) {
        [constraints addObject:[NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:superview attribute:NSLayoutAttributeRight multiplier:1.0 constant:edgeInsets.right]];
    }
    return constraints;
}

+ (NSLayoutConstraint*)constraintForCenterXtoSuperview:(UIView*)superview forView:(UIView*)view constant:(CGFloat)constant {
    return [NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeCenterX relatedBy:NSLayoutRelationEqual toItem:superview attribute:NSLayoutAttributeCenterX multiplier:1.0 constant:constant];
}

+ (NSLayoutConstraint*)constraintForCenterYtoSuperview:(UIView*)superview forView:(UIView*)view constant:(CGFloat)constant {
    return [NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:superview attribute:NSLayoutAttributeCenterY multiplier:1.0 constant:constant];
}

+ (NSArray*)constraintsForCenterXYtoSuperview:(UIView*)superview forView:(UIView*)view {
    return @[[self constraintForCenterXtoSuperview:superview forView:view constant:0.0],
             [self constraintForCenterYtoSuperview:superview forView:view constant:0.0]];
}

@end
