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
    NSString* htmlPath = [[NSBundle mainBundle] pathForResource:@"AboutPC" ofType:@"html"];
    NSError* error = nil;
    NSString* htmlString = [NSString stringWithContentsOfFile:htmlPath encoding:NSUTF8StringEncoding error:&error];
    
    NSString* version = [[[NSBundle mainBundle] infoDictionary] objectForKey:(NSString*)kCFBundleVersionKey];
    
    htmlString = [htmlString stringByReplacingOccurrencesOfString:@"$PC_VERSION$" withString:version];
    
    if (!error) {
        [webView loadHTMLString:htmlString baseURL:[NSURL URLWithString:@""]];
    }
    
    UITapGestureRecognizer* gestureRec = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(toggleDevMode)];
    gestureRec.numberOfTapsRequired = 3;
    gestureRec.numberOfTouchesRequired = 2;
    [webView addGestureRecognizer:gestureRec];
    [gestureRec release];
}

- (void)toggleDevMode {
    NSUserDefaults* defaults = [NSUserDefaults standardUserDefaults];
    BOOL devMode = [defaults boolForKey:@"PC_DEV_MODE"];
    NSString* alertTitle;
    if (devMode) {
        alertTitle = @"DEV_MODE OFF";
        [defaults setBool:NO forKey:@"PC_DEV_MODE"];
        [defaults synchronize];
    } else {
        alertTitle = @"DEV_MODE ON";
        [defaults setBool:YES forKey:@"PC_DEV_MODE"];
        [defaults synchronize];
    }
    UIAlertView* alert = [[UIAlertView alloc] initWithTitle:alertTitle message:@"Triple-tap again to toggle" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
    [alert release];
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
