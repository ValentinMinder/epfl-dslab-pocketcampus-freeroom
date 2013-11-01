//
//  MapController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 12.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PluginController.h"

@class MapItem;

@interface MapController : PluginController<PluginControllerProtocol>

+ (UIViewController*)viewControllerWithInitialMapItem:(MapItem*)mapItem;
+ (UIViewController*)viewControllerWithInitialSearchQuery:(NSString*)query;
+ (UIViewController*)viewControllerWithInitialSearchQuery:(NSString*)query pinLabelText:(NSString*)pinLabelText;

@end
