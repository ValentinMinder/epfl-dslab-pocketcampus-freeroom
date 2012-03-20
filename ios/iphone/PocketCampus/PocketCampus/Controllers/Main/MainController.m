//
//  MainController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 02.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MainController.h"
#import "PCValues.h"
#import "HomeViewController.h"

/* WARNING !! : must import <identifierName>Controller.h for each plugin*/

#import "DirectoryController.h"

@implementation MainController

@synthesize pluginsList;

- (id)initWithWindow:(UIWindow *)window_
{
    self = [super init];
    if (self) {
        window = window_;
        [self initPluginsList];
        [self initNavigationControllerAndPushHome];
        
    }
    return self;
}

- (void)initPluginsList {
    NSDictionary* plist = [NSDictionary dictionaryWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"Plugins" ofType:@"plist"]];
    NSArray* pluginsFromPlist = [plist objectForKey:@"Plugins"];
    NSMutableArray* pluginsTempArray = [NSMutableArray arrayWithCapacity:pluginsFromPlist.count];
    for (NSDictionary* pluginDic in pluginsFromPlist) {
        NSString* identifierName = [pluginDic objectForKey:@"identifierName"];
        NSLog(@"-> Detected plugin : %@", identifierName);
        
        [pluginsTempArray addObject:identifierName];
    }
    
    pluginsList = [[NSArray arrayWithArray:pluginsTempArray] retain]; //creates a non-mutable copy of the dictionary
    
}

- (NSString*)pluginControllerNameForIdentifier:(NSString*)identifier {
    return [NSString stringWithFormat:@"%@Controller", identifier];
}

- (NSString*)pluginControllerNameForIndex:(NSUInteger)index {
    return [self pluginControllerNameForIdentifier:[pluginsList objectAtIndex:index]];
}

- (void)initNavigationControllerAndPushHome {
    HomeViewController* homeViewController = [[HomeViewController alloc] initWithMainController:self];
    navController = [[UINavigationController alloc] initWithRootViewController:homeViewController];
    navController.navigationBar.tintColor = [PCValues pocketCampusRed];
    [window addSubview:navController.view];
}

- (void)dealloc
{
    [pluginsList release];
    [navController release];
    [super dealloc];
}

@end
