//
//  CourseSectionsViewController.m
//  PocketCampus
//
//  Created by Amer C on 5/3/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "CourseSectionsViewController.h"

#import "AuthenticationController.h"

#import "PCValues.h"

#import "PCUtils.h"

#import "PCTableViewSectionHeader.h"

#import "DocumentViewController.h"

//static int kDownloadedCornerImageViewTag = 3;

@implementation CourseSectionsViewController

@synthesize centerActivityIndicator, centerMessageLabel, sectionsList;

- (id)initWithCourseId:(int)aCourseId andCourseTitle:(NSString*)aCourseTitle {
    self = [super initWithNibName:@"CourseSectionsView" bundle:nil];
    if (self) {
        moodleService = [[MoodleService sharedInstanceToRetain] retain];
        authController = [[AuthenticationController alloc] init];
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
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/moodle/course" withError:NULL];
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
    NSIndexPath* selectedIndexPath = [sectionsList indexPathForSelectedRow];
    if (selectedIndexPath) {
        [sectionsList deselectRowAtIndexPath:selectedIndexPath animated:animated];
        [sectionsList reloadRowsAtIndexPaths:[NSArray arrayWithObject:selectedIndexPath] withRowAnimation:UITableViewRowAnimationFade];
    }
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
    currentWeek = -1; //-1 means outside semester time, all weeks will be displayed and toggle button hidden
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
    [PCUtils reloadTableView:sectionsList withFadingDuration:0.3];
}


- (void)presentDocumentViewControllerForFileRemoteURLString:(NSString*)fileURL {
    DocumentViewController* docViewController = [[DocumentViewController alloc] initWithDocumentRemoteURLString:fileURL];
    docViewController.title = @""; //TODO
    [self.navigationController pushViewController:docViewController animated:YES];
    [docViewController release];
}

- (void)startAuth {
    [moodleService getTequilaTokenForMoodleDelegate:self];

}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskAllButUpsideDown;
}

- (BOOL)shouldAutorotate {
    return YES;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation // iOS 5 and earlier
{    
    return (interfaceOrientation == UIInterfaceOrientationPortrait || interfaceOrientation == UIInterfaceOrientationLandscapeLeft || interfaceOrientation == UIInterfaceOrientationLandscapeRight);
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
    MoodleSection* section = [iSections objectAtIndex:indexPath.section];
    MoodleResource* resource = [section.iResources objectAtIndex:indexPath.row];
    [self presentDocumentViewControllerForFileRemoteURLString:resource.iUrl];
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
    
    /*NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setTimeZone:[NSTimeZone systemTimeZone]];
    [dateFormatter setLocale:[NSLocale systemLocale]];
    [dateFormatter setDateFormat:@"dd/MM"];
    //NSLog(@"%lld", secObj.iStartDate);
    NSString* startDate = [dateFormatter stringFromDate:[NSDate dateWithTimeIntervalSince1970:secObj.iStartDate]];*/
    
    /* startDate and endDate are not filled by server yet */
    
    NSString* title = [NSString stringWithFormat:@"%@ %d", NSLocalizedStringFromTable(@"MoodleWeek", @"MoodlePlugin", nil), section];
    
    PCTableViewSectionHeader* sectionHeader = [[PCTableViewSectionHeader alloc] initWithSectionTitle:title tableView:tableView];
    return [sectionHeader autorelease];
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 44.0;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        MoodleSection* section = [iSections objectAtIndex:indexPath.section];
        MoodleResource* resource = [section.iResources objectAtIndex:indexPath.row];
        NSString* localPath = [MoodleService localPathForURL:resource.iUrl];
        if (![MoodleService deleteFileAtPath:localPath]) {
            UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ImpossibleDeleteFile", @"MoodlePlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [errorAlert show];
            [errorAlert release];
            return;
        }
        [[GANTracker sharedTracker] trackPageview:@"/v3r1/moodle/course/document/delete" withError:NULL];
        [tableView reloadRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
    }
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView_ editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    MoodleSection* section = [iSections objectAtIndex:indexPath.section];
    MoodleResource* resource = [section.iResources objectAtIndex:indexPath.row];
    if ([MoodleService isFileCached:[MoodleService localPathForURL:resource.iUrl]]) {
        return UITableViewCellEditingStyleDelete;
    }
    return UITableViewCellEditingStyleNone;
}

/* UITableViewDataSource */

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    MoodleSection* section = [iSections objectAtIndex:indexPath.section];
    UITableViewCell* newCell = [sectionsList dequeueReusableCellWithIdentifier:@"MOODLE_SECTIONS_LIST"];
    if (newCell == nil) {
        newCell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"MOODLE_SECTIONS_LIST"] autorelease];
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
        [savedLabel release];

    }
    MoodleResource* resource = [section.iResources objectAtIndex:indexPath.row];
    newCell.textLabel.text = resource.iName;
    NSArray* pathComponents = [resource.iUrl pathComponents];
    newCell.detailTextLabel.text = [pathComponents objectAtIndex:pathComponents.count-1];
    NSString* localPath = [MoodleService localPathForURL:resource.iUrl];
    if ([MoodleService isFileCached:localPath]) {
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
    [authController release];
    [tequilaKey release];
    [moodleService cancelOperationsForDelegate:self];
    [moodleService release];
    [courseTitle release];
    [iSections release];
    [super dealloc];
}

@end
