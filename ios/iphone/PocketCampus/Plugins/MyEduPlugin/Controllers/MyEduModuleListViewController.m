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

@interface MyEduModuleListViewController ()

@property (nonatomic, strong) MyEduService* myEduService;
@property (nonatomic, strong) MyEduCourse* course;
@property (nonatomic, strong) MyEduSection* section;
@property (nonatomic, strong) NSArray* modules;
@property (nonatomic, strong) MyEduTequilaToken* tequilaToken;
@property (nonatomic, strong) PCRefreshControl* pcRefreshControl;
@property (nonatomic, strong) NSIndexPath* selectedModuleIndexPath;
@property (nonatomic, strong) NSArray* cells;

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

- (void)startGetSectionDetailsRequest {    
    VoidBlock successBlock = ^{
        [self.myEduService getSectionDetailsForRequest:[[MyEduSectionDetailsRequest alloc] initWithIMyEduRequest:[self.myEduService createMyEduRequest] iCourseCode:self.course.iCode iSectionId:self.section.iId] delegate:self];
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

- (void)initCellsWithModules {
    if (!self.modules) {
        return;
    }
    NSMutableArray* cellsTmp = [NSMutableArray arrayWithCapacity:[self.modules count]];
    
    [self.modules enumerateObjectsUsingBlock:^(MyEduModule* module, NSUInteger idx, BOOL *stop) {
        UITableViewCell* cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
        cell.selectionStyle = UITableViewCellSelectionStyleGray;
        cell.textLabel.font = [UIFont boldSystemFontOfSize:18.0];
        cell.textLabel.numberOfLines = 2;
        cell.textLabel.adjustsFontSizeToFitWidth = YES;
        cell.textLabel.text = [NSString stringWithFormat:@"%d. %@", module.iSequence, module.iTitle];
        cell.detailTextLabel.font = [UIFont systemFontOfSize:12.0];
        
        if ([MyEduService localPathOfVideoForModule:module nilIfNoFile:YES]) {
            UIImageView* downloadedImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"DownloadedRectSmall"]];
            cell.accessoryView = downloadedImageView;
        }
        
        [self.myEduService removeDownloadObserver:self forVideoModule:module];
        [self.myEduService addDownloadObserver:self forVideoOfModule:module startDownload:NO startBlock:^{
            cell.detailTextLabel.text = [NSString stringWithFormat:@"      %@", NSLocalizedStringFromTable(@"StartingDownload", @"MyEduPlugin", nil)];
            [cell setNeedsLayout];
        } finishBlock:^(NSURL *fileLocalURL) {
            UIImageView* downloadedImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"DownloadedRectSmall"]];
            cell.accessoryView = downloadedImageView;
            cell.detailTextLabel.text = nil;
        } progressBlock:^(unsigned long long nbBytesDownloaded, unsigned long long nbBytesToDownload, float ratio) {
            NSString* text = [NSString stringWithFormat:@"      %@ %d%%", NSLocalizedStringFromTable(@"DownloadingVideo", @"MyEduPlugin", nil), (int)(ratio*100)];
            cell.detailTextLabel.text = text;
            [cell setNeedsLayout];
        } cancelledBlock:^{
            cell.accessoryView = nil;
            cell.detailTextLabel.text = nil;
            [cell setNeedsLayout];
        } failureBlock:^(int statusCode) {
            cell.accessoryView = nil;
            cell.detailTextLabel.text = nil;
            [cell setNeedsLayout];
        } deletedBlock:^{
            cell.accessoryView = nil;
            cell.detailTextLabel.text = nil;
            [cell setNeedsLayout];
        }];
        
        [cellsTmp addObject:cell];
    }];
    self.cells = [cellsTmp copy];
}

#pragma mark - MyEduServiceDelegate

- (void)getSectionDetailsForRequest:(MyEduSectionDetailsRequest*)request didReturn:(MyEduSectionDetailsReply*)reply {
    [[MyEduController sharedInstance] removeLoginObserver:self];
    switch (reply.iStatus) {
        case 200:
            self.modules = reply.iMyEduModules;
            [self initCellsWithModules];
            [self.tableView reloadData];
            [self.pcRefreshControl endRefreshing];
            break;
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
    [[MyEduController sharedInstance] removeLoginObserver:self];
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

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSInteger selectedTabIndex = 0;
    if ([self.selectedModuleIndexPath isEqual:indexPath]) {
        return;
    }
    
    if (self.splitViewController.viewControllers.count > 0 && [self.splitViewController.viewControllers[1] isKindOfClass:[MyEduModuleDetailViewController class]]) {
        MyEduModuleDetailViewController* controller = (MyEduModuleDetailViewController*)(self.splitViewController.viewControllers[1]);
        selectedTabIndex = controller.tabBarController.selectedIndex;
    }
    
    MyEduModule* module = self.modules[indexPath.row];
    MyEduModuleDetailViewController* detailViewController = [[MyEduModuleDetailViewController alloc] initWithModule:module section:self.section course:self.course];
    self.selectedModuleIndexPath = indexPath;
    
    if (self.splitViewController) {
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], detailViewController];
        detailViewController.tabBarController.selectedIndex = selectedTabIndex;
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
            return [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
        }
    }
    
    UITableViewCell* cell = self.cells[indexPath.row];
    
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
    [self.myEduService removeDownloadObserver:self];
    [self.myEduService cancelOperationsForDelegate:self];
}

@end
