//
//  TransportController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "TransportController.h"

#import "NextDeparturesListViewController.h"

#import "ObjectArchiver.h"

@implementation TransportController

static BOOL settingsAreDirty = NO;
static NSMutableDictionary* settings = nil;

- (id)init {
    self = [super init];
    if (self) {
        NextDeparturesListViewController* nextDeparturesListViewController = [[NextDeparturesListViewController alloc] init];
        nextDeparturesListViewController.title = [[self class] localizedName];
        PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:nextDeparturesListViewController];
        navController.pluginIdentifier = [[self class] identifierName];
        self.mainNavigationController = navController;
    }
    return self;
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"TransportPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Transport";
}

- (void)refresh {
    if (self.mainNavigationController.visibleViewController == self.mainNavigationController.viewControllers[0]) {
        [(NextDeparturesListViewController*)(self.mainNavigationController.viewControllers[0]) refresh];
    }
}

+ (BOOL)saveObjectSetting:(NSObject<NSCoding>*)val forKey:(NSString*)settingKey {
    @synchronized(self) {
        if (settings == nil) {
            settings = [[NSMutableDictionary dictionary] retain];
        }
        [settings setObject:val forKey:settingKey];
        settingsAreDirty = YES;
        return [ObjectArchiver saveObject:settings forKey:kSettingsKey andPluginName:@"transport"];
    }
}

+ (id<NSCoding>)objectSettingForKey:(NSString*)settingKey {
    @synchronized(self) {
        if (settings == nil || settingsAreDirty) {
            [settings release];
            settings = (NSMutableDictionary*)[ObjectArchiver objectForKey:kSettingsKey andPluginName:@"transport"];
            [settings retain];
            settingsAreDirty = NO;
        }
        if (settings == nil) {
            return nil;
        }
        return [settings objectForKey:settingKey];
    }
}

- (void)dealloc {
    [super dealloc];
}

@end
