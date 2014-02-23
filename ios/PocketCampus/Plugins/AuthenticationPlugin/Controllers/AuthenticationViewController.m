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




//  Created by Loïc Gardiol on 17.07.12.



#import "AuthenticationViewController.h"

#import "AuthenticationService.h"

#import "PCEditableTableViewCell.h"

@interface AuthenticationViewController ()<UITextFieldDelegate, AuthenticationServiceDelegate>

@property (nonatomic, strong) UITextField* usernameTextField;
@property (nonatomic, strong) UITextField* passwordTextField;
@property (nonatomic, strong) UISwitch* savePasswordSwitch;
@property (nonatomic, strong) PCEditableTableViewCell* usernameCell;
@property (nonatomic, strong) PCEditableTableViewCell* passwordCell;
@property (nonatomic, strong) PCTableViewCellAdditions* loginCell;
@property (nonatomic, strong) NSString* errorMessage;
@property (nonatomic) BOOL isLoggedIn;
@property (nonatomic, strong) UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, strong) AuthenticationService* authenticationService;
@property (nonatomic, strong) NSString* username;
@property (nonatomic, strong) NSString* password;

//Persisted property, reflects last value if savePasswordSwitch
@property (nonatomic) BOOL savePassword;

@end

@implementation AuthenticationViewController

#pragma mark - Init

- (id)init;
{
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.gaiScreenName = @"/authentication";
        self.presentationMode = PresentationModeNavStack; //default
        self.modalPresentationStyle = UIModalPresentationFormSheet; //prevents full-screen on iPad
        self.authenticationService = [AuthenticationService sharedInstanceToRetain];
        self.isLoggedIn = [AuthenticationService isLoggedIn];
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    self.navigationController.view.backgroundColor = [UIColor whiteColor];
    self.title = [[self class] localizedTitle];
    self.tableView.sectionHeaderHeight = 2.0;
    self.tableView.sectionFooterHeight = 2.0;
    self.tableView.separatorColor = [UIColor clearColor];
    if (self.presentationMode == PresentationModeModal || self.presentationMode == PresentationModeTryHidden) {
        self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelPressed)];
    } else if (self.presentationMode == PresentationModeNavStack && self.isLoggedIn) {
        [self showDoneButton];
    } else {
        //nothing, unknown
    }
}

- (void)viewWillAppear:(BOOL)animated {
    //not calling super on purpose, this is to disable the auto-scrolling when getting text fields focus
    [self trackScreen];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    if ([PCUtils isIdiomPad]) {
        return UIInterfaceOrientationMaskAll;
    } else {
        return UIInterfaceOrientationMaskPortrait;
    }
    
}

#pragma mark - Public methods

- (void)authenticateSilentlyToken:(NSString*)token_ delegate:(id<AuthenticationDelegate>)delegate_ {
    self.token = token_;
    if (!delegate_) {
        @throw [NSException exceptionWithName:@"askCredientialsForTypeOfService:delegate: bad delegate" reason:@"delegate cannot be nil" userInfo:nil];
    }
    self.delegate = delegate_;
    self.username = [AuthenticationService savedUsername];
    self.password = [AuthenticationService savedPasswordForUsername:self.username];
    [self.authenticationService loginToTequilaWithUser:self.username password:self.password delegate:self];
}

- (void)focusOnInput {
    if (![self.usernameTextField.text isEqualToString:@""]) {
        [self.passwordTextField becomeFirstResponder];
    } else {
        [self.usernameTextField becomeFirstResponder];
    }
}

+ (NSString*)localizedTitle {
    return NSLocalizedStringFromTable(@"GasparAccount", @"AuthenticationPlugin", nil);
}

#pragma mark - Actions & buttons management

- (void)showDoneButton {
    UIBarButtonItem* doneButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Done", @"PocketCampus", nil) style:UIBarButtonItemStylePlain target:self action:@selector(donePressed)];
    [self.navigationItem setRightBarButtonItem:doneButton animated:YES];
}

- (void)cancelPressed {
    [self.authenticationService cancelOperationsForDelegate:self];
    [AuthenticationService deleteSavedPasswordForUsername:[AuthenticationService savedUsername]];
    if (self.presentationMode == PresentationModeModal) {
        [self.presentingViewController dismissViewControllerAnimated:YES completion:^{
            if ([(NSObject*)self.delegate respondsToSelector:@selector(userCancelledAuthentication)]) {
                [(NSObject*)self.delegate performSelectorOnMainThread:@selector(userCancelledAuthentication) withObject:nil waitUntilDone:YES];
                [AuthenticationService enqueueLogoutNotification];
            }
        }];
    } else {
        [AuthenticationService enqueueLogoutNotification];
    }
}

- (void)donePressed {
    if (self.presentingViewController && self.presentationMode == PresentationModeNavStack) {
        [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
    }
}

#pragma mark - Save password switch

- (void)savePasswordSwitchValueChanged {
    self.savePassword = self.savePasswordSwitch.isOn;
}

static NSString* const kSavePasswordBoolKey = @"AuthenticationSavePassword";
static NSString* const kSavePasswordSwitchStateOldKey = @"savePasswordSwitch"; //old key

- (BOOL)savePassword {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        //check if transferred from PCObjectArchiver to PCConfig default
        static NSString* const kAuthTransferedPasswordSwitchStateKey = @"AuthTransferedPasswordSwitchState";
        if (![[PCConfig defaults] boolForKey:kAuthTransferedPasswordSwitchStateKey]) {
            NSNumber* nsBool = (NSNumber*)[PCObjectArchiver objectForKey:kSavePasswordSwitchStateOldKey andPluginName:@"authentication"];
            if (nsBool) {
                [[PCConfig defaults] setBool:[nsBool boolValue] forKey:kSavePasswordBoolKey];
            }
            [[PCConfig defaults] setBool:YES forKey:kAuthTransferedPasswordSwitchStateKey];
            [[PCConfig defaults] synchronize];
        }
    });
    NSNumber* boolNb = [[PCConfig defaults] objectForKey:kSavePasswordBoolKey];
    if (boolNb) {
        return [boolNb boolValue];
    }
    return YES; //default if not specified yet
}

- (void)setSavePassword:(BOOL)savePassword {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        //delete old saved state if any
        [PCObjectArchiver saveObject:nil forKey:kSavePasswordSwitchStateOldKey andPluginName:@"authentication"];
    });
    [[PCConfig defaults] setBool:savePassword forKey:kSavePasswordBoolKey];
    [[PCConfig defaults] synchronize];
}

#pragma mark - Inputs things

- (void)inputFieldsDidChange {
    if (self.usernameTextField.text.length == 0 || self.passwordTextField.text.length == 0) {
        if (self.usernameTextField.text.length == 0 && !self.savePasswordSwitch.isOn) { //means user has cleared field to remove his username
            [AuthenticationService saveUsername:nil];
        }
        self.loginCell.textLabel.enabled = NO;
        self.loginCell.selectionStyle = UITableViewCellSelectionStyleNone;
    } else {
        self.loginCell.textLabel.enabled = YES;
        self.loginCell.selectionStyle = UITableViewCellSelectionStyleGray;
    }
}

#pragma mark - AuthenticationCallbackDelegate

/* STEP 1 */

- (void)loginToTequilaDidSuceedWithTequilaCookie:(NSHTTPCookie *)tequilaCookie {
    self.errorMessage = nil;
    [AuthenticationService saveUsername:self.username];
    
    if (self.showSavePasswordSwitch) {
        if (self.savePasswordSwitch.isOn) {
            [AuthenticationService savePassword:self.password forUsername:self.username];
        } else {
            [AuthenticationService deleteSavedPasswordForUsername:self.username];
        }
    } else {
        [AuthenticationService savePassword:self.password forUsername:self.username]; //default: save password
    }
    
    if (self.token) {
        [self.authenticationService authenticateToken:self.token withTequilaCookie:tequilaCookie delegate:self];
    } else { //mean user just wanted to login to tequila without loggin in to service. From settings for example.
        self.usernameCell = nil;
        self.passwordCell = nil;
        [self reloadTableViewWithEffect]; //will show logged-in UI then
    }
    self.password = nil;
}

- (void)loginToTequilaFailedWithReason:(AuthenticationTequilaLoginFailureReason)reason {
    CLSNSLog(@"-> loginToTequilaFailedloginToTequilaFailedWithReason: %d", reason);
    switch (reason) {
        case AuthenticationTequilaLoginFailureReasonBadCredentials:
        {
            [AuthenticationService deleteSavedPasswordForUsername:self.username];
            self.errorMessage = NSLocalizedStringFromTable(@"BadCredentials", @"AuthenticationPlugin", nil);
            if (self.presentationMode == PresentationModeTryHidden) {
                if (!self.viewControllerForPresentation) {
                    @throw [NSException exceptionWithName:@"nil viewControllerForPresentation" reason:@"could not present GasparViewController after failing silent authentication." userInfo:nil];
                }
                self.presentationMode = PresentationModeModal;
                self.usernameTextField.enabled = YES;
                self.passwordTextField.enabled = YES;
                
                UINavigationController* tmpNavController = [[UINavigationController alloc] initWithRootViewController:self]; //so that nav bar is shown
                [self.viewControllerForPresentation presentViewController:tmpNavController animated:YES completion:^{
                    [self focusOnInput];
                }];
            } else {
                self.usernameCell = nil;
                self.passwordCell = nil;
                self.password = nil;
                [self.tableView reloadData];
                [NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(focusOnInput) userInfo:nil repeats:NO];
            }
            break;
        }
        default:
            [self connectionError];
            break;
    }
}

/* STEP 2 */

- (void)authenticateDidSucceedForToken:(NSString *)token tequilaCookie:(NSHTTPCookie *)tequilaCookie {
    if ([(NSObject*)self.delegate respondsToSelector:@selector(authenticationSucceededPersistSession:)]) {
        [self.delegate authenticationSucceededPersistSession:self.savePassword];
    }
    [self.loadingIndicator stopAnimating];
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

- (void)authenticateFailedForToken:(NSString *)token tequilaCookie:(NSHTTPCookie *)tequilaCookie {
    if ([(NSObject*)self.delegate respondsToSelector:@selector(invalidToken)]) {
        [(NSObject*)self.delegate performSelectorOnMainThread:@selector(invalidToken) withObject:nil waitUntilDone:NO];
    }
    [self.loadingIndicator stopAnimating];
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

- (void)connectionError {
    [self.loadingIndicator stopAnimating];
    self.usernameTextField.enabled = YES;
    self.passwordTextField.enabled = YES;
    self.loginCell.textLabel.enabled = YES;
    self.loginCell.selectionStyle = UITableViewCellSelectionStyleGray;
    [PCUtils showConnectionToServerTimedOutAlert];
    
    if (self.presentationMode == PresentationModeTryHidden && [(NSObject*)self.delegate respondsToSelector:@selector(serviceConnectionToServerFailed)]) {
        [(NSObject*)self.delegate performSelectorOnMainThread:@selector(serviceConnectionToServerFailed) withObject:nil waitUntilDone:YES];
    }
}

- (void)serviceConnectionToServerFailed {
    [self connectionError];
}

#pragma mark - UITextFieldDelegate

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    self.tableView.contentInset = UIEdgeInsetsMake(self.tableView.contentInset.top, self.tableView.contentInset.left, self.tableView.contentInset.bottom+220.0, self.tableView.contentInset.right);
    /*if (textField == passwordTextField) {
        [tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:1 inSection:0] atScrollPosition:UITableViewScrollPositionTop animated:YES];
    }*/
    
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    self.tableView.contentInset = UIEdgeInsetsMake(self.tableView.contentInset.top, self.tableView.contentInset.left, self.tableView.contentInset.bottom-220.0, self.tableView.contentInset.right);
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    if (textField == self.usernameTextField) {
        [self.passwordTextField becomeFirstResponder];
    } else if (textField == self.passwordTextField) {
        [self tableView:self.tableView didSelectRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:1]]; //simulate login cell pressed
    } else {
        //nothing, unknown
    }
    return YES;
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView_ didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (self.isLoggedIn) {
        if (indexPath.section == 0) { //logout button
            [self trackAction:@"LogOut"];
            [AuthenticationService deleteSavedPasswordForUsername:[AuthenticationService savedUsername]];
            [AuthenticationService saveUsername:nil];
            [AuthenticationService enqueueLogoutNotification];
            self.username = nil;
            self.password = nil;
            self.usernameCell = nil;
            self.passwordCell = nil;
            [NSTimer scheduledTimerWithTimeInterval:0.3 target:self selector:@selector(reloadTableViewWithEffect) userInfo:nil repeats:NO];
        }
    } else {
        if (indexPath.section == 1 && self.loginCell.textLabel.enabled) { //login button
            [self trackAction:@"LogIn" contentInfo:(self.savePasswordSwitch.isOn || !self.showSavePasswordSwitch) ? @"SavePasswordYes" : @"SavePasswordNo"];
            [self.loadingIndicator startAnimating];
            [self.usernameTextField resignFirstResponder];
            [self.passwordTextField resignFirstResponder];
            self.usernameTextField.enabled = NO;
            self.passwordTextField.enabled = NO;
            self.loginCell.textLabel.enabled = NO;
            self.loginCell.selectionStyle = UITableViewCellSelectionStyleNone;
            self.username = self.usernameTextField.text;
            self.password = self.passwordTextField.text;
            [self.authenticationService loginToTequilaWithUser:self.username password:self.password delegate:self];
        }
    }
    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        if (self.presentationMode == PresentationModeNavStack && self.isLoggedIn) {
            return 40.0;
        } else {
            return 5.0;
        }
        
    }
    return 0.0;
}

- (NSString*)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section {
    if ((section == 0 && self.isLoggedIn) || (section == 1 && !self.isLoggedIn)) {
        NSString* text;
        if (self.isLoggedIn) {
            text = NSLocalizedStringFromTable(@"LoggedInExplanationLong", @"AuthenticationPlugin", nil);
        } else {
            if (self.errorMessage) {
                text = self.errorMessage;
            } else {
                if (!self.hideGasparUsageAccountMessage) {
                    text = NSLocalizedStringFromTable(@"GasparAccountRequiredFor", @"AuthenticationPlugin", nil);
                } else {
                    text = @"";
                }
            }
        }
        
        return text;
    }
    return nil;
}

#pragma mark - UITableViewDataSource

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    switch (section) {
        case 0: //gaspar account
        {
            if (self.isLoggedIn) {
                return [NSString stringWithFormat:NSLocalizedStringFromTable(@"LoggedInAsWithFormat", @"AuthenticationPlugin", nil), [AuthenticationService savedUsername]];
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
            
            if (self.isLoggedIn) { //logout button
                PCTableViewCellAdditions* logoutCell = [[PCTableViewCellAdditions alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
                logoutCell.textLabel.text = NSLocalizedStringFromTable(@"Logout", @"AuthenticationPlugin", nil);
                logoutCell.textLabel.textColor = [PCValues pocketCampusRed];
                [logoutCell setAccessibilityTraitsBlock:^UIAccessibilityTraits() {
                    return UIAccessibilityTraitStaticText | UIAccessibilityTraitButton;
                }];
                return logoutCell;
            } else { //username, password
                switch (indexPath.row) {
                    case 0: //username
                    {
                        if (self.usernameCell) {
                            return self.usernameCell;
                        }
                        self.usernameCell = [PCEditableTableViewCell editableCellWithPlaceholder:NSLocalizedStringFromTable(@"Username", @"AuthenticationPlugin", nil)];
                        self.usernameTextField = self.usernameCell.textField;
                        self.usernameTextField.autocorrectionType = UITextAutocorrectionTypeNo;
                        self.usernameTextField.autocapitalizationType = UITextAutocapitalizationTypeNone;
                        self.usernameTextField.returnKeyType = UIReturnKeyNext;
                        self.usernameTextField.clearButtonMode = UITextFieldViewModeAlways;
                        self.usernameTextField.delegate = self;
                        
                        NSString* text = [AuthenticationService savedUsername];
                        if (!text && self.username) {
                            text = self.username;
                        }
                        
                        if (text && (self.presentationMode == PresentationModeModal || self.presentationMode == PresentationModeNavStack)) {
                            self.usernameTextField.text = text;
                        }
                        [self.usernameTextField addTarget:self action:@selector(inputFieldsDidChange) forControlEvents:UIControlEventEditingChanged];
                        return self.usernameCell;
                        break;
                    }
                    case 1: //password
                    {
                        if (self.passwordCell) {
                            return self.passwordCell;
                        }
                        self.passwordCell = [PCEditableTableViewCell editableCellWithPlaceholder:NSLocalizedStringFromTable(@"Password", @"AuthenticationPlugin", nil)];
                        self.passwordTextField = self.passwordCell.textField;
                        self.passwordTextField.secureTextEntry = YES;
                        self.passwordTextField.returnKeyType = UIReturnKeyGo;
                        self.passwordCell.textField.delegate = self;
                        [self.passwordTextField addTarget:self action:@selector(inputFieldsDidChange) forControlEvents:UIControlEventEditingChanged];
                        return self.passwordCell;
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
            PCTableViewCellAdditions* cell = [[PCTableViewCellAdditions alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            self.loginCell = cell;
            self.loginCell.textLabel.text = NSLocalizedStringFromTable(@"Login", @"AuthenticationPlugin", nil);
            self.loginCell.textLabel.textColor = [PCValues pocketCampusRed];
            self.loginCell.textLabel.enabled = NO;
            __weak __typeof(self) weakSelf = self;
            [self.loginCell setAccessibilityHintBlock:^NSString *{
                return weakSelf.loginCell.textLabel.enabled ? NSLocalizedStringFromTable(@"StartsAuthenticationProcess", @"AuthenticationPlugin", nil) : NSLocalizedStringFromTable(@"PleaseFirstIndicateBothUsernamePassword", @"AuthenticationPlugin", nil);
            }];
            [self.loginCell setAccessibilityTraitsBlock:^UIAccessibilityTraits{
                return UIAccessibilityTraitButton | UIAccessibilityTraitStaticText;
            }];
            self.loadingIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
            [self.loginCell.contentView addSubview:self.loadingIndicator];
            self.loadingIndicator.center = CGPointMake(294.0, 22.0);
            self.loadingIndicator.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin;
            [self inputFieldsDidChange];
            return cell;
            break;
        }
        case 2: //save password switch
        {
            UITableViewCell* cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.textLabel.text = NSLocalizedStringFromTable(@"SavePassword", @"AuthenticationPlugin", nil);
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            self.savePasswordSwitch = [[UISwitch alloc] init];
            self.savePasswordSwitch.on = self.savePassword;
            [self.savePasswordSwitch addTarget:self action:@selector(savePasswordSwitchValueChanged) forControlEvents:UIControlEventValueChanged];
            cell.accessoryView = self.savePasswordSwitch;
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
            if (self.isLoggedIn) {
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
    self.isLoggedIn = [AuthenticationService isLoggedIn];
    if (self.isLoggedIn) {
        [self showDoneButton];
        return 1; //only logout button
    } else {
        if (self.showSavePasswordSwitch) {
            return 3;//username/password section, login button section, save password switch section
        } else {
            return 2; //username/password section, login button section
        }
    }
}

- (void)reloadTableViewWithEffect {
    [UIView animateWithDuration:0.4 animations:^{
        self.tableView.alpha = 0.0;
    } completion:^(BOOL finished) {
        [self.tableView reloadData];
    }];
    [UIView animateWithDuration:0.4 animations:^{
        self.tableView.alpha = 1.0;
    }];
}

#pragma mark - Dealloc

- (void)dealloc
{
    self.delegate = nil;
    [self.authenticationService cancelOperationsForDelegate:self];
    @try {
        [[NSNotificationCenter defaultCenter] removeObserver:self];
    }
    @catch (NSException *exception) {}
}

@end
