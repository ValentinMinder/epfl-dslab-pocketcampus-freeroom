//
//  NewsItem+Comparison.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "NewsItem+Comparison.h"

@implementation NewsItem (Comparison)

- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    return [self isEqualToNewsItem:object];
}

- (BOOL)isEqualToNewsItem:(NewsItem*)newsItem {
    return [self.title isEqualToString:newsItem.title] || self.newsItemId == newsItem.newsItemId;
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    hash += [self.title hash];
    return hash;
}

- (NSComparisonResult)compare:(NewsItem*)object {
    if (![object isKindOfClass:[self class]]) {
        @throw [NSException exceptionWithName:@"Illegal argument" reason:[NSString stringWithFormat:@"cannot compare NewsItem with %@", [object class]] userInfo:nil];
    }
    if (self.pubDate < object.pubDate) {
        return NSOrderedDescending;
    } else if (self.pubDate > object.pubDate) {
        return NSOrderedAscending;
    } else {
        return NSOrderedSame;
    }
}

@end
