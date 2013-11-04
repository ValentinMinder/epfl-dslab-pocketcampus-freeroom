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
static const NSInteger kShowOnMapSection = 1;
static const NSInteger kMealsSection = 2;

@interface FoodRestaurantViewController ()

@property (nonatomic, strong) FoodService* foodService;
@property (nonatomic, strong) EpflRestaurant* restaurant;
@property (nonatomic, strong) FoodRestaurantInfoCell* restaurantInfoCell;
@property (nonatomic, strong) UITableViewCell* showOnMapCell;
@property (nonatomic, strong) NSMutableDictionary* cellForMealName;


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
        self.cellForMealName = [NSMutableDictionary dictionaryWithCapacity:self.restaurant.rMeals.count];
#warning TO REMOVE
        self.restaurant.rPictureUrl = @"http://pocketcampus.epfl.ch/backend/restaurant-pics/vallotton.png";
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
        case kShowOnMapSection:
            return 30.0;
        case kMealsSection:
        {
            EpflMeal* meal = self.restaurant.rMeals[indexPath.row];
            return [FoodMealCell preferredHeightForMeal:meal];
        }
    }
    return 0.0;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case kShowOnMapSection:
        {
            UIViewController* mapViewController = [MapController viewControllerWithInitialMapItem:self.restaurant.rLocation];
            [self.navigationController pushViewController:mapViewController animated:YES];
            break;
        }
        default:
            [self.tableView deselectRowAtIndexPath:indexPath animated:NO]; //no selectable
            break;
    }
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell = nil;
    switch (indexPath.section) {
        case kRestaurantInfoSection:
            if (!self.restaurantInfoCell) {
                self.restaurantInfoCell = [[FoodRestaurantInfoCell alloc] initWithEpflRestaurant:self.restaurant];
                self.restaurantInfoCell.showRating = NO;
            }
            cell = self.restaurantInfoCell;
            break;
        case kShowOnMapSection:
        {
            if (!self.showOnMapCell) {
                self.showOnMapCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
                //self.showOnMapCell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
                self.showOnMapCell.textLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleFootnote];
                self.showOnMapCell.textLabel.textColor = [PCValues pocketCampusRed];
                self.showOnMapCell.textLabel.textAlignment = NSTextAlignmentCenter;
                self.showOnMapCell.textLabel.text = [NSString stringWithFormat:@"%@ ❯", NSLocalizedStringFromTable(@"ShowOnMap", @"FoodPlugin", nil)];
                self.showOnMapCell.separatorInset = UIEdgeInsetsZero;
            }
            cell = self.showOnMapCell;
            break;
        }
        case kMealsSection:
        {
            EpflMeal* meal = self.restaurant.rMeals[indexPath.row];
            FoodMealCell* mealCell = self.cellForMealName[meal.mName];
            if (!mealCell) {
                mealCell = [[FoodMealCell alloc] initWithReuseIdentifier:nil];
                mealCell.meal = meal;
                self.cellForMealName[meal.mName] = mealCell;
            }
#warning TO REMOVE
            if (indexPath.row > 1) {
                meal.mRating.voteCount = 6;
                meal.mRating.ratingValue = 0.76;
            }
#warning END OF TO REMOVE
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
        case kShowOnMapSection:
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
    return 3; //retaurant info + show on map + meals
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
