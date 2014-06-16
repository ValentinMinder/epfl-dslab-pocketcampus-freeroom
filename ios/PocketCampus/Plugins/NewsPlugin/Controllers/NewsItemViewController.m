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




//  Created by Loïc Gardiol on 24.12.12.


#import "NewsItemViewController.h"

#import "NewsUtils.h"

#import "AFNetworkReachabilityManager.h"

#import "PCPersistenceManager.h"

#import "PluginSplitViewController.h"

#import "UIActionSheet+Additions.h"

#import "TUSafariActivity.h"

#import "NewsModelAdditions.h"

@interface NewsItemViewController ()<NewsServiceDelegate, UIAlertViewDelegate, UIWebViewDelegate>

@property (nonatomic, strong) UIPopoverController* actionsPopover;
@property (nonatomic, strong) NewsFeedItem* newsFeedItem;
@property (nonatomic, strong) NewsFeedItemContent* newsFeedItemContent;
@property (nonatomic, strong) NewsService* newsService;
@property (nonatomic, strong) NSURL* urlClicked;
@property (nonatomic, strong) AFNetworkReachabilityManager* reachabilityManager;
    
@property (nonatomic, strong) IBOutlet UIWebView* webView;
@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, strong) IBOutlet UILabel* centerMessageLabel;
    
@property (nonatomic) CGFloat lastVerticalOffset;

@end

@implementation NewsItemViewController

- (id)initWithNewsFeedItem:(NewsFeedItem*)newsFeedItem
{
    [PCUtils throwExceptionIfObject:newsFeedItem notKindOfClass:[NewsFeedItem class]];
    self = [super initWithNibName:@"NewsItemView" bundle:nil];
    if (self) {
        self.gaiScreenName = @"/news/item";
        self.newsService = [NewsService sharedInstanceToRetain];
        self.newsFeedItem = newsFeedItem;
        self.title = [PCUtils isIdiomPad] ? self.newsFeedItem.title : nil;
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
    self.navigationItem.rightBarButtonItem.enabled = NO; //enabled only when NewsFeedItemContent loaded (need link property)
    
    self.webView.scalesPageToFit = NO;
    
    [self loadNewsItem];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
}

- (void)loadNewsItem {
    [self.loadingIndicator startAnimating];
    self.centerMessageLabel.hidden = YES;
    self.webView.hidden = YES;
    NewsFeedItemContentRequest* request = [[NewsFeedItemContentRequest alloc] initWithLanguage:[PCUtils userLanguageCode] itemId:self.newsFeedItem.itemId];
    [self.newsService getFeedItemContentForRequest:request delegate:self];
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
    if (!self.newsFeedItemContent) {
        return;
    }
    NSURL* newsItemURL = [NSURL URLWithString:self.newsFeedItemContent.link];
    UIActivity* safariActivity = [TUSafariActivity new];
    UIActivityViewController* viewController = [[UIActivityViewController alloc] initWithActivityItems:@[newsItemURL] applicationActivities:@[safariActivity]];
    viewController.completionHandler = ^(NSString* activityType, BOOL completed) {
        if ([activityType isEqualToString:safariActivity.activityType]) {
            [self trackAction:@"ViewInBrowser" contentInfo:[NSString stringWithFormat:@"%ld-%@", self.newsFeedItem.itemId, self.newsFeedItem.title]];
        }
    };
    [self trackAction:PCGAITrackerActionActionButtonPressed contentInfo:[NSString stringWithFormat:@"%ld-%@", self.newsFeedItem.itemId, self.newsFeedItem.title]];
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
    }
    
    [self.actionButtonSheet toggleFromBarButtonItem:self.navigationItem.rightBarButtonItem animated:YES];
     */
}

#pragma mark - Image management

- (NSString*)imageUrl {
    return [self.newsFeedItem imageUrlStringForSize:CGSizeMake(self.webView.frame.size.width, 240.0) applyDeviceScreenMultiplyingFactor:YES];
}

#pragma mark - NewsServiceDelegate

- (void)getFeedItemContentForRequest:(NewsFeedItemContentRequest *)request didReturn:(NewsFeedItemContentResponse *)response {
    switch (response.statusCode) {
        case NewsStatusCode_OK:
        {
            [self.reachabilityManager stopMonitoring];
            
            self.newsFeedItemContent = response.content;
            self.navigationItem.rightBarButtonItem.enabled = YES; //can now enable action button as we have link
            
            NSString* htmlPath = [[NSBundle mainBundle] pathForResource:@"NewsItem" ofType:@"html"];
            NSError* error = nil;
            NSString* html = [NSString stringWithContentsOfFile:htmlPath encoding:NSUTF8StringEncoding error:&error];
            if (error) {
                [self getFeedItemContentFailedForRequest:request];
                return;
            }
            
            html = [html stringByReplacingOccurrencesOfString:@"$NEWS_ITEM_FEED_NAME$" withString:self.newsFeedItemContent.feedName];
            
            NSDate* date = [NSDate dateWithTimeIntervalSince1970:self.newsFeedItem.date/1000.0];
            static NSDateFormatter* formatter = nil;
            static dispatch_once_t onceToken;
            dispatch_once(&onceToken, ^{
                formatter = [NSDateFormatter new];
                formatter.dateStyle = NSDateFormatterLongStyle;
            });
            
            NSString* dateString = [formatter stringFromDate:date];
            html = [html stringByReplacingOccurrencesOfString:@"$NEW_ITEM_PUB_DATE$" withString:dateString];
            
            html = [html stringByReplacingOccurrencesOfString:@"$NEWS_ITEM_TITLE$" withString:self.newsFeedItemContent.title];
            
            NSString* imageUrl = [self imageUrl];
            if (imageUrl) {
                html = [html stringByReplacingOccurrencesOfString:@"$NEW_ITEM_IMAGE_SRC$" withString:imageUrl];
                html = [html stringByReplacingOccurrencesOfString:@"$NEWS_ITEM_IMAGE_DISPLAY_CSS$" withString:@"inline"];
            } else {
                html = [html stringByReplacingOccurrencesOfString:@"$NEWS_ITEM_IMAGE_DISPLAY_CSS$" withString:@"none"];
            }
            html = [html stringByReplacingOccurrencesOfString:@"$NEWS_ITEM_CONTENT$" withString:self.newsFeedItemContent.contentWithoutMainImage];
            
            html = [NewsUtils htmlReplaceWidthWith100PercentInContent:html ifWidthHeigherThan:self.webView.frame.size.width];
            
            [self.webView loadHTMLString:html baseURL:[NSURL fileURLWithPath:@"/"]];
            self.webView.hidden = NO;
            [self.loadingIndicator stopAnimating];
            break;
        }
        default:
            [self getFeedItemContentFailedForRequest:request];
            break;
    }
    
}

- (void)getFeedItemContentFailedForRequest:(NewsFeedItemContentRequest *)request {
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
                break;
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
