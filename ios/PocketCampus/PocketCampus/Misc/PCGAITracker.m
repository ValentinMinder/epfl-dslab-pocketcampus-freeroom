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
NSString* const PCGAITrackerActionRefresh = @"Refresh";

static NSString* const kAccessibilityEnabledKey = @"AccessibilityEnabled";

static NSString* const kFirstLaunchAfterInstallAction = @"FirstLaunchAfterInstall";

static NSString* const kAppCrashedDuringPreviousExecution = @"AppCrashedDuringPreviousExecution";

static NSString* const kEventCategoryUserAction = @"UserAction";
static NSString* const kEventCategoryOther = @"Other";

static NSString* const kOfflineScreensUserDefaultsArrayKey = @"PCGAITrackOfflineScreens";
static NSString* const kOfflineActionsUserDefaultsArrayKey = @"PCGAITrackOfflineActions";
static NSString* const kActionUserDefaultsStringKey = @"Action";
static NSString* const kScreenNameUserDefaultsStringKey = @"Screen";
static NSString* const kContentInfoUserDefaultsStringKey = @"ContentInto";

static NSString* const kOfflineScreenPrefix = @"external-";

static id instance __strong = nil;

@interface PCGAITracker ()

@property (nonatomic, strong) id<GAITracker> gaiTracker;

@end

@implementation PCGAITracker

#pragma mark - Public

+ (instancetype)sharedTracker {
#ifndef DEBUG
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[PCGAITracker alloc] init];
        if ([[PCConfig defaults] boolForKey:PC_CONFIG_GAN_ENABLED_KEY]) {
            [instance initGAIConfig];
            CLSNSLog(@"-> Starting Google Analytics tracker.");
            [instance trackAction:@"DeviceInfo" inScreenWithName:@"/" category:kEventCategoryOther contentInfo:[NSString stringWithFormat:@"{VoiceOverEnabled:%@}", UIAccessibilityIsVoiceOverRunning() ? @"YES" : @"NO"]];
        } else {
            CLSNSLog(@"-> Google Analytics disabled (config)");
        }
    });
#endif
#ifndef TARGET_IS_EXTENSION
    [instance popAndTrackOfflineScreensAndActions];
#endif
    return instance;
}

- (void)trackScreenWithName:(NSString*)screenName {
    if (screenName.length == 0) {
        CLSNSLog(@"!! ERROR: cannot track nil screeName or of length 0.");
        return;
    }
    
#ifdef TARGET_IS_EXTENSION
    [self.class trackOfflineScreenWithName:screenName];
#else
    CLSLog(@"Track screen: '%@'", screenName);
    [self.gaiTracker set:kGAIScreenName value:screenName];
    [self.gaiTracker send:[[GAIDictionaryBuilder createAppView] build]];
#endif
}

- (void)trackAction:(NSString*)action inScreenWithName:(NSString*)screenName {
    [self trackAction:action inScreenWithName:screenName contentInfo:nil];
}

- (void)trackAction:(NSString*)action inScreenWithName:(NSString*)screenName contentInfo:(NSString*)contentInfo {
#ifdef TARGET_IS_EXTENSION
    [self.class trackOfflineAction:action inScreenWithName:screenName contentInfo:contentInfo];
#else
    [self trackAction:action inScreenWithName:screenName category:kEventCategoryUserAction contentInfo:contentInfo];
#endif
    
}

- (void)trackAppOnce {
    if ([[PCConfig defaults] boolForKey:kFirstLaunchAfterInstallAction]) {
        return;
    }
    CLSNSLog(@"-> First app launch, sending '%@' event to Google Analytics", kFirstLaunchAfterInstallAction);
    [[PCConfig defaults] setBool:YES forKey:kFirstLaunchAfterInstallAction];
    [[PCConfig defaults] synchronize];
    [self trackAction:kFirstLaunchAfterInstallAction inScreenWithName:@"/" category:kEventCategoryOther contentInfo:nil];
    
}

- (void)trackAppCrashedDuringPreviousExecution {
    [self trackAction:kAppCrashedDuringPreviousExecution inScreenWithName:@"/" category:kEventCategoryOther contentInfo:nil];
}

#pragma mark - Private

- (void)trackAction:(NSString*)action inScreenWithName:(NSString*)screenName category:(NSString*)category contentInfo:(NSString*)contentInfo {
    if (action.length == 0) {
        CLSNSLog(@"!! ERROR: cannot track nil action or of length 0.");
        return;
    }
    action = screenName.length > 0 ? [screenName stringByAppendingFormat:@"-%@", action] : action;
    CLSLog(@"Track action '%@', content info: '%@'", action, contentInfo);
    [self.gaiTracker set:kGAIScreenName value:screenName];
    [self.gaiTracker send:[[GAIDictionaryBuilder createEventWithCategory:category action:action label:contentInfo value:nil] build]];
}

+ (void)trackOfflineScreenWithName:(NSString*)screenName {
    if (screenName.length == 0) {
        CLSNSLog(@"!! ERROR: cannot track nil screeName or of length 0.");
        return;
    }
    CLSLog(@"Track offline screen: '%@'", screenName);
    NSMutableArray* offlineScreens = [[[PCPersistenceManager sharedDefaults] objectForKey:kOfflineScreensUserDefaultsArrayKey] mutableCopy];
    if (!offlineScreens) {
        offlineScreens = [NSMutableArray array];
    }
    [offlineScreens addObject:screenName];
    [[PCPersistenceManager sharedDefaults] setObject:offlineScreens forKey:kOfflineScreensUserDefaultsArrayKey];
    [[PCPersistenceManager sharedDefaults] synchronize];
}

+ (void)trackOfflineAction:(NSString*)action inScreenWithName:(NSString*)screenName {
    [self trackOfflineAction:action inScreenWithName:screenName contentInfo:nil];
}

+ (void)trackOfflineAction:(NSString*)action inScreenWithName:(NSString*)screenName contentInfo:(NSString*)contentInfo {
    if (action.length == 0) {
        CLSNSLog(@"!! ERROR: cannot track nil action or of length 0.");
        return;
    }
    CLSLog(@"Track offline action '%@', content info: '%@'", action, contentInfo);
    NSMutableArray* offlineActions = [[[PCPersistenceManager sharedDefaults] objectForKey:kOfflineActionsUserDefaultsArrayKey] mutableCopy];
    if (!offlineActions) {
        offlineActions = [NSMutableArray array];
    }
    NSDictionary* offlineActionDic = nil;
    if (screenName && contentInfo) {
        offlineActionDic = @{kActionUserDefaultsStringKey:action, kScreenNameUserDefaultsStringKey: screenName, kContentInfoUserDefaultsStringKey: contentInfo};
    } else if (screenName) {
        offlineActionDic = @{kActionUserDefaultsStringKey:action, kScreenNameUserDefaultsStringKey: screenName};
    } else if (contentInfo) {
        offlineActionDic = @{kActionUserDefaultsStringKey:action, kContentInfoUserDefaultsStringKey: contentInfo};
    } else {
        offlineActionDic = @{kActionUserDefaultsStringKey:action};
    }
    [offlineActions addObject:offlineActionDic];
    [[PCPersistenceManager sharedDefaults] setObject:offlineActions forKey:kOfflineActionsUserDefaultsArrayKey];
    [[PCPersistenceManager sharedDefaults] synchronize];
}

- (void)popAndTrackOfflineScreensAndActions NS_EXTENSION_UNAVAILABLE_IOS("") {
    BOOL found = NO;
    
    NSArray* offlineScreens = [[PCPersistenceManager sharedDefaults] objectForKey:kOfflineScreensUserDefaultsArrayKey];
    if (offlineScreens.count > 0) {
        CLSNSLog(@"-> Now tracking %u offline screens(s):", offlineScreens.count);
        found = YES;
        for (__strong NSString* offlineScreen in offlineScreens) {
            if (offlineScreen) {
                offlineScreen = [NSString stringWithFormat:@"%@%@", kOfflineScreenPrefix, offlineScreen];
            }
            [self trackScreenWithName:offlineScreen];
        }
        [[PCPersistenceManager sharedDefaults] removeObjectForKey:kOfflineScreensUserDefaultsArrayKey];
    }

    
    NSArray* offlineActions = [[PCPersistenceManager sharedDefaults] objectForKey:kOfflineActionsUserDefaultsArrayKey];
    if (offlineActions.count > 0) {
        CLSNSLog(@"Now tracking %u offline action(s):", offlineActions.count);
        found = YES;
        for (NSDictionary* offlineActionDic in offlineActions) {
            NSString* action = offlineActionDic[kActionUserDefaultsStringKey];
            NSString* screenName = offlineActionDic[kScreenNameUserDefaultsStringKey];
            NSString* contentInfo = offlineActionDic[kContentInfoUserDefaultsStringKey];
            if (screenName) {
                screenName = [NSString stringWithFormat:@"%@%@", kOfflineScreenPrefix, screenName];
            }
            [self trackAction:action inScreenWithName:screenName category:kEventCategoryUserAction contentInfo:contentInfo];
        }
        [[PCPersistenceManager sharedDefaults] removeObjectForKey:kOfflineActionsUserDefaultsArrayKey];
    }
    
    if (found) {
        [[PCPersistenceManager sharedDefaults] synchronize];
    }
}

- (void)initGAIConfig {
#ifndef TARGET_IS_EXTENSION
    if (![PCConfig isLoaded]) {
        CLSNSLog(@"-> Cannot create PCGAITracker sharedTracker instance because PCConfig is not loading yet. Returning nil.");
        return;
    }
    NSString* ganId = (NSString*)[[PCConfig defaults] objectForKey:PC_CONFIG_GAN_TRACKING_CODE_KEY];
    if (ganId.length == 0) {
        CLSNSLog(@"!! ERROR: cannot start Google Analytics tracker because tracking code is empty or absent from config.");
        return;
    }
    [GAI sharedInstance].trackUncaughtExceptions = NO;
    [GAI sharedInstance].dispatchInterval = 10;
    [GAI sharedInstance].dryRun = NO;
    // Optional: set Logger to VERBOSE for debug information.
    [[[GAI sharedInstance] logger] setLogLevel:kGAILogLevelWarning];
    self.gaiTracker = [[GAI sharedInstance] trackerWithTrackingId:ganId];
#endif
}

@end
