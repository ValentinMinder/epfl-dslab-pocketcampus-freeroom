//
//  GasparViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 17.07.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "GasparViewController.h"

#import "AuthenticationService.h"

#import "EditableTableViewCell.h"

#import "PCValues.h"

#import "PCUtils.h"

@implementation GasparViewController

@synthesize tableView, delegate, token, showSavePasswordSwitch, hideGasparUsageAccountMessage, presentationMode, viewControllerForPresentation;

- (id)init;
{
    self = [super initWithNibName:@"GasparView" bundle:nil];
    if (self) {
        presentationMode = PresentationModeNavStack; //default
        self.modalPresentationStyle = UIModalPresentationFormSheet; //prevents full-screen on iPad
        showSavePasswordSwitch = NO; //default
        hideGasparUsageAccountMessage = NO; //default
        viewControllerForPresentation = nil;
        errorMessage = nil;
        token = nil;
        usernameTextField = nil;
        passwordTextField = nil;
        loginCell = nil;
        savePasswordSwitch = nil;
        authenticationService = [[AuthenticationService sharedInstanceToRetain] retain];
        isLoggedIn = ([AuthenticationService savedPasswordForUsername:[AuthenticationService savedUsername]] != nil);
    }
    return self;
}

+ (NSString*)localizedTitle {
    return NSLocalizedStringFromTable(@"GasparAccount", @"AuthenticationPlugin", nil);
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/authentication" withError:NULL];
    self.title = [[self class] localizedTitle];
    tableView.backgroundColor = [UIColor clearColor];
    UIView* backgroundView = [[UIView alloc] initWithFrame:tableView.frame];
    backgroundView.backgroundColor = [PCValues backgroundColor1];;
    tableView.backgroundView = backgroundView;
    [backgroundView release];
    if (presentationMode == PresentationModeModal || presentationMode == PresentationModeTryHidden) {
        UIBarButtonItem* cancelButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelPressed)];
        self.navigationItem.leftBarButtonItem = cancelButton;
        [cancelButton release];
    } else if (presentationMode == PresentationModeNavStack && isLoggedIn) {
        [self showDoneButton];
    } else {
        //nothing, unknown
    }
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

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation //iOS 5
{
    if ([PCUtils isIdiomPad]) {
        return YES;
    } else {
        return (interfaceOrientation == UIInterfaceOrientationPortrait);
    }
}

- (void)showDoneButton {
    UIBarButtonItem* doneButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Done", @"PocketCampus", nil) style:UIBarButtonItemStylePlain target:self action:@selector(donePressed)];
    [self.navigationItem setRightBarButtonItem:doneButton animated:YES];
    [doneButton release];
}

- (void)cancelPressed {
    [authenticationService cancelOperationsForDelegate:self];
    [AuthenticationService deleteSavedPasswordForUsername:[AuthenticationService savedUsername]];
    if (presentationMode == PresentationModeModal) {
        [self.presentingViewController dismissViewControllerAnimated:YES completion:^{
            if ([(NSObject*)self.delegate respondsToSelector:@selector(userCancelledAuthentication)]) {
                [(NSObject*)self.delegate performSelectorOnMainThread:@selector(userCancelledAuthentication) withObject:nil waitUntilDone:YES];
                [AuthenticationService enqueueLogoutNotificationDelayed:NO];
            }
        }];
    } else {
        [AuthenticationService enqueueLogoutNotificationDelayed:NO];
    }
}

- (void)donePressed {
    if (self.presentingViewController && presentationMode == PresentationModeNavStack) {
        [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
    }
}

/*- (BOOL)checkUserInputs {
    UIAlertView* alertView;
    if ([usernameTextField.text isEqualToString:@""]) {
        alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"UsernameCannotBeEmpty", @"AuthenticationPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alertView show];
        [alertView release];
        [usernameTextField becomeFirstResponder];
        return NO;
    }
    if ([passwordTextField.text isEqualToString:@""]) {
        alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"PasswordCannotBeEmpty", @"AuthenticationPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alertView show];
        [alertView release];
        [passwordTextField becomeFirstResponder];
        return NO;
    }
    return YES;
}*/

- (void)focusOnInput {
    if (![usernameTextField.text isEqualToString:@""]) {
        [passwordTextField becomeFirstResponder];
    } else {
        [usernameTextField becomeFirstResponder];
    }
}

- (void)savePasswordSwitchValueChanged {
    [AuthenticationService savePasswordSwitchState:savePasswordSwitch.isOn];
}

- (void)inputFieldsDidChange {
    if (usernameTextField.text.length == 0 || passwordTextField.text.length == 0) {
        if (usernameTextField.text.length == 0 && !savePasswordSwitch.isOn) { //means user has cleared field to remove his username
            [AuthenticationService saveUsername:nil];
        }
        loginCell.textLabel.enabled = NO;
        loginCell.selectionStyle = UITableViewCellSelectionStyleNone;
    } else {
        loginCell.textLabel.enabled = YES;
        loginCell.selectionStyle = UITableViewCellSelectionStyleGray;
    }
}

/* UITextFieldDelegate delegation */

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    tableView.contentInset = UIEdgeInsetsMake(0.0, 0.0, 220.0, 0.0);
    /*if (textField == passwordTextField) {
        [tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:1 inSection:0] atScrollPosition:UITableViewScrollPositionTop animated:YES];
    }*/
    
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    tableView.contentInset = UIEdgeInsetsMake(0.0, 0.0, 0.0, 0.0);
}



- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    if (textField == usernameTextField) {
        [passwordTextField becomeFirstResponder];
    } else if (textField == passwordTextField) {
        [self tableView:tableView didSelectRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:1]]; //simulate login cell pressed
    } else {
        //nothing, unknown
    }
    return YES;
}

/* UITableViewDelegate delegation */

- (void)tableView:(UITableView *)tableView_ didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (isLoggedIn) {
        if (indexPath.section == 0) { //logout button
            [AuthenticationService deleteSavedPasswordForUsername:[AuthenticationService savedUsername]];
            [AuthenticationService saveUsername:nil];
            [AuthenticationService enqueueLogoutNotificationDelayed:NO];
            username = nil;
            [password release];
            password = nil;
            [usernameCell release];
            usernameCell = nil;
            [passwordCell release];
            passwordCell = nil;
            [NSTimer scheduledTimerWithTimeInterval:0.3 target:self selector:@selector(reloadTableViewWithEffect) userInfo:nil repeats:NO];
        }
    } else {
        if (indexPath.section == 1 && loginCell.textLabel.enabled) { //login button
            [[GANTracker sharedTracker] trackPageview:@"/v3r1/authentication/click/login" withError:NULL];
            [loadingIndicator startAnimating];
            [usernameTextField resignFirstResponder];
            usernameTextField.enabled = NO;
            [passwordTextField resignFirstResponder];
            passwordTextField.enabled = NO;
            loginCell.textLabel.enabled = NO;
            loginCell.selectionStyle = UITableViewCellSelectionStyleNone;
            [username release];
            username = [usernameTextField.text retain];
            [password release];
            password = [passwordTextField.text retain];
            [authenticationService loginToTequilaWithUser:username password:password delegate:self];
        }
    }
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        if (presentationMode == PresentationModeNavStack && isLoggedIn) {
            return 40.0;
        } else {
            return 5.0;
        }
        
    }
    return 0.0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    
    NSString* text;
    if (isLoggedIn && section == 0) {
        text = NSLocalizedStringFromTable(@"LoggedInExplanationLong", @"AuthenticationPlugin", nil);
        UIFont* font = [UIFont systemFontOfSize:16.0];
        CGSize reqSize = [text sizeWithFont:font constrainedToSize:CGSizeMake(260.0, 600.0)];
        return reqSize.height+15.0;
    } else if (!isLoggedIn && section == 1) {
        if (errorMessage) {
            text = errorMessage;
        } else {
            if (hideGasparUsageAccountMessage) {
                return 0.0;
            } else {
                text = NSLocalizedStringFromTable(@"GasparAccountRequiredFor", @"AuthenticationPlugin", nil);
                
            }
        }
        UIFont* font = [UIFont systemFontOfSize:16.0];
        CGSize reqSize = [text sizeWithFont:font constrainedToSize:CGSizeMake(260.0, 600.0)];
        return reqSize.height+15.0;
    } else {
        return 0.0;
    }
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section {
    
    if ((section == 0 && isLoggedIn) || (section == 1 && !isLoggedIn)) {
        NSString* text;
        if (isLoggedIn) {
            text = NSLocalizedStringFromTable(@"LoggedInExplanationLong", @"AuthenticationPlugin", nil);
        } else {
            if (errorMessage) {
                text = errorMessage;
            } else {
                if (!hideGasparUsageAccountMessage) {
                    text = NSLocalizedStringFromTable(@"GasparAccountRequiredFor", @"AuthenticationPlugin", nil);
                } else {
                    text = @"";
                }
            }
        }
        
        UILabel* label = [[UILabel alloc] init];
        label.text = text;
        UIFont* font = [UIFont systemFontOfSize:16.0];
        CGSize reqSize = [text sizeWithFont:font constrainedToSize:CGSizeMake(260.0, 600.0)];
        label.frame = CGRectMake(0, 0, 260.0, reqSize.height);
        label.numberOfLines = 0;
        label.textAlignment = UITextAlignmentCenter;
        label.backgroundColor = [UIColor clearColor];
        label.font = font;
        if (errorMessage) {
            label.textColor = [PCValues pocketCampusRed];
        } else {
            label.textColor = [PCValues textColor1];
        }
        label.shadowOffset = [PCValues shadowOffset1];
        label.shadowColor = [UIColor whiteColor];
        label.adjustsFontSizeToFitWidth = NO;
        label.text = text;
        return [label autorelease];
    }
    
    return nil;
}

/* UITableViewDataSource delegation */

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    switch (section) {
        case 0: //gaspar account
        {
            if (isLoggedIn) {
                return [NSString stringWithFormat:@"%@ « %@ »",NSLocalizedStringFromTable(@"LoggedInAs", @"AuthenticationPlugin", nil), [AuthenticationService savedUsername]];
            }
            break;
        }
        default:
            break;
    }
    return @"";
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case 0: //gaspar account
        {
            
            if (isLoggedIn) { //logout button
                UITableViewCell* cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
                cell.textLabel.text = NSLocalizedStringFromTable(@"Logout", @"AuthenticationPlugin", nil);
                cell.textLabel.textAlignment = UITextAlignmentCenter;
                cell.selectionStyle = UITableViewCellSelectionStyleGray;
                return cell;
            } else { //username, password
                switch (indexPath.row) {
                    case 0: //username
                    {
                        if (usernameCell) {
                            return usernameCell;
                        }
                        usernameCell = [[EditableTableViewCell editableCellWithPlaceholder:NSLocalizedStringFromTable(@"Username", @"AuthenticationPlugin", nil)] retain];
                        [usernameTextField release];
                        usernameTextField = [usernameCell.textField retain];
                        usernameTextField.autocorrectionType = UITextAutocorrectionTypeNo;
                        usernameTextField.autocapitalizationType = UITextAutocapitalizationTypeNone;
                        usernameTextField.returnKeyType = UIReturnKeyNext;
                        usernameTextField.clearButtonMode = UITextFieldViewModeAlways;
                        usernameTextField.delegate = self;
                        
                        NSString* text = [AuthenticationService savedUsername];
                        if (!text && username) {
                            text = username;
                        }
                        
                        if (text && (presentationMode == PresentationModeModal || presentationMode == PresentationModeNavStack)) {
                            usernameTextField.text = text;
                        }
                        [usernameTextField addTarget:self action:@selector(inputFieldsDidChange) forControlEvents:UIControlEventEditingChanged];
                        return usernameCell;
                        break;
                    }
                    case 1: //password
                    {
                        if (passwordCell) {
                            return passwordCell;
                        }
                        passwordCell = [[EditableTableViewCell editableCellWithPlaceholder:NSLocalizedStringFromTable(@"Password", @"AuthenticationPlugin", nil)] retain];
                        [passwordTextField release];
                        passwordTextField = [passwordCell.textField retain];
                        passwordTextField.secureTextEntry = YES;
                        passwordTextField.returnKeyType = UIReturnKeyGo;
                        passwordCell.textField.delegate = self;
                        [passwordTextField addTarget:self action:@selector(inputFieldsDidChange) forControlEvents:UIControlEventEditingChanged];
                        return passwordCell;
                        break;
                    }
                    default:
                        break;
                }
            }
            break;   
        }
        case 1: //login cell button
        {
            UITableViewCell* cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
            [loginCell release];
            loginCell = [cell retain];
            loginCell.textLabel.text = NSLocalizedStringFromTable(@"Login", @"AuthenticationPlugin", nil);
            loginCell.textLabel.textAlignment = UITextAlignmentCenter;
            loginCell.textLabel.textColor = [UIColor colorWithRed:81.0/255.0 green:102.0/255.0 blue:145.0/255.0 alpha:1.0];
            loginCell.selectionStyle = UITableViewCellSelectionStyleNone;
            loginCell.textLabel.enabled = NO;
            loadingIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
            [loginCell.contentView addSubview:loadingIndicator];
            loadingIndicator.center = CGPointMake(30.0, 22.0);
            [loadingIndicator release];
            [self inputFieldsDidChange];
            return cell;
            break;
        }
        case 2: //save password switch
        {
            UITableViewCell* cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
            cell.textLabel.text = NSLocalizedStringFromTable(@"SavePassword", @"AuthenticationPlugin", nil);
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            [savePasswordSwitch release];
            savePasswordSwitch = [[UISwitch alloc] init];
            BOOL on = YES; //default
            NSNumber* onNSNumber = [AuthenticationService savePasswordSwitchWasOn];
            if (onNSNumber != nil && ![onNSNumber boolValue]) { //previously save state AND this state was off
                on = NO;
            }
            savePasswordSwitch.on = on;
            [savePasswordSwitch addTarget:self action:@selector(savePasswordSwitchValueChanged) forControlEvents:UIControlEventValueChanged];
            cell.accessoryView = savePasswordSwitch;
            return cell;
            break;
        }
        default:
            break;
    }
    return nil;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case 0:
        {
            if (isLoggedIn) {
                return 1; //only logout button
            } else {
                return 2; //username, password
            }
            break;
        }
        case 1:
            return 1; //only login button
        case 2: //savePasswordSwitch
            return 1;
        default:
            break;
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    isLoggedIn = ([AuthenticationService savedPasswordForUsername:[AuthenticationService savedUsername]] != nil);
    if (isLoggedIn) {
        [self showDoneButton];
        return 1; //only logout button
    } else {
        if (showSavePasswordSwitch) {
            return 3;//username/password section, login button section, save password switch section
        } else {
            return 2; //username/password section, login button section
        }
    }
}

- (void)reloadTableViewWithEffect {
    [UIView animateWithDuration:0.4 animations:^{
        tableView.alpha = 0.0;
    } completion:^(BOOL finished) {
        [tableView reloadData];
    }];
    [UIView animateWithDuration:0.4 animations:^{
        tableView.alpha = 1.0;
    }];
}

/* instance methods */

- (void)authenticateSilentlyToken:(NSString*)token_ delegate:(id<AuthenticationCallbackDelegate>)delegate_ {
    self.token = token_;
    if (!delegate_) {
        @throw [NSException exceptionWithName:@"askCredientialsForTypeOfService:delegate: bad delegate" reason:@"delegate cannot be nil" userInfo:nil];
    }
    self.delegate = delegate_;
    [username release];
    username = [[AuthenticationService savedUsername] retain];
    [password release];
    password = [[AuthenticationService savedPasswordForUsername:username] retain];
    [authenticationService loginToTequilaWithUser:username password:password delegate:self];
}

/* AuthenticationServiceDelegate delegation */

/* STEP 1 */
- (void)loginToTequilaDidReturn:(ASIHTTPRequest*)request {
    NSString* tequilaCookie = nil;
    for(NSHTTPCookie* cookie in request.responseCookies) {
        if ([cookie.name isEqualToString:TEQUILA_COOKIE_NAME]) {
            tequilaCookie = cookie.value;
        }
    }
    if (!tequilaCookie) { //means bad credentials
        [AuthenticationService deleteSavedPasswordForUsername:username];
        [errorMessage release];
        errorMessage = [NSLocalizedStringFromTable(@"BadCredentials", @"AuthenticationPlugin", nil) retain];
        if (presentationMode == PresentationModeTryHidden) {
            if (!self.viewControllerForPresentation) {
                @throw [NSException exceptionWithName:@"nil viewControllerForPresentation" reason:@"could not present GasparViewController after failing silent authentication." userInfo:nil];
            }
            presentationMode = PresentationModeModal;
            usernameTextField.enabled = YES;
            passwordTextField.enabled = YES;
            
            UINavigationController* tmpNavController = [[UINavigationController alloc] initWithRootViewController:self]; //so that nav bar is shown
            tmpNavController.navigationBar.tintColor = [PCValues pocketCampusRed];
            [viewControllerForPresentation presentViewController:tmpNavController animated:YES completion:^{
                [self focusOnInput];
            }];
            [tmpNavController release];
        } else {
            [usernameCell release];
            usernameCell = nil;
            [passwordCell release];
            passwordCell = nil;
            [password release];
            password = nil;
            [tableView reloadData];
            [NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(focusOnInput) userInfo:nil repeats:NO];
        }
        
    } else {
        [errorMessage release];
        errorMessage = nil;
        [AuthenticationService saveUsername:username];
        if(!showSavePasswordSwitch || (showSavePasswordSwitch && [savePasswordSwitch isOn])) {
            [AuthenticationService savePassword:password forUsername:username];
        }
        if (token) {
            [authenticationService authenticateToken:token withTequilaCookie:tequilaCookie delegate:self];
        } else { //mean user just wanted to login to tequila without loggin in to service. From settings for example.
            [usernameCell release];
            usernameCell = nil;
            [passwordCell release];
            passwordCell = nil;
            [self reloadTableViewWithEffect]; //will show logged-in UI then
        }
        [password release];
        password = nil;
    }
    
}

- (void)loginToTequilaFailed:(ASIHTTPRequest*)request {
    NSLog(@"-> loginToTequilaFailed");
    [self connectionError];
}

/* STEP 2 */
- (void)authenticateTokenWithTequilaDidReturn:(ASIHTTPRequest*)request{
    NSString* redir = [request.responseHeaders objectForKey:@"Location"];
    if(redir == nil) {
        if ([(NSObject*)self.delegate respondsToSelector:@selector(invalidToken)]) {
            [(NSObject*)self.delegate performSelectorOnMainThread:@selector(invalidToken) withObject:nil waitUntilDone:NO];
        }
    } else {
        if ([(NSObject*)self.delegate respondsToSelector:@selector(authenticationSucceeded)]) {
            [(NSObject*)self.delegate performSelectorOnMainThread:@selector(authenticationSucceeded) withObject:nil waitUntilDone:YES];
        }
        if (presentationMode != PresentationModeTryHidden && (showSavePasswordSwitch && ![savePasswordSwitch isOn])) {
            [AuthenticationService enqueueLogoutNotificationDelayed:YES];
        }
    }
    [loadingIndicator stopAnimating];
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

- (void)authenticateTokenWithTequilaFailed:(ASIHTTPRequest*)request {
    NSLog(@"authenticateTokenWithTequilaFailed");
    [self connectionError];
}

- (void)connectionError {
    [loadingIndicator stopAnimating];
    loginCell.textLabel.enabled = YES;
    loginCell.selectionStyle = UITableViewCellSelectionStyleGray;
    UIAlertView* alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alertView show];
    [alertView release];
    
    if (presentationMode == PresentationModeTryHidden && [(NSObject*)self.delegate respondsToSelector:@selector(serviceConnectionToServerTimedOut)]) {
        [(NSObject*)self.delegate performSelectorOnMainThread:@selector(serviceConnectionToServerTimedOut) withObject:nil waitUntilDone:YES];
    }
}

- (void)serviceConnectionToServerTimedOut {
    [self connectionError];
}

- (void)dealloc
{
    self.delegate = nil;
    [authenticationService cancelOperationsForDelegate:self];
    [authenticationService release];
    [username release];
    [password release];
    [usernameTextField release];
    [passwordTextField release];
    [usernameCell release];
    [passwordCell release];
    [loginCell release];
    [savePasswordSwitch release];
    [token release];
    [super dealloc];
}

@end
