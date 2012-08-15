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

#import "ASIHTTPRequest.h"

#import "Reachability.h"

@interface NewsItemViewController : UIViewController<NewsServiceDelegate, ASIHTTPRequestDelegate, UIActionSheetDelegate, UIAlertViewDelegate, UIWebViewDelegate> {
    UIScrollView* scrollView;
    UILabel* feedLabel;
    UILabel* publishDateLabel;
    UILabel* titleLabel;
    UIImage* mainImage;
    UIImageView* mainImageView;
    UIActivityIndicatorView* centerActivityIndicator;
    UILabel* centerMessageLabel;
    UIWebView* webView;
    NewsItem* newsItem;
    NewsService* newsService;
    ASIHTTPRequest* thumbnailRequest;
    NSURL* urlClickedByUser;
    Reachability* reachability;
}

@property (nonatomic, assign) IBOutlet UIScrollView* scrollView;
@property (nonatomic, assign) IBOutlet UILabel* feedLabel;
@property (nonatomic, assign) IBOutlet UILabel* publishDateLabel;
@property (nonatomic, assign) IBOutlet UIActivityIndicatorView* centerActivityIndicator;
@property (nonatomic, assign) IBOutlet UILabel* centerMessageLabel;

- (id)initWithNewsItem:(NewsItem*)newsItem;
- (id)initWithNewsItem:(NewsItem *)newsItem andCachedImage:(UIImage*)image;

@end
