//
//  RestaurantsListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 08.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "RestaurantsListViewController.h"

#import "MealCell.h"

static NSString* kRestaurantCellIdentifier = @"restaurant";

@implementation RestaurantsListViewController

@synthesize tableView, centerActivityIndicator, centerMessageLabel;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        foodService = [[FoodService sharedInstanceToRetain] retain];
        meals = nil;
        restaurants = nil;
        restaurantsAndMeals = nil;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [centerActivityIndicator startAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"CenterLabelLoadingText", @"FoodPlugin", @"Tell the user that the list of restaurants is loading");
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

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [tableView deselectRowAtIndexPath:[tableView indexPathForSelectedRow] animated:YES];
}

- (void)viewWillAppear:(BOOL)animated {
    [foodService getMealsWithDelegate:self];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

/* FoodServiceDelegate delegation */

- (void)getMealsDidReturn:(NSArray*)meals_ {
    
    if (meals != nil && meals.count == meals_.count) {
        BOOL difference = NO;
        for (int i = 0; i<meals.count; i++) {
            Meal* prevMeal = [meals objectAtIndex:i];
            Meal* newMeal = [meals_ objectAtIndex:i];
            if (prevMeal.rating.ratingValue != newMeal.rating.ratingValue || prevMeal.rating.numberOfVotes != newMeal.rating.numberOfVotes) {
                difference = YES;
                break;
            }
        }
        if (!difference) {
            return;
        }
    }
    [meals release];
    meals = [meals_ retain];
    if (meals.count == 0) {
        [self getMealsNoMeals];
        return;
    }
    
    [self populateRestaurantsAndMeals];
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = @"";
    tableView.hidden = NO;
    [tableView reloadData];
}

- (void)getMealsNoMeals {
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"NoMealsWeekend", @"FoodPlugin", @"Message that says that there is not meals today");
}

- (void)getMealsFailed {
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", @"Message that says that connection to server throw an error");
}

- (void)serviceConnectionToServerTimedOut {
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
    restaurantsAndMeals = [[NSDictionary dictionaryWithDictionary:temp] retain]; //creates non-mutable copy
    restaurants = [[[tempRestaurantsSet allObjects] sortedArrayUsingComparator:^(Restaurant* rest1, Restaurant* rest2) {
        return [rest1.name localizedCaseInsensitiveCompare:rest2.name];
    }] retain];
}

/* UITableViewDelegate delegation */

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    Restaurant* restaurant = [restaurants objectAtIndex:indexPath.row];
    MenusListViewController* controller = [[MenusListViewController alloc] initWithRestaurantName:restaurant.name andMeals:[restaurantsAndMeals objectForKey:restaurant.name]];
    [self.navigationController pushViewController:controller animated:YES];
    [controller release];
}

/* UITableViewDataSource */

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

- (void)dealloc
{
    tableView.delegate = nil;
    tableView.dataSource = nil;
    [meals release];
    [restaurants release];
    [restaurantsAndMeals release];
    [foodService cancelOperationsForDelegate:self];
    [foodService release];
    [super dealloc];
}

@end
