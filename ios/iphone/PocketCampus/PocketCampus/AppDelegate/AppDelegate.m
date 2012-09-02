//
//  AppDelegate.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "AppDelegate.h"

#import "DirectoryServiceTests.h"

#import "MapServiceTests.h"

#import "GANTracker.h"

static const NSInteger kGANDispatchPeriodSec = 10;
//static NSString* const kAnalyticsAccountId = @"UA-22135241-3";

@implementation AppDelegate

@synthesize window = _window, mainController;

- (void)dealloc
{
    [[GANTracker sharedTracker] stopTracker];
    [mainController release];
    [_window release];
    [super dealloc];
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    
    [self startGANTracker];
    
    self.window = [[[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]] autorelease];
    // Override point for customization after application launch.
    self.window.backgroundColor = [UIColor blackColor];
    [application setStatusBarStyle:UIStatusBarStyleDefault animated:YES];
    self.mainController = [[[MainController alloc] initWithWindow:self.window] autorelease];

    
    /*END TEST*/
    
    /* OFFICIAL TESTS */
    
    //[[[PocketCampusLogicTests alloc] init] testAll];
    
    //[[[DirectoryServiceTests alloc] init] tempTest];
    
    //[[[MapServiceTests alloc] init] tempTest];
    
    /* END OF OFFICAL TESTS */
    
    [self.window makeKeyAndVisible];

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
    [self.mainController refreshDisplayedPlugin];
    
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    [[NSUserDefaults standardUserDefaults] setBool:NO forKey:@"PC_DEV_MODE"];
}

/* Google Analytics init */

- (void)startGANTracker {
    NSDictionary* config = [NSDictionary dictionaryWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"Config" ofType:@"plist"]];
    NSString* kAnalyticsAccountId = nil;
    if ([[config objectForKey:@"DEV_MODE"] isEqual:[NSNumber numberWithBool:YES]]) {
        kAnalyticsAccountId = [config objectForKey:@"DEV_ANALYTICS_TRACKINGCODE"];
    } else {
        kAnalyticsAccountId = [config objectForKey:@"PROD_ANALYTICS_TRACKINGCODE"];
    }
    [[GANTracker sharedTracker] startTrackerWithAccountID:kAnalyticsAccountId
                                           dispatchPeriod:kGANDispatchPeriodSec
                                                 delegate:self];

}

/* Google Analytics Delegation */

- (void)trackerDispatchDidComplete:(GANTracker *)tracker eventsDispatched:(NSUInteger)eventsDispatched eventsFailedDispatch:(NSUInteger)eventsFailedDispatch {
    NSLog(@"-> Google Analytics Dispatch: succeeded:%i, failed:%i",eventsDispatched,eventsFailedDispatch);
}

- (void)hitDispatched:(NSString *)hitString {
    //NSLog(@"Google Analytics hitDispatched: %@",hitString);
}

@end
