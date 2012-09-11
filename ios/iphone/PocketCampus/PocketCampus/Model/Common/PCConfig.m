//
//  PCConfig.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 03.09.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PCConfig.h"

#import "ASIHTTPRequest.h"

#import "Reachability.h"

#import "SBJson.h"

@implementation PCConfig

+ (void)initConfig {
   
    NSString* path = [NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES) lastObject];
	path = [path stringByAppendingPathComponent:[[NSBundle mainBundle] bundleIdentifier]];
    path = [path stringByAppendingPathComponent:@"Config.plist"];
    
    NSFileManager* fileManager = [NSFileManager defaultManager];
    if ([fileManager fileExistsAtPath:path]) { //check if a Config.plist is present in ApplicationSupport/<bundle_identifier>/
        [[self defaults] registerDefaults:[NSDictionary dictionaryWithContentsOfFile:path]];
        [[self defaults] setObject:PC_CONFIG_TYPE_FROM_APPLICATION_SUPPORT forKey:PC_CONFIG_TYPE_KEY];
        NSLog(@"-> Overriding Config defaults initialized from %@", path);
        return;
    }
    
    if (![[Reachability reachabilityForInternetConnection] isReachable]) {
        /* No connection => use bundle Config.plist */
        [self registerDefaultsFromBundle];
        [[self defaults] setObject:PC_CONFIG_TYPE_FROM_BUNDLE forKey:PC_CONFIG_TYPE_KEY];
        NSLog(@"-> No internet connection : Config defaults initialized from bundle");
        return;
    }
    
    
    /* Get config from server */
    
    NSString* app_version = [[[NSBundle mainBundle] infoDictionary] objectForKey:(NSString*)kCFBundleVersionKey];
    NSString* getConfigWithParamsURLString = [GET_CONFIG_URL stringByAppendingFormat:@"?platform=ios&app_version=%@", app_version];
    
    ASIHTTPRequest* request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:getConfigWithParamsURLString]];
    request.timeOutSeconds = 3; //must NOT delay app start time too much if server is not reachable
    request.cachePolicy = ASIAskServerIfModifiedCachePolicy;
    
    [request startSynchronous];

    if (request.error) {
        [self registerDefaultsFromBundle];
        NSLog(@"-> Error when loading config from server. Config loaded from bundle");
        return;
    }
    
    NSDictionary* config = nil;
    @try {
        config = [request.responseString JSONValue];
        [[self defaults] registerDefaults:config];
        [[self defaults] setObject:PC_CONFIG_TYPE_FROM_SERVER forKey:PC_CONFIG_TYPE_KEY];
        NSLog(@"-> Config successfully loaded from server and registered in defaults");
    }
    @catch (NSException *exception) {
        [self registerDefaultsFromBundle];
        [[self defaults] setObject:PC_CONFIG_TYPE_FROM_BUNDLE forKey:PC_CONFIG_TYPE_KEY];
        NSLog(@"-> Error when parsing config received from server. Config loaded from bundle");
    }
    
}

+ (void)registerDefaultsFromBundle {
    NSString* bundleConfigPath = [[NSBundle mainBundle] pathForResource:@"Config" ofType:@"plist"];
    NSDictionary* bundleConfig = [NSDictionary dictionaryWithContentsOfFile:bundleConfigPath];
    [[self defaults] registerDefaults:bundleConfig];
}

+ (NSUserDefaults*)defaults {
    return [NSUserDefaults standardUserDefaults];
}

@end
