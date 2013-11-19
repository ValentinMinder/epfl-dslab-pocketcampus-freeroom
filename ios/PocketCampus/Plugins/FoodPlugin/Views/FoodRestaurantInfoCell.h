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

+ (CGFloat)preferredHeightForRestaurant:(EpflRestaurant*)restaurant;

@property (nonatomic, strong) EpflRestaurant* restaurant;

/*
 * By default, this button has no target/action, you can add one
 */
@property (nonatomic, strong) IBOutlet UIButton* showOnMapButton;

/*
 * Default: YES
 */
@property (nonatomic) BOOL showRating;

@end
