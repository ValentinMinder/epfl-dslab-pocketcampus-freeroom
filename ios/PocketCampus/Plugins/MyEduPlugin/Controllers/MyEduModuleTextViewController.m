//
//  MyEduModuleTextViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyEduModuleTextViewController.h"

#import "GANTracker.h"

@interface MyEduModuleTextViewController ()

@property (nonatomic, strong) MyEduModule* module;

@end

static int kTextContentSize = 15.0;

@implementation MyEduModuleTextViewController

- (id)initWithMyEduModule:(MyEduModule*)module
{
    self = [super initWithNibName:@"MyEduModuleTextView" bundle:nil];
    if (self) {
        _module = module;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/myedu/sections/modules/text" withError:NULL];
    
    if (!self.module.iTextContent || [self.module.iTextContent isEqualToString:@""]) {
        self.centerMessageLabel.text = NSLocalizedStringFromTable(@"NoTextInModule", @"MyEduPlugin", nil);
        self.centerMessageLabel.hidden = NO;
    } else {
        [self.loadingIndicator startAnimating];
        //NSLog(@"%@", self.module.iTextContent);
        NSString* text = [self.module.iTextContent stringByReplacingOccurrencesOfString:@"\r\n" withString:@"<br>"];
        NSString* textWithStyle = [NSString stringWithFormat:@"<meta charset='utf-8'><meta name='viewport' content='width=device-width; initial-scale=1.0; maximum-scale=1.0;'><style type='text/css'> a { color:#B80000; text-decoration:none; }</style><span style='font-family: Helvetica; font-size: %dpx; word-wrap:break-word; text-align:left;'>%@</span>", kTextContentSize, text];
        [self.webView loadHTMLString:textWithStyle baseURL:[NSURL URLWithString:@"https://myedu.epfl.ch"]];
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - UIWebView

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    [self.loadingIndicator stopAnimating];
    self.webView.hidden = NO;
}

- (void)dealloc
{
    self.webView.delegate = nil;
    [self.webView stopLoading];
}

@end
