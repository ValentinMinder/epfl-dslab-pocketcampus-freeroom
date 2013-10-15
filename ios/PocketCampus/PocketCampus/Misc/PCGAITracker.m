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

#import "PCUtils.h"

static id instance __strong = nil;

static BOOL configChecked = NO;

@interface PCGAITracker ()

@property (nonatomic, strong) id<GAITracker> gaiTracker;

@end

@implementation PCGAITracker

+ (instancetype)sharedTracker {
    @synchronized(self) {
        if (instance) {
            return instance;
        }
        if (configChecked) {
            return nil;
        }
        /* Start Google Analytics tracker if enabled in config */
        if ([[PCConfig defaults] boolForKey:PC_CONFIG_GAN_ENABLED_KEY]) {
            NSLog(@"-> Starting Google Analytics tracker.");
            instance = [[PCGAITracker alloc] init];
            [instance initGAIConfig];
        } else {
            NSLog(@"-> Google Analytics disabled (config)");
        }
        configChecked = YES;
        return instance;
    }

}

- (void)initGAIConfig {
    NSString* ganId = (NSString*)[[PCConfig defaults] objectForKey:PC_CONFIG_GAN_TRACKING_CODE_KEY];
    if (ganId.length == 0) {
        NSLog(@"!! ERROR: cannot start Google Analytics tracker because tracking code is empty or absent from config.");
        return;
    }
    // Optional: automatically send uncaught exceptions to Google Analytics.
    [GAI sharedInstance].trackUncaughtExceptions = YES;
    [GAI sharedInstance].dispatchInterval = 10;
    // Optional: set Logger to VERBOSE for debug information.
    [[[GAI sharedInstance] logger] setLogLevel:kGAILogLevelWarning];
    
    self.gaiTracker = [[GAI sharedInstance] trackerWithTrackingId:ganId];
    
    [self.gaiTracker set:PC_PROD_APP_VERSION_KEY value:[PCUtils appVersion]];
}

- (void)trackScreenWithName:(NSString*)screenName {
    if (screenName.length == 0) {
        NSLog(@"!! ERROR: cannot track screeName of length 0.");
        return;
    }
    [self.gaiTracker set:kGAIScreenName value:screenName];
    [self.gaiTracker send:[[GAIDictionaryBuilder createAppView] build]];
}

@end
