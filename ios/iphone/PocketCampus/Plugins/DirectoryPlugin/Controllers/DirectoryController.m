//
//  DirectoryController.m
//  DirectoryPlugin
//
//  Created by Loïc Gardiol on 01.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "DirectoryController.h"
#import "DirectorySearchViewController.h"

@implementation DirectoryController

- (id)init
{
    self = [super init];
    if (self) {
        DirectorySearchViewController* directorySearchViewController = [[DirectorySearchViewController alloc] init];
        directorySearchViewController.title = [[self class] localizedName];
        mainViewController = directorySearchViewController;
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
    return NSLocalizedStringFromTable(@"PluginName", @"DirectoryPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Directory";
}

- (void)dealloc
{
    [super dealloc];
}

@end
