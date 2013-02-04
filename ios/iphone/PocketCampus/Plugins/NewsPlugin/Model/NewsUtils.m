//
//  NewsUtils.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "NewsUtils.h"

static NSTimeInterval kOneWeekSeconds = 604800.0;

static NSTimeInterval kOneMonthSeconds = 2592000;

@implementation NewsUtils

+ (NSArray*)eliminateDuplicateNewsItemsInArray:(NSArray*)newsItems {
    
    if (newsItems.count == 0) {
        return newsItems;
    }
    
    /*
     * Array is browsed in reverse order so that if a news appears twice (IC feed then EPFL All News feed)
     * we take the one from the first feed => earliest time it appeared, so that user does not see later
     * a news he already read coming back to the top.
     */
    
    NSMutableOrderedSet* set = [NSMutableOrderedSet orderedSet];
    
    for (int i = newsItems.count-1; i>=0; i--) {
        [set addObject:newsItems[i]];
    }
    
    return [[[set array] reverseObjectEnumerator] allObjects]; //returns reversed array
}

+ (NSArray*)newsItemsSectionsSortedByDate:(NSArray*)newsItems {
    NSMutableArray* sections = [NSMutableArray arrayWithCapacity:4];
    for (int i = 0; i<4; i++) {
        sections[i] = [NSMutableArray array];
    }
    NSDate* nowDate = [NSDate date];
    NSDateComponents* todayComps = [[NSCalendar currentCalendar] components:NSYearCalendarUnit|NSMonthCalendarUnit|NSDayCalendarUnit fromDate:nowDate];
    for (NewsItem* item in newsItems) {
        NSDate* itemDate = [NSDate dateWithTimeIntervalSince1970:item.pubDate/1000.0];
        NSDateComponents* itemComps = [[NSCalendar currentCalendar] components:NSYearCalendarUnit|NSMonthCalendarUnit|NSDayCalendarUnit fromDate:itemDate];
        if ([itemComps isEqual:todayComps]) {
            [sections[0] addObject:item];
        } else if (fabs([itemDate timeIntervalSinceNow]) < kOneWeekSeconds) {
            [sections[1] addObject:item];
        } else if (fabs([itemDate timeIntervalSinceNow]) < kOneMonthSeconds) {
            [sections[2] addObject:item];
        } else {
            [sections[3] addObject:item];
        }
    }
    return sections;
}

+ (NSString*)dateLocaleStringForTimestamp:(NSTimeInterval)timestamp {
    NSDate* date = [NSDate dateWithTimeIntervalSince1970:timestamp];
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    formatter.dateStyle = NSDateFormatterMediumStyle;
    return [formatter stringFromDate:date];
}

+ (NSString*)htmlReplaceWidthWith100PercentInContent:(NSString*)content ifWidthHeigherThan:(NSInteger)maxWidth {
    NSError* error = nil;
    
    {
        NSRegularExpression* regex = [NSRegularExpression regularExpressionWithPattern:@"width=\"(\\d{1,})\"" options:NSRegularExpressionCaseInsensitive error:&error];
        
        NSArray* matches = [regex matchesInString:content options:0 range:NSMakeRange(0, content.length)];
        NSString* maxWidthString = [NSString stringWithFormat:@"%d%%", 100];
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
