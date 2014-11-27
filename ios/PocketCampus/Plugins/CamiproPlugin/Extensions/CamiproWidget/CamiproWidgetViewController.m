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

//  Created by Loïc Gardiol on 04.06.14.

#import "CamiproWidgetViewController.h"
#import <NotificationCenter/NotificationCenter.h>

#import "CamiproService.h"

#import "CamiproController.h"

@interface CamiproWidgetViewController () <NCWidgetProviding, CamiproServiceDelegate>

@property (nonatomic, strong) IBOutlet UILabel* balanceTitleLabel;
@property (nonatomic, strong) IBOutlet UILabel* balanceLabel;
@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, copy) void (^completionHandler)(NCUpdateResult);
@property (nonatomic, strong) CamiproController* camiproController;
@property (nonatomic, strong) CamiproService* camiproService;
@property (nonatomic, strong) NSTimer* refreshTimer;

@end

@implementation CamiproWidgetViewController

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    self.gaiScreenName = @"/camipro/widget";

    self.balanceTitleLabel.text = NSLocalizedStringFromTable(@"Balance", @"CamiproPlugin", nil);
    UITapGestureRecognizer* tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(widgetTapped)];
    [self.view addGestureRecognizer:tapGesture];
    self.preferredContentSize = CGSizeMake(320.0, 50.0);
    
    //iOS 8 weirdly sometimes does not call widgetPerformUpdateWithCompletionHandler: after viewDidLoad. So we want to make sure refresh is triggered.
    self.refreshTimer = [NSTimer scheduledTimerWithTimeInterval:0.1 target:self selector:@selector(refresh) userInfo:nil repeats:NO];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
}

- (UIEdgeInsets)widgetMarginInsetsForProposedMarginInsets:(UIEdgeInsets)defaultMarginInsets {
    defaultMarginInsets.top = 0.0;
    defaultMarginInsets.bottom = 0.0;
    if ([PCUtils isIdiomPad]) {
        defaultMarginInsets.left += 9.0;
    }
    return defaultMarginInsets;
}

- (void)widgetPerformUpdateWithCompletionHandler:(void (^)(NCUpdateResult))completionHandler {
    // Perform any setup necessary in order to update the view.
    // If an error is encoutered, use NCUpdateResultFailed
    // If there's no update required, use NCUpdateResultNoData
    // If there's an update, use NCUpdateResultNewData
    self.completionHandler = completionHandler;
    [self refresh];
}

#pragma mark - Actions

- (void)widgetTapped {
    [self trackAction:@"OpenApp"];
    [self.extensionContext openURL:[NSURL URLWithString:@"pocketcampus://camipro.plugin.pocketcampus.org"] completionHandler:NULL];
}

#pragma mark - Private

- (void)refresh {
    [self.refreshTimer invalidate];
    self.refreshTimer = nil;
    
    self.preferredContentSize = CGSizeMake(320.0, 50.0);
    
    if (![[PCConfig defaults] boolForKey:PC_CONFIG_LOADED_FROM_BUNDLE_KEY]) {
        [self showNeedToLogin];
        return;
    }
    
    [self.camiproService cancelOperationsForDelegate:self];
    if (!self.camiproService) {
        self.camiproService = [CamiproService sharedInstanceToRetain];
    } 
    
    [self startBalanceAndTransactionsRequest];
    
    self.balanceLabel.text = nil;
    [self.loadingIndicator startAnimating];
}

- (SessionId*)buildSessionIdFromCamiproSession:(CamiproSession*)camiproSession {
    return [[SessionId alloc] initWithTos:0 camiproCookie:camiproSession.camiproCookie];
}

- (void)startBalanceAndTransactionsRequest {
    __weak __typeof(self) welf = self;
    VoidBlock successBlock = ^{
        CamiproRequest* request = [[CamiproRequest alloc] initWithISessionId:[welf buildSessionIdFromCamiproSession:welf.camiproService.camiproSession] iLanguage:[[NSLocale currentLocale] objectForKey:NSLocaleLanguageCode]];
        [welf.camiproService getBalanceAndTransactions:request delegate:welf];
    };
    if (self.camiproService.camiproSession) {
        successBlock();
    } else {
        if (!self.camiproController) {
            self.camiproController = [CamiproController sharedInstanceToRetain];
        }
        [self.camiproController removeLoginObserver:self];
        [self.camiproController addLoginObserver:self successBlock:successBlock userCancelledBlock:^{
            [welf showError];
            welf.camiproController = nil;
            welf.camiproService = nil;
            if (welf.completionHandler) {
                welf.completionHandler(NCUpdateResultFailed);
            }
        } failureBlock:^(NSError *error) {
            welf.camiproController = nil;
            welf.camiproService = nil;
            if (error.code == kAuthenticationErrorCodeCouldNotAskForCredentials) {
                [welf showNeedToLogin];
            } else {
                [welf showError];
            }
            if (welf.completionHandler) {
                welf.completionHandler(NCUpdateResultFailed);
            }
        }];
    }
}

- (void)showError {
    [self.loadingIndicator stopAnimating];
    NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) attributes:@{NSFontAttributeName:[UIFont boldSystemFontOfSize:16.0], NSForegroundColorAttributeName:[UIColor redColor]}];
    self.balanceLabel.attributedText = attrString;
}

- (void)showNeedToLogin {
    [self.loadingIndicator stopAnimating];
    NSString* loginRequiredString = NSLocalizedStringFromTable(@"LoginRequired", @"CamiproPlugin", nil);
    NSString* tapToOpenPCString = NSLocalizedStringFromTable(@"TapToOpenPocketCampus", @"CamiproPlugin", nil);
    NSString* finalString = [NSString stringWithFormat:@"%@\n%@", loginRequiredString, tapToOpenPCString];
    
    NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:finalString];
    
    [attrString addAttributes:@{NSFontAttributeName:[UIFont boldSystemFontOfSize:14.0], NSForegroundColorAttributeName:[UIColor redColor]} range:[finalString rangeOfString:loginRequiredString]];
    [attrString addAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:11.0]} range:[finalString rangeOfString:tapToOpenPCString]];
    
    self.balanceLabel.attributedText = attrString;
}

#pragma mark - CamiproServiceDelegate

- (void)getBalanceAndTransactionsForCamiproRequest:(CamiproRequest *)camiproRequest didReturn:(BalanceAndTransactions *)balanceAndTransactions {
    [self.loadingIndicator stopAnimating];
    switch (balanceAndTransactions.iStatus) {
        case 407:
            [self.loadingIndicator startAnimating];
            [self.camiproService deleteCamiproSession];
            [self startBalanceAndTransactionsRequest];
            break;
        case 200:
            self.balanceLabel.text = [NSString stringWithFormat:@"CHF %.2lf", balanceAndTransactions.iBalance];
            self.camiproController = nil;
            self.camiproService = nil;
            if (self.completionHandler) {
                self.completionHandler(NCUpdateResultNewData);
            }
            self.completionHandler = nil;
            break;
        default:
            [self getBalanceAndTransactionsFailedForCamiproRequest:camiproRequest];
            break;
    }
}

- (void)getBalanceAndTransactionsFailedForCamiproRequest:(CamiproRequest *)camiproRequest {
    [self showError];
    self.camiproController = nil;
    self.camiproService = nil;
    if (self.completionHandler) {
        self.completionHandler(NCUpdateResultFailed);
    }
    self.completionHandler = nil;
}

- (void)serviceConnectionToServerFailed {
    [self.loadingIndicator stopAnimating];
    NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutShort", @"PocketCampus", nil) attributes:@{NSFontAttributeName:[UIFont boldSystemFontOfSize:16.0], NSForegroundColorAttributeName:[UIColor redColor]}];
    self.balanceLabel.attributedText = attrString;
    self.camiproController = nil;
    self.camiproService = nil;
    if (self.completionHandler) {
        self.completionHandler(NCUpdateResultFailed);
    }
    self.completionHandler = nil;
}

#pragma mark - Dealloc

- (void)dealloc {
    [self.refreshTimer invalidate];
    [self.camiproService cancelOperationsForDelegate:self];
}

@end
