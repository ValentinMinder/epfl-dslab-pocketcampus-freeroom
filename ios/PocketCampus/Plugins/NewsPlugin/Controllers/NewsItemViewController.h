//
//  NewsItemViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

@import UIKit;

#import "NewsService.h"

@interface NewsItemViewController : UIViewController

- (id)initWithNewsItem:(NewsItem*)newsItem cachedImageOrNil:(UIImage*)image;

@end
