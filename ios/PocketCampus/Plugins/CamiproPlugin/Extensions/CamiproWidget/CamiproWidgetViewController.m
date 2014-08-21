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

@property (nonatomic, strong) IBOutlet UILabel* balanceTitleLabel;
@property (nonatomic, strong) IBOutlet UILabel* balanceLabel;
@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, copy) void (^completionHandler)(NCUpdateResult);
@property (nonatomic, strong) CamiproController* camiproController;
@property (nonatomic, strong) CamiproService* camiproService;

@end

@implementation CamiproWidgetViewController

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    self.balanceTitleLabel.text = NSLocalizedStringFromTable(@"Balance", @"CamiproPlugin", nil);
    UITapGestureRecognizer* tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(widgetTapped)];
    [self.view addGestureRecognizer:tapGesture];
    self.preferredContentSize = CGSizeMake(320.0, 50.0);
}

- (UIEdgeInsets)widgetMarginInsetsForProposedMarginInsets:(UIEdgeInsets)defaultMarginInsets {
    defaultMarginInsets.top = 0.0;
    defaultMarginInsets.bottom = 0.0;
    return defaultMarginInsets;
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

#pragma mark - Actions

- (void)widgetTapped {
    [self.extensionContext openURL:[NSURL URLWithString:@"pocketcampus://camipro.plugin.pocketcampus.org"] completionHandler:NULL];
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
            [welf showError];
            welf.camiproController = nil;
            welf.camiproService = nil;
            if (welf.completionHandler) {
                welf.completionHandler(NCUpdateResultFailed);
            }
        } failureBlock:^{
            welf.camiproController = nil;
            welf.camiproService = nil;
            [welf showNeedToLogin];
            if (welf.completionHandler) {
                welf.completionHandler(NCUpdateResultFailed);
            }
        }];
    }
}

- (void)showError {
    [self.loadingIndicator stopAnimating];
    NSMutableParagraphStyle* paragraphStyle = [NSMutableParagraphStyle new];
    paragraphStyle.alignment = NSTextAlignmentLeft;
    NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) attributes:@{NSFontAttributeName:[UIFont boldSystemFontOfSize:16.0], NSForegroundColorAttributeName:[UIColor redColor], NSParagraphStyleAttributeName:paragraphStyle}];
    self.balanceLabel.attributedText = attrString;
}

- (void)showNeedToLogin {
    [self.loadingIndicator stopAnimating];
    NSString* loginRequiredString = NSLocalizedStringFromTable(@"LoginRequired", @"CamiproPlugin", nil);
    NSString* tapToOpenPCString = NSLocalizedStringFromTable(@"TapToOpenPocketCampus", @"CamiproPlugin", nil);
    NSString* finalString = [NSString stringWithFormat:@"%@\n%@", loginRequiredString, tapToOpenPCString];
    
    NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:finalString];
    
    NSMutableParagraphStyle* paragraphStyle = [NSMutableParagraphStyle new];
    paragraphStyle.alignment = NSTextAlignmentLeft;
    
    [attrString setAttributes:@{NSParagraphStyleAttributeName:paragraphStyle} range:NSMakeRange(0, finalString.length)];
    [attrString addAttributes:@{NSFontAttributeName:[UIFont boldSystemFontOfSize:14.0], NSForegroundColorAttributeName:[UIColor redColor]} range:[finalString rangeOfString:loginRequiredString]];
    [attrString addAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:11.0]} range:[finalString rangeOfString:tapToOpenPCString]];
    
    self.balanceLabel.attributedText = attrString;
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
            self.camiproController = nil;
            self.camiproService = nil;
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
    [self showError];
    self.camiproController = nil;
    self.camiproService = nil;
    if (self.completionHandler) {
        self.completionHandler(NCUpdateResultFailed);
    }
}

- (void)serviceConnectionToServerFailed {
    [self.loadingIndicator stopAnimating];
    NSMutableParagraphStyle* paragraphStyle = [NSMutableParagraphStyle new];
    paragraphStyle.alignment = NSTextAlignmentLeft;
    NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutShort", @"PocketCampus", nil) attributes:@{NSFontAttributeName:[UIFont boldSystemFontOfSize:16.0], NSForegroundColorAttributeName:[UIColor redColor], NSParagraphStyleAttributeName:paragraphStyle}];
    self.balanceLabel.attributedText = attrString;
    self.camiproController = nil;
    self.camiproService = nil;
    if (self.completionHandler) {
        self.completionHandler(NCUpdateResultFailed);
    }
}

#pragma mark - Dealloc

- (void)dealloc {
    [self.camiproService cancelOperationsForDelegate:self];
}

@end
