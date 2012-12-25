//
//  NewsUtils.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "news.h"

@interface NewsUtils : NSObject

+ (NSArray*)eliminateDuplicateNewsItemsInArray:(NSArray*)newsItems;
+ (NSString*)dateLocaleStringForTimestamp:(NSTimeInterval)timestamp;
+ (NSString*)htmlReplaceWidthWith100PercentInContent:(NSString*)content ifWidthHeigherThan:(NSInteger)maxWidth;

@end
