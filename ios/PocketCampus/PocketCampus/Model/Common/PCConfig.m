//
//  PCConfig.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 22.01.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "PCConfig.h"

#import "AFNetworking.h"

static NSString* const GET_CONFIG_URL __unused = @"https://pocketcampus.epfl.ch/backend/get_config.php";
static NSString* const GET_CONFIG_PLATFORM_PARAMETER_NAME __unused = @"platform";
static NSString* const GET_CONFIG_APP_VERSION_PARAMETER_NAME __unused = @"app_version";

static BOOL loaded = NO;

@implementation PCConfig

#pragma mark - Public methods

+ (void)loadConfigAsynchronously {
    static dispatch_once_t once;
    dispatch_once(&once, ^{
        NSLog(@"-> Loading config...");
        // First load config from Config.plist
        [self registerDefaultsFromBundle];
        // Then fetch overriding key-values from server
        [self registerDefaultsFromServerWithCompletionHandler:^{
            // Finally load potential overriding dev config Config.plist in ApplicationSupport/<bundle_identifier>/
            [self registerDevDefaultsFromAppSupportIfExist];
            [[self _defaults] synchronize]; //persist to disk
            NSLog(@"-> Config loaded.");
            loaded = YES;
            [[NSNotificationCenter defaultCenter] postNotificationName:kPCConfigDidFinishLoadingNotificationName object:nil];
        }];
    });
}

+ (BOOL)isLoaded {
    return loaded;
}

+ (NSUserDefaults*)defaults {
    if (!loaded) {
        NSLog(@"WARNING: tried to access [PCConfig defaults] before config was loaded. Returning nil.");
        [NSException raise:@"Illegal access" format:nil];
        return nil;
    }
    return [self _defaults];
}

#pragma mark - Private methods

+ (NSUserDefaults*)_defaults {
    return [NSUserDefaults standardUserDefaults];
}

+ (void)registerDefaultsFromBundle {
    NSString* bundleConfigPath = [[NSBundle mainBundle] pathForResource:@"Config" ofType:@"plist"];
    NSDictionary* bundleConfig = [NSDictionary dictionaryWithContentsOfFile:bundleConfigPath];
    if (!bundleConfig) {
        @throw [NSException exceptionWithName:@"File error" reason:@"Bundle Config.plist could not be loaded" userInfo:nil];
    }
    [[self _defaults] registerDefaults:bundleConfig];
    [[self _defaults] setBool:YES forKey:PC_CONFIG_LOADED_FROM_BUNDLE_KEY];
    NSLog(@"   1. Config loaded from bundle");
}

+ (void)registerDefaultsFromServerWithCompletionHandler:(VoidBlock)completion {
    if ([[AFNetworkReachabilityManager sharedManager] isReachable]) {
        [[self _defaults] setBool:NO forKey:PC_CONFIG_LOADED_FROM_SERVER_KEY];
        NSLog(@"   !! No internet connection. Cannot fetch config from server.");
    }
    NSURLSessionConfiguration* config = [NSURLSessionConfiguration defaultSessionConfiguration];
    config.requestCachePolicy = NSURLRequestReloadIgnoringCacheData;
    config.timeoutIntervalForRequest = 3.0; //must NOT delay app start time too much if server is not reachable
    
    AFHTTPSessionManager* manager = [[AFHTTPSessionManager alloc] initWithSessionConfiguration:config];
    manager.responseSerializer = [AFJSONResponseSerializer serializer];
    NSMutableSet* acceptableContentTypes = [NSMutableSet setWithSet:manager.responseSerializer.acceptableContentTypes];
    [acceptableContentTypes addObject:@"text/html"];
    manager.responseSerializer.acceptableContentTypes = acceptableContentTypes;
    [manager GET:GET_CONFIG_URL
      parameters:@{GET_CONFIG_PLATFORM_PARAMETER_NAME:@"ios", GET_CONFIG_APP_VERSION_PARAMETER_NAME:[PCUtils appVersion]}
         success:^(NSURLSessionDataTask *task, NSDictionary* jsonServerConfig) {
             [[self _defaults] registerDefaults:jsonServerConfig];
             [[self _defaults] setBool:YES forKey:PC_CONFIG_LOADED_FROM_SERVER_KEY];
             NSLog(@"   2. Config loaded from server");
             completion();
         } failure:^(NSURLSessionDataTask *task, NSError *error) {
             [[self _defaults] setBool:NO forKey:PC_CONFIG_LOADED_FROM_SERVER_KEY];
             NSLog(@"   !! Error when loading config from server. Continuing with local config.");
             completion();
         }];
}

+ (void)registerDevDefaultsFromAppSupportIfExist {
    NSString* pathAppSupportConfig = [NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES) lastObject];
	pathAppSupportConfig = [pathAppSupportConfig stringByAppendingPathComponent:[[NSBundle mainBundle] bundleIdentifier]];
    pathAppSupportConfig = [pathAppSupportConfig stringByAppendingPathComponent:@"Config.plist"];
    
    @try {
        NSFileManager* fileManager = [NSFileManager defaultManager];
        if ([fileManager fileExistsAtPath:pathAppSupportConfig]) {
            [[self _defaults] registerDefaults:[NSDictionary dictionaryWithContentsOfFile:pathAppSupportConfig]];
            [[self _defaults] setBool:YES forKey:PC_DEV_CONFIG_LOADED_FROM_APP_SUPPORT];
            NSLog(@"   3. Detected and loaded overriding DEV config (%@)", pathAppSupportConfig);
        } else {
            [[self _defaults] setBool:NO forKey:PC_DEV_CONFIG_LOADED_FROM_APP_SUPPORT];
        }
    }
    @catch (NSException *exception) {
        [[self _defaults] setBool:NO forKey:PC_DEV_CONFIG_LOADED_FROM_APP_SUPPORT];
        NSLog(@"   !! ERROR: Detected but unable to parse overriding DEV config (%@)", pathAppSupportConfig);
    }
}


@end
