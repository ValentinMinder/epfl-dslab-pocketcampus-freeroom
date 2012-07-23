//
//  MenusListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 10.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MenusListViewController.h"

#import "PCTableViewSectionHeader.h"

#import "MapController.h"

@implementation MenusListViewController

@synthesize tableView;

static NSString* kMealCellIdentifier = @"mealCell"; 

- (id)initWithRestaurantName:(NSString*)restaurantName_ andMeals:(NSArray*)meals_
{
    self = [super initWithNibName:@"MenusListView" bundle:nil];
    if (self) {
        if (![restaurantName_ isKindOfClass:[NSString class]]) {
            @throw [NSException exceptionWithName:@"illegal argument" reason:@"restaurantName argument is not kind of class NSString" userInfo:nil];
        }
        if (![meals_ isKindOfClass:[NSArray class]]) {
            @throw [NSException exceptionWithName:@"illegal argument" reason:@"meals argument is not kind of class NSArray" userInfo:nil];
        }
        self.title = restaurantName_;
        restaurantName = [restaurantName_ retain];
        meals = [meals_ retain];
        service = [[FoodService sharedInstanceToRetain] retain];
    }
    return self;
}

- (id)initWithMeals:(NSArray*)meals_ {
    self = [super initWithNibName:@"MenusListView" bundle:nil];
    if (self) {
        restaurantName = nil;
        if (meals_ == nil) {
            @throw [NSException exceptionWithName:@"illegal argument" reason:@"meals argument cannot be nil" userInfo:nil];
        }
        meals = [meals_ retain];
        /*for (Meal* meal in meals) {
            NSMutableString* stringDescr = [meal.mealDescription mutableCopy];
            if ([[stringDescr substringToIndex:1] isEqualToString:@"\n"]) {
                [stringDescr deleteCharactersInRange:NSMakeRange(0, 1)];
            }
            
            if ([[stringDescr substringFromIndex:stringDescr.length] isEqualToString:@"\n"]) {
                [stringDescr deleteCharactersInRange:NSMakeRange(stringDescr.length, 1)];
            }
            
            //meal.mealDescription = stringDescr;
            [stringDescr release];
        }*/
        service = [[FoodService sharedInstanceToRetain] retain];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    tableView.sectionHeaderHeight = [PCValues tableViewSectionHeaderHeight];
    [self showMapButtonIfPossible];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)showMapButtonIfPossible {
    if (restaurantName == nil || [restaurantName isEqualToString:@"Bistro 31"] || [restaurantName isEqualToString:@"Hong Thaï Rung"]) { //map plugin does not know these restaurants
        return;
    }
    UIBarButtonItem* mapButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Map", @"FoodPlugin", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(mapButtonPressed)];
    [self.navigationItem setRightBarButtonItem:mapButton animated:YES];
    [mapButton release];
}

- (void)mapButtonPressed {
    if (restaurantName != nil) {
        NSString* query = restaurantName;
        if ([query isEqualToString:@"Le Puur Innovation"]) {
            query = @"Puur Innovation"; //map plugin does not find it otherwise
        } else if ([query isEqualToString:@"La Table de Vallotton"]) {
            query = @"Table de Vallotton"; //map plugin does not find it otherwise
        } else {
            //OK
        }
        UIViewController* mapViewController = [MapController viewControllerWithInitialSearchQuery:query];
        [self.navigationController pushViewController:mapViewController animated:YES];
    }
}

- (void)setForAllCellsVoteMode:(VoteMode)newMode exceptCell:(MealCell*)exceptCell animated:(BOOL)animated; {
    voteMode = newMode;
    for (MealCell* cell in [tableView visibleCells]) {
        if (cell != exceptCell) {
            [cell setVoteMode:newMode animated:animated];
        }
    }
}

- (void)setUpdatedRating:(Rating*)newRating forMeal:(Meal*)meal {
    for (Meal* meal2 in meals) {
        if (meal.mealId == meal2.mealId) {
            [meal2 setRating:newRating];
        }
    }
}

/* UITableViewDelegate delegation */

- (UIView *) tableView:(UITableView *)tableView_ viewForHeaderInSection:(NSInteger)section 
{
    Meal* meal = [meals objectAtIndex:section];
    PCTableViewSectionHeader* sectionHeader = [[PCTableViewSectionHeader alloc] initWithSectionTitle:meal.name tableView:tableView];
    return [sectionHeader autorelease];
    
}


/* UITableViewDataSource delegation */

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    Meal* meal = [meals objectAtIndex:indexPath.section];
    MealCell* cell = [tableView dequeueReusableCellWithIdentifier:kMealCellIdentifier];
    if (cell == nil) {
        cell = [[[MealCell alloc] initWithMeal:meal controller:self reuseIdentifier:kMealCellIdentifier] autorelease];
    } else {
        [cell setMeal:meal];
        [cell setVoteMode:voteMode animated:NO];
    }
    
    /*cell = [[[MealCell alloc] initWithMeal:meal controller:self reuseIdentifier:kMealCellIdentifier] autorelease];
    [cell setVoteMode:voteMode animated:NO];*/
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (meals == nil) {
        return 0;
    }
    return 1;
}

- (CGFloat)tableView:(UITableView *)tableView_ heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [MealCell requiredHeightForMeal:[meals objectAtIndex:indexPath.section]];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return meals.count;
}

- (void)dealloc
{
    tableView.delegate = nil;
    tableView.dataSource = nil;
    [service release];
    [restaurantName release];
    [meals release];
    [super dealloc];
}

@end
