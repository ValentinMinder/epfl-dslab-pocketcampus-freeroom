//
//  MapController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 12.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MapController.h"

#import "MapViewController.h"

@implementation MapController

- (id)init
{
    self = [super init];
    if (self) {
        MapViewController* mapViewController = [[MapViewController alloc] init];
        PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:mapViewController];
        navController.pluginIdentifier = [[self class] identifierName];
        self.mainNavigationController = navController;
    }
    return self;
}

+ (UIViewController*)viewControllerWithInitialSearchQuery:(NSString*)query {
    return [[[MapViewController alloc] initWithInitialQuery:query] autorelease];
}

+ (UIViewController*)viewControllerWithInitialSearchQuery:(NSString*)query pinLabelText:(NSString*)pinLabelText {
    return [[[MapViewController alloc] initWithInitialQuery:query pinTextLabel:pinLabelText] autorelease];
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"MapPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Map";
}

- (void)dealloc
{
    [super dealloc];
}

@end
