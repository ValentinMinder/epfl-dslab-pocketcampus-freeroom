//
//  CourseSectionsViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "CourseSectionsViewController.h"

#import "PCRefreshControl.h"

#import "MoodleController.h"

#import "PCUtils.h"

#import "PCValues.h"

#import "PCTableViewSectionHeader.h"

#import "MoodleResourceViewController.h"

#import "MoodleSplashDetailViewController.h"

#import "GANTracker.h"

@interface CourseSectionsViewController ()

@property (nonatomic, strong) MoodleService* moodleService;
@property (nonatomic, strong) NSArray* sections;
@property (nonatomic) int currentWeek;
@property (nonatomic, strong) MoodleCourse* course;
@property (nonatomic, strong) PCRefreshControl* pcRefreshControl;
@property (nonatomic, strong) MoodleResource* selectedResource;

@end

static NSString* kMoodleCourseSectionElementCell = @"MoodleCourseSectionElementCell";

@implementation CourseSectionsViewController

- (id)initWithCourse:(MoodleCourse*)course;
{
    self = [super initWithNibName:@"CourseSectionsView" bundle:nil];
    if (self) {
        self.course = course;
        self.title = self.course.iTitle;
        self.moodleService = [MoodleService sharedInstanceToRetain];
        self.sections = [self.moodleService getFromCacheSectionsListReplyForCourse:self.course].iSections;
        self.pcRefreshControl = [[PCRefreshControl alloc] initWithTableViewController:self refreshedDataIdentifier:[NSString stringWithFormat:@"courseSectionsList-%d", self.course.iId]];
        [self.pcRefreshControl setTarget:self selector:@selector(refresh)];
        self.clearsSelectionOnViewWillAppear = NO; //managed manually to know which row was selected and reload it to update "saved" label
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/moodle/course" withError:NULL];
    [self showToggleButtonIfPossible];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.selectedResource = nil;
    if (!self.sections) {
        [self refresh];
    }
    NSIndexPath* selectedIndexPath = [self.tableView indexPathForSelectedRow];
    if (selectedIndexPath) {
        [self.tableView deselectRowAtIndexPath:selectedIndexPath animated:animated];
        [self.tableView reloadRowsAtIndexPaths:[NSArray arrayWithObject:selectedIndexPath] withRowAnimation:UITableViewRowAnimationFade];
    }
}

/*- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [self.tableView scrollToRowAtIndexPath:[self.tableView indexPathsForVisibleRows][0] atScrollPosition:UITableViewScrollPositionTop animated:YES];
}*/

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - refresh control

- (void)refresh {
    [self.moodleService cancelOperationsForDelegate:self]; //cancel before retrying
    [self.pcRefreshControl startRefreshingWithMessage:NSLocalizedStringFromTable(@"LoadingCourse", @"MoodlePlugin", nil)];
    [self startGetCourseSectionsRequest];
}

- (void)startGetCourseSectionsRequest {
    VoidBlock successBlock = ^{
        [self.moodleService getCourseSections:[self.moodleService createMoodleRequestWithCourseId:self.course.iId] withDelegate:self];
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


#pragma mark - Utils and toggle week button

- (void)computeCurrentWeek {
    if(self.sections == nil)
        return;
    self.currentWeek = -1; //-1 means outside semester time, all weeks will be displayed and toggle button hidden
    for (NSInteger i = 0; i < self.sections.count; i++) {
        MoodleSection* iSection = [self.sections objectAtIndex:i];
        if(iSection.iResources.count != 0 && iSection.iCurrent) {
            self.currentWeek = i;
            break;
        }
    }
}

- (void)showToggleButtonIfPossible {
    int visibleCount = 0;
    for (int i = 1; i < self.sections.count; i++) {
        MoodleSection* secObj = [self.sections objectAtIndex:i];
        visibleCount += secObj.iResources.count;
    }
    if(visibleCount > 0) {
        [self computeCurrentWeek];
        [self showToggleButton];
    }
}

- (void)showToggleButton {
    UIBarButtonItem *anotherButton = nil;
    if (self.currentWeek > 0) {
        anotherButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"MoodleAllWeeks", @"MoodlePlugin", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(toggleShowAll:)];
    } else if (self.currentWeek == 0) {
        anotherButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"MoodleCurrentWeek", @"MoodlePlugin", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(toggleShowAll:)];
    }
    [self.navigationItem setRightBarButtonItem:anotherButton animated:NO];
}

- (void)toggleShowAll:(id)sender {
    if (self.currentWeek > 0) {
        self.currentWeek = 0;
    } else {
        [self computeCurrentWeek];
    }
    [self showToggleButton];
    [self.tableView reloadData];
}

#pragma MoodleServiceDelegate

- (void)getCourseSections:(MoodleRequest *)aMoodleRequest didReturn:(SectionsListReply *)sectionsListReply {
    [[MoodleController sharedInstance] removeLoginObserver:self];
    switch (sectionsListReply.iStatus) {
        case 200:
            self.sections = sectionsListReply.iSections;
            [self.moodleService saveToCacheSectionsListReply:sectionsListReply forCourse:self.course];
            [self showToggleButtonIfPossible];
            [self.tableView reloadData];
            if (self.selectedResource) {
                BOOL found __block = NO;
                [self.sections enumerateObjectsUsingBlock:^(MoodleSection* section, NSUInteger sectionI, BOOL *stop) {
                    [section.iResources enumerateObjectsUsingBlock:^(MoodleResource* resource, NSUInteger rowI, BOOL *stop) {
                        if ([resource.iUrl isEqualToString:self.selectedResource.iUrl]) { //considered same, isEqual not implemented by thrift
                            [self.tableView selectRowAtIndexPath:[NSIndexPath indexPathForRow:rowI inSection:sectionI] animated:NO scrollPosition:UITableViewScrollPositionNone];
                            *stop = YES;
                            found = YES;
                        }
                    }];
                }];
                if (!found) { //means resource has been removed from Moodle
                    if (self.splitViewController) { /* iPad */
                        MoodleSplashDetailViewController* splashViewController = [[MoodleSplashDetailViewController alloc] init];
                        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], splashViewController];
                    } else { /* iPhone */
                        [self.navigationController popViewControllerAnimated:YES];
                    }
                }
            }
            [self.pcRefreshControl endRefreshing];
            [self.pcRefreshControl markRefreshSuccessful];
            break;
        case 407:
            [self.moodleService deleteSession];
            [self startGetCourseSectionsRequest];
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
            [self getCourseSectionsFailed:aMoodleRequest];
            break;
    }
}

- (void)getCourseSectionsFailed:(MoodleRequest *)aMoodleRequest {
    [[MoodleController sharedInstance] removeLoginObserver:self];
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

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    MoodleSection* section = [self.sections objectAtIndex:indexPath.section];
    MoodleResource* resource = [section.iResources objectAtIndex:indexPath.row];
    if (self.selectedResource == resource) {
        return;
    }
    VoidBlock deletedBlock = ^{
        self.selectedResource = nil;
        [self.tableView reloadRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
        if (self.splitViewController) { /* iPad */
            MoodleSplashDetailViewController* splashViewController = [[MoodleSplashDetailViewController alloc] init];
            self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], splashViewController];
        } else { /* iPhone */
            [self.navigationController popViewControllerAnimated:YES];
        }
    };
    
    MoodleResourceViewController* detailViewController = [[MoodleResourceViewController alloc] initWithMoodleResource:resource downloadedBlock:^{
        [self.tableView reloadRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
        [self.tableView selectRowAtIndexPath:indexPath animated:NO scrollPosition:UITableViewScrollPositionNone];
    } deletedBlock:deletedBlock];
    
    
    
    if (self.splitViewController) { /* iPad */
        UINavigationController* detailNavController = [[UINavigationController alloc] initWithRootViewController:detailViewController]; //to have nav bar
        self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], detailNavController];
    } else { /* iPhone */
        [self.navigationController pushViewController:detailViewController animated:YES];
    }
    
    self.selectedResource = resource;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (self.sections == nil) {
        return 0.0;
    }
    if (![self showSection:section]) {
        return 0.0;
    }
    MoodleSection* secObj = [self.sections objectAtIndex:section];
    if (secObj.iResources.count == 0) {
        return 0.0;
    }
    return [PCValues tableViewSectionHeaderHeight];
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    if (self.sections == nil || ![self showSection:section]) {
        return nil;
    }

    MoodleSection* secObj = [self.sections objectAtIndex:section];
    if (secObj.iResources.count == 0) {
        return nil;
    }
    
    /*NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
     [dateFormatter setTimeZone:[NSTimeZone systemTimeZone]];
     [dateFormatter setLocale:[NSLocale systemLocale]];
     [dateFormatter setDateFormat:@"dd/MM"];
     //NSLog(@"%lld", secObj.iStartDate);
     NSString* startDate = [dateFormatter stringFromDate:[NSDate dateWithTimeIntervalSince1970:secObj.iStartDate]];*/
    
    /* startDate and endDate are not filled by server yet */
    
    NSString* title = [NSString stringWithFormat:@"%@ %d", NSLocalizedStringFromTable(@"MoodleWeek", @"MoodlePlugin", nil), section];
    
    return [[PCTableViewSectionHeader alloc] initWithSectionTitle:title tableView:tableView];    
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 44.0;
}

/*- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    NSLog(@"%@", indexPath);
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        MoodleSection* section = [self.sections objectAtIndex:indexPath.section];
        MoodleResource* resource = [section.iResources objectAtIndex:indexPath.row];
        if (![self.moodleService deleteDownloadedMoodleResource:resource]) {
            UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ImpossibleDeleteFile", @"MoodlePlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [errorAlert show];
            return;
        }
        [[GANTracker sharedTracker] trackPageview:@"/v3r1/moodle/course/document/delete" withError:NULL];
        
        if (self.selectedResource == resource) {
            //[self.tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade]; //hack to remove downloaded mention. Cannot refresh cell from here, would result in double cell selected bug.
            self.selectedResource = nil;
            self.selectedResourceIndexPath = nil;
        }
        
    if (self.splitViewController) { // iPad
            MoodleSplashDetailViewController* splashViewController = [[MoodleSplashDetailViewController alloc] init];
            self.splitViewController.viewControllers = @[self.splitViewController.viewControllers[0], splashViewController];
        }
    }
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView_ editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    MoodleSection* section = [self.sections objectAtIndex:indexPath.section];
    MoodleResource* resource = [section.iResources objectAtIndex:indexPath.row];
    if ([self.moodleService isMoodleResourceDownloaded:resource]) {
        return UITableViewCellEditingStyleDelete;
    }
    return UITableViewCellEditingStyleNone;
}

- (void)tableView:(UITableView *)tableView didEndEditingRowAtIndexPath:(NSIndexPath *)indexPath {
    if (!indexPath) { //API bug here. If delete commit is done and row actually not removed, this method will be called with indexPath nil
                      //return;
    }
    if (self.selectedResourceIndexPath) {
        [tableView reloadRowsAtIndexPaths:[NSArray arrayWithObject:self.selectedResourceIndexPath] withRowAnimation:UITableViewRowAnimationFade];
        [self.tableView selectRowAtIndexPath:self.selectedResourceIndexPath animated:NO scrollPosition:UITableViewRowAnimationNone];
    } else {
        if (self.tableView.indexPathForSelectedRow) {
            [self.tableView deselectRowAtIndexPath:self.tableView.indexPathForSelectedRow animated:YES];
        }
    }
}*/

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    MoodleSection* section = [self.sections objectAtIndex:indexPath.section];
    MoodleResource* resource = [section.iResources objectAtIndex:indexPath.row];
    if (self.selectedResource == resource) {
        [cell setSelected:YES];
    }
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    MoodleSection* section = [self.sections objectAtIndex:indexPath.section];
    UITableViewCell* newCell = [self.tableView dequeueReusableCellWithIdentifier:kMoodleCourseSectionElementCell];
    if (!newCell) {
        newCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:kMoodleCourseSectionElementCell];
        newCell.selectionStyle = UITableViewCellSelectionStyleGray;
        newCell.textLabel.font = [UIFont boldSystemFontOfSize:14.0];
        newCell.textLabel.adjustsFontSizeToFitWidth = YES;
        newCell.textLabel.minimumFontSize = 11.0;
        UILabel* savedLabel = [[UILabel alloc] initWithFrame:CGRectNull];
        savedLabel.text = NSLocalizedStringFromTable(@"Saved", @"MoodlePlugin", nil);
        savedLabel.font = [UIFont systemFontOfSize:13.0];
        savedLabel.textAlignment = UITextAlignmentRight;
        savedLabel.adjustsFontSizeToFitWidth = YES;
        savedLabel.textColor = [UIColor colorWithWhite:0.6 alpha:1.0];
        newCell.accessoryView = savedLabel;
    }
    MoodleResource* resource = [section.iResources objectAtIndex:indexPath.row];
    newCell.textLabel.text = resource.iName;
    NSArray* pathComponents = [resource.iUrl pathComponents];
    newCell.detailTextLabel.text = [pathComponents objectAtIndex:pathComponents.count-1];
    if ([self.moodleService isMoodleResourceDownloaded:resource]) {
        [newCell.accessoryView sizeToFit];
        /*if (![newCell.contentView viewWithTag:kDownloadedCornerImageViewTag]) { //if image view is already present, do not add it a second time
         UIImage* image = [UIImage imageNamed:@"DownloadedCorner"];
         UIImageView* imageView = [[UIImageView alloc] initWithImage:image];
         //imageView.alpha = 0.7;
         imageView.frame = CGRectMake(tableView_.frame.size.width-image.size.width, -1.0, image.size.width, image.size.height);
         imageView.tag = kDownloadedCornerImageViewTag;
         [newCell.contentView addSubview:imageView];
         [imageView release];
         }*/
    } else {
        newCell.accessoryView.frame = CGRectNull;
        //[[newCell.contentView viewWithTag:kDownloadedCornerImageViewTag] removeFromSuperview];
    }
    return newCell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (self.sections == nil) {
        return 0;
    }
    if(![self showSection:section]) {
        return 0;
    }
    MoodleSection* secObj = [self.sections objectAtIndex:section];
    return secObj.iResources.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (self.sections == nil) {
        return 0;
    }
    return self.sections.count;
}

#pragma mark - showSections

- (BOOL)showSection:(NSInteger) section {
    if (section == 0) {
        return NO;
    }
    if (self.currentWeek <= 0) {
        return YES;
    }
    return (self.currentWeek == section);
}

#pragma mark - dealloc

- (void)dealloc
{
    [self.moodleService cancelOperationsForDelegate:self];
}

@end
