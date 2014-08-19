//
//  LORadarItemBubbleView.m
//  Locus
//
//  Created by Loïc Gardiol on 06.03.14.
//  Copyright (c) 2014 Locus. All rights reserved.
//

#import "RecommendedAppThumbView.h"

#import "UIImageView+AFNetworking.h"

#import <QuartzCore/QuartzCore.h>


@interface RecommendedAppThumbView()

@property (nonatomic, strong) IBOutlet UIImageView* imageView;

@property (nonatomic, strong) IBOutlet UILabel* titleLabel;

@property (nonatomic, strong) IBOutlet UILabel* descriptionLabel;

@end
@implementation RecommendedAppThumbView

#pragma mark - Init

- (instancetype)initWithRecommendedApp:(RecommendedApp*)recommendedApp
{
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"RecommendedAppThumbView" owner:nil options:nil];
    self = (RecommendedAppThumbView*)elements[0];
    if (self) {
        self.recommendedApp = recommendedApp;
        self.imageView.contentMode = UIViewContentModeScaleAspectFit;
        self.translatesAutoresizingMaskIntoConstraints = NO;
        [self addConstraints:[NSLayoutConstraint width:220 height:80 constraintsForView:self]];
    }
    return self;
}

#pragma mark - Public

- (void)setRecommendedApp:(RecommendedApp*)recommendedApp {
    _recommendedApp = recommendedApp;
    self.titleLabel.text = recommendedApp.appName;
    self.descriptionLabel.text = recommendedApp.appDescription;
    RecommendedAppOSConfiguration* osConfiguration = [recommendedApp.appOSConfigurations objectForKey:@(AppStore_iOS)];
    [self.imageView setImageWithURL:[NSURL URLWithString:osConfiguration.appLogoURL]];
}

#pragma mark - Description

- (NSString*)description {
    return [[super description] stringByAppendingFormat:@"%@", self.recommendedApp];
}

- (void)dealloc
{
    [self.imageView cancelImageRequestOperation];
}

@end
