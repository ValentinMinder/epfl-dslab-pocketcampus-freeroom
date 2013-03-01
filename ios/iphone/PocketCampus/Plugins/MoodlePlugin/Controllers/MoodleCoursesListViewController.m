//
//  CoursesListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MoodleCoursesListViewController.h"

#import "PCRefreshControl.h"

#import "MoodleController.h"

#import "PCCenterMessageCell.h"

#import "GANTracker.h"

#import "MoodleCourseSectionsViewController.h"

#import "MoodleSplashDetailViewController.h"

#import "PCUtils.h"

@interface MoodleCoursesListViewController ()

@property (nonatomic, strong) MoodleService* moodleService;
@property (nonatomic, strong) NSArray* courses;
@property (nonatomic, strong) PCRefreshControl* pcRefreshControl;

@end

static const NSTimeInterval kRefreshValiditySeconds = 259200.0; //3 days

static NSString* kMoodleCourseListCell = @"MoodleCourseListCell";

@implementation MoodleCoursesListViewController

- (id)init
{
    self = [super initWithNibName:@"MoodleCoursesListView" bundle:nil];
    if (self) {
        self.title = NSLocalizedStringFromTable(@"MyCourses", @"MoodlePlugin", nil);
        self.moodleService = [MoodleService sharedInstanceToRetain];
        self.courses = [self.moodleService getFromCacheCourseListReply].iCourses;
        self.pcRefreshControl = [[PCRefreshControl alloc] initWithTableViewController:self pluginName:@"moodle" refreshedDataIdentifier:@"moodleCoursesList"];
        [self.pcRefreshControl setTarget:self selector:@selector(refresh)];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/moodle" withError:NULL];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    if (!self.courses || [self.pcRefreshControl shouldRefreshDataForValidity:kRefreshValiditySeconds]) {
        [self refresh];
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskAllButUpsideDown;
    
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation //iOS 5
{
    return UIInterfaceOrientationIsLandscape(interfaceOrientation) || (UIInterfaceOrientationPortrait);
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
        [[MoodleController sharedInstanceToRetain] addLoginObserver:self successBlock:successBlock userCancelledBlock:^{
            [self.pcRefreshControl endRefreshing];
        } failureBlock:^{
            [self error];
        }];
    }
}

#pragma mark - PCMasterSplitDelegate /* used on iPad */

- (UIViewController*)detailViewControllerThatShouldBeDisplayed {
    MoodleSplashDetailViewController* detailViewController = [[MoodleSplashDetailViewController alloc] init];
    return detailViewController;
}

#pragma mark - MoodleServiceDelegate

- (void)getCoursesList:(MoodleRequest *)aMoodleRequest didReturn:(CoursesListReply *)coursesListReply {
    switch (coursesListReply.iStatus) {
        case 200:
            self.courses = coursesListReply.iCourses;
            [self.moodleService saveToCacheCourseListReply:coursesListReply];
            [self.tableView reloadData];
            [self.pcRefreshControl endRefreshing];
            [self.pcRefreshControl markRefreshSuccessful];
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

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    MoodleCourse* course = self.courses[indexPath.row];
    MoodleCourseSectionsViewController* viewController = [[MoodleCourseSectionsViewController alloc] initWithCourse:course];
    [self.navigationController pushViewController:viewController animated:YES];
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.courses && [self.courses count] == 0) {
        if (indexPath.row == 1) {
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
        cell.textLabel.font = [UIFont boldSystemFontOfSize:18.0];
        cell.textLabel.numberOfLines = 2;
        cell.textLabel.adjustsFontSizeToFitWidth = YES;
    }
    
    cell.textLabel.text = course.iTitle;
    
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    if (self.courses && [self.courses count] == 0) {
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
