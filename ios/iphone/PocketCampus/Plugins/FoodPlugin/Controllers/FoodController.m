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
        mainViewController = restaurantsListViewController;
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

- (void)refresh {
    if (mainViewController == nil || ![mainViewController isKindOfClass:[RestaurantsListViewController class]]) {
        return;
    }
    if ([((RestaurantsListViewController*)mainViewController) shouldRefresh]) {
        if (mainViewController.navigationController.topViewController != mainViewController) {
            [mainViewController.navigationController popToViewController:mainViewController animated:NO];
        }
        [(RestaurantsListViewController*)mainViewController refresh];
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
