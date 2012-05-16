//
//  CamiproController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "CamiproController.h"

@implementation CamiproController

static NSString* name = nil;

- (id)init
{
    self = [super init];
    if (self) {
        /*DirectorySearchViewController* directorySearchViewController = [[DirectorySearchViewController alloc] init];
        directorySearchViewController.title = [[self class] localizedName];
        mainViewController = directorySearchViewController;*/
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
    name = [NSLocalizedStringFromTable(@"PluginName", @"CamiproPlugin", @"") retain];
    return name;
}

+ (NSString*)identifierName {
    return @"Camipro";
}

- (void)dealloc
{
    [name release];
    name = nil;
    [super dealloc];
}

@end
