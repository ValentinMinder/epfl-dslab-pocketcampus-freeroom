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


@interface EventPoolViewController ()<UIActionSheetDelegate, EventsServiceDelegate>

@property (nonatomic) int64_t poolId;
@property (nonatomic, strong) EventPool* eventPool;
@property (nonatomic, strong) EventPoolReply* poolReply;
@property (nonatomic, strong) LGRefreshControl* lgRefreshControl;
@property (nonatomic, strong) EventsService* eventsService;

@property (nonatomic) int32_t selectedPeriod; //see enum EventsPeriod in events.h
@property (nonatomic, strong) NSMutableDictionary* selectedCategories; //key = categId (NSNumber with int32), value = name string of categ
@property (nonatomic, strong) NSMutableDictionary* selectedTags; //key = tag short name, value = tag full name

@property (nonatomic, strong) NSArray* sectionsNames; //array of names of sections, to be used as key in itemsForSection

@property (nonatomic, strong) NSDictionary* tagsInPresentItems; //tags that are present in items of poolReply. key = tag short name, value = tag full name

@property (nonatomic, strong) NSArray* itemsForSection; //array of arrays of EventItem

@property (nonatomic, strong) UISearchBar* searchBar;

@property (nonatomic, strong) UIActionSheet* filterSelectionActionSheet;
@property (nonatomic, strong) UIActionSheet* periodsSelectionActionSheet;

@property (nonatomic, strong) UIBarButtonItem* actionButton;
@property (nonatomic, strong) UIBarButtonItem* filterButton;
@property (nonatomic, strong) UIBarButtonItem* scanButton;

@property (nonatomic, strong) EventItem* selectedItem;

@property (nonatomic, copy) NSString* normalTitle;

@property (nonatomic) BOOL pastMode;

@property (nonatomic) BOOL highlightNowCells;

@end

static const NSTimeInterval kRefreshValiditySeconds = 1800; //30 min

static const NSInteger kOneWeekPeriodIndex = 0;
static const NSInteger kOneMonthPeriodIndex = 1;
static const NSInteger kSixMonthsPeriodIndex = 2;
static const NSInteger kOneYearPeriodIndex = 3;

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
            self.selectedPeriod = EventsPeriods_ONE_MONTH; //default
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
        self.title = pool.poolTitle;
        self.poolReply = [self.eventsService getFromCacheEventPoolForRequest:[self createEventPoolRequest]];
    }
    return self;
}

- (id)initAndLoadRootPool {
    self = [self init];
    if (self) {
        self.poolId = [eventsConstants CONTAINER_EVENT_ID];
        self.poolReply = [self.eventsService getFromCacheEventPoolForRequest:[self createEventPoolRequest]];
        self.title = NSLocalizedStringFromTable(@"PluginName", @"EventsPlugin", nil);
    }
    return self;
}

- (id)initAndLoadEventPoolWithId:(int64_t)poolId {
    self = [self init];
    if (self) {
        self.poolId = poolId;
        self.poolReply = [self.eventsService getFromCacheEventPoolForRequest:[self createEventPoolRequest]];
        if (self.eventPool) {
            self.title = self.eventPool.poolTitle;
        }
    }
    return self;
}

#pragma mark - Public methods

- (int64_t)poolId {
    return _poolId;
}

#pragma mark - View load and visibility

- (void)viewDidLoad {
    [super viewDidLoad];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshFromCurrentData) name:kEventsFavoritesEventItemsUpdatedNotification object:self.eventsService];
    PCTableViewAdditions* tableViewAdditions = [[PCTableViewAdditions alloc] init];
    self.tableView = tableViewAdditions;
    tableViewAdditions.imageProcessingBlock = ^UIImage*(PCTableViewAdditions* tableView, NSIndexPath* indexPath, UIImage* image) {
        return [image imageByScalingAndCroppingForSize:[EventItemCell preferredImageSize] applyDeviceScreenMultiplyingFactor:YES];
    };
    tableViewAdditions.reprocessesImagesWhenContentSizeCategoryChanges = YES;
    tableViewAdditions.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return [EventItemCell preferredHeight];
    };
    
    self.lgRefreshControl = [[LGRefreshControl alloc] initWithTableViewController:self refreshedDataIdentifier:[LGRefreshControl dataIdentifierForPluginName:@"events" dataName:[NSString stringWithFormat:@"eventPool-%lld", self.poolId]]];
    [self.lgRefreshControl setTarget:self selector:@selector(refresh)];
    [self updateButtonsConditionally];
    [self fillCollectionsFromReplyAndSelection];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
    if (!self.poolReply || [self.lgRefreshControl shouldRefreshDataForValidity:kRefreshValiditySeconds] || self.eventPool.sendStarredItems) { //if sendStarredItems then list can change anytime and should be refreshed
        [self refresh];
    }
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
    [self updateButtonsConditionally];
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
    return [[EventPoolRequest alloc] initWithEventPoolId:self.poolId userToken:nil userTickets:[self.eventsService allUserTickets] starredEventItems:starredItems lang:[PCUtils userLanguageCode] period:self.selectedPeriod fetchPast:self.pastMode];
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
    [self.itemsForSection enumerateObjectsUsingBlock:^(NSArray* items, NSUInteger section, BOOL *stop1) {
        [items enumerateObjectsUsingBlock:^(EventItem* item, NSUInteger row, BOOL *stop2) {
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

#pragma mark - Buttons preparation and actions

- (void)updateButtonsConditionally {
    if (!self.poolReply) {
        return;
    }
    
    NSMutableArray* rightElements = [NSMutableArray array];

    if (self.eventPool.enableScan) {
        self.scanButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"CameraBarButton"] style:UIBarButtonItemStylePlain target:self action:@selector(cameraButtonPressed)];
        self.scanButton.accessibilityLabel = NSLocalizedStringFromTable(@"ScanACode", @"EventsPlugin", nil);
        [rightElements addObject:self.scanButton];
    }
    
    if (!self.eventPool.disableFilterByCateg || !self.eventPool.disableFilterByTags) { //will also disable period filtering
        
        NSSet* selectedTagsSet = [NSSet setWithArray:[self.selectedTags allKeys]];
        NSSet* selectableTagsSet = [NSSet setWithArray:[self.tagsInPresentItems allKeys]];
        NSString* filterButtonImageName = nil;
        if (self.poolReply.tags.count == 0 || [selectedTagsSet isEqualToSet:selectableTagsSet] || [selectableTagsSet isSubsetOfSet:selectedTagsSet]) {
            filterButtonImageName = @"FilterBarButton";
        } else {
            filterButtonImageName = @"FilterBarButtonSelected";
        }
        
        self.filterButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:filterButtonImageName] style:UIBarButtonItemStylePlain target:self action:@selector(filterButtonPressed)];
        self.filterButton.accessibilityLabel = NSLocalizedStringFromTable(@"PresentationOptions", @"EventsPlugin", nil);
        [rightElements addObject:self.filterButton];
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
        
        [buttonTitles addObject:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil)];
        
        self.filterSelectionActionSheet = [[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:nil destructiveButtonTitle:nil otherButtonTitles:nil];
        
        for (NSString* title in buttonTitles) {
            [self.filterSelectionActionSheet addButtonWithTitle:title];
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
    if (!self.periodsSelectionActionSheet) {
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

#pragma mark - UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet willDismissWithButtonIndex:(NSInteger)buttonIndex {
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
                self.title = self.normalTitle;
            } else {
                [self trackAction:@"SwitchToPastEvents"];
                self.pastMode = YES;
                self.normalTitle = self.title;
                self.title = NSLocalizedStringFromTable(@"PastEventsShort", @"EventsPlugin", nil);
            }
            [self refresh];
        } else if (buttonIndex == [self periodButtonIndex]) {
            [self presentPeriodSelectionActionSheet];
        } else {
            //ignore
        }
        self.filterSelectionActionSheet = nil;
    } else if (actionSheet == self.periodsSelectionActionSheet) {
        NSInteger nbDays = 0;
        switch (buttonIndex) {
            case kOneWeekPeriodIndex:
                self.selectedPeriod = EventsPeriods_ONE_WEEK;
                nbDays = 7;
                break;
            case kOneMonthPeriodIndex:
                self.selectedPeriod = EventsPeriods_ONE_MONTH;
                nbDays = 31;
                break;
            case kSixMonthsPeriodIndex:
                self.selectedPeriod = EventsPeriods_SIX_MONTHS;
                nbDays = 6 * 31;
                break;
            case kOneYearPeriodIndex:
                self.selectedPeriod = EventsPeriods_ONE_YEAR;
                nbDays = 365;
                break;
        }
        [self trackAction:@"ChangePeriod" contentInfo:[NSString stringWithFormat:@"%d", (int)nbDays]];
        
        [self.eventsService saveSelectedPoolPeriod:self.selectedPeriod];
        if (buttonIndex >= 0 && (buttonIndex != [self.periodsSelectionActionSheet cancelButtonIndex])) {
            [self.tableView scrollsToTop];
            [self refresh];
        }
        self.periodsSelectionActionSheet = nil;
    }
}

#pragma mark - Data fill

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
    self.selectedCategories[[EventsUtils favoriteCategory]] = self.poolReply.categs[[EventsUtils favoriteCategory]];
    self.selectedCategories[[EventsUtils featuredCategory]] = self.poolReply.categs[[EventsUtils featuredCategory]];
    
    NSDictionary* itemsForCategNumber = [EventsUtils sectionsOfEventItem:[self.poolReply.childrenItems allValues] forCategories:self.selectedCategories andTags:self.selectedTags inverseSort:self.pastMode]; //if pastMode, show older events at bottom (=> inverse sort)
    
    
    NSArray* sortedCategNumbers = [[itemsForCategNumber allKeys] sortedArrayUsingSelector:@selector(compare:)];
    
    NSMutableArray* tmpSectionsNames = [NSMutableArray arrayWithCapacity:[sortedCategNumbers count]];
    NSMutableArray* tmpItemsForSection = [NSMutableArray arrayWithCapacity:[sortedCategNumbers count]];
    
    BOOL foundFavorite = NO;
    
    for (NSNumber* categNumber in sortedCategNumbers) {
        if (!foundFavorite && [categNumber isEqualToNumber:[EventsUtils favoriteCategory]]) {
            [tmpSectionsNames addObject:NSLocalizedStringFromTable(@"Favorites", @"EventsPlugin", nil)];
            foundFavorite = YES;
        } else {
            [tmpSectionsNames addObject:self.poolReply.categs[categNumber]];
        }
        [tmpItemsForSection addObject:itemsForCategNumber[categNumber]];
    }
    self.sectionsNames = tmpSectionsNames;
    self.itemsForSection = tmpItemsForSection;
}

#pragma mark - EventsServiceDelegate

- (void)getEventPoolForRequest:(EventPoolRequest *)request didReturn:(EventPoolReply *)reply {
    switch (reply.status) {
        case 200:
            [self addMissingCategsAndTagsFromReply:reply];
            self.poolReply = reply;
            if (self.eventPool.poolTitle) {
                self.title = self.eventPool.poolTitle;
            }
            [self updateButtonsConditionally];
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
    if ([self.itemsForSection count] == 0 || [(NSArray*)(self.itemsForSection[section]) count] == 0) {
        return 0.0;
    }
    return [PCTableViewSectionHeader preferredHeight];
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    if ([(NSArray*)(self.itemsForSection[section]) count] == 0) {
        return nil;
    }
    
    NSString* title = self.sectionsNames[section];
    return [[PCTableViewSectionHeader alloc] initWithSectionTitle:title tableView:tableView];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (!self.itemsForSection.count) {
        return;
    }
    EventItem* eventItem = self.itemsForSection[indexPath.section][indexPath.row];
    
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

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.poolReply && [self.poolReply.childrenItems count] == 0) {
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
    NSString* const identifier = [(PCTableViewAdditions*)tableView autoInvalidatingReuseIdentifierForIdentifier:@"EventCell"];
    EventItem* eventItem = self.itemsForSection[indexPath.section][indexPath.row];
    EventItemCell *cell = (EventItemCell*)[tableView dequeueReusableCellWithIdentifier:identifier];
    
    if (!cell) {
        cell = [[EventItemCell alloc] initWithEventItem:eventItem reuseIdentifier:identifier];
        //cell.glowIfEventItemIsNow = YES;
    }
    
    cell.eventItem = eventItem;
    
    [cell setAccessibilityTraitsBlock:^UIAccessibilityTraits{
        return UIAccessibilityTraitButton | UIAccessibilityTraitStaticText;
    }];
    
    cell.imageView.image = nil;
    [(PCTableViewAdditions*)(self.tableView) setImageURL:[NSURL URLWithString:eventItem.eventThumbnail] forCell:cell atIndexPath:indexPath];
    
    return cell;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    if ([self.poolReply.childrenItems count] == 0 && self.eventPool.noResultText) {
        return 2; //first empty cell, second cell says no content
    }
    return [(NSArray*)(self.itemsForSection[section]) count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    if (!self.poolReply) {
        return 0;
    }
    
    if ([self.poolReply.childrenItems count] == 0 && self.eventPool.noResultText) {
        return 1;
    }
    return [self.itemsForSection count];
}

#pragma mark - dealloc

- (void)dealloc {
    [self.eventsService cancelOperationsForDelegate:self];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
