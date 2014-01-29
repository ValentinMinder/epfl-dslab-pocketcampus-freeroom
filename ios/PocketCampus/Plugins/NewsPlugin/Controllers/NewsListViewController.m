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

#import "NewsItemViewController.h"

#import "PCTableViewSectionHeader.h"

#import "PCTableViewAdditions.h"

#import "UIImage+Additions.h"

static NSString* kCellTextLabelTextStyle;

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
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            kCellTextLabelTextStyle = UIFontTextStyleFootnote;
        });
        self.gaiScreenName = @"/news";
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
    PCTableViewAdditions* tableViewAdditions = [[PCTableViewAdditions alloc] init];
    self.tableView = tableViewAdditions;
    tableViewAdditions.imageProcessingBlock = ^UIImage*(PCTableViewAdditions* tableView, NSIndexPath* indexPath, UIImage* image) {
        return [image imageByScalingAndCroppingForSize:CGSizeMake(106.0, tableView.rowHeight) applyDeviceScreenMultiplyingFactor:YES];
    };
    tableViewAdditions.reprocessesImagesWhenContentSizeCategoryChanges = YES;
    tableViewAdditions.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return floorf([PCTableViewCellAdditions preferredHeightForStyle:UITableViewCellStyleDefault textLabelTextStyle:kCellTextLabelTextStyle detailTextLabelTextStyle:nil]*1.35);
    };
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshIfNeeded) name:UIApplicationDidBecomeActiveNotification object:[UIApplication sharedApplication]];
    self.lgRefreshControl = [[LGRefreshControl alloc] initWithTableViewController:self refreshedDataIdentifier:[LGRefreshControl dataIdentifierForPluginName:@"news" dataName:@"newsList"]];
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

#pragma mark - refresh control

- (void)refreshIfNeeded {
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
}

- (void)newsItemsFailedForLanguage:(NSString*)language {
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
    NewsItem* newsItem = self.sections[indexPath.section][indexPath.row];
    
    if ([self.selectedItem isEqual:newsItem]) {
        return;
    }
    
    NewsItemViewController* newsItemViewController = [[NewsItemViewController alloc] initWithNewsItem:newsItem cachedImageOrNil:[(PCTableViewAdditions*)(self.tableView) cachedRawImageAtIndexPath:indexPath]];
    
    if (self.splitViewController) { // iPad
        self.selectedItem = newsItem;
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], [[PCNavigationController alloc] initWithRootViewController:newsItemViewController]];
    } else {
        [self.navigationController pushViewController:newsItemViewController animated:YES];
    }
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString* const identifier = [(PCTableViewAdditions*)tableView autoInvalidatingReuseIdentifierForIdentifier:@"NewsCell"];
    NewsItem* newsItem = self.sections[indexPath.section][indexPath.row];
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
    
    cell.textLabel.text = newsItem.title;
    
    [(PCTableViewAdditions*)(self.tableView) setImageURL:[NSURL URLWithString:newsItem.imageUrl] forCell:cell atIndexPath:indexPath];
    
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
