//
//  LORadarItemBubbleView.h
//  Locus
//
//  Created by Loïc Gardiol on 06.03.14.
//  Copyright (c) 2014 Locus. All rights reserved.
//

#import "recommendedapps.h"

@interface RecommendedAppThumbView : UIView

- (instancetype)initWithRecommendedApp:(RecommendedApp*)recommendedApp;

@property (nonatomic, strong) RecommendedApp* recommendedApp;
@end
