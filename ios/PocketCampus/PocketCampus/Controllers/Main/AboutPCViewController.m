//
//  AboutPCViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.08.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//


#import "AboutPCViewController.h"

#import "PCConfig.h"

@interface AboutPCViewController() <UIWebViewDelegate>

@property (nonatomic, strong) IBOutlet UIWebView* webView;

@property (nonatomic, strong) IBOutlet NSLayoutConstraint* webViewCenterYConstraint;
@property (nonatomic, strong) IBOutlet NSLayoutConstraint* webViewHeightConstraint;

@end

@implementation AboutPCViewController

- (id)init
{
    self = [super initWithNibName:@"AboutPCView" bundle:nil];
    if (self) {
        self.gaiScreenName = @"/dashboard/settings/about";
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
	self.title = NSLocalizedStringFromTable(@"About", @"PocketCampus", nil);
    self.webView.delegate = self;
    self.webView.scrollView.scrollEnabled = NO;
    self.webView.alpha = 0.0;
    NSString* htmlPath = [[NSBundle mainBundle] pathForResource:@"AboutPC" ofType:@"html"];
    NSError* error = nil;
    NSString* htmlString = [NSString stringWithContentsOfFile:htmlPath encoding:NSUTF8StringEncoding error:&error];
    
    htmlString = [htmlString stringByReplacingOccurrencesOfString:@"$PC_VERSION$" withString:[PCUtils appVersion]];
    
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

- (void)showInfos {
    NSString* build = [[[NSBundle mainBundle] infoDictionary] objectForKey:(NSString*)kCFBundleVersionKey];
    BOOL loadedFromBundle = [[PCConfig defaults] boolForKey:PC_CONFIG_LOADED_FROM_BUNDLE_KEY];
    BOOL loadedFromServer = [[PCConfig defaults] boolForKey:PC_CONFIG_LOADED_FROM_SERVER_KEY];
    BOOL loadedFromAppSupport = [[PCConfig defaults] boolForKey:PC_DEV_CONFIG_LOADED_FROM_APP_SUPPORT];
    NSString* serverAddress = [[PCConfig defaults] objectForKey:PC_CONFIG_SERVER_ADDRESS_KEY];
    NSString* serverProtocol = [[PCConfig defaults] objectForKey:PC_CONFIG_SERVER_PROTOCOL_KEY];
    NSString* serverPort = [[PCConfig defaults] objectForKey:PC_CONFIG_SERVER_PORT_KEY];
    NSString* serverUri = [[PCConfig defaults] objectForKey:PC_CONFIG_SERVER_URI_KEY];
    
    NSString* message = [NSString stringWithFormat:@"%@:%@\n\n%@:%d\n%@:%d\n%@:%d\n\n%@:%@\n%@:%@\n%@:%@\n%@:%@",@"Build", build, @"Bundle", loadedFromBundle, @"Server", loadedFromServer, @"AppSupport", loadedFromAppSupport, @"Address", serverAddress, @"Prot", serverProtocol, @"Port", serverPort, @"URI", serverUri];
    UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"PCConfig state" message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
}

/* UIWebViewDelegate delegation */

- (void)webViewDidFinishLoad:(UIWebView *)webView_ {
    CGFloat height = [[self.webView stringByEvaluatingJavaScriptFromString:@"document.body.scrollHeight"] floatValue];
    self.webViewHeightConstraint.constant = height+50.0;
    self.webViewCenterYConstraint.constant = [PCUtils is4inchDevice] ? 15.0 : 5.0;
    [UIView animateWithDuration:0.3 animations:^{
        self.webView.alpha = 1.0;
    }];
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
    self.webView.delegate = nil;
    [self.webView stopLoading];
}

@end
