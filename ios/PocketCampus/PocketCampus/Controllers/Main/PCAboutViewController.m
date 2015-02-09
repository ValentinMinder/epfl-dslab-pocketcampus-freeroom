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

//  Created by Loïc Gardiol on 07.08.12.

#import "PCAboutViewController.h"


@interface PCAboutViewController() <UIWebViewDelegate>

@property (nonatomic, strong) IBOutlet UIWebView* webView;

@end

@implementation PCAboutViewController

#pragma mark - Init

- (instancetype)init
{
    self = [super initWithNibName:@"PCAboutView" bundle:nil];
    if (self) {
        self.gaiScreenName = @"/dashboard/settings/about";
        self.automaticallyAdjustsScrollViewInsets = YES;
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
	self.title = NSLocalizedStringFromTable(@"About", @"PocketCampus", nil);
    self.webView.delegate = self;
    //self.webView.scrollView.scrollEnabled = NO;
    self.webView.alpha = 0.0;
    NSString* htmlPath = [[NSBundle mainBundle] pathForResource:@"PCAbout" ofType:@"html"];
    NSError* error = nil;
    NSString* htmlString = [NSString stringWithContentsOfFile:htmlPath encoding:NSUTF8StringEncoding error:&error];
    
    htmlString = [htmlString stringByReplacingOccurrencesOfString:@"$PC_VERSION$" withString:[PCUtils appVersion]];
    
    htmlString = [htmlString stringByReplacingOccurrencesOfString:@"$INSTITUTION_LOGO_PATH$" withString:[PCUtils pathForImageResource:@"InstitutionLogo"]];
    
    if (!error) {
        [self.webView loadHTMLString:htmlString baseURL:[NSURL URLWithString:@""]];
    }
    
    UITapGestureRecognizer* gestureRec = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(showInfos)];
    gestureRec.numberOfTapsRequired = 3;
    gestureRec.numberOfTouchesRequired = 2;
    [self.webView addGestureRecognizer:gestureRec];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return [PCUtils isIdiomPad] ? UIInterfaceOrientationMaskAll : UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Private

- (void)showInfos {
    NSString* build = [[[NSBundle mainBundle] infoDictionary] objectForKey:(NSString*)kCFBundleVersionKey];
    BOOL loadedFromBundle = [[PCConfig defaults] boolForKey:PC_CONFIG_LOADED_FROM_BUNDLE_KEY];
    BOOL loadedFromPersistedServer = [[PCConfig defaults] boolForKey:PC_CONFIG_LOADED_FROM_PERSISTED_SERVER_CONFIG_KEY];
    BOOL loadedFromServer = [[PCConfig defaults] boolForKey:PC_CONFIG_LOADED_FROM_SERVER_KEY];
    BOOL loadedFromAppSupport = [[PCConfig defaults] boolForKey:PC_DEV_CONFIG_LOADED_FROM_APP_SUPPORT];
    NSString* serverAddress = [[PCConfig defaults] objectForKey:PC_CONFIG_SERVER_ADDRESS_KEY];
    NSString* serverProtocol = [[PCConfig defaults] objectForKey:PC_CONFIG_SERVER_PROTOCOL_KEY];
    NSString* serverPort = [[PCConfig defaults] objectForKey:PC_CONFIG_SERVER_PORT_KEY];
    NSString* serverUri = [[PCConfig defaults] objectForKey:PC_CONFIG_SERVER_URI_KEY];
    
    NSString* message = [NSString stringWithFormat:@"%@:%@\n\n%@:%d\n%@:%d\n%@:%d\n%@:%d\n\n%@:%@\n%@:%@\n%@:%@\n%@:%@", @"Build", build, @"Bundle", loadedFromBundle, @"Persisted server", loadedFromPersistedServer, @"Server", loadedFromServer, @"AppSupport", loadedFromAppSupport, @"Address", serverAddress, @"Prot", serverProtocol, @"Port", serverPort, @"URI", serverUri];
    UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"PCConfig state" message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
}

#pragma mark - UIWebViewDelegate

- (void)webViewDidFinishLoad:(UIWebView *)webView_ {
    [UIView animateWithDuration:0.3 animations:^{
        self.webView.alpha = 1.0;
    }];
}

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    if (navigationType == UIWebViewNavigationTypeLinkClicked) {
        UIViewController* viewController = [[MainController publicController] viewControllerForWebURL:request.URL];
        if (viewController) {
            [self.navigationController pushViewController:viewController animated:YES];
        } else {
            [[UIApplication sharedApplication] openURL:request.URL];
        }
        return NO;
    }
    return YES;
}

#pragma mark - Dealloc

- (void)dealloc
{
    self.webView.delegate = nil;
    [self.webView stopLoading];
}

@end
