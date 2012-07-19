//
//  CourseSectionsViewController.m
//  PocketCampus
//
//  Created by Amer C on 5/3/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "CourseSectionsViewController.h"

#import "CredentialsViewController.h"

#import "AuthenticationController.h"

@interface CourseSectionsViewController ()

@end

@implementation CourseSectionsViewController

@synthesize docController;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        moodleService = [[MoodleService sharedInstanceToRetain] retain];
    }
    return self;
}

- (id)initWithCourseId:(int)aCourseId andCourseTitle:(NSString*)aCourseTitle {
    self = [super initWithNibName:@"CourseSectionsView" bundle:nil];
    if (self) {
        moodleService = [[MoodleService sharedInstanceToRetain] retain];
        authController = [[AuthenticationController alloc] init];
        tequilaKey = nil;
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
    
    if(moodleService.moodleCookie == nil) {
        centerMessageLabel.text = @"";
        centerActivityIndicator.hidden = YES;
        sectionsList.hidden = YES;
    } else {
        [self go];
    }
    webView.hidden = YES;
    sectionsList.hidden = YES;

    NSError *error;
    NSFileManager *fileMgr = [NSFileManager defaultManager];
    NSString *documentsDirectory = [NSHomeDirectory()  stringByAppendingPathComponent:@"Documents"];
    NSLog(@"Documents directory: %@",   [fileMgr contentsOfDirectoryAtPath:documentsDirectory error:&error]);
    
    
    UIBarButtonItem *anotherButton = [[UIBarButtonItem alloc] initWithTitle:@"Toggle" style:UIBarButtonItemStylePlain target:self action:@selector(toggleShowAll:)];
    self.navigationItem.rightBarButtonItem = anotherButton;
    [anotherButton release];
}

- (void)viewDidAppear:(BOOL)animated {
    if(moodleService.moodleCookie == nil) {
        centerActivityIndicator.hidden = NO;
        [centerActivityIndicator startAnimating];
        [self startAuth];
    }
    [sectionsList deselectRowAtIndexPath:[sectionsList indexPathForSelectedRow] animated:YES];
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

- (void)hideWebView:(id)sender {
    webView.hidden = YES;
    self.navigationItem.rightBarButtonItem = nil;
    [sectionsList deselectRowAtIndexPath:[sectionsList indexPathForSelectedRow] animated:YES];
    
}

- (void) computeCurrent {
    if(iSections == nil)
        return;
    current = 0;
    for (NSInteger i = 0; i < iSections.count; i++) {
        MoodleSection* iSection = [iSections objectAtIndex:i];
        if(iSection.iResources.count != 0 && iSection.iCurrent) {
            current = i;
            break;
        }
    }
}

- (void)toggleShowAll:(id)sender {
    if (current != 0) {
        current = 0;
    } else {
        [self computeCurrent];
    }
    [sectionsList reloadData];
}

/*
- (BOOL) documentInteractionController: (UIDocumentInteractionController *) controller canPerformAction: (SEL) action {
    NSLog(@"documentInteractionController:canPerformAction:");
    return YES;
}

- (void) documentInteractionController: (UIDocumentInteractionController *) controller didEndSendingToApplication: (NSString *) application {
    NSLog(@"documentInteractionController:didEndSendingToApplication:");
}

- (void) documentInteractionController: (UIDocumentInteractionController *) controller willBeginSendingToApplication: (NSString *) application {
    NSLog(@"documentInteractionController:willBeginSendingToApplication:");
}

- (void) documentInteractionControllerDidDismissOpenInMenu: (UIDocumentInteractionController *) controller {
    NSLog(@"documentInteractionControllerDidDismissOpenInMenu:");
}
*/

- (UIViewController *)documentInteractionControllerViewControllerForPreview:(UIDocumentInteractionController *)interactionController {
    NSLog(@"documentInteractionControllerViewControllerForPreview:");
    return self;
}

- (void)initDocController:(NSURL *)url {
    if (docController == nil) {
        docController = [UIDocumentInteractionController interactionControllerWithURL:url];
        docController.delegate = self;
    } else {
        docController.URL = url;
    }
}

- (void)openFile:(NSURL*)fileUrl {
    //webView.hidden = NO;
    //[webView loadRequest:[NSURLRequest requestWithURL:fileUrl]];
    //UIBarButtonItem *anotherButton = [[UIBarButtonItem alloc] initWithTitle:@"Back" style:UIBarButtonItemStylePlain target:self action:@selector(hideWebView:)];
    //self.navigationItem.rightBarButtonItem = anotherButton;
    //[anotherButton release];
    //self.navigationItem.title = @"Courses";

    //NSURL *url = [NSURL fileURLWithPath:@"http://www.zilog.com/docs/z80/um0080.pdf"];
    //UIDocumentInteractionController*
    //docController = [UIDocumentInteractionController interactionControllerWithURL:fileUrl];
    //[docController retain];
    //docController.delegate = self;
    //BOOL isValid = [docController presentOpenInMenuFromRect:CGRectZero inView:self.view animated:YES];
    //NSLog(@"isValid %d", isValid);
    //[docController presentPreviewAnimated:YES];
    
    //[self initDocController:fileUrl];
    //[docController presentPreviewAnimated:YES];
    
    // METHOD 1
    self.docController = [UIDocumentInteractionController interactionControllerWithURL:fileUrl];
    self.docController.delegate = self;
    if(![self.docController presentOpenInMenuFromRect:CGRectZero inView:self.view animated:YES]) {
        // METHOD 2
        UIDocumentInteractionController* dicController = [UIDocumentInteractionController interactionControllerWithURL:fileUrl];
        //[dicController retain];
        dicController.delegate = self;
        [dicController presentPreviewAnimated:YES];
    }
}

- (void) startAuth {
    [moodleService getTequilaTokenForMoodleDelegate:self];
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

- (void)getCourseSections:(MoodleRequest*)aMoodleRequest didReturn:(SectionsListReply*)sectionsListReply {
    [centerActivityIndicator stopAnimating];
    centerActivityIndicator.hidden = YES;
    centerMessageLabel.text = @"";
    if(sectionsListReply.iStatus == 200) {
        iSections = [sectionsListReply.iSections retain];
        int visibleCount = 0;
        for (int i = 1; i < iSections.count; i++) {
            MoodleSection* secObj = [iSections objectAtIndex:i];
            visibleCount += secObj.iResources.count;
        }
        if(visibleCount != 0) {
            //MoodleSection* sec = [iSections objectAtIndex:2];
            //sec.iCurrent = YES;
            [self computeCurrent];
            sectionsList.hidden = NO;
            [sectionsList reloadData];
        } else {
            centerMessageLabel.text = NSLocalizedStringFromTable(@"MoodleEmptyCourse", @"MoodlePlugin", nil);
            NSLog(@"MoodleEmptyCourse");
        }
    } else if(sectionsListReply.iStatus == 404) {
        centerMessageLabel.text = NSLocalizedStringFromTable(@"MoodleDown", @"MoodlePlugin", nil);
    } else if(sectionsListReply.iStatus == 405) {
        centerMessageLabel.text = @"No course specified";
    } else if(sectionsListReply.iStatus == 407) { // session timed out
        // kill the cookie
        [[NSUserDefaults standardUserDefaults] setObject:nil forKey:@"moodleCookie"];
        [[NSUserDefaults standardUserDefaults] synchronize];
        moodleService.moodleCookie = nil;
        // and re call auth
        [self startAuth];
    }
}

- (void)getCourseSectionsFailed:(MoodleRequest*)aMoodleRequest {
    [centerActivityIndicator stopAnimating];
    centerActivityIndicator.hidden = YES;
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
}

- (void)fetchMoodleResourceDidReturn:(ASIHTTPRequest*)request{
    [centerActivityIndicator stopAnimating];
    centerActivityIndicator.hidden = YES;
    
    NSString* urlStr = [moodleService getLocalPath:request.url.absoluteString];
    NSURL *fileUrl = [NSURL fileURLWithPath:urlStr];
    [self openFile:fileUrl];
}

- (void)fetchMoodleResourceFailed:(ASIHTTPRequest*)request {
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
    
    NSString* urlStr = [moodleService getLocalPath:resource.iUrl];
    NSFileManager *fileManager= [NSFileManager defaultManager]; 
    if([fileManager fileExistsAtPath:urlStr]) {
        NSURL *fileUrl = [NSURL fileURLWithPath:urlStr];
        [self openFile:fileUrl];
    } else {
        [moodleService fetchMoodleResource:moodleService.moodleCookie :resource.iUrl withDelegate:self];
        centerActivityIndicator.hidden = NO;
        [centerActivityIndicator startAnimating];
    }
}

/* UITableViewDataSource */

- (UITableViewCell *)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    MoodleSection* section = [iSections objectAtIndex:indexPath.section];
    UITableViewCell* newCell = [sectionsList dequeueReusableCellWithIdentifier:@"MOODLE_SECTIONS_LIST"];
    if (newCell == nil) {
        newCell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"MOODLE_SECTIONS_LIST"] autorelease];
        //newCell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        newCell.selectionStyle = UITableViewCellSelectionStyleGray;
    }
    MoodleResource* resource = [section.iResources objectAtIndex:indexPath.row];
    newCell.textLabel.text = resource.iName;
    return newCell;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    if(iSections == nil)
        return nil;
    if(![self showSection:section])
        return nil;
    MoodleSection* secObj = [iSections objectAtIndex:section];
    if(secObj.iResources.count == 0)
        return nil;
    return [NSString stringWithFormat:NSLocalizedStringFromTable(@"MoodleWeek", @"MoodlePlugin", nil), section];
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

- (BOOL) showSection:(NSInteger) section {
    if(section == 0)
        return NO;
    if(current == 0)
        return YES;
    return (current == section);
}

/* end */

- (void)dealloc
{
    [authController release];
    [tequilaKey release];
    [moodleService cancelOperationsForDelegate:self];
    [moodleService release];
    [super dealloc];
}

@end
