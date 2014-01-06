//
//  HelpViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 26.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "TransportHelpViewController.h"

@interface TransportHelpViewController ()

@property (nonatomic, assign) IBOutlet UIWebView* webView;

@end

@implementation TransportHelpViewController

#pragma mark - Init

- (id)init
{
    self = [super initWithNibName:@"TransportHelpView" bundle:nil];
    if (self) {
        self.title = NSLocalizedStringFromTable(@"TransportHelp", @"TransportPlugin", nil);
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(donePressed)];
    self.navigationItem.rightBarButtonItem.style = UIBarButtonItemStylePlain;
    NSError* error = nil;
    NSString* htmlString = [NSString stringWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"TransportHelp" ofType:@"html"] encoding:NSUTF8StringEncoding error:&error];
    if (!error) {
        [self.webView loadHTMLString:htmlString baseURL:nil];
    }
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Actions

- (void)donePressed {
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

#pragma mark - Dealloc

- (void)dealloc {
    self.webView.delegate = nil;
    [self.webView stopLoading];
}

@end
