/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

//  Created by Loïc Gardiol on 01.03.12.

#import "DirectoryController.h"
#import "PluginNavigationController.h"
#import "DirectorySearchViewController.h"
#import "DirectoryPersonViewController.h"
#import "DirectoryEmptyDetailViewController.h"
#import "DirectoryService.h"

NSString* const kDirectoryURLActionSearch = @"search";
NSString* const kDirectoryURLActionView = @"view";

NSString* const kDirectoryURLParameterQuery = @"q";

static DirectoryController* instance __weak = nil;

@interface DirectoryController ()<UISplitViewControllerDelegate>

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
                PCNavigationController* navController =  [[PCNavigationController alloc] initWithRootViewController:self.directorySearchViewController];
                DirectoryEmptyDetailViewController* emptyDetailViewController = [[DirectoryEmptyDetailViewController alloc] init];
                PluginSplitViewController* splitViewController = [[PluginSplitViewController alloc] initWithMasterViewController:navController detailViewController:[[PCNavigationController alloc] initWithRootViewController:emptyDetailViewController]];
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

- (UIViewController*)viewControllerForURLQueryAction:(NSString*)action parameters:(NSDictionary*)parameters {
    return [self.class viewControllerForURLQueryAction:action parameters:parameters];
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

+ (UIViewController*)viewControllerForWebURL:(NSURL*)webURL {
    if (![webURL.host isEqualToString:@"personnes.epfl.ch"] && ![webURL.host isEqualToString:@"people.epfl.ch"]) {
        return nil;
    }
    NSString* firstDotLastname = webURL.lastPathComponent;
    if ([firstDotLastname rangeOfString:@"."].location == NSNotFound) {
        return nil;
    }
    return [self viewControllerForURLQueryAction:kDirectoryURLActionSearch parameters:@{kDirectoryURLParameterQuery:firstDotLastname}];
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"DirectoryPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Directory";
}

#pragma mark - Private

+ (UIViewController*)viewControllerForURLQueryAction:(NSString*)action parameters:(NSDictionary*)parameters {
    if ([action isEqualToString:kDirectoryURLActionSearch]) { //search in EPFL directory
        NSString* query = parameters[kDirectoryURLParameterQuery];
        if (query) {
            return [[DirectoryPersonViewController alloc] initAndLoadPersonWithFullName:query];
        }
    } else if ([action isEqualToString:kDirectoryURLActionView]) {
        @try {
            if (parameters.count > 0) {
                Person* person = [Person new];
                [person setValuesForKeysWithDictionary:parameters];
                return [[DirectoryPersonViewController alloc] initWithPerson:person];
            }
        }
        @catch (NSException *exception) {
            CLSNSLog(@"!! ERROR when converting parameters to Person object: %@", exception);
        }
    } else if (action.length == 0) {
        UIViewController* viewController = [[DirectorySearchViewController alloc] init];
        viewController.title = [[self class] localizedName];
        return viewController;
    }
    return nil;
}

#pragma mark - UISplitViewControllerDelegate

- (BOOL)splitViewController:(UISplitViewController *)svc shouldHideViewController:(UIViewController *)vc inOrientation:(UIInterfaceOrientation)orientation {
    /*if (orientation == UIInterfaceOrientationMaskPortrait) {
     return YES;
     }*/
    return NO;
}

#pragma mark - Dealloc

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
