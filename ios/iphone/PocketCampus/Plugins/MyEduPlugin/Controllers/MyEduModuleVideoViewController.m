//
//  MyEduModuleVideoViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyEduModuleVideoViewController.h"

#import "PCValues.h"

@interface MyEduModuleVideoViewController ()

@property (nonatomic, strong) MyEduModule* module;

@end

@implementation MyEduModuleVideoViewController

- (id)initWithMyEduModule:(MyEduModule*)module
{
    self = [super initWithNibName:@"MyEduModuleVideoView" bundle:nil];
    if (self) {
        _module = module;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    if (!self.module.iVideoURL || [self.module.iVideoURL isEqualToString:@""]) {
        self.centerMessageLabel.text = NSLocalizedStringFromTable(@"NoVideoInModule", @"MyEduPlugin", nil);
    } else {
        [self.loadingIndicator startAnimating];
        self.webView.layer.masksToBounds = NO;
        self.webView.layer.shadowOffset = CGSizeMake(0.0, 2.00);
        self.webView.layer.shadowRadius = 3;
        self.webView.layer.shadowOpacity = 0.5;
        self.webView.allowsInlineMediaPlayback = YES;
        self.webView.scrollView.bounces = NO;
        NSString* htmlString = [MyEduService videoHTMLCodeForMyEduModule:self.module videoWidth:self.webView.frame.size.width-16.0 videoHeight:self.webView.frame.size.height-16.0];
        if (htmlString) {
            [self.webView loadHTMLString:htmlString baseURL:[NSURL URLWithString:@"http://myedu.epfl.ch/"]];
            self.centerMessageLabel.text = NSLocalizedStringFromTable(@"LoadingVideo", @"MyEduPlugin", nil);
        } else {
            [self.loadingIndicator stopAnimating];
            self.centerMessageLabel.text = NSLocalizedStringFromTable(@"VideoNotSupported", @"MyEduPlugin", nil);
        }
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - UIWebViewDelegate

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    self.webView.alpha = 0.0;
    self.webView.hidden = NO;
    [UIView animateWithDuration:0.3 animations:^{
        self.webView.alpha = 1.0;
        self.centerMessageLabel.alpha = 0.0;
        self.loadingIndicator.alpha = 0.0;
    } completion:^(BOOL finished) {
        [self.loadingIndicator stopAnimating];
        self.loadingIndicator.alpha = 1.0;
        self.centerMessageLabel.hidden = YES;
        self.centerMessageLabel.alpha = 1.0;
    }];
}

#pragma mark - dealloc

- (void)dealloc
{
    self.webView.delegate = nil;
    [self.webView stopLoading];
}

@end
