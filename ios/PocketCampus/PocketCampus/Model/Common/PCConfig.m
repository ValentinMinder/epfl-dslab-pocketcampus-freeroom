//
//  PCConfig.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 22.01.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "PCConfig.h"

#import "ASIHTTPRequest.h"

#import "Reachability.h"

#import "SBJson.h"

static NSString* GET_CONFIG_URL __unused = @"http://pocketcampus.epfl.ch/backend/get_config.php";

@implementation PCConfig

+ (void)initConfig {
    
    NSLog(@"-> Loading config...");
    // First load config from Config.plist
    [self registerDefaultsFromBundle];
    
    // Then fetch overriding key-values from server
    [self registerDefaultsFromServer];
    
    // Finally load potential overriding dev config Config.plist in ApplicationSupport/<bundle_identifier>/
    [self registerDevDefaultsFromAppSupportIfExist];
    
    [[self defaults] synchronize]; //persist to disk
    NSLog(@"-> Config loaded.");
}

+ (NSUserDefaults*)defaults {
    return [NSUserDefaults standardUserDefaults];
}

+ (void)registerDefaultsFromBundle {
    NSString* bundleConfigPath = [[NSBundle mainBundle] pathForResource:@"Config" ofType:@"plist"];
    NSDictionary* bundleConfig = [NSDictionary dictionaryWithContentsOfFile:bundleConfigPath];
    if (!bundleConfig) {
        @throw [NSException exceptionWithName:@"File error" reason:@"Bundle Config.plist could not be loaded" userInfo:nil];
    }
    [[self defaults] registerDefaults:bundleConfig];
    [[self defaults] setBool:YES forKey:PC_CONFIG_LOADED_FROM_BUNDLE_KEY];
    NSLog(@"   1. Config loaded from bundle");
}

+ (void)registerDefaultsFromServer {
    if (![[Reachability reachabilityForInternetConnection] isReachable]) {
        [[self defaults] setBool:NO forKey:PC_CONFIG_LOADED_FROM_SERVER_KEY];
        NSLog(@"   !! No internet connection. Cannot fetch config from server.");
        return;
    }
    NSString* app_version = [[[NSBundle mainBundle] infoDictionary] objectForKey:(NSString*)kCFBundleVersionKey];
    NSString* getConfigWithParamsURLString = [GET_CONFIG_URL stringByAppendingFormat:@"?platform=ios&app_version=%@", app_version];
    ASIHTTPRequest* request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:getConfigWithParamsURLString]];
    request.timeOutSeconds = 3; //must NOT delay app start time too much if server is not reachable
    request.cachePolicy = ASIAskServerIfModifiedCachePolicy;
    [request startSynchronous];
    
    if (request.error) {
        [[self defaults] setBool:NO forKey:PC_CONFIG_LOADED_FROM_SERVER_KEY];
        NSLog(@"   !! Error when loading config from server. Continuing with local config.");
    } else {
        NSDictionary* config = nil;
        @try {
            config = [request.responseString JSONValue];
            [[self defaults] registerDefaults:config];
            [[self defaults] setBool:YES forKey:PC_CONFIG_LOADED_FROM_SERVER_KEY];
            NSLog(@"   2. Config loaded from server");
        }
        @catch (NSException *exception) {
            [[self defaults] setBool:NO forKey:PC_CONFIG_LOADED_FROM_SERVER_KEY];
            NSLog(@"   !! Error when parsing config received from server");
        }
    }
}

+ (void)registerDevDefaultsFromAppSupportIfExist {
    NSString* pathAppSupportConfig = [NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES) lastObject];
	pathAppSupportConfig = [pathAppSupportConfig stringByAppendingPathComponent:[[NSBundle mainBundle] bundleIdentifier]];
    pathAppSupportConfig = [pathAppSupportConfig stringByAppendingPathComponent:@"Config.plist"];
    
    @try {
        NSFileManager* fileManager = [NSFileManager defaultManager];
        if ([fileManager fileExistsAtPath:pathAppSupportConfig]) {
            [[self defaults] registerDefaults:[NSDictionary dictionaryWithContentsOfFile:pathAppSupportConfig]];
            [[self defaults] setBool:YES forKey:PC_DEV_CONFIG_LOADED_FROM_APP_SUPPORT];
            NSLog(@"   3. Detected and loaded overriding DEV config (%@)", pathAppSupportConfig);
        } else {
            [[self defaults] setBool:NO forKey:PC_DEV_CONFIG_LOADED_FROM_APP_SUPPORT];
        }
    }
    @catch (NSException *exception) {
        [[self defaults] setBool:NO forKey:PC_DEV_CONFIG_LOADED_FROM_APP_SUPPORT];
        NSLog(@"   !! ERROR: Detected but unable to parse overriding DEV config (%@)", pathAppSupportConfig);
    }
}


@end
