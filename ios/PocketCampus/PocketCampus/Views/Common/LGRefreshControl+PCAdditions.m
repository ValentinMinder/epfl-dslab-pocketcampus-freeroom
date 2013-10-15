//
//  LGRefreshControl+PCAdditions.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 29.09.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "LGRefreshControl+PCAdditions.h"

@implementation LGRefreshControl (PCAdditions)

+ (NSString*)dataIdentifierForPluginName:(NSString*)pluginName dataName:(NSString*)dataName {
    [PCUtils throwExceptionIfObject:pluginName notKindOfClass:NSString.class];
    [PCUtils throwExceptionIfObject:dataName notKindOfClass:NSString.class];
    return [NSString stringWithFormat:@"%@-%@", pluginName, dataName];
}

@end
