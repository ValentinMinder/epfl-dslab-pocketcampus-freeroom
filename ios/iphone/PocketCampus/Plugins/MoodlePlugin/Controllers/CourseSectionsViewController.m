//
//  CourseSectionsViewController.m
//  PocketCampus
//
//  Created by Amer C on 5/3/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "CourseSectionsViewController.h"

#import "CredentialsViewController.h"

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
    pingedAuthPlugin = NO;
    webView.hidden = YES;

    NSError *error;
    NSFileManager *fileMgr = [NSFileManager defaultManager];
    NSString *documentsDirectory = [NSHomeDirectory()  stringByAppendingPathComponent:@"Documents"];
    //NSString *filePath = [documentsDirectory  stringByAppendingPathComponent:@"file1.txt"];
    //NSString *str = @"Hello";
    //[str writeToFile:filePath atomically:YES   encoding:NSUTF8StringEncoding error:&error];
    NSLog(@"Documents directory: %@",   [fileMgr contentsOfDirectoryAtPath:documentsDirectory error:&error]);
    
}

- (void)viewDidAppear:(BOOL)animated {
    if(moodleService.moodleCookie == nil) {
        if(pingedAuthPlugin) {
            [self.navigationController popViewControllerAnimated:YES];
        } else {
            CredentialsViewController* controller = [[CredentialsViewController alloc] initWithCallback:self];
            [self.navigationController presentViewController:controller animated:YES completion:NULL];
            [controller release];
            pingedAuthPlugin = YES;
        }
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
    centerMessageLabel.text = @"Loading";
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
    
    UIDocumentInteractionController* dicController = [UIDocumentInteractionController interactionControllerWithURL:fileUrl];
    //[dicController retain];
    dicController.delegate = self;
    [dicController presentPreviewAnimated:YES];
    
    //[self initDocController:fileUrl];
    //[docController presentPreviewAnimated:YES];
    
    //TODO for some reason the below is not working, should make it work and switch to it
    //self.docController = [UIDocumentInteractionController interactionControllerWithURL:fileUrl];
    //self.docController.delegate = self;
    //[self.docController presentOpenInMenuFromRect:CGRectZero inView:self.view animated:YES];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

/* service delegation */

- (void)serviceConnectionToServerTimedOut {
    [centerActivityIndicator stopAnimating];
    centerActivityIndicator.hidden = YES;
    centerMessageLabel.text = @"Connection timed out, check your internet connectivity";
}

/* MoodleServiceDelegate delegation */

- (void)getCourseSections:(MoodleRequest*)aMoodleRequest didReturn:(SectionsListReply*)sectionsListReply {
    [centerActivityIndicator stopAnimating];
    centerActivityIndicator.hidden = YES;
    centerMessageLabel.text = @"";
    if(sectionsListReply.iStatus == 200) {
        iSections = [sectionsListReply.iSections retain];
        if(iSections.count != 0) {
            sectionsList.hidden = NO;
            [sectionsList reloadData];
        } else {
            centerMessageLabel.text = @"This course has 0 sections";
        }
    } else if(sectionsListReply.iStatus == 404) {
        centerMessageLabel.text = @"Moodle is down";
    } else if(sectionsListReply.iStatus == 405) {
        centerMessageLabel.text = @"No course specified";
    } else if(sectionsListReply.iStatus == 407) { // session timed out
        // kill the cookie
        [[NSUserDefaults standardUserDefaults] setObject:nil forKey:@"moodleCookie"];
        [[NSUserDefaults standardUserDefaults] synchronize];
        moodleService.moodleCookie = nil;
        // and re call auth
        CredentialsViewController* controller = [[CredentialsViewController alloc] initWithCallback:self];
        [self.navigationController presentViewController:controller animated:YES completion:NULL];
        [controller release];
        pingedAuthPlugin = YES;
    }
}

- (void)getCourseSectionsFailed:(MoodleRequest*)aMoodleRequest {
    [centerActivityIndicator stopAnimating];
    centerActivityIndicator.hidden = YES;
    centerMessageLabel.text = @"Could not reach the server, check your internet connection and try again";
}

- (void)fetchMoodleResourceDidReturn:(ASIHTTPRequest*)request{
    [centerActivityIndicator stopAnimating];
    centerActivityIndicator.hidden = YES;
    
    //NSURL *fileUrl = [NSURL URLWithString:@"http://www.zilog.com/docs/z80/um0080.pdf"];
    NSString* urlStr = [moodleService getLocalPath:request.url.absoluteString];
    NSURL *fileUrl = [NSURL fileURLWithPath:urlStr];
    [self openFile:fileUrl];
}

- (void)fetchMoodleResourceFailed:(ASIHTTPRequest*)request {
    [centerActivityIndicator stopAnimating];
    centerActivityIndicator.hidden = YES;
    centerMessageLabel.text = @"Could not reach server, check Internet connectivity";
}

/* AuthenticationCallbackDelegate delegation */

- (int)getTypeOfService {
    return TypeOfService_SERVICE_MOODLE;
}

- (void)gotSessionId:(SessionId*)aSessionId {
    moodleService.moodleCookie = aSessionId.moodleCookie;
    [[NSUserDefaults standardUserDefaults] setObject:moodleService.moodleCookie forKey:@"moodleCookie"];
    [[NSUserDefaults standardUserDefaults] synchronize];
    [self go];
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
    return [NSString stringWithFormat:@"Week %d", section];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if(iSections == nil)
        return 0;
    MoodleSection* secObj = [iSections objectAtIndex:section];
    return secObj.iResources.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if(iSections == nil)
        return 0;
    return iSections.count;
}

/* end */

- (void)dealloc
{
    [moodleService cancelOperationsForDelegate:self];
    [moodleService release];
    [super dealloc];
}

@end
