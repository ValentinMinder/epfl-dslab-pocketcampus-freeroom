//
//  GasparViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 17.07.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GasparViewController.h"

#import "AuthenticationService.h"

#import "EditableTableViewCell.h"

#import "PCValues.h"

@implementation GasparViewController

@synthesize tableView, delegate, token, showSavePasswordSwitch, hideGasparUsageAccountMessage, presentationMode, viewControllerForPresentation;

- (id)init;
{
    self = [super initWithNibName:@"GasparView" bundle:nil];
    if (self) {
        presentationMode = PresentationModeNavStack; //default
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
    self.title = [[self class] localizedTitle];
    tableView.backgroundColor = [UIColor clearColor];
    UIView* backgroundView = [[UIView alloc] initWithFrame:tableView.frame];
    backgroundView.backgroundColor = [PCValues backgroundColor1];;
    tableView.backgroundView = backgroundView;
    [backgroundView release];
    if (presentationMode == PresentationModeModal || presentationMode == PresentationModeTryHidden) {
        UIBarButtonItem* cancelButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelPressed)];
        self.navigationItem.rightBarButtonItem = cancelButton;
        [cancelButton release];
    }
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

- (void)cancelPressed {
    [authenticationService cancelOperationsForDelegate:self];
    [AuthenticationService savePassword:nil];
    if (presentationMode == PresentationModeModal) {
        [self.presentingViewController dismissViewControllerAnimated:YES completion:^{
            if ([(NSObject*)self.delegate respondsToSelector:@selector(userCancelledAuthentication)]) {
                [(NSObject*)self.delegate performSelectorOnMainThread:@selector(userCancelledAuthentication) withObject:nil waitUntilDone:YES];
            }
        }];
    }
}

- (BOOL)checkUserInputs {
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
}

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
            [AuthenticationService saveUsername:nil];
            [AuthenticationService savePassword:nil];
            [AuthenticationService enqueueLogoutNotification];
            username = nil;
            [password release];
            password = nil;
            [NSTimer scheduledTimerWithTimeInterval:0.3 target:self selector:@selector(reloadTableViewWithEffect) userInfo:nil repeats:NO];
        }
    } else {
        if (indexPath.section == 1 && loginCell.textLabel.enabled) { //login button
            if ([self checkUserInputs]) {
                [usernameTextField resignFirstResponder];
                [passwordTextField resignFirstResponder];
                [loadingIndicator startAnimating];
                loginCell.textLabel.enabled = NO;
                loginCell.selectionStyle = UITableViewCellSelectionStyleNone;
                [username release];
                username = [usernameTextField.text retain];
                [password release];
                password = [passwordTextField.text retain];
                [authenticationService loginToTequilaWithUser:username password:password delegate:self];
            }
        }
    }
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
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
            if (!hideGasparUsageAccountMessage) {
                text = NSLocalizedStringFromTable(@"GasparAccountRequiredFor", @"AuthenticationPlugin", nil);
            } else {
                text = @"";
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
                        EditableTableViewCell* cell = [EditableTableViewCell editableCellWithPlaceholder:NSLocalizedStringFromTable(@"Username", @"AuthenticationPlugin", nil)];
                        [usernameTextField release];
                        usernameTextField = [cell.textField retain];
                        usernameTextField.autocorrectionType = UITextAutocorrectionTypeNo;
                        usernameTextField.autocapitalizationType = UITextAutocapitalizationTypeNone;
                        usernameTextField.returnKeyType = UIReturnKeyNext;
                        usernameTextField.clearButtonMode = UITextFieldViewModeWhileEditing;
                        usernameTextField.delegate = self;
                        
                        NSString* text = [AuthenticationService savedUsername];
                        if (!text && username) {
                            text = username;
                        }
                        if (text && presentationMode == PresentationModeModal) {
                            usernameTextField.text = text;
                        }
                        return cell;
                        break;
                    }
                    case 1: //password
                    {
                        EditableTableViewCell* cell = [EditableTableViewCell editableCellWithPlaceholder:NSLocalizedStringFromTable(@"Password", @"AuthenticationPlugin", nil)];
                        [passwordTextField release];
                        passwordTextField = [cell.textField retain];
                        passwordTextField.secureTextEntry = YES;
                        passwordTextField.returnKeyType = UIReturnKeyGo;
                        cell.textField.delegate = self;
                        return cell;
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
            loginCell.selectionStyle = UITableViewCellSelectionStyleGray;
            loadingIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
            [loginCell.contentView addSubview:loadingIndicator];
            loadingIndicator.center = CGPointMake(275.0, 22.0);
            [loadingIndicator release];
            return cell;
            break;
        }
        case 2: //save password switch
        {
            UITableViewCell* cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
            cell.textLabel.text = NSLocalizedStringFromTable(@"SavePassword", @"AuthenticationPlugin", nil);
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
    isLoggedIn = ([AuthenticationService savedPassword] != nil);
    if (isLoggedIn) {
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
    [token release];
    token = [token_ retain];
    if (delegate_ == nil) {
        @throw [NSException exceptionWithName:@"askCredientialsForTypeOfService:delegate: bad delegate" reason:@"delegate cannot be nil" userInfo:nil];
    }
    self.delegate = delegate_;
    [username release];
    username = [[AuthenticationService savedUsername] retain];
    [password release];
    password = [[AuthenticationService savedPassword] retain];
    [authenticationService loginToTequilaWithUser:username password:password delegate:self];
}

/* AuthenticationServiceDelegate delegation */

/* STEP 1 */
- (void)loginToTequilaDidReturn:(ASIHTTPRequest*)request {
    NSLog(@"-> loginToTequilaDidReturn");
    [tequilaCookie release];
    tequilaCookie = nil;
    for(NSHTTPCookie* cookie in request.responseCookies) {
        if ([cookie.name isEqualToString:TEQUILA_COOKIE_NAME]) {
            tequilaCookie = [cookie.value retain];
        }
    }
    if (tequilaCookie == nil) { //means bad credentials
        [AuthenticationService savePassword:nil];
        [errorMessage release];
        errorMessage = [NSLocalizedStringFromTable(@"BadCredentials", @"AuthenticationPlugin", nil) retain];
        if (presentationMode == PresentationModeTryHidden) {
            if (!self.viewControllerForPresentation) {
                @throw [NSException exceptionWithName:@"nil viewControllerForPresentation" reason:@"could not present GasparViewController after failing silent authentication." userInfo:nil];
            }
            presentationMode = PresentationModeModal;
            [viewControllerForPresentation presentViewController:self animated:YES completion:^{
            [self focusOnInput];
            }];
        } else {
            [tableView reloadData];
            [NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(focusOnInput) userInfo:nil repeats:NO];
        }
        
    } else {
        [errorMessage release];
        errorMessage = nil;
        [AuthenticationService saveUsername:username];
        if(!showSavePasswordSwitch || [savePasswordSwitch isOn]) { //TODO password switch
            [AuthenticationService savePassword:password];
        }
        if (token) {
            [authenticationService authenticateToken:token withTequilaCookie:tequilaCookie delegate:self];
        } else { //mean user just wanted to login to tequila without loggin in to service. From settings for example.
            [self reloadTableViewWithEffect]; //will show logged-in UI then
        }
        
    }
    [password release];
    password = nil;
}

- (void)loginToTequilaFailed:(ASIHTTPRequest*)request {
    NSLog(@"-> loginToTequilaFailed");
    [self connectionError];
}

/* STEP 2 */
- (void)authenticateTokenWithTequilaDidReturn:(ASIHTTPRequest*)request{
    NSLog(@"-> authenticateTokenWithTequilaDidReturn");
    NSString* redir = [request.responseHeaders objectForKey:@"Location"];
    if(redir == nil) {
        if ([(NSObject*)self.delegate respondsToSelector:@selector(invalidToken)]) {
            [(NSObject*)self.delegate performSelectorOnMainThread:@selector(invalidToken) withObject:nil waitUntilDone:NO];
        }
    } else {
        if ([(NSObject*)self.delegate respondsToSelector:@selector(authenticationSucceeded)]) {
            [(NSObject*)self.delegate performSelectorOnMainThread:@selector(authenticationSucceeded) withObject:nil waitUntilDone:YES];
        }
        if (presentationMode != PresentationModeTryHidden && (showSavePasswordSwitch && ![savePasswordSwitch isOn]) && [(NSObject*)self.delegate respondsToSelector:@selector(deleteSessionWhenFinished)]) {
            [(NSObject*)self.delegate performSelectorOnMainThread:@selector(deleteSessionWhenFinished) withObject:nil waitUntilDone:YES];
        }
        [loadingIndicator stopAnimating];
        [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];

    }
}

- (void)authenticateTokenWithTequilaFailed:(ASIHTTPRequest*)request {
    NSLog(@"authenticateTokenWithTequilaFailed");
    [self connectionError];
}

- (void)connectionError {
    [loadingIndicator stopAnimating];
    loginCell.textLabel.enabled = YES;
    loginCell.selectionStyle = UITableViewCellSelectionStyleBlue;
    UIAlertView* alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alertView show];
    [alertView release];
}

- (void)serviceConnectionToServerTimedOut {
    NSLog(@"serviceConnectionToServerTimedOut");
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
    [loginCell release];
    [savePasswordSwitch release];
    [token release];
    [super dealloc];
}

@end
