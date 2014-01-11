

//  Created by Loïc Gardiol on 05.05.12.


#import <Foundation/Foundation.h>

#import "news.h"

@interface NewsUtils : NSObject

/*
 * Will eliminate all duplicates in newsItem, using the definition of equality defined in NewsItem+Comparison
 */
+ (NSArray*)eliminateDuplicateNewsItemsInArray:(NSArray*)newsItems;

/*
 * Returns an array1 of arrays, in which array1[0] = news from today, array1[1] = news at most 1 week old,
 * array1[2] = news at most 1 month old, array1[3] = older news
 */
+ (NSArray*)newsItemsSectionsSortedByDate:(NSArray*)newsItems;

+ (NSString*)htmlReplaceWidthWith100PercentInContent:(NSString*)content ifWidthHeigherThan:(NSInteger)maxWidth;

@end
