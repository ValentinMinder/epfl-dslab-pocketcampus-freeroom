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

static TransportController* instance __weak = nil;

static BOOL settingsAreDirty = NO;
static NSMutableDictionary* settings = nil;

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"TransportController cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super init];
        if (self) {
            NextDeparturesListViewController* nextDeparturesListViewController = [[NextDeparturesListViewController alloc] init];
            nextDeparturesListViewController.title = [[self class] localizedName];
            PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:nextDeparturesListViewController];
            navController.pluginIdentifier = [[self class] identifierName];
            self.mainNavigationController = navController;
            instance = self;
        }
        return self;
    }
}

+ (id)sharedInstanceToRetain {
    @synchronized (self) {
        if (instance) {
            return instance;
        }
#if __has_feature(objc_arc)
        return [[[self class] alloc] init];
#else
        return [[[[self class] alloc] init] autorelease];
#endif
    }
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"TransportPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Transport";
}

+ (BOOL)saveObjectSetting:(NSObject<NSCoding>*)val forKey:(NSString*)settingKey {
    @synchronized(self) {
        if (settings == nil) {
            settings = [NSMutableDictionary dictionary];
        }
        [settings setObject:val forKey:settingKey];
        settingsAreDirty = YES;
        return [ObjectArchiver saveObject:settings forKey:kSettingsKey andPluginName:@"transport"];
    }
}

+ (id<NSCoding>)objectSettingForKey:(NSString*)settingKey {
    @synchronized(self) {
        if (settings == nil || settingsAreDirty) {
            settings = (NSMutableDictionary*)[ObjectArchiver objectForKey:kSettingsKey andPluginName:@"transport"];
            settingsAreDirty = NO;
        }
        if (settings == nil) {
            return nil;
        }
        return [settings objectForKey:settingKey];
    }
}

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}

@end
