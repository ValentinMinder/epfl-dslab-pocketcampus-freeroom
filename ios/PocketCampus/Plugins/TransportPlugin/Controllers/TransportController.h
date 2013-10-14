//
//  TransportController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PluginController.h"

#define kSettingsKey @"settings"
#define kTransportSettingsKeyBestResult @"bestResult"

@interface TransportController : PluginController<PluginControllerProtocol>

/* will aggregate all settings in 1 file */
+ (BOOL)saveObjectSetting:(NSObject<NSCoding>*)val forKey:(NSString*)settingKey;
+ (id<NSCoding>)objectSettingForKey:(NSString*)settingKey;

@end
