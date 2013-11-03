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

#import "UIImageView+AFNetworking.h"

@interface FoodRestaurantInfoCell ()

@property (nonatomic, strong) IBOutlet UIImageView* backgroundImageView;
@property (nonatomic, strong) IBOutlet UIView* overlayView;
@property (nonatomic, strong) IBOutlet UILabel* satRateLabel;

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
        CAGradientLayer *gradient = [CAGradientLayer layer];
        gradient.frame = self.overlayView.bounds;
        gradient.startPoint = CGPointMake(0, 0.5);
        gradient.endPoint = CGPointMake(1.0, 0.5);
        gradient.colors = [NSArray arrayWithObjects:(id)[[UIColor colorWithWhite:1.0 alpha:0.6] CGColor], (id)[[UIColor clearColor] CGColor], nil];
        [self.overlayView.layer insertSublayer:gradient atIndex:0];
        _showRating = YES; //Default
        self.restaurant = restaurant;
    }
    return self;
}

#pragma mark - Public properties and methods

+ (CGFloat)preferredHeight {
    return 120.0;
}

- (void)setRestaurant:(EpflRestaurant *)restaurant {
    _restaurant = restaurant;
    if (self.restaurant.rPictureUrl) {
        self.backgroundImageView.contentMode = UIViewContentModeScaleAspectFill;
        [self.backgroundImageView setImageWithURL:[NSURL URLWithString:self.restaurant.rPictureUrl]];
    }
    
    NSString* satRateTitleString = NSLocalizedStringFromTable(@"satisfaction", @"FoodPlugin", nil);
    NSString* satRateString = [NSString stringWithFormat:@"%.0lf%%", self.restaurant.rRating.ratingValue*100.0]; //show percentage
    NSString* nbVotesString = [NSString stringWithFormat:@"(%d %@)", self.restaurant.rRating.voteCount, self.restaurant.rRating.voteCount > 1 ? NSLocalizedStringFromTable(@"votes", @"FoodPlugin", nil) : NSLocalizedStringFromTable(@"vote", @"FoodPlugin", nil)];
    NSString* fullSatRateString = [NSString stringWithFormat:@"%@\n%@\n%@", satRateString, satRateTitleString, nbVotesString];
    NSMutableAttributedString* satAttrString = [[NSMutableAttributedString alloc] initWithString:fullSatRateString];
    UIFont* biggerFont = [UIFont fontWithDescriptor:self.satRateLabel.font.fontDescriptor size:self.satRateLabel.font.fontDescriptor.pointSize+14];
    [satAttrString addAttribute:NSFontAttributeName value:biggerFont range:[fullSatRateString rangeOfString:satRateString]];
    UIColor* color = nil;
    if (self.restaurant.rRating.ratingValue > 0.66) {
        color = [UIColor colorWithRed:0.152941 green:0.921569 blue:0.000000 alpha:1.0];
    } else if (self.restaurant.rRating.ratingValue > 0.33) {
        color = [UIColor colorWithRed:0.921569 green:0.584314 blue:0.000000 alpha:1.0];
    } else {
        color = [UIColor colorWithRed:1.000000 green:0.000000 blue:0.000000 alpha:1.0];
    }
    [satAttrString addAttribute:NSForegroundColorAttributeName value:color range:[fullSatRateString rangeOfString:satRateString]];
    
    self.satRateLabel.attributedText = satAttrString;
}

- (void)setShowRating:(BOOL)showRating {
    _showRating = showRating;
    self.overlayView.hidden = !showRating;
}

#pragma mark - Actions


#pragma mark - Dealloc

- (void)dealloc {
    [self.backgroundImageView cancelImageRequestOperation];
}

@end
