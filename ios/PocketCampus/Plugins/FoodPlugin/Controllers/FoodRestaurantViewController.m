//
//  FoodRestaurantViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 30.10.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "FoodRestaurantViewController.h"
#import "FoodService.h"
#import "FoodRestaurantInfoCell.h"
#import "FoodMealCell.h"
#import "MapController.h"

static const NSInteger kRestaurantInfoSection = 0;
static const NSInteger kMealsSection = 1;

@interface FoodRestaurantViewController ()

@property (nonatomic, strong) FoodService* foodService;
@property (nonatomic, strong) EpflRestaurant* restaurant;
@property (nonatomic, strong) FoodRestaurantInfoCell* restaurantInfoCell;


@end

@implementation FoodRestaurantViewController

#pragma mark - Init

- (instancetype)initWithEpflRestaurant:(EpflRestaurant*)restaurant;
{
    [PCUtils throwExceptionIfObject:restaurant notKindOfClass:[EpflRestaurant class]];
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        self.foodService = [FoodService sharedInstanceToRetain];
        self.restaurant = restaurant;
        self.title = self.restaurant.rName;
#warning TO REMOVE
        self.restaurant.rPictureUrl = @"http://pocketcampus.epfl.ch/backend/restaurants-pics/vallotton.png";
        self.restaurant.rRating.ratingValue = 0.28;
        self.restaurant.rRating.voteCount = 578;
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self refreshFavoriteButton];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshFavoriteButton) name:kFavoritesRestaurantsUpdatedNotificationName object:self.foodService];
}

- (NSUInteger)supportedInterfaceOrientations {
    return [PCUtils isIdiomPad] ? UIInterfaceOrientationMaskAll : UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Actions

- (void)refreshFavoriteButton {
    UIImage* image = [UIImage imageNamed:[self.foodService isRestaurantFavorite:self.restaurant] ? @"FavoriteGlowNavBarButton" : @"FavoriteNavBarButton"];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithImage:image style:UIBarButtonItemStylePlain target:self action:@selector(favoritePressed)];
}

- (void)favoritePressed {
    if ([self.foodService isRestaurantFavorite:self.restaurant]) {
        [self.foodService removeFavoritRestaurant:self.restaurant];
    } else {
        [self.foodService addFavoriteRestaurant:self.restaurant];
    }
}


#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case kRestaurantInfoSection:
            return [FoodRestaurantInfoCell preferredHeight];
        case kMealsSection:
        {
            EpflMeal* meal = self.restaurant.rMeals[indexPath.row];
            return [FoodMealCell preferredHeightForMeal:meal];
        }
    }
    return 0.0;
}

/*- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
}*/

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell = nil;
    switch (indexPath.section) {
        case kRestaurantInfoSection:
            if (!self.restaurantInfoCell) {
                self.restaurantInfoCell = [[FoodRestaurantInfoCell alloc] initWithEpflRestaurant:self.restaurant];
                self.restaurantInfoCell.showRating = YES;
            }
            cell = self.restaurantInfoCell;
            break;
        case kMealsSection:
        {
            EpflMeal* meal = self.restaurant.rMeals[indexPath.row];
            static NSString* kMealCell = @"MealCell";
            FoodMealCell* mealCell = [self.tableView dequeueReusableCellWithIdentifier:kMealCell];
            if (!mealCell) {
                mealCell = [[FoodMealCell alloc] initWithReuseIdentifier:kMealCell];
            }
#warning TO REMOVE
            if (indexPath.row > 1) {
                meal.mRating.voteCount = 6;
                meal.mRating.ratingValue = 0.76;
            }
#warning END OF TO REMOVE
            mealCell.meal = meal;
            cell = mealCell;
            break;
        }
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case kRestaurantInfoSection:
            return 1;
        case kMealsSection:
            return self.restaurant.rMeals.count;
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
    @try {
        [[NSNotificationCenter defaultCenter] removeObserver:self];
    }
    @catch (NSException *exception) {}
}

@end
