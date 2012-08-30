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

- (id)init
{
    self = [super initWithNibName:@"TransportHelpView" bundle:nil];
    if (self) {
        // Custom initialization
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
    UIBarButtonItem* doneButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(dismissViewController)];
    self.navigationItem.rightBarButtonItem = doneButton;
    [doneButton release];
    NSString* htmlPath = [[NSBundle mainBundle] pathForResource:@"TransportHelp" ofType:@"html"];
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

/* IBActions */

- (IBAction)dismissViewController {
    if ([self.presentingViewController respondsToSelector:@selector(dismissViewControllerAnimated:completion:)]) { // >= iOS 5.0
        [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
    } else {
        [self.presentingViewController dismissModalViewControllerAnimated:YES];
    }
}

@end
