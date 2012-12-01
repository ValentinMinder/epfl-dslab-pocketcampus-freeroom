//
//  MapController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 12.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MapController.h"

#import "MapViewController.h"

static MapController* instance __weak = nil;

@implementation MapController

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"MapController cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super init];
        if (self) {
            MapViewController* mapViewController = [[MapViewController alloc] init];
            PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:mapViewController];
            navController.pluginIdentifier = [[self class] identifierName];
            self.mainNavigationController = navController;
            instance = self;
        }
        return self;
    }
}

+ (id)sharedInstance {
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

+ (UIViewController*)viewControllerWithInitialSearchQuery:(NSString*)query {
    return [[MapViewController alloc] initWithInitialQuery:query];
}

+ (UIViewController*)viewControllerWithInitialSearchQuery:(NSString*)query pinLabelText:(NSString*)pinLabelText {
    return [[MapViewController alloc] initWithInitialQuery:query pinTextLabel:pinLabelText];
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"MapPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Map";
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
