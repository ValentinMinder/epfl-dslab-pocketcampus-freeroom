//
//  NewsListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "NewsListViewController.h"

#import "news.h"

#import "PCValues.h"

#import "NewsUtils.h"

#import "NewsItemViewController.h"

static NSString* kNewsCellIdentifier = @"NewsCell";
static NSString* kThumbnailIndexPathKey = @"ThumbnailIndexPath";

@implementation NewsListViewController

@synthesize tableView, centerActivityIndicator, centerMessageLabel;

- (id)init 
{
    self = [super initWithNibName:@"NewsListView" bundle:nil];
    if (self) {
        newsService = [[NewsService sharedInstanceToRetain] retain];
        newsItems = nil;
        networkQueue = [[ASINetworkQueue alloc] init];
        networkQueue.maxConcurrentOperationCount = 6;
        thumbnails = [[NSMutableDictionary alloc] init];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    self.view.backgroundColor = [PCValues backgroundColor1];
    tableView.rowHeight = 50.0;
    tableView.backgroundColor = [UIColor clearColor];
    [centerActivityIndicator startAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"CenterLabelLoadingText", @"NewsPlugin", @"Tell the user that the news are loading");
    [newsService getNewsItemsForLanguage:[self userLanguageIdentfier] delegate:self];
    [networkQueue go];
    //[newsService getNewsItemContentForId:99119833152 delegate:self];
    //[newsService getFeedsForLanguage:[self userLanguageIdentfier] delegate:self];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void)viewDidAppear:(BOOL)animated {
    [tableView deselectRowAtIndexPath:[[tableView indexPathsForSelectedRows] objectAtIndex:0] animated:YES];
    [networkQueue setSuspended:NO];
}

- (void)viewWillDisappear:(BOOL)animated {
    [networkQueue setSuspended:YES];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

/* NewsServiceDelegate delegation */

- (void)newsItemsForLanguage:(NSString*)language didReturn:(NSArray*)newsItems_ {
    [newsItems release];
    newsItems = [[NewsUtils eliminateDuplicateNewsItemsInArray:newsItems_] retain];
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = @"";
    [tableView reloadData];
    tableView.alpha = 0.0;
    tableView.hidden = NO;
    [UIView transitionWithView:tableView duration:0.5 options:UIViewAnimationCurveEaseIn animations:^{
        tableView.alpha = 1.0;
    } completion:NULL];
}

- (void)newsItemsFailedForLanguage:(NSString*)language {
    tableView.hidden = YES;
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", nil);
}

/*- (void)feedsForLanguage:(NSString *)language didReturn:(NSArray *)feeds {
    for (Feed* feed in feeds) {
        NSLog(@"%@ %d", feed.title, feed.items.count);
    }
}

- (void)newsItemContentForId:(Id)newsItemId didReturn:(NSString *)content {
    NSLog(@"%@", content);
}*/

- (void)serviceConnectionToServerTimedOut {
    tableView.hidden = YES;
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
}

/* ASIHTTPRequestDelegate delegation */

- (void)requestFinished:(ASIHTTPRequest *)request {
    NSIndexPath* indexPath = [request.userInfo objectForKey:kThumbnailIndexPathKey];
    if (indexPath == nil) { //should never happen
        return;
    }
    UITableViewCell* cell = [tableView cellForRowAtIndexPath:indexPath];
    UIImage* image = [UIImage imageWithData:request.responseData];
    cell.imageView.image = image;
    [thumbnails setObject:image forKey:indexPath];
}

/* UITableViewDelegate delegation */

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NewsItemViewController* newsItemViewController;
    UIImage* thumbnail = [thumbnails objectForKey:indexPath];
    if (thumbnail != nil) {
        newsItemViewController = [[NewsItemViewController alloc] initWithNewsItem:[newsItems objectAtIndex:indexPath.row] andCachedImage:thumbnail];
    } else {
        newsItemViewController = [[NewsItemViewController alloc] initWithNewsItem:[newsItems objectAtIndex:indexPath.row]];
    }
    [self.navigationController pushViewController:newsItemViewController animated:YES];
    [newsItemViewController release];
}

/* UITableViewDataSource delegation */

- (UITableViewCell*)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NewsItem* newsItem = [newsItems objectAtIndex:indexPath.row];
    UITableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:kNewsCellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kNewsCellIdentifier] autorelease];
        cell.contentView.backgroundColor = [UIColor clearColor];
        cell.textLabel.font = [UIFont boldSystemFontOfSize:13.0];
        cell.textLabel.backgroundColor = [UIColor clearColor];
        cell.textLabel.numberOfLines = 3;
        cell.textLabel.adjustsFontSizeToFitWidth = YES;
        cell.imageView.backgroundColor = [UIColor clearColor];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        cell.selectionStyle = UITableViewCellSelectionStyleGray;
    }
    
    cell.textLabel.text = newsItem.title;
    
    if ([thumbnails objectForKey:indexPath] == nil) {
        cell.imageView.image = [UIImage imageNamed:@"BackgroundNewsThumbnail.png"]; //Temporary thumbnail until image is loaded
        if (newsItem.imageUrl != nil) {
            ASIHTTPRequest* thumbnailRequest = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:newsItem.imageUrl]];
            thumbnailRequest.cachePolicy = NSURLRequestReturnCacheDataElseLoad;
            thumbnailRequest.cacheStoragePolicy = ASICachePermanentlyCacheStoragePolicy;
            thumbnailRequest.delegate = self;
            thumbnailRequest.userInfo = [NSDictionary dictionaryWithObject:indexPath forKey:kThumbnailIndexPathKey];
            thumbnailRequest.timeOutSeconds = 5.0; //do not overload network with thumbnails that fail to load
            [networkQueue addOperation:thumbnailRequest];
        }
    } else {
        cell.imageView.image = [thumbnails objectForKey:indexPath];
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (newsItems == nil) {
        return 0;
    }
    return newsItems.count;
}

/* Utilities */

- (NSString*)userLanguageIdentfier {
    return [[NSLocale currentLocale] objectForKey:NSLocaleLanguageCode];
}

- (void)dealloc
{
    for (ASIHTTPRequest* req in networkQueue.operations) {
        req.delegate = nil;
        [req cancel];
    }
    networkQueue.delegate = nil;
    [networkQueue release];
    [newsService cancelOperationsForDelegate:self];
    [newsService release];
    [newsItems release];
    [thumbnails release];
    [super dealloc];
}

@end
