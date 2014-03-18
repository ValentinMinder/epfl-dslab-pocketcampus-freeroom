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
@property (nonatomic, strong) NSMutableDictionary* responseForReferenceDate;

@end

@implementation IsAcademiaDayScheduleViewController

#pragma mark - Init

- (id)init
{
    self = [super init];
    if (self) {
        self.title = NSLocalizedStringFromTable(@"MySchedule", @"IsAcademiaPlugin", nil);
        self.responseForReferenceDate = [NSMutableDictionary dictionary];
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

#pragma mark - Refresh & actions

- (void)refreshPressed {
    
}

- (void)refreshForDisplayedDay {
    [self.isaService cancelOperationsForDelegate:self];
    ScheduleRequest* req = [ScheduleRequest new];
    req.weekStart = [[self mondayReferenceDateForDate:self.dayView.date] timeIntervalSince1970];
    req.language = [PCUtils userLanguageCode];
    [self.isaService getScheduleWithRequest:req delegate:self];
}

- (void)todayPressed {
    self.dayView.date = [NSDate date];
}

#pragma mark - Date utils

- (NSDate*)mondayReferenceDateForDate:(NSDate*)date {
    [PCUtils throwExceptionIfObject:date notKindOfClass:[NSDate class]];
    NSCalendar* gregorianCalendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    gregorianCalendar.locale = [NSLocale currentLocale];
    NSDateComponents* comps = [gregorianCalendar components:NSYearCalendarUnit | NSWeekCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit | NSSecondCalendarUnit fromDate:date];
    [comps setWeekday:2]; //Monday
    [comps setWeek:comps.week];
    [comps setHour:8]; //8a.m.
    [comps setMinute:0];
    [comps setSecond:0];
    NSDate* monday8am = [gregorianCalendar dateFromComponents:comps];
    return monday8am;
}

#pragma mark - IsAcademiaService

- (void)getScheduleForRequest:(ScheduleRequest *)request didReturn:(ScheduleResponse *)scheduleResponse {
    switch (scheduleResponse.statusCode) {
        case IsaStatusCode_OK:
        {
            NSDate* date = [self mondayReferenceDateForDate:[NSDate dateWithTimeIntervalSince1970:request.weekStart]];
            self.responseForReferenceDate[date] = scheduleResponse;
            [self.dayView reloadData];
            break;
        }
        case IsaStatusCode_INVALID_SESSION:
        {
            __weak __typeof(self) weakSelf = self;
            [[AuthenticationController sharedInstance] addLoginObserver:self success:^{
                [weakSelf refreshForDisplayedDay];
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

/*- (void)calendarDayTimelineView:(TKCalendarDayView *)calendarDay didMoveToDate:(NSDate *)date {
    if (!self.responseForReferenceDate[[self mondayReferenceDateForDate:date]]) {
        [self refreshForDisplayedDay];
    }
}*/

- (void)calendarDayTimelineView:(TKCalendarDayView *)calendarDay eventViewWasSelected:(TKCalendarDayEventView *)eventView {
    
}

#pragma mark - TKCalendarDayViewDataSource

- (NSArray *)calendarDayTimelineView:(TKCalendarDayView *)calendarDay eventsForDate:(NSDate *)date {
    StudyDay* studyDay = self.responseForReferenceDate[[self mondayReferenceDateForDate:date]];
    if (!studyDay) {
        [self refreshForDisplayedDay];
        return @[];
    }
    
    NSMutableArray* eventViews = [NSMutableArray arrayWithCapacity:studyDay.periods.count];
    for (StudyPeriod* period in studyDay.periods) {
        TKCalendarDayEventView* view = [self.dayView dequeueReusableEventView];
        if (!view) {
            view = [TKCalendarDayEventView eventView];
        }
        view.startDate = [NSDate dateWithTimeIntervalSince1970:period.startTime];
        view.endDate = [NSDate dateWithTimeIntervalSince1970:period.endTime];
        view.titleLabel.text = period.name;
        [eventViews addObject:view];
    }
    return eventViews;
}

#pragma mark - Dealloc

- (void)dealloc
{
    [self.isaService cancelOperationsForDelegate:self];
}

@end
