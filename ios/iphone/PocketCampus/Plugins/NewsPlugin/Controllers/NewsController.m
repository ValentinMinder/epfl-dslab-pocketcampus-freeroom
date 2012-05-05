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

static NSString* name = nil;

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

+ (NSString*)localizedName {
    if (name != nil) {
        return name;
    }
    name = [NSLocalizedStringFromTable(@"PluginName", @"NewsPlugin", @"") retain];
    return name;
}

+ (NSString*)identifierName {
    return @"News";
}

- (void)dealloc
{
    [name release];
    name = nil;
    [super dealloc];
}

@end
