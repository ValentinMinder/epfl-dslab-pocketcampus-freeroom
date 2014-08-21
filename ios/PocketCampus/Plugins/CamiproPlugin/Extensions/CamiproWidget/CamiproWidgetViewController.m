//
//  TodayViewController.m
//  Camipro EPFL
//
//  Created by Loïc Gardiol on 04.06.14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

#import "CamiproWidgetViewController.h"
#import <NotificationCenter/NotificationCenter.h>

#import "CamiproService.h"

#import "CamiproController.h"

@interface CamiproWidgetViewController () <NCWidgetProviding, CamiproServiceDelegate>

@property (nonatomic, strong) IBOutlet UILabel* balanceLabel;
@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, copy) void (^completionHandler)(NCUpdateResult);
@property (nonatomic, strong) CamiproController* camiproController;
@property (nonatomic, strong) CamiproService* camiproService;

@end

@implementation CamiproWidgetViewController

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.preferredContentSize = CGSizeMake(300.0, 38.0);
    [self widgetPerformUpdateWithCompletionHandler:NULL];
}

- (void)widgetPerformUpdateWithCompletionHandler:(void (^)(NCUpdateResult))completionHandler {
    // Perform any setup necessary in order to update the view.
    
    // If an error is encoutered, use NCUpdateResultFailed
    // If there's no update required, use NCUpdateResultNoData
    // If there's an update, use NCUpdateResultNewData
    
    self.completionHandler = completionHandler;
    
    [self.camiproService cancelOperationsForDelegate:self];
    if (!self.camiproService) {
        self.camiproService = [CamiproService sharedInstanceToRetain];
    }

    [self startBalanceAndTransactionsRequest];

    self.balanceLabel.text = nil;
    [self.loadingIndicator startAnimating];
    
}

#pragma mark - Private

- (SessionId*)buildSessionIdFromCamiproSession:(CamiproSession*)camiproSession {
    return [[SessionId alloc] initWithTos:0 camiproCookie:camiproSession.camiproCookie];
}

- (void)startBalanceAndTransactionsRequest {
    VoidBlock successBlock = ^{
        CamiproRequest* request = [[CamiproRequest alloc] initWithISessionId:[self buildSessionIdFromCamiproSession:self.camiproService.camiproSession] iLanguage:[[NSLocale currentLocale] objectForKey:NSLocaleLanguageCode]];
        [self.camiproService getBalanceAndTransactions:request delegate:self];
    };
    if (self.camiproService.camiproSession) {
        successBlock();
    } else {
        if (!self.camiproController) {
            self.camiproController = [CamiproController sharedInstanceToRetain];
        }
        __weak __typeof(self) welf = self;
        [self.camiproController removeLoginObserver:self];
        [self.camiproController addLoginObserver:self successBlock:successBlock userCancelledBlock:^{
            [welf.loadingIndicator stopAnimating];
            welf.balanceLabel.text = NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil);
            if (welf.completionHandler) {
                welf.completionHandler(NCUpdateResultFailed);
            }
        } failureBlock:^{
            [welf.loadingIndicator stopAnimating];
            welf.balanceLabel.text = NSLocalizedStringFromTable(@"TapToOpenAppAndLogin", @"CamiproPlugin", nil);
            if (welf.completionHandler) {
                welf.completionHandler(NCUpdateResultFailed);
            }
        }];
    }
}

#pragma mark - CamiproServiceDelegate

- (void)getBalanceAndTransactionsForCamiproRequest:(CamiproRequest *)camiproRequest didReturn:(BalanceAndTransactions *)balanceAndTransactions {
    [self.loadingIndicator stopAnimating];
    switch (balanceAndTransactions.iStatus) {
        case 407:
            CLSNSLog(@"-> User session has expired. Trying to login...");
            [self.loadingIndicator startAnimating];
            [self.camiproService deleteCamiproSession];
            [self startBalanceAndTransactionsRequest];
            break;
        case 200:
            self.balanceLabel.text = [NSString stringWithFormat:@"CHF %.2lf", balanceAndTransactions.iBalance];
            if (self.completionHandler) {
                self.completionHandler(NCUpdateResultNewData);
            }
            break;
        default:
            [self getBalanceAndTransactionsFailedForCamiproRequest:camiproRequest];
            break;
    }
}

- (void)getBalanceAndTransactionsFailedForCamiproRequest:(CamiproRequest *)camiproRequest {
    [self.loadingIndicator stopAnimating];
    self.balanceLabel.text = NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil);
    if (self.completionHandler) {
        self.completionHandler(NCUpdateResultFailed);
    }
}

- (void)serviceConnectionToServerFailed {
    [self.loadingIndicator stopAnimating];
    self.balanceLabel.text = NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil);
    if (self.completionHandler) {
        self.completionHandler(NCUpdateResultFailed);
    }
}

#pragma mark - Dealloc

- (void)dealloc {
    [self.camiproService cancelOperationsForDelegate:self];
}

@end
