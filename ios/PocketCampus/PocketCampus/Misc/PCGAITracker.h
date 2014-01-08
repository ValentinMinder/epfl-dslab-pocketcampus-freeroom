//
//  PCGAITracker.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 20.09.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

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

@interface PCGAITracker : NSObject

/*
 * If not existing, creates and returns a tracker with code fetched from PCConfig.
 * If Google Analytics indicated as disabled in config, this method returns nil.
 * */
+ (instancetype)sharedTracker;

- (void)trackScreenWithName:(NSString*)screenName;

/*
 * Action can be either one of the PCGAITrackerAction above, or custom (string should be CamelCased)
 */
- (void)trackAction:(NSString*)action inScreenWithName:(NSString*)screenName;

/*
 * The first time ever this method is called (even between app launched)
 * a first launch action is sent to GAN. Does nothing the other times.
 */
- (void)trackAppOnce;

@end
