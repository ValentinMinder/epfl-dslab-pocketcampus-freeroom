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

#import "MBProgressHUD.h"

#import "PCDatePickerView.h"

@interface IsAcademiaDayScheduleViewController ()<IsAcademiaServiceDelegate, UIActionSheetDelegate>

@property (nonatomic, strong) IsAcademiaService* isaService;
@property (nonatomic, strong) NSMutableDictionary* responseForReferenceDate;

@property (nonatomic, strong) MBProgressHUD* progressHUD;
@property (nonatomic, strong) MBProgressHUD* messageHUD;

@property (nonatomic, strong) UIActionSheet* detailsActionSheet;

@property (nonatomic, strong) PCDatePickerView* datePickerView;

@property (nonatomic, strong) NSDate* lastRefreshDate;

@end

@implementation IsAcademiaDayScheduleViewController

#pragma mark - Init

- (id)init
{
    self = [super init];
    if (self) {
        self.gaiScreenName = @"/isacademia";
        self.responseForReferenceDate = [NSMutableDictionary dictionary];
        self.isaService = [IsAcademiaService sharedInstanceToRetain];
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.tabBarController.tabBar.frame = CGRectZero;
    self.navigationController.view.backgroundColor = [UIColor whiteColor];
    self.dayView.is24hClock = [PCUtils userLocaleIs24Hour];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(refreshPressed)];
    
    UIBarButtonItem* todayItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Today", @"PocketCampus", nil) style:UIBarButtonItemStylePlain target:self action:@selector(todayPressed)];
    UIBarButtonItem* flexibleSpaceItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
    UIBarButtonItem* goToDateItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"GoToDate", @"IsAcademiaPlugin", nil) style:UIBarButtonItemStylePlain target:self action:@selector(goToDatePressed)];
    self.toolbarItems = @[todayItem, flexibleSpaceItem, goToDateItem];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(preferredContentSizeChanged) name:UIContentSizeCategoryDidChangeNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(appDidBecomeActive) name:UIApplicationDidBecomeActiveNotification object:nil];
    
    [[MainController publicController] addPluginStateObserver:self selector:@selector(willLoseForeground) notification:PluginWillLoseForegroundNotification pluginIdentifierName:@"IsAcademia"];
    [[MainController publicController] addPluginStateObserver:self selector:@selector(didEnterForeground) notification:PluginDidEnterForegroundNotification pluginIdentifierName:@"IsAcademia"];
    
    self.progressHUD = [[MBProgressHUD alloc] initWithView:self.dayView];
    self.progressHUD.userInteractionEnabled = NO;
    [self.dayView addSubview:self.progressHUD];
    self.progressHUD.opacity = 0.6;
    self.progressHUD.userInteractionEnabled = NO; //so that day view is still touchable
    
    self.messageHUD = [[MBProgressHUD alloc] initWithView:self.dayView];
    self.messageHUD.userInteractionEnabled = NO;
    [self.dayView addSubview:self.messageHUD];
    self.messageHUD.opacity = 0.5;
    self.messageHUD.mode = MBProgressHUDModeText;
    
    [self calendarDayTimelineView:self.dayView didMoveToDate:self.dayView.date]; //force refresh
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
    self.navigationController.navigationBar.hairlineDividerView.hidden = YES;
    self.navigationController.toolbarHidden = NO;
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    self.navigationController.navigationBar.hairlineDividerView.hidden = NO;
    self.navigationController.toolbarHidden = YES;
}

- (NSUInteger)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Notifications

- (void)preferredContentSizeChanged {
    [self.dayView reloadData];
}

- (void)appDidBecomeActive {
    [self.dayView reloadData];
    [self refreshAndGoToTodayIfNeeded];
}

- (void)willLoseForeground {
    [self.datePickerView dismiss];
}

- (void)didEnterForeground {
    if (self.datePickerView) {
        //check uncessary, just to make clear that
        //self.datePickerView != nil means it was presented
        [self.datePickerView presentInView:self.view];
    }
}

#pragma mark - Refresh & actions

- (void)refreshAndGoToTodayIfNeeded {
    if (!self.lastRefreshDate || ![self.lastRefreshDate isToday]) {
        self.dayView.date = [NSDate date];
        [self refreshForDisplayedDaySkipCache:NO];
    }
}

- (void)refreshPressed {
    [self trackAction:PCGAITrackerActionRefresh];
    [self refreshForDisplayedDaySkipCache:YES];
}

- (void)refreshForDisplayedDaySkipCache:(BOOL)skipCache {
    self.lastRefreshDate = [NSDate date];
    [self.messageHUD hide:NO];
    [self.progressHUD show:NO];
    [self.isaService cancelOperationsForDelegate:self];
    ScheduleRequest* req = [ScheduleRequest new];
    NSDate* monday8am = [IsAcademiaModelAdditions mondayReferenceDateForDate:self.dayView.date];
    req.weekStart = [monday8am timeIntervalSince1970]*1000;
    req.language = [PCUtils userLanguageCode];
    [self.isaService getScheduleWithRequest:req skipCache:skipCache delegate:self];
}

- (void)todayPressed {
    [self trackAction:@"Today"];
    self.dayView.date = [NSDate date];
    [self calendarDayTimelineView:self.dayView didMoveToDate:self.dayView.date]; //force refresh
}

- (void)goToDatePressed {
    PCDatePickerView* pcDatePicker = [PCDatePickerView new];
    pcDatePicker.datePicker.datePickerMode = UIDatePickerModeDate;
    pcDatePicker.datePicker.date = self.dayView.date;
    __weak __typeof(self) welf = self;
    [pcDatePicker setUserValidatedDateBlock:^(PCDatePickerView* view, NSDate* date) {
        welf.dayView.date = date;
        [welf calendarDayTimelineView:welf.dayView didMoveToDate:welf.dayView.date]; //force refresh
        [view dismiss];
        welf.datePickerView = nil;
        
        //GA stuff
        NSDateFormatter* formatter = [NSDateFormatter new];
        formatter.dateFormat = @"yyyy-MM-dd";
        NSString* dateString = [formatter stringFromDate:date];
        [welf trackAction:@"GoToDateSelected" contentInfo:dateString];
    }];
    [pcDatePicker setUserCancelledBlock:^(PCDatePickerView* view) {
        [view dismiss];
        welf.datePickerView = nil;
    }];
    [pcDatePicker presentFromBarButtonItem:[self.toolbarItems lastObject]];
    self.datePickerView = pcDatePicker;
    [self trackAction:@"GoToDate"];
}

#pragma mark - IsAcademiaService

- (void)getScheduleForRequest:(ScheduleRequest *)request didReturn:(ScheduleResponse *)scheduleResponse {
    [self.progressHUD hide:YES];
    switch (scheduleResponse.statusCode) {
        case IsaStatusCode_OK:
        {
            NSDate* date = [IsAcademiaModelAdditions mondayReferenceDateForDate:request.weekStart ? [NSDate dateWithTimeIntervalSince1970:request.weekStart/1000] : [NSDate date]];
            self.responseForReferenceDate[date] = scheduleResponse;
            [self.dayView reloadData];
            [self calendarDayTimelineView:self.dayView didMoveToDate:self.dayView.date];
            break;
        }
        case IsaStatusCode_INVALID_SESSION:
        {
            __weak __typeof(self) welf = self;
            [[AuthenticationController sharedInstance] addLoginObserver:self success:^{
                [welf refreshForDisplayedDaySkipCache:YES];
            } userCancelled:^{
                welf.messageHUD.labelText = NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil);
                welf.messageHUD.detailsLabelText = NSLocalizedStringFromTable(@"LoginRequired", @"PocketCampus", nil);
                [welf.messageHUD show:NO];
            } failure:^(NSError *error) {
                [welf getScheduleFailedForRequest:request];
            }];
            break;
        }
        case IsaStatusCode_NETWORK_ERROR:
        {
            self.messageHUD.labelText = NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil);
            self.messageHUD.detailsLabelText = NSLocalizedStringFromTable(@"IsAcademiaServerUnreachableTryLater", @"IsAcademiaPlugin", nil);
            [self.messageHUD show:NO];
            break;
        }
        case IsaStatusCode_ISA_ERROR:
        {
            self.messageHUD.labelText = NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil);
            self.messageHUD.detailsLabelText = NSLocalizedStringFromTable(@"IsAcademiaServerProblemTryLater", @"IsAcademiaPlugin", nil);
            [self.messageHUD show:NO];
            break;
        }
        default:
            [self getScheduleFailedForRequest:request];
            break;
    }
}

- (void)getScheduleFailedForRequest:(ScheduleRequest *)request {
    [self.progressHUD hide:NO];
    self.messageHUD.labelText = NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil);
    self.messageHUD.detailsLabelText = NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil);
    [self.messageHUD show:NO];
}

- (void)serviceConnectionToServerFailed {
    [self.progressHUD hide:NO];
    self.messageHUD.labelText = NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil);
    self.messageHUD.detailsLabelText = NSLocalizedStringFromTable(@"ConnectionToServerTimedOutAlert", @"PocketCampus", nil);
    [self.messageHUD show:NO];
}

#pragma mark - UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet willDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (actionSheet == self.detailsActionSheet) {
        if (buttonIndex != actionSheet.cancelButtonIndex) {
            NSString* room = [actionSheet buttonTitleAtIndex:buttonIndex];
            UIViewController* viewController = [MapController viewControllerWithInitialSearchQuery:room];
            [self.navigationController pushViewController:viewController animated:YES];
            [self trackAction:@"ViewRoomOnMap" contentInfo:room];
        }
        self.detailsActionSheet = nil;
    }
}

#pragma mark - TKCalendarDayViewDelegate

- (void)calendarDayTimelineView:(TKCalendarDayView *)calendarDay didMoveToDate:(NSDate *)date {
    ScheduleResponse* scheduleResponse = self.responseForReferenceDate[[IsAcademiaModelAdditions mondayReferenceDateForDate:date]];
    if (scheduleResponse) {
        [self.isaService cancelOperationsForDelegate:self];
        [self.progressHUD hide:NO];
        StudyDay* studyDay = [scheduleResponse studyDayForDate:date];
        if (studyDay.periods.count == 0) {
            self.messageHUD.labelText = [date isToday] ? NSLocalizedStringFromTable(@"NoCourseToday", @"IsAcademiaPlugin", nil) : NSLocalizedStringFromTable(@"NoCourseOnThatDay", @"IsAcademiaPlugin", nil);
            self.messageHUD.detailsLabelText = nil;
            [self.messageHUD show:YES];
        } else {
            [self.messageHUD hide:NO];
        }
    } else {
        [self refreshForDisplayedDaySkipCache:NO];
    }
}

- (void)calendarDayTimelineView:(TKCalendarDayView *)calendarDay eventViewWasSelected:(TKCalendarDayEventView *)eventView {
    IsAcademiaStudyPeriodCalendarDayEventView* view = (IsAcademiaStudyPeriodCalendarDayEventView*)eventView;
    if (![view isKindOfClass:[IsAcademiaStudyPeriodCalendarDayEventView class]]) {
        return;
    }
    StudyPeriod* period = view.studyPeriod;
    if (!period) {
        return;
    }
    NSArray* rooms = view.studyPeriod.rooms;
    NSString* sheetString = [NSString stringWithFormat:NSLocalizedStringFromTable(rooms.count > 0 ? @"CourseDetailsAndRoomsActionSheetWithFormat" : @"CourseDetailsActionSheetWithFormat", @"IsAcademiaPlugin", nil), period.periodTypeString, period.name, period.startAndEndTimeString];
    self.detailsActionSheet = [[UIActionSheet alloc] initWithTitle:sheetString delegate:self cancelButtonTitle:nil destructiveButtonTitle:nil otherButtonTitles:nil];
    for (NSString* room in rooms) {
        [self.detailsActionSheet addButtonWithTitle:room];
    }
    [self.detailsActionSheet addButtonWithTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil)];
    self.detailsActionSheet.cancelButtonIndex = self.detailsActionSheet.numberOfButtons-1;
    [self.detailsActionSheet showFromToolbar:self.navigationController.toolbar];
    [self trackAction:@"ViewPeriodProperties" contentInfo:period.name];
}

#pragma mark - TKCalendarDayViewDataSource

- (NSArray *)calendarDayTimelineView:(TKCalendarDayView *)calendarDay eventsForDate:(NSDate *)date {
    ScheduleResponse* scheduleResponse = self.responseForReferenceDate[[IsAcademiaModelAdditions mondayReferenceDateForDate:date]];
    StudyDay* studyDay = [scheduleResponse studyDayForDate:date];
    if (!studyDay.periods.count) {
        return @[];
    }
    NSMutableArray* eventViews = [NSMutableArray arrayWithCapacity:studyDay.periods.count];
    BOOL systemIsOutsideEPFLTimeZone = [PCUtils systemIsOutsideEPFLTimeZone];
    int studyDayIndex = 0;
    if (systemIsOutsideEPFLTimeZone) {
        studyDayIndex = (int)[scheduleResponse.days indexOfObject:studyDay];
    }
    [scheduleResponse.days enumerateObjectsUsingBlock:^(StudyDay* _studyDay, NSUInteger indexU, BOOL *stop) {
        int index = (int)indexU;
        BOOL includeDay = (_studyDay == studyDay) || (systemIsOutsideEPFLTimeZone && (abs(index-studyDayIndex) <= 1) ); //if outside time zone, also include 1 day before and 1 day after
        if (includeDay) {
            for (StudyPeriod* period in _studyDay.periods) {
                IsAcademiaStudyPeriodCalendarDayEventView* view = (IsAcademiaStudyPeriodCalendarDayEventView*)[self.dayView dequeueReusableEventView];
                if (!view) {
                    view = [IsAcademiaStudyPeriodCalendarDayEventView studyPeriodEventView];
                }
                //#warning REMOVE
                //period.name = @"dsfjhaiusdz fuaszdfipu atsodiuftaouzsdt f uzastdfo";
                //period.endTime = period.startTime + 2700*1000;
                //period.rooms = @[];
                //period.periodType = StudyPeriodType_WRITTEN_EXAM;
                view.studyPeriod = period;
                [eventViews addObject:view];
            }
        }
    }];
    
    return eventViews;
}

#pragma mark - Dealloc

- (void)dealloc
{
    [self.isaService cancelOperationsForDelegate:self];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
