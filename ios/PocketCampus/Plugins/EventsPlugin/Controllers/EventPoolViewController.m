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

//  Created by Loïc Gardiol on 01.03.13.

#import "EventPoolViewController.h"

#import "PCCenterMessageCell.h"

#import "PCScanningViewController.h"

#import "EventItemCell.h"

#import "EventsUtils.h"

#import "PCTableViewSectionHeader.h"

#import "EventItemViewController.h"

#import "EventItem+Additions.h"

#import "EventsCategorySelectorViewController.h"

#import "EventsTagsViewController.h"

#import "MainController.h"

#import "PCURLSchemeHandler.h"

#import "EventsShareFavoriteItemsViewController.h"

static NSInteger const kSegmentIndexAll = 0;
static NSInteger const kSegmentIndexRightNow = 1;

static const NSTimeInterval kRefreshValiditySeconds = 1800; //30 min

static const NSInteger kOneWeekPeriodIndex = 0;
static const NSInteger kOneMonthPeriodIndex = 1;
static const NSInteger kSixMonthsPeriodIndex = 2;
static const NSInteger kOneYearPeriodIndex = 3;

static NSInteger const kDefaultEventsPeriod = EventsPeriods_ONE_MONTH;

static NSInteger const kRightNowEventsPeriodProxyValue = -5; //set self.selectedPeriod to this value to indicate that you want events right now (translated by createEventPoolRequest)

static NSInteger const kRightNowEventsPeriod = 1;

static const UISearchBarStyle kSearchBarDefaultStyle = UISearchBarStyleDefault;
static const UISearchBarStyle kSearchBarActiveStyle = UISearchBarStyleMinimal;

@interface EventPoolViewController ()<UIActionSheetDelegate, UISearchDisplayDelegate, EventsServiceDelegate>

@property (nonatomic, readwrite) int64_t poolId;
@property (nonatomic, strong) EventPool* eventPool;
@property (nonatomic, strong) EventPoolReply* poolReply;
@property (nonatomic, strong) LGARefreshControl* lgRefreshControl;
@property (nonatomic, strong) EventsService* eventsService;

@property (nonatomic) int32_t selectedPeriod; //see enum EventsPeriod in events.h
@property (nonatomic, strong) NSMutableDictionary* selectedCategories; //key = categId (NSNumber with int32), value = name string of categ
@property (nonatomic, strong) NSMutableDictionary* selectedTags; //key = tag short name, value = tag full name

@property (nonatomic, strong) NSArray* sectionsNames; //array of names of sections, to be used as key in itemsForSection

@property (nonatomic, strong) NSDictionary* tagsInPresentItems; //tags that are present in items of poolReply. key = tag short name, value = tag full name

@property (nonatomic, strong) NSArray* itemsForSection; //array of arrays of EventItem
@property (nonatomic, strong) NSArray* searchFilteredItemsForSection; //for search

@property (nonatomic, strong) UISearchDisplayController* searchController;
@property (nonatomic, strong) UISearchBar* searchBar;
@property (nonatomic, strong) NSOperationQueue* searchQueue;
@property (nonatomic, strong) NSTimer* typingTimer;
@property (nonatomic, strong) NSRegularExpression* currentSearchRegex;

@property (nonatomic, strong) UIActionSheet* filterSelectionActionSheet;
@property (nonatomic, strong) UIActionSheet* periodsSelectionActionSheet;

@property (nonatomic, strong) UIBarButtonItem* actionButton;
@property (nonatomic, strong) UIBarButtonItem* filterButton;
@property (nonatomic, strong) UIBarButtonItem* scanButton;

@property (nonatomic, strong) UISegmentedControl* segmentedControl;

@property (nonatomic, strong) EventItem* selectedItem;

@property (nonatomic, readonly, getter=isFilterByTagActive) BOOL filterByTagActive;

@property (nonatomic) BOOL pastMode;

@property (nonatomic) BOOL highlightNowCells;

//Following are used to keep state when switching to Right Now mode
@property (nonatomic) int32_t prevSelectedPeriod;
@property (nonatomic, strong) NSMutableDictionary* prevSelectedCategories;
@property (nonatomic, strong) NSMutableDictionary* prevSelectedTags;
@property (nonatomic) BOOL prevPastMode;

@end

@implementation EventPoolViewController

#pragma mark - Inits

- (id)init
{
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        self.gaiScreenName = @"/events/pool";
        self.poolId = 0;
        self.eventsService = [EventsService sharedInstanceToRetain];
        self.selectedPeriod = [self.eventsService lastSelectedPoolPeriod];
        if (self.selectedPeriod == 0) {
            self.selectedPeriod = kDefaultEventsPeriod;
        }
    }
    return self;
}

- (id)initWithEventPool:(EventPool*)pool {
    [PCUtils throwExceptionIfObject:pool notKindOfClass:[EventPool class]];
    self = [self init];
    if (self) {
        self.eventPool = pool;
        self.poolId = pool.poolId;
        self.poolReply = [self.eventsService getFromCacheEventPoolForRequest:[self createEventPoolRequest]];
        [self updateTitleForCurrentParameters];
    }
    return self;
}

- (id)initAndLoadRootPool {
    self = [self init];
    if (self) {
        self.poolId = [eventsConstants CONTAINER_EVENT_ID];
        self.poolReply = [self.eventsService getFromCacheEventPoolForRequest:[self createEventPoolRequest]];
        [self updateTitleForCurrentParameters];
    }
    return self;
}

- (id)initAndLoadEventPoolWithId:(int64_t)poolId {
    self = [self init];
    if (self) {
        self.poolId = poolId;
        self.poolReply = [self.eventsService getFromCacheEventPoolForRequest:[self createEventPoolRequest]];
        [self updateTitleForCurrentParameters];
    }
    return self;
}

#pragma mark - Public methods

- (int64_t)poolId {
    return _poolId;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshFromCurrentData) name:kEventsFavoritesEventItemsUpdatedNotification object:self.eventsService];
    PCTableViewAdditions* tableViewAdditions = [[PCTableViewAdditions alloc] init];
    self.tableView = tableViewAdditions;
    tableViewAdditions.imageProcessingBlock = ^UIImage*(PCTableViewAdditions* tableView, NSIndexPath* indexPath, UIImage* image) {
        return [image imageByScalingAndCroppingForSize:[EventItemCell preferredImageSize] applyDeviceScreenMultiplyingFactor:YES];
    };
    tableViewAdditions.reprocessesImagesWhenContentSizeCategoryChanges = YES;

    RowHeightBlock rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return [EventItemCell preferredHeight];
    };
    tableViewAdditions.rowHeightBlock = rowHeightBlock;
    
    if (self.poolId == [eventsConstants CONTAINER_EVENT_ID]) {
        //show [All | Right Now] toggle only in root pool
        NSArray* segmentedControlItems = @[NSLocalizedStringFromTable(@"All", @"PocketCampus", nil), NSLocalizedStringFromTable(@"RightNow", @"EventsPlugin", nil)];
        self.segmentedControl = [[UISegmentedControl alloc] initWithItems:segmentedControlItems];
        
        self.segmentedControl.tintColor = [PCValues pocketCampusRed];
        self.segmentedControl.selectedSegmentIndex = kSegmentIndexAll;
        [self.segmentedControl addTarget:self action:@selector(segmentedControlValueChanged) forControlEvents:UIControlEventValueChanged];
        UIBarButtonItem* segmentedControlBarItem = [[UIBarButtonItem alloc] initWithCustomView:self.segmentedControl];
        
        [self.segmentedControl addObserver:self forKeyPath:NSStringFromSelector(@selector(frame)) options:0 context:NULL];
        
        UIBarButtonItem* flexibleSpaceLeft = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
        UIBarButtonItem* flexibleSpaceRight = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
        self.toolbarItems = @[flexibleSpaceLeft, segmentedControlBarItem, flexibleSpaceRight];
    }
    
    self.searchQueue = [NSOperationQueue new];
    self.searchQueue.maxConcurrentOperationCount = 1;
    self.searchBar = [[UISearchBar alloc] initWithFrame:CGRectMake(0, 0, self.tableView.frame.size.width, 1.0)];
    [self.searchBar sizeToFit];
    self.searchBar.autoresizingMask = UIViewAutoresizingFlexibleWidth;
    //self.searchBar.placeholder = NSLocalizedStringFromTable(@"Search", @"PocketCampus", nil);
    self.searchBar.searchBarStyle = kSearchBarDefaultStyle;
    
    self.tableView.tableHeaderView = self.searchBar;
    
    self.searchController = [[UISearchDisplayController alloc] initWithSearchBar:self.searchBar contentsController:self];
    self.searchController.searchResultsDelegate = self;
    self.searchController.searchResultsDataSource = self;
    self.searchController.delegate = self;
    self.searchController.searchResultsTableView.rowHeight = rowHeightBlock(tableViewAdditions);
    self.searchController.searchResultsTableView.allowsMultipleSelection = NO;
    
    self.lgRefreshControl = [[LGARefreshControl alloc] initWithTableViewController:self refreshedDataIdentifier:[LGARefreshControl dataIdentifierForPluginName:@"events" dataName:[NSString stringWithFormat:@"eventPool-%lld", self.poolId]]];
    [self.lgRefreshControl setTarget:self selector:@selector(refresh)];
    [self updateUI];
    [self fillCollectionsFromReplyAndSelection];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    if (!self.searchController.isActive) {
        [self.navigationController setToolbarHidden:(self.poolId != [eventsConstants CONTAINER_EVENT_ID]) animated:animated];
    }
    [self trackScreen];
    if (!self.poolReply || [self.lgRefreshControl shouldRefreshDataForValidity:kRefreshValiditySeconds] || self.eventPool.sendStarredItems) { //if sendStarredItems then list can change anytime and should be refreshed
        [self refresh];
    }
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.navigationController setToolbarHidden:YES animated:animated];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    if ([PCUtils isIdiomPad]) {
        return UIInterfaceOrientationMaskAll;
    } else {
        return UIInterfaceOrientationMaskPortrait;
    }
}

#pragma mark - Properties

- (EventPool*)eventPool {
    if (self.poolReply) {
        return self.poolReply.eventPool;
    }
    return _eventPool;
}

- (void)setPoolReply:(EventPoolReply *)poolReply {
    _poolReply = poolReply;
    if (poolReply.eventPool) {
        self.eventPool = self.poolReply.eventPool;
    }
}

#pragma mark - Refresh control

- (void)refreshFromCurrentData {
    [self updateUI];
    [self fillCollectionsFromReplyAndSelection];
    [self.tableView reloadData];
    [self reselectLastSelectedItem];
}

- (void)refresh {
    [self.eventsService cancelOperationsForDelegate:self]; //cancel before retrying
    [self.lgRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"LoadingEventPool", @"EventsPlugin", nil)];
    [self startGetEventPoolRequest];
}

- (EventPoolRequest*)createEventPoolRequest {
    NSArray* starredItems = nil;
    if (self.eventPool.sendStarredItems) {
        starredItems = [self.eventsService allFavoriteEventItemIds];
    }
    
    EventPoolRequest* poolRequest = [EventPoolRequest new];
    poolRequest.eventPoolId = self.poolId;
    poolRequest.userTickets = [[self.eventsService allUserTickets] mutableCopy];
    poolRequest.starredEventItems = [starredItems mutableCopy];
    poolRequest.lang = [PCUtils userLanguageCode];
    poolRequest.periodInHours = self.selectedPeriod == kRightNowEventsPeriodProxyValue ? kRightNowEventsPeriod : self.selectedPeriod * 24;
    poolRequest.fetchPast = self.pastMode;
    
    return poolRequest;
}

- (void)startGetEventPoolRequest {
    EventPoolRequest* request = [self createEventPoolRequest];
    [self.eventsService getEventPoolForRequest:request delegate:self];
}

- (void)reselectLastSelectedItem {
    if (!self.selectedItem) {
        return;
    }
    BOOL found __block = NO;
    UITableView* tableView = self.searchController.isActive ? self.searchController.searchResultsTableView : self.tableView;
    NSArray* itemsForSection = self.searchController.isActive ? self.searchFilteredItemsForSection : self.itemsForSection;
    [itemsForSection enumerateObjectsUsingBlock:^(NSArray* items, NSUInteger section, BOOL *stop1) {
        [items enumerateObjectsUsingBlock:^(EventItem* item, NSUInteger row, BOOL *stop2) {
            if ([item isEqual:self.selectedItem]) {
                [tableView selectRowAtIndexPath:[NSIndexPath indexPathForRow:row inSection:section] animated:NO scrollPosition:UITableViewScrollPositionNone];
                self.selectedItem = item;
                *stop1 = YES;
                *stop2 = YES;
                found = YES;
            }
        }];
    }];
}

#pragma mark - Data fill and utils

/**
 * Add categs and tags from reply into selectedCategories and selectedTags,
 * to ensure "selected" by default behavior (if categ/tag was not available before)
 * IMPORTANT: call this *before* updating self.poolReply
 */
- (void)addMissingCategsAndTagsFromReply:(EventPoolReply*)reply {
    
    //on purpose using self.poolReply
    NSSet* categIdsSet = [NSSet setWithArray:self.poolReply.categs.allKeys];
    NSSet* shortTagNamesSet = [NSSet setWithArray:self.poolReply.tags.allKeys];
    
    //now comparing on reply parameter
    for (NSNumber* categId in reply.categs) {
        if (![categIdsSet containsObject:categId]) {
            self.selectedCategories[categId] = reply.categs[categId];
        }
    }
    
    for (NSString* shortTagName in reply.tags) {
        if (![shortTagNamesSet containsObject:shortTagName]) {
            self.selectedTags[shortTagName] = reply.tags[shortTagName];
        }
    }
}

- (void)fillCollectionsFromReplyAndSelection {
    if (!self.poolReply) {
        return;
    }
    
    if (!self.selectedCategories) { //select all categories (available in reply) by default
        self.selectedCategories = [self.poolReply.categs mutableCopy];
    }
    
    if (!self.selectedTags) { //select all tags (available in reply) by default
        self.selectedTags = [self.poolReply.tags mutableCopy];
    }
    
    NSMutableDictionary* tmpTagsInPresentItems = [NSMutableDictionary dictionary];
    for (EventItem* item in [self.poolReply.childrenItems allValues]) {
        for (NSString* tagKey in item.eventTags) {
            if (!tmpTagsInPresentItems[tagKey] && self.poolReply.tags[tagKey]) {
                tmpTagsInPresentItems[tagKey] = self.poolReply.tags[tagKey];
            }
        }
    }
    
    self.tagsInPresentItems = tmpTagsInPresentItems;
    
    //Force Favorites and Features to be always selected
    self.selectedCategories[kEventItemCategoryFavorite] = self.poolReply.categs[kEventItemCategoryFavorite];
    self.selectedCategories[kEventItemCategoryFeatured] = self.poolReply.categs[kEventItemCategoryFeatured];
    
    NSDictionary* itemsForCategNumber = [EventsUtils sectionsOfEventItem:[self.poolReply.childrenItems allValues] forCategories:self.selectedCategories andTags:self.selectedTags inverseSort:self.pastMode]; //if pastMode, show older events at bottom (=> inverse sort)
    
    
    NSArray* sortedCategNumbers = [[itemsForCategNumber allKeys] sortedArrayUsingSelector:@selector(compare:)];
    
    NSMutableArray* tmpSectionsNames = [NSMutableArray arrayWithCapacity:[sortedCategNumbers count]];
    NSMutableArray* tmpItemsForSection = [NSMutableArray arrayWithCapacity:[sortedCategNumbers count]];
    
    BOOL foundFavorite = NO;
    
    for (NSNumber* categNumber in sortedCategNumbers) {
        if (!foundFavorite && [categNumber isEqualToNumber:kEventItemCategoryFavorite]) {
            [tmpSectionsNames addObject:NSLocalizedStringFromTable(@"Favorites", @"EventsPlugin", nil)];
            foundFavorite = YES;
        } else {
            [tmpSectionsNames addObject:self.poolReply.categs[categNumber]];
        }
        [tmpItemsForSection addObject:itemsForCategNumber[categNumber]];
    }
    self.sectionsNames = tmpSectionsNames;
    
    /*
#warning REMOVE
    
    NSMutableSet* allSet =  [NSMutableSet setWithArray:self.poolReply.childrenItems.allValues];
    NSMutableSet* afterSet = [NSMutableSet set];
    for (NSArray* items in tmpItemsForSection) {
        [afterSet addObjectsFromArray:items];
    }
    
    NSMutableSet* minusSet = [allSet mutableCopy];
    [minusSet minusSet:afterSet];
    
#warning END OF REMOVE
    */
    self.itemsForSection = tmpItemsForSection;
}

- (BOOL)isFilterByTagActive {
    if (!self.selectedTags) {
        return NO;
    }
    NSSet* selectedTagsSet = [NSSet setWithArray:[self.selectedTags allKeys]];
    NSSet* selectableTagsSet = [NSSet setWithArray:[self.tagsInPresentItems allKeys]];
    BOOL filterByTagActive = !(self.poolReply.tags.count == 0 || [selectedTagsSet isEqualToSet:selectableTagsSet] || [selectableTagsSet isSubsetOfSet:selectedTagsSet]);
    return filterByTagActive;
}

- (NSArray*)searchFilteredItemsForSectionFromPattern:(NSString*)pattern {
    static NSUInteger const options = NSDiacriticInsensitiveSearch | NSCaseInsensitiveSearch;
    NSPredicate* predicate = [NSPredicate predicateWithBlock:^BOOL(EventItem* eventItem, NSDictionary *bindings) {
        if (eventItem.eventTitle && [eventItem.eventTitle rangeOfString:pattern options:options].location != NSNotFound) {
            return YES;
        }
        if (eventItem.eventPlace && [eventItem.eventPlace rangeOfString:pattern options:options].location != NSNotFound) {
            return YES;
        }
        if (eventItem.eventSpeaker && [eventItem.eventSpeaker rangeOfString:pattern options:options].location != NSNotFound) {
            return YES;
        }
        if (eventItem.eventDetails && [eventItem.eventDetails rangeOfString:pattern options:options].location != NSNotFound) {
            return YES;
        }
        return NO;
    }];
    
    NSMutableArray* searchFilteredItemsForSection = [NSMutableArray arrayWithCapacity:self.itemsForSection.count];
    for (NSArray* items in self.itemsForSection) {
        NSArray* filteredItems = [items filteredArrayUsingPredicate:predicate];
        [searchFilteredItemsForSection addObject:filteredItems];
    }
    return searchFilteredItemsForSection;
}

#pragma mark - UI update

- (void)updateTitleForCurrentParameters {
    if (self.poolId == [eventsConstants CONTAINER_EVENT_ID]) {
        if (self.pastMode) {
            self.title = NSLocalizedStringFromTable(@"PastEventsShort", @"EventsPlugin", nil);
        } else if (self.segmentedControl && self.segmentedControl.selectedSegmentIndex == kSegmentIndexRightNow) {
            self.title = NSLocalizedStringFromTable(@"EventsRightNow", @"EventsPlugin", nil);
        } else {
            self.title = NSLocalizedStringFromTable(@"PluginName", @"EventsPlugin", nil);
        }
    } else if (self.eventPool.poolTitle) {
        self.title = self.eventPool.poolTitle;
    }
}

- (void)updateUI {
    
    [self updateTitleForCurrentParameters];
    
    if (!self.poolReply) {
        return;
    }
    
    NSMutableArray* rightElements = [NSMutableArray array];

    if (self.eventPool.enableScan) {
        self.scanButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"CameraBarButton"] style:UIBarButtonItemStylePlain target:self action:@selector(cameraButtonPressed)];
        self.scanButton.accessibilityLabel = NSLocalizedStringFromTable(@"ScanACode", @"EventsPlugin", nil);
        [rightElements addObject:self.scanButton];
    }
    
    BOOL filterByTagActive = self.isFilterByTagActive;
    if (!(self.segmentedControl && self.segmentedControl.selectedSegmentIndex != kSegmentIndexAll)
        && (!self.eventPool.disableFilterByCateg || !self.eventPool.disableFilterByTags)) { //will also disable period filtering
        
        NSString* filterButtonImageName = filterByTagActive || self.pastMode ? @"FilterBarButtonSelected" : @"FilterBarButton";
        
        self.filterButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:filterButtonImageName] style:UIBarButtonItemStylePlain target:self action:@selector(filterButtonPressed)];
        self.filterButton.accessibilityLabel = NSLocalizedStringFromTable(@"PresentationOptions", @"EventsPlugin", nil);
        [rightElements addObject:self.filterButton];
    }
    
    if (self.segmentedControl && self.segmentedControl.selectedSegmentIndex == kSegmentIndexAll) {
        
        NSString* periodString = [EventsUtils periodStringForEventsPeriod:self.selectedPeriod selected:NO];
        
        NSString* segmentAllTitle = nil;
        if (self.pastMode && filterByTagActive) {
            segmentAllTitle = [NSString stringWithFormat:NSLocalizedStringFromTable(@"PeriodWithFormat(Past,FilteredByTag)", @"EventsPlugin", nil), periodString];
        } else if (self.pastMode) {
            segmentAllTitle = [NSString stringWithFormat:NSLocalizedStringFromTable(@"PeriodWithFormat(Past)", @"EventsPlugin", nil), periodString];
        } else if (filterByTagActive) {
            segmentAllTitle = [NSString stringWithFormat:NSLocalizedStringFromTable(@"PeriodWithFormat(FilteredByTag)", @"EventsPlugin", nil), periodString];
        } else {
            segmentAllTitle = periodString;
        }
        
        [self.segmentedControl setTitle:segmentAllTitle forSegmentAtIndex:kSegmentIndexAll];
    }
    
    if (self.eventPool.sendStarredItems) {
        self.actionButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAction target:self action:@selector(actionButtonPressed)];
        if (self.poolReply.childrenItems.count == 0) {
            self.actionButton.enabled = NO;
        }
        [rightElements addObject:self.actionButton];
    }
    
    self.navigationItem.rightBarButtonItems = rightElements;
}

#pragma mark - Actions

- (void)actionButtonPressed {
    if (!self.eventPool) {
        return;
    }
    EventsShareFavoriteItemsViewController* viewController = [[EventsShareFavoriteItemsViewController alloc] initWithRelatedEventPool:self.eventPool];
    PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:viewController];
    navController.modalPresentationStyle = UIModalPresentationFormSheet;
    [self presentViewController:navController animated:YES completion:NULL];
}

- (void)filterButtonPressed {
    if (self.periodsSelectionActionSheet.isVisible) {
        [self.periodsSelectionActionSheet dismissWithClickedButtonIndex:[self.periodsSelectionActionSheet cancelButtonIndex] animated:YES];
        return;
    }
    
    if (!self.filterSelectionActionSheet) {
        NSMutableArray* buttonTitles = [NSMutableArray arrayWithCapacity:3];
        
        if ([self goToCategoryButtonIndex] >= 0) {
            [buttonTitles addObject:NSLocalizedStringFromTable(@"SelectCategory", @"EventsPlugin", nil)];
        }
        
        if ([self filterByTagsButtonIndex] >= 0) {
            NSString* title = nil;
            
            NSSet* selectedTagsSet = [NSSet setWithArray:[self.selectedTags allKeys]];
            NSSet* selectableTagsSet = [NSSet setWithArray:[self.tagsInPresentItems allKeys]];
            
            if (self.poolReply.tags.count == 0 || [selectedTagsSet isEqualToSet:selectableTagsSet] || [selectableTagsSet isSubsetOfSet:selectedTagsSet]) {
                title = NSLocalizedStringFromTable(@"FilterByTags", @"EventsPlugin", nil);
            } else {
                title = [NSString stringWithFormat:NSLocalizedStringFromTable(@"FilterByTagsWithFormat", @"EventsPlugin", nil), self.selectedTags.count, self.tagsInPresentItems.count];
            }
            [buttonTitles addObject:title];
        }
        
        if ([self pastModeButtonIndex] >= 0) {
            NSString* title = nil;
            if (self.pastMode) {
                title = NSLocalizedStringFromTable(@"BackToUpcomingEvents", @"EventsPlugin", nil);
            } else {
                title = NSLocalizedStringFromTable(@"PastEvents", @"EventsPlugin", nil);
            }
            [buttonTitles addObject:title];
        }
        
        if ([self periodButtonIndex] >= 0) {
            NSString* periodString = [NSString stringWithFormat:NSLocalizedStringFromTable(@"PeriodWithFormat", @"EventsPlugin", nil), [EventsUtils periodStringForEventsPeriod:self.selectedPeriod selected:NO]];
            [buttonTitles addObject:periodString];
        }
        
        BOOL showResetFilterButton = self.isFilterByTagActive || self.pastMode;
        
        if (showResetFilterButton) {
            [buttonTitles addObject:NSLocalizedStringFromTable(@"ResetFilter", @"EventsPlugin", nil)];
        }
        
        [buttonTitles addObject:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil)];
        
        self.filterSelectionActionSheet = [[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:nil destructiveButtonTitle:nil otherButtonTitles:nil];
        
        for (NSString* title in buttonTitles) {
            [self.filterSelectionActionSheet addButtonWithTitle:title];
        }
        
        if (showResetFilterButton) {
            self.filterSelectionActionSheet.destructiveButtonIndex = self.filterSelectionActionSheet.numberOfButtons-2;
        }
        
        self.filterSelectionActionSheet.cancelButtonIndex = self.filterSelectionActionSheet.numberOfButtons-1;
    }
    
    [self.filterSelectionActionSheet toggleFromBarButtonItem:self.filterButton animated:YES];
}

- (NSInteger)goToCategoryButtonIndex {
    if (self.eventPool.disableFilterByCateg) {
        return -1; //should not be required
    }
    return 0;
}

- (NSInteger)filterByTagsButtonIndex {
    if (self.eventPool.disableFilterByTags) {
        return -1;
    }
    if (self.eventPool.disableFilterByCateg) {
        return 0;
    }
    return 1;
}

- (NSInteger)pastModeButtonIndex {
    if (self.poolId != [eventsConstants CONTAINER_EVENT_ID]) { //root pool
        return -1;
    }
    if (self.eventPool.disableFilterByCateg) {
        if (self.eventPool.disableFilterByTags) {
            return 0;
        }
        return 1;
    }
    return 2;
}

- (NSInteger)periodButtonIndex {
    if (self.eventPool.disableFilterByCateg) {
        return -1; //disableFilterByCateg hides filter button and thus also period selection
    }
    if (self.poolId != [eventsConstants CONTAINER_EVENT_ID]) {
        return -1;
    }
    if (self.eventPool.disableFilterByTags) {
        return 1;
    }
    if ([self pastModeButtonIndex] < 0) {
        return 2;
    }
    return 3;
}

- (NSInteger)resetFilterButtonIndex {
    NSInteger periodButtonIndex = [self periodButtonIndex];
    if (periodButtonIndex >= 0 && (self.isFilterByTagActive || self.pastMode)) {
        return periodButtonIndex + 1;
    }
    return -1;
}

- (void)cameraButtonPressed {
    
    [self trackAction:@"ShowCodeScanner"];
    
    PCScanningViewController* scanningViewController = [PCScanningViewController new];
    
    scanningViewController.resultBlock = ^(NSString *result) {
        if (!result) {
            [self showQRCodeError];
            return;
        }
        NSURL* url = [NSURL URLWithString:result];
        if (!url) {
            [self showQRCodeError];
            return;
        }
        PCURLSchemeHandler* handler = [[MainController publicController] urlSchemeHandlerSharedInstance];
        NSDictionary* params = [handler parametersForPocketCampusURL:url];
        UIViewController* viewController = [handler viewControllerForPocketCampusURL:url];
        if (!viewController && !params[kEventsURLParameterUserTicket] && !params[kEventsURLParameterExchangeToken]) { //those parameter do not provide a view controller
            [self showQRCodeError];
            return;
        }
        if ([viewController isKindOfClass:[EventPoolViewController class]] && [(EventPoolViewController*)viewController poolId] == self.poolId) {
            [self refresh];
        } else {
            if ([viewController isKindOfClass:[EventItemViewController class]] && params[kEventsURLParameterMarkFavoriteEventItemId]) {
                [(EventItemViewController*)viewController setShowFavoriteButton:YES];
            }
            if ([self.splitViewController.viewControllers[0] isKindOfClass:[UINavigationController class]]) {
                UINavigationController* navController = self.splitViewController.viewControllers[0];
                if (navController.topViewController == self) {
                    PCNavigationController* eventNavController = [[PCNavigationController alloc] initWithRootViewController:viewController];
                    self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], eventNavController];
                } else {
                    [self.navigationController pushViewController:viewController animated:NO];
                }
            } else {
                [self.navigationController pushViewController:viewController animated:NO];
            }
        }
        [self dismissViewControllerAnimated:YES completion:NULL];
        
        NSString* contentInfo = [NSString stringWithFormat:@"%@?%@", url.lastPathComponent, url.query];
        [self trackAction:@"QRCodeScanned" contentInfo:contentInfo];
        
    };
    scanningViewController.cancelBlock = ^() {
        [self dismissViewControllerAnimated:YES completion:nil];
    };
    scanningViewController.errorBlock = ^(NSError *error) {
        [self showQRCodeError];
    };
    
    PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:scanningViewController];
    navController.navigationBar.barStyle = UIBarStyleBlack;
    navController.navigationBar.translucent = YES;
    navController.toolbarHidden = NO;
    navController.toolbar.barStyle = UIBarStyleBlack;
    navController.toolbar.tintColor = [UIColor whiteColor];
    navController.toolbar.translucent = YES;
    navController.modalPresentationStyle = UIModalPresentationFormSheet;
    [self presentViewController:navController animated:YES completion:NULL];
}

- (void)showQRCodeError {
    [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"QRCodeErrorMessage", @"EventsPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
}

//resulting actions

- (void)presentCategoriesController {
    EventPoolViewController* controller __weak = self;
    EventsCategorySelectorViewController* categoryViewController = [[EventsCategorySelectorViewController alloc] initWithCategories:self.sectionsNames selectedInitially:nil userValidatedSelectionBlock:^(NSArray *newlySelected) {
        if ([newlySelected count] != 1) {
            @throw [NSException exceptionWithName:@"Unsupported operation" reason:@"only single category selection is supported currently" userInfo:nil];
        }
        NSString* selectedCateg = newlySelected[0];
        
        NSInteger section = [controller.sectionsNames indexOfObject:selectedCateg];
        
        if (section != NSNotFound) {
            [controller.presentedViewController dismissViewControllerAnimated:YES completion:^{
                [controller.tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:section] atScrollPosition:UITableViewScrollPositionTop animated:YES];
            }];
        }
        
    }];
    UINavigationController* navController = [[UINavigationController alloc] initWithRootViewController:categoryViewController];
    navController.modalPresentationStyle = UIModalPresentationFormSheet;
    [self presentViewController:navController animated:YES completion:NULL];
}

- (void)presentTagsController {
    __weak __typeof(self) welf = self;
    NSArray* selectableTags = [[self.tagsInPresentItems allValues] sortedArrayUsingSelector:@selector(compare:)]; //alphabetically
    NSMutableSet* selectedInitially = [NSMutableSet setWithCapacity:selectableTags.count];
    
    NSSet* selectableTagsSet = [NSSet setWithArray:selectableTags];
    NSSet* selectedTagsSet = [NSSet setWithArray:[self.selectedTags allValues]];
    for (NSString* tag in selectedTagsSet) {
        if ([selectableTagsSet containsObject:tag]) {
            [selectedInitially addObject:tag];
        }
    }
    
    EventsTagsViewController* tagsViewController = [[EventsTagsViewController alloc] initWithTags:selectableTags selectedInitially:selectedInitially userValidatedSelectionBlock:^(NSSet *newlySelected) {
        if (newlySelected.count == 0 && selectableTags.count > 0) {
            [[[UIAlertView alloc] initWithTitle:nil message:NSLocalizedStringFromTable(@"SelectAtLeastOneTag", @"EventsPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
            return;
        }

        if (newlySelected.count == selectableTags.count) {
            //then, user selected everything, meaning "I want to show everything"
            //so select all possible from reply, so that if refresh, new items with
            //tags not present before will not be hidden
            self.selectedTags = [self.poolReply.tags mutableCopy];
        } else {
            NSMutableDictionary* selectedTags = [NSMutableDictionary dictionaryWithCapacity:newlySelected.count];
            [self.poolReply.tags enumerateKeysAndObjectsUsingBlock:^(NSString* tagKey, NSString* tag, BOOL *stop) {
                if ([newlySelected containsObject:tag]) {
                    selectedTags[tagKey] = tag;
                }
            }];
            self.selectedTags = selectedTags;
        }
        [welf refreshFromCurrentData];
        [welf dismissViewControllerAnimated:YES completion:NULL];
    }];
    UINavigationController* navController = [[UINavigationController alloc] initWithRootViewController:tagsViewController];
    navController.modalPresentationStyle = UIModalPresentationFormSheet;
    [self presentViewController:navController animated:YES completion:NULL];
}

- (void)presentPeriodSelectionActionSheet {
    if (self.periodsSelectionActionSheet) {
        [self.periodsSelectionActionSheet dismissWithClickedButtonIndex:self.periodsSelectionActionSheet.cancelButtonIndex animated:NO];
    }
    NSString* title = nil;
    if (self.pastMode) {
        title = NSLocalizedStringFromTable(@"ShowPastEventsFor...", @"EventsPlugin", nil);
    } else {
        title = NSLocalizedStringFromTable(@"ShowEventsFor...", @"EventsPlugin", nil);
    }
    self.periodsSelectionActionSheet = [[UIActionSheet alloc] initWithTitle:title delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:nil otherButtonTitles:
                                        [EventsUtils periodStringForEventsPeriod:EventsPeriods_ONE_WEEK selected:(self.selectedPeriod == EventsPeriods_ONE_WEEK)],
                                        [EventsUtils periodStringForEventsPeriod:EventsPeriods_ONE_MONTH selected:(self.selectedPeriod == EventsPeriods_ONE_MONTH)],
                                        [EventsUtils periodStringForEventsPeriod:EventsPeriods_SIX_MONTHS selected:(self.selectedPeriod == EventsPeriods_SIX_MONTHS)],
                                         [EventsUtils periodStringForEventsPeriod:EventsPeriods_ONE_YEAR selected:(self.selectedPeriod == EventsPeriods_ONE_YEAR)]
                                        , nil];

    [self.periodsSelectionActionSheet showFromBarButtonItem:self.filterButton animated:YES];
}

- (void)segmentedControlValueChanged {
    switch (self.segmentedControl.selectedSegmentIndex) {
        case kSegmentIndexAll:
            [self trackAction:@"SwitchBackToAllEvents"];
            self.tableView.tableHeaderView = self.searchBar;
            self.selectedCategories = self.prevSelectedCategories ?: self.selectedCategories;
            self.selectedTags = self.prevSelectedTags ?: self.selectedTags;
            self.pastMode = self.prevPastMode;
            self.selectedPeriod = self.prevSelectedPeriod != 0 ? self.prevSelectedPeriod : [self.eventsService lastSelectedPoolPeriod];
            break;
        case kSegmentIndexRightNow:
            [self trackAction:@"SwitchToHapenningNowEvents"];
            self.tableView.tableHeaderView = nil;
            //Saving filter state for All tab
            self.prevSelectedCategories = self.selectedCategories;
            self.prevSelectedTags = self.selectedTags;
            self.prevPastMode = self.pastMode;
            self.prevSelectedPeriod = self.selectedPeriod;
            
            //Now setting filter for Right Now tab
            self.selectedCategories = nil;
            self.selectedTags = nil;
            self.pastMode = NO;
            self.selectedPeriod = kRightNowEventsPeriodProxyValue;
        default:
            break;
    }
    self.itemsForSection = @[];
    [self.tableView reloadData];
    [self updateUI];
    [self refresh];
}

#pragma mark - KVO

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    if (object == self.segmentedControl && [keyPath isEqualToString:NSStringFromSelector(@selector(frame))]) {
        if (!self.segmentedControl.superview) {
            return;
        }
        CGFloat width = self.segmentedControl.superview.frame.size.width-18.0;
        if (width > 350.0) {
            width = 350.0;
        }
        CGFloat height = self.segmentedControl.superview.frame.size.height-16.0;
        if (height < 20.0) {
            height = 20.0;
        }
        self.segmentedControl.bounds = CGRectMake(0, 0, width, height);
    }
}

#pragma mark - UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet willDismissWithButtonIndex:(NSInteger)buttonIndex {
#warning ugly, see if updates of iOS 8 solve this problem
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        if (actionSheet == self.filterSelectionActionSheet) {
            if (buttonIndex == [self goToCategoryButtonIndex]) {
                [self trackAction:@"ShowCategories"];
                [self presentCategoriesController];
            } else if (buttonIndex == [self filterByTagsButtonIndex]) {
                [self trackAction:@"ShowTags"];
                [self presentTagsController];
            } else if (buttonIndex == [self  pastModeButtonIndex]) {
                if (self.pastMode) {
                    [self trackAction:@"SwitchBackToUpcomingEvents"];
                    self.pastMode = NO;
                } else {
                    [self trackAction:@"SwitchToPastEvents"];
                    self.pastMode = YES;
                }
                [self refresh];
            } else if (buttonIndex == [self periodButtonIndex]) {
                [self presentPeriodSelectionActionSheet];
            } else if (buttonIndex == [self resetFilterButtonIndex]) {
                [self trackAction:@"ResetFilter"];
                self.selectedCategories = nil;
                self.selectedTags = nil;
                self.pastMode = NO;
                [self refresh];
            } else {
                //ignore
            }
            self.filterSelectionActionSheet = nil;
        } else if (actionSheet == self.periodsSelectionActionSheet) {
            switch (buttonIndex) {
                case kOneWeekPeriodIndex:
                    self.selectedPeriod = EventsPeriods_ONE_WEEK;
                    break;
                case kOneMonthPeriodIndex:
                    self.selectedPeriod = EventsPeriods_ONE_MONTH;
                    break;
                case kSixMonthsPeriodIndex:
                    self.selectedPeriod = EventsPeriods_SIX_MONTHS;
                    break;
                case kOneYearPeriodIndex:
                    self.selectedPeriod = EventsPeriods_ONE_YEAR;
                    break;
            }
            [self trackAction:@"ChangePeriod" contentInfo:[NSString stringWithFormat:@"%d", (int)self.selectedPeriod]];
            [self.eventsService saveSelectedPoolPeriod:self.selectedPeriod];
            if (buttonIndex >= 0 && (buttonIndex != [self.periodsSelectionActionSheet cancelButtonIndex])) {
                [self.tableView setContentOffset:CGPointMake(0.0, 0.0) animated:YES]; // Scroll to top
                [self refresh];
            }
            self.periodsSelectionActionSheet = nil;
        }

    });
}

#pragma mark - UISearchDisplayDelegate

- (BOOL)searchDisplayController:(UISearchDisplayController *)controller shouldReloadTableForSearchString:(NSString *)searchString {
    [self.typingTimer invalidate];
    [self.searchQueue cancelAllOperations];
    if (searchString.length == 0) {
        self.searchFilteredItemsForSection = nil;
        self.currentSearchRegex = nil;
        return YES;
    } else {
        //perform search in background
        typeof(self) welf __weak = self;
        self.typingTimer = [NSTimer scheduledTimerWithTimeInterval:self.searchFilteredItemsForSection.count ? 0.2 : 0.0 block:^{ //interval: so that first search is not delayed (would display "No results" otherwise)
            [welf.searchQueue addOperationWithBlock:^{
                if (!welf) {
                    return;
                }
                __strong __typeof(welf) strongSelf = welf;
                NSArray* filteredSections = [strongSelf searchFilteredItemsForSectionFromPattern:searchString]; //heavy-computation line
                if (!welf) {
                    return;
                }
                NSRegularExpression* currentSearchRegex = [NSRegularExpression regularExpressionWithPattern:searchString options:NSRegularExpressionCaseInsensitive error:NULL];
                [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                    welf.searchFilteredItemsForSection = filteredSections;
                    welf.currentSearchRegex = currentSearchRegex;
                    [welf.searchController.searchResultsTableView reloadData];
                    [welf reselectLastSelectedItem];
                }];
            }];
        } repeats:NO];
        self.typingTimer.tolerance = 0.05;
        return NO;
    }
}

- (void)searchDisplayController:(UISearchDisplayController *)controller willHideSearchResultsTableView:(UITableView *)tableView {
    [self.typingTimer invalidate];
    [self.searchQueue cancelAllOperations];
    [self.tableView reloadData];
}

- (void)searchDisplayControllerWillBeginSearch:(UISearchDisplayController *)controller {
    [self trackAction:PCGAITrackerActionSearch];
    if ([PCUtils isIdiomPad]) {
        self.searchBar.searchBarStyle = kSearchBarActiveStyle;
    }
    [self.navigationController setToolbarHidden:YES animated:YES];
    [self.eventsService cancelOperationsForDelegate:self];
    [self.lgRefreshControl endRefreshing];
}

- (void)searchDisplayControllerWillEndSearch:(UISearchDisplayController *)controller {
    if ([PCUtils isIdiomPad]) {
        self.searchBar.searchBarStyle = kSearchBarDefaultStyle;
    }
    [self reselectLastSelectedItem];
    [self.navigationController setToolbarHidden:NO animated:YES];
}


#pragma mark - EventsServiceDelegate

- (void)getEventPoolForRequest:(EventPoolRequest *)request didReturn:(EventPoolReply *)reply {
    switch (reply.status) {
        case 200:
            [self addMissingCategsAndTagsFromReply:reply];
            self.poolReply = reply;
            [self updateUI];
            [self fillCollectionsFromReplyAndSelection];
            [self.tableView reloadData];
            [self reselectLastSelectedItem];
            [self.lgRefreshControl endRefreshingAndMarkSuccessful];
            break;
        default:
            [self getEventPoolFailedForRequest:request];
            break;
    }
}

- (void)getEventPoolFailedForRequest:(EventPoolRequest *)request {
    [self error];
}

- (void)showExchangeContactError {
    [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ExchangeContactErrorMessage", @"EventsPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
}

- (void)error {
    [PCUtils showServerErrorAlert];
    [self.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"ServerErrorShort", @"PocketCampus", nil)];
}

- (void)serviceConnectionToServerFailed {
    [PCUtils showConnectionToServerTimedOutAlert];
    [self.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutShort", @"PocketCampus", nil)];
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (tableView == self.tableView) {
        if ([self.itemsForSection count] == 0 || [(NSArray*)(self.itemsForSection[section]) count] == 0) {
            return 0.0;
        }
    } else if (tableView == self.searchController.searchResultsTableView) {
        if ([self.searchFilteredItemsForSection count] == 0 || [(NSArray*)(self.searchFilteredItemsForSection[section]) count] == 0) {
            return 0.0;
        }
    }
    
    return [PCTableViewSectionHeader preferredHeightWithInfoButton:YES]; //even without info button, want to make it higher
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    if (tableView == self.tableView) {
        if ([(NSArray*)(self.itemsForSection[section]) count] == 0) {
            return nil;
        }
    } else if (tableView == self.searchController.searchResultsTableView) {
        if ([(NSArray*)(self.searchFilteredItemsForSection[section]) count] == 0) {
            return nil;
        }
    }
    
    NSString* title = self.sectionsNames[section];
    return [[PCTableViewSectionHeader alloc] initWithSectionTitle:title tableView:tableView];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    EventItem* eventItem = nil;
    if (tableView == self.tableView) {
        if (!self.itemsForSection.count) {
            return;
        }
        eventItem = self.itemsForSection[indexPath.section][indexPath.row];
    } else if (tableView == self.searchController.searchResultsTableView) {
        if (!self.searchFilteredItemsForSection.count) {
            return;
        }
        eventItem = self.searchFilteredItemsForSection[indexPath.section][indexPath.row];
        [self.searchController.searchBar resignFirstResponder];
    }
    
    EventItemViewController* eventItemViewController = [[EventItemViewController alloc] initWithEventItem:eventItem];
    
    eventItemViewController.showFavoriteButton = !self.eventPool.disableStar;
    
    if (self.splitViewController && self.poolId == [eventsConstants CONTAINER_EVENT_ID]) {
        self.selectedItem = eventItem;
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], [[PCNavigationController alloc] initWithRootViewController:eventItemViewController]];
    } else {
        [self.navigationController pushViewController:eventItemViewController animated:YES];
    }
    [self trackAction:@"ShowEventItem" contentInfo:[NSString stringWithFormat:@"%lld-%@", eventItem.eventId, eventItem.eventTitle]];
}


#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (tableView == self.tableView && self.poolReply && [self.poolReply.childrenItems count] == 0) {
        if (indexPath.row == 1) {
            NSString* message = self.eventPool.noResultText;
            if (!message) {
                message = NSLocalizedStringFromTable(@"NoEvent", @"EventsPlugin", nil);
            }
            return [[PCCenterMessageCell alloc] initWithMessage:message];
        } else {
            UITableViewCell* cell =[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            return cell;
        }
    }
    
    if (tableView == self.searchController.searchResultsTableView) {
        //UISearchDisplayController takes care itself to show a "No result" message when
        //filteredSections is empty
    }
    
    static NSString* const kIdentifierName = @"EventCell";
    NSString* identifier = nil;
    EventItem* eventItem = nil;
    if (tableView == self.tableView) {
        identifier = [(PCTableViewAdditions*)tableView autoInvalidatingReuseIdentifierForIdentifier:kIdentifierName];
        eventItem = self.itemsForSection[indexPath.section][indexPath.row];
    } else if (tableView == self.searchController.searchResultsTableView) {
        identifier = kIdentifierName;
        eventItem = self.searchFilteredItemsForSection[indexPath.section][indexPath.row];
    }
    EventItemCell *cell = (EventItemCell*)[tableView dequeueReusableCellWithIdentifier:identifier];
    
    if (!cell) {
        cell = [[EventItemCell alloc] initWithEventItem:eventItem reuseIdentifier:identifier];
        //cell.glowIfEventItemIsNow = YES;
    }
    
    cell.eventItem = eventItem;
    
    [cell setAccessibilityTraitsBlock:^UIAccessibilityTraits{
        return UIAccessibilityTraitButton | UIAccessibilityTraitStaticText;
    }];
    
    cell.imageView.image = nil; // as said in PCTableViewAdditions doc for setImageURL:forCell:atIndexPath:
    if (tableView == self.tableView) {
        [(PCTableViewAdditions*)(self.tableView) setImageURL:[NSURL URLWithString:eventItem.eventThumbnail] forCell:cell atIndexPath:indexPath];
    }
    
    return cell;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (tableView == self.tableView) {
        if ([self.poolReply.childrenItems count] == 0 && self.eventPool.noResultText) {
            return 2; //first empty cell, second cell says no content
        }
        return [(NSArray*)(self.itemsForSection[section]) count];
    } else if (tableView == self.searchController.searchResultsTableView) {
        if (!self.searchFilteredItemsForSection) {
            return 0;
        }
        NSArray* items = self.searchFilteredItemsForSection[section];
        return items.count;
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (tableView == self.tableView) {
        if (!self.poolReply) {
            return 0;
        }
        
        if ([self.poolReply.childrenItems count] == 0 && self.eventPool.noResultText) {
            return 1;
        }
        return [self.itemsForSection count];
    }
    if (tableView == self.searchController.searchResultsTableView) {
        return self.searchFilteredItemsForSection.count;
    }
    return 0;
}

#pragma mark - Dealloc

- (void)dealloc {
    [self.eventsService cancelOperationsForDelegate:self];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    @try {
        [self.segmentedControl removeObserver:self forKeyPath:NSStringFromSelector(@selector(frame))];
    }
    @catch (NSException *exception) {}
}

@end
