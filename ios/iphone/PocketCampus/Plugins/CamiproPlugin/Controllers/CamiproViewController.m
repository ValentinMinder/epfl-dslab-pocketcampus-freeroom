//
//  CamiproViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 17.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "CamiproViewController.h"

#import "PCValues.h"

#import "PCUtils.h"

#import "PCTableViewSectionHeader.h"

#import "ObjectArchiver.h"

#import "authentication.h"

static NSString* kHistoryCellIdentifier = @"CamiproHistoryCell";
static NSUInteger kTransactionPriceViewTag = 15;
static CGFloat kBalanceCellHeight = 70.0;

@implementation CamiproViewController

@synthesize tableView, centerActivityIndicator, centerMessageLabel, toolbar, shouldRefresh;

- (id)init
{
    self = [super initWithNibName:@"CamiproView" bundle:nil];
    if (self) {
        authController = [[AuthenticationController sharedInstanceToRetain] retain];
        camiproService = [[CamiproService sharedInstanceToRetain] retain];
        balanceAndTransactions = nil;
        tequilaKey = nil;
        
        /* TEST */
        
        //[CamiproService saveSessionId:nil];
        
        /* END OF TEST */
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/camipro" withError:NULL];
    self.view.backgroundColor = [PCValues backgroundColor1];
    tableView.contentInset = UIEdgeInsetsMake(0, 0, toolbar.frame.size.height, 0);
    lastUpdateLabel = [[UILabel alloc] initWithFrame:CGRectMake(40.0, 0.0, 240.0, self.toolbar.frame.size.height)];
    lastUpdateLabel.textColor = [UIColor whiteColor];
    lastUpdateLabel.font = [UIFont systemFontOfSize:15.0];
    lastUpdateLabel.textAlignment = UITextAlignmentCenter;
    lastUpdateLabel.backgroundColor = [UIColor clearColor];
    lastUpdateLabel.shadowOffset = CGSizeMake(0.0, -1.0);
    lastUpdateLabel.shadowColor = [UIColor blackColor];
    [self.toolbar addSubview:lastUpdateLabel];
    [lastUpdateLabel release];
    UIBarButtonItem* refreshButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(refresh)];
    [self.navigationItem setRightBarButtonItem:refreshButton animated:YES];
    [refreshButton release];
    [self refresh];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}
                                              
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)refresh {
    centerMessageLabel.text = @"";
    [centerActivityIndicator startAnimating];
    tableView.hidden = YES;
    toolbar.hidden = YES;
    self.navigationItem.rightBarButtonItem.enabled = NO;
    CamiproSession* sessionId = [CamiproService lastSessionId];
    if (sessionId == nil) {
        NSLog(@"-> No previously saved sessionId. Requesting credentials...");
        [self login];
    } else {
        NSLog(@"-> Trying to getBalanceAndTransactions with previously saved SessionId");
        [self startBalanceAndTransactionsRequestWithSessionId:sessionId];
    }
}

- (IBAction)othersPressed {
    UIActionSheet* actionSheet = [[UIActionSheet alloc] initWithTitle:@"" delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:nil otherButtonTitles:NSLocalizedStringFromTable(@"ReloadInstructions", @"CamiproPlugin", nil), NSLocalizedStringFromTable(@"Statistics", @"CamiproPlugin", nil), nil];
    [actionSheet showFromToolbar:self.toolbar];
    [actionSheet release];
}

- (void)login {
    [camiproService getTequilaTokenForCamiproDelegate:self];
}

- (SessionId*) buildSessionIdFromCamiproSession:(CamiproSession*)camiproSession {
    return [[[SessionId alloc] initWithTos:TypeOfService_SERVICE_CAMIPRO pocketCampusSessionId:nil moodleCookie:nil camiproCookie:camiproSession.camiproCookie isaCookie:nil] autorelease];
}

- (void)startBalanceAndTransactionsRequestWithSessionId:(CamiproSession*)sessionId {
    CamiproRequest* request = [[CamiproRequest alloc] initWithISessionId:[self buildSessionIdFromCamiproSession:sessionId] iLanguage:[[NSLocale currentLocale] objectForKey:NSLocaleLanguageCode]];
    [camiproService getBalanceAndTransactions:request delegate:self];
    [request release];
}

/* AuthenticationCallbackDelegate delegation */

- (void)userCancelledAuthentication {
    [CamiproService saveSessionId:nil];
    [centerActivityIndicator stopAnimating];
    if (self.navigationController.visibleViewController == self) {
        [self.navigationController popViewControllerAnimated:YES]; //leaving plugin
    }
}

- (void)authenticationSucceeded {
    [camiproService getSessionIdForServiceWithTequilaKey:tequilaKey delegate:self];
}

- (void)invalidToken {
    NSLog(@"-> invalid token");
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil);
    centerMessageLabel.hidden = NO;
    tableView.hidden = YES;
    toolbar.hidden = YES;
    self.navigationItem.rightBarButtonItem.enabled = YES;
    [CamiproService saveSessionId:nil];
}

/* CamiproServiceDelegate delegation */

- (void)getTequilaTokenForCamiproDidReturn:(TequilaToken*)tequilaKey_ {
    [tequilaKey release];
    tequilaKey = [tequilaKey_ retain];
    [authController authToken:tequilaKey.iTequilaKey presentationViewController:self.navigationController delegate:self];
}

- (void)getTequilaTokenForCamiproFailed {
    [self serviceConnectionToServerTimedOut];
}

- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken*)tequilaKey didReturn:(CamiproSession*)sessionId {
    [CamiproService saveSessionId:sessionId];
    [self startBalanceAndTransactionsRequestWithSessionId:sessionId];
}

- (void)getSessionIdForServiceFailedForTequilaKey:(TequilaToken*)tequilaKey {
    [self serviceConnectionToServerTimedOut];
    [CamiproService saveSessionId:nil];
}

- (void)getBalanceAndTransactionsForCamiproRequest:(CamiproRequest*)camiproRequest didReturn:(BalanceAndTransactions*)balanceAndTransactions_ {
    switch (balanceAndTransactions_.iStatus) {
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
            [centerActivityIndicator stopAnimating];
            self.navigationItem.rightBarButtonItem.enabled = YES;
            centerMessageLabel.hidden = YES;
            [balanceAndTransactions release];
            balanceAndTransactions = [balanceAndTransactions_ retain];
            tableView.alpha = 0.0;
            tableView.hidden = NO;
            toolbar.alpha = 0.0;
            toolbar.hidden = NO;
            lastUpdateLabel.text = [NSString stringWithFormat:@"%@ %@", NSLocalizedStringFromTable(@"LastUpdate", @"CamiproPlugin", nil),balanceAndTransactions.iDate];
            [tableView reloadData];
            [UIView animateWithDuration:0.2 animations:^{
                tableView.alpha = 1.0;
                toolbar.alpha = 1.0;
            }];
            break;
        }
        default:
            NSLog(@"!! Unknown status code %d in getBalanceAndTransactionsForCamiproRequest:didReturn:", balanceAndTransactions_.iStatus);
            [self getBalanceAndTransactionsFailedForCamiproRequest:camiproRequest];
            break;
    }
}

- (void)getBalanceAndTransactionsFailedForCamiproRequest:(CamiproRequest*)camiproRequest {
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil);
    centerMessageLabel.hidden = NO;
    tableView.hidden = YES;
    toolbar.hidden = YES;
    self.navigationItem.rightBarButtonItem.enabled = YES;
    //[CamiproService saveSessionId:nil];
}

- (void)sendLoadingInfoByEmailForCamiproRequest:(CamiproRequest *)camiproRequest didReturn:(SendMailResult *)sendMailResult {
    if (sendMailAlertView == nil) {
        return;
    }
    switch (sendMailResult.iStatus) {
        case 407: //user not authenticated (sessionId expired)
        {
            [sendMailAlertView dismissWithClickedButtonIndex:0 animated:YES];
            UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"SessionExpiredPleaseRefresh", @"CamiproPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [errorAlert show];
            [errorAlert release];
            break;
        }
        case 404:
            NSLog(@"-> 404 error in status from sendLoadingInfoByEmailForCamiproRequest:didReturn:");
            [sendMailAlertView dismissWithClickedButtonIndex:0 animated:YES];
            UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"CamiproDown", @"CamiproPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [errorAlert show];
            [errorAlert release];
            break;
        case 200: //OK
        {
            sendMailAlertView.message = NSLocalizedStringFromTable(@"Sent", @"CamiproPlugin", nil);
            [sendMailAlertView dismissWithClickedButtonIndex:0 animated:YES];
            break;
        }
        default:
            NSLog(@"!! Unknown status code %d in sendLoadingInfoByEmailForCamiproRequest:didReturn:", sendMailResult.iStatus);
            break;
    }
}

- (void)sendLoadingInfoByEmailFailedForCamiproRequest:(CamiproRequest *)camiproRequest {
    if (sendMailAlertView == nil) {
        return;
    }
    [sendMailAlertView dismissWithClickedButtonIndex:0 animated:NO];
    [PCUtils showServerErrorAlert];
}

- (void)getStatsAndLoadingInfoForCamiproRequest:(CamiproRequest *)camiproRequest didReturn:(StatsAndLoadingInfo *)statsAndLoadingInfo {
    if (statsAlertView == nil) {
        return;
    }
    switch (statsAndLoadingInfo.iStatus) {
        case 407: //user not authenticated (sessionId expired)
        {
            [statsAlertView dismissWithClickedButtonIndex:0 animated:YES];
            UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"SessionExpiredPleaseRefresh", @"CamiproPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [errorAlert show];
            [errorAlert release];
            break;
        }
        case 404:
            NSLog(@"-> 404 error in status from getStatsAndLoadingInfoForCamiproRequest:didReturn:");
            [statsAlertView dismissWithClickedButtonIndex:0 animated:YES];
            UIAlertView* errorAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"CamiproDown", @"CamiproPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [errorAlert show];
            [errorAlert release];
            /* TEST */
            /*
            [statsAlertView dismissWithClickedButtonIndex:0 animated:NO];
            NSString* message = [NSString stringWithFormat:NSLocalizedStringFromTable(@"StatsWithFormat", @"CamiproPlugin", nil), 333.0, 1000.0, 320.0];
            UIAlertView* statsAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Statistics", @"CamiproPlugin", nil) message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [statsAlert show];
            [statsAlert release];
             */
             /* END OF TEST */
            break;
        case 200: //OK
        {
            [statsAlertView dismissWithClickedButtonIndex:0 animated:YES];
            NSString* message = [NSString stringWithFormat:NSLocalizedStringFromTable(@"StatsWithFormat", @"CamiproPlugin", nil), statsAndLoadingInfo.iCardStatistics.iTotalPaymentsLastMonth, statsAndLoadingInfo.iCardStatistics.iTotalPaymentsLastThreeMonths, statsAndLoadingInfo.iCardStatistics.iTotalPaymentsLastThreeMonths/3.0];
            UIAlertView* statsAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Statistics", @"CamiproPlugin", nil) message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [statsAlert show];
            [statsAlert release];
            break;
        }
        default:
            NSLog(@"!! Unknown status code %d in getStatsAndLoadingInfoForCamiproRequest:didReturn:", statsAndLoadingInfo.iStatus);
            break;
    }
}

- (void)getStatsAndLoadingInfoFailedForCamiproRequest:(CamiproRequest *)camiproRequest {
    if (statsAlertView == nil) {
        return;
    }
    [statsAlertView dismissWithClickedButtonIndex:0 animated:NO];
    [PCUtils showServerErrorAlert];
}

- (void)serviceConnectionToServerTimedOut {
    if (sendMailAlertView != nil) {
        [sendMailAlertView dismissWithClickedButtonIndex:0 animated:YES];
        [PCUtils showConnectionToServerTimedOutAlert];
        return;
    }
    if (statsAlertView != nil) {
        [statsAlertView dismissWithClickedButtonIndex:0 animated:YES];
        [PCUtils showConnectionToServerTimedOutAlert];
        return;
    }
    shouldRefresh = YES;
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
    centerMessageLabel.hidden = NO;
    tableView.hidden = YES;
    toolbar.hidden = YES;
    self.navigationItem.rightBarButtonItem.enabled = YES;
}

/* UIActionSheetDelegate delegation */

- (void)actionSheet:(UIActionSheet *)actionSheet willDismissWithButtonIndex:(NSInteger)buttonIndex {
    switch (buttonIndex) {
        case 0: //reload instructions
        {
            [[GANTracker sharedTracker] trackPageview:@"/v3r1/camipro/click/reload" withError:NULL];
            sendMailAlertView = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"ReloadInstructions", @"CamiproPlugin", nil) message:NSLocalizedStringFromTable(@"ReloadInstructionsSendMailExplanations", @"CamiproPlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) otherButtonTitles:NSLocalizedStringFromTable(@"Send", @"CamiproPlugin", nil), nil];
            [sendMailAlertView show];
            break;
        }
        case 1: //statistics
        {
            [[GANTracker sharedTracker] trackPageview:@"/v3r1/camipro/click/stats" withError:NULL];
            statsAlertView = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Statistics", @"CamiproPlugin", nil) message:NSLocalizedStringFromTable(@"Loading...", @"PocketCampus", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) otherButtonTitles:nil];
            [statsAlertView show];
            CamiproRequest* statsRequest = [[CamiproRequest alloc] initWithISessionId:[self buildSessionIdFromCamiproSession:[CamiproService lastSessionId]] iLanguage:[[NSLocale preferredLanguages] objectAtIndex:0]];
            [camiproService getStatsAndLoadingInfo:statsRequest delegate:self];
            [statsRequest release];
            break;
        }
        default:
            break;
    }
    
}

/* UIAlertViewDelegate delegation */

- (void)alertView:(UIAlertView *)alertView willDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (alertView == sendMailAlertView) {
        [sendMailAlertView release];
        sendMailAlertView = nil;
        if (buttonIndex == 0) { //Cancel
            [camiproService cancelOperationsForDelegate:self];
            return;
        }
        [[GANTracker sharedTracker] trackPageview:@"/v3r1/camipro/click/email" withError:NULL];
        sendMailAlertView = [[UIAlertView alloc] initWithTitle:@"" message:NSLocalizedStringFromTable(@"Sending...", @"CamiproPlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) otherButtonTitles: nil];
        [sendMailAlertView show];
        
        CamiproRequest* mailRequest = [[CamiproRequest alloc] initWithISessionId:[self buildSessionIdFromCamiproSession:[CamiproService lastSessionId]] iLanguage:[[NSLocale preferredLanguages] objectAtIndex:0]];
        [camiproService sendLoadingInfoByEmail:mailRequest delegate:self];
        [mailRequest release];
    } else if (alertView == statsAlertView) {
        [statsAlertView release];
        statsAlertView = nil;
        if (buttonIndex == 0) { //Cancel
            [camiproService cancelOperationsForDelegate:self];
            return;
        }
    } else {
        //nothing to do
    }
}

/* UITableViewDelegation delegation */

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return kBalanceCellHeight;
    }
    return 50.0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return [PCValues tableViewSectionHeaderHeight];
}

- (UIView *)tableView:(UITableView *)tableView_ viewForHeaderInSection:(NSInteger)section {
    PCTableViewSectionHeader* headerView = nil;
    switch (section) {
        case 0:
            headerView = [[PCTableViewSectionHeader alloc] initWithSectionTitle:NSLocalizedStringFromTable(@"CamiproBalance", @"CamiproPlugin", nil) tableView:tableView];
            break;
        case 1:
            headerView = [[PCTableViewSectionHeader alloc] initWithSectionTitle:NSLocalizedStringFromTable(@"TransactionsHistory", @"CamiproPlugin", nil) tableView:tableView];
            break;
        default:
            NSLog(@"!! Unexcepted tableview session");
            break;
    }
    return [headerView autorelease];
    
}

/* UITableViewDataSource delegation */

- (UITableViewCell*)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* newCell = nil;
    if (indexPath.section == 0) {
        newCell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
        newCell.selectionStyle = UITableViewCellSelectionStyleNone;
        newCell.backgroundColor = [PCValues backgroundColor1];
        UILabel* balanceLabel = [[UILabel alloc] initWithFrame:CGRectMake(10.0, 0.0, 300.0, kBalanceCellHeight)];
        balanceLabel.text = [NSString stringWithFormat:@"CHF %.2lf", balanceAndTransactions.iBalance];
        balanceLabel.textAlignment = UITextAlignmentCenter;
        balanceLabel.font = [UIFont systemFontOfSize:34.0];
        balanceLabel.textColor = [PCValues textColor1];
        balanceLabel.backgroundColor = [UIColor clearColor];
        balanceLabel.shadowColor = [PCValues shadowColor1];
        balanceLabel.shadowOffset = [PCValues shadowOffset1];
        [newCell.contentView addSubview:balanceLabel];
        [balanceLabel release];
        return newCell;
    }
    
    newCell = [tableView dequeueReusableCellWithIdentifier:kHistoryCellIdentifier];
    
    Transaction* transcation = [balanceAndTransactions.iTransactions objectAtIndex:indexPath.row];
    
    if (newCell == nil) {
        newCell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:kHistoryCellIdentifier] autorelease];
        newCell.selectionStyle = UITableViewCellSelectionStyleNone;
        newCell.backgroundColor = [UIColor clearColor];
        newCell.textLabel.textColor = [PCValues textColor1];
        UILabel* priceLabel = [[UILabel alloc] initWithFrame:CGRectMake(211.0, 27.0, 100.0, 17.0)];
        priceLabel.backgroundColor = [UIColor clearColor];
        priceLabel.tag = kTransactionPriceViewTag;
        priceLabel.font = [UIFont systemFontOfSize:15.0];
        priceLabel.textAlignment = UITextAlignmentRight;
        
        [newCell.contentView addSubview:priceLabel];
        [priceLabel release];
    }
    newCell.textLabel.text = transcation.iPlace;
    newCell.detailTextLabel.text = transcation.iDate;
    UILabel* priceLabel = (UILabel*)[newCell.contentView viewWithTag:kTransactionPriceViewTag];
    priceLabel.text = [NSString stringWithFormat:@"CHF %.2lf", transcation.iAmount];
    
    if (transcation.iAmount > 0.0) {
        priceLabel.textColor = [UIColor colorWithRed:0.09 green:0.79 blue:0 alpha:1.0]; //light green
    } else {
        priceLabel.textColor = [PCValues pocketCampusRed];
    }
    
    return newCell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (balanceAndTransactions == nil) {
        return 0;
    }
    switch (section) {
        case 0:
            return 1;
            break;
        case 1:
            if (balanceAndTransactions.iTransactions == nil) {
                return 0;
            }
            return [balanceAndTransactions.iTransactions count];
        default:
            return 0;
            break;
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (balanceAndTransactions == nil) {
        return 0;
    }
    return 2; //balance and history sections
}

- (void)dealloc
{
    [authController release];
    [camiproService cancelOperationsForDelegate:self];
    [camiproService release];
    [balanceAndTransactions release];
    [tequilaKey release];
    [super dealloc];
}

@end
