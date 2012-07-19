//
//  CoursesListViewController.m
//  PocketCampus
//
//  Created by Amer C on 5/2/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "CoursesListViewController.h"

#import "CourseSectionsViewController.h"

#import "CredentialsViewController.h"

#import "MoodleService.h"

@implementation CoursesListViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        authController = [[AuthenticationController alloc] init];
        moodleService = [[MoodleService sharedInstanceToRetain] retain];
        tequilaKey = nil;
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    if(moodleService.moodleCookie == nil) {
        centerMessageLabel.text = @"";
        centerActivityIndicator.hidden = YES;
        coursesList.hidden = YES;
    } else {
        [self go];
    }
    //[controller release];
    
}

- (void)viewDidAppear:(BOOL)animated {
    if(moodleService.moodleCookie == nil) {
        centerActivityIndicator.hidden = NO;
        [centerActivityIndicator startAnimating];
        [self startAuth];
    }
    [coursesList deselectRowAtIndexPath:[coursesList indexPathForSelectedRow] animated:YES];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void)go {
    centerActivityIndicator.hidden = NO;
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
    
    /*UIBarButtonItem *anotherButton = [[UIBarButtonItem alloc] initWithTitle:@"Logout" style:UIBarButtonItemStylePlain target:self action:@selector(logoutFromMoodle:)];          
    self.navigationItem.rightBarButtonItem = anotherButton;
    [anotherButton release];*/
    self.navigationItem.title = NSLocalizedStringFromTable(@"MoodleCourses", @"MoodlePlugin", nil);
}

- (void) startAuth {
    [moodleService getTequilaTokenForMoodleDelegate:self];
}

- (void)logoutFromMoodle:(id)sender {
    [[NSUserDefaults standardUserDefaults] setObject:nil forKey:@"moodleCookie"];
    [[NSUserDefaults standardUserDefaults] synchronize];
    [self.navigationController popViewControllerAnimated:YES];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

/* service delegation */

- (void)serviceConnectionToServerTimedOut {
    [centerActivityIndicator stopAnimating];
    centerActivityIndicator.hidden = YES;
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
}

/* MoodleServiceDelegate delegation */

- (void)getTequilaTokenForMoodleDidReturn:(TequilaToken*)tequilaKey_ {
    NSLog(@"-> getTequilaTokenForMoodleDidReturn:%@", tequilaKey_);
    [tequilaKey release];
    tequilaKey = [tequilaKey_ retain];
    [authController authToken:tequilaKey.iTequilaKey presentationViewController:self.navigationController delegate:self];
}

- (void)getTequilaTokenForMoodleFailed {
    NSLog(@"-> getTequilaTokenForMoodleFailed");
    [self serviceConnectionToServerTimedOut];
}

- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken*)tequilaKey didReturn:(MoodleSession*)sessionId {
    //centerMessageLabel.text = sessionId.moodleCookie;
    moodleService.moodleCookie = sessionId.moodleCookie;
    [[NSUserDefaults standardUserDefaults] setObject:moodleService.moodleCookie forKey:@"moodleCookie"];
    [[NSUserDefaults standardUserDefaults] synchronize];
    [self go];
}

- (void)getSessionIdForServiceFailedForTequilaKey:(TequilaToken*)tequilaKey {
    [self serviceConnectionToServerTimedOut];
}

- (void)getCoursesList:(MoodleRequest*)aMoodleRequest didReturn:(CoursesListReply*)coursesListReply {
    //NSLog(@"courselistreply %@", coursesListReply);
    [centerActivityIndicator stopAnimating];
    centerActivityIndicator.hidden = YES;
    centerMessageLabel.text = @"";
    if(coursesListReply.iStatus == 200) {
        iCourses = [coursesListReply.iCourses retain];
        if(iCourses.count != 0) {
            coursesList.hidden = NO;
            [coursesList reloadData];
        } else {
            centerMessageLabel.text = NSLocalizedStringFromTable(@"MoodleNoCourses", @"MoodlePlugin", nil);
        }
    } else if(coursesListReply.iStatus == 404) {
        centerMessageLabel.text = NSLocalizedStringFromTable(@"MoodleDown", @"MoodlePlugin", nil);
    } else if(coursesListReply.iStatus == 407) { // session timed out
        // kill the cookie
        [[NSUserDefaults standardUserDefaults] setObject:nil forKey:@"moodleCookie"];
        [[NSUserDefaults standardUserDefaults] synchronize];
        moodleService.moodleCookie = nil;
        // and re call auth
        /*CredentialsViewController* controller = [[CredentialsViewController alloc] initWithCallback:self];
        [self.navigationController presentViewController:controller animated:YES completion:NULL];
        [controller release];
        pingedAuthPlugin = YES;*/
        [self startAuth];
    }
}

- (void)getCoursesListFailed:(MoodleRequest*)aMoodleRequest {
    [centerActivityIndicator stopAnimating];
    centerActivityIndicator.hidden = YES;
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
}

/* AuthenticationCallbackDelegate delegation */

- (void)authenticationSucceeded {
    [moodleService getSessionIdForServiceWithTequilaKey:tequilaKey delegate:self];
}

- (void)invalidToken {
    // TODO
}

- (void) deleteSessionWhenFinished {
    // TODO
}

- (void)userCancelledAuthentication {
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
    [tequilaKey release];
    [super dealloc];
}

@end
