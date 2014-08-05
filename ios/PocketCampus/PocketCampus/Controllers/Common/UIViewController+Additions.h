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

//  Created by Loïc Gardiol on 06.01.14.

#import <UIKit/UIKit.h>

@interface UIViewController (Additions)

@property (nonatomic, strong) NSString* gaiScreenName;

/*
 * Shortcut for PCGAITracker trackScreenWithName
 * Does nothing (emits a warning) if self.gaiScreenName is nil
 */
- (void)trackScreen;

/*
 * Shortcut for PCGAITracker trackScreenWithName:action: with screenName = self.gaiScreenName
 * Does nothing (emits a warning) if self.gaiScreenName is nil
 */
- (void)trackAction:(NSString*)action;

/*
 * Shortcut for PCGAITracker trackScreenWithName:action:contentInfo: with screenName = self.gaiScreenName
 * Does nothing (emits a warning) if self.gaiScreenName is nil
 */
- (void)trackAction:(NSString*)action contentInfo:(NSString*)contentInfo;

/**
 * @return YES if self is being popped of its navigation controller's stack
 */
@property (nonatomic, readonly) BOOL isDisappearingBecausePopped;

/**
 * @return YES if self is disappearing because a new view controller is
 * being pushed on its navigation controller's stack
 */
@property (nonatomic, readonly) BOOL isDisappearingBecauseOtherPushed;

@end
