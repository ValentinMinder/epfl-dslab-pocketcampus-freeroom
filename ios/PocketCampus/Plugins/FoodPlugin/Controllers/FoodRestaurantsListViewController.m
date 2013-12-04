//
//  RestaurantsListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 08.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "FoodRestaurantsListViewController.h"

#import "FoodRestaurantViewController.h"

#import "PCUtils.h"

#import "PCValues.h"

#import "PCTableViewSectionHeader.h"

#import "ObjectArchiver.h"

#import "FoodService.h"

#import "PCCenterMessageCell.h"

#import "NSDate+Addtions.h"

#import "PCTableViewCellAdditions.h"

#import "AuthenticationService.h"

#import "FoodMealCell.h"

static NSString* kLastRefreshDateKey = @"lastRefreshDate";

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
        self.foodService = [FoodService sharedInstanceToRetain];
        self.foodResponse = [self.foodService getFoodFromCacheForRequest:[self createFoodRequest]];
        if (self.foodResponse) {
            self.foodService.pictureUrlForMealType = self.foodResponse.mealTypePictureUrls; //see doc of self.foodService.pictureUrlForMealType
            self.foodService.userPriceTarget = self.foodResponse.userStatus; //see doc of self.foodService.userPriceTarget
        }
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshIfNeeded) name:UIApplicationDidBecomeActiveNotification object:[UIApplication sharedApplication]];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(fillCollectionsAndReloadTableView) name:kFavoritesRestaurantsUpdatedNotificationName object:self.foodService];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refresh) name:kFoodMealCellUserSuccessfullyRatedMealNotificationName object:nil];
    
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

- (FoodRequest*)createFoodRequest {
    FoodRequest* req = [FoodRequest new];
    req.deviceLanguage = [PCUtils userLanguageCode];
    req.mealTime = MealTime_LUNCH;
    req.mealDate = -1; //now
    req.deviceId = [[[UIDevice currentDevice] identifierForVendor] UUIDString];
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
            self.foodService.pictureUrlForMealType = response.mealTypePictureUrls; //see doc of self.foodService.pictureUrlForMealType
            self.foodService.userPriceTarget = response.userStatus; //see doc of self.foodService.userPriceTarget
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

- (void)serviceConnectionToServerTimedOut {
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
    
    NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
    
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
    static NSString* kRestaurantCellIdentifier = @"RestaurantCell";
    PCTableViewCellAdditions* cell = [self.tableView dequeueReusableCellWithIdentifier:kRestaurantCellIdentifier];
    if (!cell) {
        cell = [[PCTableViewCellAdditions alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kRestaurantCellIdentifier];
        cell.accessoryType = [PCUtils isIdiomPad] ? UITableViewCellAccessoryNone : UITableViewCellAccessoryDisclosureIndicator;
        cell.textLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleBody];
    }
    cell.textLabel.text = restaurant.rName;
    cell.favoriteIndicationVisible = [self.foodService isRestaurantFavorite:restaurant];
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
    @try {
        [[NSNotificationCenter defaultCenter] removeObserver:self];
    }
    @catch (NSException *exception) {}
    [self.foodService cancelOperationsForDelegate:self];
}

@end
