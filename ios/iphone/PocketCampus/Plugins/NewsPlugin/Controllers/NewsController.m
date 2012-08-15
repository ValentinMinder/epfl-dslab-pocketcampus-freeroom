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
        mainViewController = newsListViewController;
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
    if (mainViewController == nil || ![mainViewController isKindOfClass:[NewsListViewController class]]) {
        return;
    }
    if (((NewsListViewController*)mainViewController).shouldRefresh) {
        [(NewsListViewController*)mainViewController refresh];
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
