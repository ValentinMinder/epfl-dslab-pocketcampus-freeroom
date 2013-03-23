//
//  EventPoolViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "EventPoolViewController.h"

#import "PCUtils.h"

#import "PCRefreshControl.h"

#import "PCCenterMessageCell.h"

#import "EventItemCell.h"

#import "GANTracker.h"

#import "EventsUtils.h"

#import "PCTableViewSectionHeader.h"

#import "PCValues.h"

#import "EventItemViewController.h"

#import "PCTableViewWithRemoteThumbnails.h"

#import "EventItem+Additions.h"

#import "EventsCategorySelectorViewController.h"

#import "UIActionSheet+Additions.h"

@interface EventPoolViewController ()

@property (nonatomic) int64_t poolId;
@property (nonatomic, strong) EventPoolReply* poolReply;
@property (nonatomic, strong) PCRefreshControl* pcRefreshControl;
@property (nonatomic, strong) EventsService* eventsService;

@property (nonatomic) int32_t selectedPeriod; //see enum EventsPeriod in events.h
@property (nonatomic, strong) NSMutableDictionary* selectedCategories; //key = categId (NSNumber with int32), value = name string of categ
@property (nonatomic, strong) NSMutableDictionary* selectedTags; //key = tag short name, value = tag full name

@property (nonatomic, strong) NSArray* sectionsNames; //array of names of sections, to be used as key in itemsForSectionName
@property (nonatomic, strong) NSArray* itemsForSection; //array of arrays of EventItem

@property (nonatomic, strong) UIActionSheet* filterSelectionActionSheet;
@property (nonatomic, strong) UIActionSheet* periodsSelectionActionSheet;

@property (nonatomic, strong) UIBarButtonItem* filterButton;
@property (nonatomic, strong) UIBarButtonItem* scanButton;

@property (nonatomic, strong) EventItem* selectedItem;

@end

static const NSTimeInterval kRefreshValiditySeconds = 1800; //30 min

static const NSInteger kOneWeekPeriodIndex = 0;
static const NSInteger kOneMonthPeriodIndex = 1;
static const NSInteger kSixMonthsPeriodIndex = 2;

static NSString* kEventCell = @"EventCell";

@implementation EventPoolViewController

#pragma mark - Inits

- (id)init
{
    self = [super initWithNibName:@"EventPoolView" bundle:nil];
    if (self) {
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
    [PCUtils throughExceptionIfObject:pool notKindOfClass:[EventPool class]];
    self = [self init];
    if (self) {
        self.poolId = pool.poolId;
        self.title = pool.poolTitle;
        self.poolReply = [self.eventsService getFromCacheEventPoolForRequest:[self createEventPoolRequest]];
        [self initRefreshControl];
    }
    return self;
}

- (id)initAndLoadRootPool {
    self = [self init];
    if (self) {
        self.poolId = [eventsConstants CONTAINER_EVENT_ID];
        self.poolReply = [self.eventsService getFromCacheEventPoolForRequest:[self createEventPoolRequest]];
        self.title = NSLocalizedStringFromTable(@"PluginName", @"EventsPlugin", nil);
        [self initRefreshControl];
    }
    return self;
}

- (id)initAndLoadEventPoolWithId:(int64_t)poolId {
    self = [self init];
    if (self) {
        self.poolId = poolId;
        self.poolReply = [self.eventsService getFromCacheEventPoolForRequest:[self createEventPoolRequest]];
        if (self.poolReply) {
            self.title = self.poolReply.eventPool.poolTitle;
        }
        [self initRefreshControl];
    }
    return self;
}

#pragma mark - View load and visibility

- (void)viewDidLoad {
    [super viewDidLoad];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(favoritesWereUpdated) name:kFavoritesEventItemsUpdatedNotification object:self.eventsService];
    self.tableView.rowHeight = [EventItemCell height];
    if (self.poolId == [eventsConstants CONTAINER_EVENT_ID]) {
        [[GANTracker sharedTracker] trackPageview:@"/v3r1/events" withError:NULL];
    } else {
        [[GANTracker sharedTracker] trackPageview:[NSString stringWithFormat:@"/v3r1/%lld/subevents", self.poolId] withError:NULL];
    }
    
    [(PCTableViewWithRemoteThumbnails*)(self.tableView) setTemporaryThumnail:[UIImage imageNamed:@"NoImageCell_64"]];
    [self showButtonsConditionally];
    [self fillCollectionsFromReplyAndSelection];
    [self.tableView reloadData];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    if (!self.poolReply || [self.pcRefreshControl shouldRefreshDataForValidity:kRefreshValiditySeconds]) {
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

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation //<= iOS5
{
    if ([PCUtils isIdiomPad]) {
        return YES;
    } else {
        return (interfaceOrientation == UIInterfaceOrientationPortrait);
    }
}

#pragma mark - Refresh control

- (void)favoritesWereUpdated {
    [self fillCollectionsFromReplyAndSelection];
    [self.tableView reloadData];
    [self reselectLastSelectedItem];
}

- (void)initRefreshControl {
    self.pcRefreshControl = [[PCRefreshControl alloc] initWithTableViewController:self pluginName:@"events" refreshedDataIdentifier:[NSString stringWithFormat:@"eventPool-%lld", self.poolId]];
    [self.pcRefreshControl setTarget:self selector:@selector(refresh)];
}

- (void)refresh {
    [self.eventsService cancelOperationsForDelegate:self]; //cancel before retrying
    [self.pcRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"LoadingEventPool", @"EventsPlugin", nil)];
    [self startGetEventPoolRequest];
}

- (EventPoolRequest*)createEventPoolRequest {
    return [[EventPoolRequest alloc] initWithEventPoolId:self.poolId userToken:[self.eventsService lastUserToken] lang:[PCUtils userLanguageCode] period:self.selectedPeriod];
}

- (void)startGetEventPoolRequest { 
    [self.eventsService getEventPoolForRequest:[self createEventPoolRequest] delegate:self];
}

- (void)reselectLastSelectedItem {
    if (self.selectedItem) {
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
}

#pragma mark - Buttons preparation and actions

- (void)showButtonsConditionally {
    if (!self.poolReply) {
        return;
    }
    
    NSMutableArray* rightElements = [NSMutableArray array];
    
    if (self.poolReply.eventPool.enableScan) {
        self.scanButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCamera target:self action:@selector(cameraButtonPressed)];
        //self.scanButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Scan", @"EventsPlugin", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(cameraButtonPressed)];
        [rightElements addObject:self.scanButton];
    }
    
    if (!self.poolReply.eventPool.disableFilterByCateg) { //will also disable period filtering
        //self.filterButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Filter", @"EventsPlugin", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(filterButtonPressed)];
        self.filterButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"EyeBarButton"] style:UIBarButtonItemStyleBordered target:self action:@selector(filterButtonPressed)];
        [rightElements addObject:self.filterButton];
    }
    
    self.navigationItem.rightBarButtonItems = rightElements;
}

- (void)filterButtonPressed {
    if (self.periodsSelectionActionSheet.isVisible) {
        [self.periodsSelectionActionSheet dismissWithClickedButtonIndex:[self.periodsSelectionActionSheet cancelButtonIndex] animated:YES];
        return;
    }
    
    if (!self.filterSelectionActionSheet) {
        
        if ([self periodButtonIndex] > 0) { //check if period selection should be displayed
            NSString* periodString = [NSString stringWithFormat:NSLocalizedStringFromTable(@"PeriodWithFormat", @"EventsPlugin", nil), [EventsUtils periodStringForEventsPeriod:self.selectedPeriod selected:NO]];
            
            self.filterSelectionActionSheet = [[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:nil otherButtonTitles:NSLocalizedStringFromTable(@"SelectCategory", @"EventsPlugin", nil), periodString, nil];
        } else {
            self.filterSelectionActionSheet = [[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:nil otherButtonTitles:NSLocalizedStringFromTable(@"SelectCategory", @"EventsPlugin", nil), nil];
        }
        self.filterSelectionActionSheet.delegate = self;
    }
    [self.filterSelectionActionSheet toggleFromBarButtonItem:self.filterButton animated:YES];
}

- (NSInteger)goToCategoryButtonIndex {
    if (self.poolReply.eventPool.disableFilterByCateg) {
        return -1; //should not be required
    }
    return 0;
}

- (NSInteger)periodButtonIndex {
    if (self.poolReply.eventPool.disableFilterByCateg) {
        return -1; //disableFilterByCateg hides filter button and thus also period selection
    }
    if (self.poolId != [eventsConstants CONTAINER_EVENT_ID]) {
        return -1;
    }
    return 1;
}

- (void)cameraButtonPressed {
    ZBarReaderViewController *reader = [ZBarReaderViewController new];
    reader.readerDelegate = self;
    reader.supportedOrientationsMask = ZBarOrientationMask(UIInterfaceOrientationPortrait);
    
    ZBarImageScanner *scanner = reader.scanner;
    
    [scanner setSymbology: ZBAR_I25 config: ZBAR_CFG_ENABLE to: 0];
    
    
    [self presentViewController:reader animated:YES completion:NULL];
    
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

- (void)presentPeriodSelectionActionSheet {
    if (!self.periodsSelectionActionSheet) {

        self.periodsSelectionActionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedStringFromTable(@"ShowEventsFor...", @"EventsPlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:nil otherButtonTitles:
                                            [EventsUtils periodStringForEventsPeriod:EventsPeriods_ONE_WEEK selected:(self.selectedPeriod == EventsPeriods_ONE_WEEK)],
                                            [EventsUtils periodStringForEventsPeriod:EventsPeriods_ONE_MONTH selected:(self.selectedPeriod == EventsPeriods_ONE_MONTH)],
                                            [EventsUtils periodStringForEventsPeriod:EventsPeriods_SIX_MONTHS selected:(self.selectedPeriod == EventsPeriods_SIX_MONTHS)]
                                            , nil];

    }
    [self.periodsSelectionActionSheet showFromBarButtonItem:self.filterButton animated:YES];
}

#pragma mark - UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet willDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (actionSheet == self.filterSelectionActionSheet) {
        if (buttonIndex == [self goToCategoryButtonIndex]) {
            [self presentCategoriesController];
        } else if (buttonIndex == [self periodButtonIndex]) {
            [self presentPeriodSelectionActionSheet];
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
        }
        [self.eventsService saveSelectedPoolPeriod:self.selectedPeriod];
        if (buttonIndex >= 0 && (buttonIndex != [self.periodsSelectionActionSheet cancelButtonIndex])) {
            [self.tableView scrollsToTop];
            [self refresh];
        }
        self.periodsSelectionActionSheet = nil;
    }
}

#pragma mark - Data fill

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
    
    //Force Favorites and Features to be always selected
    self.selectedCategories[[EventsUtils favoriteCategory]] = self.poolReply.categs[[EventsUtils favoriteCategory]];
    self.selectedCategories[[EventsUtils featuredCategory]] = self.poolReply.categs[[EventsUtils featuredCategory]];
    
    NSDictionary* itemsForCategNumber = [EventsUtils sectionsOfEventItem:[self.poolReply.childrenItems allValues] forCategories:self.selectedCategories andTags:self.selectedTags];
    
    
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


#pragma mark - ZBar and Image picker delegate

- (void) imagePickerController:(UIImagePickerController*)reader didFinishPickingMediaWithInfo:(NSDictionary*)info
{
    // ADD: get the decode results
    id<NSFastEnumeration> results = [info objectForKey: ZBarReaderControllerResults];
    ZBarSymbol *symbol = nil;
    for(symbol in results)
        //just grab the first barcode
        break;
    
    // EXAMPLE: do something useful with the barcode data
    
    if (!symbol.data) {
        [self showQRCodeError];
        return;
    }
    
    NSDictionary* parameters = [PCUtils urlStringParameters:symbol.data];
    
    if (!parameters) {
        [self showQRCodeError];
    }
    
    BOOL found = NO;
    
    NSString* userToken = parameters[@"userToken"];
    if (userToken) {
        found = YES;
        [self.eventsService saveUserToken:userToken];
        [self refresh];
    }
    
    NSString* exchangeToken = parameters[@"exchangeToken"];
    if (exchangeToken) {
        found = YES;
        if (![self.eventsService lastUserToken]) {
            [self showNoUserTokenError];
        } else {
            ExchangeRequest* req = [[ExchangeRequest alloc] initWithUserToken:[self.eventsService lastUserToken] exchangeToken:exchangeToken];
            [self.eventsService exchangeContactsForRequest:req delegate:self];
        }
    }
    
    NSString* eventItemIdToMarkFavorite = parameters[@"markFavorite"];
    if (eventItemIdToMarkFavorite) {
        found = YES;
        int64_t itemId = [eventItemIdToMarkFavorite longLongValue];
        /*[self.eventsService addFavoriteEventItemId:itemId];
        [self fillSectionsFromReplyForCurrentCategoriesAndTags];
        [self.tableView reloadData];*/
        EventItemViewController* viewController = [[EventItemViewController alloc] initAndLoadEventItemWithId:itemId];
        [self.navigationController pushViewController:viewController animated:YES];
    }
    
    if (found) {
        [self dismissViewControllerAnimated:YES completion:NULL];
    } else {
        [self showQRCodeError];
    }
}

- (void)showQRCodeError {
    [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"QRCodeErrorMessage", @"EventsPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
}

- (void)showNoUserTokenError {
    [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"UserNotRegistered", @"EventsPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
}

#pragma mark - EventsServiceDelegate

- (void)getEventPoolForRequest:(EventPoolRequest *)request didReturn:(EventPoolReply *)reply {
    switch (reply.status) {
        case 200:
            self.poolReply = reply;
            if (self.poolReply.eventPool.poolTitle) {
                self.title = self.poolReply.eventPool.poolTitle;
            }
            [self showButtonsConditionally];
            [self fillCollectionsFromReplyAndSelection];
            [self.tableView reloadData];
            [self reselectLastSelectedItem];
            [self.pcRefreshControl endRefreshing];
            [self.pcRefreshControl markRefreshSuccessful];
            break;
        default:
            [self getEventPoolFailedForRequest:request];
            break;
    }
}

- (void)getEventPoolFailedForRequest:(EventPoolRequest *)request {
    [self error];
}

- (void)exchangeContactsForRequest:(ExchangeRequest *)request didReturn:(ExchangeReply *)reply {
    switch (reply.status) {
        case 200:
            [self refresh];
            break;
        case 400:
            [self showExchangeContactError];
            break;
        case 500:
            [self exchangeContactsFailedForRequest:request];
        default:
            break;
    }
}

- (void)exchangeContactsFailedForRequest:(ExchangeRequest *)request {
    [self error];
}

- (void)showExchangeContactError {
    [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ExchangeContactErrorMessage", @"EventsPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
}

- (void)error {
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

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if ([self.itemsForSection count] == 0 || [self.itemsForSection[section] count] == 0) {
        return 0.0;
    }
    return [PCValues tableViewSectionHeaderHeight];
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    if ([self.itemsForSection[section] count] == 0) {
        return nil;
    }
    
    NSString* title = self.sectionsNames[section];
    
    return [[PCTableViewSectionHeader alloc] initWithSectionTitle:title tableView:tableView];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    EventItem* eventItem = self.itemsForSection[indexPath.section][indexPath.row];
    
    EventItemViewController* eventItemViewController = [[EventItemViewController alloc] initWithEventItem:eventItem];
    
    if (self.splitViewController && self.poolId == [eventsConstants CONTAINER_EVENT_ID]) {
        self.selectedItem = eventItem;
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], [[UINavigationController alloc] initWithRootViewController:eventItemViewController]];
    } else {
        [self.navigationController pushViewController:eventItemViewController animated:YES];
    }
}


#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.poolReply && [self.poolReply.childrenItems count] == 0) {
        if (indexPath.row == 1) {
            NSString* message = self.poolReply.eventPool.noResultText;
            if (!message) {
                message = NSLocalizedStringFromTable(@"NoEvent", @"EventsPlugin", nil);
            }
            return [[PCCenterMessageCell alloc] initWithMessage:message];
        } else {
            return [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        }
    }
    
    EventItem* eventItem = self.itemsForSection[indexPath.section][indexPath.row];
    EventItemCell *cell = (EventItemCell*)[tableView dequeueReusableCellWithIdentifier:kEventCell];
    
    if (!cell) {
        cell = [[EventItemCell alloc] initWithEventItem:eventItem reuseIdentifier:kEventCell];
    }
    
    cell.eventItem = eventItem;
    
    if ([self.poolReply.eventPool.poolTitle isEqualToString:@"Schedule"]) { //special case to show hour for EDIC open house
        cell.rightSubtitleLabel.text = [eventItem dateString:EventItemDateStyleLong];
    }
    
    /*UITableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:kEventCell];
    
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:kEventCell];
    }
    
    cell.textLabel.text = eventItem.eventTitle;
    cell.textLabel.numberOfLines = 2;
    cell.detailTextLabel.text = eventItem.eventPlace;
     */
    
    [(PCTableViewWithRemoteThumbnails*)(self.tableView) setThumbnailURL:[NSURL URLWithString:eventItem.eventThumbnail] forCell:cell atIndexPath:indexPath];
    
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    if ([self.poolReply.childrenItems count] == 0 && self.poolReply.eventPool.noResultText) {
        return 2; //first empty cell, second cell says no content
    }
    return [self.itemsForSection[section] count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    if (!self.poolReply) {
        return 0;
    }
    
    if ([self.poolReply.childrenItems count] == 0 && self.poolReply.eventPool.noResultText) {
        [PCUtils addCenteredLabelInView:self.tableView withMessage:self.poolReply.eventPool.noResultText];
        return 1;
    }
    [PCUtils removeCenteredLabelInView:self.tableView];
    return [self.itemsForSection count];
}

#pragma mark - dealloc

- (void)dealloc {
    [self.eventsService cancelOperationsForDelegate:self];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
