//
//  CoursesListViewController.m
//  PocketCampus
//
//  Created by Amer C on 5/2/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "CoursesListViewController.h"

#import "CourseSectionsViewController.h"

#import "MoodleService.h"

#import "PCValues.h"

#import "PCUtils.h"

@implementation CoursesListViewController

@synthesize coursesList, centerActivityIndicator, centerMessageLabel;

- (id)init
{
    self = [super initWithNibName:@"CoursesListView" bundle:nil];
    if (self) {
        // Custom initialization
        authController = [[AuthenticationController alloc] init];
        moodleService = [[MoodleService sharedInstanceToRetain] retain];
        tequilaKey = nil;
        iCourses = nil;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/moodle" withError:NULL];
    self.view.backgroundColor = [PCValues backgroundColor1];
    coursesList.backgroundColor = [UIColor clearColor];
    UIView* backgroundView = [[UIView alloc] initWithFrame:coursesList.frame];
    backgroundView.backgroundColor = [PCValues backgroundColor1];;
    coursesList.backgroundView = backgroundView;
    [backgroundView release];
    [centerActivityIndicator startAnimating];
    if(moodleService.moodleCookie == nil) {
        centerMessageLabel.text = @"";
        coursesList.hidden = YES;
        [self startAuth];
    } else {
        [self startGetCoursesListRequest];
    }
    
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [coursesList deselectRowAtIndexPath:[coursesList indexPathForSelectedRow] animated:animated];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void)startGetCoursesListRequest {
    [centerActivityIndicator startAnimating];
    centerMessageLabel.text = @"";
    coursesList.hidden = YES;
    
    SessionId* sess = [[SessionId alloc] init];
    [sess setTos:TypeOfService_SERVICE_MOODLE];
    [sess setMoodleCookie:moodleService.moodleCookie];
    MoodleRequest* req = [[MoodleRequest alloc] init];
    [req setISessionId:sess];
    [req setILanguage:@"en"];
    [moodleService getCoursesList:req withDelegate:self];
    [req release];
    [sess release];
    
    self.title = NSLocalizedStringFromTable(@"MoodleCourses", @"MoodlePlugin", nil);
}

- (void)startAuth {
    [centerActivityIndicator startAnimating];
    [moodleService getTequilaTokenForMoodleDelegate:self];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskAllButUpsideDown;
}

- (BOOL)shouldAutorotate {
    if (coursesList.hidden) {
        return NO;
    }
    return YES;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation //iOS 5 and earlier
{
    if (coursesList.hidden) {
        return (interfaceOrientation == UIInterfaceOrientationPortrait);
    }
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
    NSLog(@"-> getTequilaTokenForMoodleFailed");
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", nil);
    //[MoodleService deleteMoodleCookie];
}

- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken*)tequilaKey didReturn:(MoodleSession*)sessionId {
    [moodleService saveMoodleCookie:sessionId.moodleCookie];
    [self startGetCoursesListRequest];
}

- (void)getSessionIdForServiceFailedForTequilaKey:(TequilaToken*)tequilaKey {
    NSLog(@"-> getSessionIdForServiceFailedForTequilaKey");
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", nil);
    [MoodleService deleteMoodleCookie];
}

- (void)getCoursesList:(MoodleRequest*)aMoodleRequest didReturn:(CoursesListReply*)coursesListReply {
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = @"";
    if(coursesListReply.iStatus == 200) {
        [iCourses release];
        iCourses = [coursesListReply.iCourses retain];
        if(iCourses.count != 0) {
            [PCUtils reloadTableView:coursesList withFadingDuration:0.2];
        } else {
            centerMessageLabel.text = NSLocalizedStringFromTable(@"MoodleNoCourse", @"MoodlePlugin", nil);
        }
    } else if(coursesListReply.iStatus == 404) {
        centerMessageLabel.text = NSLocalizedStringFromTable(@"MoodleDown", @"MoodlePlugin", nil);
    } else if(coursesListReply.iStatus == 407) { // session timed out
        // kill the cookie
        [moodleService saveMoodleCookie:nil];
        // and re call auth
        [self startAuth];
    }
}

- (void)getCoursesListFailed:(MoodleRequest*)aMoodleRequest {
    NSLog(@"-> getCoursesListFailed");
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
    [MoodleService deleteMoodleCookie];
}

- (void)userCancelledAuthentication {
    [MoodleService deleteMoodleCookie];
    [centerActivityIndicator stopAnimating];
    if (self.navigationController.visibleViewController == self) {
        [self.navigationController popViewControllerAnimated:YES]; //leaving plugin
    }
}

/* UITableViewDelegate delegation */

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    MoodleCourse* course = [iCourses objectAtIndex:indexPath.row];
    CourseSectionsViewController* controller = [[CourseSectionsViewController alloc] initWithCourseId:course.iId andCourseTitle:course.iTitle];
    [self.navigationController pushViewController:controller animated:YES];
    [controller release];
}

/* UITableViewDataSource */

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    MoodleCourse* course = [iCourses objectAtIndex:indexPath.row];
    UITableViewCell* newCell = [coursesList dequeueReusableCellWithIdentifier:@"MOODLE_COURSES_LIST"];
    if (newCell == nil) {
        newCell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"MOODLE_COURSES_LIST"] autorelease];
        newCell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        newCell.selectionStyle = UITableViewCellSelectionStyleGray;
    }
    newCell.textLabel.text = course.iTitle;
    return newCell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (iCourses == nil) {
        return 0;
    }
    return iCourses.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

/* end */

- (void)dealloc
{
    [authController release];
    [moodleService cancelOperationsForDelegate:self];
    [moodleService release];
    [iCourses release];
    [tequilaKey release];
    [super dealloc];
}

@end
