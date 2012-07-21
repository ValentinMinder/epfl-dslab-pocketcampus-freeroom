//
//  CamiproController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "CamiproController.h"

#import "CamiproViewController.h"

@implementation CamiproController

static NSString* name = nil;
static BOOL initObserversDone = NO;

- (id)init
{
    self = [super init];
    if (self) {
        CamiproViewController* camiproViewController = [[CamiproViewController alloc] init];
        camiproViewController.title = [[self class] localizedName];
        mainViewController = camiproViewController;
    }
    return self;
}

- (id)initWithMainController:(MainController *)mainController_
{
    self = [self init];
    if (self) {
        mainController = mainController_;
        
    }
    return self;
}

+ (void)initObservers {
    @synchronized(self) {
        if (initObserversDone) {
            return;
        }
        [[NSNotificationCenter defaultCenter] addObserverForName:[AuthenticationService logoutNotificationName] object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *note) {
            NSLog(@"-> Camipro received %@ notification", [AuthenticationService logoutNotificationName]);
            [CamiproService saveSessionId:nil]; //removing stored session
        }];
        initObserversDone = YES;
    }
}

+ (NSString*)localizedName {
    if (name != nil) {
        return name;
    }
    name = [NSLocalizedStringFromTable(@"PluginName", @"CamiproPlugin", @"") retain];
    return name;
}

+ (NSString*)identifierName {
    return @"Camipro";
}

- (void)dealloc
{
    [name release];
    name = nil;
    [super dealloc];
}

@end
