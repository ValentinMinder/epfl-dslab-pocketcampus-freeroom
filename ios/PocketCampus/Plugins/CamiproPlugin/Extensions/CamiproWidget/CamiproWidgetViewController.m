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

@interface CamiproWidgetViewController () <NCWidgetProviding, CamiproServiceDelegate>

@property (nonatomic, strong) IBOutlet UILabel* balanceLabel;
@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* loadingIndicator;

@property (nonatomic, strong) CamiproService* camiproService;

@property (nonatomic, copy) void (^completionHandler)(NCUpdateResult);

@end

@implementation CamiproWidgetViewController

- (void)widgetPerformUpdateWithCompletionHandler:(void (^)(NCUpdateResult))completionHandler {
    // Perform any setup necessary in order to update the view.
    
    // If an error is encoutered, use NCUpdateResultFailed
    // If there's no update required, use NCUpdateResultNoData
    // If there's an update, use NCUpdateResultNewData
    
    self.completionHandler = completionHandler;
    
    if (!self.camiproService) {
        self.camiproService = [CamiproService sharedInstanceToRetain];
    }

    SessionId* session = [[SessionId alloc] initWithTos:0 camiproCookie:self.camiproService.camiproSession.camiproCookie];
    CamiproRequest* request = [[CamiproRequest alloc] initWithISessionId:session iLanguage:@"fr"];
    [self.camiproService getBalanceAndTransactions:request delegate:self];
}

#pragma mark - CamiproServiceDelegate

- (void)getBalanceAndTransactionsForCamiproRequest:(CamiproRequest *)camiproRequest didReturn:(BalanceAndTransactions *)balanceAndTransactions {
    [self.loadingIndicator stopAnimating];
    self.balanceLabel.text = [NSString stringWithFormat:@"CHF %.2lf", balanceAndTransactions.iBalance];
}

- (void)getBalanceAndTransactionsFailedForCamiproRequest:(CamiproRequest *)camiproRequest {
    
}

- (void)serviceConnectionToServerFailed {
    
}

@end
