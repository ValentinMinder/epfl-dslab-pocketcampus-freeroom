//
//  FoodController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 08.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "FoodController.h"
#import "RestaurantsListViewController.h"

@implementation FoodController

- (id)init
{
    self = [super init];
    if (self) {
        RestaurantsListViewController* restaurantsListViewController = [[RestaurantsListViewController alloc] init];
        restaurantsListViewController.title = [[self class] localizedName];
        PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:restaurantsListViewController];
        navController.pluginIdentifier = [[self class] identifierName];
        self.mainNavigationController = navController;
    }
    return self;
}

- (void)refresh {
    if (mainViewController == nil || ![mainViewController isKindOfClass:[RestaurantsListViewController class]]) {
        return;
    }
    if ([(RestaurantsListViewController*)(self.mainNavigationController.viewControllers[0]) shouldRefresh]) {
        if (self.mainNavigationController.topViewController != self.mainNavigationController.viewControllers[0]) {
            [self.mainNavigationController popToRootViewControllerAnimated:NO];
        }
        [(RestaurantsListViewController*)(self.mainNavigationController.viewControllers[0]) refresh];
    }
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"FoodPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Food";
}

- (NSString*)localizedStringForKey:(NSString*)key {
    return NSLocalizedStringFromTable(key, [[self class] identifierName], @"");
}

- (void)dealloc
{
    [super dealloc];
}

@end
