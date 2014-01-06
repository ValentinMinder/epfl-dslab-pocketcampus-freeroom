//
//  PCGAITracker.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 20.09.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "PCGAITracker.h"

#import "GAI.h"
#import "GAIDictionaryBuilder.h"
#import "GAIFields.h"

NSString* const PCGAITrackerActionMarkFavorite = @"MarkFavorite";
NSString* const PCGAITrackerActionUnmarkFavorite = @"UnmarkFavorite";
NSString* const PCGAITrackerActionActionButtonPressed = @"ActionButtonPressed";
NSString* const PCGAITrackerActionClearHistory = @"ClearHistory";
NSString* const PCGAITrackerActionAdd = @"Add";
NSString* const PCGAITrackerActionDelete = @"Delete";
NSString* const PCGAITrackerActionReorder = @"Reorder";
NSString* const PCGAITrackerActionCopy = @"Copy";
NSString* const PCGAITrackerActionHelp = @"Help";
NSString* const PCGAITrackerActionSearch = @"Search";

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
    NSLog(@"-> Sending screen: %@", screenName);
    [self.gaiTracker set:kGAIScreenName value:screenName];
    [self.gaiTracker send:[[GAIDictionaryBuilder createAppView] build]];
}

- (void)trackAction:(NSString*)action inScreenWithName:(NSString*)screenName {
    if (action.length == 0) {
        NSLog(@"!! ERROR: cannot track nil action or of length 0.");
        return;
    }
    NSLog(@"-> Sending action: %@ in screen: %@", action, screenName);
    action = screenName.length > 0 ? [screenName stringByAppendingFormat:@"-%@", action] : action;
    [self.gaiTracker set:kGAIScreenName value:screenName];
    [self.gaiTracker send:[[GAIDictionaryBuilder createEventWithCategory:@"ui_action" action:action label:nil value:nil] build]];
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
