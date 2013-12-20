//
//  CamiproViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 17.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "CamiproViewController.h"

#import "PCTableViewSectionHeader.h"

#import "PCObjectArchiver.h"

#import "CamiproService.h"

#import "AuthenticationController.h"

#import "authentication.h"

#import "CamiproTransactionCell.h"

#import <QuartzCore/QuartzCore.h>

@interface CamiproViewController ()<UITableViewDataSource, UITableViewDelegate, UIAlertViewDelegate, AuthenticationCallbackDelegate, CamiproServiceDelegate>



@property (nonatomic, weak) IBOutlet UITableView* tableView;
@property (nonatomic, weak) IBOutlet UIActivityIndicatorView* centerActivityIndicator;
@property (nonatomic, weak) IBOutlet UILabel* centerMessageLabel;
@property (nonatomic, weak) IBOutlet UIToolbar* toolbar;

@property (nonatomic, strong) UITableViewController* tableViewController;
@property (nonatomic, strong) LGRefreshControl* lgRefreshControl;

// iPad only
@property (nonatomic, strong) IBOutlet UILabel* statsLabel;
@property (nonatomic, strong) IBOutlet UILabel* statsContentLabel;
@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* statsActivityIndicator;
@property (nonatomic, strong) IBOutlet UIButton* reloadCardButton;
@property (nonatomic, strong) IBOutlet UIView* verticalLine;

// iPhone only
@property (nonatomic, weak) IBOutlet UIBarButtonItem* reloadCardBarButton;
@property (nonatomic, weak) IBOutlet UIBarButtonItem* statsBarButton;

@property (nonatomic, strong) UIAlertView* sendMailAlertView;
@property (nonatomic, strong) UIAlertView* statsAlertView;
@property (nonatomic, strong) AuthenticationController* authController;
@property (nonatomic, strong) CamiproService* camiproService;
@property (nonatomic, strong) BalanceAndTransactions* balanceAndTransactions;
@property (nonatomic, strong) TequilaToken* tequilaKey;

@property (nonatomic, strong) NSTimer* tableViewMasksToBoundsTimer;

@property (nonatomic) BOOL shouldRefresh;

@end

@implementation CamiproViewController

#pragma mark - Init

- (id)init
{
    self = [super initWithNibName:@"CamiproView" bundle:nil];
    if (self) {
        self.authController = [AuthenticationController sharedInstanceToRetain];
        self.camiproService = [CamiproService sharedInstanceToRetain];
        
        /* TEST */
        
        //[CamiproService saveSessionId:nil];
        
        /* END OF TEST */
        
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.tableViewController = [[UITableViewController alloc] initWithStyle:self.tableView.style];
    [self addChildViewController:self.tableViewController];
    self.lgRefreshControl = [[LGRefreshControl alloc] initWithTableViewController:self.tableViewController refreshedDataIdentifier:nil];
    [self.lgRefreshControl setTarget:self selector:@selector(refresh)];
    self.tableViewController.tableView = self.tableView;
    
    UIEdgeInsets insets = [PCUtils edgeInsetsForViewController:self];
    insets.bottom = self.toolbar.frame.size.height;
    self.tableView.contentInset = insets;
    self.tableView.scrollIndicatorInsets = self.tableView.contentInset;
    
    if ([PCUtils isIdiomPad]) {
        self.statsLabel.text = NSLocalizedStringFromTable(@"Statistics", @"CamiproPlugin", nil);
        [self.reloadCardButton setTitle:NSLocalizedStringFromTable(@"ReloadInstructions", @"CamiproPlugin", nil) forState:UIControlStateNormal];
        self.verticalLine.backgroundColor = self.tableView.separatorColor;
    } else {
        self.reloadCardBarButton.title = NSLocalizedStringFromTable(@"ReloadCard", @"CamiproPlugin", nil);
        self.statsBarButton.title = NSLocalizedStringFromTable(@"Statistics", @"CamiproPlugin", nil);
    }
    
    UIBarButtonItem* refreshButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(refresh)];
    [self.navigationItem setRightBarButtonItem:refreshButton animated:YES];
    [self refresh];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[PCGAITracker sharedTracker] trackScreenWithName:@"/camipro"];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return [PCUtils isIdiomPad] ? UIInterfaceOrientationMaskAll : UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Refresh, login and requests

- (void)refresh {
    [self.lgRefreshControl endRefreshing];
    self.centerMessageLabel.text = @"";
    [self.centerActivityIndicator startAnimating];
    self.tableView.hidden = YES;
    self.verticalLine.hidden = YES;
    self.toolbar.hidden = YES;
    self.navigationItem.rightBarButtonItem.enabled = NO;
    
    //iPad
    self.statsLabel.hidden = YES;
    self.statsContentLabel.hidden = YES;
    self.reloadCardButton.hidden = YES;
    
    CamiproSession* sessionId = [CamiproService lastSessionId];
    if (!sessionId) {
        NSLog(@"-> No previously saved sessionId. Requesting credentials...");
        [self login];
    } else {
        NSLog(@"-> Trying to getBalanceAndTransactions with previously saved SessionId");
        [self.camiproService cancelOperationsForDelegate:self];
        [self startBalanceAndTransactionsRequestWithSessionId:sessionId];
    }
}

- (void)login {
    [self.camiproService getTequilaTokenForCamiproDelegate:self];
}

- (SessionId*)buildSessionIdFromCamiproSession:(CamiproSession*)camiproSession {
    return [[SessionId alloc] initWithTos:TypeOfService_SERVICE_CAMIPRO pocketCampusSessionId:nil moodleCookie:nil camiproCookie:camiproSession.camiproCookie isaCookie:nil];
}

- (void)startBalanceAndTransactionsRequestWithSessionId:(CamiproSession*)sessionId {
    CamiproRequest* request = [[CamiproRequest alloc] initWithISessionId:[self buildSessionIdFromCamiproSession:sessionId] iLanguage:[[NSLocale currentLocale] objectForKey:NSLocaleLanguageCode]];
    [self.camiproService getBalanceAndTransactions:request delegate:self];
}

- (void)startGetStatsRequest {
    CamiproRequest* statsRequest = [[CamiproRequest alloc] initWithISessionId:[self buildSessionIdFromCamiproSession:[CamiproService lastSessionId]] iLanguage:[[NSLocale preferredLanguages] objectAtIndex:0]];
    [self.camiproService getStatsAndLoadingInfo:statsRequest delegate:self];
}

#pragma mark - Actions

- (IBAction)statsPressed {
    [[PCGAITracker sharedTracker] trackScreenWithName:@"/camipro/stats"];
    self.statsAlertView = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Statistics", @"CamiproPlugin", nil) message:NSLocalizedStringFromTable(@"Loading...", @"PocketCampus", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) otherButtonTitles:nil];
    [self.statsAlertView show];
    [self startGetStatsRequest];
}

- (IBAction)reloadCardPressed {
    [[PCGAITracker sharedTracker] trackScreenWithName:@"/camipro/charge"];
    self.sendMailAlertView = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"ReloadInstructions", @"CamiproPlugin", nil) message:NSLocalizedStringFromTable(@"ReloadInstructionsSendMailExplanations", @"CamiproPlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) otherButtonTitles:NSLocalizedStringFromTable(@"Send", @"CamiproPlugin", nil), nil];
    [self.sendMailAlertView show];
}


#pragma mark - AuthenticationCallbackDelegate

- (void)userCancelledAuthentication {
    [CamiproService saveSessionId:nil];
    [self.centerActivityIndicator stopAnimating];
    if (self.navigationController.visibleViewController == self) {
        [self.navigationController popViewControllerAnimated:YES]; //leaving plugin
    }
}

- (void)authenticationSucceeded {
    [self.camiproService getSessionIdForServiceWithTequilaKey:self.tequilaKey delegate:self];
}

- (void)invalidToken {
    NSLog(@"-> invalid token");
    [self.centerActivityIndicator stopAnimating];
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil);
    self.centerMessageLabel.hidden = NO;
    self.tableView.hidden = YES;
    self.verticalLine.hidden = YES;
    self.toolbar.hidden = YES;
    self.navigationItem.rightBarButtonItem.enabled = YES;
    [CamiproService saveSessionId:nil];
}

#pragma mark - CamiproServiceDelegate

- (void)getTequilaTokenForCamiproDidReturn:(TequilaToken*)tequilaKey {
    self.tequilaKey = tequilaKey;
    [self.authController authToken:tequilaKey.iTequilaKey presentationViewController:self.navigationController delegate:self];
}

- (void)getTequilaTokenForCamiproFailed {
    [self serviceConnectionToServerFailed];
}

- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken*)tequilaKey didReturn:(CamiproSession*)sessionId {
    [CamiproService saveSessionId:sessionId];
    [self startBalanceAndTransactionsRequestWithSessionId:sessionId];
}

- (void)getSessionIdForServiceFailedForTequilaKey:(TequilaToken*)tequilaKey {
    [self serviceConnectionToServerFailed];
    [CamiproService saveSessionId:nil];
}

- (void)getBalanceAndTransactionsForCamiproRequest:(CamiproRequest*)camiproRequest didReturn:(BalanceAndTransactions*)balanceAndTransactions {
    switch (balanceAndTransactions.iStatus) {
        case 407: //user not authenticated (sessionId expired)
            NSLog(@"-> User session has expired. Requesting credientials...");
            [CamiproService saveSessionId:nil];
            [self login];
            break;
        case 404:
            NSLog(@"-> 404 error in status from getBalanceAndTransactionsForCamiproRequest:didReturn:");
            [self getBalanceAndTransactionsFailedForCamiproRequest:camiproRequest];
            break;
        case 200: //OK
        {
            [self.centerActivityIndicator stopAnimating];
            self.navigationItem.rightBarButtonItem.enabled = YES;
            self.centerMessageLabel.hidden = YES;
            self.balanceAndTransactions = balanceAndTransactions;
            self.tableView.alpha = 0.0;
            self.tableView.hidden = NO;
            self.verticalLine.alpha = 0.0;
            self.verticalLine.hidden = NO;
            self.toolbar.alpha = 0.0;
            self.toolbar.hidden = NO;
            [self.tableView reloadData];
            self.reloadCardButton.hidden = NO;
            self.reloadCardButton.alpha = 0.0; //iPad
            
            [UIView animateWithDuration:0.2 animations:^{
                self.tableView.alpha = 1.0;
                self.verticalLine.alpha = 1.0;
                self.toolbar.alpha = 1.0;
                self.reloadCardButton.alpha = 1.0; //iPad
            }];
            if ([PCUtils isIdiomPad]) {
                self.statsLabel.hidden = NO;
                self.statsContentLabel.hidden = NO;
                self.statsContentLabel.text = nil;
                [self.statsActivityIndicator startAnimating];
                [self startGetStatsRequest];
            }
            break;
        }
        default:
            NSLog(@"!! Unknown status code %d in getBalanceAndTransactionsForCamiproRequest:didReturn:", balanceAndTransactions.iStatus);
            [self getBalanceAndTransactionsFailedForCamiproRequest:camiproRequest];
            break;
    }
}

- (void)getBalanceAndTransactionsFailedForCamiproRequest:(CamiproRequest*)camiproRequest {
    [self.centerActivityIndicator stopAnimating];
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil);
    self.centerMessageLabel.hidden = NO;
    self.tableView.hidden = YES;
    self.verticalLine.hidden = YES;
    self.toolbar.hidden = YES;
    self.navigationItem.rightBarButtonItem.enabled = YES;
    //iPad
    self.statsLabel.hidden = YES;
    self.statsContentLabel.hidden = YES;
    self.reloadCardButton.hidden = YES;
}

- (void)sendLoadingInfoByEmailForCamiproRequest:(CamiproRequest *)camiproRequest didReturn:(SendMailResult *)sendMailResult {
    if (!self.sendMailAlertView) {
        return;
    }
    switch (sendMailResult.iStatus) {
        case 407: //user not authenticated (sessionId expired)
        {
            [self.sendMailAlertView dismissWithClickedButtonIndex:0 animated:YES];
            UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"SessionExpiredPleaseRefresh", @"CamiproPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [errorAlert show];
            break;
        }
        case 404:
        {
            NSLog(@"-> 404 error in status from sendLoadingInfoByEmailForCamiproRequest:didReturn:");
            [self.sendMailAlertView dismissWithClickedButtonIndex:0 animated:YES];
            UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"CamiproDown", @"CamiproPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [errorAlert show];
            break;
        }
        case 200: //OK
        {
            self.sendMailAlertView.message = NSLocalizedStringFromTable(@"Sent", @"CamiproPlugin", nil);
            [self.sendMailAlertView dismissWithClickedButtonIndex:0 animated:YES];
            break;
        }
        default:
            NSLog(@"!! Unknown status code %d in sendLoadingInfoByEmailForCamiproRequest:didReturn:", sendMailResult.iStatus);
            break;
    }
}

- (void)sendLoadingInfoByEmailFailedForCamiproRequest:(CamiproRequest *)camiproRequest {
    if (!self.sendMailAlertView) {
        return;
    }
    [self.sendMailAlertView dismissWithClickedButtonIndex:0 animated:NO];
    [PCUtils showServerErrorAlert];
}

- (void)getStatsAndLoadingInfoForCamiproRequest:(CamiproRequest *)camiproRequest didReturn:(StatsAndLoadingInfo *)statsAndLoadingInfo {
    if (![PCUtils isIdiomPad] && !self.statsAlertView) {
        return;
    }
    if ([PCUtils isIdiomPad]) {
        self.statsContentLabel.alpha = 0.0;
        [UIView animateWithDuration:0.4 animations:^{
            self.statsContentLabel.alpha = 1.0;
        }];
    }
    switch (statsAndLoadingInfo.iStatus) {
        case 407: //user not authenticated (sessionId expired)
        {
            if ([PCUtils isIdiomPad]) {
                [self.statsActivityIndicator stopAnimating];
                self.statsContentLabel.text = NSLocalizedStringFromTable(@"SessionExpiredPleaseRefresh", @"CamiproPlugin", nil);
            } else {
                [self.statsAlertView dismissWithClickedButtonIndex:0 animated:YES];
                UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"SessionExpiredPleaseRefresh", @"CamiproPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
                [errorAlert show];
            }
            break;
        }
        case 404:
        {
            NSLog(@"-> 404 error in status from getStatsAndLoadingInfoForCamiproRequest:didReturn:");
            
            if ([PCUtils isIdiomPad]) {
                [self.statsActivityIndicator stopAnimating];
                self.statsContentLabel.text = NSLocalizedStringFromTable(@"CamiproDown", @"CamiproPlugin", nil);
            } else {
                [self.statsAlertView dismissWithClickedButtonIndex:0 animated:YES];
                UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"CamiproDown", @"CamiproPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
                [errorAlert show];
            }
            break;
        }
        case 200: //OK
        {
            if ([PCUtils isIdiomPad]) {
                [self.statsActivityIndicator stopAnimating];
                self.statsContentLabel.text = [NSString stringWithFormat:NSLocalizedStringFromTable(@"StatsWithFormat", @"CamiproPlugin", nil), statsAndLoadingInfo.iCardStatistics.iTotalPaymentsLastMonth, statsAndLoadingInfo.iCardStatistics.iTotalPaymentsLastThreeMonths, statsAndLoadingInfo.iCardStatistics.iTotalPaymentsLastThreeMonths/3.0];
            } else {
                [self.statsAlertView dismissWithClickedButtonIndex:0 animated:YES];
                NSString* message = [NSString stringWithFormat:NSLocalizedStringFromTable(@"StatsWithFormat", @"CamiproPlugin", nil), statsAndLoadingInfo.iCardStatistics.iTotalPaymentsLastMonth, statsAndLoadingInfo.iCardStatistics.iTotalPaymentsLastThreeMonths, statsAndLoadingInfo.iCardStatistics.iTotalPaymentsLastThreeMonths/3.0];
                UIAlertView* statsAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Statistics", @"CamiproPlugin", nil) message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
                [statsAlert show];
            }
            break;
        }
        default:
            NSLog(@"!! Unknown status code %d in getStatsAndLoadingInfoForCamiproRequest:didReturn:", statsAndLoadingInfo.iStatus);
            if ([PCUtils isIdiomPad]) {
                [self.statsActivityIndicator stopAnimating];
                self.statsContentLabel.text = NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil);
            } else {
                [self.statsAlertView dismissWithClickedButtonIndex:0 animated:YES];
            }
            break;
    }
}

- (void)getStatsAndLoadingInfoFailedForCamiproRequest:(CamiproRequest *)camiproRequest {
    if (!self.statsAlertView) {
        return;
    }
    [self.statsAlertView dismissWithClickedButtonIndex:0 animated:NO];
    [PCUtils showServerErrorAlert];
}

- (void)serviceConnectionToServerFailed {
    if (self.sendMailAlertView) {
        [self.sendMailAlertView dismissWithClickedButtonIndex:0 animated:YES];
        [PCUtils showConnectionToServerTimedOutAlert];
        return;
    }
    if (self.statsAlertView) {
        [self.statsAlertView dismissWithClickedButtonIndex:0 animated:YES];
        [PCUtils showConnectionToServerTimedOutAlert];
        return;
    }
    self.shouldRefresh = YES;
    [self.centerActivityIndicator stopAnimating];
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
    self.centerMessageLabel.hidden = NO;
    self.tableView.hidden = YES;
    self.verticalLine.hidden = YES;
    self.toolbar.hidden = YES;
    //iPad
    self.statsLabel.hidden = YES;
    self.statsContentLabel.hidden = YES;
    self.reloadCardButton.hidden = YES;
    self.navigationItem.rightBarButtonItem.enabled = YES;
}

#pragma mark - UIAlertViewDelegate

- (void)alertView:(UIAlertView *)alertView willDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (alertView == self.sendMailAlertView) {
        self.sendMailAlertView = nil;
        if (buttonIndex == 0) { //Cancel
            [self.camiproService cancelOperationsForDelegate:self];
            return;
        }
        [[PCGAITracker sharedTracker] trackScreenWithName:@"/v3r1/camipro/click/email"];
        self.sendMailAlertView = [[UIAlertView alloc] initWithTitle:@"" message:NSLocalizedStringFromTable(@"Sending...", @"CamiproPlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) otherButtonTitles: nil];
        [self.sendMailAlertView show];
        
        CamiproRequest* mailRequest = [[CamiproRequest alloc] initWithISessionId:[self buildSessionIdFromCamiproSession:[CamiproService lastSessionId]] iLanguage:[[NSLocale preferredLanguages] objectAtIndex:0]];
        [self.camiproService sendLoadingInfoByEmail:mailRequest delegate:self];
    } else if (alertView == self.statsAlertView) {
        self.statsAlertView = nil;
        if (buttonIndex == 0) { //Cancel
            [self.camiproService cancelOperationsForDelegate:self];
            return;
        }
    } else {
        //nothing to do
    }
}

#pragma mark - UITableViewDelegate

static const CGFloat kBalanceCellHeightPhone = 70.0;
static const CGFloat kBalanceCellHeightPad = 120.0;

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        if ([PCUtils isIdiomPad]) {
            return kBalanceCellHeightPad;
        } else {
            return kBalanceCellHeightPhone;
        }
    }
    return 50.0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return [PCTableViewSectionHeader preferredHeight];
}

- (UIView *)tableView:(UITableView *)tableView_ viewForHeaderInSection:(NSInteger)section {
    PCTableViewSectionHeader* headerView = nil;
    switch (section) {
        case 0:
            headerView = [[PCTableViewSectionHeader alloc] initWithSectionTitle:NSLocalizedStringFromTable(@"CamiproBalance", @"CamiproPlugin", nil) tableView:self.tableView];
            break;
        case 1:
            headerView = [[PCTableViewSectionHeader alloc] initWithSectionTitle:NSLocalizedStringFromTable(@"TransactionsHistory", @"CamiproPlugin", nil) tableView:self.tableView];
            break;
        default:
            NSLog(@"!! Unexcepted tableview session");
            break;
    }
    //headerView.bounds = CGRectMake(0, 0, self.tableView.frame.size.width, headerView.frame.size.height);
    return headerView;
    
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell = nil;
    if (indexPath.section == 0) { //balance cell
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        UILabel* balanceLabel = nil;
        if ([PCUtils isIdiomPad]) {
            balanceLabel = [[UILabel alloc] initWithFrame:CGRectMake(10.0, 0.0, 300.0, kBalanceCellHeightPad)];
            balanceLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:48.0];
        } else {
            balanceLabel = [[UILabel alloc] initWithFrame:CGRectMake(10.0, 0.0, 300.0, kBalanceCellHeightPhone)];
            balanceLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:34.0];
        }
        balanceLabel.text = [NSString stringWithFormat:@"CHF %.2lf", self.balanceAndTransactions.iBalance];
        balanceLabel.textAlignment = NSTextAlignmentCenter;
        balanceLabel.backgroundColor = [UIColor clearColor];
        //balanceLabel.textColor = [UIColor darkGrayColor];
        balanceLabel.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        [cell.contentView addSubview:balanceLabel];
        
        return cell;
    }
    
    static NSString* const identifier = @"CamiproHistoryCell";
    //transactions cells
    Transaction* transaction = self.balanceAndTransactions.iTransactions[indexPath.row];
    cell = [self.tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[CamiproTransactionCell alloc] initWithReuseIdentifier:identifier];
    }
    ((CamiproTransactionCell*)cell).transaction = transaction;
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (!self.balanceAndTransactions) {
        return 0;
    }
    switch (section) {
        case 0:
            return 1; //balance
        case 1:
            return self.balanceAndTransactions.iTransactions.count;
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (!self.balanceAndTransactions) {
        return 0;
    }
    return 2; //balance and history sections
}

#pragma mark - Dealloc

- (void)dealloc
{
    [self.camiproService cancelOperationsForDelegate:self];
}

@end
