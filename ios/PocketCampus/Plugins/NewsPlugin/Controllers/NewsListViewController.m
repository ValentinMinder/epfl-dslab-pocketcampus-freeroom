/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

//  Created by Loïc Gardiol on 05.05.12.

#import "NewsListViewController.h"

#import "NewsService.h"

#import "NewsUtils.h"

#import "NewsModelAdditions.h"

#import "NewsItemViewController.h"

#import "PCTableViewSectionHeader.h"

static NSString* kCellTextLabelTextStyle;

static NSTimeInterval kAutomaticRefreshPeriodSeconds = 1800.0; //30min

@interface NewsListViewController ()<NewsServiceDelegate>

@property (nonatomic, strong) NewsService* newsService;
@property (nonatomic, strong) NSArray* sections; //array of arrays of NewsFeedItem
@property (nonatomic, strong) LGARefreshControl* lgRefreshControl;
@property (nonatomic, strong) NewsFeedItem* selectedItem;

@end

@implementation NewsListViewController

#pragma mark - Init

- (id)init 
{
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            kCellTextLabelTextStyle = UIFontTextStyleFootnote;
        });
        self.gaiScreenName = @"/news";
        self.newsService = [NewsService sharedInstanceToRetain];
        NewsFeedsRequest* request = [[NewsFeedsRequest alloc] initWithLanguage:[PCUtils userLanguageCode] generalFeedIncluded:YES];
        NewsFeedsResponse* cachedResponse = [self.newsService getFromCacheAllFeedsForRequest:request];
        [self fillSectionsFromNewsFeedsResponse:cachedResponse];
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
    PCTableViewAdditions* tableViewAdditions = [PCTableViewAdditions new];
    self.tableView = tableViewAdditions;
    tableViewAdditions.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return floorf([PCTableViewCellAdditions preferredHeightForStyle:UITableViewCellStyleDefault textLabelTextStyle:kCellTextLabelTextStyle detailTextLabelTextStyle:nil]*1.35);
    };
    tableViewAdditions.reprocessesImagesWhenContentSizeCategoryChanges = YES;
    tableViewAdditions.imageProcessingBlock = ^UIImage*(PCTableViewAdditions* tableView, NSIndexPath* indexPath, UIImage* image) {
        CGFloat rowHeight = tableView.rowHeightBlock(tableView);
        CGFloat imageWidth = rowHeight * (16.0/9.0);
        return [image imageByScalingAndCroppingForSize:CGSizeMake(imageWidth, rowHeight) applyDeviceScreenMultiplyingFactor:YES];
    };
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshIfNeeded) name:UIApplicationDidBecomeActiveNotification object:[UIApplication sharedApplication]];
    self.lgRefreshControl = [[LGARefreshControl alloc] initWithTableViewController:self refreshedDataIdentifier:[LGARefreshControl dataIdentifierForPluginName:@"news" dataName:@"newsList"]];
    [self.lgRefreshControl setTarget:self selector:@selector(refresh)];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
    [self refreshIfNeeded];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskAllButUpsideDown;
    
}

#pragma mark - Refresh control

- (void)refreshIfNeeded {
    if (!self.sections || [self.lgRefreshControl shouldRefreshDataForValidity:kAutomaticRefreshPeriodSeconds]) {
        [self refresh];
    }
}

- (void)refresh {
    [self.newsService cancelOperationsForDelegate:self];
    [self.lgRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"LoadingNews", @"NewsPlugin", nil)];
    NewsFeedsRequest* request = [[NewsFeedsRequest alloc] initWithLanguage:[PCUtils userLanguageCode] generalFeedIncluded:YES];
    [self.newsService getAllFeedsForRequest:request delegate:self];
}

- (void)fillSectionsFromNewsFeedsResponse:(NewsFeedsResponse*)response {
    if (!response) {
        self.sections = nil;
        return;
    }
    NSMutableArray* mAllNewsFeedItems = [NSMutableArray arrayWithCapacity:response.feeds.count*20]; //magic estimate
    for (NewsFeed* feed in response.feeds) {
        [mAllNewsFeedItems addObjectsFromArray:feed.items];
    }
    self.sections = [NewsUtils newsFeedItemsSectionsSortedByDate:mAllNewsFeedItems makeItemsUnique:YES];
}

#pragma mark - NewsServiceDelegate

- (void)getAllFeedsForRequest:(NewsFeedsRequest *)request didReturn:(NewsFeedsResponse *)response {
    
    switch (response.statusCode) {
        case NewsStatusCode_OK:
        {
            
            [self fillSectionsFromNewsFeedsResponse:response];
            [self.tableView reloadData];
            __weak __typeof(self) welf = self;
            if (self.selectedItem) {
                BOOL found __block = NO;
                [self.sections enumerateObjectsUsingBlock:^(NSArray* items, NSUInteger section, BOOL *stop1) {
                    [items enumerateObjectsUsingBlock:^(NewsFeedItem* item, NSUInteger row, BOOL *stop2) {
                        if ([item isEqual:self.selectedItem]) {
                            [welf.tableView selectRowAtIndexPath:[NSIndexPath indexPathForRow:row inSection:section] animated:NO scrollPosition:UITableViewScrollPositionNone];
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
            break;
        }
        default:
            [self getAllFeedsFailedForRequest:request];
            break;
    }
    
}

- (void)getAllFeedsFailedForRequest:(NewsFeedsRequest *)request {
    [PCUtils showServerErrorAlert];
    [self.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"ServerErrorShort", @"PocketCampus", nil)];
}

- (void)serviceConnectionToServerFailed {
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
    NewsFeedItem* newsFeedItem = self.sections[indexPath.section][indexPath.row];
    
    if ([self.selectedItem isEqual:newsFeedItem]) {
        return;
    }
    [self trackAction:@"OpenNewsItem"  contentInfo:[NSString stringWithFormat:@"%ld-%@", newsFeedItem.itemId, newsFeedItem.title]];
    NewsItemViewController* newsItemViewController = [[NewsItemViewController alloc] initWithNewsFeedItem:newsFeedItem];
    if (self.splitViewController) { // iPad
        self.selectedItem = newsFeedItem;
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], [[PCNavigationController alloc] initWithRootViewController:newsItemViewController]];
    } else {
        [self.navigationController pushViewController:newsItemViewController animated:YES];
    }
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString* const identifier = [(PCTableViewAdditions*)tableView autoInvalidatingReuseIdentifierForIdentifier:@"NewsCell"];
    NewsFeedItem* newsFeedItem = self.sections[indexPath.section][indexPath.row];
    PCTableViewCellAdditions* cell = [self.tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[PCTableViewCellAdditions alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
        UIFont* font = [UIFont preferredFontForTextStyle:kCellTextLabelTextStyle];
        cell.textLabel.font = [UIFont boldSystemFontOfSize:font.pointSize];
        cell.textLabel.numberOfLines = 3;
        cell.imageView.backgroundColor = [UIColor clearColor];
        if (![PCUtils isIdiomPad]) {
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        }
        [cell setAccessibilityTraitsBlock:^UIAccessibilityTraits{
            return UIAccessibilityTraitButton | UIAccessibilityTraitStaticText;
        }];
    }
    
    cell.textLabel.text = newsFeedItem.title;
    
    NSString* imageUrlString = [newsFeedItem imageUrlStringForSize:CGSizeMake(106.0, tableView.rowHeight) applyDeviceScreenMultiplyingFactor:YES];
    
    cell.imageView.image = nil; // as said in PCTableViewAdditions doc for setImageURL:forCell:atIndexPath:
    [(PCTableViewAdditions*)(self.tableView) setImageURL:[NSURL URLWithString:imageUrlString] forCell:cell atIndexPath:indexPath];
    
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
    [self.newsService cancelOperationsForDelegate:self];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
