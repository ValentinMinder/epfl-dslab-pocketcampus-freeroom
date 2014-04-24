/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

//  Created by Loïc Gardiol on 05.05.12.

#import "NewsUtils.h"

#import "NewsModelAdditions.h"

static NSTimeInterval kOneWeekSeconds = 604800.0;

static NSTimeInterval kOneMonthSeconds = 2592000;

@implementation NewsUtils

+ (NSArray*)newsFeedItemsSectionsSortedByDate:(NSArray*)newsFeedItems makeItemsUnique:(BOOL)makeItemsUnique {
    
    newsFeedItems = [newsFeedItems sortedArrayUsingSelector:@selector(compareDateToNewsFeedItem:)];
    if (makeItemsUnique) {
        NSMutableOrderedSet* uniqueNewsFeedItems = [NSMutableOrderedSet orderedSetWithCapacity:newsFeedItems.count];
        for (NewsFeedItem* item in [newsFeedItems reverseObjectEnumerator]) {
            [uniqueNewsFeedItems insertObject:item atIndex:0]; //reverse-back
        }
        newsFeedItems = [uniqueNewsFeedItems array];
    }
    
    NSMutableArray* sections = [NSMutableArray arrayWithCapacity:4];
    for (int i = 0; i<4; i++) {
        sections[i] = [NSMutableArray array];
    }
    NSDate* nowDate = [NSDate date];
    NSDateComponents* todayComps = [[NSCalendar currentCalendar] components:NSYearCalendarUnit|NSMonthCalendarUnit|NSDayCalendarUnit fromDate:nowDate];
    for (NewsFeedItem* item in newsFeedItems) {
        NSDate* itemDate = [NSDate dateWithTimeIntervalSince1970:item.date/1000.0];
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
