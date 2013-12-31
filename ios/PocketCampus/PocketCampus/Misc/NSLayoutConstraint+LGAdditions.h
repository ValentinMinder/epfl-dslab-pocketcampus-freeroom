//
//  NSLayoutConstraint+LGAdditions.h
//  HEALP.me
//
//  Created by Loïc Gardiol on 28.06.13.
//  Copyright (c) 2013 UsefulWeb.ch. All rights reserved.
//

#import <UIKit/UIKit.h>

static const CGFloat kNoInsetConstraint = FLT_MIN;

@interface NSLayoutConstraint (LGAdditions)

+ (NSLayoutConstraint*)widthConstraint:(CGFloat)width forView:(UIView*)view;
+ (NSLayoutConstraint*)heightConstraint:(CGFloat)height forView:(UIView*)view;
+ (NSArray*)width:(CGFloat)width height:(CGFloat)height constraintsForView:(UIView*)view;

/*
 * Returns array of constraints between superview and view, to be added to superview.
 * edgeInsets define for each edge, what the margin should be between the view and the superview.
 * Note: pass kNoInsertConstraint value for the edges you don't want to apply a constraint to.
 */
+ (NSArray*)constraintsToSuperview:(UIView*)superview forView:(UIView*)view edgeInsets:(UIEdgeInsets)edgeInsets;

+ (NSLayoutConstraint*)constraintForCenterXtoSuperview:(UIView*)superview forView:(UIView*)view constant:(CGFloat)constant;

+ (NSLayoutConstraint*)constraintForCenterYtoSuperview:(UIView*)superview forView:(UIView*)view constant:(CGFloat)constant;

+ (NSArray*)constraintsForCenterXYtoSuperview:(UIView*)superview forView:(UIView*)view;

@end
