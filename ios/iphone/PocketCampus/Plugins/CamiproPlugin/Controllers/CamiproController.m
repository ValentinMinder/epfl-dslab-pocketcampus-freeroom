//
//  CamiproController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "CamiproController.h"

#import "CamiproViewController.h"

#import "ObjectArchiver.h"

@implementation CamiproController

static BOOL initObserversDone = NO;
static NSString* kDeleteSessionAtInitKey = @"DeleteSessionAtInit";

- (id)init
{
    self = [super init];
    if (self) {
        [[self class] deleteSessionIfNecessary];
        CamiproViewController* camiproViewController = [[CamiproViewController alloc] init];
        camiproViewController.title = [[self class] localizedName];
        PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:camiproViewController];
        navController.pluginIdentifier = [[self class] identifierName];
        self.mainNavigationController = navController;
    }
    return self;
}

- (void)refresh {
    if (((CamiproViewController*)(self.mainNavigationController.visibleViewController)).shouldRefresh) {
        [((CamiproViewController*)(self.mainNavigationController.visibleViewController)) refresh];
    }
}

+ (void)deleteSessionIfNecessary {
    NSNumber* deleteSession = (NSNumber*)[ObjectArchiver objectForKey:kDeleteSessionAtInitKey andPluginName:@"camipro"];
    if (deleteSession && [deleteSession boolValue]) {
        NSLog(@"-> Delayed logout notification on Camipro now applied : deleting sessionId");
        [CamiproService saveSessionId:nil];
        [ObjectArchiver saveObject:nil forKey:kDeleteSessionAtInitKey andPluginName:@"camipro"];
    }
}

+ (void)initObservers {
    @synchronized(self) {
        if (initObserversDone) {
            return;
        }
        [[NSNotificationCenter defaultCenter] addObserverForName:[AuthenticationService logoutNotificationName] object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *notification) {
            NSNumber* delayed = [notification.userInfo objectForKey:[AuthenticationService delayedUserInfoKey]];
            if ([delayed boolValue]) {
                NSLog(@"-> Camipro received %@ notification delayed", [AuthenticationService logoutNotificationName]);
                [ObjectArchiver saveObject:[NSNumber numberWithBool:YES] forKey:kDeleteSessionAtInitKey andPluginName:@"camipro"];
            } else {
                NSLog(@"-> Camipro received %@ notification", [AuthenticationService logoutNotificationName]);
                [CamiproService saveSessionId:nil]; //removing stored session
            }
        }];
        initObserversDone = YES;
    }
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"CamiproPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Camipro";
}

- (void)dealloc
{
    [[self class] deleteSessionIfNecessary];
    [super dealloc];
}

@end
