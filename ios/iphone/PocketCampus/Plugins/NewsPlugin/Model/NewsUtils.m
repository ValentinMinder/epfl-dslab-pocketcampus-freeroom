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
        if ([countDictionary objectForKey:item.title] == nil) {
            [mutableNewsItems addObject:item];
            [countDictionary setObject:item forKey:item.title]; //object value is not important in dictionary
        }
    }
    return mutableNewsItems;
}

+ (NSString*)dateAndTimeLocaleStringForTimestamp:(NSTimeInterval)timestamp {
    NSDate* date = [NSDate dateWithTimeIntervalSince1970:timestamp];
    NSDateFormatter* formatter = [[[NSDateFormatter alloc] init] autorelease];
    formatter.locale = [NSLocale currentLocale];
    formatter.dateStyle = NSDateFormatterShortStyle;
    formatter.timeStyle = NSTimeZoneNameStyleShortGeneric;
    return [formatter stringFromDate:date];
}

@end
