//
//  FoodController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 08.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "FoodController.h"
#import "RestaurantsListViewController.h"

static FoodController* instance __weak = nil;

@implementation FoodController

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"FoodController cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super init];
        if (self) {
            RestaurantsListViewController* restaurantsListViewController = [[RestaurantsListViewController alloc] init];
            restaurantsListViewController.title = [[self class] localizedName];
            PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:restaurantsListViewController];
            navController.pluginIdentifier = [[self class] identifierName];
            self.mainNavigationController = navController;
            instance = self;
        }
        return self;
    }
}

+ (id)sharedInstanceToRetain {
    @synchronized (self) {
        if (instance) {
            return instance;
        }
#if __has_feature(objc_arc)
        return [[[self class] alloc] init];
#else
        return [[[[self class] alloc] init] autorelease];
#endif
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
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}

@end
