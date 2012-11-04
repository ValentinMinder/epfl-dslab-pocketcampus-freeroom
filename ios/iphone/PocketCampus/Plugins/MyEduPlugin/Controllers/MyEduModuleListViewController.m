//
//  MyEduModuleListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 02.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyEduModuleListViewController.h"

#import "ObjectArchiver.h"

#import "PCUtils.h"

#import "PCRefreshControl.h"

#import "PCCenterMessageCell.h"

#import "MyEduModuleDetailViewController.h"

@interface MyEduModuleListViewController ()

@property (nonatomic, strong) MyEduService* myEduService;
@property (nonatomic, strong) MyEduCourse* course;
@property (nonatomic, strong) MyEduSection* section;
@property (nonatomic, strong) NSArray* modules;
@property (nonatomic, strong) AuthenticationController* authController;
@property (nonatomic, strong) MyEduTequilaToken* tequilaToken;
@property (nonatomic, strong) PCRefreshControl* pcRefreshControl;

@end

static NSString* kMyEduModuleListCell = @"MyEduModuleListCell";

@implementation MyEduModuleListViewController

- (id)initWithMyEduCourse:(MyEduCourse*)course andSection:(MyEduSection*)section
{
    self = [super initWithNibName:@"MyEduModuleListView" bundle:nil];
    if (self) {
        // Custom initialization
        self.course = course;
        self.section = section;
        self.title = NSLocalizedStringFromTable(@"Modules", @"MyEduPlugin", nil);
        self.myEduService = [MyEduService sharedInstanceToRetain];
        self.authController = [[AuthenticationController alloc] init];
        self.modules = [self.myEduService getFromCacheSectionDetailsForRequest:[[MyEduSectionDetailsRequest alloc] initWithICourseCode:self.course.iCode iSectionId:self.section.iId] myeduRequest:[self.myEduService createMyEduRequest]].iMyEduModules;
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
    self.pcRefreshControl = [[PCRefreshControl alloc] initWithTableViewController:self];
    [self.pcRefreshControl setTarget:self selector:@selector(refresh)];
}

- (void)viewDidAppear:(BOOL)animated {
    if (!self.modules) {
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
    [self.pcRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"DownloadingModuleList", @"MyEduPlugin", nil)];
    [self startGetSectionDetailsRequest];
}

- (void)login {
    [self.myEduService getTequilaTokenForMyEduWithDelegate:self];
}

- (void)startGetSectionDetailsRequest {
    if ([self.myEduService lastSession]) {
        [self.myEduService getSectionDetailsForRequest:[[MyEduSectionDetailsRequest alloc] initWithICourseCode:self.course.iCode iSectionId:self.section.iId] myeduRequest:[self.myEduService createMyEduRequest] delegate:self];
    } else {
        [self login];
    }
}

#pragma mark - MyEduServiceDelegate

- (void)getSectionDetailsForRequest:(MyEduSectionDetailsRequest*)request myeduRequest:(MyEduRequest*)myeduRequest didReturn:(MyEduSectionDetailsReply*)reply {
    switch (reply.iStatus) {
        case 200:
            self.modules = reply.iMyEduModules;
            [self.tableView reloadData];
            [self.pcRefreshControl endRefreshing];
            break;
        case 407:
            [self login];
            break;
        default:
            [self getSectionDetailsFailedForRequest:request myeduRequest:myeduRequest];
            break;
    }
}

- (void)getSectionDetailsFailedForRequest:(MyEduSectionDetailsRequest *)request myeduRequest:(MyEduRequest*)myeduRequest {
    [self error];
}

- (void)getTequilaTokenForMyEduDidReturn:(MyEduTequilaToken *)tequilaToken {
    self.tequilaToken = tequilaToken;
    if (self.splitViewController) {
        [self.authController authToken:tequilaToken.iTequilaKey presentationViewController:self.splitViewController delegate:self];
    } else {
        [self.authController authToken:tequilaToken.iTequilaKey presentationViewController:self delegate:self];
    }
}

- (void)getTequilaTokenForMyEduFailed {
    [self error];
}

- (void)getMyEduSessionForTequilaToken:(MyEduTequilaToken *)tequilaToken didReturn:(MyEduSession *)myEduSession {
    [self.myEduService saveSession:myEduSession];
    [self startGetSectionDetailsRequest];
}

- (void)getMyEduSessionFailedForTequilaToken:(MyEduTequilaToken *)tequilaToken {
    [self error];
}

- (void)error {
    self.pcRefreshControl.type = RefreshControlTypeProblem;
    self.pcRefreshControl.message = NSLocalizedStringFromTable(@"ConnectionToServerErrorShort", @"PocketCampus", nil);
    if (!self.modules) {
        [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
    }
    [self.pcRefreshControl hideInTimeInterval:2.0];
}

- (void)serviceConnectionToServerTimedOut {
    self.pcRefreshControl.type = RefreshControlTypeProblem;
    self.pcRefreshControl.message = NSLocalizedStringFromTable(@"ConnectionToServerTimedOutShort", @"PocketCampus", nil);
    if (!self.modules) {
        [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
    }
    [self.pcRefreshControl hideInTimeInterval:2.0];
}

#pragma mark - AuthenticationCallbackDelegate

- (void)authenticationSucceeded {
    if (!self.tequilaToken) {
        NSLog(@"-> ERROR : no tequilaToken saved after successful authentication");
        return;
    }
    [self.myEduService getMyEduSessionForTequilaToken:self.tequilaToken delegate:self];
}

- (void)userCancelledAuthentication {
    //TODO
}

- (void)invalidToken {
    [self startGetSectionDetailsRequest];
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    MyEduModule* module = self.modules[indexPath.row];
    MyEduModuleDetailViewController* detailViewController = [[MyEduModuleDetailViewController alloc] initWithModule:module section:self.section course:self.course];
    if (self.splitViewController) {
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], detailViewController];
    } else {
        //TODO push on nav controller (iPhone)
    }
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.modules && [self.modules count] == 0) {
        if (indexPath.row == 2) {
            return [[PCCenterMessageCell alloc] initWithMessage:NSLocalizedStringFromTable(@"NoModule", @"MyEduPlugin", nil)];
        } else {
            return [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        }
    }
    
    MyEduModule* module = self.modules[indexPath.row];
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kMyEduModuleListCell];
    
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kMyEduModuleListCell];
        cell.selectionStyle = UITableViewCellSelectionStyleGray;
    }
    
    cell.textLabel.text = [NSString stringWithFormat:@"%d. %@", module.iSequence, module.iTitle];
    
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    if ([self.modules count] == 0) {
        return 3; //first empty cell, second cell says no content
    }
    return [self.modules count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    if (!self.modules) {
        return 0;
    }
    return 1;
}

- (void)dealloc
{
    [self.myEduService cancelOperationsForDelegate:self];
}

@end
