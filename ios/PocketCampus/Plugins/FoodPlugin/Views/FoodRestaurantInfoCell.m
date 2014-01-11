



//  Created by Loïc Gardiol on 30.10.13.



#import "FoodRestaurantInfoCell.h"

#import "FoodService.h"

#import "MapController.h"

#import "FoodRestaurantViewController.h"

#import "UIImageView+AFNetworking.h"

#import "UIImage+Additions.h"

@interface FoodRestaurantInfoCell ()

@property (nonatomic, strong) IBOutlet UIImageView* backgroundImageView;
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
        return 36.5; //just top bar for satRateLabel and showOnMapButton
    }
    return [PCUtils isIdiomPad] ? 200.0 : 150.0;
}

- (void)setRestaurant:(EpflRestaurant *)restaurant {
    _restaurant = restaurant;
    self.backgroundImageView.hidden = (self.restaurant.rPictureUrl == nil);
    if (self.restaurant.rPictureUrl) {
        FoodRestaurantInfoCell* weakSelf __weak = self;
        
        NSURLRequest* request = [[NSURLRequest alloc] initWithURL:[NSURL URLWithString:self.restaurant.rPictureUrl]];
        [self.backgroundImageView setImageWithURLRequest:request placeholderImage:nil success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image) {
            CGFloat width = weakSelf.backgroundImageView.frame.size.width;
            CGFloat height = weakSelf.backgroundImageView.frame.size.height;
            [weakSelf.backgroundImageView addConstraints:[NSLayoutConstraint width:width height:height constraintsForView:weakSelf.backgroundImageView]];
            image = [image imageByScalingAndCroppingForSize:CGSizeMake(width, height) applyDeviceScreenMultiplyingFactor:YES];
            weakSelf.backgroundImageView.image = image;
        } failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error) {
            //nothing to do
        }];
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
