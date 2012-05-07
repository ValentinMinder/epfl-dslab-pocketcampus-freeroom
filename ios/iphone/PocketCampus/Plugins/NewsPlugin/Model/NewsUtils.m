//
//  NewsUtils.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "NewsUtils.h"

@implementation NewsUtils

+ (NSArray*)eliminateDuplicateNewsItemsInArray:(NSArray*)newsItems {
    NSMutableArray* mutableNewsItems = [NSMutableArray array];
    NSMutableDictionary* countDictionary = [NSMutableDictionary dictionaryWithCapacity:newsItems.count];
    for (NewsItem* item in newsItems) {
        NSString* stringId = [NSString stringWithFormat:@"%lld", item.newsItemId];
        if ([countDictionary objectForKey:stringId] == nil && [countDictionary objectForKey:item.title] == nil) {
            [mutableNewsItems addObject:item];
            [countDictionary setObject:item forKey:stringId]; //object value is not important in dictionary
            [countDictionary setObject:item forKey:item.title]; //object value is not important in dictionary
        }
    }
    //NSLog(@"%@", countDictionary);
    return mutableNewsItems;
}

+ (NSString*)dateLocaleStringForTimestamp:(NSTimeInterval)timestamp {
    NSDate* date = [NSDate dateWithTimeIntervalSince1970:timestamp];
    NSDateFormatter* formatter = [[[NSDateFormatter alloc] init] autorelease];
    formatter.locale = [NSLocale currentLocale];
    formatter.dateStyle = NSDateFormatterShortStyle;
    return [formatter stringFromDate:date];
}

@end
