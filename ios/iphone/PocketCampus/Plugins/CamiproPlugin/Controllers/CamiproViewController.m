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

#import <QuartzCore/QuartzCore.h>

static NSString* kHistoryCellIdentifier = @"CamiproHistoryCell";
static NSUInteger kTransactionPriceViewTag = 15;

@interface CamiproViewController ()



//@property (nonatomic, strong) UILabel* lastUpdateLabel;
@property (nonatomic, weak) IBOutlet UITableView* tableView;
@property (nonatomic, weak) IBOutlet UIActivityIndicatorView* centerActivityIndicator;
@property (nonatomic, weak) IBOutlet UILabel* centerMessageLabel;
@property (nonatomic, weak) IBOutlet UIToolbar* toolbar;


// iPad only
@property (nonatomic, weak) IBOutlet UIView* statsContainerView;
@property (nonatomic, weak) IBOutlet UILabel* statsLabel;
@property (nonatomic, weak) IBOutlet UILabel* statsContentLabel;
@property (nonatomic, weak) IBOutlet UIActivityIndicatorView* statsActivityIndicator;
@property (nonatomic, weak) IBOutlet UIButton* reloadCardButton;

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

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/camipro" withError:NULL];
    self.view.backgroundColor = [PCValues backgroundColor1];
    
    
    if ([PCUtils isIdiomPad]) {
        self.tableView.layer.masksToBounds = NO;
        self.tableView.layer.shadowOffset = CGSizeMake(0, 0);
        self.tableView.layer.shadowOpacity = 0.5;
        //self.tableView.layer.shadowPath = [UIBezierPath bezierPathWithRect:self.tableView.bounds].CGPath;
        
        self.statsLabel.text = NSLocalizedStringFromTable(@"Statistics", @"CamiproPlugin", nil);
        self.statsLabel.textColor = [UIColor colorWithWhite:0.3 alpha:1.0];
        self.statsLabel.shadowColor = [PCValues shadowColor1];
        self.statsLabel.shadowOffset = [PCValues shadowOffset1];
        
        self.statsContentLabel.textColor = [UIColor colorWithWhite:0.4 alpha:1.0];
        self.statsContentLabel.shadowColor = [PCValues shadowColor1];
        self.statsContentLabel.shadowOffset = [PCValues shadowOffset1];
        

        [self.reloadCardButton setTitle:NSLocalizedStringFromTable(@"ReloadInstructions", @"CamiproPlugin", nil) forState:UIControlStateNormal];

        [self.reloadCardButton setBackgroundImage:[PCValues imageForGenericGreyButton] forState:UIControlStateNormal];
        [self.reloadCardButton setBackgroundImage:[PCValues highlightedForGenericGreyButton] forState:UIControlStateHighlighted];
        
        [self willAnimateRotationToInterfaceOrientation:[[UIApplication sharedApplication] statusBarOrientation] duration:0.0];
    } else {
        self.reloadCardBarButton.title = NSLocalizedStringFromTable(@"ReloadCard", @"CamiproPlugin", nil);
        self.statsBarButton.title = NSLocalizedStringFromTable(@"Statistics", @"CamiproPlugin", nil);
    }
    
    self.tableView.contentInset = UIEdgeInsetsMake(0, 0, self.toolbar.frame.size.height, 0);
    /*self.lastUpdateLabel = [[UILabel alloc] initWithFrame:CGRectMake((self.tableView.frame.size.width/2.0)-120.0, 0, 240.0, self.toolbar.frame.size.height)];
    //self.lastUpdateLabel.center = self.toolbar.center;
    self.lastUpdateLabel.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin;
    self.lastUpdateLabel.textColor = [UIColor whiteColor];
    self.lastUpdateLabel.font = [UIFont systemFontOfSize:15.0];
    self.lastUpdateLabel.textAlignment = UITextAlignmentCenter;
    self.lastUpdateLabel.backgroundColor = [UIColor clearColor];
    self.lastUpdateLabel.shadowOffset = CGSizeMake(0.0, -1.0);
    self.lastUpdateLabel.shadowColor = [UIColor blackColor];
    [self.toolbar addSubview:self.lastUpdateLabel];*/
    
    UIBarButtonItem* refreshButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(refresh)];
    [self.navigationItem setRightBarButtonItem:refreshButton animated:YES];
    [self refresh];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    if ([PCUtils isIdiomPad]) {
        return UIInterfaceOrientationMaskAll;
    } else {
        return UIInterfaceOrientationMaskPortrait;
    }
}
                                              
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    if ([PCUtils isIdiomPad]) {
        return YES;
    } else {
        return (interfaceOrientation == UIInterfaceOrientationPortrait);
    }
}

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
    if (![PCUtils isIdiomPad]) {
        return;
    }
    
    self.tableView.layer.masksToBounds = YES;
    self.tableView.layer.shadowOpacity = 0.0;
    [self.tableViewMasksToBoundsTimer invalidate];
    self.tableViewMasksToBoundsTimer = [NSTimer scheduledTimerWithTimeInterval:duration+0.05 target:self selector:@selector(setTableViewMasksToBoundsNO) userInfo:nil repeats:NO];
}

- (void)setTableViewMasksToBoundsNO {
    self.tableView.layer.masksToBounds = NO;
    self.tableView.layer.shadowOpacity = 0.5;
}

- (void)refresh {
    self.centerMessageLabel.text = @"";
    [self.centerActivityIndicator startAnimating];
    self.tableView.hidden = YES;
    self.toolbar.hidden = YES;
    //self.lastUpdateLabel.hidden = YES;
    self.navigationItem.rightBarButtonItem.enabled = NO;
    
    //iPad
    self.statsContainerView.hidden = YES;
    self.reloadCardButton.hidden = YES;
    
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
}

- (IBAction)statsPressed {
    [self actionSheet:nil willDismissWithButtonIndex:1]; //actionSheet var is not checked and 0 is reload. I agree it's not very nice to to like this...
}

- (IBAction)reloadCardPressed {
    [self actionSheet:nil willDismissWithButtonIndex:0]; //actionSheet var is not checked and 0 is reload. I agree it's not very nice to to like this...
}

- (void)login {
    [self.camiproService getTequilaTokenForCamiproDelegate:self];
}

- (SessionId*) buildSessionIdFromCamiproSession:(CamiproSession*)camiproSession {
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

/* AuthenticationCallbackDelegate delegation */

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
    self.toolbar.hidden = YES;
    //self.lastUpdateLabel.hidden = YES;
    self.navigationItem.rightBarButtonItem.enabled = YES;
    [CamiproService saveSessionId:nil];
}

/* CamiproServiceDelegate delegation */

- (void)getTequilaTokenForCamiproDidReturn:(TequilaToken*)tequilaKey {
    self.tequilaKey = tequilaKey;
    [self.authController authToken:tequilaKey.iTequilaKey presentationViewController:self.navigationController delegate:self];
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
            self.toolbar.alpha = 0.0;
            self.toolbar.hidden = NO;
            //self.lastUpdateLabel.hidden = NO;
            //self.lastUpdateLabel.text = [NSString stringWithFormat:@"%@ %@", NSLocalizedStringFromTable(@"LastUpdate", @"CamiproPlugin", nil), self.balanceAndTransactions.iDate];
            [self.tableView reloadData];
            self.reloadCardButton.hidden = NO;
            self.reloadCardButton.alpha = 0.0; //iPad
            
            [UIView animateWithDuration:0.2 animations:^{
                self.tableView.alpha = 1.0;
                self.toolbar.alpha = 1.0;
                self.reloadCardButton.alpha = 1.0; //iPad
            }];
            if ([PCUtils isIdiomPad]) {
                self.statsContainerView.hidden = NO;
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
    self.toolbar.hidden = YES;
    //self.lastUpdateLabel.hidden = YES;
    self.navigationItem.rightBarButtonItem.enabled = YES;
    self.statsContainerView.hidden = YES; //iPad
    self.reloadCardButton.hidden = YES; //iPad
    //[CamiproService saveSessionId:nil];
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

- (void)serviceConnectionToServerTimedOut {
    if (!self.sendMailAlertView) {
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
    self.toolbar.hidden = YES;
    //self.lastUpdateLabel.hidden = YES;
    self.statsContainerView.hidden = YES; //iPad
    self.reloadCardButton.hidden = YES; //iPad
    self.navigationItem.rightBarButtonItem.enabled = YES;
}

/* UIActionSheetDelegate delegation */

- (void)actionSheet:(UIActionSheet *)actionSheet willDismissWithButtonIndex:(NSInteger)buttonIndex {
    switch (buttonIndex) {
        case 0: //reload instructions
        {
            [[GANTracker sharedTracker] trackPageview:@"/v3r1/camipro/click/reload" withError:NULL];
            self.sendMailAlertView = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"ReloadInstructions", @"CamiproPlugin", nil) message:NSLocalizedStringFromTable(@"ReloadInstructionsSendMailExplanations", @"CamiproPlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) otherButtonTitles:NSLocalizedStringFromTable(@"Send", @"CamiproPlugin", nil), nil];
            [self.sendMailAlertView show];
            break;
        }
        case 1: //statistics
        {
            [[GANTracker sharedTracker] trackPageview:@"/v3r1/camipro/click/stats" withError:NULL];
            self.statsAlertView = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Statistics", @"CamiproPlugin", nil) message:NSLocalizedStringFromTable(@"Loading...", @"PocketCampus", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) otherButtonTitles:nil];
            [self.statsAlertView show];
            [self startGetStatsRequest];
            break;
        }
        default:
            break;
    }
    
}

/* UIAlertViewDelegate delegation */

- (void)alertView:(UIAlertView *)alertView willDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (alertView == self.sendMailAlertView) {
        self.sendMailAlertView = nil;
        if (buttonIndex == 0) { //Cancel
            [self.camiproService cancelOperationsForDelegate:self];
            return;
        }
        [[GANTracker sharedTracker] trackPageview:@"/v3r1/camipro/click/email" withError:NULL];
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

/* UITableViewDelegation delegation */

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
    return [PCValues tableViewSectionHeaderHeight];
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
    headerView.bounds = CGRectMake(0, 0, self.tableView.frame.size.width, headerView.frame.size.height);
    return headerView;
    
}

/* UITableViewDataSource delegation */

- (UITableViewCell*)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* newCell = nil;
    if (indexPath.section == 0) {
        newCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        newCell.selectionStyle = UITableViewCellSelectionStyleNone;
        newCell.backgroundColor = [PCValues backgroundColor1];
        UILabel* balanceLabel = nil;
        if ([PCUtils isIdiomPad]) {
            balanceLabel = [[UILabel alloc] initWithFrame:CGRectMake(10.0, 0.0, 300.0, kBalanceCellHeightPad)];
            balanceLabel.font = [UIFont systemFontOfSize:48.0];
        } else {
            balanceLabel = [[UILabel alloc] initWithFrame:CGRectMake(10.0, 0.0, 300.0, kBalanceCellHeightPhone)];
            balanceLabel.font = [UIFont systemFontOfSize:34.0];
        }
        balanceLabel.text = [NSString stringWithFormat:@"CHF %.2lf", self.balanceAndTransactions.iBalance];
        balanceLabel.textAlignment = UITextAlignmentCenter;
        balanceLabel.textColor = [PCValues textColor1];
        balanceLabel.backgroundColor = [UIColor clearColor];
        balanceLabel.shadowColor = [PCValues shadowColor1];
        balanceLabel.shadowOffset = [PCValues shadowOffset1];
        balanceLabel.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        [newCell.contentView addSubview:balanceLabel];
        return newCell;
    }
    
    newCell = [self.tableView dequeueReusableCellWithIdentifier:kHistoryCellIdentifier];
    
    Transaction* transcation = [self.balanceAndTransactions.iTransactions objectAtIndex:indexPath.row];
    
    if (newCell == nil) {
        newCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:kHistoryCellIdentifier];
        newCell.selectionStyle = UITableViewCellSelectionStyleNone;
        newCell.backgroundColor = [UIColor clearColor];
        newCell.textLabel.textColor = [PCValues textColor1];
        UILabel* priceLabel = [[UILabel alloc] initWithFrame:CGRectMake(211.0, 15.0, 100.0, 21.0)];
        priceLabel.backgroundColor = [UIColor clearColor];
        priceLabel.tag = kTransactionPriceViewTag;
        priceLabel.font = [UIFont systemFontOfSize:17.0];
        priceLabel.textAlignment = UITextAlignmentRight;
        priceLabel.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin;
        [newCell.contentView addSubview:priceLabel];
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
    if (!self.balanceAndTransactions) {
        return 0;
    }
    switch (section) {
        case 0:
            return 1; //balance
        case 1:
            if (!self.balanceAndTransactions.iTransactions) {
                return 0;
            }
            return [self.balanceAndTransactions.iTransactions count];
        default:
            return 0;
            break;
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (!self.balanceAndTransactions) {
        return 0;
    }
    return 2; //balance and history sections
}

- (void)dealloc
{
    [self.camiproService cancelOperationsForDelegate:self];
}

@end
