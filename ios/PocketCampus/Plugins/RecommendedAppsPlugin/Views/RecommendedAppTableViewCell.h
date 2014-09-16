//
//  RecommendedAppTableViewCell.h
//  PocketCampus
//
//  Created by Silviu Andrica on 8/18/14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "recommendedapps.h"

@class RecommendedAppThumbView;

@interface RecommendedAppTableViewCell : UITableViewCell

- (instancetype)initWithRecommendedApps:(NSArray*)recommendedApps forCategory:(RecommendedAppCategory*)category andAppThumbTappedBlock:(void (^)(RecommendedAppThumbView * thumbView)) appThumbTappedBlock;

@end
