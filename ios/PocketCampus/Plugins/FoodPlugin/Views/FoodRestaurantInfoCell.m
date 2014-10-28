/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

//  Created by Loïc Gardiol on 30.10.13.

#import "FoodRestaurantInfoCell.h"

#import "FoodService.h"

#import "MapController.h"

#import "FoodRestaurantViewController.h"

#import "UIImageView+AFNetworking.h"

static const CGFloat kTopBarHeight = 30.0;

@interface FoodRestaurantInfoCell ()

@property (nonatomic, strong) IBOutlet UIImageView* backgroundImageView;
@property (nonatomic, strong) IBOutlet UILabel* satRateLabel;
@property (nonatomic, strong) IBOutlet NSLayoutConstraint* satRateLabelLeftConstraint;

@property (nonatomic, strong) NSLayoutConstraint* satRatLabelNullWidthConstraint;

@property (nonatomic, strong) NSLayoutConstraint* backgroundImageViewHeightConstraint;

@end

@implementation FoodRestaurantInfoCell

#pragma mark - Init

- (instancetype)initWithEpflRestaurant:(EpflRestaurant*)restaurant
{
    [PCUtils throwExceptionIfObject:restaurant notKindOfClass:[EpflRestaurant class]];
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"FoodRestaurantInfoCell" owner:nil options:nil];
    self = (FoodRestaurantInfoCell*)elements[0];
    if (self) {
        self.backgroundImageView.contentMode = UIViewContentModeScaleAspectFill;
        self.backgroundImageView.clipsToBounds = YES;
        self.satRateLabel.isAccessibilityElement = NO;
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.showOnMapButton.accessibilityHint = NSLocalizedStringFromTable(@"ShowsRestaurantOnMap", @"FoodPlugin", nil);
        //self.separatorInset = UIEdgeInsetsMake(0, 1000, 0, 0);
        [self.showOnMapButton setTitle:[NSString stringWithFormat:@"  %@  ", NSLocalizedStringFromTable(@"ShowOnMap", @"FoodPlugin", nil)] forState:UIControlStateNormal];
        _showRating = YES; //Default
        
        self.restaurant = restaurant;
    }
    return self;
}


#pragma mark - Public properties and methods

+ (CGFloat)preferredHeightForRestaurant:(EpflRestaurant*)restaurant {
    if (!restaurant.rPictureUrl) {
        return kTopBarHeight + 0.5; //just top bar for satRateLabel and showOnMapButton. 0.5 (room for separator)
    }
    return [PCUtils isIdiomPad] ? 240.0 : 0.3 * [UIScreen mainScreen].bounds.size.height;
}

- (void)setRestaurant:(EpflRestaurant *)restaurant {
    _restaurant = restaurant;
    self.backgroundImageView.hidden = (self.restaurant.rPictureUrl == nil);
    if (self.restaurant.rPictureUrl) {
        FoodRestaurantInfoCell* welf __weak = self;
        
        
/*#warning REMOVE
         NSURLRequest* request = [[NSURLRequest alloc] initWithURL:[NSURL URLWithString:self.restaurant.rPictureUrl] cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:30.0];
         
         id<AFImageCache> cache = [UIImageView sharedImageCache];
         UIImage* cachedImage __block = [cache cachedImageForRequest:request];
         cachedImage = nil;
#warning END OF REMOVE*/
        
        NSURLRequest* request = [[NSURLRequest alloc] initWithURL:[NSURL URLWithString:self.restaurant.rPictureUrl]];
        id<AFImageCache> cache = [UIImageView sharedImageCache];
        UIImage* cachedImage __block = [cache cachedImageForRequest:request];
         
        if (cachedImage) {
            [NSTimer scheduledTimerWithTimeInterval:0.0 block:^{
                if (!welf.backgroundImageView) {
                    return;
                }
                welf.backgroundImageView.image = cachedImage;
            } repeats:NO];
            
        } else {
            [self.backgroundImageView setImageWithURLRequest:request placeholderImage:nil success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image) {
                if (!welf.backgroundImageView) {
                    return;
                }
                welf.backgroundImageView.image = image;
            } failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error) {
                //nothing to do
            }];
        }
    }
    
    self.satRateLabel.hidden = NO;
    NSString* satRateTitleString = NSLocalizedStringFromTable(@"satisfaction", @"FoodPlugin", nil);
    NSString* satRateString = self.restaurant.rRating.voteCount > 0 ? [NSString stringWithFormat:@"%.0lf%%", self.restaurant.rRating.ratingValue*100.0] : @"-%"; //show percentage
    NSString* nbVotesString = [NSString stringWithFormat:@"(%d %@)", self.restaurant.rRating.voteCount, self.restaurant.rRating.voteCount > 1 ? NSLocalizedStringFromTable(@"ratings", @"FoodPlugin", nil) : NSLocalizedStringFromTable(@"rating", @"FoodPlugin", nil)];
    NSString* fullSatRateString = [NSString stringWithFormat:@"%@ %@ %@", satRateString, satRateTitleString, nbVotesString];
    NSMutableAttributedString* satAttrString = [[NSMutableAttributedString alloc] initWithString:fullSatRateString];
    UIFont* rateFont = [UIFont systemFontOfSize:self.satRateLabel.font.fontDescriptor.pointSize];
    NSRange satRateStringRange = [fullSatRateString rangeOfString:satRateString];
    [satAttrString addAttribute:NSFontAttributeName value:rateFont range:satRateStringRange];
    UIColor* color = [self colorForRating:self.restaurant.rRating];
    [satAttrString addAttribute:NSForegroundColorAttributeName value:color range:satRateStringRange];
    
    self.satRateLabel.attributedText = satAttrString;
}

- (void)setShowRating:(BOOL)showRating {
    _showRating = showRating;
    self.satRateLabel.hidden = !showRating;
    if (!self.satRatLabelNullWidthConstraint) {
        self.satRatLabelNullWidthConstraint = [NSLayoutConstraint widthConstraint:0.0 forView:self.satRateLabel];
    }
    if (showRating) {
        self.satRateLabelLeftConstraint.constant = 5.0;
        [self.satRateLabel removeConstraint:self.satRatLabelNullWidthConstraint];
    } else {
        self.satRateLabelLeftConstraint.constant = 0.0;
        [self.satRateLabel addConstraint:self.satRatLabelNullWidthConstraint];
    }
}

/*
 * http://stackoverflow.com/questions/340209/generate-colors-between-red-and-green-for-a-power-meter
 */
- (UIColor*)colorForRating:(EpflRating*)rating {
    [PCUtils throwExceptionIfObject:rating notKindOfClass:[EpflRating class]];
    if (!rating.voteCount) {
        return [UIColor blackColor];
    }
    double hue = rating.ratingValue * 0.3; // Hue (note 0.4 = Green)
    double saturation = 1.0;
    double brightness = 0.7;
    return [UIColor colorWithHue:hue saturation:saturation brightness:brightness alpha:1.0];
}

#pragma mark - Dealloc

- (void)dealloc {
    [self.backgroundImageView cancelImageRequestOperation];
}

@end
