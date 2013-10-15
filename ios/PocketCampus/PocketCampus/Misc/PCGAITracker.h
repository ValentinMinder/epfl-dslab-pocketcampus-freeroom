//
//  PCGAITracker.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 20.09.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface PCGAITracker : NSObject

/*
 * If not existing, creates and returns a tracker with code fetched from PCConfig.
 * If Google Analytics indicated as disabled in config, this method returns nil.
 * */
+ (instancetype)sharedTracker;

- (void)trackScreenWithName:(NSString*)screenName;

@end
