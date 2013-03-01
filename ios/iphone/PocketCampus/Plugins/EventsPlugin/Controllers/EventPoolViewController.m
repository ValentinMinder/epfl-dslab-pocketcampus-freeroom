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

@interface EventPoolViewController ()

@property (nonatomic) int64_t poolId;
@property (nonatomic, strong) EventPoolReply* poolReply;
@property (nonatomic, strong) PCRefreshControl* pcRefreshControl;
@property (nonatomic, strong) EventsService* eventsService;
@property (nonatomic, strong) NSArray* sections;

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
    }
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

#pragma mark - Sections fill

- (void)fillSectionsFromReplyForCurrentCategoriesAndTags {
    if (!self.poolReply) {
        return;
    }
    
    //TODO: fill sections accordingly
    self.sections = [NSArray arrayWithObject:[self.poolReply.childrenItems allValues]];
}

#pragma mark - EventsServiceDelegate

- (void)getEventPoolForRequest:(EventPoolRequest *)request didReturn:(EventPoolReply *)reply {
    switch (reply.status) {
        case 200:
            self.poolReply = reply;
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
    
    EventItem* eventItem = self.sections[indexPath.section][indexPath.row];
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
    if (self.poolReply && [self.poolReply.childrenItems count] == 0) {
        return 2; //first empty cell, second cell says no content
    }
    return [self.sections[section] count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    if (!self.sections) {
        return 0;
    }
    return 1;
}

#pragma mark - dealloc

- (void)dealloc {
    [self.eventsService cancelOperationsForDelegate:self];
}

@end
