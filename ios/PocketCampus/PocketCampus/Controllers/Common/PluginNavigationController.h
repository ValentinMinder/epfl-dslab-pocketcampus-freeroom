//
//  PluginNavigationController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "PCNavigationController.h"

@interface PluginNavigationController : PCNavigationController

@property (nonatomic, copy) NSString* pluginIdentifier;

@end
