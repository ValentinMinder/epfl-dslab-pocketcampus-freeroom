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
        if ([[pluginDic objectForKey:@"enabled"] boolValue]) {
            NSLog(@"-> Detected enabled plugin : %@", identifierName);
            [pluginsTempArray addObject:identifierName];
        }
    }
    
    /* sorting plugins alphabetically on plugin name. Should be on localized name, but cannot access it here */
    /*[pluginsTempArray sortWithOptions:0 usingComparator:^NSComparisonResult(id obj1, id obj2) {
        NSString* name1 = (NSString*)obj1;
        NSString* name2 = (NSString*)obj2;
        return [name1 compare:name2];
    }];*/
    
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

- (void)refreshDisplayedPlugin {
    id viewController = navController.visibleViewController;
    if (viewController != nil && [viewController respondsToSelector:@selector(refresh)]) {
        [viewController refresh];
    }
}

- (void)dealloc
{
    [pluginsList release];
    [navController release];
    [super dealloc];
}

@end
