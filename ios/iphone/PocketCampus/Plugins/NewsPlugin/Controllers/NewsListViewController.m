//
//  NewsListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "NewsListViewController.h"

#import "PCValues.h"

#import "PCUtils.h"

#import "NewsUtils.h"

#import "ASIDownloadCache.h"

#import "PCRefreshControl.h"

#import "NewsItemViewController.h"

#import "PCTableViewSectionHeader.h"

static NSString* kNewsCellIdentifier = @"NewsCell";
static NSString* kThumbnailIndexPathKey = @"ThumbnailIndexPath";

@interface NewsListViewController ()

@property (nonatomic, strong) NewsService* newsService;
@property (nonatomic, strong) NSArray* sections; //array of arrays, as returned by [NewsUtils eliminateDuplicateNewsItemsInArray:]
@property (nonatomic, strong) ASINetworkQueue* networkQueue;
@property (nonatomic, strong) NSMutableDictionary* thumbnails; //key : NSIndexPath , value : UIImage
@property (nonatomic, strong) Reachability* reachability;
@property (nonatomic, strong) NSMutableSet* failedThumbsIndexPaths;
@property (nonatomic, strong) PCRefreshControl* pcRefreshControl;
@property (nonatomic, strong) NewsItem* selectedItem;

@end

static NSTimeInterval kAutomaticRefreshPeriodSeconds = 1800.0; //30min

@implementation NewsListViewController

- (id)init 
{
    self = [super initWithNibName:@"NewsListView" bundle:nil];
    if (self) {
        self.newsService = [NewsService sharedInstanceToRetain];
        NSArray* newsItems = [self.newsService getFromCacheNewsItemsForLanguage:[PCUtils userLanguageCode]];
        if (newsItems) {
            newsItems = [NewsUtils eliminateDuplicateNewsItemsInArray:newsItems];
            self.sections = [NewsUtils newsItemsSectionsSortedByDate:newsItems];
        }
        self.networkQueue = [[ASINetworkQueue alloc] init];
        self.networkQueue.maxConcurrentOperationCount = 6;
        self.thumbnails = [[NSMutableDictionary alloc] init];
        self.pcRefreshControl = [[PCRefreshControl alloc] initWithTableViewController:self pluginName:@"news" refreshedDataIdentifier:@"newsList"];
        [self.pcRefreshControl setTarget:self selector:@selector(refresh)];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/news" withError:NULL];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshIfNeed) name:UIApplicationDidBecomeActiveNotification object:[UIApplication sharedApplication]];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.networkQueue setSuspended:NO];
    [self refreshIfNeed];
}

- (void)viewWillDisappear:(BOOL)animated {
    [self.networkQueue setSuspended:YES];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskAllButUpsideDown;
    
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation //iOS 5
{
    return UIInterfaceOrientationIsLandscape(interfaceOrientation) || (UIInterfaceOrientationPortrait);
}

#pragma mark - refresh control

- (void)refreshIfNeed {
    if (!self.sections || [self.pcRefreshControl shouldRefreshDataForValidity:kAutomaticRefreshPeriodSeconds]) {
        [self refresh];
    }
}

- (void)refresh {
    [self.newsService cancelOperationsForDelegate:self];
    [self.pcRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"LoadingNews", @"NewsPlugin", nil)];
    [self startGetNewsItemsRequest];
    [self.networkQueue go];
}

- (void)startGetNewsItemsRequest {
    [self.newsService getNewsItemsForLanguage:[PCUtils userLanguageCode] delegate:self];

}

- (void)reloadFailedThumbnailsCells {
    [self.tableView reloadRowsAtIndexPaths:[self.failedThumbsIndexPaths allObjects] withRowAnimation:UITableViewRowAnimationNone];
}

#pragma mark - NewsServiceDelegate

- (void)newsItemsForLanguage:(NSString*)language didReturn:(NSArray*)newsItems {
    newsItems = [NewsUtils eliminateDuplicateNewsItemsInArray:newsItems];
    self.sections = [NewsUtils newsItemsSectionsSortedByDate:newsItems];
    
    // index path are no longer corresponding
    [self.networkQueue cancelAllOperations];
    [self.thumbnails removeAllObjects]; 
    
    [self.tableView reloadData];
    
    if (self.selectedItem) {
        [self.sections enumerateObjectsUsingBlock:^(NSArray* items, NSUInteger section, BOOL *stop1) {
            [items enumerateObjectsUsingBlock:^(NewsItem* item, NSUInteger row, BOOL *stop2) {
                if ([item isEqual:self.selectedItem]) {
                    [self.tableView selectRowAtIndexPath:[NSIndexPath indexPathForRow:row inSection:section] animated:NO scrollPosition:UITableViewScrollPositionNone];
                    self.selectedItem = item;
                }
            }];
        }];
    }
    [self.pcRefreshControl endRefreshing];
    [self.pcRefreshControl markRefreshSuccessful];
    self.tableView.accessibilityIdentifier = @"NewsList";
}

- (void)newsItemsFailedForLanguage:(NSString*)language {
    self.pcRefreshControl.type = RefreshControlTypeProblem;
    self.pcRefreshControl.message = NSLocalizedStringFromTable(@"ServerErrorShort", @"PocketCampus", nil);
    [PCUtils showServerErrorAlert];
    [self.pcRefreshControl hideInTimeInterval:2.0];
}

- (void)serviceConnectionToServerTimedOut {
    self.pcRefreshControl.type = RefreshControlTypeProblem;
    self.pcRefreshControl.message = NSLocalizedStringFromTable(@"ConnectionToServerTimedOutShort", @"PocketCampus", nil);
    [PCUtils showConnectionToServerTimedOutAlert];
    [self.pcRefreshControl hideInTimeInterval:2.0];
}

#pragma mark - ASIHTTPRequestDelegate

- (void)requestFinished:(ASIHTTPRequest *)request {
    NSIndexPath* indexPath = [request.userInfo objectForKey:kThumbnailIndexPathKey];
    if (indexPath == nil) { //should never happen
        return;
    }
    if (self.failedThumbsIndexPaths) {
        [self.failedThumbsIndexPaths removeObject:indexPath];
    }
    UITableViewCell* cell = [self.tableView cellForRowAtIndexPath:indexPath];
    UIImage* image = [UIImage imageWithData:request.responseData];
    cell.imageView.image = image;
    [self.thumbnails setObject:image forKey:indexPath];
}

- (void)requestFailed:(ASIHTTPRequest *)request {
    NSIndexPath* reqIndexPath = [request.userInfo objectForKey:kThumbnailIndexPathKey];
    
    if (!self.failedThumbsIndexPaths) {
        self.failedThumbsIndexPaths = [NSMutableSet setWithObject:reqIndexPath];
    } else {
        [self.failedThumbsIndexPaths addObject:reqIndexPath];
    }
    
    if (!self.reachability) {
        self.reachability = [Reachability reachabilityForInternetConnection];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadFailedThumbnailsCells) name:kReachabilityChangedNotification object:self.reachability];
        [self.reachability startNotifier];
    }
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if ([self.sections[section] count] == 0) {
        return 0.0;
    }
    return [PCValues tableViewSectionHeaderHeight];
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    if ([self.sections[section] count] == 0) {
        return nil;
    }
    
    NSString* title = nil;
    
    switch (section) {
        case 0:
            title = NSLocalizedStringFromTable(@"TodaySectionTitle", @"NewsPlugin", nil);
            break;
        case 1:
            title = NSLocalizedStringFromTable(@"WeekSectionTitle", @"NewsPlugin", nil);
            break;
        case 2:
            title = NSLocalizedStringFromTable(@"MonthSectionTitle", @"NewsPlugin", nil);
            break;
        case 3:
            title = NSLocalizedStringFromTable(@"OlderSectionTitle", @"NewsPlugin", nil);
            break;
        default:
            title = @""; //not supported, should not happen
            break;
    }
    
    return [[PCTableViewSectionHeader alloc] initWithSectionTitle:title tableView:tableView];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NewsItem* newsItem = self.sections[indexPath.section][indexPath.row];
    
    if ([self.selectedItem isEqual:newsItem]) {
        return;
    }
    
    UIImage* thumbnail = [self.thumbnails objectForKey:indexPath];
    
    NewsItemViewController* newsItemViewController = [[NewsItemViewController alloc] initWithNewsItem:newsItem cachedImageOrNil:thumbnail];
    
    if (self.splitViewController) { // iPad
        self.selectedItem = newsItem;
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], [[UINavigationController alloc] initWithRootViewController:newsItemViewController]];
    } else {
        [self.navigationController pushViewController:newsItemViewController animated:YES];
    }
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NewsItem* newsItem = self.sections[indexPath.section][indexPath.row];
    UITableViewCell* cell = [self.tableView dequeueReusableCellWithIdentifier:kNewsCellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kNewsCellIdentifier];
        cell.contentView.backgroundColor = [UIColor clearColor];
        cell.textLabel.font = [UIFont boldSystemFontOfSize:13.0];
        cell.textLabel.backgroundColor = [UIColor clearColor];
        cell.textLabel.numberOfLines = 3;
        cell.textLabel.adjustsFontSizeToFitWidth = YES;
        cell.imageView.backgroundColor = [UIColor clearColor];
        if (![PCUtils isIdiomPad]) {
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        }
        cell.selectionStyle = UITableViewCellSelectionStyleGray;
    }
    
    cell.textLabel.text = newsItem.title;
    
    if (!self.thumbnails[indexPath]) {
        cell.imageView.image = [UIImage imageNamed:@"BackgroundNewsThumbnail.png"]; //Temporary thumbnail until image is loaded
        if (newsItem.imageUrl) {
            ASIHTTPRequest* thumbnailRequest = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:newsItem.imageUrl]];
            thumbnailRequest.downloadCache = [ASIDownloadCache sharedCache];
            thumbnailRequest.cachePolicy = ASIOnlyLoadIfNotCachedCachePolicy;
            thumbnailRequest.cacheStoragePolicy = ASICachePermanentlyCacheStoragePolicy;
            thumbnailRequest.secondsToCache = 7257600.0; //seconds == 3 months. Images are not likely to change
            //thumbnailRequest.cachePolicy = ASIDoNotReadFromCacheCachePolicy; //FOR TESTS
            thumbnailRequest.delegate = self;
            thumbnailRequest.userInfo = [NSMutableDictionary dictionaryWithObject:indexPath forKey:kThumbnailIndexPathKey];
            thumbnailRequest.timeOutSeconds = 10.0; //do not overload network with thumbnails that fail to load
            [self.networkQueue addOperation:thumbnailRequest];
        }
    } else {
        cell.imageView.image = self.thumbnails[indexPath];
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (!self.sections) {
        return 0;
    }
    return [self.sections[section] count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (!self.sections) {
        return 0;
    }
    return self.sections.count;
}

#pragma mark - dealloc

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [self.reachability stopNotifier];
    for (ASIHTTPRequest* req in self.networkQueue.operations) {
        req.delegate = nil;
        [req cancel];
    }
    self.networkQueue.delegate = nil;
    [self.newsService cancelOperationsForDelegate:self];
}

@end
