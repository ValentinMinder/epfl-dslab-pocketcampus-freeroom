//
//  MainController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 02.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

@class PluginController;

@interface MainController : NSObject {
    @private UINavigationController* navController;
    @private UIWindow* window;
    @private NSArray* pluginsList; //list of found plugins names (NSString*)
}

@property (readonly) NSArray* pluginsList;
@property (assign) PluginController* activePluginController;

- (id)initWithWindow:(UIWindow*)window;
- (NSString*)pluginControllerNameForIdentifier:(NSString*)identifier;
- (NSString*)pluginControllerNameForIndex:(NSUInteger)index;
- (void)refreshDisplayedPlugin;

@end
