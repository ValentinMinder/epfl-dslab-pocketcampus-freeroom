//
//  PCConfig2.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 22.01.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "PCConfig2.h"

#import "ASIHTTPRequest.h"

#import "Reachability.h"

#import "SBJson.h"

static NSString* GET_CONFIG_URL __unused = @"http://pocketcampus.epfl.ch/backend/get_config.php";

@implementation PCConfig2

+ (void)initConfig {
    
    // First load config from Config.plist
    [self registerDefaultsFromBundle];
    
    
    // Then fetch overriding config from server
    [self registerDefaultsFromServer];
    
    // Finally load potential overriding dev config Config.plist in ApplicationSupport/<bundle_identifier>/
    [self registerDevDefaultsFromAppSupportIfExist];
    
}

+ (NSUserDefaults*)defaults {
    return [NSUserDefaults standardUserDefaults];
}

+ (void)registerDefaultsFromBundle {
    NSString* bundleConfigPath = [[NSBundle mainBundle] pathForResource:@"Config" ofType:@"plist"];
    NSDictionary* bundleConfig = [NSDictionary dictionaryWithContentsOfFile:bundleConfigPath];
    [[self defaults] registerDefaults:bundleConfig];
}

+ (void)registerDefaultsFromServer {
    if (![[Reachability reachabilityForInternetConnection] isReachable]) {
        NSLog(@"-> No internet connection. Cannot fetch config from server.");
        return;
    }
    NSString* app_version = [[[NSBundle mainBundle] infoDictionary] objectForKey:(NSString*)kCFBundleVersionKey];
    NSString* getConfigWithParamsURLString = [GET_CONFIG_URL stringByAppendingFormat:@"?platform=ios&app_version=%@", app_version];
    ASIHTTPRequest* request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:getConfigWithParamsURLString]];
    request.timeOutSeconds = 3; //must NOT delay app start time too much if server is not reachable
    request.cachePolicy = ASIAskServerIfModifiedCachePolicy;
    [request startSynchronous];
    
    if (request.error) {
        NSLog(@"-> Error when loading config from server. Continuing with local config.");
    } else {
        NSDictionary* config = nil;
        @try {
            config = [request.responseString JSONValue];
            [[self defaults] registerDefaults:config];
            NSLog(@"-> Config successfully loaded from server and registered in defaults");
        }
        @catch (NSException *exception) {
            NSLog(@"-> Error when parsing config received from server");
        }
    }
}

+ (void)registerDevDefaultsFromAppSupportIfExist {
    NSString* pathAppSupportConfig = [NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES) lastObject];
	pathAppSupportConfig = [pathAppSupportConfig stringByAppendingPathComponent:[[NSBundle mainBundle] bundleIdentifier]];
    pathAppSupportConfig = [pathAppSupportConfig stringByAppendingPathComponent:@"Config.plist"];
    
    NSFileManager* fileManager = [NSFileManager defaultManager];
    if ([fileManager fileExistsAtPath:pathAppSupportConfig]) {
        [[self defaults] registerDefaults:[NSDictionary dictionaryWithContentsOfFile:pathAppSupportConfig]];
        NSLog(@"-> Overriding Config defaults initialized from %@", pathAppSupportConfig);
    }
}


@end
