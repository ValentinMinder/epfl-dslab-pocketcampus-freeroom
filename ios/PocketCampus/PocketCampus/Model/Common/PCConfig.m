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

NSString* const PC_CONFIG_CRASHLYTICS_ENABLED_KEY = @"CRASHLYTICS_ENABLED";

NSString* const PC_CONFIG_CRASHLYTICS_APIKEY_KEY = @"CRASHLYTICS_APIKEY";


NSString* const PC_USER_CONFIG_CRASHLYTICS_ENABLED_KEY = @"USER_CRASHLYTICS_ENABLED";

NSString* const PC_CONFIG_FOOD_RATINGS_ENABLED = @"FOOD_RATINGS_ENABLED";


NSString* const PC_CONFIG_LOADED_FROM_BUNDLE_KEY = @"CONFIG_LOADED_FROM_BUNDLE";

NSString* const PC_CONFIG_LOADED_FROM_PERSISTED_SERVER_CONFIG_KEY = @"CONFIG_LOADED_FROM_PERSISTED_SERVER_CONFIG";

NSString* const PC_CONFIG_LOADED_FROM_SERVER_KEY = @"CONFIG_LOADED_FROM_SERVER";

NSString* const PC_DEV_CONFIG_LOADED_FROM_APP_SUPPORT = @"CONFIG_LOADED_FROM_APP_SUPPORT";


NSInteger const PC_PROD_GAN_DISPATCH_PERIOD_SEC = 10; // The constant is used in AppDelegate when starting the GAN tracker

NSString* const PC_PROD_APP_VERSION_KEY = @"APP_VERSION";


NSString* const kPCConfigDidFinishLoadingNotification = @"kPCConfigDidFinishLoadingNotification";


//Private
static NSString* const kGetConfigURLString = @"https://pocketcampus.epfl.ch/backend/get_config.php";
static NSString* const kGetConfigPlatformParameterName = @"platform";
static NSString* const kGetConfigAppVersionParameterName = @"app_version";

static NSTimeInterval const kConfigRequestTimeoutIntervalSeconds = 3.0; //should not be too large, otherwsie slows app startup in case of bad connection

static NSString* const kPersistedServerConfigFilename = @"ConfigFromServer.plist";

static BOOL loaded = NO;

@implementation PCConfig

#pragma mark - Public methods

+ (void)loadConfigAsynchronously {
    static dispatch_once_t once;
    dispatch_once(&once, ^{
        CLSNSLog(@"-> Loading config...");
        // First load config from Config.plist
        [self registerDefaultsFromBundle];
        // Then load cached server config (from previous fetch)
        [self registerDefaultsFromPersistedServerConfigIfExists];
        // Then fetch overriding key-values from server
        [self registerAndPersistDefaultsFromServerWithCompletionHandler:^{
            // Finally load potential overriding dev config Config.plist in ApplicationSupport/<bundle_identifier>/
            [self registerDevDefaultsFromAppSupportIfExists];
            [self registerDefaultsUserConfigDefaultValuesIfNotDefined];
            [[self _defaults] synchronize]; //persist to disk
            CLSNSLog(@"-> Config loaded.");
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
        CLSNSLog(@"WARNING: tried to access [PCConfig defaults] before config was loaded. Returning nil.");
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
    CLSNSLog(@"   1. Config loaded from bundle");
}

+ (void)registerDefaultsFromPersistedServerConfigIfExists {
    NSDictionary* persistedServerConfig = [self persistedServerConfig];
    if (persistedServerConfig) {
        [[self _defaults] registerDefaults:persistedServerConfig];
        CLSNSLog(@"   2. Config loaded from persisted server config");
    } else {
        CLSNSLog(@"   2. No persisted server config");
    }
    [[self _defaults] setBool:(persistedServerConfig != nil) forKey:PC_CONFIG_LOADED_FROM_PERSISTED_SERVER_CONFIG_KEY];
}

+ (void)registerAndPersistDefaultsFromServerWithCompletionHandler:(VoidBlock)completion {
    if (![PCUtils hasDeviceInternetConnection]) {
        [[self _defaults] setBool:NO forKey:PC_CONFIG_LOADED_FROM_SERVER_KEY];
        CLSNSLog(@"   !! No internet connection. Cannot fetch config from server.");
        completion();
        return;
    }
    NSURLSessionConfiguration* config = [NSURLSessionConfiguration defaultSessionConfiguration];
    config.requestCachePolicy = NSURLRequestReloadIgnoringCacheData;
    
    AFHTTPSessionManager* manager = [[AFHTTPSessionManager alloc] initWithSessionConfiguration:config];
    manager.responseSerializer = [AFJSONResponseSerializer serializer];
    NSMutableSet* acceptableContentTypes = [NSMutableSet setWithSet:manager.responseSerializer.acceptableContentTypes];
    [acceptableContentTypes addObject:@"text/html"];
    manager.responseSerializer.acceptableContentTypes = acceptableContentTypes;
    
    NSURLSessionTask* task = [manager GET:kGetConfigURLString
      parameters:@{kGetConfigPlatformParameterName:@"ios", kGetConfigAppVersionParameterName:[PCUtils appVersion]}
         success:^(NSURLSessionDataTask *task, NSDictionary* jsonServerConfig) {
             [[self _defaults] registerDefaults:jsonServerConfig];
             [[self _defaults] setBool:YES forKey:PC_CONFIG_LOADED_FROM_SERVER_KEY];
             if ([self persistServerConfig:jsonServerConfig]) {
                 CLSNSLog(@"   3. Config loaded from server and persisted");
             } else {
                 CLSNSLog(@"   3. Config loaded from server (WARNING: could not be persisted)");
             }
             completion();
         } failure:^(NSURLSessionDataTask *task, NSError *error) {
             [[self _defaults] setBool:NO forKey:PC_CONFIG_LOADED_FROM_SERVER_KEY];
             CLSNSLog(@"   !! Error when loading config from server. Continuing with local config.");
             completion();
         }];
    
    [NSTimer scheduledTimerWithTimeInterval:kConfigRequestTimeoutIntervalSeconds block:^{
        if (task.state == NSURLSessionTaskStateRunning) {
            [task cancel];
        }
        [manager invalidateSessionCancelingTasks:NO]; //might retain cycle the manager with its session otherwise. See http://stackoverflow.com/a/24370373/1423774
    } repeats:NO];
}

+ (void)registerDevDefaultsFromAppSupportIfExists {
    NSString* pathAppSupportConfig = [NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES) lastObject];
	pathAppSupportConfig = [pathAppSupportConfig stringByAppendingPathComponent:[[NSBundle mainBundle] bundleIdentifier]];
    pathAppSupportConfig = [pathAppSupportConfig stringByAppendingPathComponent:@"Config.plist"];
    
    @try {
        NSFileManager* fileManager = [NSFileManager defaultManager];
        if ([fileManager fileExistsAtPath:pathAppSupportConfig]) {
            [[self _defaults] registerDefaults:[NSDictionary dictionaryWithContentsOfFile:pathAppSupportConfig]];
            [[self _defaults] setBool:YES forKey:PC_DEV_CONFIG_LOADED_FROM_APP_SUPPORT];
            CLSNSLog(@"   4. Detected and loaded overriding DEV config (%@)", pathAppSupportConfig);
        } else {
            [[self _defaults] setBool:NO forKey:PC_DEV_CONFIG_LOADED_FROM_APP_SUPPORT];
        }
    }
    @catch (NSException *exception) {
        [[self _defaults] setBool:NO forKey:PC_DEV_CONFIG_LOADED_FROM_APP_SUPPORT];
        CLSNSLog(@"   !! ERROR: Detected but unable to parse overriding DEV config (%@)", pathAppSupportConfig);
    }
}

+ (void)registerDefaultsUserConfigDefaultValuesIfNotDefined {
    NSUserDefaults* defaults = [self _defaults];
    if (![defaults objectForKey:PC_USER_CONFIG_CRASHLYTICS_ENABLED_KEY]) {
        [defaults setBool:YES forKey:PC_USER_CONFIG_CRASHLYTICS_ENABLED_KEY];
    }
}

#pragma mark Utilities

+ (NSString*)pathForPersistedServerConfig {
    NSString* appSupport = [NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES) lastObject];
	NSString* pcAppSupport = [appSupport stringByAppendingPathComponent:[[NSBundle mainBundle] bundleIdentifier]];
    NSString* configFilePath = [pcAppSupport stringByAppendingPathComponent:kPersistedServerConfigFilename];
    [PCPersistenceManager createComponentsForPath:configFilePath];
    return configFilePath;
}

+ (BOOL)persistServerConfig:(NSDictionary*)serverConfig {
    return [serverConfig writeToFile:[self pathForPersistedServerConfig] atomically:YES]; //avoid corruption if crash (see doc.)
}

+ (NSDictionary*)persistedServerConfig {
    return [NSDictionary dictionaryWithContentsOfFile:[self pathForPersistedServerConfig]];
}


@end
