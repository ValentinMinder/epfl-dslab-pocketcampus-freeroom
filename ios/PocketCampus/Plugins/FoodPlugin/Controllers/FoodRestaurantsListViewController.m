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

//  Created by Loïc Gardiol on 08.03.12.

#import "FoodRestaurantsListViewController.h"

#import "FoodRestaurantViewController.h"

#import "PCUtils.h"

#import "PCValues.h"

#import "PCTableViewSectionHeader.h"

#import "PCPersistenceManager.h"

#import "FoodService.h"

#import "PCCenterMessageCell.h"

#import "NSDate+Addtions.h"

#import "PCTableViewCellAdditions.h"

#import "AuthenticationService.h"

#import "FoodMealCell.h"

static NSString* const kLastRefreshDateKey = @"lastRefreshDate";

/*
 * Will refresh if last refresh date is not same date as current OR older than kRefreshValiditySeconds ago
 * Important to refresh often, otherwise ratings are not updated. Background update of ratings should be
 * considered in a future update.
 */
static const NSTimeInterval kRefreshValiditySeconds = 300.0; //5 min.

@interface FoodRestaurantsListViewController ()<FoodServiceDelegate> 

@property (nonatomic, strong) FoodService* foodService;
@property (nonatomic, strong) FoodResponse* foodResponse;
@property (nonatomic, strong) NSArray* restaurantsSorted; //sorted first by favorite on top, then by name
@property (nonatomic, strong) LGRefreshControl* lgRefreshControl;
@property (nonatomic, strong) EpflRestaurant* selectedRestaurant;

@property (nonatomic, weak) FoodRestaurantViewController* restaurantViewController;

@end

@implementation FoodRestaurantsListViewController

- (id)init
{
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        self.gaiScreenName = @"/food";
        self.foodService = [FoodService sharedInstanceToRetain];
        self.foodResponse = [self.foodService getFoodFromCacheForRequest:[self createFoodRequest]];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    PCTableViewAdditions* tableViewAdditions = [[PCTableViewAdditions alloc] init];
    self.tableView = tableViewAdditions;
    tableViewAdditions.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return [PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleDefault];
    };
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshIfNeeded) name:UIApplicationDidBecomeActiveNotification object:[UIApplication sharedApplication]];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(fillCollectionsAndReloadTableView) name:kFoodFavoritesRestaurantsUpdatedNotification object:self.foodService];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refresh) name:kFoodMealCellUserSuccessfullyRatedMealNotification object:nil];
    self.lgRefreshControl = [[LGRefreshControl alloc] initWithTableViewController:self refreshedDataIdentifier:[LGRefreshControl dataIdentifierForPluginName:@"food" dataName:@"restaurantsAndMeals"]];
    [self.lgRefreshControl setTarget:self selector:@selector(refresh)];
}

- (void)viewWillAppear:(BOOL)animated  {
    [super viewWillAppear:animated];
    [self trackScreen];
    [self refreshIfNeeded];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

#pragma  mark - Refresh and data

- (FoodRequest*)createFoodRequest {
    FoodRequest* req = [FoodRequest new];
    req.deviceLanguage = [PCUtils userLanguageCode];
    req.mealTime = MealTime_LUNCH;
    req.mealDate = -1; //now
    req.userGaspar = [AuthenticationService savedUsername];
    return req;
}

- (void)refreshIfNeeded {
    if (!self.foodResponse || [self.lgRefreshControl shouldRefreshDataForValidity:kRefreshValiditySeconds] || ![[NSDate date] isSameDayAsDate:self.lgRefreshControl.lastSuccessfulRefreshDate]) {
        if (!self.splitViewController && self.navigationController.topViewController != self) {
            [self.navigationController popToViewController:self animated:NO];
        }
        [self refresh];
    }
    [self fillCollectionsAndReloadTableView];
}

- (void)refresh {
    [self.foodService cancelOperationsForDelegate:self];
    [self.lgRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"LoadingMenus", @"FoodPlugin", nil)];
    [self.foodService getFoodForRequest:[self createFoodRequest] delegate:self];
}

- (void)fillCollectionsAndReloadTableView {
    [self fillCollections];
    [self.tableView reloadData];
    [self reselectLastSelectedItem]; //keep selection ater refresh on iPad
}

- (void)fillCollections {
    if (!self.foodResponse) {
        self.restaurantsSorted = nil;
        return;
    }
    self.restaurantsSorted = [self.foodResponse.menu sortedArrayUsingSelector:@selector(compareToEpflRestaurant:)]; //defined in Additions category on EpflRestaurant
}

- (void)reselectLastSelectedItem {
    if (!self.selectedRestaurant) {
        return;
    }
    BOOL found __block = NO;
    [self.restaurantsSorted enumerateObjectsUsingBlock:^(EpflRestaurant* restaurant, NSUInteger index, BOOL *stop) {
        if ([restaurant isEqual:self.selectedRestaurant]) {
            [self.tableView selectRowAtIndexPath:[NSIndexPath indexPathForRow:index inSection:0] animated:NO scrollPosition:UITableViewScrollPositionNone];
            self.selectedRestaurant = restaurant;
            *stop = YES;
            found = YES;
        }
    }];
    if (!found) {
        self.selectedRestaurant = nil;
    }
}

#pragma mark - FoodServiceDelegate

- (void)getFoodForRequest:(FoodRequest *)request didReturn:(FoodResponse *)response {
    switch (response.statusCode) {
        case FoodStatusCode_OK:
            self.foodResponse = response;
            [self fillCollectionsAndReloadTableView];
            [self.lgRefreshControl endRefreshingAndMarkSuccessful];
            if (self.restaurantViewController) {
                NSInteger index = [self.foodResponse.menu indexOfObject:self.restaurantViewController.restaurant];
                if (index != NSNotFound) {
                    EpflRestaurant* restaurant = self.foodResponse.menu[index];
                    self.restaurantViewController.restaurant = restaurant;
                }
            }
            break;
        default:
            [self getFoodFailedForRequest:request];
            break;
    }
    
}

- (void)getFoodFailedForRequest:(FoodRequest *)request {
    [PCUtils showServerErrorAlert];
    [self.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"ServerErrorShort", @"PocketCampus", nil)];
}

- (void)serviceConnectionToServerFailed {
    [PCUtils showConnectionToServerTimedOutAlert];
    [self.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutShort", @"PocketCampus", nil)];
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (!self.restaurantsSorted.count) {
        return 0.0;
    }
    return [PCTableViewSectionHeader preferredHeight];
}

- (UIView *)tableView:(UITableView *)tableView_ viewForHeaderInSection:(NSInteger)section {
    if (!self.restaurantsSorted.count) {
        return nil;
    }
    
    static NSDateFormatter* dateFormatter = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        dateFormatter = [NSDateFormatter new];
        [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
    });
    
    NSString* dateString = [dateFormatter stringFromDate:[NSDate date]]; //now
    
    dateString = [NSString stringWithFormat:NSLocalizedStringFromTable(@"MenusForTodayWithFormat", @"FoodPlugin", nil), dateString];
    
    PCTableViewSectionHeader* sectionHeader = [[PCTableViewSectionHeader alloc] initWithSectionTitle:dateString tableView:self.tableView];
    return sectionHeader;
    
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (!self.restaurantsSorted.count) {
        return;
    }
    EpflRestaurant* restaurant = self.restaurantsSorted[indexPath.row];
    if (self.splitViewController && [restaurant isEqual:self.selectedRestaurant]) {
        UINavigationController* navController = [self.splitViewController.viewControllers[1] isKindOfClass:[UINavigationController class]] ? self.splitViewController.viewControllers[1] : nil;
        [navController popToRootViewControllerAnimated:YES];
        return;
    }
    FoodRestaurantViewController* viewController = [[FoodRestaurantViewController alloc] initWithEpflRestaurant:restaurant];
    self.restaurantViewController = viewController;
    if (self.splitViewController) {
        self.selectedRestaurant = restaurant;
        PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:self.restaurantViewController];
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], navController];
    } else {
        [self.navigationController pushViewController:self.restaurantViewController animated:YES];
    }
    [self trackAction:@"ShowRestaurant" contentInfo:restaurant.rName];
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

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (self.restaurantsSorted && self.restaurantsSorted.count == 0) {
        if (indexPath.row == 1) {
            return [[PCCenterMessageCell alloc] initWithMessage:NSLocalizedStringFromTable(@"NoMealsToday", @"FoodPlugin", nil)];
        } else {
            UITableViewCell* cell =[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            return cell;
        }
    }
    
    EpflRestaurant* restaurant = self.restaurantsSorted[indexPath.row];
    NSString* const identifier = [(PCTableViewAdditions*)tableView autoInvalidatingReuseIdentifierForIdentifier:@"RestaurantCell"];
    PCTableViewCellAdditions* cell = [self.tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[PCTableViewCellAdditions alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
        cell.accessoryType = [PCUtils isIdiomPad] ? UITableViewCellAccessoryNone : UITableViewCellAccessoryDisclosureIndicator;
        cell.textLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultTextLabelTextStyle];
    }
    cell.textLabel.text = restaurant.rName;
    cell.favoriteIndicationVisible = [self.foodService isRestaurantFavorite:restaurant];
    cell.accessibilityHint = NSLocalizedStringFromTable(@"ShowsMenuForThisRestaurant", @"FoodPlugin", nil);
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (!self.restaurantsSorted) {
        return 0;
    }
    if (self.restaurantsSorted.count == 0) {
        return 2; //no restaurant message in second cell
    }
    return self.restaurantsSorted.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

#pragma mark - dealloc

- (void)dealloc {
    [self.foodService cancelOperationsForDelegate:self];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
