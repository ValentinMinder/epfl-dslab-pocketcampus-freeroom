//
//  DocumentViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 20.07.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "DocumentViewController.h"



@implementation DocumentViewController

@synthesize webView, centerActivityIndicator, centerMessageLabel;

- (id)initWithDocumentRemoteURLString:(NSString*)documentRemoteURL;
{
    self = [super initWithNibName:@"DocumentView" bundle:nil];
    if (self) {
        documentRemoteURLString = [documentRemoteURL retain];
        documentLocalURL = nil;
        docInteractionController = nil;
        moodleService = [[MoodleService sharedInstanceToRetain] retain];
        NSString* tmpLocalURL = [moodleService localPathForURL:documentRemoteURLString];
        if ([[NSFileManager defaultManager] fileExistsAtPath:tmpLocalURL]) { //check if downloaded already
            documentLocalURL = [[NSURL fileURLWithPath:tmpLocalURL] retain];
            docInteractionController = [[UIDocumentInteractionController interactionControllerWithURL:documentLocalURL] retain];
            docInteractionController.delegate = self;
        }
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/moodle/course/document" withError:NULL];
    webView.scalesPageToFit = YES; //otherwise, pinching zoom is disabled
    UIBarButtonItem* actionButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAction target:self action:@selector(actionButtonPressed)];
    self.navigationItem.rightBarButtonItem = actionButton;
    [actionButton release];
    [centerActivityIndicator startAnimating];
    webView.hidden = YES;
    if (documentLocalURL) {
        [webView loadRequest:[NSURLRequest requestWithURL:documentLocalURL]];
    } else { //needs to be downloaded
        [centerActivityIndicator startAnimating];
        centerMessageLabel.text = NSLocalizedStringFromTable(@"DownloadingFile", @"MoodlePlugin", nil);
        [moodleService fetchMoodleResourceWithURL:documentRemoteURLString cookie:moodleService.moodleCookie delegate:self];
        actionButton.enabled = NO;
    }
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void)viewWillDisappear:(BOOL)animated {
    [webView stopLoading];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return YES; //support all orientations
}
                                
- (void)actionButtonPressed {
    BOOL couldShowMenu = [docInteractionController presentOptionsMenuFromBarButtonItem:self.navigationItem.rightBarButtonItem animated:YES];
    if (!couldShowMenu) {
        UIAlertView* alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Sorry", @"MoodlePlugin", nil) message:NSLocalizedStringFromTable(@"NoActionForThisFile", @"MoodlePlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alertView show];
        [alertView release];
    }
}

/* MoodleServiceDelegate delegation */

- (void)fetchMoodleResourceDidReturn:(ASIHTTPRequest *)request {
    centerMessageLabel.hidden = YES;
    documentLocalURL = [[NSURL fileURLWithPath:[moodleService localPathForURL:request.url.absoluteString]] retain];
    docInteractionController = [[UIDocumentInteractionController interactionControllerWithURL:documentLocalURL] retain];
    docInteractionController.delegate = self;
    self.navigationItem.rightBarButtonItem.enabled = YES;
    [webView loadRequest:[NSURLRequest requestWithURL:documentLocalURL]];
}

- (void)fetchMoodleResourceFailed:(ASIHTTPRequest *)request {
    webView.hidden = YES;
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ErrorWhileDownloadingFile", @"MoodlePlugin", nil);
}

- (void)serviceConnectionToServerTimedOut {
    webView.hidden = YES;
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ErrorWhileDownloadingFile", @"MoodlePlugin", nil);
}

/* UIDocumentInteractionControllerDelegate delegation */

- (BOOL)documentInteractionController:(UIDocumentInteractionController *)controller canPerformAction:(SEL)action
{
    if (action == @selector (print:) && [UIPrintInteractionController canPrintURL:controller.URL]) {
        return YES;
    }
    return NO;
}

- (BOOL)documentInteractionController:(UIDocumentInteractionController *)controller performAction:(SEL)action
{
    bool __block success = NO;
    if (action == @selector(print:)) {
        UIPrintInteractionController *printController = [UIPrintInteractionController sharedPrintController];
        printController.printingItem = controller.URL;
        [printController presentAnimated:YES completionHandler:^(UIPrintInteractionController *printInteractionController, BOOL completed, NSError *error){
            if (completed) {
                success = YES;
            }
        }];
    }
    return success;
}

/* UIWebViewDelegate delegation */

- (void)webViewDidFinishLoad:(UIWebView *)webView_ {
    [centerActivityIndicator stopAnimating];
    webView.hidden = NO;
}

- (void)webView:(UIWebView *)webView_ didFailLoadWithError:(NSError *)error {
    webView.hidden = YES;
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"FileCouldNotBeDisplayedTryOpenIn", @"MoodlePlugin", nil);
    centerMessageLabel.hidden = NO;
}

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    if ([request.URL.path isEqualToString:documentLocalURL.path]) {
        return YES;
    }
    [[UIApplication sharedApplication] openURL:request.URL]; //open in Safari so that current webview does not leave document
    return NO;
}

- (void)dealloc {
    [moodleService cancelOperationsForDelegate:self];
    [moodleService release];
    webView.delegate = nil;
    [webView stopLoading];
    [documentRemoteURLString release];
    [documentLocalURL release];
    docInteractionController.delegate = nil;
    [docInteractionController release];
    [super dealloc];
}

@end
