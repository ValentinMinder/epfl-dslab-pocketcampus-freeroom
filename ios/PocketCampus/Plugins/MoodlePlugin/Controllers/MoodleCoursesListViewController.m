//
//  CoursesListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MoodleCoursesListViewController.h"

#import "MoodleController.h"

#import "PCCenterMessageCell.h"

#import "MoodleCourseSectionsViewController.h"

#import "MoodleSplashDetailViewController.h"

#import "MoodleService.h"

#import "PluginSplitViewController.h"

@interface MoodleCoursesListViewController ()<PCMasterSplitDelegate, MoodleServiceDelegate>

@property (nonatomic, strong) MoodleService* moodleService;
@property (nonatomic, strong) NSArray* courses;
@property (nonatomic, strong) LGRefreshControl* lgRefreshControl;

@end

static const NSTimeInterval kRefreshValiditySeconds = 259200.0; //3 days

static NSString* kMoodleCourseListCell = @"MoodleCourseListCell";

@implementation MoodleCoursesListViewController

- (id)init
{
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        self.title = NSLocalizedStringFromTable(@"MyCourses", @"MoodlePlugin", nil);
        self.moodleService = [MoodleService sharedInstanceToRetain];
        self.courses = [self.moodleService getFromCacheCourseListReply].iCourses;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.lgRefreshControl = [[LGRefreshControl alloc] initWithTableViewController:self refreshedDataIdentifier:[LGRefreshControl dataIdentifierForPluginName:@"moodle" dataName:@"coursesList"]];
    [self.lgRefreshControl setTarget:self selector:@selector(refresh)];
    self.tableView.rowHeight = 65.0;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[PCGAITracker sharedTracker] trackScreenWithName:@"/moodle"];
    if (!self.courses || [self.lgRefreshControl shouldRefreshDataForValidity:kRefreshValiditySeconds]) {
        [self refresh];
    }
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskAllButUpsideDown;
    
}

#pragma mark - refresh control

- (void)refresh {
    [self.moodleService cancelOperationsForDelegate:self]; //cancel before retrying
    [self.lgRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"LoadingCourseList", @"MoodlePlugin", nil)];
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
            [self.lgRefreshControl endRefreshing];
        } failureBlock:^{
            [self error];
        }];
    }
}

#pragma mark - PCMasterSplitDelegate /* used on iPad */

- (UIViewController*)detailViewControllerThatShouldBeDisplayed {
    MoodleSplashDetailViewController* detailViewController = [[MoodleSplashDetailViewController alloc] init];
    return [[PCNavigationController alloc] initWithRootViewController:detailViewController];
}

#pragma mark - MoodleServiceDelegate

- (void)getCoursesList:(MoodleRequest *)aMoodleRequest didReturn:(CoursesListReply *)coursesListReply {
    switch (coursesListReply.iStatus) {
        case 200:
            self.courses = coursesListReply.iCourses;
            [self.moodleService saveToCacheCourseListReply:coursesListReply];
            [self.tableView reloadData];
            [self.lgRefreshControl endRefreshingAndMarkSuccessful];
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
            [self.lgRefreshControl endRefreshing];
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
    [PCUtils showServerErrorAlert];
    [self.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"ServerErrorShort", @"PocketCampus", nil)];
}

- (void)serviceConnectionToServerTimedOut {
    [PCUtils showConnectionToServerTimedOutAlert];
    [self.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutShort", @"PocketCampus", nil)];
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (self.courses.count == 0) {
        return;
    }
    MoodleCourse* course = self.courses[indexPath.row];
    MoodleCourseSectionsViewController* viewController = [[MoodleCourseSectionsViewController alloc] initWithCourse:course];
    [self.navigationController pushViewController:viewController animated:YES];
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.courses && [self.courses count] == 0) {
        if (indexPath.row == 1) {
            return [[PCCenterMessageCell alloc] initWithMessage:NSLocalizedStringFromTable(@"MoodleNoCourse", @"MoodlePlugin", nil)];
        } else {
            UITableViewCell* cell =[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            return cell;
        }
    }
    
    MoodleCourse* course = self.courses[indexPath.row];
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kMoodleCourseListCell];
    
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kMoodleCourseListCell];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        cell.textLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleBody];
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
