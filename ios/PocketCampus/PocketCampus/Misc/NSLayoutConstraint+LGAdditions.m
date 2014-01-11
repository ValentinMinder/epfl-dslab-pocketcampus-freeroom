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


//  Created by Loïc Gardiol on 28.06.13.


#import "NSLayoutConstraint+LGAdditions.h"

@implementation NSLayoutConstraint (LGAdditions)

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
