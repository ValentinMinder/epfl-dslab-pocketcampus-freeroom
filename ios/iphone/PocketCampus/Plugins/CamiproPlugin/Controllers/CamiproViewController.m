//
//  CamiproViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 17.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "CamiproViewController.h"

@implementation CamiproViewController

@synthesize tableView, centerActivityIndicator, centerMessageLabel;

- (id)init
{
    self = [super initWithNibName:@"CamiproView" bundle:nil];
    if (self) {
        authController = [[AuthenticationController alloc] init];
        camiproService = [[CamiproService sharedInstanceToRetain] retain];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
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

/* CamiproServiceDelegate delegation */

- (void)getBalanceAndTransactionsForCamiproRequest:(CamiproRequest*)camiproRequest didReturn:(BalanceAndTransactions*)balanceAndTransactions {
    //TODO
}

- (void)getBalanceAndTransactionsFailedForCamiproRequest:(CamiproRequest*)camiproRequest {
    //TODO
}

- (void)serviceConnectionToServerTimedOut {
    //TODO
}


/* UITableViewDelegation delegation */

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    //TODO
}

/* UITableViewDataSource delegation */

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    //TODO
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    //TODO
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    //TODO
}

- (void)dealloc
{
    [authController release];
    [camiproService release];
    [super dealloc];
}

@end
