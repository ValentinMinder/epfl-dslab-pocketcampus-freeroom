//
//  PCConfig.h
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
 */

extern NSString* const PC_CONFIG_SERVER_PROTOCOL_KEY;

extern NSString* const PC_CONFIG_SERVER_ADDRESS_KEY;

extern NSString* const PC_CONFIG_SERVER_PORT_KEY;

extern NSString* const PC_CONFIG_SERVER_URI_KEY;

extern NSString* const PC_CONFIG_ENABLED_PLUGINS_ARRAY_KEY;

extern NSString* const PC_CONFIG_ALLOW_MEALS_MULTI_VOTES_KEY;

extern NSString* const PC_CONFIG_GAN_ENABLED_KEY; //GAN is Google Analytics

extern NSString* const PC_CONFIG_GAN_TRACKING_CODE_KEY;

extern NSString* const PC_CONFIG_CRASHLYTICS_APIKEY_KEY;

/*
 * Indications on which config steps were successfull
 * Keys for booleans values stored in defaults as well
 */

extern NSString* const PC_CONFIG_LOADED_FROM_BUNDLE_KEY;

extern NSString* const PC_CONFIG_LOADED_FROM_SERVER_KEY;

extern NSString* const PC_DEV_CONFIG_LOADED_FROM_APP_SUPPORT;

/*
 * Constants
 */

extern NSInteger const PC_PROD_GAN_DISPATCH_PERIOD_SEC; // The constant is used in AppDelegate when starting the GAN tracker

extern NSString* const PC_PROD_APP_VERSION_KEY;

/*
 * This notification is posted *once* on default notifications center, when the config has finished loading
 */
extern NSString* const kPCConfigDidFinishLoadingNotification;


@interface PCConfig : NSObject

/*
 * Starts the config loading in background.
 * Posts kPCConfigDidFinishLoadingNotificationName when finished
 */
+ (void)loadConfigAsynchronously;

+ (BOOL)isLoaded;

+ (NSUserDefaults*)defaults;

@end
