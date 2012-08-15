//
//  CourseSectionsViewController.m
//  PocketCampus
//
//  Created by Amer C on 5/3/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "CourseSectionsViewController.h"

#import "AuthenticationController.h"

#import "PCValues.h"

#import "PCUtils.h"

#import "PCTableViewSectionHeader.h"

#import "DocumentViewController.h"

static int kActivityIndicatorViewTag = 3;

@implementation CourseSectionsViewController

@synthesize centerActivityIndicator, centerMessageLabel, sectionsList;

- (id)initWithCourseId:(int)aCourseId andCourseTitle:(NSString*)aCourseTitle {
    self = [super initWithNibName:@"CourseSectionsView" bundle:nil];
    if (self) {
        currentLoadingView = nil;
        moodleService = [[MoodleService sharedInstanceToRetain] retain];
        authController = [[AuthenticationController alloc] init];
        currentLoadingView = nil;
        tequilaKey = nil;
        iSections = nil;
        courseId = aCourseId;
        courseTitle = [aCourseTitle retain];
        self.title = courseTitle;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.   
    self.view.backgroundColor = [PCValues backgroundColor1];
    sectionsList.hidden = YES;
    sectionsList.backgroundColor = [UIColor clearColor];
    UIView* backgroundView = [[UIView alloc] initWithFrame:sectionsList.frame];
    backgroundView.backgroundColor = [UIColor whiteColor];
    sectionsList.backgroundView = backgroundView;
    [backgroundView release];
    
    if(moodleService.moodleCookie == nil) {
        centerMessageLabel.text = @"";
        [centerActivityIndicator stopAnimating];
        sectionsList.hidden = YES;
    } else {
        [self startGetCourseSectionsRequest];
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    if(moodleService.moodleCookie == nil) {
        [centerActivityIndicator startAnimating];
        [self startAuth];
    }
    [sectionsList deselectRowAtIndexPath:[sectionsList indexPathForSelectedRow] animated:animated];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void)startGetCourseSectionsRequest {
    [centerActivityIndicator startAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"LoadingCourse", @"MoodlePlugin", nil);
    sectionsList.hidden = YES;
    
    SessionId* sess = [[SessionId alloc] init];
    [sess setTos:TypeOfService_SERVICE_MOODLE];
    [sess setMoodleCookie:moodleService.moodleCookie];
    MoodleRequest* req = [[MoodleRequest alloc] init];
    [req setISessionId:sess];
    [req setILanguage:@"en"];
    [req setICourseId:courseId];
    [moodleService getCourseSections:req withDelegate:self];
    [req release];
    [sess release];
}

- (void)computeCurrentWeek {
    if(iSections == nil)
        return;
    currentWeek = -1;
    for (NSInteger i = 0; i < iSections.count; i++) {
        MoodleSection* iSection = [iSections objectAtIndex:i];
        if(iSection.iResources.count != 0 && iSection.iCurrent) {
            currentWeek = i;
            break;
        }
    }
}

- (void)showToggleButton {
    UIBarButtonItem *anotherButton = nil;
    if (currentWeek > 0) {
        anotherButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"MoodleAllWeeks", @"MoodlePlugin", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(toggleShowAll:)];
    } else if (currentWeek == 0) {
        anotherButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"MoodleCurrentWeek", @"MoodlePlugin", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(toggleShowAll:)];
    }
    [self.navigationItem setRightBarButtonItem:anotherButton animated:YES];
    [anotherButton release];
}

- (void)toggleShowAll:(id)sender {
    if (currentWeek > 0) {
        currentWeek = 0;
    } else {
        [self computeCurrentWeek];
    }
    [self showToggleButton];
    [self cancelCurrentDownloadingDocument];
    [PCUtils reloadTableView:sectionsList withFadingDuration:0.3];
}


- (void)presentDocumentViewControllerForFile:(NSURL*)fileURL {
    currentLoadingView = nil;
    DocumentViewController* docViewController = [[DocumentViewController alloc] initWithDocumentLocalURL:fileURL];
    docViewController.title = @""; //TODO
    [self.navigationController pushViewController:docViewController animated:YES];
    [docViewController release];
}

- (void)startAuth {
    [moodleService getTequilaTokenForMoodleDelegate:self];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{    
    return (interfaceOrientation == UIInterfaceOrientationPortrait || interfaceOrientation == UIInterfaceOrientationLandscapeLeft || interfaceOrientation == UIInterfaceOrientationLandscapeRight);
}

- (void)cancelCurrentDownloadingDocument {
    if (currentLoadingView && [currentLoadingView isAnimating]) {
        UITableViewCell* prevSelectedCell = (UITableViewCell*)[[currentLoadingView superview] superview];
        prevSelectedCell.accessoryView.hidden = NO;
        [moodleService cancelOperationsForDelegate:self];
        [currentLoadingView stopAnimating];
        currentLoadingView = nil;
    }
}

/* service delegation */

- (void)serviceConnectionToServerTimedOut {
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
}

/* MoodleServiceDelegate delegation */

- (void)getTequilaTokenForMoodleDidReturn:(TequilaToken*)tequilaKey_ {
    [tequilaKey release];
    tequilaKey = [tequilaKey_ retain];
    [authController authToken:tequilaKey.iTequilaKey presentationViewController:self.navigationController delegate:self];
}

- (void)getTequilaTokenForMoodleFailed {
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", nil);
}

- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken*)tequilaKey didReturn:(MoodleSession*)sessionId {
    [moodleService saveMoodleCookie:sessionId.moodleCookie];
    [self startGetCourseSectionsRequest];
}

- (void)getSessionIdForServiceFailedForTequilaKey:(TequilaToken*)tequilaKey {
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", nil);
}

- (void)getCourseSections:(MoodleRequest*)aMoodleRequest didReturn:(SectionsListReply*)sectionsListReply {
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = @"";
    if(sectionsListReply.iStatus == 200) {
        [iSections release];
        iSections = [sectionsListReply.iSections retain];
        int visibleCount = 0;
        for (int i = 1; i < iSections.count; i++) {
            MoodleSection* secObj = [iSections objectAtIndex:i];
            visibleCount += secObj.iResources.count;
        }
        if(visibleCount != 0) {
            [self computeCurrentWeek];
            [self showToggleButton];
            [PCUtils reloadTableView:sectionsList withFadingDuration:0.2];
        } else {
            centerMessageLabel.text = NSLocalizedStringFromTable(@"MoodleEmptyCourse", @"MoodlePlugin", nil);
            NSLog(@"-> Moodle : empty course");
        }
    } else if(sectionsListReply.iStatus == 404) {
        centerMessageLabel.text = NSLocalizedStringFromTable(@"MoodleDown", @"MoodlePlugin", nil);
    } else if(sectionsListReply.iStatus == 405) {
        centerMessageLabel.text = @"Error, no course specified"; //should never happen
    } else if(sectionsListReply.iStatus == 407) { // session timed out
        // kill the cookie
        [moodleService saveMoodleCookie:nil];
        // and re call auth
        [self startAuth];
    }
}

- (void)getCourseSectionsFailed:(MoodleRequest*)aMoodleRequest {
    NSLog(@"-> getCourseSectionsFailed");
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", nil);
}

- (void)fetchMoodleResourceDidReturn:(ASIHTTPRequest*)request{
    [currentLoadingView stopAnimating];
    [[[sectionsList cellForRowAtIndexPath:[sectionsList indexPathForSelectedRow]] accessoryView] setHidden:NO];
    NSString* localPath = [moodleService localPathForURL:request.url.absoluteString];
    [self presentDocumentViewControllerForFile:[NSURL fileURLWithPath:localPath]];
}

- (void)fetchMoodleResourceFailed:(ASIHTTPRequest*)request {
    NSLog(@"-> fetchMoodleResourceFailed");
    NSIndexPath* selectedIndexPath = [sectionsList indexPathForSelectedRow];
    [sectionsList deselectRowAtIndexPath:selectedIndexPath animated:YES];
    [[[sectionsList cellForRowAtIndexPath:selectedIndexPath] accessoryView] setHidden:NO];
    [currentLoadingView stopAnimating];
    UIAlertView* downloadErrorAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ErrorWhileDownloadingFile", @"MoodlePlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [downloadErrorAlert show];
    [downloadErrorAlert release];
}

/* AuthenticationCallbackDelegate delegation */

- (void)authenticationSucceeded {
    [moodleService getSessionIdForServiceWithTequilaKey:tequilaKey delegate:self];
}

- (void)invalidToken {
    NSLog(@"-> invalidToken");
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", nil);
}

- (void)userCancelledAuthentication {
    [centerActivityIndicator stopAnimating];
    if (self.navigationController.visibleViewController == self) {
        [self.navigationController popViewControllerAnimated:YES]; //leaving plugin
    }
}

/* UITableViewDelegate delegation */

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell = [tableView cellForRowAtIndexPath:indexPath];
    
    UIActivityIndicatorView* cellActivityIndicatorView = (UIActivityIndicatorView*)[cell.contentView viewWithTag:kActivityIndicatorViewTag];
    
    if (cellActivityIndicatorView == currentLoadingView && [cellActivityIndicatorView isAnimating]) {
        return;
    }
    
    [self cancelCurrentDownloadingDocument];
    
    MoodleSection* section = [iSections objectAtIndex:indexPath.section];
    MoodleResource* resource = [section.iResources objectAtIndex:indexPath.row];
    NSString* urlStr = [moodleService localPathForURL:resource.iUrl];
    NSFileManager *fileManager= [NSFileManager defaultManager]; 
    if([fileManager fileExistsAtPath:urlStr]) {
        [self presentDocumentViewControllerForFile:[NSURL fileURLWithPath:urlStr]];
    } else { //show file loading animation
        //currentLoadingView = [(UIActivityIndicatorView*)[[tableView cellForRowAtIndexPath:indexPath] viewWithTag:kCourseCellLoadingViewTag] retain];
        [currentLoadingView release];
        
        currentLoadingView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhite];
        currentLoadingView.center = cell.accessoryView.center;
        currentLoadingView.tag = kActivityIndicatorViewTag;
        [cell.contentView addSubview:currentLoadingView];
        cell.accessoryView.hidden = YES;
        [currentLoadingView startAnimating];
        [moodleService fetchMoodleResource:moodleService.moodleCookie :resource.iUrl withDelegate:self];
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if(iSections == nil)
        return 0.0;
    if(![self showSection:section])
        return 0.0;
    MoodleSection* secObj = [iSections objectAtIndex:section];
    if(secObj.iResources.count == 0)
        return 0.0;
    return [PCValues tableViewSectionHeaderHeight];
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section 
{
    if(iSections == nil)
        return nil;
    if(![self showSection:section])
        return nil;
    MoodleSection* secObj = [iSections objectAtIndex:section];
    if(secObj.iResources.count == 0)
        return nil;
    
    NSString* title = [NSString stringWithFormat:NSLocalizedStringFromTable(@"MoodleWeek", @"MoodlePlugin", nil), section];
    
    PCTableViewSectionHeader* sectionHeader = [[PCTableViewSectionHeader alloc] initWithSectionTitle:title tableView:tableView];
    return [sectionHeader autorelease];
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 40.0;
}

/* UITableViewDataSource */

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    MoodleSection* section = [iSections objectAtIndex:indexPath.section];
    UITableViewCell* newCell = [sectionsList dequeueReusableCellWithIdentifier:@"MOODLE_SECTIONS_LIST"];
    if (newCell == nil) {
        newCell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:@"MOODLE_SECTIONS_LIST"] autorelease];
        /*UIActivityIndicatorView* loadingView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
        loadingView.tag = kCourseCellLoadingViewTag;
        newCell.accessoryView = loadingView;
        [loadingView release];*/
        newCell.selectionStyle = UITableViewCellSelectionStyleGray;        
        newCell.textLabel.font = [UIFont boldSystemFontOfSize:14.0];
        newCell.textLabel.adjustsFontSizeToFitWidth = YES;
        newCell.textLabel.minimumFontSize = 11.0;
        UILabel* fileTypeLabel = [[UILabel alloc] initWithFrame:CGRectMake(0.0, 0.0, 50.0, 35.0)];
        fileTypeLabel.font = [UIFont systemFontOfSize:13.0];
        fileTypeLabel.textAlignment = UITextAlignmentRight;
        fileTypeLabel.adjustsFontSizeToFitWidth = YES;
        fileTypeLabel.textColor = [PCValues textColorLocationBlue];
        newCell.accessoryView = fileTypeLabel;
        [fileTypeLabel release];

    }
    MoodleResource* resource = [section.iResources objectAtIndex:indexPath.row];
    newCell.textLabel.text = resource.iName;
    ((UILabel*)newCell.accessoryView).text = [NSString stringWithFormat:@"%@  ", [MoodleService fileTypeForURL:resource.iUrl]];
    return newCell;
}

/*- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    if(iSections == nil)
        return nil;
    if(![self showSection:section])
        return nil;
    MoodleSection* secObj = [iSections objectAtIndex:section];
    if(secObj.iResources.count == 0)
        return nil;
    return [NSString stringWithFormat:NSLocalizedStringFromTable(@"MoodleWeek", @"MoodlePlugin", nil), section];
}*/

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if(iSections == nil)
        return 0;
    if(![self showSection:section])
        return 0;
    MoodleSection* secObj = [iSections objectAtIndex:section];
    return secObj.iResources.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if(iSections == nil)
        return 0;
    return iSections.count;
}

- (BOOL)showSection:(NSInteger) section {
    if(section == 0)
        return NO;
    if(currentWeek <= 0)
        return YES;
    return (currentWeek == section);
}

/* end */

- (void)dealloc
{
    [currentLoadingView release];
    [authController release];
    [tequilaKey release];
    [moodleService cancelOperationsForDelegate:self];
    [moodleService release];
    [courseTitle release];
    [super dealloc];
}

@end
