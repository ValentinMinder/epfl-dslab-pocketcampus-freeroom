//
//  DirectoryController.m
//  DirectoryPlugin
//
//  Created by Loïc Gardiol on 01.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "DirectoryController.h"
#import "PluginNavigationController.h"
#import "DirectorySearchViewController.h"

@implementation DirectoryController

- (id)initWithMainController:(MainController2 *)mainController_
{
    self = [super init];
    if (self) {
        mainController = mainController_;
        DirectorySearchViewController* directorySearchViewController = [[DirectorySearchViewController alloc] init];
        directorySearchViewController.title = [[self class] localizedName];
        PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:directorySearchViewController];
        navController.pluginIdentifier = [[self class] identifierName];
        mainNavigationController = navController;
    }
    return self;
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"DirectoryPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Directory";
}

- (void)pluginDidBecomePassive {
    //nothing
}

- (void)pluginWillLoseFocus {
    //NSLog(@"%@", mainNavigationController.visibleViewController);
    if ([mainNavigationController.visibleViewController isKindOfClass:[DirectorySearchViewController class]]) {
        [[(DirectorySearchViewController*)mainNavigationController.visibleViewController searchBar] resignFirstResponder];
    }
}
- (void)pluginDidRegainActive {
    if ([mainNavigationController.visibleViewController isKindOfClass:[DirectorySearchViewController class]]) {
        [[(DirectorySearchViewController*)mainNavigationController.visibleViewController searchBar] becomeFirstResponder];
    }
}

@end
