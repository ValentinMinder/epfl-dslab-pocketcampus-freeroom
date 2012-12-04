//
//  CoursesListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "CoursesListViewController.h"

#import "PCRefreshControl.h"

#import "MoodleController.h"

#import "PCCenterMessageCell.h"

#import "GANTracker.h"

#import "CourseSectionsViewController.h"

@interface CoursesListViewController ()

@property (nonatomic, strong) MoodleService* moodleService;
@property (nonatomic, strong) NSArray* courses;
@property (nonatomic, strong) PCRefreshControl* pcRefreshControl;

@end

static NSString* kMoodleCourseListCell = @"MoodleCourseListCell";

@implementation CoursesListViewController

- (id)init
{
    self = [super initWithNibName:@"CoursesListView" bundle:nil];
    if (self) {
        self.moodleService = [MoodleService sharedInstanceToRetain];
        self.courses = [self.moodleService getFromCacheCoursesListForRequest:[self.moodleService createMoodleRequestWithCourseId:0]].iCourses;
        self.pcRefreshControl = [[PCRefreshControl alloc] initWithTableViewController:self];
        [self.pcRefreshControl setTarget:self selector:@selector(refresh)];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/moodle" withError:NULL];
}

- (void)viewDidAppear:(BOOL)animated {
    if (!self.courses) {
        [self refresh];
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - refresh control

- (void)refresh {
    [self.moodleService cancelOperationsForDelegate:self]; //cancel before retrying
    [self.pcRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"LoadingCourseList", @"MoodlePlugin", nil)];
    [self startGetCoursesListRequest];
}

- (void)startGetCoursesListRequest {
    VoidBlock successBlock = ^{
        [self.moodleService getCoursesList:[self.moodleService createMoodleRequestWithCourseId:0] withDelegate:self];
    };
    if ([self.moodleService lastSession]) {
        successBlock();
    } else {
        NSLog(@"-> No saved session, loggin in...");
        [[MoodleController sharedInstance] addLoginObserver:self operationIdentifier:nil successBlock:successBlock userCancelledBlock:^{
            [self.pcRefreshControl endRefreshing];
        } failureBlock:^{
            [self error];
        }];
    }
}

#pragma mark - MoodleServiceDelegate

- (void)getCoursesList:(MoodleRequest *)aMoodleRequest didReturn:(CoursesListReply *)coursesListReply {
    [[MoodleController sharedInstance] removeLoginObserver:self];
    switch (coursesListReply.iStatus) {
        case 200:
            self.courses = coursesListReply.iCourses;
            [self.tableView reloadData];
            [self.pcRefreshControl endRefreshing];
            break;
        case 407:
            [self.moodleService deleteSession];
            [self startGetCoursesListRequest];
            break;
        case 405:
            [self error];
            break;
        case 404:
        {
            [self.pcRefreshControl endRefreshing];
            UIAlertView* alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"MoodleDown", @"MoodlePlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [alert show];
        }
        default:
            [self getCoursesListFailed:aMoodleRequest];
            break;
    }
}

- (void)getCoursesListFailed:(MoodleRequest *)aMoodleRequest {
    [[MoodleController sharedInstance] removeLoginObserver:self];
    [self error];
}

- (void)error {
    self.pcRefreshControl.type = RefreshControlTypeProblem;
    self.pcRefreshControl.message = NSLocalizedStringFromTable(@"ConnectionToServerErrorShort", @"PocketCampus", nil);
    if (!self.courses) {
        [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
    }
    [self.pcRefreshControl hideInTimeInterval:2.0];
}

- (void)serviceConnectionToServerTimedOut {
    self.pcRefreshControl.type = RefreshControlTypeProblem;
    self.pcRefreshControl.message = NSLocalizedStringFromTable(@"ConnectionToServerTimedOutShort", @"PocketCampus", nil);
    if (!self.courses) {
        [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
    }
    [self.pcRefreshControl hideInTimeInterval:2.0];
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    MoodleCourse* course = self.courses[indexPath.row];
    CourseSectionsViewController* viewController = [[CourseSectionsViewController alloc] initWithCourseId:course.iId andCourseTitle:course.iTitle];
    [self.navigationController pushViewController:viewController animated:YES];
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.courses && [self.courses count] == 0) {
        if (indexPath.row == 2) {
            return [[PCCenterMessageCell alloc] initWithMessage:NSLocalizedStringFromTable(@"NotSubscribedToAnyCourse", @"MyEduPlugin", nil)];
        } else {
            return [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        }
    }
    
    MoodleCourse* course = self.courses[indexPath.row];
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kMoodleCourseListCell];
    
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kMoodleCourseListCell];
        cell.selectionStyle = UITableViewCellSelectionStyleGray;
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        cell.textLabel.font = [UIFont boldSystemFontOfSize:16.0];
        cell.textLabel.numberOfLines = 2;
        cell.textLabel.adjustsFontSizeToFitWidth = YES;
    }
    
    cell.textLabel.text = course.iTitle;
    
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    if ([self.courses count] == 0) {
        return 2; //first empty cell, second cell says no content
    }
    return [self.courses count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    if (!self.courses) {
        return 0;
    }
    return 1;
}

#pragma mark - dealloc

- (void)dealloc {
    [self.moodleService cancelOperationsForDelegate:self];
}

@end
