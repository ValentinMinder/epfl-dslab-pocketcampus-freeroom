//
//  AppDelegate.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "AppDelegate.h"

#import "PCConfig.h"

#import "PCUtils.h"

#import "PCValues.h"

#import "GANTracker.h"

#import "MyEduServiceTests.h"

#import "PushNotifControllerTests.h"

static id test __strong __unused = nil;

@implementation AppDelegate

@synthesize window = _window;

- (void)dealloc
{
    [[GANTracker sharedTracker] stopTracker];
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    /* Apply appearence proxy => specified UI elements will defaut to PC defined look&feel, eg. red navigation bar */
    [PCValues applyAppearenceProxy];
    
    /* Initialize defaults with PC config */
    [PCConfig initConfig];
    
    /* Start Google Analytics tracker if enabled in config */
    if ([[PCConfig defaults] boolForKey:PC_CONFIG_GAN_ENABLED_KEY]) {
        NSLog(@"-> Starting Google Analytics tracker");
        NSString* ganId = (NSString*)[[PCConfig defaults] objectForKey:PC_CONFIG_GAN_TRACKING_CODE_KEY];
        if (ganId) {
            [[GANTracker sharedTracker] startTrackerWithAccountID:ganId dispatchPeriod:10 delegate:self];
        } else {
            NSLog(@"!! ERROR: could not start Google Analytics tracker because tracking code is absent from config.");
        }
        
    } else {
        NSLog(@"-> Google Analytics disabled (config)");
    }
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    // Override point for customization after application launch.
    self.window.backgroundColor = [UIColor blackColor];
    
    self.mainController = [[MainController alloc] initWithWindow:self.window];
    
    
    /* TESTS */
    
    //test = [[PushNotifControllerTests alloc] init];
    //[test testRegistrationAuthenticated];

    
    //[[[PocketCampusLogicTests alloc] init] testAll];
    
    //[[[DirectoryServiceTests alloc] init] tempTest];
    
    //[[[MapServiceTests alloc] init] tempTest];
    
    //[[[MyEduServiceTests alloc] init] tempTest];
    
    /* END OF TESTS */
    
    [self.window makeKeyAndVisible];
    
    /* App might have been opened by notification touch */
    NSDictionary* userInfo = [launchOptions objectForKey:UIApplicationLaunchOptionsRemoteNotificationKey];
    if (userInfo) {
        [self application:[UIApplication sharedApplication] didReceiveRemoteNotification:userInfo];
    }
    
    //test
    //[self application:[UIApplication sharedApplication] didReceiveRemoteNotification:[NSDictionary dictionaryWithObject:@"myedu" forKey:@"pluginName"]];

    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    //[[NSUserDefaults standardUserDefaults] setBool:NO forKey:@"PC_DEV_MODE"];
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

- (void)applicationDidReceiveMemoryWarning:(UIApplication *)application {
    [self.mainController appDidReceiveMemoryWarning];
}

- (NSUInteger)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window {
    return [window.rootViewController supportedInterfaceOrientations];
}

- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    NSString* deviceTokenString = [[[deviceToken description] stringByTrimmingCharactersInSet:[NSCharacterSet characterSetWithCharactersInString:@"<>"]] stringByReplacingOccurrencesOfString:@" " withString:@""];
    NSNotification* notif = [NSNotification notificationWithName:AppDidSucceedToRegisterToNotifications object:nil userInfo:[NSDictionary dictionaryWithObject:deviceTokenString forKey:kPushDeviceTokenStringKey]];
    [[NSNotificationCenter defaultCenter] postNotification:notif];
}

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    [[NSNotificationCenter defaultCenter] postNotificationName:AppDidFailToRegisterToNotifications object:nil];
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
    NSString* pluginName = userInfo[@"pluginName"];
    NSString* message = userInfo[@"aps"][@"alert"];
    NSLog(@"-> Notification received for plugin %@: %@", pluginName, message);
    [[NSNotificationCenter defaultCenter] postNotificationName:[self.class nsNotificationNameForPluginLowerIdentifier:pluginName] object:nil userInfo:userInfo];
}

+ (NSString*)nsNotificationNameForPluginLowerIdentifier:(NSString*)pluginLowerIdentifier {
    return [NSString stringWithFormat:@"%@_%@", RemoteNotifForPluginName, pluginLowerIdentifier];
}

/* Google Analytics Delegation */

- (void)trackerDispatchDidComplete:(GANTracker *)tracker eventsDispatched:(NSUInteger)eventsDispatched eventsFailedDispatch:(NSUInteger)eventsFailedDispatch {
    NSLog(@"-> Google Analytics Dispatch: succeeded:%i, failed:%i",eventsDispatched,eventsFailedDispatch);
}

- (void)hitDispatched:(NSString *)hitString {
    //NSLog(@"Google Analytics hitDispatched: %@",hitString);
}

@end
