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

static NSString* name = nil;

- (id)init
{
    self = [super init];
    if (self) {
        RestaurantsListViewController* restaurantsListViewController = [[RestaurantsListViewController alloc] initWithNibName:@"RestaurantsListView" bundle:nil];
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

+ (NSString*)localizedName {
    if (name != nil) {
        return name;
    }
    name = [NSLocalizedStringFromTable(@"PluginName", @"FoodPlugin", @"") retain];
    return name;
}

+ (NSString*)identifierName {
    return @"Food";
}

- (NSString*)localizedStringForKey:(NSString*)key {
    return NSLocalizedStringFromTable(key, [[self class] identifierName], @"");
}

- (void)dealloc
{
    [name release];
    name = nil;
    [super dealloc];
}

@end
