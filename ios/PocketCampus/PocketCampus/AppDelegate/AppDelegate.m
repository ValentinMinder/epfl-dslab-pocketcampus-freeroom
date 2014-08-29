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

//  Created by Loïc Gardiol on 28.02.12.

#import "AppDelegate.h"

#import "PCURLSchemeHandler.h"

#import <Crashlytics/Crashlytics.h>

#import "AFNetworkReachabilityManager.h"

NSString* const kAppDelegateAppDidSucceedToRegisterForRemoteNotificationsNotification = @"AppDelegateAppDidSucceedToRegisterForRemoteNotificationsNotification";
NSString* const kAppDelegatePushDeviceTokenStringUserInfoKey = @"AppDelegatePushDeviceTokenStringUserInfoKey";
NSString* const kAppDelegateAppFailedToRegisterForRemoteNotificationsNotification = @"AppDelegateAppFailedToRegisterForRemoteNotificationsNotification";
NSString* const kAppDelegateAppDidRegisterUserNotificationSettingsNotification = @"AppDelegateAppDidRegisterUserNotificationSettingsNotification";

static id test __strong __unused = nil;

static NSString* const kAppDidReceiveRemoteNotificationForPlugin = @"AppDidReceiveRemoteNotificationForPlugin";

@interface AppDelegate ()

@property (nonatomic, strong) MainController* mainController;

@property (nonatomic, readwrite, strong) CTTelephonyNetworkInfo* telephonyInfo;

@end

@implementation AppDelegate

@synthesize window = _window;

#pragma mark - UIApplicationDelegate

- (BOOL)application:(UIApplication *)application willFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{

#if TARGET_IPHONE_SIMULATOR
    NSLog(@"Application Support: %@", [[[NSFileManager defaultManager] URLsForDirectory:NSApplicationSupportDirectory inDomains:NSUserDomainMask] lastObject]);
#endif
    [PCPersistenceManager migrateDataOnceToSharedAppGroupPersistence];
    
    // So that [[UIDevice currentDevice] orientation] returns a correct value (see doc)
    [[UIDevice currentDevice] beginGeneratingDeviceOrientationNotifications];
    
    // Need to start monitoring, otherwise sharedManager.networkReachabilityStatus is wrong
    // Bug in AFNetworkReachabilityManager ?
    [[AFNetworkReachabilityManager sharedManager] startMonitoring];
    
    // Need to be initialized so that classes can subscribe to CTRadioAccessTechnologyDidChangeNotification
    self.telephonyInfo = [CTTelephonyNetworkInfo new];
    
    // Load PocketCampus configuration (will populate [PCConfig defaults])
    [PCConfig loadConfigAsynchronously];

    // Apply appearence proxy => specified UI elements will defaut to PC defined look&feel
    [PCValues applyAppearenceProxy];
    
    // Initialize shared NSURLCache that will be used as default cache for all requests by default
    NSURLCache* cache = [[NSURLCache alloc] initWithMemoryCapacity:4*1024*1024 diskCapacity:100*1024*1024 diskPath:nil];
    [NSURLCache setSharedURLCache:cache];

    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.window.backgroundColor = [UIColor blackColor];
    
    self.mainController = [[MainController alloc] initWithWindow:self.window];
    
    [self.window makeKeyAndVisible];
    
    // App might have been opened by tapping a notification
    NSDictionary* userInfo = launchOptions[UIApplicationLaunchOptionsRemoteNotificationKey];
    if (userInfo) {
        [self application:[UIApplication sharedApplication] didReceiveRemoteNotification:userInfo];
    }

    return YES;
}

- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation {
    return [self.mainController handlePocketCampusURL:url];
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
    CLSLog(@"ApplicationWillResignActive");
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    //[[NSUserDefaults standardUserDefaults] setBool:NO forKey:@"PC_DEV_MODE"];
    CLSLog(@"ApplicationDidEnterBackground");
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
    CLSLog(@"ApplicationWillEnterForeground");
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    CLSLog(@"ApplicationDidBecomeActive");
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    CLSLog(@"ApplicationWillTerminate");
}

- (NSUInteger)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window {
    return [window.rootViewController supportedInterfaceOrientations];
}

- (void)application:(UIApplication *)application didRegisterUserNotificationSettings:(UIUserNotificationSettings *)notificationSettings {
    [[NSNotificationCenter defaultCenter] postNotificationName:kAppDelegateAppDidRegisterUserNotificationSettingsNotification object:self];
}

- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    NSString* deviceTokenString = [[[deviceToken description] stringByTrimmingCharactersInSet:[NSCharacterSet characterSetWithCharactersInString:@"<>"]] stringByReplacingOccurrencesOfString:@" " withString:@""];
    NSNotification* notif = [NSNotification notificationWithName:kAppDelegateAppDidSucceedToRegisterForRemoteNotificationsNotification object:self userInfo:@{kAppDelegatePushDeviceTokenStringUserInfoKey:deviceTokenString}];
    [[NSNotificationCenter defaultCenter] postNotification:notif];
}

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    [[NSNotificationCenter defaultCenter] postNotificationName:kAppDelegateAppFailedToRegisterForRemoteNotificationsNotification object:self];
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
    NSString* pluginName = userInfo[@"pluginName"];
    NSString* message = userInfo[@"aps"][@"alert"];
    CLSNSLog(@"-> Notification received for plugin '%@': %@  (userInfo:%@)", pluginName, message, userInfo);
    [[NSNotificationCenter defaultCenter] postNotificationName:[self.class nsNotificationNameForPluginLowerIdentifier:[pluginName lowercaseString]] object:self userInfo:userInfo];
}

#pragma mark - Public

+ (NSString*)nsNotificationNameForPluginLowerIdentifier:(NSString*)pluginLowerIdentifier {
    return [NSString stringWithFormat:@"%@_%@", kAppDidReceiveRemoteNotificationForPlugin, pluginLowerIdentifier];
}

@end
