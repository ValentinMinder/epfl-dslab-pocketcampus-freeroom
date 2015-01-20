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

#import "MoodleController.h"

#import "MoodleCoursesListViewController.h"

#import "PCPersistenceManager.h"

#import "MoodleService.h"

#import "MoodleModelAdditions.h"

@interface MoodleController ()<UISplitViewControllerDelegate>

@property (nonatomic, strong) MoodleService* moodleService;

@end

@implementation MoodleController

static MoodleController* instance __weak = nil;

#pragma mark - Init

+ (void)initialize {
    //initializing default settings
    NSUserDefaults* defaults = [PCPersistenceManager userDefaultsForPluginName:@"moodle"];
    if (![defaults objectForKey:kMoodleSaveDocsPositionGeneralSettingBoolKey]) {
        [defaults setBool:YES forKey:kMoodleSaveDocsPositionGeneralSettingBoolKey];
    }
}

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"MoodleController cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super init];
        if (self) {
            MoodleCoursesListViewController* coursesListViewController = [[MoodleCoursesListViewController alloc] init];
            
            if ([PCUtils isIdiomPad]) {
                PCNavigationController* navController =  [[PCNavigationController alloc] initWithRootViewController:coursesListViewController];
                UIViewController* emptyDetailViewController = [[UIViewController alloc] init]; //splash view controller will be returned by coursesListViewController as PluginSplitViewControllerDelegate
                PluginSplitViewController* splitViewController = [[PluginSplitViewController alloc] initWithMasterViewController:navController detailViewController:emptyDetailViewController];
                splitViewController.gaiScreenName = @"moodle";
                splitViewController.pluginIdentifier = [[self class] identifierName];
                splitViewController.delegate = self;
                self.mainSplitViewController = splitViewController;
            } else {
                PluginNavigationController* navController = [[PluginNavigationController alloc] initWithRootViewController:coursesListViewController];
                navController.pluginIdentifier = [[self class] identifierName];
                self.mainNavigationController = navController;
            }
            
            instance = self;
        }
        return self;
    }
}

#pragma mark - PluginController

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

+ (void)initObservers {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [[NSNotificationCenter defaultCenter] addObserverForName:kAuthenticationLogoutNotification object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *notification) {
            [[MoodleService sharedInstanceToRetain] deleteAllDownloadedMoodleFiles]; //removing all downloaded Moodle files
            [PCPersistenceManager deleteCacheForPluginName:@"moodle"];
            [[MainController publicController] requestLeavePlugin:@"Moodle"];
        }];
    });
}

#pragma mark - PluginControllerProtocol

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"MoodlePlugin", @"");
}

+ (NSString*)identifierName {
    return @"Moodle";
}

#pragma mark - UISplitViewControllerDelegate

- (BOOL)splitViewController:(UISplitViewController *)svc shouldHideViewController:(UIViewController *)vc inOrientation:(UIInterfaceOrientation)orientation {
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
