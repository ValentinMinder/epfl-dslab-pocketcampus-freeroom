

//  Created by Loïc Gardiol on 28.12.12.


#import "news.h"

@interface NewsItem (Comparison)

- (BOOL)isEqual:(id)object;
- (NSUInteger)hash;
- (NSComparisonResult)compare:(NewsItem*)object;

@end
