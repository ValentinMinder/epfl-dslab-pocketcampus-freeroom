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

- (id)initWithDocumentLocalURL:(NSURL*)documentURL_
{
    self = [super initWithNibName:@"DocumentView" bundle:nil];
    if (self) {
        documentURL = [documentURL_ retain];
        docInteractionController = [[UIDocumentInteractionController interactionControllerWithURL:documentURL] retain];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/moodle/course/document" withError:NULL];
    docInteractionController.delegate = self;
    //webView.hidden = YES;
    webView.scalesPageToFit = YES; //otherwise, pinching zoom is disabled
    //[centerActivityIndicator startAnimating];
    UIBarButtonItem* actionButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAction target:self action:@selector(actionButtonPressed)];
    self.navigationItem.rightBarButtonItem = actionButton;
    [actionButton release];
    
    [webView loadRequest:[NSURLRequest requestWithURL:documentURL]];
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
    if ([request.URL.path isEqualToString:documentURL.path]) {
        return YES;
    }
    [[UIApplication sharedApplication] openURL:request.URL]; //open in Safari so that current webview does not leave document
    return NO;
}

- (void)dealloc {
    webView.delegate = nil;
    [webView stopLoading];
    [documentURL release];
    docInteractionController.delegate = nil;
    [docInteractionController release];
    [super dealloc];
}

@end
