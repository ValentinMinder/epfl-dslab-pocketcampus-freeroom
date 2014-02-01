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


#import "PCGAITracker.h"

#import "GAI.h"
#import "GAIDictionaryBuilder.h"
#import "GAIFields.h"

NSString* const PCGAITrackerActionMarkFavorite = @"MarkFavorite";
NSString* const PCGAITrackerActionUnmarkFavorite = @"UnmarkFavorite";
NSString* const PCGAITrackerActionActionButtonPressed = @"Share";
NSString* const PCGAITrackerActionClearHistory = @"ClearHistory";
NSString* const PCGAITrackerActionAdd = @"Add";
NSString* const PCGAITrackerActionDelete = @"Delete";
NSString* const PCGAITrackerActionReorder = @"Reorder";
NSString* const PCGAITrackerActionCopy = @"Copy";
NSString* const PCGAITrackerActionHelp = @"Help";
NSString* const PCGAITrackerActionSearch = @"Search";

static NSString* const kAccessibilityEnabledKey = @"AccessibilityEnabled";

static NSString* const kFirstLaunchAfterInstallAction = @"FirstLaunchAfterInstall";

static NSString* const kAppCrashedDuringPreviousExecution = @"AppCrashedDuringPreviousExecution";

static id instance __strong = nil;

@interface PCGAITracker ()

@property (nonatomic, strong) id<GAITracker> gaiTracker;

@end

@implementation PCGAITracker

#pragma mark - Public

+ (instancetype)sharedTracker {
    if (![PCConfig isLoaded]) {
        NSLog(@"-> Cannot create PCGAITracker sharedTracker instance because PCConfig is not loading yet. Returning nil.");
        return nil;
    }
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[PCGAITracker alloc] init];
        if ([[PCConfig defaults] boolForKey:PC_CONFIG_GAN_ENABLED_KEY]) {
            [instance initGAIConfig];
            NSLog(@"-> Starting Google Analytics tracker.");
        } else {
            NSLog(@"-> Google Analytics disabled (config)");
        }
    });
    return instance;
}

- (void)trackScreenWithName:(NSString*)screenName {
    if (screenName.length == 0) {
        NSLog(@"!! ERROR: cannot track nil screeName or of length 0.");
        return;
    }
    [self.gaiTracker set:kGAIScreenName value:screenName];
    [self.gaiTracker set:kAccessibilityEnabledKey value:UIAccessibilityIsVoiceOverRunning() ? @"YES" : @"NO"];
    [self.gaiTracker send:[[GAIDictionaryBuilder createAppView] build]];
}

- (void)trackAction:(NSString*)action inScreenWithName:(NSString*)screenName {
    if (action.length == 0) {
        NSLog(@"!! ERROR: cannot track nil action or of length 0.");
        return;
    }
    action = screenName.length > 0 ? [screenName stringByAppendingFormat:@"-%@", action] : action;
    [self.gaiTracker set:kGAIScreenName value:screenName];
    [self.gaiTracker send:[[GAIDictionaryBuilder createEventWithCategory:@"ui_action" action:action label:nil value:nil] build]];
}

- (void)trackAppOnce {
    if ([[PCConfig defaults] boolForKey:kFirstLaunchAfterInstallAction]) {
        return;
    }
    NSLog(@"-> First app launch, sending '%@' event to Google Analytics", kFirstLaunchAfterInstallAction);
    [[PCConfig defaults] setBool:YES forKey:kFirstLaunchAfterInstallAction];
    [[PCConfig defaults] synchronize];
    [self trackAction:kFirstLaunchAfterInstallAction inScreenWithName:@"/"];
    
}

- (void)trackAppCrashedDuringPreviousExecution {
    [self trackAction:kAppCrashedDuringPreviousExecution inScreenWithName:@"/"];
}

#pragma mark - Private

- (void)initGAIConfig {
    NSString* ganId = (NSString*)[[PCConfig defaults] objectForKey:PC_CONFIG_GAN_TRACKING_CODE_KEY];
    if (ganId.length == 0) {
        NSLog(@"!! ERROR: cannot start Google Analytics tracker because tracking code is empty or absent from config.");
        return;
    }
    [GAI sharedInstance].trackUncaughtExceptions = NO;
    [GAI sharedInstance].dispatchInterval = 10;
    [GAI sharedInstance].dryRun = NO;
    // Optional: set Logger to VERBOSE for debug information.
    [[[GAI sharedInstance] logger] setLogLevel:kGAILogLevelWarning];
    self.gaiTracker = [[GAI sharedInstance] trackerWithTrackingId:ganId];
}

@end
