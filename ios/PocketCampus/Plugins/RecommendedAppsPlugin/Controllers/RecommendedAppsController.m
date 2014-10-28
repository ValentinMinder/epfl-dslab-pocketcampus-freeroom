/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of PocketCampus.Org nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
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

//  Created by Silviu Andrica on 18.08.2014.

#import "RecommendedAppsController.h"

#import "RecommendedAppsListViewController.h"

static RecommendedAppsController* instance __weak = nil;

@interface RecommendedAppsController ()

@end

@implementation RecommendedAppsController

#pragma mark - Init

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"RecommendedAppsController cannot be instancied more than once at a time, use sharedInstance instead." userInfo:nil];
        }
        self = [super init];
        if (self) {
            RecommendedAppsListViewController* categoriesListViewController = [[RecommendedAppsListViewController alloc] init];
            categoriesListViewController.title = [[self class] localizedName];
            ///if ([PCUtils isIdiomPad]) {
//                PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:restaurantsListViewController];
//                FoodSplashDetailViewController* splashDetailViewController = [FoodSplashDetailViewController new];
//                PluginSplitViewController* splitViewController = [[PluginSplitViewController alloc] initWithMasterViewController:navController detailViewController:[[PCNavigationController alloc] initWithRootViewController:splashDetailViewController]];
//                splitViewController.pluginIdentifier = [[self class] identifierName];
//                splitViewController.delegate = self;
//                self.mainSplitViewController = splitViewController;
            //} else {
                PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:categoriesListViewController];
                navController.pluginIdentifier = [[self class] identifierName];
                self.mainNavigationController = navController;
            //}
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
    return NSLocalizedStringFromTable(@"PluginName", @"RecommendedAppsPlugin", @"");
}

+ (NSString*)identifierName {
    return @"RecommendedApps";
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
