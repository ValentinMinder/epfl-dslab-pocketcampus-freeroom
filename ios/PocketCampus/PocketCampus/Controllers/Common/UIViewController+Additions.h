//
//  UIViewController+Additions.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 06.01.14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIViewController (Additions)

@property (nonatomic, strong) NSString* gaiScreenName;

/*
 * Shortcut for PCGAITracker trackScreenWithName
 * Does nothing (emits a warning) if self.gaiScreenName is nil
 */
- (void)trackScreen;

/*
 * Shortcut for PCGAITracker trackScreenWithName:action:
 * Does nothing (emits a warning) if self.gaiScreenName is nil
 */
- (void)trackAction:(NSString*)action;

@end
