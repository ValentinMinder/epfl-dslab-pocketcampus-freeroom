/*
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

//  Created by Loïc Gardiol on 14.06.14.

#import "PCWebViewController.h"

@interface PCWebViewController ()<UIWebViewDelegate>

@property (nonatomic, strong, readwrite) IBOutlet UIWebView* webView;

@property (nonatomic, copy) NSURL* originalURL;
@property (nonatomic, copy) NSString* htmlString;
@property (nonatomic, strong) UIBarButtonItem* originalRightBarButtonItem;
@property (nonatomic, strong) UIBarButtonItem* goBackBarButton;
@property (nonatomic, strong) UIBarButtonItem* goForwardBarButton;
@property (nonatomic, strong) UIActivityIndicatorView* loadingIndicator;

@end

@implementation PCWebViewController

#pragma mark - Init

- (instancetype)initWithURL:(NSURL*)url title:(NSString *)title {
    [PCUtils throwExceptionIfObject:url notKindOfClass:[NSURL class]];
    self = [super initWithNibName:@"PCWebView" bundle:nil];
    if (self) {
        self.originalURL = url;
        self.title = title;
        self.automaticallyHandlesInternallyRecognizedURLs = YES; //Default
    }
    return self;
}

- (instancetype)initWithHTMLString:(NSString*)htmlString title:(NSString*)title {
    [PCUtils throwExceptionIfObject:htmlString notKindOfClass:[NSString class]];
    self = [super initWithNibName:@"PCWebView" bundle:nil];
    if (self) {
        self.htmlString = htmlString;
        self.title = title;
        self.automaticallyHandlesInternallyRecognizedURLs = YES; //Default
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    if (self.originalURL) {
        self.webView.scalesPageToFit = YES;
        [self.webView loadRequest:[NSURLRequest requestWithURL:self.originalURL]];
    } else if (self.htmlString) {
        [self.webView loadHTMLString:self.htmlString baseURL:nil];
    } else {
        //should not happen
    }
    self.originalRightBarButtonItem = self.navigationItem.rightBarButtonItem;
    self.loadingIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    self.goBackBarButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"ArrowLeftBarButton"] style:UIBarButtonItemStylePlain target:self.webView action:@selector(goBack)];
    UIBarButtonItem* fixedSpace = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace target:nil action:nil];
    fixedSpace.width = 50.0;
    self.goForwardBarButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"ArrowRightBarButton"] style:UIBarButtonItemStylePlain target:self.webView action:@selector(goForward)];
    UIBarButtonItem* flexibleSpace = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
    UIBarButtonItem* loadingItem = [[UIBarButtonItem alloc] initWithCustomView:self.loadingIndicator];
    self.toolbarItems = @[self.goBackBarButton, fixedSpace, self.goForwardBarButton, flexibleSpace, loadingItem];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.toolbar.translucent = NO;
    self.navigationController.toolbarHidden = NO;
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    self.navigationController.toolbar.translucent = YES;
    self.navigationController.toolbarHidden = YES;
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    if (!self.isDisappearingBecauseOtherPushed) {
        [self.webView loadHTMLString:@"" baseURL:nil]; //prevent major memory leak, see http://stackoverflow.com/a/16514274/1423774
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    [self.webView reload]; //should release a bit of memory
}

#pragma mark - Private

- (void)showOpenInSafariButtonConditionally {
    if (self.webView.request.URL.host) {
        UIBarButtonItem* openInSafari = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"OpenInSafari", @"PocketCampus", nil) style:UIBarButtonItemStylePlain target:self action:@selector(openInSafariTapped)];
        self.navigationItem.rightBarButtonItems = self.originalRightBarButtonItem ? @[self.originalRightBarButtonItem, openInSafari] : @[openInSafari];
    } else {
        self.navigationItem.rightBarButtonItem = self.originalRightBarButtonItem ? self.originalRightBarButtonItem : self.navigationItem.rightBarButtonItem;
    }
}

#pragma mark - Buttons actions

- (void)openInSafariTapped {
    if (!self.webView.request.URL.host) {
        return;
    }
    [[UIApplication sharedApplication] openURL:self.webView.request.URL];
    [self trackAction:@"ViewInBrowser" contentInfo:self.title];
}

#pragma mark - UIWebViewDelegate

- (void)webViewDidStartLoad:(UIWebView *)webView {
    [self.loadingIndicator startAnimating];
    self.goBackBarButton.enabled = webView.canGoBack;
    self.goForwardBarButton.enabled = webView.canGoForward;
    [self showOpenInSafariButtonConditionally];
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    [self.loadingIndicator stopAnimating];
    self.goBackBarButton.enabled = webView.canGoBack;
    self.goForwardBarButton.enabled = webView.canGoForward;
    [self showOpenInSafariButtonConditionally];
}

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    if (self.automaticallyHandlesInternallyRecognizedURLs && ![request.URL isEqual:self.originalURL]) {
        UIViewController* viewController = [[MainController publicController] viewControllerForWebURL:request.URL];
        if (viewController) {
            [self.navigationController pushViewController:viewController animated:YES];
            return NO;
        }
    }
    if (self.shouldLoadRequest) {
        return self.shouldLoadRequest(request, navigationType);
    }
    return YES;
}

#pragma mark - Dealloc

- (void)dealloc {
    self.webView.delegate = nil;
    [self.webView stopLoading];
}

@end
