//
//  NewsController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "NewsController.h"

#import "NewsListViewController.h"

#import "NewsSplashViewController.h"

#import "PCUtils.h"

static NewsController* instance __weak = nil;

@implementation NewsController

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"NewsController cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super init];
        if (self) {
            NewsListViewController* newsListViewController = [[NewsListViewController alloc] init];
            newsListViewController.title = [[self class] localizedName];
            
            if ([PCUtils isIdiomPad]) {
                UINavigationController* navController = [[UINavigationController alloc] initWithRootViewController:newsListViewController];
                NewsSplashViewController* splashViewController = [[NewsSplashViewController alloc] init];
                self.mainSplitViewController = [[PluginSplitViewController alloc] initWithMasterViewController:navController detailViewController:splashViewController];
                self.mainSplitViewController.pluginIdentifier = [[self class] identifierName];
                self.mainSplitViewController.delegate = self;
                
            } else {
                PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:newsListViewController];
                navController.pluginIdentifier = [[self class] identifierName];
                self.mainNavigationController = navController;
            }
            
            instance = self;
        }
        return self;
    }
}

#pragma mark - PluginControllerProtocol

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
    return NSLocalizedStringFromTable(@"PluginName", @"NewsPlugin", @"");
}

+ (NSString*)identifierName {
    return @"News";
}

#pragma mark - UISplitViewControllerDelegate

- (BOOL)splitViewController:(UISplitViewController *)svc shouldHideViewController:(UIViewController *)vc inOrientation:(UIInterfaceOrientation)orientation {
    return NO;
}

#pragma mark - dealloc

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
