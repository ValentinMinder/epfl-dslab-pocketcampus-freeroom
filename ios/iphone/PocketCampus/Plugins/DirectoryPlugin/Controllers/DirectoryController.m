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

static NSString* name = nil;

- (id)init
{
    self = [super init];
    if (self) {
        DirectorySearchViewController* directorySearchViewController = [[DirectorySearchViewController alloc] initWithNibName:@"DirectorySearchView" bundle:nil];
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
    if (name != nil) {
        return name;
    }
    name = [NSLocalizedStringFromTable(@"PluginName", @"DirectoryPlugin", @"") retain];
    return name;
}

+ (NSString*)identifierName {
    return @"Directory";
}

- (NSString*)localizedStringForKey:(NSString*)key {
    return NSLocalizedStringFromTable(key, [[self class] identifierName], @"");
}

- (void)cancelAllOperations {
    //DirectoryServiced managned by DirectorySearchViewController instance
}

- (void)refresh {
    //Meaning-less for Directory
}

- (void)dealloc
{
    [name release];
    name = nil;
    [super dealloc];
}

@end
