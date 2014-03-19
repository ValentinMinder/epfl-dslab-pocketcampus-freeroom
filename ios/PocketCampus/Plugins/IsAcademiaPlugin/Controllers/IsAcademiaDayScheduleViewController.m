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

//  Created by Loïc Gardiol on 17.03.14.

#import "IsAcademiaDayScheduleViewController.h"

#import <TapkuLibrary/TapkuLibrary.h>

#import "IsAcademiaService.h"

#import "IsAcademiaStudyPeriodCalendarDayEventView.h"

#import "MapController.h"

@interface IsAcademiaDayScheduleViewController ()<IsAcademiaServiceDelegate, UIActionSheetDelegate>

@property (nonatomic, strong) IsAcademiaService* isaService;
@property (nonatomic, strong) NSMutableDictionary* responseForReferenceDate;

@property (nonatomic, strong) UIActionSheet* roomsActionSheet;

@end

@implementation IsAcademiaDayScheduleViewController

#pragma mark - Init

- (id)init
{
    self = [super init];
    if (self) {
        self.title = NSLocalizedStringFromTable(@"MySchedule", @"IsAcademiaPlugin", nil);
        self.responseForReferenceDate = [NSMutableDictionary dictionary];
        self.isaService = [IsAcademiaService sharedInstanceToRetain];
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
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(preferredContentSizeChanged) name:UIContentSizeCategoryDidChangeNotification object:nil];
    [self refreshForDisplayedDay];
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

#pragma mark - Notifications

- (void)preferredContentSizeChanged {
    [self.dayView reloadData];
}

#pragma mark - Refresh & actions

- (void)refreshPressed {
    [self refreshForDisplayedDay];
}

- (void)refreshForDisplayedDay {
    [self.isaService cancelOperationsForDelegate:self];
    ScheduleRequest* req = [ScheduleRequest new];
    NSDate* monday8am = [self mondayReferenceDateForDate:self.dayView.date];
    req.weekStart = [monday8am timeIntervalSince1970]*1000;
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
    [comps setYear:comps.year];
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
            NSDate* date = request.weekStart ? [self mondayReferenceDateForDate:[NSDate dateWithTimeIntervalSince1970:request.weekStart/1000]] : [self mondayReferenceDateForDate:[NSDate date]];
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
        {
            [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"IsAcademiaServerUnreachableTryLater", @"IsAcademiaPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
            break;
        }
        default:
            [self getScheduleFailedForRequest:request];
            break;
    }
}

- (void)getScheduleFailedForRequest:(ScheduleRequest *)request {
    [PCUtils showServerErrorAlert];
}

- (void)serviceConnectionToServerFailed {
    [PCUtils showConnectionToServerTimedOutAlert];
}

#pragma mark - UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet willDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (actionSheet == self.roomsActionSheet) {
        if (buttonIndex != actionSheet.cancelButtonIndex) {
            NSString* room = [actionSheet buttonTitleAtIndex:buttonIndex];
            UIViewController* viewController = [MapController viewControllerWithInitialSearchQuery:room];
            [self.navigationController pushViewController:viewController animated:YES];
        }
        self.roomsActionSheet = nil;
    }
}

#pragma mark - TKCalendarDayViewDelegate

- (void)calendarDayTimelineView:(TKCalendarDayView *)calendarDay didMoveToDate:(NSDate *)date {
    if (!self.responseForReferenceDate[[self mondayReferenceDateForDate:date]]) {
        [self refreshForDisplayedDay];
    }
}

- (void)calendarDayTimelineView:(TKCalendarDayView *)calendarDay eventViewWasSelected:(TKCalendarDayEventView *)eventView {
    IsAcademiaStudyPeriodCalendarDayEventView* view = (IsAcademiaStudyPeriodCalendarDayEventView*)eventView;
    NSArray* rooms = view.studyPeriod.rooms;
    if (rooms.count == 0) {
        return;
    } else if (rooms.count == 1) {
        NSString* room = [rooms lastObject];
        UIViewController* viewController = [MapController viewControllerWithInitialSearchQuery:room];
        [self.navigationController pushViewController:viewController animated:YES];
    } else {
        self.roomsActionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedStringFromTable(@"ShowOnMap", @"IsAcademiaPlugin", nil) delegate:self cancelButtonTitle:nil destructiveButtonTitle:nil otherButtonTitles:nil];
        for (NSString* room in rooms) {
            [self.roomsActionSheet addButtonWithTitle:room];
        }
        [self.roomsActionSheet addButtonWithTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil)];
        self.roomsActionSheet.cancelButtonIndex = self.roomsActionSheet.numberOfButtons-1;
        [self.roomsActionSheet showFromToolbar:self.navigationController.toolbar];
    }
}

#pragma mark - TKCalendarDayViewDataSource

- (NSArray *)calendarDayTimelineView:(TKCalendarDayView *)calendarDay eventsForDate:(NSDate *)date {
    ScheduleResponse* scheduleResponse = self.responseForReferenceDate[[self mondayReferenceDateForDate:date]];
    StudyDay* studyDay = [scheduleResponse studyDayForDate:date];
    if (!studyDay) {
        return @[];
    }
    
    NSMutableArray* eventViews = [NSMutableArray arrayWithCapacity:studyDay.periods.count];
    for (StudyPeriod* period in studyDay.periods) {
        IsAcademiaStudyPeriodCalendarDayEventView* view = (IsAcademiaStudyPeriodCalendarDayEventView*)[self.dayView dequeueReusableEventView];
        if (!view) {
            view = [IsAcademiaStudyPeriodCalendarDayEventView studyPeriodEventView];
        }
        //#warning REMOVE
        //period.name = @"dsfjhaiusdz fuaszdfipu atsodiuftaouzsdt f uzastdfo";
        //period.endTime = period.startTime + 2700*1000;
        //period.rooms = @[[period.rooms firstObject], @"INM 200", @"INJ 238"];
        view.studyPeriod = period;
        [eventViews addObject:view];
    }
    return eventViews;
}

#pragma mark - Dealloc

- (void)dealloc
{
    [self.isaService cancelOperationsForDelegate:self];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
