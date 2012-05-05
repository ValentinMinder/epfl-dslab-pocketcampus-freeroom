//
//  NewsItemViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "NewsItemViewController.h"

@implementation NewsItemViewController

@synthesize feedlabel, imageView, titleLabel, publishDateLabel, centerActivityIndicatorView, contentWebView;

- (id)init
{
    self = [super initWithNibName:@"NewsItemView" bundle:nil];
    if (self) {
        // Custom initialization
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
        imageView.image = image;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    self.title = newsItem.title;
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

/* NewsServiceDelegate delegation */

- (void)serviceConnectionToServerTimedOut {
    //TODO
}

- (void)dealloc
{
    [newsItem release];
    [super dealloc];
}

@end
