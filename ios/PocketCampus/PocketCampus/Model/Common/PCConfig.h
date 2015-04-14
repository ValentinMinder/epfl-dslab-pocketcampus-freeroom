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

@import Foundation;

/*
 * PocketCampus configuration
 *
 * Configuration is of the form key-value.
 *
 * Values are populated into the defaults [PCConfig defaults] in the follow order of *overriding* priority (i overrides i+1):
 *
 * 1. Key-values found in "Application Support/<bundle_identifier>/Config.plist" (for DEV purposes)
 * 2. Key-values fetched from server with address http://pocketcampus.epfl.ch/backend/get_config.php
 * 3. Server key-values persisted from previous fetch (2.) (persisted in Application Support/<bundle_identifier>/ConfigFromServer.plist)
 * 4. Key-values found in bundle file Config.plist
 *
 * First, keys from Config.plist are loaded, then from cached ConfigFromServer.plist overrides then,
 * then any key-value pair returned by the server overrides those and finally the ones in Application Support override those again.
 */


/*
 * Keys of [PCConfig defaults] that should NOT be modified by user
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

extern NSString* const PC_CONFIG_CRASHLYTICS_ENABLED_KEY;

extern NSString* const PC_CONFIG_CRASHLYTICS_APIKEY_KEY;

extern NSString* const PC_CONFIG_FOOD_RATINGS_ENABLED;

extern NSString* const PC_CONFIG_CLOUDPRINT_ENABLED;


/*
 * Keys of [PCConfig defaults] that can be modified by user ("settings")
 */

extern NSString* const PC_USER_CONFIG_CRASHLYTICS_ENABLED_KEY; //Default: YES

/*
 * Indications on which config steps were successfull
 * Keys for booleans values stored in defaults as well
 */

extern NSString* const PC_CONFIG_LOADED_FROM_BUNDLE_KEY; //4.

extern NSString* const PC_CONFIG_LOADED_FROM_PERSISTED_SERVER_CONFIG_KEY; //3.

extern NSString* const PC_CONFIG_LOADED_FROM_SERVER_KEY; //2.

extern NSString* const PC_DEV_CONFIG_LOADED_FROM_APP_SUPPORT; //1.

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

/**
 * Starts the config loading in background.
 * Posts kPCConfigDidFinishLoadingNotificationName when finished
 * This method is thread-safe and will run once only.
 */
+ (void)loadConfigAsynchronously;

/**
 * @return YES if [self loadConfigAsynchronously] has finished, NO otherwise
 */
+ (BOOL)isLoaded;

+ (NSUserDefaults*)defaults;


/**
 * DEBUG
 */

#ifdef DEBUG

/**
 * @return array of config file paths that can be pass to
 */
+ (NSArray*)bundledDebugConfigsPaths;

/**
 * Prepares config persistence so that at next app lauch the config
 * described by configPath is loaded.
 * WARNING: the app is killed after preparing for the new config.
 * @param configPath the path the config. Can be nil if you want NO custom config.
 */
+ (void)applyAndDieConfigWithPath:(NSString*)configPath;

#endif

@end
