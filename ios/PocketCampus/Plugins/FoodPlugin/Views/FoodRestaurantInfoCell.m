//
//  FoodRestaurantInfoCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 30.10.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "FoodRestaurantInfoCell.h"

#import "FoodService.h"

#import "MapController.h"

#import "FoodRestaurantViewController.h"

@interface FoodRestaurantInfoCell ()

@property (nonatomic, strong) IBOutlet UIImageView* backgroundImageView;
@property (nonatomic, strong) IBOutlet UILabel* restaurantNameLabel;
@property (nonatomic, strong) IBOutlet UIButton* showOnMapButton;

@end

@implementation FoodRestaurantInfoCell

#pragma mark - Init

- (instancetype)initWithEpflRestaurant:(EpflRestaurant*)restaurant
{
    [PCUtils throwExceptionIfObject:restaurant notKindOfClass:[EpflRestaurant class]];
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"FoodRestaurantInfoCell" owner:nil options:nil];
    self = (FoodRestaurantInfoCell*)elements[0];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.restaurant = restaurant;
        self.showOnMapButton.hidden = (self.restaurant.rLocation == nil);
    }
    return self;
}

#pragma mark - Public properties and methods

+ (CGFloat)preferredHeight {
    return 100.0;
}

- (void)setRestaurant:(EpflRestaurant *)restaurant {
    _restaurant = restaurant;
    self.restaurantNameLabel.text = self.restaurant.rName;
}

#pragma mark - Actions

- (IBAction)showOnMapPressed {
    if (!self.restaurant.rLocation || !self.restaurantViewController) {
        return;
    }
    UIViewController* mapViewController = [MapController viewControllerWithInitialMapItem:self.restaurant.rLocation];
    [self.restaurantViewController.navigationController pushViewController:mapViewController animated:YES];
}

@end
