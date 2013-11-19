//
//  NewsListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//



#import "NewsListViewController.h"

#import "NewsService.h"

#import "NewsUtils.h"

#import "NewsItemViewController.h"

#import "PCTableViewSectionHeader.h"

#import "PCTableViewWithRemoteThumbnails.h"

#import "UIImage+Additions.h"

static NSString* kNewsCellIdentifier = @"NewsCell";
static NSString* kThumbnailIndexPathKey = @"ThumbnailIndexPath";

@interface NewsListViewController ()<NewsServiceDelegate>

@property (nonatomic, strong) NewsService* newsService;
@property (nonatomic, strong) NSArray* sections; //array of arrays, as returned by [NewsUtils eliminateDuplicateNewsItemsInArray:]
@property (nonatomic, strong) LGRefreshControl* lgRefreshControl;
@property (nonatomic, strong) NewsItem* selectedItem;

@end

static NSTimeInterval kAutomaticRefreshPeriodSeconds = 1800.0; //30min

@implementation NewsListViewController

- (id)init 
{
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        self.newsService = [NewsService sharedInstanceToRetain];
        NSArray* newsItems = [self.newsService getFromCacheNewsItemsForLanguage:[PCUtils userLanguageCode]];
        if (newsItems) {
            newsItems = [NewsUtils eliminateDuplicateNewsItemsInArray:newsItems];
            self.sections = [NewsUtils newsItemsSectionsSortedByDate:newsItems];
        }
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.tableView = [[PCTableViewWithRemoteThumbnails alloc] init];
    ((PCTableViewWithRemoteThumbnails*)(self.tableView)).imageProcessingBlock = ^UIImage*(NSIndexPath* indexPath, UITableViewCell* cell, UIImage* image) {
        return [image imageByScalingAndCroppingForSize:CGSizeMake(106.0, 60.0) applyDeviceScreenMultiplyingFactor:YES];
    };
    self.tableView.rowHeight = 60.0;
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshIfNeeded) name:UIApplicationDidBecomeActiveNotification object:[UIApplication sharedApplication]];
    self.lgRefreshControl = [[LGRefreshControl alloc] initWithTableViewController:self refreshedDataIdentifier:[LGRefreshControl dataIdentifierForPluginName:@"news" dataName:@"newsList"]];
    [self.lgRefreshControl setTarget:self selector:@selector(refresh)];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[PCGAITracker sharedTracker] trackScreenWithName:@"/news"];
    [self refreshIfNeeded];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskAllButUpsideDown;
    
}

#pragma mark - refresh control

- (void)refreshIfNeeded {
    if (![PCUtils hasDeviceInternetConnection]) {
        return;
    }
    if (!self.sections || [self.lgRefreshControl shouldRefreshDataForValidity:kAutomaticRefreshPeriodSeconds]) {
        [self refresh];
    }
}

- (void)refresh {
    [self.newsService cancelOperationsForDelegate:self];
    [self.lgRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"LoadingNews", @"NewsPlugin", nil)];
    [self.newsService getNewsItemsForLanguage:[PCUtils userLanguageCode] delegate:self];
}

#pragma mark - NewsServiceDelegate

- (void)newsItemsForLanguage:(NSString*)language didReturn:(NSArray*)newsItems {
    newsItems = [NewsUtils eliminateDuplicateNewsItemsInArray:newsItems];
    self.sections = [NewsUtils newsItemsSectionsSortedByDate:newsItems];
    
    [self.tableView reloadData];
    
    if (self.selectedItem) {
        BOOL found __block = NO;
        [self.sections enumerateObjectsUsingBlock:^(NSArray* items, NSUInteger section, BOOL *stop1) {
            [items enumerateObjectsUsingBlock:^(NewsItem* item, NSUInteger row, BOOL *stop2) {
                if ([item isEqual:self.selectedItem]) {
                    [self.tableView selectRowAtIndexPath:[NSIndexPath indexPathForRow:row inSection:section] animated:NO scrollPosition:UITableViewScrollPositionNone];
                    self.selectedItem = item;
                    *stop1 = YES;
                    *stop2 = YES;
                    found = YES;
                }
            }];
        }];
        if (!found) {
            self.selectedItem = nil;
        }
    }
    [self.lgRefreshControl endRefreshingAndMarkSuccessful];
    self.tableView.accessibilityIdentifier = @"NewsList";
}

- (void)newsItemsFailedForLanguage:(NSString*)language {
    [PCUtils showServerErrorAlert];
    [self.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"ServerErrorShort", @"PocketCampus", nil)];
}

- (void)serviceConnectionToServerTimedOut {
    [PCUtils showConnectionToServerTimedOutAlert];
    [self.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutShort", @"PocketCampus", nil)];
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if ([self.sections[section] count] == 0) {
        return 0.0;
    }
    return [PCTableViewSectionHeader preferredHeight];
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
    
    NewsItemViewController* newsItemViewController = [[NewsItemViewController alloc] initWithNewsItem:newsItem cachedImageOrNil:[(PCTableViewWithRemoteThumbnails*)(self.tableView) rawImageAtIndexPath:indexPath]];
    
    if (self.splitViewController) { // iPad
        self.selectedItem = newsItem;
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], [[PCNavigationController alloc] initWithRootViewController:newsItemViewController]];
    } else {
        [self.navigationController pushViewController:newsItemViewController animated:YES];
    }
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NewsItem* newsItem = self.sections[indexPath.section][indexPath.row];
    UITableViewCell* cell = [self.tableView dequeueReusableCellWithIdentifier:kNewsCellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kNewsCellIdentifier];
        cell.textLabel.font = [UIFont boldSystemFontOfSize:13.0];
        cell.textLabel.numberOfLines = 3;
        //cell.textLabel.adjustsFontSizeToFitWidth = YES;
        cell.imageView.backgroundColor = [UIColor clearColor];
        if (![PCUtils isIdiomPad]) {
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        }
    }
    
    cell.textLabel.text = newsItem.title;
    
    [(PCTableViewWithRemoteThumbnails*)(self.tableView) setImageURL:[NSURL URLWithString:newsItem.imageUrl] forCell:cell atIndexPath:indexPath];
    
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
    @try {
        [[NSNotificationCenter defaultCenter] removeObserver:self];
    }
    @catch (NSException *exception) {}
    [self.newsService cancelOperationsForDelegate:self];
}

@end
