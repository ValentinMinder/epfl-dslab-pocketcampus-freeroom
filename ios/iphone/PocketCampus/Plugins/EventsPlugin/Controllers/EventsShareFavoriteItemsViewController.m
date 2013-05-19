//
//  ShareFavoriteItemsViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 18.05.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "EventsShareFavoriteItemsViewController.h"

#import "EditableTableViewCell.h"

#import "PCUtils.h"

#import "PCValues.h"

@interface EventsShareFavoriteItemsViewController ()

@property (nonatomic, strong) EventsService* eventsService;

@property (nonatomic, strong) EditableTableViewCell* emailCell;

@property (nonatomic, strong) UIActivityIndicatorView* loadingIndicator;

@property (nonatomic) BOOL operationInProgress;

@end

@implementation EventsShareFavoriteItemsViewController

- (id)initWithRelatedEventPool:(EventPool *)eventPool
{
    [PCUtils throwExceptionIfObject:eventPool notKindOfClass:[EventPool class]];
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.relatedEventPool = eventPool;
        self.title = NSLocalizedStringFromTable(@"Share", @"EventsPlugin", nil);
        self.eventsService = [EventsService sharedInstanceToRetain];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.tableView.backgroundColor = [UIColor clearColor];
    UIView* backgroundView = [[UIView alloc] initWithFrame:self.tableView.frame];
    backgroundView.backgroundColor = [PCValues backgroundColor1];;
    self.tableView.backgroundView = backgroundView;
    
	
    
    //self.tableView.tableHeaderView = headerLabel;
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelPressed)];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Send", @"PocketCampus", nil) style:UIBarButtonItemStyleDone target:self action:@selector(sendPressed)];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self textControlValueChanged:self.emailCell.textField]; //force enable/disable buttons
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [self.emailCell.textField becomeFirstResponder];
}

#pragma mark - Public properties

- (void)setRelatedEventPool:(EventPool *)relatedEventPool {
    [PCUtils throwExceptionIfObject:relatedEventPool notKindOfClass:[EventPool class]];
    _relatedEventPool = relatedEventPool;
}

- (void)setPrefilledEmail:(NSString *)prefilledEmail {
    _prefilledEmail = prefilledEmail;
    self.emailCell.textField.text = prefilledEmail;
}

#pragma mark - Buttons actions

- (void)cancelPressed {
    [self.eventsService cancelOperationsForDelegate:self];
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

- (void)sendPressed {
    [self.emailCell.textField resignFirstResponder];
    SendEmailRequest* req = [[SendEmailRequest alloc] initWithEventPoolId:self.relatedEventPool.poolId starredEventItems:[self.eventsService allFavoriteEventItemIds] userTickets:[self.eventsService allUserTickets] emailAddress:self.emailCell.textField.text lang:[PCUtils userLanguageCode]];
    [self.eventsService sendStarredItemsByEmail:req delegate:self];
    self.operationInProgress = YES;
}

#pragma mark - Text controls value changes

-  (void)textControlValueChanged:(UITextField*)textField {
    if (textField == self.emailCell.textField) {
        self.navigationItem.rightBarButtonItem.enabled = ([self validateInputs] && !self.operationInProgress);
    }
}

#pragma mark - Utilities

- (void)setOperationInProgress:(BOOL)operationInProgress {
    _operationInProgress = operationInProgress;
    if (operationInProgress) {
        [self.loadingIndicator startAnimating];
    } else {
        [self.loadingIndicator stopAnimating];
    }
    self.navigationItem.rightBarButtonItem.enabled = !operationInProgress;
    self.emailCell.textField.enabled = !operationInProgress;
}

- (BOOL)validateInputs {
    if (self.emailCell.textField.text.length < 3) {
        //magic value
        return NO;
    }
    if ([self.emailCell.textField.text rangeOfString:@"@"].location == NSNotFound) {
        //verify that there is an @
        return NO;
    }
    return YES;
}

#pragma mark - EventsServiceDelegate

- (void)sendStarredItemsByEmailForRequest:(SendEmailRequest *)request didReturn:(SendEmailReply *)reply {
    self.operationInProgress = NO;
    switch (reply.status) {
        case 200:
            [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
            break;
        default:
            [self sendStarredItemsByEmailFailedForRequest:request];
            break;
    }
}

- (void)sendStarredItemsByEmailFailedForRequest:(SendEmailRequest *)request {
    self.operationInProgress = NO;
    [PCUtils showServerErrorAlert];
}

- (void)serviceConnectionToServerTimedOut {
    self.operationInProgress = NO;
    [PCUtils showConnectionToServerTimedOutAlert];
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        return 70.0;
    }
    return 0.0;
}

- (UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        UILabel* headerLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 280.0, 1.0)];
        headerLabel.text = NSLocalizedStringFromTable(@"SendByEmailInstructions", @"EventsPlugin", nil);
        headerLabel.backgroundColor = [UIColor clearColor];
        headerLabel.numberOfLines = 0;
        headerLabel.textAlignment = UITextAlignmentCenter;
        headerLabel.font = [UIFont systemFontOfSize:16.0];
        headerLabel.textColor = [PCValues textColor1];
        headerLabel.shadowColor = [PCValues shadowColor1];
        headerLabel.shadowOffset = [PCValues shadowOffset1];
        return headerLabel;
    }
    return nil;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 80.0;
}

- (UIView*)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section {
    if (section == 0) {
        UIView* containerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.tableView.frame.size.width, 1.0)];
        containerView.backgroundColor = [UIColor clearColor];
        if (!self.loadingIndicator) {
            self.loadingIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
            self.loadingIndicator.color = [PCValues textColor1];
            //[self.loadingIndicator startAnimating];
        }
        self.loadingIndicator.center = CGPointMake(containerView.frame.size.width/2.0, containerView.frame.size.height/2.0);
        self.loadingIndicator.autoresizingMask = UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin;
        [containerView addSubview:self.loadingIndicator];
        return containerView;
    }
    return nil;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [self.tableView deselectRowAtIndexPath:indexPath animated:NO];
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (!self.emailCell) {
        self.emailCell = [EditableTableViewCell editableCellWithPlaceholder:@"your@email.com"];
        self.emailCell.textLabel.text = @"Email";
        self.emailCell.textField.text = self.prefilledEmail;
        self.emailCell.textField.keyboardType = UIKeyboardTypeEmailAddress;
        self.emailCell.textField.autocorrectionType = UITextAutocorrectionTypeNo;
        self.emailCell.textField.clearButtonMode = UITextFieldViewModeAlways;
        [self.emailCell.textField addTarget:self action:@selector(textControlValueChanged:) forControlEvents:UIControlEventEditingChanged];
    }
    return self.emailCell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 1;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

#pragma mark - Dealloc

- (void)dealloc {
    [self.eventsService cancelOperationsForDelegate:self];
}

@end
