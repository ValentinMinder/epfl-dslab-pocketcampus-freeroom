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

//  Created by Loïc Gardiol on 05.05.12.

#import "NewsController.h"

#import "NewsListViewController.h"

#import "NewsSplashViewController.h"

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
                PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:newsListViewController];
                NewsSplashViewController* splashViewController = [[NewsSplashViewController alloc] init];
                self.mainSplitViewController = [[PluginSplitViewController alloc] initWithMasterViewController:navController detailViewController:[[PCNavigationController alloc] initWithRootViewController:splashViewController]];
                self.mainSplitViewController.gaiScreenName = @"/news";
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

- (UIViewController*)viewControllerForURLQueryAction:(NSString*)action parameters:(NSDictionary*)parameters {
    return [self.class viewControllerForURLQueryAction:action parameters:parameters];
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"NewsPlugin", @"");
}

+ (NSString*)identifierName {
    return @"News";
}

#pragma mark - Private

+ (UIViewController*)viewControllerForURLQueryAction:(NSString*)action parameters:(NSDictionary*)parameters {
    if (action.length == 0) {
        NewsListViewController* newsListViewController = [[NewsListViewController alloc] init];
        newsListViewController.title = [[self class] localizedName];
        return newsListViewController;
    }
    return nil;
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
