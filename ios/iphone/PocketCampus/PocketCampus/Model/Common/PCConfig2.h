//
//  PCConfig2.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 22.01.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>


/*
 * Note on PocketCampus configation
 *
 * Configuration is of the form key-value.
 *
 * Values are populated into the defaults [PCConfig defaults] in the follow order of *overriding* priority (i overrides i+1):
 *
 * 1) Key-values found in "Application Support/<bundle_identifier>/Config.plist" (for DEV purposes)
 * 2) Fetched from server with address http://pocketcampus.epfl.ch/backend/get_config.php
 * 3) Key-values found in bundle file Config.plist
 *
 * First, keys from Config.plist are loaded, then any key-value pair returned by the server
 * overrides those and finally the ones in Application Support override those again.
 */


/*
 * Keys of [PCConfig defaults]
 * Those are the keys that must be used in Config.plist (see above)
 * IMPORTANT: __unused compiler flags are put so suppress unused warning. Do not remove variables.
 */

static NSString* PC_CONFIG_SERVER_PROTOCOL_KEY __unused = @"SERVER_PROTOCOL";

static NSString* PC_CONFIG_SERVER_ADDRESS_KEY __unused = @"SERVER_ADDRESS";

static NSString* PC_CONFIG_SERVER_PORT_KEY __unused = @"SERVER_PORT";

static NSString* PC_CONFIG_SERVER_URI_KEY __unused = @"SERVER_URI";

static NSString* PC_CONFIG_ENABLED_PLUGINS_ARRAY_KEY __unused = @"ENABLED_PLUGINS";

static NSString* PC_CONFIG_ALLOW_MEALS_MULTI_VOTES_KEY __unused = @"ALLOW_MEALS_MULTI_VOTES";

static NSString* PC_GAN_ENABLED_KEY __unused = @"GAN_ENABLED"; //GAN is Google Analytics

static NSString* PC_GAN_TRACKING_CODE_KEY __unused = @"GA_TRACKING_CODE";


/*
 * CONSTANTS
 */

static NSInteger PC_PROD_GAN_DISPATCH_PERIOD_SEC __unused = 10; // The constant is used in AppDelegate when starting the GAN tracker


@interface PCConfig2 : NSObject

+ (void)initConfig;

+ (NSUserDefaults*)defaults;

@end
