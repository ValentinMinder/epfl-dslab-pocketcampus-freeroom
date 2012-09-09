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
        NSString* lowerTitle = [item.title lowercaseString];
        if ([countDictionary objectForKey:stringId] == nil && [countDictionary objectForKey:lowerTitle] == nil) {
            [mutableNewsItems addObject:item];
            [countDictionary setObject:[NSNull null] forKey:stringId]; //object value is not important in dictionary
            [countDictionary setObject:[NSNull null] forKey:lowerTitle]; //object value is not important in dictionary
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

+ (NSString*)htmlReplaceWidthInContent:(NSString*)content ifWidthHeigherThan:(NSInteger)maxWidth {
    NSError* error = nil;
    
    {
        NSRegularExpression* regex = [NSRegularExpression regularExpressionWithPattern:@"width=\"(\\d{1,})\"" options:NSRegularExpressionCaseInsensitive error:&error];
        
        NSArray* matches = [regex matchesInString:content options:0 range:NSMakeRange(0, content.length)];
        NSString* maxWidthString = [NSString stringWithFormat:@"%d", maxWidth];
        for (NSTextCheckingResult* match in matches) {
            if (match.numberOfRanges < 1) { //range zero is whole pattern, not group
                continue;
            }
            NSString* numbersString = [content substringWithRange:[match rangeAtIndex:1]];
            NSInteger number = [numbersString integerValue];
            if (number > maxWidth) {
                content = [content stringByReplacingCharactersInRange:[match rangeAtIndex:1] withString:maxWidthString];
            }
        }
    }
    
    return content;
    
    /*if ([regex numberOfMatchesInString:lowerTitle options:0 range:titleRange] > 0 || [regexWithoutSpace numberOfMatchesInString:lowerTitle options:0 range:titleRange] > 0) {
        [retAnnotations addObject:mapItemAnnotation];
        return retAnnotations;
    }*/
}

@end
