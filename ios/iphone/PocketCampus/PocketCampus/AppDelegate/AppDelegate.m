//
//  AppDelegate.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "AppDelegate.h"

#import "FoodService.h"

#import "food.h"

#import "TransportService.h"

#import "transport.h"

#import "TransportUtils.h"

#import "MapService.h"

#import "NewsService.h"

#import "AuthenticationController.h"

#import "AuthenticationService.h"

#import "CamiproService.h"



#import "PocketCampusLogicTests.h"

@implementation AppDelegate

@synthesize window = _window, mainController;

- (void)dealloc
{
    [self.mainController release];
    [_window release];
    [super dealloc];
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    self.window = [[[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]] autorelease];
    // Override point for customization after application launch.
    self.window.backgroundColor = [UIColor blackColor];
    
    self.mainController = [[MainController alloc] initWithWindow:self.window];

    /*TEST*/
    
    //FoodService* service = [[FoodService sharedInstanceToRetain] retain];
    
    //TransportService* service = [[TransportService sharedInstanceToRetain] retain];
    
    //[service autocomplete:@"lau" delegate:self];
    
    //[service getLocationsForNames:[NSArray arrayWithObjects:@"EPFL", @"Lausanne-Flon", nil] delegate:self];
    
    //[service getTripsFrom:@"EPFL" to:@"Lausanne-Flon" delegate:self];
    
    //[service getTripsFrom:@"EPFL" to:@"Lausanne" delegate:self];
    
    //MapService* mapService = [[MapService sharedInstanceToRetain] retain];
    
    //[mapService searchFor:@"candea" delegate:self];
    //[mapService getLayerListWithDelegate:self];
    
    //NewsService* service = [[NewsService sharedInstanceToRetain] retain];
    
    //NSString* lang = [[NSLocale currentLocale] objectForKey:NSLocaleLanguageCode];
    //NSLog(@"lang : %@", lang);
    //[service getNewsItemsForLanguage:@"fr-FR" delegate:self];
    
    //AuthenticationController* authController = [[AuthenticationController alloc] init];
    
    //[authController loginToService:TypeOfService_SERVICE_CAMIPRO delegate:self];
    
    //AuthenticationService* authService = [[AuthenticationService sharedInstanceToRetain] retain];
    //[authService getTequilaKeyForService:TypeOfService_SERVICE_CAMIPRO delegate:self];    
    
    
    
    /*END TEST*/
    
    /* OFFICIAL TESTS */
    
    //[[[PocketCampusLogicTests alloc] init] testAll];
    
    /* END OF OFFICAL TESTS */
    
    [self.window makeKeyAndVisible];

    return YES;
}

/*
- (void)autocompleteFor:(NSString*)constraint didReturn:(NSArray*)results {
    NSLog(@"%@", results);
}

- (void)locationsForNames:(NSArray*)names didReturn:(NSArray*)locations {
    NSLog(@"%@", locations);
}
*/
/*- (void)tripsFrom:(NSString*)from to:(NSString*)to didReturn:(QueryTripsResult*)tripResult {
    NSLog(@"context : %@", tripResult.context);
    NSLog(@"from : %@", tripResult.from);
    NSLog(@"via : %@", tripResult.via);
    NSLog(@"to : %@", tripResult.to);
    NSLog(@"from amb : %@", tripResult.ambiguousFrom);
    NSLog(@"to amb : %@", tripResult.ambiguousTo);
    //NSLog(@"connections : %@",  tripResult.connections);
    
    TransportTrip* trip = [tripResult.connections objectAtIndex:0];
    
    
    NSLog(@"%@", [TransportUtils automaticTimeStringForTimestamp:(NSTimeInterval)(trip.departureTime/1000.0) maxIntervalForMinutesLeftString:15.0]);
    
}*/
/*
- (void)tripsFromStationID:(NSString*)fromStationID toStationID:(NSString*)toStationID didReturn:(QueryTripsResult*)tripResult {
    NSLog(@"%@", tripResult);
}

- (void)nearestFavoriteTransportStationDidReturn:(TransportStation*)nearestStation {
    UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"Nearest station" message:nearestStation.name delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
    [alert release];
}
- (void)nearestFavoriteTransportStationFailed:(NSString*)reason {
    NSLog(@"failed : %@", reason);
}*/

/*
 - (void)test:(double)argVal{
    NSNumber* val = [NSNumber numberWithDouble:argVal];

    NSArray* array = [NSArray arrayWithObject:val];


    NSLog(@"%lf", [[array objectAtIndex:0] doubleValue]);
 }

- (void)test32:(void**)arg {
    NSLog(@"arg : %d", *arg);
}

- (void)test2 {
    NSLog(@"OK");
}

- (void)test3:(NSString*)aString {
    NSLog(@"string %@", aString);
}
*/

/*- (void)getMealsDidReturn:(NSArray*)meals {
    NSLog(@"%@", meals);
}*/
/*
- (void)getRestaurantsDidReturn:(NSArray*)restaurants {
    NSLog(@"%@", restaurants);
}

- (void)getSandwichesDidReturn:(NSArray*)sandwiches {
    NSLog(@"%@", sandwiches);
}*/
/*
- (void)hasVotedFor:(NSString*)deviceId didReturn:(BOOL)hasVoted; {
    NSLog(@"has voted : %@ %d", deviceId, hasVoted);
}

- (void)setRatingForMeal:(Id)mealId rating:(double)rating deviceId:(NSString*)deviceId didReturn:(int)status; {
    NSLog(@"%lf, %lld, %@, %d", rating, mealId, deviceId, status);
}

- (void)getRatingsDidReturn:(NSDictionary*)ratings {
    NSLog(@"%@",ratings);
}*/

/*- (void)getLayerListDidReturn:(NSArray*)layerList { //array of MapLayer
    NSLog(@"%@", layerList);
}*/

/*- (void)getLayerItemsForLayerId:(Id)layerID didReturn:(NSArray*)layerItems { //array of MapItem
    for(MapItem* item in layerItems) {
        NSLog(@"%@", [item descriptionObject]);
    }
}*/
/*- (void)searchFor:(NSString*)query didReturn:(NSArray*)results { //array of MapItem
    for(MapItem* item in results) {
        NSLog(@"%@", [item descriptionObject]);
    }
}*/
 /*
- (void)newsItemsForLanguage:(NSString*)language didReturn:(NSArray*)newsItems {
    NSLog(@"%@", newsItems);
}
*/

/*- (void)gotSessionId:(SessionId*)sessionId {
    NSLog(@"gotSessionId:%@", sessionId);
    CamiproRequest* request = [[[CamiproRequest alloc] initWithISessionId:sessionId iLanguage:@"en"] autorelease];
    CamiproService* camiproService = [[CamiproService sharedInstanceToRetain] retain];
    [camiproService sendLoadingInfoByEmail:request delegate:self];
}*/
/*
- (void)userCancelledAuthentication {
    NSLog(@"user cancelled auth");
}

- (void)authenticationTimeout {
    NSLog(@"auth timeout");
}*/

/*- (void)getTequilaKeyForService:(int)service didReturn:(TequilaKey*)tequilaKey {
    NSLog(@"tequilaKey : %@", tequilaKey);
}

- (void)getTequilaKeyFailedForService:(int)service {
    NSLog(@"getTequilaKeyFailedForService:%d", service);
}*/

/*- (void)getBalanceAndTransactionsForCamiproRequest:(CamiproRequest*)camiproRequest didReturn:(BalanceAndTransactions*)balanceAndTransactions {
    NSLog(@"%@", balanceAndTransactions);
}

- (void)getBalanceAndTransactionsFailedForCamiproRequest:(CamiproRequest*)camiproRequest {
    NSLog(@"getBalanceAndTransactionsFailedForCamiproRequest");
}

- (void)getStatsAndLoadingInfoForCamiproRequest:(CamiproRequest*)camiproRequest didReturn:(StatsAndLoadingInfo*)statsAndLoadingInfo {
    NSLog(@"%@", statsAndLoadingInfo);
}

- (void)getStatsAndLoadingInfoFailedForCamiproRequest:(CamiproRequest*)camiproRequest {
    NSLog(@"getStatsAndLoadingInfoFailedForCamiproRequest");
}

- (void)sendLoadingInfoByEmailForCamiproRequest:(CamiproRequest*)camiproRequest didReturn:(SendMailResult*)sendMailResult {
    NSLog(@"%@", sendMailResult);
}

- (void)sendLoadingInfoByEmailFailedForCamiproRequest:(CamiproRequest*)camiproRequest {
    NSLog(@"sendLoadingInfoByEmailFailedForCamiproRequest");
}
*/
- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
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
}

@end
