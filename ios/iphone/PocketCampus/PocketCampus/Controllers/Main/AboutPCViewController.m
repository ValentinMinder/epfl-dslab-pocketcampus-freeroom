//
//  AboutPCViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.08.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "AboutPCViewController.h"

#import "PCValues.h"

#import "PCConfig.h"

#import "PCUtils.h"

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
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/dashboard/about" withError:NULL];
	self.title = NSLocalizedStringFromTable(@"About", @"PocketCampus", nil);
    self.view.backgroundColor = [PCValues backgroundColor1];
    webView.delegate = self;
    webView.scrollView.scrollEnabled = NO;
    webView.alpha = 0.0;
    NSString* htmlPath = [[NSBundle mainBundle] pathForResource:@"AboutPC" ofType:@"html"];
    NSError* error = nil;
    NSString* htmlString = [NSString stringWithContentsOfFile:htmlPath encoding:NSUTF8StringEncoding error:&error];
    
    NSString* version = [[[NSBundle mainBundle] infoDictionary] objectForKey:(NSString*)kCFBundleVersionKey];
    
    htmlString = [htmlString stringByReplacingOccurrencesOfString:@"$PC_VERSION$" withString:version];
    
    if (!error) {
        [webView loadHTMLString:htmlString baseURL:[NSURL URLWithString:@""]];
    }
    
    UITapGestureRecognizer* gestureRec = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(showInfos)];
    gestureRec.numberOfTapsRequired = 3;
    gestureRec.numberOfTouchesRequired = 2;
    [webView addGestureRecognizer:gestureRec];
    [gestureRec release];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    if ([PCUtils isIdiomPad]) {
        return UIInterfaceOrientationMaskAll;
    } else {
        return UIInterfaceOrientationMaskPortrait;
    }
    
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation //iOS 5
{
    if ([PCUtils isIdiomPad]) {
        return YES;
    } else {
        return (interfaceOrientation == UIInterfaceOrientationPortrait);
    }
}

- (void)showInfos {
    NSString* message = [NSString stringWithFormat:@"%@\n\n%@", [[PCConfig defaults] objectForKey:PC_CONFIG_TYPE_KEY], [[PCConfig defaults] objectForKey:PC_CONFIG_SERVER_ADDRESS_KEY]];
    UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"PC_CONFIG_TYPE" message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
    [alert release];
}

/* UIWebViewDelegate delegation */

- (void)webViewDidFinishLoad:(UIWebView *)webView_ {
    [webView sizeToFit];
    if ([PCUtils isIdiomPad]) {
        webView.frame = CGRectOffset(webView.frame, 0, -20.0);
    } else {
        if ([PCUtils is4inchDevice]) {
            webView.frame = CGRectOffset(webView.frame, 0, -10.0);
        } else {
            webView.frame = CGRectOffset(webView.frame, 0, 15.0);
        }
    }
    webView.alpha = 1.0;
}

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
