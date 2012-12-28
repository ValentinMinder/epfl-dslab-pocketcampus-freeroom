//
//  NewsItem+Comparison.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "news.h"

@interface NewsItem (Comparison)

- (BOOL)isEqual:(id)object;
- (NSUInteger)hash;

@end
