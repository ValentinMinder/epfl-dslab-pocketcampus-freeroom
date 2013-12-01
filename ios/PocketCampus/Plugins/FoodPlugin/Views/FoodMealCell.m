//
//  FoodMealCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "FoodMealCell.h"

#import "FoodService.h"

#import "AuthenticationService.h"

#import "UIImageView+AFNetworking.h"

#import "NSTimer+Blocks.h"

@import CoreText;

typedef enum {
    RatingStatusReady,
    RatingStatusLoading,
    RatingStatusRated
} RatingStatus;

static const CGFloat kMinHeight = 110.0;
static const CGFloat kmealTypeImageViewLeftConstraintPhone = 10.0;
static const CGFloat kmealTypeImageViewLeftConstraintPad = 25.0;
static const CGFloat kmealTypeImageViewDefaultHeightConstraint = 50.0;
static const CGFloat kTextViewWidth = 252.0;
static const CGFloat kBottomZoneHeight = 30.0;
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
@property (nonatomic, strong) NSLayoutConstraint* rateControlsViewLeftConstraint;

@property (nonatomic, strong) UILongPressGestureRecognizer* infoContentViewTapGesture; //actually using for touchDown, because tap gesture does not support it

- (IBAction)smileyButtonTapped:(id)sender; //not see by IB otherwise

@end

@implementation FoodMealCell

#pragma mark - Init

- (instancetype)initWithReuseIdentifier:(NSString*)reuseIdentifier {
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"FoodMealCell" owner:nil options:nil];
    self = (FoodMealCell*)elements[0];
    if (self) {
        self.reuseIdentifier = reuseIdentifier;
        self.foodService = [FoodService sharedInstanceToRetain];
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.originalSeparatorInsets = self.separatorInset;
        self.mealTypeImageViewLeftConstraint.constant = [PCUtils isIdiomPad] ? kmealTypeImageViewLeftConstraintPad : kmealTypeImageViewLeftConstraintPhone;
        self.textViewWidthConstraint.constant = kTextViewWidth;
        self.textViewBottomConstraint.constant = kBottomZoneHeight;
        self.imageView.contentMode = UIViewContentModeScaleAspectFit;
        [self.satRateButton addTarget:self action:@selector(ratePressed) forControlEvents:UIControlEventTouchUpInside];
        self.infoContentViewTapGesture = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(infoContentViewTapped)];
        self.infoContentViewTapGesture.minimumPressDuration = 0.001;
        self.infoContentViewTapGesture.enabled = NO;
        [self.infoContentView addGestureRecognizer:self.infoContentViewTapGesture];
        [self.contentView insertSubview:self.rateControlsView aboveSubview:self.infoContentView]; //doing that here and not in IB so that we can work on the view that is hidden by infoContentView otherwise :)
        self.rateControlsView.translatesAutoresizingMaskIntoConstraints = NO;
        [self.contentView addConstraints:[NSLayoutConstraint constraintsToSuperview:self.contentView forView:self.rateControlsView edgeInsets:UIEdgeInsetsMake(0, kNoInsetConstraint, 0, kNoInsetConstraint)]];
        self.rateControlsViewLeftConstraint = [NSLayoutConstraint constraintWithItem:self.infoContentView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.rateControlsView attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0.0];
        [self.contentView addConstraint:self.rateControlsViewLeftConstraint];
        [self.rateControlsView addConstraint:[NSLayoutConstraint widthConstraint:kRateControlsViewWidth forView:self.rateControlsView]];
        self.ratingStatus = RatingStatusReady;
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(rateModeEnabledNotification:) name:FoodMealCellDidEnableRateModeNotification object:nil];
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
        FoodMealCell* weakSelf __weak = self;
        [self.mealTypeImageView setImageWithURLRequest:req placeholderImage:nil success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image) {
            weakSelf.mealTypeImageView.image = image;
            weakSelf.mealTypeImageViewHeightConstraint.constant = kmealTypeImageViewDefaultHeightConstraint;
        } failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error) {
            weakSelf.mealTypeImageView.image = nil;
            weakSelf.mealTypeImageViewHeightConstraint.constant = 3.0;
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
        NSNumberFormatter* numberFormatter = [NSNumberFormatter new];
        numberFormatter.numberStyle = NSNumberFormatterCurrencyStyle;
        numberFormatter.locale = [NSLocale localeWithLocaleIdentifier:@"fr_CH"]; //force swiss franc notation
        NSString* priceString = [numberFormatter stringFromNumber:[NSNumber numberWithFloat:price]];
        priceString = [priceString stringByReplacingOccurrencesOfString:@"CHF" withString:@""];
        self.pricesLabel.text = priceString;
    } else {
        self.pricesLabel.text = nil;
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
    

    [self.satRateButton setAttributedTitle:satRateAttrString forState:UIControlStateNormal];
}

+ (CGFloat)preferredHeightForMeal:(EpflMeal*)meal {
    [PCUtils throwExceptionIfObject:meal notKindOfClass:[EpflMeal class]];
    NSAttributedString* attrString = [self attributedStringForMeal:meal];
    CTFramesetterRef framesetter = CTFramesetterCreateWithAttributedString((__bridge CFAttributedStringRef)attrString);
    CGSize targetSize = CGSizeMake(kTextViewWidth-10.0, CGFLOAT_MAX); //account for text left and right insets of the text view
    CGSize size = CTFramesetterSuggestFrameSizeWithConstraints(framesetter, CFRangeMake(0, [attrString length]), NULL, targetSize, NULL);
    CFRelease(framesetter);
    CGFloat finalHeight = size.height + kBottomZoneHeight + 16.0; //give some margin so that text is not too tight between top and bottom lines
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
    [attrString addAttribute:NSFontAttributeName value:[UIFont preferredFontForTextStyle:UIFontTextStyleCaption1] range:[fullString rangeOfString:meal.mDescription]];
    return attrString;
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
        [[NSNotificationCenter defaultCenter] postNotificationName:FoodMealCellDidEnableRateModeNotification object:self];
    }
    _rateModeEnabled = rateModeEnabled;
    self.infoContentViewTapGesture.enabled = rateModeEnabled;
    self.satRateButton.enabled = !rateModeEnabled;
    self.satRateButton.alpha = rateModeEnabled ? 0.5 : 1.0;
    
    if ([PCUtils isIdiomPad] &&  !UIDeviceOrientationIsPortrait([[UIDevice currentDevice] orientation])) {
        self.rateControlsViewLeftConstraint.constant = rateModeEnabled ? kRateControlsViewWidth : 0.0;
        //self.separatorInset = rateModeEnabled ? UIEdgeInsetsZero : self.originalSeparatorInsets;
        self.infoContentViewLeftConstraint.constant = 0.0;
        self.infoContentViewRightConstraint.constant = 0.0;
    } else {
        CGFloat offset = rateModeEnabled ? kRateControlsViewWidth : 0.0;
        self.rateControlsViewLeftConstraint.constant = 0.0;
        //self.separatorInset = rateModeEnabled ? UIEdgeInsetsZero : self.originalSeparatorInsets;
        self.infoContentViewLeftConstraint.constant = -offset;
        self.infoContentViewRightConstraint.constant = offset;
    }
    
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
    self.ratingStatus = RatingStatusLoading;
    VoteRequest* req = [[VoteRequest alloc] initWithMealId:self.meal.mId rating:ratingValue deviceId:[PCUtils uniqueDeviceIdentifier]];
    [self.foodService voteForRequest:req delegate:self];
}

#pragma mark - FoodServiceDelegate

- (void)voteForRequest:(VoteRequest *)request didReturn:(VoteResponse *)response {
    switch (response.submitStatus) {
        case SubmitStatus_VALID:
        {
            self.ratingStatus = RatingStatusRated;
            FoodMealCell* weakSelf __weak = self;
            [NSTimer scheduledTimerWithTimeInterval:1.0 block:^{
                [weakSelf infoContentViewTapped]; //hide rating controls, not longer need them
            } repeats:NO];
            break;
        }
        case SubmitStatus_ALREADY_VOTED:
            self.ratingStatus = RatingStatusReady;
            [[[UIAlertView alloc] initWithTitle:nil message:NSLocalizedStringFromTable(@"RatingAlreadyDone", @"FoodPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
            [self infoContentViewTapped]; //hide rating controls, not longer need them
            break;
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

- (void)serviceConnectionToServerTimedOut {
    if (self.ratingStatus == RatingStatusLoading) {
        self.ratingStatus = RatingStatusReady;
        [PCUtils showConnectionToServerTimedOutAlert];
    }
}

#pragma mark - Dealloc

- (void)dealloc {
    [self.mealTypeImageView cancelImageRequestOperation];
    [self.foodService cancelOperationsForDelegate:self];
    @try {
        [[NSNotificationCenter defaultCenter] removeObserver:self];
    }
    @catch (NSException *exception) {}
}

@end
