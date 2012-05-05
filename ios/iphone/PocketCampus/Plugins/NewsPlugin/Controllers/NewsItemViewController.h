//
//  NewsItemViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "NewsService.h"

#import "news.h"

@interface NewsItemViewController : UIViewController<NewsServiceDelegate> {
    UILabel* feedLabel;
    UIImageView* imageView;
    UILabel* titleLabel;
    UILabel* publishDateLabel;
    UIActivityIndicatorView* centerActivityIndicatorView;
    UIWebView* contentWebView;
    NewsItem* newsItem;
}

@property (nonatomic, assign) IBOutlet UILabel* feedlabel;
@property (nonatomic, assign) IBOutlet UIImageView* imageView;
@property (nonatomic, assign) IBOutlet UILabel* titleLabel;
@property (nonatomic, assign) IBOutlet UILabel* publishDateLabel;
@property (nonatomic, assign) IBOutlet UIActivityIndicatorView* centerActivityIndicatorView;
@property (nonatomic, assign) IBOutlet UIWebView* contentWebView;

- (id)initWithNewsItem:(NewsItem*)newsItem;
- (id)initWithNewsItem:(NewsItem *)newsItem andCachedImage:(UIImage*)image;

@end
