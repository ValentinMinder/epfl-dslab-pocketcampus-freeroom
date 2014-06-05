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

@property (nonatomic, copy) void (^completionHandler)(NCUpdateResult);

@end

@implementation CamiproWidgetViewController

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self widgetPerformUpdateWithCompletionHandler:NULL];
}

- (void)widgetPerformUpdateWithCompletionHandler:(void (^)(NCUpdateResult))completionHandler {
    // Perform any setup necessary in order to update the view.
    
    // If an error is encoutered, use NCUpdateResultFailed
    // If there's no update required, use NCUpdateResultNoData
    // If there's an update, use NCUpdateResultNewData
    
    /*self.completionHandler = completionHandler;
    
    if (!self.camiproService) {
        self.camiproService = [CamiproService sharedInstanceToRetain];
    }

    SessionId* session = [[SessionId alloc] initWithTos:0 camiproCookie:self.camiproService.camiproSession.camiproCookie];
    CamiproRequest* request = [[CamiproRequest alloc] initWithISessionId:session iLanguage:@"fr"];
    [self.camiproService getBalanceAndTransactions:request delegate:self];*/
    self.balanceLabel.text = [NSString stringWithFormat:@"CHF %.2lf", 53.00];
    if (completionHandler) {
        completionHandler(NCUpdateResultNewData);
    }
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
