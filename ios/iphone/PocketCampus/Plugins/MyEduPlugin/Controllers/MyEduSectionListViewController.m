//
//  MyEduSectionListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 31.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyEduSectionListViewController.h"

#import "ObjectArchiver.h"

#import "PCUtils.h"

#import "PCRefreshControl.h"

#import "PCCenterMessageCell.h"

#import "MyEduController.h"

#import "MyEduModuleListViewController.h"

#import "MyEduCourseInfoViewController.h"

@interface MyEduSectionListViewController ()

@property (nonatomic, strong) MyEduService* myEduService;
@property (nonatomic, strong) MyEduCourse* course;
@property (nonatomic, strong) NSArray* sections;
@property (nonatomic, strong) MyEduTequilaToken* tequilaToken;
@property (nonatomic, strong) PCRefreshControl* pcRefreshControl;

@end

static NSString* kMyEduSectionListCell = @"MyEduSectionListCell";

@implementation MyEduSectionListViewController

- (id)initWithMyEduCourse:(MyEduCourse*)course
{
    self = [super initWithNibName:@"MyEduSectionListView" bundle:nil];
    if (self) {
        // Custom initialization
        self.course = course;
        self.title = NSLocalizedStringFromTable(@"Sections", @"MyEduPlugin", nil);
        self.myEduService = [MyEduService sharedInstanceToRetain];
        self.sections = [self.myEduService getFromCacheCourseDetailsForRequest:[[MyEduCourseDetailsRequest alloc] initWithIMyEduRequest:[self.myEduService createMyEduRequest] iCourseCode:self.course.iCode]].iMyEduSections;
        self.pcRefreshControl = [[PCRefreshControl alloc] initWithTableViewController:self];
        [self.pcRefreshControl setTarget:self selector:@selector(refresh)];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    /*UIView* backgroundView = [[UIView alloc] init];
     backgroundView.backgroundColor = [UIColor whiteColor];
     self.tableView.backgroundView = backgroundView;
     self.tableView.backgroundColor = [UIColor clearColor];*/
}

- (void)viewDidAppear:(BOOL)animated {
    if (!self.sections) {
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
    [self.myEduService cancelOperationsForDelegate:self]; //cancel before retrying
    [self.pcRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"DownloadingSectionList", @"MyEduPlugin", nil)];
    [self startGetCourseDetailsRequest];
}

- (void)startGetCourseDetailsRequest {    
    VoidBlock successBlock = ^{
        [self.myEduService getCourseDetailsForRequest:[[MyEduCourseDetailsRequest alloc] initWithIMyEduRequest:[self.myEduService createMyEduRequest] iCourseCode:self.course.iCode] delegate:self];
    };
    if ([self.myEduService lastSession]) {
        successBlock();
    } else {
        NSLog(@"-> No saved session, loggin in...");
        [[MyEduController sharedInstance] addLoginObserver:self operationIdentifier:nil successBlock:successBlock userCancelledBlock:^{
            [self.pcRefreshControl endRefreshing];
        } failureBlock:^{
            [self error];
        }];
    }
}

#pragma mark - PCMasterSplitDelegate

- (UIViewController*)detailViewControllerThatShouldBeDisplayed {
    return [[MyEduCourseInfoViewController alloc] initWithCourse:self.course];
}

#pragma mark - MyEduServiceDelegate

- (void)getCourseDetailsForRequest:(MyEduCourseDetailsRequest*)request didReturn:(MyEduCourseDetailsReply*)reply {
    [[MyEduController sharedInstance] removeLoginObserver:self];
    switch (reply.iStatus) {
        case 200:
            self.sections = reply.iMyEduSections;
            [self.tableView reloadData];
            [self.pcRefreshControl endRefreshing];
            break;
        case 407:
            [self.myEduService deleteSession];
            [self startGetCourseDetailsRequest];
            break;
        default:
            [self getCourseDetailsFailedForRequest:request];
            break;
    }
}

- (void)getCourseDetailsFailedForRequest:(MyEduCourseDetailsRequest *)request {
    [[MyEduController sharedInstance] removeLoginObserver:self];
    [self error];
}


- (void)error {
    self.pcRefreshControl.type = RefreshControlTypeProblem;
    self.pcRefreshControl.message = NSLocalizedStringFromTable(@"ConnectionToServerErrorShort", @"PocketCampus", nil);
    if (!self.sections) {
        [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
    }
    [self.pcRefreshControl hideInTimeInterval:2.0];
}

- (void)serviceConnectionToServerTimedOut {
    self.pcRefreshControl.type = RefreshControlTypeProblem;
    self.pcRefreshControl.message = NSLocalizedStringFromTable(@"ConnectionToServerTimedOutShort", @"PocketCampus", nil);
    if (!self.sections) {
        [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
    }
    [self.pcRefreshControl hideInTimeInterval:2.0];
}


#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    MyEduSection* section = self.sections[indexPath.row];
    [self.navigationController pushViewController:[[MyEduModuleListViewController alloc] initWithMyEduCourse:self.course andSection:section] animated:YES];
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.sections && [self.sections count] == 0) {
        if (indexPath.row == 2) {
            return [[PCCenterMessageCell alloc] initWithMessage:NSLocalizedStringFromTable(@"NoSection", @"MyEduPlugin", nil)];
        } else {
            return [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        }
    }
    
    MyEduSection* section = self.sections[indexPath.row];
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kMyEduSectionListCell];
    
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:kMyEduSectionListCell];
        cell.selectionStyle = UITableViewCellSelectionStyleGray;
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        cell.detailTextLabel.numberOfLines = 3;
    }
    
    cell.textLabel.text = [NSString stringWithFormat:@"%d. %@", section.iSequence, section.iTitle];
    cell.detailTextLabel.text = section.iDescription;
    
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    if ([self.sections count] == 0) {
        return 3; //first empty cell, second cell says no content
    }
    return [self.sections count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    if (!self.sections) {
        return 0;
    }
    return 1;
}

- (void)dealloc
{
    [self.myEduService cancelOperationsForDelegate:self];
}


@end
