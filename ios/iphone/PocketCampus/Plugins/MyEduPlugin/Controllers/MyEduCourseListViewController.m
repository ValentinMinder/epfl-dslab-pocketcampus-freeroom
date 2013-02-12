//
//  MyEduCourseListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyEduCourseListViewController.h"

#import "ObjectArchiver.h"

#import "PCUtils.h"

#import "PCRefreshControl.h"

#import "PCCenterMessageCell.h"

#import "MyEduController.h"

#import "MyEduSectionListViewController.h"

#import "MyEduSplashDetailViewController.h"

#import "PushNotifController.h"

#import "GANTracker.h"

static const NSTimeInterval kRefreshValiditySeconds = 604800.0; //1 week

@interface MyEduCourseListViewController ()

@property (nonatomic, strong) MyEduService* myEduService;
@property (nonatomic, strong) NSArray* subscribedCourses;
@property (nonatomic, strong) MyEduTequilaToken* tequilaToken;
@property (nonatomic, strong) PCRefreshControl* pcRefreshControl;

@end

static NSString* kMyEduCourseListCell = @"MyEduCourseListCell";

@implementation MyEduCourseListViewController

- (id)init
{
    self = [super initWithNibName:@"MyEduCourseListView" bundle:nil];
    if (self) {
        // Custom initialization
        self.myEduService = [MyEduService sharedInstanceToRetain];
        self.subscribedCourses = [self.myEduService getFromCacheSubscribedCoursesListForRequest:[self.myEduService createMyEduRequest]].iSubscribedCourses;
        self.pcRefreshControl = [[PCRefreshControl alloc] initWithTableViewController:self pluginName:@"myedu"  refreshedDataIdentifier:@"myEduSubscribedCourseList"];
        [self.pcRefreshControl setTarget:self selector:@selector(refresh)];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/myedu" withError:NULL];
    /*UIView* backgroundView = [[UIView alloc] init];
    backgroundView.backgroundColor = [UIColor whiteColor];
    self.tableView.backgroundView = backgroundView;
    self.tableView.backgroundColor = [UIColor clearColor];*/
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    if (!self.subscribedCourses || [self.pcRefreshControl shouldRefreshDataForValidity:kRefreshValiditySeconds]) {
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
    [self.myEduService cancelOperationsForDelegate:self]; //cancel before retrying
    [self.pcRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"DownloadingCourseList", @"MyEduPlugin", nil)];
    [self startGetSubscribedCoursesListRequest];
}

- (void)startGetSubscribedCoursesListRequest {
    VoidBlock successBlock = ^{
         [self.myEduService getSubscribedCoursesListForRequest:[self.myEduService createMyEduRequest] delegate:self];
    };
    if ([self.myEduService lastSession]) {
        successBlock();
    } else {
        NSLog(@"-> No saved session, loggin in...");
        [[MyEduController sharedInstanceToRetain] addLoginObserver:self successBlock:successBlock userCancelledBlock:^{
            [self.pcRefreshControl endRefreshing];
        } failureBlock:^{
            [self error];
        }];
    }
}

#pragma mark - PCMasterSplitDelegate

- (UIViewController*)detailViewControllerThatShouldBeDisplayed {
    MyEduSplashDetailViewController* detailViewController = [[MyEduSplashDetailViewController alloc] init];
    return detailViewController;
}

#pragma mark - MyEduServiceDelegate

- (void)getSubscribedCoursesListForRequest:(MyEduRequest *)request didReturn:(MyEduSubscribedCoursesListReply *)reply {
    switch (reply.iStatus) {
        case 200:
            self.subscribedCourses = reply.iSubscribedCourses;
            [self.tableView reloadData];
            [self.pcRefreshControl endRefreshing];
            [self.pcRefreshControl markRefreshSuccessful];
            break;
        case 407:
            [self.myEduService deleteSession];
            [self startGetSubscribedCoursesListRequest];
            break;
        default:
            [self getSubscribedCoursesListFailedForRequest:request];
            break;
    }
}

- (void)getSubscribedCoursesListFailedForRequest:(MyEduRequest *)request {
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

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    MyEduCourse* course = self.subscribedCourses[indexPath.row];
    [self.navigationController pushViewController:[[MyEduSectionListViewController alloc] initWithMyEduCourse:course] animated:YES];
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.subscribedCourses && [self.subscribedCourses count] == 0) {
        if (indexPath.row == 1) {
            return [[PCCenterMessageCell alloc] initWithMessage:NSLocalizedStringFromTable(@"NotSubscribedToAnyCourse", @"MyEduPlugin", nil)];
        } else {
            return [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        }
    }
    
    MyEduCourse* course = self.subscribedCourses[indexPath.row];
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kMyEduCourseListCell];
    
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kMyEduCourseListCell];
        cell.selectionStyle = UITableViewCellSelectionStyleGray;
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        cell.textLabel.font = [UIFont boldSystemFontOfSize:18.0];
        cell.textLabel.numberOfLines = 2;
        cell.textLabel.adjustsFontSizeToFitWidth = YES;
    }
    
    cell.textLabel.text = course.iTitle;
    //cell.detailTextLabel.text = course.iDescription;
    
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    if ([self.subscribedCourses count] == 0) {
        return 2; //first empty cell, second cell says no content
    }
    return [self.subscribedCourses count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    if (!self.subscribedCourses) {
        return 0;
    }
    return 1;
}

- (void)dealloc
{
    [self.myEduService cancelOperationsForDelegate:self];
}


@end
