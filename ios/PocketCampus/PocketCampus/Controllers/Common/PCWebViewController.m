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

@property (nonatomic, strong) IBOutlet UIWebView* webView;

@property (nonatomic, strong) NSURL* originalURL;

@end

@implementation PCWebViewController

#pragma mark - Init

- (instancetype)initWithURL:(NSURL*)url title:(NSString *)title {
    [PCUtils throwExceptionIfObject:url notKindOfClass:[NSURL class]];
    self = [super initWithNibName:@"PCWebView" bundle:nil];
    if (self) {
        self.originalURL = url;
        self.title = title;
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    self.webView.scalesPageToFit = YES;
    [self.webView loadRequest:[NSURLRequest requestWithURL:self.originalURL]];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"OpenInSafari", @"PocketCampus", nil) style:UIBarButtonItemStylePlain target:self action:@selector(openInSafariTapped)];
}

#pragma mark - Buttons actions

- (void)openInSafariTapped {
    [[UIApplication sharedApplication] openURL:self.webView.request.URL];
}

#pragma mark - UIWebViewDelegate

//nothing

#pragma mark - Dealloc

- (void)dealloc {
    self.webView.delegate = nil;
    [self.webView stopLoading];
}

@end
