//
//  IsAcademiaDayScheduleViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 17.03.14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

#import "IsAcademiaDayScheduleViewController.h"

#import <TapkuLibrary/TapkuLibrary.h>

#import "IsAcademiaService.h"

@interface IsAcademiaDayScheduleViewController ()<IsAcademiaServiceDelegate>

@property (nonatomic, strong) IsAcademiaService* isaService;

@end

@implementation IsAcademiaDayScheduleViewController

#pragma mark - Init

- (id)init
{
    self = [super init];
    if (self) {
        self.title = NSLocalizedStringFromTable(@"MySchedule", @"IsAcademiaPlugin", nil);
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.navigationController.view.backgroundColor = [UIColor whiteColor];
    self.dayView.is24hClock = [PCUtils userLocaleIs24Hour];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(refreshPressed)];
    UIBarButtonItem* todayItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Today", @"PocketCampus", nil) style:UIBarButtonItemStylePlain target:self action:@selector(todayPressed)];
    self.toolbarItems = @[todayItem];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBar.hairlineDividerImageView.hidden = YES;
    [self.navigationController setToolbarHidden:NO];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    self.navigationController.navigationBar.hairlineDividerImageView.hidden = NO;
    [self.navigationController setToolbarHidden:YES];
}


- (NSUInteger)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Refresh & controls

- (void)refreshPressed {
#warning TODO
}

- (void)todayPressed {
#warning TODO
}

- (void)startScheduleRequestForDisplayedDay {
    #warning TODO
}

#pragma mark - IsAcademiaService

- (void)getScheduleForRequest:(ScheduleRequest *)request didReturn:(ScheduleResponse *)scheduleResponse {
    switch (scheduleResponse.statusCode) {
        case IsaStatusCode_OK:
#warning TODO
            break;
        case IsaStatusCode_INVALID_SESSION:
        {
            __weak __typeof(self) weakSelf = self;
            [[AuthenticationController sharedInstance] addLoginObserver:self success:^{
                [weakSelf startScheduleRequestForDisplayedDay];
            } userCancelled:^{
#warning TODO
            } failure:^{
                [self getScheduleFailedForRequest:request];
            }];
            break;
        }
        case IsaStatusCode_NETWORK_ERROR:
#warning TODO
            break;
        default:
            [self getScheduleFailedForRequest:request];
            break;
    }
}

- (void)getScheduleFailedForRequest:(ScheduleRequest *)request {
#warning TODO
}

- (void)serviceConnectionToServerFailed {
#warning TODO
}

#pragma mark - TKCalendarDayViewDelegate

- (void)calendarDayTimelineView:(TKCalendarDayView *)calendarDay didMoveToDate:(NSDate *)date {
    
}

- (void)calendarDayTimelineView:(TKCalendarDayView *)calendarDay eventViewWasSelected:(TKCalendarDayEventView *)eventView {
    
}

#pragma mark - TKCalendarDayViewDataSource

- (NSArray *)calendarDayTimelineView:(TKCalendarDayView *)calendarDay eventsForDate:(NSDate *)date {
    #warning TODO
    return @[];
}

#pragma mark - Dealloc

- (void)dealloc
{
    [self.isaService cancelOperationsForDelegate:self];
}

@end
