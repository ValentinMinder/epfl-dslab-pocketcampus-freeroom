//
//  DirectoryController.m
//  DirectoryPlugin
//
//  Created by Loïc Gardiol on 01.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "DirectoryController.h"
#import "PluginNavigationController.h"
#import "DirectorySearchViewController.h"

static DirectoryController* instance __weak = nil;

@implementation DirectoryController

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"DirectoryController cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super init];
        if (self) {
            DirectorySearchViewController* directorySearchViewController = [[DirectorySearchViewController alloc] init];
            directorySearchViewController.title = [[self class] localizedName];
            PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:directorySearchViewController];
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

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"DirectoryPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Directory";
}

- (void)pluginDidBecomePassive {
    //nothing
}

- (void)pluginWillLoseFocus {
    //NSLog(@"%@", mainNavigationController.visibleViewController);
    if ([self.mainNavigationController.visibleViewController isKindOfClass:[DirectorySearchViewController class]]) {
        [[(DirectorySearchViewController*)self.mainNavigationController.visibleViewController searchBar] resignFirstResponder];
    }
}
- (void)pluginDidRegainActive {
    if ([self.mainNavigationController.visibleViewController isKindOfClass:[DirectorySearchViewController class]]) {
        [[(DirectorySearchViewController*)self.mainNavigationController.visibleViewController searchBar] becomeFirstResponder];
    }
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
