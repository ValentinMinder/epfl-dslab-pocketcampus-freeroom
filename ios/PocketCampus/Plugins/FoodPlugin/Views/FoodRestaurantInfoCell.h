//
//  FoodRestaurantInfoCell.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 30.10.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@class EpflRestaurant;
@class FoodRestaurantViewController;

@interface FoodRestaurantInfoCell : UITableViewCell

- (instancetype)initWithEpflRestaurant:(EpflRestaurant*)restaurant;

+ (CGFloat)preferredHeight;

@property (nonatomic, strong) EpflRestaurant* restaurant;

@property (nonatomic, weak) FoodRestaurantViewController* restaurantViewController;

@end
