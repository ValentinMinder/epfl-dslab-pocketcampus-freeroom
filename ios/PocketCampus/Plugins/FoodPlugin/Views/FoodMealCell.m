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

//  Created by Loïc Gardiol on 01.11.13.

#import "FoodMealCell.h"

#import "FoodService.h"

#import "AuthenticationService.h"

#import "UIImageView+AFNetworking.h"

#import "NSTimer+Blocks.h"

#import "PCConfig.h"

@import CoreText;

typedef enum {
    RatingStatusReady,
    RatingStatusLoading,
    RatingStatusRated
} RatingStatus;

NSString* const kFoodMealCellDidEnableRateModeNotification = @"kFoodMealCellDidEnableRateModeNotification";
NSString* const kFoodMealCellUserSuccessfullyRatedMealNotification = @"kFoodMealCellUserSuccessfullyRatedMealNotification";

static BOOL ratingsEnabled = NO;

static const CGFloat kMinHeight = 110.0;
static const CGFloat kmealTypeImageViewLeftConstraintPhone = 10.0;
static const CGFloat kmealTypeImageViewLeftConstraintPad = 25.0;
static const CGFloat kmealTypeImageViewDefaultHeightConstraint = 50.0;
static const CGFloat kTextViewLeftConstraintPhone = 68.0;
static const CGFloat kTextViewLeftConstraintPad = 83.0;
static const CGFloat kRatingsEnabledHorizontalLineHeight = 0.5;
static const CGFloat kRatingsDisabledHorizontalLineHeight = 0.0;
static const CGFloat kRatingsEnabledBottomZoneHeight = 30.0;
static const CGFloat kRatingsDisabledBottomZoneHeight = 0.0;
static const CGFloat kRateControlsViewWidth = 248.0;

@interface FoodMealCell ()<FoodServiceDelegate>

@property (nonatomic, copy, readwrite) NSString* reuseIdentifier;

@property (nonatomic, strong) FoodService* foodService;

@property (nonatomic) UIEdgeInsets originalSeparatorInsets;

@property (nonatomic, strong) IBOutlet UIView* infoContentView;
@property (nonatomic, strong) IBOutlet UIImageView* mealTypeImageView;
@property (nonatomic, strong) IBOutlet UILabel* pricesLabel;
@property (nonatomic, strong) IBOutlet UITextView* textView;
@property (nonatomic, strong) IBOutlet UIButton* satRateButton;

@property (nonatomic, strong) IBOutlet UIView* rateControlsView;
@property (nonatomic, strong) IBOutlet UILabel* rateControlsViewTopLabel;
@property (nonatomic) RatingStatus ratingStatus;
@property (nonatomic, strong) IBOutlet UIButton* happyButton;
@property (nonatomic, strong) IBOutlet UIButton* mehButton;
@property (nonatomic, strong) IBOutlet UIButton* sadButton;

@property (nonatomic, strong) IBOutlet NSLayoutConstraint* infoContentViewLeftConstraint;
@property (nonatomic, strong) IBOutlet NSLayoutConstraint* infoContentViewRightConstraint;
@property (nonatomic, strong) IBOutlet NSLayoutConstraint* mealTypeImageViewLeftConstraint;
@property (nonatomic, strong) IBOutlet NSLayoutConstraint* mealTypeImageViewHeightConstraint;
@property (nonatomic, strong) IBOutlet NSLayoutConstraint* textViewWidthConstraint;
@property (nonatomic, strong) IBOutlet NSLayoutConstraint* textViewBottomConstraint;
@property (nonatomic, strong) IBOutlet NSLayoutConstraint* horizontalLineHeightConstraint;
@property (nonatomic, strong) NSLayoutConstraint* rateControlsViewLeftConstraint;

@property (nonatomic, strong) UILongPressGestureRecognizer* infoContentViewTapGesture; //actually using for touchDown, because tap gesture does not support it

- (IBAction)smileyButtonTapped:(id)sender; //not see by IB otherwise

@end

@implementation FoodMealCell

@synthesize reuseIdentifier = _reuseIdentifier;

#pragma mark - Init

+ (void)initialize {
    ratingsEnabled = [[PCConfig defaults] boolForKey:PC_CONFIG_FOOD_RATINGS_ENABLED];
}

- (instancetype)initWithReuseIdentifier:(NSString*)reuseIdentifier {
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"FoodMealCell" owner:nil options:nil];
    self = (FoodMealCell*)elements[0];
    if (self) {
        self.reuseIdentifier = reuseIdentifier;
        self.foodService = [FoodService sharedInstanceToRetain];
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.originalSeparatorInsets = self.separatorInset;
        self.textView.scrollEnabled = NO;
        self.textView.isAccessibilityElement = NO; //will generate custom accessiblityLabel for whole cell
        self.mealTypeImageView.isAccessibilityElement = NO;
        self.pricesLabel.isAccessibilityElement = NO;
        self.satRateButton.isAccessibilityElement = NO;
        self.mealTypeImageViewLeftConstraint.constant = [PCUtils isIdiomPad] ? kmealTypeImageViewLeftConstraintPad : kmealTypeImageViewLeftConstraintPhone;
        self.textViewBottomConstraint.constant = [self.class bottomZoneHeight];
        self.horizontalLineHeightConstraint.constant = ratingsEnabled ? kRatingsEnabledHorizontalLineHeight : kRatingsDisabledHorizontalLineHeight;
        self.imageView.contentMode = UIViewContentModeScaleAspectFit;
        [self.satRateButton addTarget:self action:@selector(ratePressed) forControlEvents:UIControlEventTouchUpInside];
        self.infoContentViewTapGesture = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(infoContentViewTapped)];
        self.infoContentViewTapGesture.minimumPressDuration = 0.001;
        self.infoContentViewTapGesture.enabled = NO;
        [self.infoContentView addGestureRecognizer:self.infoContentViewTapGesture];
        self.rateControlsView.hidden = !ratingsEnabled;
        self.satRateButton.hidden = !ratingsEnabled;
        self.rateControlsView.isAccessibilityElement = NO;
        self.rateControlsView.accessibilityElementsHidden = YES;
        [self.contentView insertSubview:self.rateControlsView aboveSubview:self.infoContentView]; //doing that here and not in IB so that we can work on the view that is hidden by infoContentView otherwise :)
        self.rateControlsView.translatesAutoresizingMaskIntoConstraints = NO;
        [self.contentView addConstraints:[NSLayoutConstraint constraintsToSuperview:self.contentView forView:self.rateControlsView edgeInsets:UIEdgeInsetsMake(0, kNoInsetConstraint, 0, kNoInsetConstraint)]];
        self.rateControlsViewLeftConstraint = [NSLayoutConstraint constraintWithItem:self.infoContentView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.rateControlsView attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0.0];
        [self.contentView addConstraint:self.rateControlsViewLeftConstraint];
        [self.rateControlsView addConstraint:[NSLayoutConstraint widthConstraint:kRateControlsViewWidth forView:self.rateControlsView]];
        self.ratingStatus = RatingStatusReady;
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(rateModeEnabledNotification:) name:kFoodMealCellDidEnableRateModeNotification object:nil];
    }
    return self;
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    return [self initWithReuseIdentifier:reuseIdentifier];
}

#pragma mark - UIView overrides

- (void)layoutSubviews {
    [super layoutSubviews];
    [self setRateModeEnabled:self.rateModeEnabled animated:NO postNotif:NO force:YES];
}

#pragma mark - UITableViewCell overrides

- (void)prepareForReuse {
    [super prepareForReuse];
    [self setRateModeEnabled:NO animated:NO postNotif:NO force:NO]; //if cell being reused, back to not rating mode
}

#pragma mark - Notifications listening

- (void)rateModeEnabledNotification:(NSNotification*)notif {
    if (notif.object != self) {
        /*
         * If other cell sets shows rate controls view, hide rate controls view of this cell
         * (we want only one cell at a time to show rate controls view)
         */
        [self setRateModeEnabled:NO animated:YES postNotif:NO force:NO];
    }
}

#pragma mark - Meal stuff

- (void)setMeal:(EpflMeal *)meal {
    [PCUtils throwExceptionIfObject:meal notKindOfClass:[EpflMeal class]];
    _meal = meal;
    
    //Meal image
    NSNumber* primaryType = self.meal.mTypes.count > 0 ? (NSNumber*)(self.meal.mTypes[0]) : [NSNumber numberWithInteger:MealType_UNKNOWN];
    NSString* urlString = [[FoodService sharedInstanceToRetain] pictureUrlForMealType][primaryType];
    if (urlString) {
        NSURLRequest* req = [[NSURLRequest alloc] initWithURL:[NSURL URLWithString:urlString]];
        __weak __typeof(self) welf = self;
        [self.mealTypeImageView setImageWithURLRequest:req placeholderImage:nil success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image) {
            welf.mealTypeImageView.image = image;
            welf.mealTypeImageViewHeightConstraint.constant = kmealTypeImageViewDefaultHeightConstraint;
        } failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error) {
            welf.mealTypeImageView.image = nil;
            welf.mealTypeImageViewHeightConstraint.constant = 3.0;
        }];
    } else {
        self.mealTypeImageView.image = nil;
        self.mealTypeImageViewHeightConstraint.constant = 3.0;
    }
    
    // Meal text
    self.textView.attributedText = [self.class attributedStringForMeal:meal];
    
    // Prices
    float price = [self.meal.mPrices[[NSNumber numberWithInteger:[[FoodService sharedInstanceToRetain] userPriceTarget]]] floatValue];
    if (price == 0.0) {
        price = [self.meal.mPrices[[NSNumber numberWithInteger:PriceTarget_ALL]] floatValue];
    }
    if (price == 0.0) {
        price = [self.meal.mPrices[[NSNumber numberWithInteger:PriceTarget_VISITOR]] floatValue];
    }
    if (price == 0.0) {
        price = [self.meal.mPrices[[NSNumber numberWithInteger:PriceTarget_STAFF]] floatValue];
    }
 
    if (price > 0.0) {
        BOOL integer = ((price - (float)((int)price)) == 0.0);
        NSString* priceString = nil;
        NSString* currencyString = @"CHF";
        if (integer) {
            priceString = [NSString stringWithFormat:@"%.0f\nCHF", price];
        } else {
            priceString = [NSString stringWithFormat:@"%.2f\nCHF", price];
        }
        self.pricesLabel.numberOfLines = 2;
        NSMutableAttributedString* attrPriceString = [[NSMutableAttributedString alloc] initWithString:priceString];
        [attrPriceString addAttribute:NSFontAttributeName value:[self.pricesLabel.font fontWithSize:self.pricesLabel.font.pointSize-6.0] range:[priceString rangeOfString:currencyString]];
        
        
        //self.pricesLabel.numberOfLines = integer ? 1 : 2;
        
        /*NSNumberFormatter* numberFormatter = [NSNumberFormatter new];
        numberFormatter.numberStyle = NSNumberFormatterDecimalStyle;
        numberFormatter.locale = [NSLocale localeWithLocaleIdentifier:@"fr_CH"]; //force swiss franc notation
        NSString* priceString = [numberFormatter stringFromNumber:[NSNumber numberWithFloat:price]];*/
        self.pricesLabel.attributedText = attrPriceString;
        self.accessibilityLabel = [NSString stringWithFormat:NSLocalizedStringFromTable(@"MealWithFormatDescriptionAndPrice", @"FoodPlugin", nil), self.textView.text, attrPriceString.string];
    } else {
        self.pricesLabel.numberOfLines = 2;
        NSAttributedString* attrString = [[NSAttributedString alloc] initWithString:NSLocalizedStringFromTable(@"UnknownPrice", @"FoodPlugin", nil) attributes:@{NSFontAttributeName:[self.pricesLabel.font fontWithSize:self.pricesLabel.font.pointSize-6.0]}];
        self.pricesLabel.attributedText = attrString;
        self.accessibilityLabel = self.textView.text;
    }
    
    // Rating
    NSString* satRateString = nil;
    if (self.meal.mRating.voteCount > 0) {
        satRateString = [NSString stringWithFormat:@"%.0lf%% %@ (%d %@) – ", self.meal.mRating.ratingValue*100, NSLocalizedStringFromTable(@"satisfaction", @"FoodPlugin", nil), self.meal.mRating.voteCount, self.meal.mRating.voteCount > 1 ? NSLocalizedStringFromTable(@"ratings", @"FoodPlugin", nil) : NSLocalizedStringFromTable(@"rating", @"FoodPlugin", nil)];
    } else {
        satRateString = [NSString stringWithFormat:@"%@ – ", NSLocalizedStringFromTable(@"NoRating", @"FoodPlugin", nil)];
    }
    NSString* voteString =  NSLocalizedStringFromTable(@"Rate", @"FoodPlugin", nil);
    NSString* fullString = [NSString stringWithFormat:@"%@%@      ", satRateString, voteString]; //spaces to extent touch zone of button
    NSMutableAttributedString* satRateAttrString = [[NSMutableAttributedString alloc] initWithString:fullString];
    if (self.meal.mRating.voteCount > 0) {
        [satRateAttrString addAttributes:@{NSForegroundColorAttributeName:[UIColor colorWithWhite:0.2 alpha:1.0],
                                           NSFontAttributeName:[UIFont boldSystemFontOfSize:self.satRateButton.titleLabel.font.fontDescriptor.pointSize]}
                                   range:[fullString rangeOfString:satRateString]];
    } else {
        [satRateAttrString addAttributes:@{NSForegroundColorAttributeName:[UIColor lightGrayColor],
                                           NSFontAttributeName:[UIFont systemFontOfSize:self.satRateButton.titleLabel.font.fontDescriptor.pointSize]}
                                   range:[fullString rangeOfString:satRateString]];
    }
    
    //It shouldn't be necessary as default color for button is tintColor of window, which is PocketCampus red.
    //But this is ignored by iOS 7.1 apparently (falling back to default default color => blue)
    [satRateAttrString addAttribute:NSForegroundColorAttributeName value:[PCValues pocketCampusRed] range:[fullString rangeOfString:voteString]];
    
    [self.satRateButton setAttributedTitle:satRateAttrString forState:UIControlStateNormal];
}

+ (CGFloat)preferredHeightForMeal:(EpflMeal*)meal inTableView:(UITableView*)tableView {
    [PCUtils throwExceptionIfObject:meal notKindOfClass:[EpflMeal class]];
    NSAttributedString* attrString = [self attributedStringForMeal:meal];
    CTFramesetterRef framesetter = CTFramesetterCreateWithAttributedString((__bridge CFAttributedStringRef)attrString);
    CGSize targetSize = CGSizeMake(tableView.bounds.size.width - ([PCUtils isIdiomPad] ? kTextViewLeftConstraintPad : kTextViewLeftConstraintPhone), CGFLOAT_MAX); //account for text left and right insets of the text view
    CGSize size = CTFramesetterSuggestFrameSizeWithConstraints(framesetter, CFRangeMake(0, [attrString length]), NULL, targetSize, NULL);
    CFRelease(framesetter);
    CGFloat finalHeight = size.height + [self bottomZoneHeight] + (ratingsEnabled ? 22.0 : 0.0); //give some margin so that text is not too tight between top and bottom lines
    return finalHeight > kMinHeight ? finalHeight : kMinHeight;
}

+ (NSAttributedString*)attributedStringForMeal:(EpflMeal*)meal {
    [PCUtils throwExceptionIfObject:meal notKindOfClass:[EpflMeal class]];
    NSString* fullString = meal.mName;
    if (meal.mDescription.length > 0) {
        fullString = [fullString stringByAppendingFormat:@"\n%@", meal.mDescription];
    }
    NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:fullString];
    [attrString addAttribute:NSFontAttributeName value:[UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline] range:[fullString rangeOfString:meal.mName]];
    [attrString addAttribute:NSFontAttributeName value:[UIFont preferredFontForTextStyle:UIFontTextStyleFootnote] range:[fullString rangeOfString:meal.mDescription]];
    return attrString;
}

+ (CGFloat)bottomZoneHeight {
    return ratingsEnabled ? kRatingsEnabledBottomZoneHeight : kRatingsDisabledBottomZoneHeight;
}

#pragma mark - Ratings

- (void)setRatingStatus:(RatingStatus)ratingStatus {
    _ratingStatus = ratingStatus;
    switch (ratingStatus) {
        case RatingStatusReady:
            self.rateControlsViewTopLabel.text = NSLocalizedStringFromTable(@"RatingStatusReadyText", @"FoodPlugin", nil);
            self.happyButton.alpha = 1.0;
            self.mehButton.alpha = 1.0;
            self.sadButton.alpha = 1.0;
            self.happyButton.enabled = YES;
            self.mehButton.enabled = YES;
            self.sadButton.enabled = YES;
            break;
        case RatingStatusLoading:
            self.rateControlsViewTopLabel.text = NSLocalizedStringFromTable(@"Loading...", @"PocketCampus", nil);
            self.happyButton.enabled = NO;
            self.mehButton.enabled = NO;
            self.sadButton.enabled = NO;
            break;
        case RatingStatusRated:
            self.rateControlsViewTopLabel.text = NSLocalizedStringFromTable(@"RatingStatusRatedText", @"FoodPlugin", nil);
            self.happyButton.enabled = NO;
            self.mehButton.enabled = NO;
            self.sadButton.enabled = NO;
            break;
        default:
            break;
    }
}

- (void)infoContentViewTapped {
    [self setRateModeEnabled:NO animated:YES postNotif:NO force:NO];
}

- (void)ratePressed {
    [self setRateModeEnabled:YES animated:YES postNotif:YES force:NO];
}

- (void)setRateModeEnabled:(BOOL)rateModeEnabled {
    [self setRateModeEnabled:rateModeEnabled animated:NO postNotif:NO force:NO];
}

- (void)setRateModeEnabled:(BOOL)rateModeEnabled animated:(BOOL)animated postNotif:(BOOL)postNotif force:(BOOL)force {
    if (!force && _rateModeEnabled == rateModeEnabled) {
        return;
    }
    if (!_rateModeEnabled && rateModeEnabled && postNotif) {
        [[NSNotificationCenter defaultCenter] postNotificationName:kFoodMealCellDidEnableRateModeNotification object:self];
    }
    _rateModeEnabled = rateModeEnabled;
    self.infoContentViewTapGesture.enabled = rateModeEnabled;
    self.satRateButton.enabled = !rateModeEnabled;
    self.satRateButton.alpha = rateModeEnabled ? 0.5 : 1.0;
    
    CGFloat offset = rateModeEnabled ? kRateControlsViewWidth : 0.0;
    self.rateControlsViewLeftConstraint.constant = 0.0;
    //self.separatorInset = rateModeEnabled ? UIEdgeInsetsZero : self.originalSeparatorInsets;
    self.infoContentViewLeftConstraint.constant = -offset;
    self.infoContentViewRightConstraint.constant = offset;
    
    [UIView animateWithDuration:animated ? 0.3 : 0.0 animations:^{
        [self layoutIfNeeded];
    }];
}

- (IBAction)smileyButtonTapped:(id)sender {
    double ratingValue = -1.0;
    CGFloat dimmedAlpha = 0.2;
    if (sender == self.happyButton) {
        ratingValue = 1.0;
        self.mehButton.alpha = dimmedAlpha;
        self.sadButton.alpha = dimmedAlpha;
    } else if (sender == self.mehButton) {
        ratingValue = 0.5;
        self.happyButton.alpha = dimmedAlpha;
        self.sadButton.alpha = dimmedAlpha;
    } else if (sender == self.sadButton) {
        ratingValue = 0.0;
        self.happyButton.alpha = dimmedAlpha;
        self.mehButton.alpha = dimmedAlpha;
    } else {
        //should not happen
        return;
    }
    [[PCGAITracker sharedTracker] trackAction:@"RateMeal" inScreenWithName:self.screenNameForGoogleAnalytics contentInfo:[NSString stringWithFormat:@"%lld-%@", self.meal.mId, self.meal.mName]];
    self.ratingStatus = RatingStatusLoading;
    NSString* identifier = [PCUtils uniqueDeviceIdentifier];
    VoteRequest* req = [[VoteRequest alloc] initWithMealId:self.meal.mId rating:ratingValue deviceId:identifier];
    [self.foodService voteForRequest:req delegate:self];
}

#pragma mark - FoodServiceDelegate

- (void)voteForRequest:(VoteRequest *)request didReturn:(VoteResponse *)response {
    switch (response.submitStatus) {
        case SubmitStatus_VALID:
        {
            self.ratingStatus = RatingStatusRated;
            __weak __typeof(self) welf = self;
            [NSTimer scheduledTimerWithTimeInterval:1.0 block:^{
                [welf infoContentViewTapped]; //hide rating controls, not longer need them
            } repeats:NO];
            [NSTimer scheduledTimerWithTimeInterval:1.4 block:^{
                [[NSNotificationCenter defaultCenter] postNotificationName:kFoodMealCellUserSuccessfullyRatedMealNotification object:welf];
            } repeats:NO];
            break;
        }
        case SubmitStatus_ALREADY_VOTED:
        {
            self.ratingStatus = RatingStatusReady;
            [[[UIAlertView alloc] initWithTitle:nil message:NSLocalizedStringFromTable(@"RatingAlreadyDone", @"FoodPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
            [self infoContentViewTapped]; //hide rating controls, not longer need them
            break;
        }
        case SubmitStatus_TOO_EARLY:
            self.ratingStatus = RatingStatusReady;
            [[[UIAlertView alloc] initWithTitle:nil message:NSLocalizedStringFromTable(@"RatingTooEarly", @"FoodPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
            [self infoContentViewTapped]; //hide rating controls, not longer need them
            break;
        case SubmitStatus_ERROR:
            [self ratingError];
            break;
        default:
            [self ratingError];
            break;
    }
}

- (void)voteFailedForRequest:(VoteRequest *)request {
    [self ratingError];
}

- (void)ratingError {
    self.ratingStatus = RatingStatusReady;
    [PCUtils showServerErrorAlert];
}

- (void)serviceConnectionToServerFailed {
    if (self.ratingStatus == RatingStatusLoading) {
        self.ratingStatus = RatingStatusReady;
        [PCUtils showConnectionToServerTimedOutAlert];
    }
}

#pragma mark - Dealloc

- (void)dealloc {
    [self.mealTypeImageView cancelImageRequestOperation];
    [self.foodService cancelOperationsForDelegate:self];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
