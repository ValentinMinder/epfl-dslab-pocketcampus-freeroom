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

//  Created by Loïc Gardiol on 04.11.14.

#import "FoodMealsByRestaurantViewController.h"

#import "FoodService.h"

#import "FoodMealCell.h"

#import "PCTableViewSectionHeader.h"

#import "FoodRestaurantViewController.h"

#import "PCCenterMessageCell.h"

@interface FoodMealsByRestaurantViewController ()

@property (nonatomic, readwrite, copy) NSArray* restaurants;
@property (nonatomic, readwrite, copy) BOOL (^shouldShowMealBlock)(EpflMeal* meal);

@property (nonatomic, strong) NSDictionary* filteredMealsForRestaurantId; //key: @(EpflRestaurant.rId), value: NSOrderedSet of EpflMeal

@property (nonatomic) BOOL existsMenus;

@property (nonatomic, weak) FoodRestaurantViewController* restaurantViewController;

@end

@implementation FoodMealsByRestaurantViewController

#pragma mark - Init

- (instancetype)init {
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        self.gaiScreenName = @"/food/menus";
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
}

- (NSUInteger)supportedInterfaceOrientations {
    return [PCUtils isIdiomPad] ? UIInterfaceOrientationMaskAll : UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Public

- (void)setRestaurants:(NSArray*)restaurants shouldShowMealBlock:(BOOL (^)(EpflMeal* meal))shouldShowMealBlock {
    self.restaurants = restaurants;
    self.shouldShowMealBlock = shouldShowMealBlock;
    [self fillCollectionsAndReloadTableView];
    if (self.restaurantViewController.restaurant) {
        NSUInteger index = [self.restaurants indexOfObject:self.restaurantViewController.restaurant];
        if (index != NSNotFound) {
            self.restaurantViewController.restaurant = self.restaurants[index];
        }
    }
}

#pragma mark - Private

- (void)fillCollectionsAndReloadTableView {
    if (!self.restaurants.count) {
        self.filteredMealsForRestaurantId = nil;
        self.existsMenus = NO;
        [self.tableView reloadData];
        return;
    }
    NSMutableDictionary* filteredMealsForRestaurantId = [NSMutableDictionary dictionaryWithCapacity:self.restaurants.count];
    for (EpflRestaurant* restaurant in self.restaurants) {
        if (self.shouldShowMealBlock) {
            NSMutableOrderedSet* filteredMeals = [NSMutableOrderedSet orderedSetWithCapacity:(restaurant.rMeals.count / 2)]; //wild guess
            for (EpflMeal* meal in restaurant.rMeals) {
                if (self.shouldShowMealBlock(meal)) {
                    [filteredMeals addObject:meal];
                }
            }
            filteredMealsForRestaurantId[@(restaurant.rId)] = filteredMeals;
        } else {
            filteredMealsForRestaurantId[@(restaurant.rId)] = restaurant.rUniqueMeals;
        }
        if (!self.existsMenus) {
            self.existsMenus = [filteredMealsForRestaurantId[@(restaurant.rId)] count] > 0;
        }
    }
    self.filteredMealsForRestaurantId = filteredMealsForRestaurantId;
    [self.tableView reloadData];
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (!self.existsMenus) {
        return 44.0;
    }
    EpflRestaurant* restaurant = self.restaurants[indexPath.section];
    EpflMeal* meal = self.filteredMealsForRestaurantId[@(restaurant.rId)][indexPath.row];
    return [FoodMealCell preferredHeightForMeal:meal inTableView:tableView];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (!self.existsMenus) {
        return 0.0;
    }
    EpflRestaurant* restaurant = self.restaurants[section];
    NSOrderedSet* filteredMeals = self.filteredMealsForRestaurantId[@(restaurant.rId)];
    if (!filteredMeals.count) {
        return 0.0;
    }
    return [PCTableViewSectionHeader preferredHeightWithInfoButton:YES]; //we want all section headers to be same height
}

- (UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    if (!self.existsMenus) {
        return nil;
    }
    EpflRestaurant* restaurant = self.restaurants[section];
    NSOrderedSet* filteredMeals = self.filteredMealsForRestaurantId[@(restaurant.rId)];
    if (!filteredMeals.count) {
        return nil;
    }
    PCTableViewSectionHeader* header = [[PCTableViewSectionHeader alloc] initWithSectionTitle:restaurant.rName tableView:tableView showInfoButton:YES];
    __weak __typeof(self) welf = self;
    [header setInfoButtonTappedBlock:^{
        [self trackAction:@"ShowRestaurant" contentInfo:restaurant.rName];
        FoodRestaurantViewController* viewController = [[FoodRestaurantViewController alloc] initWithEpflRestaurant:restaurant];
        [welf.navigationController pushViewController:viewController animated:YES];
        welf.restaurantViewController = viewController;
    }];
    return header;
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (!self.existsMenus) {
        if (indexPath.row == 1) {
            return [[PCCenterMessageCell alloc] initWithMessage:NSLocalizedStringFromTable(@"NoMeals", @"FoodPlugin", nil)];
        } else {
            UITableViewCell* cell =[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            return cell;
        }
    }
    EpflRestaurant* restaurant = self.restaurants[indexPath.section];
    EpflMeal* meal = self.filteredMealsForRestaurantId[@(restaurant.rId)][indexPath.row];
    static NSString* const identifier = @"FoodMealCell";
    FoodMealCell* cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[FoodMealCell alloc] initWithReuseIdentifier:identifier];
        cell.screenNameForGoogleAnalytics = self.gaiScreenName;
    }
    cell.meal = meal;
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (!self.existsMenus) {
        return 2;
    }
    EpflRestaurant* restaurant = self.restaurants[section];
    NSOrderedSet* filteredMeals = self.filteredMealsForRestaurantId[@(restaurant.rId)];
    return filteredMeals.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (!self.existsMenus) {
        return 1;
    }
    return self.restaurants.count;
}

@end
