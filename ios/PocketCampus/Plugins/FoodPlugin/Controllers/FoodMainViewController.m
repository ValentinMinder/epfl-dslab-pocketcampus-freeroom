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

#import "FoodMainViewController.h"

#import "FoodRestaurantViewController.h"

#import "FoodMealsByRestaurantViewController.h"

#import "PCTableViewSectionHeader.h"

#import "PCPersistenceManager.h"

#import "FoodService.h"

#import "PCCenterMessageCell.h"

#import "NSDate+Addtions.h"

#import "PCTableViewCellAdditions.h"

#import "AuthenticationService.h"

#import "FoodMealCell.h"

#import "FoodMealTypeCell.h"

#import "PCDatePickerView.h"

static NSString* const kLastRefreshDateKey = @"lastRefreshDate";

/*
 * Will refresh if last refresh date is not same day as current OR older than kRefreshValiditySeconds ago
 * Important to refresh often, otherwise ratings are not updated. Background update of ratings should be
 * considered in a future update.
 */

static NSTimeInterval const kRefreshValiditySeconds = 300.0; //5 min.

static NSInteger const kRestaurantsSegmentIndex = 0;
static NSInteger const kMealTypesSegmentIndex = 1;

static NSInteger const kMealTimeButtonIndex = 0;
static NSInteger const kMealDateButtonIndex = 1;

static NSString* const kMealTypeCellReuseIdentifier = @"MealTypeCell";

@interface FoodMainViewController ()<FoodServiceDelegate, UIActionSheetDelegate, UITableViewDelegate, UITableViewDataSource, UICollectionViewDataSource, UICollectionViewDelegate>

@property (nonatomic, strong) UITableViewController* restaurantsTableViewController;

@property (nonatomic, weak) IBOutlet PCTableViewAdditions* restaurantsTableView;
@property (nonatomic, weak) IBOutlet UICollectionView* mealTypesCollectionView;

@property (nonatomic, strong) UISegmentedControl* segmentedControl;

@property (nonatomic, strong) FoodService* foodService;
@property (nonatomic, strong) FoodResponse* foodResponse;
@property (nonatomic, strong) NSArray* restaurantsSorted; //sorted first by favorite on top, then by name
@property (nonatomic, strong) LGARefreshControl* lgRefreshControl;
@property (nonatomic, strong) EpflRestaurant* selectedRestaurant;
@property (nonatomic) NSInteger selectedMealType; //of enum type MealType

@property (nonatomic) NSInteger selectedMealTime; //of enum type MealTime
@property (nonatomic) NSDate* selectedMealDate;

@property (nonatomic, readonly) NSInteger lastSelectedSegmentIndex; //used to save state of selected segment index when leaving plugin

@property (nonatomic, weak) FoodRestaurantViewController* restaurantViewController;

@property (nonatomic, weak) FoodMealsByRestaurantViewController* mealsByRestaurantViewController;

@end

@implementation FoodMainViewController

#pragma mark - Init

- (instancetype)init
{
    self = [[[NSBundle mainBundle] loadNibNamed:NSStringFromClass(self.class) owner:nil options:nil] firstObject];
    if (self) {
        self.title = NSLocalizedStringFromTable(@"Menus", @"FoodPlugin", nil);
        self.gaiScreenName = @"/food";
        self.selectedMealTime = MealTime_LUNCH; //default
        self.foodService = [FoodService sharedInstanceToRetain];
        self.foodResponse = [self.foodService getFoodFromCacheForRequest:[self createFoodRequest]];
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    self.restaurantsTableViewController = [[UITableViewController alloc] initWithStyle:self.restaurantsTableView.style];
    self.restaurantsTableViewController.tableView = self.restaurantsTableView;
    [self addChildViewController:self.restaurantsTableViewController];
    self.lgRefreshControl = [[LGARefreshControl alloc] initWithTableViewController:self.restaurantsTableViewController refreshedDataIdentifier:[LGARefreshControl dataIdentifierForPluginName:@"food" dataName:@"restaurantsAndMeals"]];
    [self.lgRefreshControl setTarget:self selector:@selector(refresh)];
    
    self.restaurantsTableView.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return [PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleDefault];
    };
    
    NSArray* segmentedControlItems = @[NSLocalizedStringFromTable(@"ByRestaurant", @"FoodPlugin", nil), NSLocalizedStringFromTable(@"ByMealTypes", @"FoodPlugin", nil)];
    self.segmentedControl = [[UISegmentedControl alloc] initWithItems:segmentedControlItems];
    self.segmentedControl.selectedSegmentIndex = kRestaurantsSegmentIndex;
    [self.segmentedControl addTarget:self action:@selector(segmentedControlValueChanged) forControlEvents:UIControlEventValueChanged];
    UIBarButtonItem* segmentedControlBarItem = [[UIBarButtonItem alloc] initWithCustomView:self.segmentedControl];
    
    [self.segmentedControl addObserver:self forKeyPath:NSStringFromSelector(@selector(frame)) options:0 context:NULL];
    
    UIBarButtonItem* flexibleSpaceLeft = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
    UIBarButtonItem* flexibleSpaceRight = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
    self.toolbarItems = @[flexibleSpaceLeft, segmentedControlBarItem, flexibleSpaceRight];
    
    NSInteger lastSelectedSegmentedIndex = self.lastSelectedSegmentIndex;
    if (lastSelectedSegmentedIndex != kRestaurantsSegmentIndex && lastSelectedSegmentedIndex != kMealTypesSegmentIndex) {
        lastSelectedSegmentedIndex = 0; //in case segmented control has changed since last save
    }
    self.segmentedControl.selectedSegmentIndex = lastSelectedSegmentedIndex;
    
    [self.mealTypesCollectionView registerNib:[UINib nibWithNibName:NSStringFromClass([FoodMealTypeCell class]) bundle:nil] forCellWithReuseIdentifier:kMealTypeCellReuseIdentifier];
    UICollectionViewFlowLayout* layout = (UICollectionViewFlowLayout*)(self.mealTypesCollectionView.collectionViewLayout);
    layout.itemSize = [FoodMealTypeCell preferredSize];
    self.mealTypesCollectionView.collectionViewLayout = layout;
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"ClockBarButton"] style:UIBarButtonItemStylePlain target:self action:@selector(clockButtonTapped)];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshIfNeeded) name:UIApplicationDidBecomeActiveNotification object:[UIApplication sharedApplication]];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(fillCollectionsAndUpdateUI) name:kFoodFavoritesRestaurantsUpdatedNotification object:self.foodService];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refresh) name:kFoodMealCellUserSuccessfullyRatedMealNotification object:nil];
}

- (void)viewWillAppear:(BOOL)animated  {
    [super viewWillAppear:animated];
    [self trackScreen];
    [self refreshIfNeeded];
    [self.navigationController setToolbarHidden:NO animated:animated];
    self.mealTypesCollectionView.contentInset = self.mealTypesCollectionView.scrollIndicatorInsets = [PCUtils edgeInsetsForViewController:self];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.navigationController setToolbarHidden:YES animated:animated];
}

- (NSUInteger)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskPortrait;
}

#pragma  mark - Refresh and data

- (FoodRequest*)createFoodRequest {
    FoodRequest* req = [FoodRequest new];
    req.deviceLanguage = [PCUtils userLanguageCode];
    req.mealTime = (int)self.selectedMealTime;
    req.mealDate = self.selectedMealDate ? [self.selectedMealDate timeIntervalSince1970]*1000 : -1;
    req.userGaspar = [AuthenticationService savedUsername];
    return req;
}

- (void)refreshIfNeeded {
    if (!self.foodResponse || [self.lgRefreshControl shouldRefreshDataForValidity:kRefreshValiditySeconds] || ![[NSDate date] isSameDayAsDate:self.lgRefreshControl.lastSuccessfulRefreshDate]) {
        self.selectedMealDate = nil;
        self.selectedMealTime = MealTime_LUNCH; // default
        if (!self.splitViewController && self.navigationController.topViewController != self) {
            [self.navigationController popToViewController:self animated:NO];
        }
        [self refresh];
    }
    [self fillCollectionsAndUpdateUI];
}

- (void)refresh {
    [self.foodService cancelOperationsForDelegate:self];
    [self.lgRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"LoadingMenus", @"FoodPlugin", nil)];
    [self.foodService getFoodForRequest:[self createFoodRequest] delegate:self];
}

- (void)fillCollectionsAndUpdateUI {
    [self fillCollections];
    [self.restaurantsTableView reloadData];
    [self.mealTypesCollectionView reloadData];
    [self reselectLastSelectedItem]; //keep selection after refresh on iPad
    self.restaurantsTableView.hidden = (self.segmentedControl.selectedSegmentIndex != kRestaurantsSegmentIndex);
    self.mealTypesCollectionView.hidden = (self.segmentedControl.selectedSegmentIndex != kMealTypesSegmentIndex);
    
    NSString* timeString = nil;
    NSString* dateString = nil;
    if (self.selectedMealDate) {
        timeString = NSLocalizedStringFromTable(self.selectedMealTime == MealTime_LUNCH ? @"Lunch" : @"Dinner", @"FoodPlugin", nil);
        static NSDateFormatter* formatter = nil;
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            formatter = [NSDateFormatter new];
            formatter.doesRelativeDateFormatting = YES;
            formatter.dateStyle = NSDateFormatterShortStyle;
            formatter.timeStyle = NSDateFormatterNoStyle;
        });
        dateString = [formatter stringFromDate:self.selectedMealDate];
    } else {
        timeString = NSLocalizedStringFromTable(self.selectedMealTime == MealTime_LUNCH ? @"LunchMenus" : @"DinnerMenus", @"FoodPlugin", nil);
    }
    self.title = dateString ? [NSString stringWithFormat:@"%@ – %@", timeString, dateString] : timeString;
}

- (void)fillCollections {
    if (!self.foodResponse) {
        self.restaurantsSorted = nil;
        return;
    }
    self.restaurantsSorted = [self.foodResponse.menu sortedArrayUsingSelector:@selector(compareToEpflRestaurant:)]; //defined in Additions category on EpflRestaurant
}

- (void)reselectLastSelectedItem {
    if (self.selectedRestaurant) {
        BOOL found __block = NO;
        [self.restaurantsSorted enumerateObjectsUsingBlock:^(EpflRestaurant* restaurant, NSUInteger index, BOOL *stop) {
            if ([restaurant isEqual:self.selectedRestaurant]) {
                [self.restaurantsTableView selectRowAtIndexPath:[NSIndexPath indexPathForRow:index inSection:0] animated:NO scrollPosition:UITableViewScrollPositionNone];
                self.selectedRestaurant = restaurant;
                *stop = YES;
                found = YES;
            }
        }];
        if (!found) {
            self.selectedRestaurant = nil;
        }
    }
    
    if (self.selectedMealType != 0) {
        NSUInteger itemIndex = [[EpflMeal allMealTypes] indexOfObject:@(self.selectedMealType)];
        if (itemIndex != NSNotFound) {
            [self.mealTypesCollectionView selectItemAtIndexPath:[NSIndexPath indexPathForItem:itemIndex inSection:0] animated:NO scrollPosition:UICollectionViewScrollPositionNone];
        }
    }
}

#pragma mark - Actions

- (void)clockButtonTapped {
    
    NSString* mealTimeActionTitle = nil;
    NSInteger newMealTime = 0;
    NSString* newMealTimeGAAction = nil;
    if (self.selectedMealTime == MealTime_LUNCH) {
        mealTimeActionTitle = NSLocalizedStringFromTable(@"DinnerMenus", @"FoodPlugin", nil);
        newMealTime = MealTime_DINNER;
        newMealTimeGAAction = @"ViewDinner";
    } else {
        mealTimeActionTitle = NSLocalizedStringFromTable(@"LunchMenus", @"FoodPlugin", nil);
        newMealTime = MealTime_LUNCH;
        newMealTimeGAAction = @"ViewLunch";
    }
    
    if ([UIAlertController class]) {
        __weak __typeof(self) welf = self;
        UIAlertController* alertController = [UIAlertController alertControllerWithTitle:nil message:nil preferredStyle:UIAlertControllerStyleActionSheet];
        
        if (self.selectedMealTime != MealTime_LUNCH || self.selectedMealDate) {
            UIAlertAction* backToDefaultsActions = [UIAlertAction actionWithTitle:NSLocalizedStringFromTable(@"BackToTodayLunchMenus", @"FoodPlugin", nil) style:UIAlertActionStyleDestructive handler:^(UIAlertAction *action) {
                [welf trackAction:@"BackToTodayLunch"];
                welf.selectedMealDate = nil;
                welf.selectedMealTime = MealTime_LUNCH;
                [welf refresh];
            }];
            [alertController addAction:backToDefaultsActions];
        }
        
        UIAlertAction* mealTimeAction = [UIAlertAction actionWithTitle:mealTimeActionTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
            [welf trackAction:newMealTimeGAAction];
            welf.selectedMealTime = newMealTime;
            [welf refresh];
        }];
        [alertController addAction:mealTimeAction];
        
        UIAlertAction* mealDateAction = [UIAlertAction actionWithTitle:NSLocalizedStringFromTable(@"SeeMenusForAnotherDay", @"FoodPlugin", nil) style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
            [welf showMealDatePicker];
        }];
        [alertController addAction:mealDateAction];
        
        UIAlertAction* cancelAction = [UIAlertAction actionWithTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) style:UIAlertActionStyleCancel handler:NULL];
        [alertController addAction:cancelAction];
        
        alertController.popoverPresentationController.barButtonItem = self.navigationItem.rightBarButtonItem;
        
        [self presentViewController:alertController animated:YES completion:NULL];
        
    } else {
        UIActionSheet* actionSheet = [[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:nil otherButtonTitles:mealTimeActionTitle, NSLocalizedStringFromTable(@"SeeMenusForAnotherDay", @"FoodPlugin", nil), nil];
        [actionSheet showFromBarButtonItem:self.navigationItem.rightBarButtonItem animated:YES];
    }
}

- (void)segmentedControlValueChanged {
    switch (self.segmentedControl.selectedSegmentIndex) {
        case kRestaurantsSegmentIndex:
            [self trackAction:@"FilterByRestaurant"];
            break;
        case kMealTypesSegmentIndex:
            [self trackAction:@"FilterByIngredient"];
            break;
    }
    [self refreshIfNeeded];
    [self saveLastSelectedSegmentIndex];
}

#pragma mark - Private

- (void)showMealDatePicker {
    PCDatePickerView* pickerView = [[PCDatePickerView alloc] init];
    pickerView.datePicker.datePickerMode = UIDatePickerModeDate;
    __weak __typeof(self) welf = self;
    if (self.selectedMealDate) {
        pickerView.datePicker.date = self.selectedMealDate;
    }
    [pickerView setUserCancelledBlock:^(PCDatePickerView* view) {
        [view dismiss];
    }];
    [pickerView setUserValidatedDateBlock:^(PCDatePickerView* view, NSDate* date) {
        //GA stuff
        NSDateFormatter* formatter = [NSDateFormatter new];
        formatter.dateFormat = @"yyyy-MM-dd";
        NSString* dateString = [formatter stringFromDate:date];
        [welf trackAction:@"ViewDay" contentInfo:dateString];
        
        [view dismiss];
        if ([date isSameDayAsDate:[NSDate date]]) {
            welf.selectedMealDate = nil;
        } else {
            welf.selectedMealDate = date;
        }
        [welf refresh];
    }];
    if (![UIAlertController class]) {
        pickerView.showTodayButton = YES;
        [pickerView setUserTappedTodayBlock:^(PCDatePickerView* view) {
            [view dismiss];
            welf.selectedMealDate = nil;
            [welf refresh];
        }];
    }
    [pickerView presentFromBarButtonItem:self.navigationItem.rightBarButtonItem];
}

static NSString* const kLastSelectedSegmentedIndexKey = @"FoodMainViewControllerLastSelectedSegmentedIndex";

- (void)saveLastSelectedSegmentIndex {
    [[PCPersistenceManager userDefaultsForPluginName:@"food"] setInteger:self.segmentedControl.selectedSegmentIndex forKey:kLastSelectedSegmentedIndexKey];
}

- (NSInteger)lastSelectedSegmentIndex {
    return [[PCPersistenceManager userDefaultsForPluginName:@"food"] integerForKey:kLastSelectedSegmentedIndexKey];
}

#pragma mark - KVO

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    if (object == self.segmentedControl && [keyPath isEqualToString:NSStringFromSelector(@selector(frame))]) {
        if (!self.segmentedControl.superview) {
            return;
        }
        CGFloat width = self.segmentedControl.superview.frame.size.width-18.0;
        if (width > 350.0) {
            width = 350.0;
        }
        CGFloat height = self.segmentedControl.superview.frame.size.height-16.0;
        if (height < 20.0) {
            height = 20.0;
        }
        self.segmentedControl.bounds = CGRectMake(0, 0, width, height);
    }
}

#pragma mark - UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet willDismissWithButtonIndex:(NSInteger)buttonIndex {
    switch (buttonIndex) {
        case kMealTimeButtonIndex:
            if (self.selectedMealTime == MealTime_LUNCH) {
                [self trackAction:@"ViewDinner"];
                self.selectedMealTime = MealTime_DINNER;
            } else {
                [self trackAction:@"ViewLunch"];
                self.selectedMealTime = MealTime_LUNCH;
            }
            [self refresh];
            break;
        case kMealDateButtonIndex:
            [self showMealDatePicker];
            break;
        default:
            break;
    }
}

#pragma mark - FoodServiceDelegate

- (void)getFoodForRequest:(FoodRequest *)request didReturn:(FoodResponse *)response {
    switch (response.statusCode) {
        case FoodStatusCode_OK:
            self.foodResponse = response;
            [self fillCollectionsAndUpdateUI];
            [self.lgRefreshControl endRefreshingAndMarkSuccessful];
            if (self.restaurantViewController) {
                NSInteger index = [self.foodResponse.menu indexOfObject:self.restaurantViewController.restaurant];
                if (index != NSNotFound) {
                    EpflRestaurant* restaurant = self.foodResponse.menu[index];
                    self.restaurantViewController.restaurant = restaurant;
                }
            }
            if (self.mealsByRestaurantViewController) {
                [self.mealsByRestaurantViewController setRestaurants:self.restaurantsSorted shouldShowMealBlock:self.mealsByRestaurantViewController.shouldShowMealBlock];
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
        self.selectedMealType = 0;
        PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:self.restaurantViewController];
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], navController];
    } else {
        [self.navigationController pushViewController:self.restaurantViewController animated:YES];
    }
    [self trackAction:@"ShowRestaurant" contentInfo:restaurant.rName];
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (self.restaurantsSorted && self.restaurantsSorted.count == 0) {
        if (indexPath.row == 1) {
            return [[PCCenterMessageCell alloc] initWithMessage:NSLocalizedStringFromTable(@"NoMeals", @"FoodPlugin", nil)];
        } else {
            UITableViewCell* cell =[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            return cell;
        }
    }
    
    EpflRestaurant* restaurant = self.restaurantsSorted[indexPath.row];
    NSString* const identifier = [(PCTableViewAdditions*)tableView autoInvalidatingReuseIdentifierForIdentifier:@"RestaurantCell"];
    PCTableViewCellAdditions* cell = [self.restaurantsTableView dequeueReusableCellWithIdentifier:identifier];
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

#pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    FoodMealsByRestaurantViewController* viewController = self.mealsByRestaurantViewController ?: [[FoodMealsByRestaurantViewController alloc] init];
    NSNumber* nsMealType = [EpflMeal allMealTypes][indexPath.item];
    NSInteger mealType = [nsMealType integerValue];
    if (mealType == MealType_UNKNOWN) {
        //means all
        viewController.title = NSLocalizedStringFromTable(@"AllMenus", @"FoodPlugin", nil);
        [viewController setRestaurants:self.restaurantsSorted shouldShowMealBlock:nil];
    } else {
        viewController.title = [NSString stringWithFormat:NSLocalizedStringFromTable(@"MenuWithMealTypeWithFormat", @"FoodPlugin", nil), [EpflMeal localizedNameForMealType:mealType]];
        [viewController setRestaurants:self.restaurantsSorted shouldShowMealBlock:^BOOL(EpflMeal *meal) {
            return [meal.mTypes containsObject:nsMealType];
        }];
    }
    [self trackAction:@"ShowMenusForIngredient" contentInfo:[EpflMeal enumNameForMealType:mealType]];
    self.mealsByRestaurantViewController = viewController;
    if (self.splitViewController) {
        self.selectedMealType = mealType;
        self.selectedRestaurant = nil;
        UINavigationController* navController = self.splitViewController.viewControllers[1];
        if ([navController isKindOfClass:[UINavigationController class]] && [[navController.viewControllers firstObject] isKindOfClass:[FoodMealsByRestaurantViewController class]]) {
            [navController popToRootViewControllerAnimated:NO];
        } else {
            navController = [[PCNavigationController alloc] initWithRootViewController:viewController];
        }
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], navController];
    } else {
        [self.navigationController pushViewController:viewController animated:YES];
    }
}

#pragma mark - UICollectionViewDataSource

- (UICollectionViewCell*)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    NSNumber* nbMealType = [EpflMeal allMealTypes][indexPath.item];
    FoodMealTypeCell* cell = [self.mealTypesCollectionView dequeueReusableCellWithReuseIdentifier:kMealTypeCellReuseIdentifier forIndexPath:indexPath];
    cell.mealType = [nbMealType integerValue];
    return cell;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return [EpflMeal allMealTypes].count;
}

#pragma mark - Dealloc

- (void)dealloc {
    [self.foodService cancelOperationsForDelegate:self];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    @try {
        [self.segmentedControl removeObserver:self forKeyPath:NSStringFromSelector(@selector(frame))];
    }
    @catch (NSException *exception) {}
}

@end
