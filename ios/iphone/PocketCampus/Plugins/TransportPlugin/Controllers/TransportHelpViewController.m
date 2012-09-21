//
//  HelpViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 26.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "TransportHelpViewController.h"

#import "PCValues.h"

@implementation TransportHelpViewController

@synthesize webView;

- (id)initWithHTMLFilePath:(NSString*)htmlFilePath_;
{
    self = [super initWithNibName:@"TransportHelpView" bundle:nil];
    if (self) {
        htmlFilePath = [htmlFilePath_ retain];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/transport/help" withError:NULL];
	self.title = NSLocalizedStringFromTable(@"TransportHelp", @"TransportPlugin", nil);
    self.view.backgroundColor = [PCValues backgroundColor1];
    webView.backgroundColor = [UIColor clearColor];
    UIBarButtonItem* doneButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(dismissViewController)];
    self.navigationItem.rightBarButtonItem = doneButton;
    [doneButton release];
    NSError* error = nil;
    NSString* htmlString = [NSString stringWithContentsOfFile:htmlFilePath encoding:NSUTF8StringEncoding error:&error];
    if (!error) {
        [webView loadHTMLString:htmlString baseURL:[NSURL URLWithString:@""]];
    }
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation //<= iOS5
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

/* IBActions */

- (IBAction)dismissViewController {
    if ([self.presentingViewController respondsToSelector:@selector(dismissViewControllerAnimated:completion:)]) { // >= iOS 5.0
        [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
    } else {
        [self.presentingViewController dismissModalViewControllerAnimated:YES];
    }
}

- (void)dealloc {
    webView.delegate = nil;
    [webView stopLoading];
    [htmlFilePath release];
    [super dealloc];
}

@end
