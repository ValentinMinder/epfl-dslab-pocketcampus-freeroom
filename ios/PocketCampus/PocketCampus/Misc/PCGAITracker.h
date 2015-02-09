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

//  Created by Loïc Gardiol on 20.09.13.

@import Foundation;

extern NSString* const PCGAITrackerActionMarkFavorite;
extern NSString* const PCGAITrackerActionUnmarkFavorite;
extern NSString* const PCGAITrackerActionActionButtonPressed;
extern NSString* const PCGAITrackerActionClearHistory;
extern NSString* const PCGAITrackerActionAdd;
extern NSString* const PCGAITrackerActionDelete;
extern NSString* const PCGAITrackerActionReorder;
extern NSString* const PCGAITrackerActionCopy;
extern NSString* const PCGAITrackerActionHelp;
extern NSString* const PCGAITrackerActionSearch;
extern NSString* const PCGAITrackerActionRefresh;

@interface PCGAITracker : NSObject

/**
 * If not existing, creates and returns a tracker with code fetched from PCConfig.
 * If Google Analytics indicated as disabled in config, this method returns nil.
 * */
+ (instancetype)sharedTracker;

- (void)trackScreenWithName:(NSString*)screenName;

/**
 * Action can be either one of the PCGAITrackerAction above, or custom (string should be CamelCased)
 * Same as next with nil contentInfo.
 */
- (void)trackAction:(NSString*)action inScreenWithName:(NSString*)screenName;

/**
 * Same as previous one, with possiblity to pass custom key-values to be added to the GAN event.
 */
- (void)trackAction:(NSString*)action inScreenWithName:(NSString*)screenName contentInfo:(NSString*)contentInfo;

/**
 * The first time ever this method is called (even between app launched)
 * a first launch action is sent to GAN. Does nothing the other times.
 */
- (void)trackAppOnce NS_EXTENSION_UNAVAILABLE_IOS("");

- (void)trackAppCrashedDuringPreviousExecution NS_EXTENSION_UNAVAILABLE_IOS("");

@end
