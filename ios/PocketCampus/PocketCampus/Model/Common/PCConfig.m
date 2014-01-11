/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */






//  Created by Loïc Gardiol on 22.01.13.



#import "PCConfig.h"

#import "AFNetworking.h"

NSString* const PC_CONFIG_SERVER_PROTOCOL_KEY = @"SERVER_PROTOCOL";

NSString* const PC_CONFIG_SERVER_ADDRESS_KEY = @"SERVER_ADDRESS";

NSString* const PC_CONFIG_SERVER_PORT_KEY = @"SERVER_PORT";

NSString* const PC_CONFIG_SERVER_URI_KEY = @"SERVER_URI";

NSString* const PC_CONFIG_ENABLED_PLUGINS_ARRAY_KEY = @"ENABLED_PLUGINS";

NSString* const PC_CONFIG_ALLOW_MEALS_MULTI_VOTES_KEY = @"ALLOW_MEALS_MULTI_VOTES";

NSString* const PC_CONFIG_GAN_ENABLED_KEY = @"GA_ENABLED"; //GAN is Google Analytics

NSString* const PC_CONFIG_GAN_TRACKING_CODE_KEY = @"GA_TRACKING_CODE";

NSString* const PC_CONFIG_CRASHLYTICS_APIKEY_KEY = @"CRASHLYTICS_APIKEY";


NSString* const PC_CONFIG_LOADED_FROM_BUNDLE_KEY = @"CONFIG_LOADED_FROM_BUNDLE";

NSString* const PC_CONFIG_LOADED_FROM_SERVER_KEY = @"CONFIG_LOADED_FROM_SERVER";

NSString* const PC_DEV_CONFIG_LOADED_FROM_APP_SUPPORT = @"CONFIG_LOADED_FROM_APP_SUPPORT";


NSInteger const PC_PROD_GAN_DISPATCH_PERIOD_SEC = 10; // The constant is used in AppDelegate when starting the GAN tracker

NSString* const PC_PROD_APP_VERSION_KEY = @"APP_VERSION";


NSString* const kPCConfigDidFinishLoadingNotification = @"kPCConfigDidFinishLoadingNotification";


//Private
static NSString* const kGetConfigURLString = @"https://pocketcampus.epfl.ch/backend/get_config.php";
static NSString* const kGetConfigPlatformParameterName = @"platform";
static NSString* const kGetConfigAppVersionParameterName = @"app_version";

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
            [[NSNotificationCenter defaultCenter] postNotificationName:kPCConfigDidFinishLoadingNotification object:nil];
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
    [manager GET:kGetConfigURLString
      parameters:@{kGetConfigPlatformParameterName:@"ios", kGetConfigAppVersionParameterName:[PCUtils appVersion]}
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
