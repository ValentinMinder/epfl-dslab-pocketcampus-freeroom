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

//  Created by Loïc Gardiol on 30.10.13.

#import "FoodRestaurantViewController.h"
#import "FoodService.h"
#import "FoodRestaurantInfoCell.h"
#import "FoodMealCell.h"
#import "MapController.h"

static const NSInteger kRestaurantInfoSection = 0;
static const NSInteger kMealsSection = 1;

@interface FoodRestaurantViewController ()

@property (nonatomic, strong) FoodService* foodService;
@property (nonatomic, strong) FoodRestaurantInfoCell* restaurantInfoCell;
@property (nonatomic, strong) UITableViewCell* showOnMapCell;
@property (nonatomic, strong) NSMutableDictionary* cellForMealId; //key: NSNumber of EpflMeal.mId

@end

@implementation FoodRestaurantViewController

#pragma mark - Init

- (instancetype)initWithEpflRestaurant:(EpflRestaurant*)restaurant;
{
    [PCUtils throwExceptionIfObject:restaurant notKindOfClass:[EpflRestaurant class]];
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        self.gaiScreenName = @"/food/restaurant";
        self.foodService = [FoodService sharedInstanceToRetain];
        _restaurant = restaurant;
        self.title = self.restaurant.rName;
        self.cellForMealId = [NSMutableDictionary dictionaryWithCapacity:self.restaurant.rUniqueMeals.count];
//#warning TO REMOVE
        //self.restaurant.rPictureUrl = @"http://pocketcampus.epfl.ch/backend/restaurant-pics/vallotton.png";
        //self.restaurant.rRating.ratingValue = 0.76;
        //self.restaurant.rRating.voteCount = 578;
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
    PCTableViewAdditions* tableViewAdditions = [PCTableViewAdditions new];
    self.tableView = tableViewAdditions;
    __weak __typeof(self) welf = self;
    tableViewAdditions.contentSizeCategoryDidChangeBlock = ^(PCTableViewAdditions* tableView) {
        [welf.cellForMealId removeAllObjects];
    };
    [self refreshFavoriteButton];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshFavoriteButton) name:kFoodFavoritesRestaurantsUpdatedNotification object:self.foodService];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
}

- (NSUInteger)supportedInterfaceOrientations {
    return [PCUtils isIdiomPad] ? UIInterfaceOrientationMaskAll : UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Public properties

- (void)setRestaurant:(EpflRestaurant *)restaurant {
    _restaurant = restaurant;
    self.restaurantInfoCell = nil,
    [self.cellForMealId removeAllObjects];
    [self.tableView reloadData];
}

#pragma mark - Actions

- (void)refreshFavoriteButton {
    BOOL isFavorite = [self.foodService isRestaurantFavorite:self.restaurant];
    UIImage* image = [PCValues imageForFavoriteNavBarButtonLandscapePhone:NO glow:isFavorite];
    UIBarButtonItem* button = [[UIBarButtonItem alloc] initWithImage:image style:UIBarButtonItemStylePlain target:self action:@selector(favoritePressed)];
    button.accessibilityLabel = isFavorite ? NSLocalizedStringFromTable(@"RemoveRestaurantFromFavorites", @"FoodPlugin", nil) : NSLocalizedStringFromTable(@"AddRestaurantToFavorites", @"FoodPlugin", nil);
    self.navigationItem.rightBarButtonItem = button;
}

- (void)favoritePressed {
    if ([self.foodService isRestaurantFavorite:self.restaurant]) {
        [self trackAction:PCGAITrackerActionUnmarkFavorite contentInfo:self.restaurant.rName];
        [self.foodService removeFavoritRestaurant:self.restaurant];
    } else {
        [self trackAction:PCGAITrackerActionMarkFavorite contentInfo:self.restaurant.rName];
        [self.foodService addFavoriteRestaurant:self.restaurant];
    }
}

- (void)showOnMapPressed {
    [self trackAction:@"ViewRestaurantOnMap" contentInfo:self.restaurant.rName];
    UIViewController* mapViewController = [MapController viewControllerWithInitialMapItem:self.restaurant.rLocation];
    [self.navigationController pushViewController:mapViewController animated:YES];
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case kRestaurantInfoSection:
            return [FoodRestaurantInfoCell preferredHeightForRestaurant:self.restaurant];
        case kMealsSection:
        {
            EpflMeal* meal = self.restaurant.rUniqueMeals[indexPath.row];
            return [FoodMealCell preferredHeightForMeal:meal inTableView:tableView];
        }
    }
    return 0.0;
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell = nil;
    switch (indexPath.section) {
        case kRestaurantInfoSection:
            if (!self.restaurantInfoCell) {
                self.restaurantInfoCell = [[FoodRestaurantInfoCell alloc] initWithEpflRestaurant:self.restaurant];
                if (self.restaurant.rLocation) {
                    [self.restaurantInfoCell.showOnMapButton addTarget:self action:@selector(showOnMapPressed) forControlEvents:UIControlEventTouchUpInside];
                } else {
                    self.restaurantInfoCell.showOnMapButton.hidden = YES;
                }
                self.restaurantInfoCell.showRating = [[PCConfig defaults] boolForKey:PC_CONFIG_FOOD_RATINGS_ENABLED];
            }
            cell = self.restaurantInfoCell;
            break;
        case kMealsSection:
        {
            EpflMeal* meal = self.restaurant.rUniqueMeals[indexPath.row];
            NSNumber* nsMealId = [NSNumber numberWithLongLong:(long long)meal.mId];
            FoodMealCell* mealCell = self.cellForMealId[nsMealId];
/*#warning TO REMOVE
            if (indexPath.row > 1) {
                meal.mRating.voteCount = 6;
                meal.mRating.ratingValue = 0.76;
            }
#warning END OF TO REMOVE*/
            if (!mealCell) {
                mealCell = [[FoodMealCell alloc] initWithReuseIdentifier:nil];
                mealCell.meal = meal;
                mealCell.screenNameForGoogleAnalytics = self.gaiScreenName;
                self.cellForMealId[nsMealId] = mealCell;
            }
            cell = mealCell;
            break;
        }
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case kRestaurantInfoSection:
            if (![[PCConfig defaults] boolForKey:PC_CONFIG_FOOD_RATINGS_ENABLED] && !self.restaurant.rLocation) {
                return 0;
            }
            return 1;
        case kMealsSection:
            return self.restaurant.rUniqueMeals.count;
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (!self.restaurant) {
        return 0;
    }
    return 2; //retaurant info + meals
}

#pragma mark - Dealloc

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
