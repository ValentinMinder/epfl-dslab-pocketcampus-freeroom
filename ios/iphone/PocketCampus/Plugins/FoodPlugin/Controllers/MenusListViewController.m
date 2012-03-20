//
//  MenusListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 10.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MenusListViewController.h"

#import "PCTableViewSectionHeader.h"

@implementation MenusListViewController

static NSString* kMealCellIdentifier = @"mealCell"; 

- (id)initWithRestaurantName:(NSString*)restaurantName andMeals:(NSArray*)meals_
{
    self = [super initWithNibName:@"MenusListView" bundle:nil];
    if (self) {
        self.title = restaurantName;
        if (meals_ == nil) {
            @throw [NSException exceptionWithName:@"illegal argument" reason:@"meals argument cannot be nil" userInfo:nil];
        }
        meals = [meals_ retain];
        service = [[FoodService sharedInstanceToRetain] retain];
    }
    return self;
}

- (id)initWithMeals:(NSArray*)meals_ {
    self = [super initWithNibName:@"MenusListView" bundle:nil];
    if (self) {
        //self.title = restaurantName;
        if (meals_ == nil) {
            @throw [NSException exceptionWithName:@"illegal argument" reason:@"meals argument cannot be nil" userInfo:nil];
        }
        meals = [meals_ retain];
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

- (void)setForAllCellsVoteMode:(VoteMode)newMode exceptCell:(MealCell*)exceptCell animated:(BOOL)animated; {
    for (MealCell* cell in [tableView visibleCells]) {
        if (cell != exceptCell) {
            [cell setVoteMode:newMode animated:animated];
        }
    }
}

/* UITableViewDataSource delegation */

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    Meal* meal = [meals objectAtIndex:indexPath.section];
    MealCell* cell = [tableView dequeueReusableCellWithIdentifier:kMealCellIdentifier];
    if (cell == nil) {
        cell = [[[MealCell alloc] initWithMeal:meal controller:self reuseIdentifier:kMealCellIdentifier] autorelease];
    } else {
        [cell setMeal:meal];
    }
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

- (UIView *) tableView:(UITableView *)tableView_ viewForHeaderInSection:(NSInteger)section 
{
    Meal* meal = [meals objectAtIndex:section];
    PCTableViewSectionHeader* sectionHeader = [[PCTableViewSectionHeader alloc] initWithSectionTitle:meal.name];
    return [sectionHeader autorelease];
    
}

- (void)dealloc
{
    [tableView release];
    [service release];
    [meals release];
    [super dealloc];
}

@end
