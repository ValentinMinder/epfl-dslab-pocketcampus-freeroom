//
//  MenuCellView.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 10.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
#import "FoodService.h"
#import "PCValues.h"
#import "JSFavStarControl.h"
#import "UIDevice+IdentifierAddition.h"

@class MenusListViewController;

typedef enum {
    VoteModeVote,
    VoteModeOK,
    VoteModeDisabled,
    VoteModeUnset
} VoteMode;

@interface MealCell : UITableViewCell<FoodServiceDelegate> {
    FoodService* service;
    Meal* meal;
    MenusListViewController* controller;
    UILabel* votesLabel;
    JSFavStarControl* ratingView;
    UITextView* textView;
    UIActivityIndicatorView* ratingActivityIndicator;
    UIButton* voteButton;
    CGRect voteButtonNormalFrame;
    CGRect voteButtonHiddenFrame;
    VoteMode voteMode;
    
}


- (id)initWithMeal:(Meal*)meal_ controller:(MenusListViewController*)controller_ reuseIdentifier:(NSString*)reuseIdentfier;
- (void)setMeal:(Meal*)newMeal;
+ (CGFloat)requiredHeightForMeal:(Meal*)meal_;
- (void)setVoteMode:(VoteMode)newMode animated:(BOOL)animated;

@end
