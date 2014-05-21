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






//  Created by Loïc Gardiol on 15.01.13.



#import <UIKit/UIKit.h>

@interface UIActionSheet (Additions)

/*
 * Call this method from action handler of bar button item to show action sheet or dismiss it
 * with clicked button index cancel automatically if it is already visible
 *
 * IMPORTANT: you must release the action sheet when the delegate method actionSheet:didDismissWithButtonIndex: 
 * has been called. Otherwise, repeated toggling will cause the app to crash (API weakness).
 */
- (void)toggleFromBarButtonItem:(UIBarButtonItem *)item animated:(BOOL)animated;

/*
 * Same as previous, but will also first dismiss (not animated) any UIPopoverController or UIActionSheet that is in othersToDismiss (if not nil)
 */
- (void)toggleFromBarButtonItem:(UIBarButtonItem *)item othersToDismiss:(NSArray*)othersToDismiss animated:(BOOL)animated;

/*
 * Call this method from action handler of view to show action sheet or automatically dismiss it
 * with clicked button index cancel if it is already visible.
 */
- (void)toggleFromRect:(CGRect)rect inView:(UIView *)view animated:(BOOL)animated;

@end
