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



#import <UIKit/UIKit.h>

static const CGFloat kNoInsetConstraint = CGFLOAT_MIN;

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
