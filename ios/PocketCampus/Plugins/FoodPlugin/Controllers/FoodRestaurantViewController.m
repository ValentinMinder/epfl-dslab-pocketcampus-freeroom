//
//  FoodRestaurantViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 30.10.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "FoodRestaurantViewController.h"

#import "FoodService.h"

static const NSInteger kRestaurantInfoSection = 0;
static const NSInteger kMealsSection = 1;

@interface FoodRestaurantViewController ()

@property (nonatomic, strong) EpflRestaurant* restaurant;

@end

@implementation FoodRestaurantViewController

#pragma mark - Init

- (instancetype)initWithEpflRestaurant:(EpflRestaurant*)restaurant;
{
    [PCUtils throwExceptionIfObject:restaurant notKindOfClass:[EpflRestaurant class]];
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        self.restaurant = restaurant;
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];

}


#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell = nil;
    switch (indexPath.section) {
#warning TODO
        case kRestaurantInfoSection:
            
            break;
        case kMealsSection:
            
            break;
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case kRestaurantInfoSection:
            return 1;
        case kMealsSection:
            return self.restaurant.rMeals.count;
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (!self.restaurant) {
        return 0;
    }
    return 2; //retaurant info + meals
}

@end
