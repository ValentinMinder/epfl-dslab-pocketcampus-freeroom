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
    return [[NSOrderedSet orderedSetWithArray:newsItems] array];
}

+ (NSString*)dateLocaleStringForTimestamp:(NSTimeInterval)timestamp {
    NSDate* date = [NSDate dateWithTimeIntervalSince1970:timestamp];
    NSDateFormatter* formatter = [[[NSDateFormatter alloc] init] autorelease];
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
