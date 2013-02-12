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

#import "MyEduController.h"

#import "MyEduModuleDetailViewController.h"

#import "PCTableViewCellAdditions.h"

#import "GANTracker.h"

static const NSTimeInterval kRefreshValiditySeconds = 86400.0; //1 day

@interface MyEduModuleListViewController ()

@property (nonatomic, strong) MyEduService* myEduService;
@property (nonatomic, strong) MyEduCourse* course;
@property (nonatomic, strong) MyEduSection* section;
@property (nonatomic, strong) NSArray* modules;
@property (nonatomic, strong) MyEduTequilaToken* tequilaToken;
@property (nonatomic, strong) PCRefreshControl* pcRefreshControl;
@property (nonatomic, strong) MyEduModule* selectedMyEduModule;
@property (nonatomic, strong) NSDictionary* cellForMyEduModule;

@end

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
        self.modules = [self.myEduService getFromCacheSectionDetailsForRequest:[[MyEduSectionDetailsRequest alloc] initWithIMyEduRequest:[self.myEduService createMyEduRequest] iCourseCode:self.course.iCode iSectionId:self.section.iId]].iMyEduModules;
        if (self.modules) {
            [self initCellsWithModules];
        }
        self.pcRefreshControl = [[PCRefreshControl alloc] initWithTableViewController:self pluginName:@"myedu" refreshedDataIdentifier:[NSString stringWithFormat:@"myEduSectionList-%d-%d", self.course.iId, self.section.iId]];
        [self.pcRefreshControl setTarget:self selector:@selector(refresh)];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/myedu/sections/modules" withError:NULL];
    /*UIView* backgroundView = [[UIView alloc] init];
     backgroundView.backgroundColor = [UIColor whiteColor];
     self.tableView.backgroundView = backgroundView;
     self.tableView.backgroundColor = [UIColor clearColor];*/
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    if (!self.modules || [self.pcRefreshControl shouldRefreshDataForValidity:kRefreshValiditySeconds]) {
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
    [self.pcRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"DownloadingModuleList", @"MyEduPlugin", nil)];
    [self startGetSectionDetailsRequest];
}

- (void)startGetSectionDetailsRequest {    
    VoidBlock successBlock = ^{
        [self.myEduService getSectionDetailsForRequest:[[MyEduSectionDetailsRequest alloc] initWithIMyEduRequest:[self.myEduService createMyEduRequest] iCourseCode:self.course.iCode iSectionId:self.section.iId] delegate:self];
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

- (void)initCellsWithModules {
    if (!self.modules) {
        return;
    }
    NSMutableDictionary* cellsTmp = [NSMutableDictionary dictionaryWithCapacity:self.modules.count];
    
    [self.modules enumerateObjectsUsingBlock:^(MyEduModule* module, NSUInteger idx, BOOL *stop) {
        PCTableViewCellAdditions* cell = [[PCTableViewCellAdditions alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
        cell.selectionStyle = UITableViewCellSelectionStyleGray;
        cell.textLabel.font = [UIFont boldSystemFontOfSize:18.0];
        cell.textLabel.numberOfLines = 2;
        cell.textLabel.adjustsFontSizeToFitWidth = YES;
        cell.textLabel.text = [NSString stringWithFormat:@"%d. %@", module.iSequence, module.iTitle];
        cell.detailTextLabel.font = [UIFont systemFontOfSize:12.0];
        
        if ([MyEduService localPathOfVideoForModule:module nilIfNoFile:YES]) {
            cell.downloadedIndicationVisible = YES;
        }
        
        if ([self.selectedMyEduModule isEqual:module]) {
            cell.durablySelected = YES;
        }
        
        [self.myEduService removeDownloadObserver:self forVideoModule:module];
        [self.myEduService addDownloadObserver:self forVideoOfModule:module startDownload:NO startBlock:^{
            cell.detailTextLabel.text = [NSString stringWithFormat:@"      %@", NSLocalizedStringFromTable(@"StartingDownload", @"MyEduPlugin", nil)];
            [cell setNeedsLayout];
        } finishBlock:^(NSURL *fileLocalURL) {
            [cell setDownloadedIndicationVisible:YES];
            cell.detailTextLabel.text = nil;
        } progressBlock:^(unsigned long long nbBytesDownloaded, unsigned long long nbBytesToDownload, float ratio) {
            NSString* text = [NSString stringWithFormat:@"      %@ %d%%", NSLocalizedStringFromTable(@"DownloadingVideo", @"MyEduPlugin", nil), (int)(ratio*100)];
            cell.detailTextLabel.text = text;
            [cell setNeedsLayout];
        } cancelledBlock:^{
            [cell setDownloadedIndicationVisible:NO];
            cell.detailTextLabel.text = nil;
            [cell setNeedsLayout];
        } failureBlock:^(int statusCode) {
           [cell setDownloadedIndicationVisible:NO];
            cell.detailTextLabel.text = nil;
            [cell setNeedsLayout];
        } deletedBlock:^{
            [cell setDownloadedIndicationVisible:NO];
            cell.detailTextLabel.text = nil;
            [cell setNeedsLayout];
        }];
        
        cellsTmp[(id<NSCopying>)module] = cell;
    }];
    self.cellForMyEduModule = [cellsTmp copy];
}

#pragma mark - MyEduServiceDelegate

- (void)getSectionDetailsForRequest:(MyEduSectionDetailsRequest*)request didReturn:(MyEduSectionDetailsReply*)reply {
    switch (reply.iStatus) {
        case 200:
        {
            NSMutableArray* modulesWithoutHidden = [reply.iMyEduModules mutableCopy];
            for (MyEduModule* module in reply.iMyEduModules) {
                if (!module.iVisible) {
                    [modulesWithoutHidden removeObject:module];
                }
            }
            self.modules = [modulesWithoutHidden copy]; //non-mutable
            [self initCellsWithModules];
            [self.tableView reloadData];
            [self.pcRefreshControl endRefreshing];
            [self.pcRefreshControl markRefreshSuccessful];
            break;
        }
        case 407:
            [self.myEduService deleteSession];
            [self startGetSectionDetailsRequest];
            break;
        default:
            [self getSectionDetailsFailedForRequest:request];
            break;
    }
}

- (void)getSectionDetailsFailedForRequest:(MyEduSectionDetailsRequest *)request {
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
    NSInteger selectedTabIndex = 0;
    
    MyEduModule* module = self.modules[indexPath.row];
    
    if (self.splitViewController && [self.selectedMyEduModule isEqual:module]) {
        return;
    }
    
    if (self.splitViewController.viewControllers.count > 0 && [self.splitViewController.viewControllers[1] isKindOfClass:[MyEduModuleDetailViewController class]]) {
        MyEduModuleDetailViewController* controller = (MyEduModuleDetailViewController*)(self.splitViewController.viewControllers[1]);
        selectedTabIndex = controller.tabBarController.selectedIndex;
    }
    
    MyEduModuleDetailViewController* detailViewController = [[MyEduModuleDetailViewController alloc] initWithModule:module section:self.section course:self.course];
    
    if (self.splitViewController) { //iPad
        PCTableViewCellAdditions* prevCell = self.cellForMyEduModule[self.selectedMyEduModule];
        prevCell.durablySelected = NO;
        self.selectedMyEduModule = module;
        
        PCTableViewCellAdditions* newCell = self.cellForMyEduModule[module];
        newCell.durablySelected = YES;
        
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], detailViewController];
        detailViewController.tabBarController.selectedIndex = selectedTabIndex;
    } else { //iPhone
        [self.navigationController pushViewController:detailViewController animated:YES];
    }
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.modules && [self.modules count] == 0) {
        if (indexPath.row == 1) {
            return [[PCCenterMessageCell alloc] initWithMessage:NSLocalizedStringFromTable(@"NoModule", @"MyEduPlugin", nil)];
        } else {
            return [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil]; //two empty cells first
        }
    }
    
    MyEduModule* module = self.modules[indexPath.row];
    
    return self.cellForMyEduModule[module];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    if ([self.modules count] == 0) {
        return 2; //first empty cell, second cell says no content
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
    [self.myEduService removeDownloadObserver:self];
    [self.myEduService cancelOperationsForDelegate:self];
}

@end
