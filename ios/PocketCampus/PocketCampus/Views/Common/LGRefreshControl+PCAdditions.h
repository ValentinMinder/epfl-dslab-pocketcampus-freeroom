//
//  LGRefreshControl+PCAdditions.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 29.09.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "LGRefreshControl.h"

@interface LGRefreshControl (PCAdditions)

+ (NSString*)dataIdentifierForPluginName:(NSString*)pluginName dataName:(NSString*)dataName;

@end
