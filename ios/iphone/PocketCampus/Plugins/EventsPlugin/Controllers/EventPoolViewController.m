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

@end

static const NSTimeInterval kRefreshValiditySeconds = 1800; //30 min

static NSString* kEventCell = @"EventCell";

@implementation EventPoolViewController

#pragma mark - Inits

- (id)init
{
    self = [super initWithNibName:@"EventPoolView" bundle:nil];
    if (self) {
        self.poolId = 0;
        self.eventsService = [EventsService sharedInstanceToRetain];
    }
    return self;
}

- (id)initWithEventPool:(EventPool*)pool {
    [PCUtils throughExceptionIfObject:pool notKindOfClass:[EventPool class]];
    self = [self init];
    if (self) {
        self.poolId = pool.poolId;
        self.title = pool.poolTitle;
        [self initRefreshControl];
    }
    return self;
}

- (id)initAndLoadRootPool {
    self = [self init];
    if (self) {
        self.poolId = [eventsConstants CONTAINER_EVENT_ID];
        self.title = NSLocalizedStringFromTable(@"PluginName", @"EventsPlugin", nil);
        [self initRefreshControl];
    }
    return self;
}

- (id)initAndLoadEventPoolWithId:(int64_t)poolId {
    self = [self init];
    if (self) {
        self.poolId = poolId;
        [self initRefreshControl];
    }
    return self;
}

#pragma mark - View load and visibility

- (void)viewDidLoad {
    [super viewDidLoad];
    self.tableView.rowHeight = [EventItemCell height];
    if (self.poolId == -1) {
        [[GANTracker sharedTracker] trackPageview:@"/v3r1/events" withError:NULL];
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    if (!self.poolReply || [self.pcRefreshControl shouldRefreshDataForValidity:kRefreshValiditySeconds]) {
        [self refresh];
    } else {
        //if favorites modified, need to reload
        [self fillSectionsFromReplyForCurrentCategoriesAndTags];
        [self.tableView reloadData];
    }
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

- (BOOL)shouldAutorotate {
    //special dynamic conditions for temporary prevent rotation
    return NO;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation // iOS 5 and earlier
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

#pragma mark - Refresh control

- (void)initRefreshControl {
    self.pcRefreshControl = [[PCRefreshControl alloc] initWithTableViewController:self pluginName:@"events" refreshedDataIdentifier:[NSString stringWithFormat:@"eventPool-%lld", self.poolId]];
    [self.pcRefreshControl setTarget:self selector:@selector(refresh)];
}

- (void)refresh {
    [self.eventsService cancelOperationsForDelegate:self]; //cancel before retrying
    [self.pcRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"LoadingEventPool", @"EventsPlugin", nil)];
    [self startGetEventPoolRequest];
}

- (void)startGetEventPoolRequest {
    EventPoolRequest* req = [[EventPoolRequest alloc] initWithEventPoolId:self.poolId userToken:[self.eventsService lastUserToken] lang:[PCUtils userLanguageCode] period:EventsPeriods_ONE_MONTH]; //TODO dynamic period
    [self.eventsService getEventPoolForRequest:req delegate:self];
}

#pragma mark - Buttons preparation and actions

- (void)showButtonsConditionally {
    if (!self.poolReply) {
        return;
    }
    if (self.poolReply.eventPool.enableScan) {
        self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCamera target:self action:@selector(cameraButtonPressed)];
    }
}

- (void)cameraButtonPressed {
    ZBarReaderViewController *reader = [ZBarReaderViewController new];
    reader.readerDelegate = self;
    reader.supportedOrientationsMask = ZBarOrientationMask(UIInterfaceOrientationPortrait);
    
    ZBarImageScanner *scanner = reader.scanner;
    
    [scanner setSymbology: ZBAR_I25 config: ZBAR_CFG_ENABLE to: 0];
    
    
    [self presentViewController:reader animated:YES completion:NULL];
    
}


#pragma mark - Sections fill

- (void)fillSectionsFromReplyForCurrentCategoriesAndTags {
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
    self.sectionsNames = [tmpSectionsNames copy];
    self.itemsForSection = [tmpItemsForSection copy];
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
            [self showButtonsConditionally];
            [self fillSectionsFromReplyForCurrentCategoriesAndTags];
            [self.tableView reloadData];
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
    [self.navigationController pushViewController:[[EventItemViewController alloc] initWithEventItem:eventItem] animated:YES];
    //[self.navigationController pushViewController:[[EventItemViewController alloc] initAndLoadEventItemWithId:eventItem.eventId] animated:YES];
}


#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.poolReply && [self.poolReply.childrenItems count] == 0) {
        if (indexPath.row == 1) {
            NSString* message = self.poolReply.eventPool.noResultText;
            if (!message) {
                NSLocalizedStringFromTable(@"NoEvent", @"EventsPlugin", nil);
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
        [PCUtils addCenteredLabelInTableView:self.tableView withMessage:self.poolReply.eventPool.noResultText];
        return 1;
    }
    [PCUtils removeCenteredLabelInTableView:self.tableView];
    return [self.itemsForSection count];
}

#pragma mark - dealloc

- (void)dealloc {
    [self.eventsService cancelOperationsForDelegate:self];
}

@end
