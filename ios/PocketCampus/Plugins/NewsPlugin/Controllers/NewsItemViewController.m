//
//  NewsItemViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "NewsItemViewController.h"

#import "NewsUtils.h"

#import "AFNetworkReachabilityManager.h"

#import "PCObjectArchiver.h"

#import "PluginSplitViewController.h"

#import "UIActionSheet+Additions.h"

#import "TUSafariActivity.h"

@interface NewsItemViewController ()<NewsServiceDelegate, UIAlertViewDelegate, UIWebViewDelegate>

@property (nonatomic, strong) UIImage* image;
@property (nonatomic, strong) UIPopoverController* actionsPopover;
@property (nonatomic, strong) NewsItem* newsItem;
@property (nonatomic, strong) NewsService* newsService;
@property (nonatomic, strong) NSURL* urlClicked;
@property (nonatomic, strong) AFNetworkReachabilityManager* reachabilityManager;
    
@property (nonatomic, strong) IBOutlet UIWebView* webView;
@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, strong) IBOutlet UILabel* centerMessageLabel;
    
@property (nonatomic) CGFloat lastVerticalOffset;

@end

@implementation NewsItemViewController

- (id)initWithNewsItem:(NewsItem*)newsItem cachedImageOrNil:(UIImage*)image
{
    self = [super initWithNibName:@"NewsItemView" bundle:nil];
    if (self) {
        self.newsService = [NewsService sharedInstanceToRetain];
        self.newsItem = newsItem;
        self.image = image;
        self.title = [PCUtils isIdiomPad] ? self.newsItem.title : nil;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    if (self.splitViewController) {
        self.navigationItem.leftBarButtonItem = [(PluginSplitViewController*)(self.splitViewController) toggleMasterViewBarButtonItem];
    }
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAction target:self action:@selector(actionButtonPressed)];
    
    self.webView.scalesPageToFit = NO;
    
    [self saveImageToDisk];
    
    [self loadNewsItem];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[PCGAITracker sharedTracker] trackScreenWithName:@"/news/item"];
}

- (void)loadNewsItem {
    [self.loadingIndicator startAnimating];
    self.centerMessageLabel.hidden = YES;
    self.webView.hidden = YES;
    [self.newsService getNewsItemContentForId:self.newsItem.newsItemId delegate:self];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskAllButUpsideDown;
}

#pragma mark - Actions

- (void)actionButtonPressed {
    NSURL* newsItemURL = [NSURL URLWithString:self.newsItem.link];
    UIActivityViewController* viewController = [[UIActivityViewController alloc] initWithActivityItems:@[newsItemURL] applicationActivities:@[[TUSafariActivity new]]];
    
    if (self.splitViewController) {
        if (!self.actionsPopover) {
            self.actionsPopover = [[UIPopoverController alloc] initWithContentViewController:viewController];
            self.actionsPopover.popoverContentSize = viewController.preferredContentSize;
        }
        [self.actionsPopover togglePopoverFromBarButtonItem:self.navigationItem.rightBarButtonItem permittedArrowDirections:UIPopoverArrowDirectionUp animated:YES];
    } else {
        [self presentViewController:viewController animated:YES completion:NULL];
    }
    
    /*
    if (!self.actionButtonSheet) {
        self.actionButtonSheet = [[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:nil otherButtonTitles:NSLocalizedStringFromTable(@"OpenInSafari", @"NewsPlugin", nil), nil];
        self.actionButtonSheet.accessibilityIdentifier = @"NewsItemActionSheet";
    }
    
    [self.actionButtonSheet toggleFromBarButtonItem:self.navigationItem.rightBarButtonItem animated:YES];
     */
}

#pragma mark - Image management

- (NSString*)pathForImage {
    NSString* key = [NSString stringWithFormat:@"newsItemImage-%u", [self.newsItem.imageUrl hash]];
    return [PCObjectArchiver pathForKey:key pluginName:@"news" customFileExtension:[self.newsItem.imageUrl pathExtension] isCache:YES];
}

- (void)saveImageToDisk {
    if (!self.image) {
        return;
    }
    if ([[NSFileManager defaultManager] fileExistsAtPath:[self pathForImage]]) {
        return;
    }
    NSData* jpgData = UIImageJPEGRepresentation(self.image, 1.0);
    [jpgData writeToFile:[self pathForImage] atomically:NO];
}

#pragma mark - NewsServiceDelegate


- (void)newsItemContentForId:(int64_t)newsItemId didReturn:(NSString *)content {
    [self.reachabilityManager stopMonitoring];
    NSString* htmlPath = [[NSBundle mainBundle] pathForResource:@"NewsItem" ofType:@"html"];
    NSError* error = nil;
    NSString* html = [NSString stringWithContentsOfFile:htmlPath encoding:NSUTF8StringEncoding error:&error];
    if (error) {
        [self error];
        return;
    }
    
    html = [html stringByReplacingOccurrencesOfString:@"$NEWS_ITEM_FEED_NAME$" withString:self.newsItem.feed];
    
    NSDate* date = [NSDate dateWithTimeIntervalSince1970:self.newsItem.pubDate/1000.0];
    static NSDateFormatter* formatter = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        formatter = [NSDateFormatter new];
        formatter.dateStyle = NSDateFormatterLongStyle;
    });
    
    NSString* dateString = [formatter stringFromDate:date];
    html = [html stringByReplacingOccurrencesOfString:@"$NEW_ITEM_PUB_DATE$" withString:dateString];
    
    html = [html stringByReplacingOccurrencesOfString:@"$NEWS_ITEM_TITLE$" withString:self.newsItem.title];
    
    if (self.newsItem.imageUrl) {
        NSString* imageSrc = self.newsItem.imageUrl;
        NSString* path = [self pathForImage];
        if ([[NSFileManager defaultManager] fileExistsAtPath:path]) { //then image was saved to disk (in viewDidLoad)
            imageSrc = path;
        }
        html = [html stringByReplacingOccurrencesOfString:@"$NEW_ITEM_IMAGE_SRC$" withString:imageSrc];
        html = [html stringByReplacingOccurrencesOfString:@"$NEWS_ITEM_IMAGE_DISPLAY_CSS$" withString:@"inline"];
    } else {
        html = [html stringByReplacingOccurrencesOfString:@"$NEWS_ITEM_IMAGE_DISPLAY_CSS$" withString:@"none"];
    }
    html = [html stringByReplacingOccurrencesOfString:@"$NEWS_ITEM_CONTENT$" withString:content];
    
    html = [NewsUtils htmlReplaceWidthWith100PercentInContent:html ifWidthHeigherThan:self.webView.frame.size.width];
    
    [self.webView loadHTMLString:html baseURL:[NSURL fileURLWithPath:@"/"]];
    self.webView.hidden = NO;
    [self.loadingIndicator stopAnimating];
}

- (void)newsItemContentFailedForId:(int64_t)newsItemId {
    [self error];
}

- (void)error {
    self.webView.hidden = YES;
    [self.loadingIndicator stopAnimating];
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil);
    self.centerMessageLabel.hidden = NO;
}

- (void)serviceConnectionToServerFailed {
    self.webView.hidden = YES;
    [self.loadingIndicator stopAnimating];
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
    self.centerMessageLabel.hidden = NO;
    if (!self.reachabilityManager) {
        NewsItemViewController* weakSelf __weak = self;
        self.reachabilityManager = [AFNetworkReachabilityManager managerForDomain:@"google.com"];
        [self.reachabilityManager setReachabilityStatusChangeBlock:^(AFNetworkReachabilityStatus status) {
            if (status > 0) { //means internet reachable
                [weakSelf loadNewsItem];
            }
        }];
        [self.reachabilityManager startMonitoring];
    }
}

#pragma mark - UIWebViewDelegate

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    if (navigationType == UIWebViewNavigationTypeLinkClicked) {
        self.urlClicked = request.URL;
        NSString* title = self.urlClicked.host;
        if (self.urlClicked.path.length > 1) { //empty path is "/"
            title = [title stringByAppendingString:@"/..."];
        }
        UIAlertView* alertView = [[UIAlertView alloc] initWithTitle:title message:NSLocalizedStringFromTable(@"ClickLinkLeaveApplicationExplanation", @"NewsPlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) otherButtonTitles:@"OK", nil];
        [alertView show];
        return NO;
    }
    return YES;
}

#pragma mark - UIAlertViewDelegate

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (self.urlClicked) {
        switch (buttonIndex) {
            case 0: //cancel
                //Nothing to do
                break;
            case 1: //OK
                [[UIApplication sharedApplication] openURL:self.urlClicked];
            default:
                break;
        }
        self.urlClicked = nil;
    }
}

#pragma mark - dealloc

- (void)dealloc {
    [self.reachabilityManager stopMonitoring];
    self.webView.delegate = nil;
    [self.webView stopLoading];
    [self.newsService cancelOperationsForDelegate:self];
}

@end
