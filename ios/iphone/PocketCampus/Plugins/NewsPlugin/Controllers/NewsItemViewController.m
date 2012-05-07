//
//  NewsItemViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "NewsItemViewController.h"

#import "PCValues.h"

#import "NewsUtils.h"

#import "ASIHTTPRequest.h"

@implementation NewsItemViewController

@synthesize scrollView, feedLabel, publishDateLabel, centerActivityIndicator, centerMessageLabel;

- (id)init
{
    self = [super initWithNibName:@"NewsItemView" bundle:nil];
    if (self) {
        newsService = [NewsService sharedInstanceToRetain];
        newsItem = nil;
        mainImageView = nil;
        mainImage = nil;
        thumbnailRequest = nil;
    }
    return self;
}

- (id)initWithNewsItem:(NewsItem*)newsItem_ {
    self = [self init];
    if (self) {
        newsItem = [newsItem_ retain];
        
    }
    return self;
}

- (id)initWithNewsItem:(NewsItem *)newsItem_ andCachedImage:(UIImage*)image {
    self = [self init];
    if (self) {
        newsItem = [newsItem_ retain];
        mainImage = [image retain];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    self.title = newsItem.title;
    //self.view.backgroundColor = [PCValues backgroundColor1];
    [centerActivityIndicator startAnimating];

    feedLabel.text = newsItem.feed;
    publishDateLabel.text = [NewsUtils dateLocaleStringForTimestamp:newsItem.pubDate/1000.0];
    
    titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(10.0, 40.0, 300.0, 50.0)];
    titleLabel.text = newsItem.title;
    titleLabel.font = [UIFont boldSystemFontOfSize:16.0];
    titleLabel.textColor = [PCValues textColor1];
    //titleLabel.shadowColor = nil;
    //titleLabel.backgroundColor = [UIColor yellowColor];
    titleLabel.numberOfLines = 0;
    [titleLabel sizeToFit];
    
    [scrollView addSubview:titleLabel];
    
    if (newsItem.imageUrl != nil) {
        if (mainImage != nil) {
            [self addMainImageToScrollView];
            [newsService getNewsItemContentForId:newsItem.newsItemId delegate:self];
        } else { //thumbnail has to be downloaded. When it returns, the request for news content will be done
            thumbnailRequest = [[ASIHTTPRequest requestWithURL:[NSURL URLWithString:newsItem.imageUrl]] retain];
            thumbnailRequest.timeOutSeconds = 20.0;
            thumbnailRequest.delegate = self;
            [thumbnailRequest startAsynchronous];
        }
    } else {
        [newsService getNewsItemContentForId:newsItem.newsItemId delegate:self];
    }
}

- (void)addMainImageToScrollView {
    if (mainImage != nil) {
        [mainImage autorelease];
        if (mainImage.size.width > 300.0) {
            mainImage = [[UIImage imageWithCGImage:(CGImageRef)mainImage.CGImage scale:(mainImage.size.width/300.0) orientation:UIImageOrientationUp] retain]; //new resized image
        }
        mainImageView = [[[UIImageView alloc] initWithImage:mainImage] autorelease];
        mainImageView.center = CGPointMake(scrollView.center.x, titleLabel.frame.origin.y+titleLabel.frame.size.height+(mainImageView.frame.size.height/2.0)+15.0);
        [scrollView addSubview:mainImageView];
    }
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

/* ASIHTTPRequestDelegate delegation */

- (void)requestFinished:(ASIHTTPRequest *)request {
    if (request == thumbnailRequest) {
        mainImage = [[UIImage imageWithData:request.responseData] retain];
        [self addMainImageToScrollView];
        [newsService getNewsItemContentForId:newsItem.newsItemId delegate:self];
        [thumbnailRequest release];
        thumbnailRequest = nil;
    }
}

- (void)requestFailed:(ASIHTTPRequest *)request {
    if (request == thumbnailRequest) {
        [self serviceConnectionToServerTimedOut];
        [thumbnailRequest release];
        thumbnailRequest = nil;
    }
}

/* NewsServiceDelegate delegation */

- (void)newsItemContentForId:(Id)newsItemId didReturn:(NSString *)content {
    UIWebView* webView = [[UIWebView alloc] initWithFrame:CGRectMake(5.0, mainImageView.frame.origin.y+mainImageView.frame.size.height+10.0, 310.0, 300.0)];
    [webView loadHTMLString:content baseURL:nil];
    [webView sizeToFit];
    [scrollView addSubview:webView];
}

- (void)newsItemContentFailedForId:(Id)newsItemId {
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", nil);
    centerMessageLabel.hidden = NO;
}

- (void)serviceConnectionToServerTimedOut {
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
    centerMessageLabel.hidden = NO;
}

- (void)dealloc
{
    [titleLabel release];
    [mainImageView release];
    [mainImage release];
    [newsItem release];
    [newsService cancelOperationsForDelegate:self];
    [newsService release];
    if (thumbnailRequest != nil) {
        thumbnailRequest.delegate = nil;
        [thumbnailRequest release];
    }
    [super dealloc];
}

@end
