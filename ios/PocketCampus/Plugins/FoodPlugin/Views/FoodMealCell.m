//
//  FoodMealCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "FoodMealCell.h"

#import "FoodService.h"

#import "UIImageView+AFNetworking.h"

@import CoreText;

static const CGFloat kTextViewWidth = 252.0;

@interface FoodMealCell ()

@property (nonatomic, strong) IBOutlet UIImageView* mealTypeImageView;
@property (nonatomic, strong) IBOutlet UITextView* textView;

@property (nonatomic, strong) IBOutlet NSLayoutConstraint* textViewWidthConstraint;

@end

@implementation FoodMealCell

#pragma mark - Init

- (instancetype)initWithReuseIdentifier:(NSString*)reuseIdentifier {
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"FoodMealCell" owner:nil options:nil];
    self = (FoodMealCell*)elements[0];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.textViewWidthConstraint.constant = kTextViewWidth;
        self.imageView.contentMode = UIViewContentModeScaleAspectFit;
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
    self.textView.attributedText = [self.class attributedStringForMeal:meal];
#warning TODO use
#warning TO REMOVE
    [self.mealTypeImageView setImageWithURL:[NSURL URLWithString:@"http://pocketcampus.epfl.ch/backend/meals-pics/meat.png"]];
}

+ (CGFloat)preferredHeightForMeal:(EpflMeal*)meal {
    [PCUtils throwExceptionIfObject:meal notKindOfClass:[EpflMeal class]];
    NSAttributedString* attrString = [self attributedStringForMeal:meal];
    CTFramesetterRef framesetter = CTFramesetterCreateWithAttributedString((__bridge CFAttributedStringRef)attrString);
    CGSize targetSize = CGSizeMake(kTextViewWidth, CGFLOAT_MAX);
    CGSize size = CTFramesetterSuggestFrameSizeWithConstraints(framesetter, CFRangeMake(0, [attrString length]), NULL, targetSize, NULL);
    CFRelease(framesetter);
    CGFloat finalHeight = size.height+20.0; //give some margin
    return finalHeight > 60.0 ? finalHeight : 60.0;
}

+ (NSAttributedString*)attributedStringForMeal:(EpflMeal*)meal {
    [PCUtils throwExceptionIfObject:meal notKindOfClass:[EpflMeal class]];
    NSString* fullString = [NSString stringWithFormat:@"%@%@%@", meal.mName, meal.mDescription.length > 0 ? @"\n" : @"", meal.mDescription.length > 0 ? meal.mDescription : @""];
    NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:fullString];
    [attrString addAttribute:NSFontAttributeName value:[UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline] range:[fullString rangeOfString:meal.mName]];
    [attrString addAttribute:NSFontAttributeName value:[UIFont preferredFontForTextStyle:UIFontTextStyleCaption1] range:[fullString rangeOfString:meal.mDescription]];
    return attrString;
}

#pragma mark - Dealloc

- (void)dealloc {
    [self.mealTypeImageView cancelImageRequestOperation];
}

@end
