//
//  MenusListViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 10.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "FoodService.h"

#import "MealCell.h"

@class RestaurantsListViewController;

@interface MenusListViewController : UIViewController {
    UITableView* tableView;
    NSString* restaurantName;
    NSArray* meals;
    VoteMode voteMode;
}

- (id)initWithRestaurantName:(NSString*)restaurantName andMeals:(NSArray*)meals_;
- (void)setForAllCellsVoteMode:(VoteMode)newMode exceptCell:(MealCell*)exceptCell animated:(BOOL)animated; //pass nil for exceptCell to include all cells
- (void)setUpdatedRating:(Rating*)newRating forMeal:(Meal*)meal;
- (void)showMapButtonIfPossible;

@property (nonatomic, assign) IBOutlet UITableView* tableView;

@end
