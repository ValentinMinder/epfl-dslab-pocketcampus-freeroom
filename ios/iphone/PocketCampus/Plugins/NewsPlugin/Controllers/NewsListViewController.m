//
//  NewsListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "NewsListViewController.h"

#import "news.h"

#import "PCValues.h"

#import "PCUtils.h"

#import "NewsUtils.h"

#import "NewsItemViewController.h"

#import "ASIDownloadCache.h"

static NSString* kNewsCellIdentifier = @"NewsCell";
static NSString* kThumbnailIndexPathKey = @"ThumbnailIndexPath";

@implementation NewsListViewController

@synthesize tableView, centerActivityIndicator, centerMessageLabel, shouldRefresh;

- (id)init 
{
    self = [super initWithNibName:@"NewsListView" bundle:nil];
    if (self) {
        newsService = [[NewsService sharedInstanceToRetain] retain];
        newsItems = nil;
        networkQueue = [[ASINetworkQueue alloc] init];
        networkQueue.maxConcurrentOperationCount = 6;
        thumbnails = [[NSMutableDictionary alloc] init];
        reachability = nil;
        failedThumbsIndexPaths = nil;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/news" withError:NULL];
    self.view.backgroundColor = [PCValues backgroundColor1];
    tableView.rowHeight = 50.0;
    tableView.backgroundColor = [UIColor clearColor];
    UIBarButtonItem* refreshButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(refresh)];
    self.navigationItem.rightBarButtonItem = refreshButton;
    [refreshButton release];
    [self refresh];
    //[newsService getNewsItemContentForId:99119833152 delegate:self];
    //[newsService getFeedsForLanguage:[self userLanguageIdentfier] delegate:self];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [tableView deselectRowAtIndexPath:[[tableView indexPathsForSelectedRows] objectAtIndex:0] animated:animated];
    [networkQueue setSuspended:NO];
}

- (void)viewWillDisappear:(BOOL)animated {
    [networkQueue setSuspended:YES];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation //<= iOS 5
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)refresh {
    shouldRefresh = NO;
    tableView.hidden = YES;
    [centerActivityIndicator startAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"CenterLabelLoadingText", @"NewsPlugin", @"Tell the user that the news are loading");
    [newsService cancelOperationsForDelegate:self];
    [newsService getNewsItemsForLanguage:[self userLanguageIdentfier] delegate:self];
    [networkQueue go];
}

- (void)reloadFailedThumbnailsCells {
    [tableView reloadRowsAtIndexPaths:[failedThumbsIndexPaths allObjects] withRowAnimation:UITableViewRowAnimationNone];
}

/* NewsServiceDelegate delegation */

- (void)newsItemsForLanguage:(NSString*)language didReturn:(NSArray*)newsItems_ {
    [newsItems release];
    newsItems = [[NewsUtils eliminateDuplicateNewsItemsInArray:newsItems_] retain];
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = @"";
    [PCUtils reloadTableView:tableView withFadingDuration:0.5];
    tableView.accessibilityIdentifier = @"NewsList";
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
    shouldRefresh = YES;
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
    if (failedThumbsIndexPaths) {
        [failedThumbsIndexPaths removeObject:indexPath];
    }
    UITableViewCell* cell = [tableView cellForRowAtIndexPath:indexPath];
    UIImage* image = [UIImage imageWithData:request.responseData];
    cell.imageView.image = image;
    [thumbnails setObject:image forKey:indexPath];
}

- (void)requestFailed:(ASIHTTPRequest *)request {
    NSIndexPath* reqIndexPath = [request.userInfo objectForKey:kThumbnailIndexPathKey];
    
    if (!failedThumbsIndexPaths) {
        failedThumbsIndexPaths = [[NSMutableSet setWithObject:reqIndexPath] retain];
    } else {
        [failedThumbsIndexPaths addObject:reqIndexPath];
    }
    
    if (!reachability) {
        reachability = [[Reachability reachabilityForInternetConnection] retain];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadFailedThumbnailsCells) name:kReachabilityChangedNotification object:reachability];
        [reachability startNotifier];
    }
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
            thumbnailRequest.downloadCache = [ASIDownloadCache sharedCache];
            thumbnailRequest.cachePolicy = ASIOnlyLoadIfNotCachedCachePolicy;
            thumbnailRequest.cacheStoragePolicy = ASICachePermanentlyCacheStoragePolicy;
            thumbnailRequest.secondsToCache = 7257600.0; //seconds == 3 months. Images are not likely to change
            //thumbnailRequest.cachePolicy = ASIDoNotReadFromCacheCachePolicy; //FOR TESTS
            thumbnailRequest.delegate = self;
            thumbnailRequest.userInfo = [NSMutableDictionary dictionaryWithObject:indexPath forKey:kThumbnailIndexPathKey];
            thumbnailRequest.timeOutSeconds = 10.0; //do not overload network with thumbnails that fail to load
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
    return [[NSLocale preferredLanguages] objectAtIndex:0];
}

- (void)dealloc
{
    if (reachability) {
        [reachability stopNotifier];
    }
    [[NSNotificationCenter defaultCenter] removeObserver:self name:kReachabilityChangedNotification object:reachability];
    [reachability release];
    reachability = nil;
    for (ASIHTTPRequest* req in networkQueue.operations) {
        req.delegate = nil;
        [req cancel];
    }
    [failedThumbsIndexPaths release];
    networkQueue.delegate = nil;
    [networkQueue release];
    [newsService cancelOperationsForDelegate:self];
    [newsService release];
    [newsItems release];
    [thumbnails release];
    [super dealloc];
}

@end
