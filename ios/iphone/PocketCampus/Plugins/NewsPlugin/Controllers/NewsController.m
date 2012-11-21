//
//  NewsController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "NewsController.h"

#import "NewsListViewController.h"

@implementation NewsController

- (id)init
{
    self = [super init];
    if (self) {
        NewsListViewController* newsListViewController = [[NewsListViewController alloc] init];
        newsListViewController.title = [[self class] localizedName];
        PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:newsListViewController];
        navController.pluginIdentifier = [[self class] identifierName];
        self.mainNavigationController = navController;
    }
    return self;
}

- (void)refresh {
    if (((NewsListViewController*)(self.mainNavigationController.viewControllers[0])).shouldRefresh) {
        [((NewsListViewController*)(self.mainNavigationController.viewControllers[0])) refresh];
    }
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"NewsPlugin", @"");
}

+ (NSString*)identifierName {
    return @"News";
}

- (void)dealloc
{
    [super dealloc];
}

@end
