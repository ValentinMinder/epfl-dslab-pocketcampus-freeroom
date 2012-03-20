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

@interface MenusListViewController : UIViewController {
    IBOutlet UITableView* tableView;
    NSArray* meals;
    FoodService* service;
}

- (id)initWithRestaurantName:(NSString*)restaurantName andMeals:(NSArray*)meals_;
- (void)setForAllCellsVoteMode:(VoteMode)newMode exceptCell:(MealCell*)exceptCell animated:(BOOL)animated; //pass nil for exceptCell to include all cells

@end
