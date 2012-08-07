//
//  AboutPCViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.08.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "AboutPCViewController.h"

#import "PCValues.h"

@implementation AboutPCViewController

@synthesize webView;

- (id)init
{
    self = [super initWithNibName:@"AboutPCView" bundle:nil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	self.title = NSLocalizedStringFromTable(@"About ", @"PocketCampus", nil);
    self.view.backgroundColor = [PCValues backgroundColor1];
    webView.delegate = self;
    NSString* htmlPath = [[NSBundle mainBundle] pathForResource:@"AboutPC" ofType:@"html"];
    NSError* error = nil;
    NSString* htmlString = [NSString stringWithContentsOfFile:htmlPath encoding:NSUTF8StringEncoding error:&error];
    if (!error) {
        [webView loadHTMLString:htmlString baseURL:[NSURL URLWithString:@""]];
    }
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

/* UIWebViewDelegate delegation */

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    if (navigationType == UIWebViewNavigationTypeLinkClicked) {
        [[UIApplication sharedApplication] openURL:request.URL];
        return NO;
    }
    return YES;
}

- (void)dealloc
{
    webView.delegate = nil;
    [webView stopLoading];
    [super dealloc];
}

@end
