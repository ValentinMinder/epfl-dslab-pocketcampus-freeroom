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

- (id)init
{
    self = [super init];
    if (self) {
        DirectorySearchViewController* directorySearchViewController = [[DirectorySearchViewController alloc] init];
        directorySearchViewController.title = [[self class] localizedName];
        PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:directorySearchViewController];
        navController.pluginIdentifier = [[self class] identifierName];
        self.mainNavigationController = navController;
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
    if ([self.mainNavigationController.visibleViewController isKindOfClass:[DirectorySearchViewController class]]) {
        [[(DirectorySearchViewController*)self.mainNavigationController.visibleViewController searchBar] resignFirstResponder];
    }
}
- (void)pluginDidRegainActive {
    if ([self.mainNavigationController.visibleViewController isKindOfClass:[DirectorySearchViewController class]]) {
        [[(DirectorySearchViewController*)self.mainNavigationController.visibleViewController searchBar] becomeFirstResponder];
    }
}

@end
