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

#import "IsAcademiaController.h"

#import "IsAcademiaService.h"

#import "IsAcademiaDayScheduleViewController.h"

#import "IsAcademiaGradesViewController.h"


static IsAcademiaController* instance __weak = nil;

@interface IsAcademiaController ()<UITabBarControllerDelegate>

@property (nonatomic, strong) IsAcademiaService* moodleService;
@property (nonatomic, weak) UISegmentedControl* scheduleViewControllerSegmentedControl;
@property (nonatomic, weak) UISegmentedControl* gradesViewControllerSegmentedControl;

@end

@implementation IsAcademiaController

#pragma mark - Init

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"IsAcademiaController cannot be instancied more than once at a time, use sharedInstance instead." userInfo:nil];
        }
        self = [super init];
        if (self) {
            IsAcademiaDayScheduleViewController* scheduleViewController = [IsAcademiaDayScheduleViewController new];
            UISegmentedControl* scheduleViewControllerSegmentedControl = [self titleViewSegmentedControlInstance];
            self.scheduleViewControllerSegmentedControl = scheduleViewControllerSegmentedControl;
            scheduleViewController.navigationItem.titleView = scheduleViewControllerSegmentedControl;
            
            IsAcademiaGradesViewController* gradesViewController = [IsAcademiaGradesViewController new];
            UISegmentedControl* gradesViewControllerSegmentedControl = [self titleViewSegmentedControlInstance];
            self.gradesViewControllerSegmentedControl = gradesViewControllerSegmentedControl;
            gradesViewController.navigationItem.titleView = gradesViewControllerSegmentedControl;
            
            PluginTabBarController* tabBarController = [PluginTabBarController new];
            PCNavigationController* scheduleNavController = [[PCNavigationController alloc] initWithRootViewController:scheduleViewController];
            PCNavigationController* gradesNavController = [[PCNavigationController alloc] initWithRootViewController:gradesViewController];
            tabBarController.viewControllers = @[scheduleNavController, gradesNavController];
            tabBarController.pluginIdentifier = [[self class] identifierName];
            self.mainTabBarController = tabBarController;
            
            // Restore last selected tab
            self.scheduleViewControllerSegmentedControl.selectedSegmentIndex = [self lastSelectedSegmentedIndex];
            [self titleViewSegmentedControlValueChanged:self.scheduleViewControllerSegmentedControl]; // Segement programatically changed so need to call this
            
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
        return [[[self class] alloc] init];
    }
}

+ (void)initObservers {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [[NSNotificationCenter defaultCenter] addObserverForName:kAuthenticationLogoutNotification object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *notification) {
            [PCPersistenceManager deleteCacheForPluginName:@"isacademia"];
            [[MainController publicController] requestLeavePlugin:@"IsAcademia"];
        }];
    });
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"IsAcademiaPlugin", nil);
}

+ (NSString*)identifierName {
    return @"IsAcademia";
}

#pragma mark - UITabBarControllerDelegate

#pragma mark - Private

static NSInteger const kScheduleSegmentIndex = 0;

- (UISegmentedControl*)titleViewSegmentedControlInstance {
    UISegmentedControl* segmentedControl = [[UISegmentedControl alloc] initWithItems:@[NSLocalizedStringFromTable(@"Schedule", @"IsAcademiaPlugin", nil), NSLocalizedStringFromTable(@"Grades", @"IsAcademiaPlugin", nil)]];
    segmentedControl.selectedSegmentIndex = kScheduleSegmentIndex;
    segmentedControl.tintColor = [UIColor clearColor];
    [segmentedControl setTitleTextAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:17.0], NSForegroundColorAttributeName:[PCValues pocketCampusRed]} forState:UIControlStateNormal];
    [segmentedControl setTitleTextAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:17.0], NSForegroundColorAttributeName:[[PCValues pocketCampusRed] colorWithAlphaComponent:0.2]} forState:UIControlStateHighlighted];
    [segmentedControl setTitleTextAttributes:@{NSFontAttributeName:[UIFont boldSystemFontOfSize:17.0], NSForegroundColorAttributeName:[UIColor blackColor]} forState:UIControlStateSelected];
    [segmentedControl addTarget:self action:@selector(titleViewSegmentedControlValueChanged:) forControlEvents:UIControlEventValueChanged];
    return segmentedControl;
}

- (void)titleViewSegmentedControlValueChanged:(UISegmentedControl*)segmentedControl {
    self.mainTabBarController.selectedIndex = segmentedControl.selectedSegmentIndex;
    self.scheduleViewControllerSegmentedControl.selectedSegmentIndex = segmentedControl.selectedSegmentIndex;
    self.gradesViewControllerSegmentedControl.selectedSegmentIndex = segmentedControl.selectedSegmentIndex;
    [self saveSelectedSegmentedIndex:segmentedControl.selectedSegmentIndex];
}

static NSString* const kLastSelectedSegmentedIndexIntegerKey = @"IsAcademiaControllerLastSelectedSegmentedIndexInteger";

- (NSInteger)lastSelectedSegmentedIndex {
    return [[PCPersistenceManager userDefaultsForPluginName:@"isacademia"] integerForKey:kLastSelectedSegmentedIndexIntegerKey];
}

- (void)saveSelectedSegmentedIndex:(NSInteger)index {
    [[PCPersistenceManager userDefaultsForPluginName:@"isacademia"] setInteger:index forKey:kLastSelectedSegmentedIndexIntegerKey];
}

#pragma mark - Dealloc

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
}

@end
