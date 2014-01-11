

//  Created by Loïc Gardiol on 24.12.12.


@import UIKit;

#import "NewsService.h"

@interface NewsItemViewController : UIViewController

- (id)initWithNewsItem:(NewsItem*)newsItem cachedImageOrNil:(UIImage*)image;

@end
