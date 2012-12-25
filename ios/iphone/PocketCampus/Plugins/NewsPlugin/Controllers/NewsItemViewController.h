//
//  NewsItemViewController2.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "NewsService.h"

#import "ASIHTTPRequest.h"

@interface NewsItemViewController : UIViewController<NewsServiceDelegate, ASIHTTPRequestDelegate, UIActionSheetDelegate, UIAlertViewDelegate, UIWebViewDelegate>

- (id)initWithNewsItem:(NewsItem*)newsItem cachedImageOrNil:(UIImage*)image;

@property (nonatomic, strong) IBOutlet UIImageView* backgroundImageView;
@property (nonatomic, strong) IBOutlet UIWebView* webView;
@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, strong) IBOutlet UILabel* centerMessageLabel;

@end
