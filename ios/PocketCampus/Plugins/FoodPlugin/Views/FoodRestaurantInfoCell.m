//
//  FoodRestaurantInfoCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 30.10.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "FoodRestaurantInfoCell.h"

#import "FoodService.h"

@interface FoodRestaurantInfoCell ()

@property (nonatomic, strong) UIImageView* backgroundImageView;

@end

@implementation FoodRestaurantInfoCell

- (instancetype)initWithEpflRestaurant:(EpflRestaurant*)restaurant
{
    [PCUtils throwExceptionIfObject:restaurant notKindOfClass:[EpflRestaurant class]];
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"FoodRestaurantInfoCell" owner:nil options:nil];
    self = (FoodRestaurantInfoCell*)elements[0];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.restaurant = restaurant;
    }
    return self;
}

+ (CGFloat)preferredHeight {
    return 100.0;
}

- (void)setRestaurant:(EpflRestaurant *)restaurant {
    _restaurant = restaurant;
#warning TODO
}

@end
