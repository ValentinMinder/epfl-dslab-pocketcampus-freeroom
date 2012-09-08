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

- (id)initWithMainController:(MainController*)mainController_ {
    self = [super init];
    if (self) {
        mainController = mainController_;
        NextDeparturesListViewController* viewController = [[NextDeparturesListViewController alloc] init];
        viewController.title = [[self class] localizedName];
        mainViewController = viewController;
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
    if (mainViewController == nil || ![mainViewController isKindOfClass:[NextDeparturesListViewController class]] || mainViewController.navigationController.visibleViewController != mainViewController) {
        return;
    }
    [(NextDeparturesListViewController*)mainViewController refresh];
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
