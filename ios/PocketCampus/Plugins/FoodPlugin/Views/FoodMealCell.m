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

@import CoreText;

static const CGFloat kMinHeight = 110.0;
static const CGFloat kTextViewWidth = 252.0;
static const CGFloat kBottomZoneHeight = 30.0;

@interface FoodMealCell ()

@property (nonatomic, strong) IBOutlet UIView* infoContentView;
@property (nonatomic, strong) IBOutlet UIImageView* mealTypeImageView;
@property (nonatomic, strong) IBOutlet UILabel* pricesLabel;
@property (nonatomic, strong) IBOutlet UITextView* textView;
@property (nonatomic, strong) IBOutlet UIButton* satRateButton;

@property (nonatomic, strong) IBOutlet UIView* rateControlsView;


@property (nonatomic, strong) IBOutlet NSLayoutConstraint* infoContentViewLeftConstraint;
@property (nonatomic, strong) IBOutlet NSLayoutConstraint* infoContentViewRightConstraint;
@property (nonatomic, strong) IBOutlet NSLayoutConstraint* textViewWidthConstraint;
@property (nonatomic, strong) IBOutlet NSLayoutConstraint* textViewBottomConstraint;

@property (nonatomic, strong) UILongPressGestureRecognizer* infoContentViewTapGesture; //actually using for touchDown, because tap gesture does not support it

@end

@implementation FoodMealCell

#pragma mark - Init

- (instancetype)initWithReuseIdentifier:(NSString*)reuseIdentifier {
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"FoodMealCell" owner:nil options:nil];
    self = (FoodMealCell*)elements[1];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.textViewWidthConstraint.constant = kTextViewWidth;
        self.textViewBottomConstraint.constant = kBottomZoneHeight;
        self.imageView.contentMode = UIViewContentModeScaleAspectFit;
        [self.satRateButton addTarget:self action:@selector(ratePressed) forControlEvents:UIControlEventTouchUpInside];
        self.infoContentViewTapGesture = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(infoContentViewTapped)];
        self.infoContentViewTapGesture.minimumPressDuration = 0.001;
        self.infoContentViewTapGesture.enabled = NO;
        [self.infoContentView addGestureRecognizer:self.infoContentViewTapGesture];
        [self.contentView insertSubview:self.rateControlsView belowSubview:self.infoContentView]; //doing that here and not in IB so that we can work on the view that is hidden by infoContentView otherwise :)
        self.rateControlsView.translatesAutoresizingMaskIntoConstraints = NO;
        [self.contentView addConstraints:[NSLayoutConstraint constraintsToSuperview:self.contentView forView:self.rateControlsView edgeInsets:UIEdgeInsetsMake(0, 0, 0, 0)]];
    }
    return self;
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    return [self initWithReuseIdentifier:reuseIdentifier];
}

- (void)setMeal:(EpflMeal *)meal {
    [PCUtils throwExceptionIfObject:meal notKindOfClass:[EpflMeal class]];
    _meal = meal;
    
    //Meal image

    NSNumber* primaryType = self.meal.mTypes.count > 0 ? (NSNumber*)(self.meal.mTypes[0]) : [NSNumber numberWithInteger:MealType_UNKNOWN];
    NSString* urlString = [[FoodService sharedInstanceToRetain] pictureUrlForMealType][primaryType];
    if (urlString) {
        [self.mealTypeImageView setImageWithURL:[NSURL URLWithString:urlString]];
    } else {
        self.mealTypeImageView.image = nil;
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
    [satRateAttrString addAttribute:NSForegroundColorAttributeName value:[UIColor darkGrayColor] range:[fullString rangeOfString:satRateString]];
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

- (void)infoContentViewTapped {
    [self setRateModeEnabled:NO animated:YES];
}

- (void)ratePressed {
    [self setRateModeEnabled:YES animated:YES];
}

- (void)setRateModeEnabled:(BOOL)rateModeEnabled {
    [self setRateModeEnabled:rateModeEnabled animated:NO];
}

- (void)setRateModeEnabled:(BOOL)rateModeEnabled animated:(BOOL)animated {
    _rateModeEnabled = rateModeEnabled;
    self.infoContentViewTapGesture.enabled = rateModeEnabled;
    CGFloat offset = rateModeEnabled ? self.contentView.frame.size.width-72.0 : 0.0;
    self.infoContentViewLeftConstraint.constant = -offset;
    self.infoContentViewRightConstraint.constant = offset;
    
    [UIView animateWithDuration:animated ? 0.3 : 0.0 animations:^{
        [self.contentView layoutIfNeeded];
    }];
}

#pragma mark - Dealloc

- (void)dealloc {
    [self.mealTypeImageView cancelImageRequestOperation];
}

@end
