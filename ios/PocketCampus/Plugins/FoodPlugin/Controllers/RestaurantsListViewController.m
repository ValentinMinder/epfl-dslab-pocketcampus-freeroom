//
//  RestaurantsListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 08.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "RestaurantsListViewController.h"

#import "MealCell.h"

#import "PCUtils.h"

#import "PCValues.h"

#import "PCTableViewSectionHeader.h"

#import "ObjectArchiver.h"

#import "FoodService.h"

#import "MenusListViewController.h"

#import "PCCenterMessageCell.h"

#import "NSDate+Addtions.h"

static NSString* kLastRefreshDateKey = @"lastRefreshDate";

/*
 * Will refresh if last refresh date is not same date as current OR older than kRefreshValiditySeconds ago
 * Important to refresh often, otherwise ratings are not updated. Background update of ratings should be
 * considered in a future update.
 */
static const NSTimeInterval kRefreshValiditySeconds = 300.0; //5 min.

@interface RestaurantsListViewController ()<FoodServiceDelegate> 

@property (nonatomic, strong) FoodService* foodService;
@property (nonatomic, strong) NSArray* meals; //Array of Meal as returned by FoodService
@property (nonatomic, strong) NSArray* restaurants; //Array of Restaurant for which menus are available
@property (nonatomic, strong) NSDictionary* restaurantsAndMeals; //key : restaurant name, value : NSArray of corresponding meals

@property (nonatomic, strong) LGRefreshControl* lgRefreshControl;

@end

@implementation RestaurantsListViewController

- (id)init
{
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        self.foodService = [FoodService sharedInstanceToRetain];
        self.meals = [self.foodService getFromCacheMeals];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshIfNeeded) name:UIApplicationDidBecomeActiveNotification object:[UIApplication sharedApplication]];
    self.lgRefreshControl = [[LGRefreshControl alloc] initWithTableViewController:self refreshedDataIdentifier:[LGRefreshControl dataIdentifierForPluginName:@"food" dataName:@"restaurantsAndMeals"]];
    [self.lgRefreshControl setTarget:self selector:@selector(refresh)];
}

- (void)viewWillAppear:(BOOL)animated  {
    [super viewWillAppear:animated];
    [[PCGAITracker sharedTracker] trackScreenWithName:@"/food"];
    [self refreshIfNeeded];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

#pragma  mark - Refresh and data

- (void)refreshIfNeeded {
    if (!self.meals.count || [self.lgRefreshControl shouldRefreshDataForValidity:kRefreshValiditySeconds] || ![[NSDate date] isSameDayAsDate:self.lgRefreshControl.lastSuccessfulRefreshDate]) {
        if (self.navigationController.topViewController != self) {
            [self.navigationController popToViewController:self animated:NO];
        }
        [self refresh];
    }
    [self fillCollectionsAndReloadTableView];
}

- (void)refresh {
    [self.foodService cancelOperationsForDelegate:self];
    [self.lgRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"LoadingMenus", @"FoodPlugin", nil)];
    [self.foodService getMealsWithDelegate:self];
}
- (void)fillCollectionsAndReloadTableView {
    [self fillCollections];
    [self.tableView reloadData];
    [self reselectLastSelectedItem]; //keep selection ater refresh on iPad
}

- (void)fillCollections {
    if (!self.meals) {
        self.restaurantsAndMeals = nil;
        self.restaurants = nil;
        return;
    }
    
    NSMutableDictionary* temp = [NSMutableDictionary dictionary];
    NSMutableSet* tempRestaurantsSet = [NSMutableSet set];
    for (Meal* meal in self.meals) {
        if ([temp objectForKey:meal.restaurant.name] == nil) {
            [tempRestaurantsSet addObject:meal.restaurant];
        }
        
        NSMutableArray* mealsOfRestaurant = [temp objectForKey:meal.restaurant.name];
        if (mealsOfRestaurant == nil) {
            [temp setObject:[NSMutableArray arrayWithObject:meal] forKey:meal.restaurant.name];
        } else {
            [mealsOfRestaurant addObject:meal];
        }
    }
    self.restaurantsAndMeals = [NSDictionary dictionaryWithDictionary:temp]; //creates non-mutable copy
    self.restaurants = [[tempRestaurantsSet allObjects] sortedArrayUsingComparator:^(Restaurant* rest1, Restaurant* rest2) {
        return [rest1.name localizedCaseInsensitiveCompare:rest2.name];
    }];
}

- (void)reselectLastSelectedItem {
#warning TODO
}

#pragma mark - FoodServiceDelegate

- (void)getMealsDidReturn:(NSArray*)meals {
    self.meals = meals;
    [self fillCollectionsAndReloadTableView];
    [self.lgRefreshControl endRefreshingAndMarkSuccessful];
}

- (void)getMealsFailed {
    [PCUtils showServerErrorAlert];
    [self.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"ServerErrorShort", @"PocketCampus", nil)];
}

- (void)serviceConnectionToServerTimedOut {
    [PCUtils showConnectionToServerTimedOutAlert];
    [self.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutShort", @"PocketCampus", nil)];
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return [PCTableViewSectionHeader preferredHeight];
}

- (UIView *)tableView:(UITableView *)tableView_ viewForHeaderInSection:(NSInteger)section {
    NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
    
    NSString* dateString = [dateFormatter stringFromDate:[NSDate date]]; //now
    
    dateString = [NSString stringWithFormat:NSLocalizedStringFromTable(@"MenusForTodayWithFormat", @"FoodPlugin", nil), dateString];
    
    PCTableViewSectionHeader* sectionHeader = [[PCTableViewSectionHeader alloc] initWithSectionTitle:dateString tableView:self.tableView];
    return sectionHeader;
    
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (!self.restaurants.count) {
        return;
    }
    Restaurant* restaurant = self.restaurants[indexPath.row];
    MenusListViewController* controller = [[MenusListViewController alloc] initWithRestaurantName:restaurant.name andMeals:self.restaurantsAndMeals[restaurant.name]]; //must not give a copy but current reference, so that rating can be updated on this instance directly
    [self.navigationController pushViewController:controller animated:YES];
}

#pragma mark - UITableViewDataSource

/*- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    if (!self.restaurants.count) {
        return nil;
    }
    NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
    NSString* dateString = [dateFormatter stringFromDate:[NSDate date]]; //now
    return [NSString stringWithFormat:NSLocalizedStringFromTable(@"MenusForTodayWithFormat", @"FoodPlugin", nil), dateString];
}*/

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (self.restaurants && self.restaurants.count == 0) {
        if (indexPath.row == 1) {
            return [[PCCenterMessageCell alloc] initWithMessage:NSLocalizedStringFromTable(@"NoMealsToday", @"FoodPlugin", nil)];
        } else {
            UITableViewCell* cell =[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            return cell;
        }
    }
    
    Restaurant* restaurant = self.restaurants[indexPath.row];
    static NSString* kRestaurantCellIdentifier = @"RestaurantCell";
    UITableViewCell* cell = [self.tableView dequeueReusableCellWithIdentifier:kRestaurantCellIdentifier];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kRestaurantCellIdentifier];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        cell.textLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleBody];
    }
    cell.textLabel.text = restaurant.name;
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (!self.restaurants) {
        return 0;
    }
    if (self.restaurants.count == 0) {
        return 2; //no restaurant message in second cell
    }
    return self.restaurants.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

#pragma mark - dealloc

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [self.foodService cancelOperationsForDelegate:self];
}

@end
