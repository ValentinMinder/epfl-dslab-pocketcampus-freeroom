//
//  CamiproController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PluginController.h"

@interface CamiproController : PluginController<PluginControllerProtocol>

+ (void)initObservers;

@end
