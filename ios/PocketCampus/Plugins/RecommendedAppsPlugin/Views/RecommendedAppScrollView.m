//
//  LOChatPartnerBubblesScrollView.m
//  Nowy
//
//  Created by Loïc Gardiol on 22.07.14.
//  Copyright (c) 2014 Locus. All rights reserved.
//

#import "RecommendedAppScrollView.h"
#import "RecommendedAppThumbView.h"

@interface RecommendedAppScrollView ()

@property (nonatomic, weak) UILabel* titleLabel;

@end

@implementation RecommendedAppScrollView

#pragma mark - Init

- (void)defaultInit {
    [self addConstraint:[NSLayoutConstraint heightConstraint:[self.class requiredHeight] forView:self]];
}

- (instancetype)init {
    self = [super init];
    if (self) {
        [self defaultInit];
    }
    return self;
}

- (instancetype)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self defaultInit];
    }
    return self;
}

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self defaultInit];
    }
    return self;
}

#pragma mark - Public

- (void)setTitle:(NSString *)title {
    _title = title;
    self.titleLabel.text = title;
}

- (void)setAppItems:(NSArray *)appItems{
    _appItems = appItems;
    [self update];
}

#pragma mark - Actions

- (void)appItemBubbleTapped:(UITapGestureRecognizer*)tapGesture {
    RecommendedAppThumbView* bubble = (RecommendedAppThumbView*)tapGesture.view;
    if (![bubble isKindOfClass:[RecommendedAppThumbView class]]) {
        return;
    }
    if (self.appThumbTapped) {
        self.appThumbTapped(bubble);
    }
}

#pragma mark - Private

- (void)update {
    for (UIView* subview in self.subviews) {
        [subview removeFromSuperview];
    }
    
    static CGFloat const kBubblesMargin = 5.0;
    
    __weak __typeof(self) welf = self;
    
    NSMutableArray* bubbles = [NSMutableArray arrayWithCapacity:self.appItems.count];
    
    [self.appItems enumerateObjectsUsingBlock:^(RecommendedApp* item, NSUInteger index, BOOL *stop) {
        RecommendedAppThumbView* bubble = [[RecommendedAppThumbView alloc] initWithRecommendedApp:item];
        NSLayoutConstraint* leftConstraint = nil;
        NSLayoutConstraint* rightConstraint = nil;
        NSLayoutConstraint* verticalCenterConstraint = [NSLayoutConstraint constraintForCenterYtoSuperview:self forView:bubble constant:0.0];
        
        if (index == 0) {
            UILabel* titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 1, 1)];
            titleLabel.translatesAutoresizingMaskIntoConstraints = NO;
            titleLabel.numberOfLines = 0;
            titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:16.0];
            titleLabel.textColor = [UIColor darkGrayColor];
            titleLabel.textAlignment = NSTextAlignmentRight;
            titleLabel.text = self.title;
            titleLabel.preferredMaxLayoutWidth = 100.0;
            //titleLabel.backgroundColor = [UIColor yellowColor];
            [titleLabel addConstraint:[NSLayoutConstraint heightConstraint:[self.class requiredHeight] forView:titleLabel]];
            [self addSubview:titleLabel];
            [self addConstraints:[NSLayoutConstraint constraintsToSuperview:self forView:titleLabel edgeInsets:UIEdgeInsetsMake(0.0, kBubblesMargin, 0.0, kNoInsetConstraint)]];
            [titleLabel sizeToFit];
            
            leftConstraint = [NSLayoutConstraint constraintWithItem:bubble attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:titleLabel attribute:NSLayoutAttributeRight multiplier:1.0 constant:kBubblesMargin];
        } else {
            leftConstraint = [NSLayoutConstraint constraintWithItem:bubble attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:bubbles[index-1] attribute:NSLayoutAttributeRight multiplier:1.0 constant:kBubblesMargin];
            if (index == self.appItems.count - 1) {
                rightConstraint = [NSLayoutConstraint constraintsToSuperview:self forView:bubble edgeInsets:UIEdgeInsetsMake(kNoInsetConstraint, kNoInsetConstraint, kNoInsetConstraint, -15.0)][0];
            }
        }
        
        [bubbles addObject:bubble];
        
        [self addSubview:bubble];
        [self addConstraints:@[leftConstraint, verticalCenterConstraint]];
        if (rightConstraint) {
            [self addConstraint:rightConstraint];
        }
        
        UITapGestureRecognizer* tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:welf action:@selector(appItemBubbleTapped:)];
        [bubble addGestureRecognizer:tapGesture];
    }];
}

+ (CGFloat)requiredHeight {
    return 80.0;
}


@end
