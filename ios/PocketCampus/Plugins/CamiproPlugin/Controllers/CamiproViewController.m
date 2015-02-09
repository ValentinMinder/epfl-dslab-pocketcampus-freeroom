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

//  Created by Loïc Gardiol on 17.05.12.

#import "CamiproViewController.h"

#import "PCTableViewSectionHeader.h"

#import "PCPersistenceManager.h"

#import "CamiproService.h"

#import "CamiproController.h"

#import "CamiproInfoWidgetCell.h"

#import "CamiproTransactionCell.h"

#import <QuartzCore/QuartzCore.h>

static NSInteger const kBalanceSection = 0;
static NSInteger const kHistorySection = 1;

@interface CamiproViewController ()<UITableViewDataSource, UITableViewDelegate, UIAlertViewDelegate, CamiproServiceDelegate>

@property (nonatomic, weak) IBOutlet PCTableViewAdditions* tableView;
@property (nonatomic, weak) IBOutlet UIActivityIndicatorView* centerActivityIndicator;
@property (nonatomic, weak) IBOutlet UILabel* centerMessageLabel;
@property (nonatomic, weak) IBOutlet UIToolbar* toolbar;

@property (nonatomic, strong) UITableViewController* tableViewController;
@property (nonatomic, strong) LGARefreshControl* lgRefreshControl;

@property (nonatomic, strong, readonly) CamiproInfoWidgetCell* infoWidgetCell;

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
@property (nonatomic, strong) CamiproService* camiproService;
@property (nonatomic, strong) BalanceAndTransactions* balanceAndTransactions;

@property (nonatomic) BOOL shouldRefresh;

@end

@implementation CamiproViewController

#pragma mark - Init

- (id)init
{
    self = [super initWithNibName:@"CamiproView" bundle:nil];
    if (self) {
        self.gaiScreenName = @"/camipro";
        self.camiproService = [CamiproService sharedInstanceToRetain];
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.tableViewController = [[UITableViewController alloc] initWithStyle:self.tableView.style];
    [self addChildViewController:self.tableViewController];
    self.lgRefreshControl = [[LGARefreshControl alloc] initWithTableViewController:self.tableViewController refreshedDataIdentifier:nil];
    [self.lgRefreshControl setTarget:self selector:@selector(refresh)];
    self.tableViewController.tableView = self.tableView;
    
    UIEdgeInsets insets = [PCUtils edgeInsetsForViewController:self];
    insets.bottom = self.toolbar.frame.size.height;
    self.tableView.contentInset = insets;
    self.tableView.scrollIndicatorInsets = self.tableView.contentInset;
    
    if ([PCUtils isIdiomPad]) {
        self.statsLabel.text = NSLocalizedStringFromTable(@"Statistics", @"CamiproPlugin", nil);
        [self.reloadCardButton setTitle:NSLocalizedStringFromTable(@"ReloadInstructions", @"CamiproPlugin", nil) forState:UIControlStateNormal];
        self.reloadCardButton.accessibilityHint = NSLocalizedStringFromTable(@"AllowsToReceiveByEmailBankingInformationToReloadCard", @"CamiproPlugin", nil);
        self.verticalLine.backgroundColor = self.tableView.separatorColor;
    } else {
        self.reloadCardBarButton.title = NSLocalizedStringFromTable(@"ReloadCard", @"CamiproPlugin", nil);
        self.reloadCardBarButton.accessibilityHint = NSLocalizedStringFromTable(@"AllowsToReceiveByEmailBankingInformationToReloadCard", @"CamiproPlugin", nil);
        self.statsBarButton.title = NSLocalizedStringFromTable(@"Statistics", @"CamiproPlugin", nil);
    }
    
    UIBarButtonItem* refreshButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(refreshPressed)];
    [self.navigationItem setRightBarButtonItem:refreshButton animated:YES];
    [self refresh];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
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
    
    [self.camiproService cancelOperationsForDelegate:self];
    [self startBalanceAndTransactionsRequest];
}

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
        [[CamiproController sharedInstanceToRetain] addLoginObserver:self successBlock:successBlock userCancelledBlock:^{
            [self.centerActivityIndicator stopAnimating];
            [[MainController publicController] requestLeavePlugin:[CamiproController identifierName]];
        } failureBlock:^(NSError *error) {
            [self getBalanceAndTransactionsFailedForCamiproRequest:nil];
        }];
    }
}

- (void)startGetStatsRequest {
    VoidBlock successBlock = ^{
        CamiproRequest* request = [[CamiproRequest alloc] initWithISessionId:[self buildSessionIdFromCamiproSession:self.camiproService.camiproSession] iLanguage:[PCUtils userLanguageCode]];
        [self.camiproService getStatsAndLoadingInfo:request delegate:self];
    };
    if (self.camiproService.camiproSession) {
        successBlock();
    } else {
        [[CamiproController sharedInstanceToRetain] addLoginObserver:self successBlock:successBlock userCancelledBlock:^{
            if (!self.statsAlertView) {
                return;
            }
            [self.statsAlertView dismissWithClickedButtonIndex:0 animated:YES];
        } failureBlock:^(NSError *error) {
            [self getStatsAndLoadingInfoFailedForCamiproRequest:nil];
        }];
    }
}

#pragma mark - Actions

- (void)refreshPressed {
    [self trackAction:@"Refresh"];
    [self refresh];
}

- (IBAction)statsPressed {
    [self trackAction:@"Stats"];
    self.statsAlertView = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Statistics", @"CamiproPlugin", nil) message:NSLocalizedStringFromTable(@"Loading...", @"PocketCampus", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) otherButtonTitles:nil];
    [self.statsAlertView show];
    [self startGetStatsRequest];
}

- (IBAction)reloadCardPressed {
    self.sendMailAlertView = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"ReloadInstructions", @"CamiproPlugin", nil) message:NSLocalizedStringFromTable(@"ReloadInstructionsSendMailExplanations", @"CamiproPlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) otherButtonTitles:NSLocalizedStringFromTable(@"Send", @"CamiproPlugin", nil), nil];
    [self.sendMailAlertView show];
}


#pragma mark - CamiproServiceDelegate

- (void)getBalanceAndTransactionsForCamiproRequest:(CamiproRequest*)camiproRequest didReturn:(BalanceAndTransactions*)balanceAndTransactions {
    switch (balanceAndTransactions.iStatus) {
        case 407: //user not authenticated (sessionId expired)
            CLSNSLog(@"-> User session has expired. Requesting credientials...");
            [self.camiproService deleteCamiproSession];
            [self startBalanceAndTransactionsRequest];
            break;
        case 404:
            CLSNSLog(@"-> 404 error in status from getBalanceAndTransactionsForCamiproRequest:didReturn:");
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
            CLSNSLog(@"!! Unknown status code %d in getBalanceAndTransactionsForCamiproRequest:didReturn:", balanceAndTransactions.iStatus);
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
            CLSNSLog(@"-> 404 error in status from sendLoadingInfoByEmailForCamiproRequest:didReturn:");
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
            CLSNSLog(@"!! Unknown status code %d in sendLoadingInfoByEmailForCamiproRequest:didReturn:", sendMailResult.iStatus);
            [self sendLoadingInfoByEmailFailedForCamiproRequest:camiproRequest];
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
            CLSNSLog(@"-> 404 error in status from getStatsAndLoadingInfoForCamiproRequest:didReturn:");
            
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
            CLSNSLog(@"!! Unknown status code %d in getStatsAndLoadingInfoForCamiproRequest:didReturn:", statsAndLoadingInfo.iStatus);
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
        [self trackAction:@"RequestEmail"];
        self.sendMailAlertView = [[UIAlertView alloc] initWithTitle:@"" message:NSLocalizedStringFromTable(@"Sending...", @"CamiproPlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) otherButtonTitles: nil];
        [self.sendMailAlertView show];
        
        CamiproRequest* mailRequest = [[CamiproRequest alloc] initWithISessionId:[self buildSessionIdFromCamiproSession:self.camiproService.camiproSession] iLanguage:[NSLocale preferredLanguages][0]];
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
    switch (indexPath.section) {
        case kBalanceSection:
            if (indexPath.row == [self balanceCellIndex]) {
                return [PCUtils isIdiomPad] ? kBalanceCellHeightPad : kBalanceCellHeightPhone;
            }
            if (indexPath.row == [self infoWidgetCellIndex]) {
                return [self.infoWidgetCell preferredHeightInTableView:self.tableView];
            }
            break;
        case kHistorySection:
            return 50.0;
            break;
    }
    return 0.0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return [PCTableViewSectionHeader preferredHeight];
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    PCTableViewSectionHeader* headerView = nil;
    switch (section) {
        case kBalanceSection:
            headerView = [[PCTableViewSectionHeader alloc] initWithSectionTitle:NSLocalizedStringFromTable(@"CamiproBalance", @"CamiproPlugin", nil) tableView:self.tableView];
            break;
        case kHistorySection:
            headerView = [[PCTableViewSectionHeader alloc] initWithSectionTitle:NSLocalizedStringFromTable(@"TransactionsHistory", @"CamiproPlugin", nil) tableView:self.tableView];
            break;
        default:
            break;
    }
    //headerView.bounds = CGRectMake(0, 0, self.tableView.frame.size.width, headerView.frame.size.height);
    return headerView;
    
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    PCTableViewCellAdditions* cell = nil;
    switch (indexPath.section) {
        case kBalanceSection:
        {
            if (indexPath.row == [self balanceCellIndex]) {
                cell = [[PCTableViewCellAdditions alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
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
                balanceLabel.isAccessibilityElement = NO;
                [cell setAccessibilityLabelBlock:^NSString* {
                    return [NSString stringWithFormat:NSLocalizedStringFromTable(@"CamiproBalanceWithFormat", @"CamiproPlugin", nil), balanceLabel.text];
                }];
                [cell.contentView addSubview:balanceLabel];
            } else if (indexPath.row == [self infoWidgetCellIndex]) {
                cell = self.infoWidgetCell;
                __weak __typeof(self) welf = self;
                [(CamiproInfoWidgetCell*)cell setCloseButtonTapped:^{
                    [welf saveWidgetInfoHidden:YES];
                    [welf.tableView reloadSections:[NSIndexSet indexSetWithIndex:kBalanceSection] withRowAnimation:UITableViewRowAnimationAutomatic];
                }];
            }
            break;
        }
        case kHistorySection:
        {
            NSString* identifier = [(PCTableViewAdditions*)tableView autoInvalidatingReuseIdentifierForIdentifier:@"CamiproHistoryCell"];
            //transactions cells
            Transaction* transaction = self.balanceAndTransactions.iTransactions[indexPath.row];
            cell = [self.tableView dequeueReusableCellWithIdentifier:identifier];
            if (!cell) {
                cell = [[CamiproTransactionCell alloc] initWithReuseIdentifier:identifier];
            }
            ((CamiproTransactionCell*)cell).transaction = transaction;
            break;
        }
        default:
            break;
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (!self.balanceAndTransactions) {
        return 0;
    }
    switch (section) {
        case kBalanceSection:
            if ([self infoWidgetCellIndex] >= 0) {
                return 2;
            }
            return 1;
        case kHistorySection:
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

#pragma mark - Private

@synthesize infoWidgetCell = _infoWidgetCell;

- (CamiproInfoWidgetCell*)infoWidgetCell {
    if (!_infoWidgetCell) {
        _infoWidgetCell = [CamiproInfoWidgetCell new];
    }
    return _infoWidgetCell;
}

static NSString* const kHideWidgetInfoBoolKey = @"HideWidgetInfoBool";

- (void)saveWidgetInfoHidden:(BOOL)hidden {
    [[PCPersistenceManager userDefaultsForPluginName:@"camipro"] setBool:hidden forKey:kHideWidgetInfoBoolKey];
}

- (BOOL)shouldHideWidgetInfo {
    if ([PCUtils isOSVersionSmallerThan:8.0]) {
        //Widgets only available on iOS 8
        return YES;
    }
    return [[PCPersistenceManager userDefaultsForPluginName:@"camipro"] boolForKey:kHideWidgetInfoBoolKey];
}

- (NSInteger)infoWidgetCellIndex {
    if ([self shouldHideWidgetInfo]) {
        return -1;
    }
    return 0;
}

- (NSInteger)balanceCellIndex {
    if ([self infoWidgetCellIndex] >= 0) {
        return 1;
    }
    return 0;
}

#pragma mark - Dealloc

- (void)dealloc
{
    [self.camiproService cancelOperationsForDelegate:self];
}

@end
