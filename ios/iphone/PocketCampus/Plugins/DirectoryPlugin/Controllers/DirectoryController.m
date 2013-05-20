//
//  DirectoryController.m
//  DirectoryPlugin
//
//  Created by Loïc Gardiol on 01.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "DirectoryController.h"
#import "PCUtils.h"
#import "PluginNavigationController.h"
#import "DirectorySearchViewController.h"
#import "DirectoryEmptyDetailViewController.h"

static DirectoryController* instance __weak = nil;

@interface DirectoryController ()

@property (nonatomic, strong) DirectorySearchViewController* directorySearchViewController;

@end

@implementation DirectoryController

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"DirectoryController cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super init];
        if (self) {
                        
            self.directorySearchViewController = [[DirectorySearchViewController alloc] init];
            self.directorySearchViewController.title = [[self class] localizedName];
            
            if ([PCUtils isIdiomPad]) {
                UINavigationController* navController =  [[UINavigationController alloc] initWithRootViewController:self.directorySearchViewController];
                DirectoryEmptyDetailViewController* emptyDetailViewController = [[DirectoryEmptyDetailViewController alloc] init];
                PluginSplitViewController* splitViewController = [[PluginSplitViewController alloc] initWithMasterViewController:navController detailViewController:emptyDetailViewController];
                splitViewController.pluginIdentifier = [[self class] identifierName];
                splitViewController.delegate = self;
                self.mainSplitViewController = splitViewController;
            } else {
                PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:self.directorySearchViewController];
                navController.pluginIdentifier = [[self class] identifierName];
                self.mainNavigationController = navController;
            }
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

- (BOOL)handleURLQueryAction:(NSString *)action parameters:(NSDictionary *)parameters {
    UIViewController* viewController = [self viewControllerForURLQueryAction:action parameters:parameters];
    if (!viewController) {
        return NO;
    }
    UINavigationController* navController = nil;
    if ([PCUtils isIdiomPad]) {
        navController = self.mainSplitViewController.viewControllers[1];
        if (![navController isKindOfClass:[UINavigationController class]]) {
            navController = [[UINavigationController alloc] initWithRootViewController:viewController];
            self.mainSplitViewController.viewControllers = @[self.mainSplitViewController.viewControllers[0], navController];
        }
    } else {
        navController = self.mainNavigationController;
        [navController popToRootViewControllerAnimated:NO];
    }
    if (!viewController.navigationController) {
        [navController pushViewController:viewController animated:YES];
    }
    return YES;
}

- (UIViewController*)viewControllerForURLQueryAction:(NSString*)action parameters:(NSDictionary*)parameters {
    if ([action isEqualToString:@"search"]) { //search in EPFL directory
        NSString* query = parameters[@"q"];
        if (query) {
            return [[PCUnkownPersonViewController alloc] initAndLoadPersonWithFullName:query];
        }
    } else if ([action isEqualToString:@"view"]) {
        @try {
            if (parameters.count > 0) {
                Person* person = [Person new];
                [person setValuesForKeysWithDictionary:parameters];
                return [[PCUnkownPersonViewController alloc] initWithPerson:person];
            }
        }
        @catch (NSException *exception) {
            NSLog(@"!! ERROR when converting parameters to Person object: %@", exception);
        }
    }
    return nil;
}


#pragma mark - PluginControllerProtocol

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"DirectoryPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Directory";
}

#pragma mark - UISplitViewControllerDelegate

- (BOOL)splitViewController:(UISplitViewController *)svc shouldHideViewController:(UIViewController *)vc inOrientation:(UIInterfaceOrientation)orientation {
    /*if (orientation == UIInterfaceOrientationMaskPortrait) {
     return YES;
     }*/
    return NO;
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
