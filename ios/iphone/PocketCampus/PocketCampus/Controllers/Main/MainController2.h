//
//  MainController2.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface MainController2 : NSObject

- (id)initWithWindow:(UIWindow*)window;
- (void)refreshDisplayedPlugin;

- (void)setActivePluginWithIdentifier:(NSString*)identifier;

- (void)mainMenuIsReady;

@end
