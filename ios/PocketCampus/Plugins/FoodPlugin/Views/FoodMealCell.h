//
//  FoodMealCell.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@class EpflMeal;

@interface FoodMealCell : UITableViewCell

- (instancetype)initWithReuseIdentifier:(NSString*)reuseIdentifier;

@property (nonatomic, strong) EpflMeal* meal;

+ (CGFloat)preferredHeightForMeal:(EpflMeal*)meal;

/*
 * If YES, cell's content is shifted to the right and reveals rating controls
 * Default: NO
 */
@property (nonatomic, getter = isRateModeEnabled) BOOL rateModeEnabled;
- (void)setRateModeEnabled:(BOOL)rateModeEnabled animated:(BOOL)animated;

@end
