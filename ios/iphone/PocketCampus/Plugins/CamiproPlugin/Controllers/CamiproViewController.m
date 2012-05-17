//
//  CamiproViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 17.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "CamiproViewController.h"

#import "PCValues.h"

#import "PCTableViewSectionHeader.h"

#import "ObjectArchiver.h"

static NSString* kHistoryCellIdentifier = @"CamiproHistoryCell";
static NSUInteger kTransactionPriceViewTag = 15;
static CGFloat kBalanceCellHeight = 70.0;

@implementation CamiproViewController

@synthesize tableView, centerActivityIndicator, centerMessageLabel, toolbar;

- (id)init
{
    self = [super initWithNibName:@"CamiproView" bundle:nil];
    if (self) {
        authController = [[AuthenticationController alloc] init];
        camiproService = [[CamiproService sharedInstanceToRetain] retain];
        balanceAndTransactions = nil;
        
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
    self.view.backgroundColor = [PCValues backgroundColor1];
    [self refresh];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}
                                              
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)refresh {
    [centerActivityIndicator startAnimating];
    tableView.hidden = YES;
    toolbar.hidden = YES;
    self.navigationItem.rightBarButtonItem.enabled = NO;
    SessionId* sessionId = [CamiproService lastSessionId];
    if (sessionId == nil) {
        NSLog(@"-> No previously saved sessionId. Requesting credentials...");
        [self showLoginPopup];
    } else {
        NSLog(@"-> Trying to getBalanceAndTransactions with previously saved SessionId");
        [self startBalanceAndTransactionsRequestWithSessionId:sessionId];
    }
}

- (IBAction)logoutPressed {
    [CamiproService saveSessionId:nil];
    [UIView animateWithDuration:0.2 animations:^{
        tableView.alpha = 0.0;
        toolbar.alpha = 0.0;
    } completion:^(BOOL finished) {
        [self.navigationController popViewControllerAnimated:YES];
    }];
}

- (void)showLoginPopup {
    [authController loginToService:TypeOfService_SERVICE_CAMIPRO prefillWithLastUsedUsername:YES delegate:self];
}

- (void)startBalanceAndTransactionsRequestWithSessionId:(SessionId*)sessionId {
    CamiproRequest* request = [[CamiproRequest alloc] initWithISessionId:sessionId iLanguage:[[NSLocale currentLocale] objectForKey:NSLocaleLanguageCode]];
    [camiproService getBalanceAndTransactions:request delegate:self];
    [request release];
}

/* AuthenticationCallbackDelegate delegation */

- (void)gotSessionId:(SessionId*)sessionId {
    NSLog(@"-> gotSessionId");
    [CamiproService saveSessionId:sessionId];
    [self startBalanceAndTransactionsRequestWithSessionId:sessionId];
}

- (void)userCancelledAuthentication {
    [centerActivityIndicator stopAnimating];
    if (self.navigationController.visibleViewController == self) {
        [self.navigationController popViewControllerAnimated:YES]; //leaving plugin
    }
}

- (void)authenticationTimeout {
    [self serviceConnectionToServerTimedOut];
}

/* CamiproServiceDelegate delegation */

- (void)getBalanceAndTransactionsForCamiproRequest:(CamiproRequest*)camiproRequest didReturn:(BalanceAndTransactions*)balanceAndTransactions_ {
    NSLog(@"%@", balanceAndTransactions_);
    switch (balanceAndTransactions_.iStatus) {
        case 407: //user not authenticated (sessionId expired)
            NSLog(@"-> User session has expired. Requesting credientials...");
            [CamiproService saveSessionId:nil];
            [self showLoginPopup];
            break;
        case 404:
            NSLog(@"-> 404 error in status from getBalanceAndTransactionsForCamiproRequest:didReturn:");
            [self getBalanceAndTransactionsFailedForCamiproRequest:camiproRequest];
        case 200: //OK
        {
            NSLog(@"-> BalanceAndTransactions received status success (200)");
            [centerActivityIndicator stopAnimating];
            self.navigationItem.rightBarButtonItem.enabled = YES;
            centerMessageLabel.hidden = YES;
            [balanceAndTransactions release];
            balanceAndTransactions = [balanceAndTransactions_ retain];
            [tableView reloadData];
            tableView.alpha = 0.0;
            tableView.hidden = NO;
            toolbar.alpha = 0.0;
            toolbar.hidden = NO;
            UIBarButtonItem* refreshButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(refresh)];
            [self.navigationItem setRightBarButtonItem:refreshButton animated:YES];
            [refreshButton release];
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
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", nil);
    centerMessageLabel.hidden = NO;
    tableView.hidden = YES;
    toolbar.hidden = YES;
    self.navigationItem.rightBarButtonItem.enabled = YES;
}

- (void)serviceConnectionToServerTimedOut {
    [centerActivityIndicator stopAnimating];
    centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
    centerMessageLabel.hidden = NO;
    tableView.hidden = YES;
    toolbar.hidden = YES;
    self.navigationItem.rightBarButtonItem.enabled = YES;
}


/* UITableViewDelegation delegation */

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return kBalanceCellHeight;
    }
    return 44.0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return [PCValues tableViewSectionHeaderHeight];
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    PCTableViewSectionHeader* headerView = nil;
    switch (section) {
        case 0:
            headerView = [[PCTableViewSectionHeader alloc] initWithSectionTitle:NSLocalizedStringFromTable(@"CamiproBalance", @"CamiproPlugin", nil)];
            break;
        case 1:
            headerView = [[PCTableViewSectionHeader alloc] initWithSectionTitle:NSLocalizedStringFromTable(@"TransactionsHistory", @"CamiproPlugin", nil)];
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
        newCell.backgroundColor = [UIColor clearColor];
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
        newCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:kHistoryCellIdentifier];
        newCell.selectionStyle = UITableViewCellSelectionStyleNone;
        newCell.backgroundColor = [UIColor clearColor];
        newCell.textLabel.textColor = [PCValues textColor1];
        UILabel* priceLabel = [[UILabel alloc] initWithFrame:CGRectMake(211.0, 24.0, 100.0, 17.0)];
        priceLabel.backgroundColor = [UIColor clearColor];
        priceLabel.tag = kTransactionPriceViewTag;
        priceLabel.font = [UIFont systemFontOfSize:15.0];
        priceLabel.textAlignment = UITextAlignmentRight;
        priceLabel.textColor = [PCValues pocketCampusRed];
        [newCell.contentView addSubview:priceLabel];
        [priceLabel release];
    }
    newCell.textLabel.text = transcation.iPlace;
    newCell.detailTextLabel.text = transcation.iDate;
    UILabel* priceLabel = (UILabel*)[newCell.contentView viewWithTag:kTransactionPriceViewTag];
    priceLabel.text = [NSString stringWithFormat:@"CHF %.2lf", transcation.iAmount];
    
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
    [camiproService release];
    [balanceAndTransactions release];
    [super dealloc];
}

@end
