//
//  CamiproController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PluginControllerAuthentified.h"

@interface CamiproController : PluginControllerAuthentified<PluginControllerProtocol>

+ (void)initObservers;

@end
