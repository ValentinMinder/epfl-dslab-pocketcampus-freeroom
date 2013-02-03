//
//  RestaurantsListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 08.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "RestaurantsListViewController.h"

#import "MealCell.h"

#import "PCUtils.h"

#import "PCValues.h"

#import "PCTableViewSectionHeader.h"

#import "ObjectArchiver.h"

static NSString* kRestaurantCellIdentifier = @"restaurant";

static NSString* kLastRefreshDateKey = @"lastRefreshDate";

/*
 * Will refresh if last refresh date is not same date as current OR older than kRefreshValiditySeconds ago
 * Important to refresh often, otherwise ratings are not updated. Background update of ratings should be
 * considered in a future update.
 */
static const NSTimeInterval kRefreshValiditySeconds = 300.0; //5 min.

@implementation RestaurantsListViewController

@synthesize tableView, centerActivityIndicator, centerMessageLabel;

- (id)init
{
    self = [super initWithNibName:@"RestaurantsListView" bundle:nil];
    if (self) {
        foodService = [[FoodService sharedInstanceToRetain] retain];
        meals = [[foodService getFromCacheMeals] retain];
        restaurants = nil;
        restaurantsAndMeals = nil;
        lastRefreshDate = [(NSDate*)[ObjectArchiver objectForKey:kLastRefreshDateKey andPluginName:@"food" isCache:YES] retain];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/food" withError:NULL];
    self.view.backgroundColor = [PCValues backgroundColor1];
    UIBarButtonItem* refreshButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(refresh)];
    self.navigationItem.rightBarButtonItem = refreshButton;
    [refreshButton release];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshIfNeeded) name:UIApplicationDidBecomeActiveNotification object:[UIApplication sharedApplication]];
    /* TEST */
    /*
    Rating* rating = [[Rating alloc] initWithRatingValue:3.0 numberOfVotes:10 sumOfRatings:20];
    Location* location = [[Location alloc] initWithLatitude:0 longitude:0 altitude:0];
    Restaurant* restaurant1 = [[Restaurant alloc] initWithRestaurantId:1 name:@"Le Corbusier" location:location];
    Restaurant* restaurant2 = [[Restaurant alloc] initWithRestaurantId:1 name:@"Cafétéria BC" location:location];

    
    Meal* meal1 = [[Meal alloc] initWithMealId:111 name:@"Assiette 1" mealDescription:@"Filet de lieu noir (DE) à l'Italienne\nBrocoli ou salade ou potage\nPommes vapeurs" restaurant:restaurant1 rating:rating price:7.0];
    Meal* meal2 = [[Meal alloc] initWithMealId:222 name:@"Assiette végétarienne" mealDescription:@"Escalope de légumes panée, sauce dips au séré\nSalade, potage" restaurant:restaurant1 rating:rating price:8.0];
    
    Meal* meal3 = [[Meal alloc] initWithMealId:333 name:@"Assiette végétarienne" mealDescription:@"Escalope de légumes panée, sauce dips au séré\nSalade, potage" restaurant:restaurant1 rating:rating price:9.0];
    
    Meal* meal4 = [[Meal alloc] initWithMealId:444 name:@"Fourchette Verte" mealDescription:@"Boulettes de volailles (BR), sauce curry\nLégumes ou salade ou potage\nBoulgour" restaurant:restaurant2 rating:rating price:7.0];
    
    NSArray* meals2 = [NSArray arrayWithObjects:meal1, meal2, meal3, meal4, nil];
    
    [self getMealsDidReturn:meals2];
    */
    /* END OF TEST */
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [tableView deselectRowAtIndexPath:[tableView indexPathForSelectedRow] animated:animated];
    [self refreshIfNeeded];
}

- (void)refreshIfNeeded {
    if (lastRefreshDate && meals) {
        NSCalendar* calendar = [NSCalendar currentCalendar];
        
        unsigned unitFlags = NSYearCalendarUnit | NSMonthCalendarUnit |  NSDayCalendarUnit;
        NSDateComponents* compLastRefresh = [calendar components:unitFlags fromDate:lastRefreshDate];
        NSDateComponents* compNow = [calendar components:unitFlags fromDate:[NSDate date]];
        
        if ([compLastRefresh day]   == [compNow day] &&
            [compLastRefresh month] == [compNow month] &&
            [compLastRefresh year]  == [compNow year] &&
            abs([lastRefreshDate timeIntervalSinceNow]) < kRefreshValiditySeconds) {
            [self reloadAndShowTableView];
            return;
        }
    }
    if (self.navigationController.topViewController != self) {
        [self.navigationController popToViewController:self animated:NO];
    }
    [self refresh];
}

- (void)refresh {
    [meals release];
    meals = nil;
    tableView.hidden = YES;
    [centerActivityIndicator startAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"CenterLabelLoadingText", @"FoodPlugin", @"Tell the user that the list of restaurants is loading");
    [foodService cancelOperationsForDelegate:self];
    [foodService getMealsWithDelegate:self];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation //<= iOS5
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)reloadAndShowTableView {
    [self populateRestaurantsAndMeals];
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = @"";
    tableView.hidden = NO;
    [PCUtils reloadTableView:tableView withFadingDuration:0.2];
}

#pragma mark - FoodServiceDelegate

- (void)getMealsDidReturn:(NSArray*)meals_ {
    self.navigationItem.rightBarButtonItem.enabled = YES;
    if (meals_.count == 0) {
        [self getMealsNoMeals];
        return;
    }
    /*BOOL difference = NO;
    BOOL mealsWasNil = (meals == nil);
    if (meals != nil && meals.count == meals_.count) {
        for (int i = 0; i<meals.count; i++) {
            Meal* prevMeal = [meals objectAtIndex:i];
            Meal* newMeal = [meals_ objectAtIndex:i];
            if (prevMeal.rating.ratingValue != newMeal.rating.ratingValue || prevMeal.rating.numberOfVotes != newMeal.rating.numberOfVotes) {
                difference = YES;
                break;
            }
        }
        if (!difference && !self.tableView.hidden) {
            return;
        }
    }*/
    [meals release];
    meals = [meals_ retain];
    [self reloadAndShowTableView];
    [lastRefreshDate release];
    lastRefreshDate = [[NSDate date] retain];
    [ObjectArchiver saveObject:lastRefreshDate forKey:kLastRefreshDateKey andPluginName:@"food" isCache:YES];
}

- (void)getMealsNoMeals {
    self.navigationItem.rightBarButtonItem.enabled = YES;
    tableView.hidden = YES;
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"NoMealsWeekend", @"FoodPlugin", @"Message that says that there is no meals today");
}

- (void)getMealsFailed {
    self.navigationItem.rightBarButtonItem.enabled = YES;
    tableView.hidden = YES;
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", @"Message that says that connection to server throw an error");
}

- (void)serviceConnectionToServerTimedOut {
    self.navigationItem.rightBarButtonItem.enabled = YES;
    tableView.hidden = YES;
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", @"Message that says that connection to server is impossible and that internet connection must be checked.");
}

- (void)populateRestaurantsAndMeals {
    if (meals == nil) {
        @throw [NSException exceptionWithName:@"Cannot populate restaurantsAndMeals" reason:@"meals is nil" userInfo:nil];
    }
    
    NSMutableDictionary* temp = [NSMutableDictionary dictionary];
    NSMutableSet* tempRestaurantsSet = [NSMutableSet set];
    for (Meal* meal in meals) {
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
    [restaurantsAndMeals release];
    restaurantsAndMeals = [[NSDictionary dictionaryWithDictionary:temp] retain]; //creates non-mutable copy
    [restaurants release];
    restaurants = [[[tempRestaurantsSet allObjects] sortedArrayUsingComparator:^(Restaurant* rest1, Restaurant* rest2) {
        return [rest1.name localizedCaseInsensitiveCompare:rest2.name];
    }] retain];
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return [PCValues tableViewSectionHeaderHeight];
}

- (UIView *) tableView:(UITableView *)tableView_ viewForHeaderInSection:(NSInteger)section
{
    NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
    
    NSString* dateString = [dateFormatter stringFromDate:[NSDate date]]; //now
    
    dateString = [NSString stringWithFormat:NSLocalizedStringFromTable(@"MenusForTodayWithFormat", @"FoodPlugin", nil), dateString];
    
    [dateFormatter release];
    
    PCTableViewSectionHeader* sectionHeader = [[PCTableViewSectionHeader alloc] initWithSectionTitle:dateString tableView:tableView];
    return [sectionHeader autorelease];
    
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    Restaurant* restaurant = [restaurants objectAtIndex:indexPath.row];
    MenusListViewController* controller = [[MenusListViewController alloc] initWithRestaurantName:restaurant.name andMeals:[restaurantsAndMeals objectForKey:restaurant.name]]; //must not give a copy but current reference, so that rating can be updated on this instance directly
    [self.navigationController pushViewController:controller animated:YES];
    [controller release];
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    Restaurant* restaurant = [restaurants objectAtIndex:indexPath.row];
    
    UITableViewCell* newCell = [tableView dequeueReusableCellWithIdentifier:kRestaurantCellIdentifier];
    
    if (newCell == nil) {
        newCell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kRestaurantCellIdentifier] autorelease];
        newCell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        newCell.selectionStyle = UITableViewCellSelectionStyleGray;
    }

    if (restaurant.name != nil) {
        newCell.textLabel.text = restaurant.name;
    }
    
    return newCell;
    
    //MealCell* newCell = [[MealCell alloc] initWithMeal:[meals objectAtIndex:indexPath.row] andController:self showRestaurantName:YES];
    //return [newCell autorelease];
}

/*- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    return NSLocalizedStringFromTable(@"MenusTableViewSectionName", @"FoodPlugin", @"Name of section listing restaurants");
}*/

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (restaurants == nil) {
        return 0;
    }
    return restaurants.count;
    //return meals.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

#pragma mark - dealloc

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    tableView.delegate = nil;
    tableView.dataSource = nil;
    [lastRefreshDate release];
    [meals release];
    [restaurants release];
    [restaurantsAndMeals release];
    [foodService cancelOperationsForDelegate:self];
    [foodService release];
    [super dealloc];
}

@end
