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

#import "AuthenticationViewController.h"

#import "PCEditableTableViewCell.h"

static NSInteger const kUsernameRowIndex = 0;
static NSInteger const kPasswordRowIndex = 1;

@interface AuthenticationViewController ()<UITextFieldDelegate>

@property (nonatomic, strong) PCEditableTableViewCell* usernameCell;
@property (nonatomic, strong) PCEditableTableViewCell* passwordCell;
@property (nonatomic, strong) PCTableViewCellAdditions* loginCell;
@property (nonatomic, strong) UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, strong) UISwitch* savePasswordSwitch;
@property (nonatomic, strong) NSMutableDictionary* showDoneButtonBoolForState;

@property (nonatomic) NSInteger credentialsSectionIndex;
@property (nonatomic) NSInteger loginOutButtonSectionIndex;
@property (nonatomic) NSInteger savePasswordSwitchSectionIndex;

@end

@implementation AuthenticationViewController

#pragma mark - Init

- (id)init {
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.gaiScreenName = @"/authentication";
        self.savePasswordSwitchValue = YES; //Default
        self.showDoneButtonBoolForState = [NSMutableDictionary dictionary];
        self.credentialsSectionIndex = 0; //Default
        self.loginOutButtonSectionIndex = 1; //Default
        self.savePasswordSwitchSectionIndex = -1; //Default
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = [[self class] localizedTitle];
    self.tableView.sectionHeaderHeight = 2.0;
    self.tableView.sectionFooterHeight = 2.0;
    self.tableView.separatorColor = [UIColor clearColor];
}

- (void)viewWillAppear:(BOOL)animated {
    //not calling super on purpose, this is to disable the auto-scrolling when getting text fields focus
    [self trackScreen];
    self.navigationController.view.backgroundColor = [UIColor whiteColor];
    [self.tableView reloadData];
}

#pragma mark - Public

+ (NSString*)localizedTitle {
    return NSLocalizedStringFromTable(@"GasparAccount", @"AuthenticationPlugin", nil);
}

- (void)setState:(AuthenticationViewControllerState)state {
    [self setState:state animated:NO];
}

- (void)setState:(AuthenticationViewControllerState)state animated:(BOOL)animated {
    if (state == _state) {
        return;
    }
    _state = state;
    [self recomputeSectionIndices];
    [self updateDoneButtonVisibility];
    if (animated) {
        [UIView animateWithDuration:0.4 animations:^{
            self.tableView.alpha = 0.0;
        } completion:^(BOOL finished) {
            [self.tableView reloadData];
        }];
        [UIView animateWithDuration:0.4 animations:^{
            self.tableView.alpha = 1.0;
        }];
    } else {
        [self.tableView reloadData];
    }
}

- (void)setShowCancelButton:(BOOL)showCancelButton {
    if (showCancelButton) {
        if (!self.navigationItem.leftBarButtonItem) {
            self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelTapped)];
        }
    } else {
        self.navigationItem.leftBarButtonItem = nil;
    }
}

- (void)setShowDoneButton:(BOOL)showDoneButton forState:(AuthenticationViewControllerState)state {
    self.showDoneButtonBoolForState[@(state)] = @(showDoneButton);
    if (state == self.state) {
        [self updateDoneButtonVisibility];
    }
}

- (void)setShowSavePasswordSwitch:(BOOL)showSavePasswordSwitch {
    _showSavePasswordSwitch = showSavePasswordSwitch;
    [self recomputeSectionIndices];
    [self.tableView reloadData];
}

- (void)setSavePasswordSwitchValue:(BOOL)savePasswordSwitchValue {
    if (!self.savePasswordSwitch) {
        self.savePasswordSwitch = [[UISwitch alloc] init];
        [self.savePasswordSwitch addTarget:self action:@selector(savePasswordSwitchValueChanged) forControlEvents:UIControlEventValueChanged];
        self.savePasswordSwitch.enabled = (self.state != AuthenticationViewControllerStateLoading);
    }
    [self.savePasswordSwitch setOn:savePasswordSwitchValue];
}

- (BOOL)savePasswordSwitchValue {
    if (self.savePasswordSwitch) {
        return self.savePasswordSwitch.isOn;
    }
    return YES;
}

- (void)setUsername:(NSString *)username {
    _username = username;
    self.usernameCell.textField.text = username;
    [self inputsValueChanged];
}

- (void)setPassword:(NSString *)password {
    _password = password;
    self.passwordCell.textField.text = password;
    [self inputsValueChanged];
}

- (void)focusOnInput {
    if (self.usernameCell.textField.text.length == 0) {
        [self.usernameCell.textField becomeFirstResponder];
    } else {
        [self.passwordCell.textField becomeFirstResponder];
    }
}

#pragma mark - Actions and observation

- (void)cancelTapped {
    if (self.userTappedCancelBlock) {
        self.userTappedCancelBlock();
    }
}

- (void)doneTapped {
    if (self.userTappedDoneBlock) {
        self.userTappedDoneBlock();
    }
}

- (void)savePasswordSwitchValueChanged {
    UIResponder* firstResponder = nil;
    if (self.usernameCell.textField.isFirstResponder) {
        firstResponder = self.usernameCell.textField;
    } else if (self.passwordCell.textField.isFirstResponder) {
        firstResponder = self.passwordCell.textField;
    }
    [self.tableView reloadData];
    [firstResponder becomeFirstResponder]; //keep first responder after reloadData
}

- (void)inputsValueChanged {
    if (self.state == AuthenticationViewControllerStateLoading
        || self.usernameCell.textField.text.length == 0
        || self.passwordCell.textField.text.length == 0) {
        self.loginCell.textLabel.enabled = NO;
        self.loginCell.selectionStyle = UITableViewCellSelectionStyleNone;
    } else {
        self.loginCell.textLabel.enabled = YES;
        self.loginCell.selectionStyle = UITableViewCellSelectionStyleGray;
    }
}

#pragma mark - UITextFieldDelegate

- (BOOL)textFieldShouldClear:(UITextField *)textField {
    if (textField == self.usernameCell.textField) {
        if (self.userClearedUsernameBlock) {
            self.userClearedUsernameBlock();
        }
    }
    return YES;
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    self.tableView.contentInset = UIEdgeInsetsMake(self.tableView.contentInset.top, self.tableView.contentInset.left, self.tableView.contentInset.bottom+220.0, self.tableView.contentInset.right);
    
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    self.tableView.contentInset = UIEdgeInsetsMake(self.tableView.contentInset.top, self.tableView.contentInset.left, self.tableView.contentInset.bottom-220.0, self.tableView.contentInset.right);
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    if (textField == self.usernameCell.textField) {
        [self.passwordCell.textField becomeFirstResponder];
    } else if (textField == self.passwordCell.textField) {
        [self tableView:self.tableView didSelectRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:self.loginOutButtonSectionIndex]]; //simulate login cell pressed
    } else {
        //nothing, unknown
    }
    return YES;
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if (indexPath.section == self.loginOutButtonSectionIndex) {
        if (self.loginCell.textLabel.enabled
            && (self.state == AuthenticationViewControllerStateAskCredentials
            || self.state == AuthenticationViewControllerStateWrongCredentials)) {
            if (!self.loginBlock) {
                CLSNSLog(@"!! ERROR: User pressed login but loginBlock is nil");
                return;
            }
            [self trackAction:@"LogIn" contentInfo:(self.savePasswordSwitch.isOn || !self.showSavePasswordSwitch) ? @"SavePasswordYes" : @"SavePasswordNo"];
            //_ because no need to update cells values
            _username = self.usernameCell.textField.text;
            _password = self.passwordCell.textField.text;
            self.loginBlock(self.username, self.password, self.savePasswordSwitch.isOn);
        } else if (self.state == AuthenticationViewControllerStateLoggedIn) {
            if (!self.logoutBlock) {
                CLSNSLog(@"!! ERROR: User pressed logout but logoutBlock is nil");
                return;
            }
            [self trackAction:@"LogOut"];
            _username = nil;
            _password = nil;
            self.logoutBlock();
        } else {
            //nothing else
        }
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (section == 0) { //testing constant because it's about margin above FIRST section (not specific one)
        if (self.state == AuthenticationViewControllerStateLoggedIn) {
            return 40.0;
        } else {
            return 5.0;
        }
    }
    return 0.0;
}

#pragma mark - UITableViewDataSource

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    if (self.state == AuthenticationViewControllerStateLoggedIn && self.username && section == self.loginOutButtonSectionIndex) {
        return [NSString stringWithFormat:NSLocalizedStringFromTable(@"LoggedInAsWithFormat", @"AuthenticationPlugin", nil), self.username];
    }
    return nil;
}

- (NSString*)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section {
    if (section == self.credentialsSectionIndex && self.state == AuthenticationViewControllerStateWrongCredentials) {
        return NSLocalizedStringFromTable(@"BadCredentials", @"AuthenticationPlugin", nil);
    }
    if (section == (self.tableView.numberOfSections - 1) && self.bottomMessageBlock) {
        return self.bottomMessageBlock(self);
    }
    return nil;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell = nil;
    if (indexPath.section == self.credentialsSectionIndex) {
        switch (indexPath.row) {
            case kUsernameRowIndex:
            {
                if (!self.usernameCell) {
                    self.usernameCell = [PCEditableTableViewCell editableCellWithPlaceholder:NSLocalizedStringFromTable(@"Username", @"AuthenticationPlugin", nil)];
                    self.usernameCell.selectionStyle = UITableViewCellSelectionStyleNone;
                    self.usernameCell.textField.autocorrectionType = UITextAutocorrectionTypeNo;
                    self.usernameCell.textField.autocapitalizationType = UITextAutocapitalizationTypeNone;
                    self.usernameCell.textField.returnKeyType = UIReturnKeyNext;
                    self.usernameCell.textField.clearButtonMode = UITextFieldViewModeAlways;
                    self.usernameCell.textField.delegate = self;
                    [self.usernameCell.textField addTarget:self action:@selector(inputsValueChanged) forControlEvents:UIControlEventEditingChanged];
                    self.usernameCell.textField.text = self.username;
                }
                self.usernameCell.textField.enabled = (self.state != AuthenticationViewControllerStateLoading);
                cell = self.usernameCell;
                break;
            }
            case kPasswordRowIndex:
            {
                if (!self.passwordCell) {
                    self.passwordCell = [PCEditableTableViewCell editableCellWithPlaceholder:NSLocalizedStringFromTable(@"Password", @"AuthenticationPlugin", nil)];
                    self.passwordCell.selectionStyle = UITableViewCellSelectionStyleNone;
                    self.passwordCell.textField.secureTextEntry = YES;
                    self.passwordCell.textField.returnKeyType = UIReturnKeyGo;
                    self.passwordCell.textField.delegate = self;
                    [self.passwordCell.textField addTarget:self action:@selector(inputsValueChanged) forControlEvents:UIControlEventEditingChanged];
                    self.passwordCell.textField.text = self.password;
                }
                self.passwordCell.textField.enabled = (self.state != AuthenticationViewControllerStateLoading);
                cell = self.passwordCell;
                break;
            }
        }
    } else if (indexPath.section == self.loginOutButtonSectionIndex) {
        if (self.state == AuthenticationViewControllerStateLoggedIn) {
            PCTableViewCellAdditions* logoutCell = [[PCTableViewCellAdditions alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            logoutCell.textLabel.text = NSLocalizedStringFromTable(@"Logout", @"AuthenticationPlugin", nil);
            logoutCell.textLabel.textColor = [PCValues pocketCampusRed];
            [logoutCell setAccessibilityTraitsBlock:^UIAccessibilityTraits() {
                return UIAccessibilityTraitStaticText | UIAccessibilityTraitButton;
            }];
            cell = logoutCell;
        } else {
            if (!self.loginCell) {
                self.loginCell = [[PCTableViewCellAdditions alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];;
                self.loginCell.textLabel.text = NSLocalizedStringFromTable(@"Login", @"AuthenticationPlugin", nil);
                self.loginCell.textLabel.textColor = [PCValues pocketCampusRed];
                self.loginCell.textLabel.enabled = NO;
                __weak __typeof(self) welf = self;
                [self.loginCell setAccessibilityHintBlock:^NSString *{
                    return welf.loginCell.textLabel.enabled ? NSLocalizedStringFromTable(@"StartsAuthenticationProcess", @"AuthenticationPlugin", nil) : NSLocalizedStringFromTable(@"PleaseFirstIndicateBothUsernamePassword", @"AuthenticationPlugin", nil);
                }];
                [self.loginCell setAccessibilityTraitsBlock:^UIAccessibilityTraits{
                    return UIAccessibilityTraitButton | UIAccessibilityTraitStaticText;
                }];
                self.loadingIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
                self.loadingIndicator.hidesWhenStopped = YES;
                [self.loginCell.contentView addSubview:self.loadingIndicator];
                self.loadingIndicator.center = CGPointMake(294.0, 22.0);
                self.loadingIndicator.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin;
            }
            cell = self.loginCell;
            if (self.state == AuthenticationViewControllerStateLoading) {
                [self.loadingIndicator startAnimating];
            } else {
                [self.loadingIndicator stopAnimating];
            }
            [self inputsValueChanged]; //make it enable or not
        }
    } else if (indexPath.section == self.savePasswordSwitchSectionIndex) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        cell.textLabel.text = NSLocalizedStringFromTable(@"SavePassword", @"AuthenticationPlugin", nil);
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        if (!self.savePasswordSwitch) {
            self.savePasswordSwitch = [[UISwitch alloc] init];
            self.savePasswordSwitch.on = self.savePasswordSwitchValue;
            [self.savePasswordSwitch addTarget:self action:@selector(savePasswordSwitchValueChanged) forControlEvents:UIControlEventValueChanged];
        }
        self.savePasswordSwitch.enabled = (self.state != AuthenticationViewControllerStateLoading);
        cell.textLabel.enabled = self.savePasswordSwitch.enabled;
        cell.accessoryView = self.savePasswordSwitch;
    } else {
        //unsupported
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == self.credentialsSectionIndex) {
        return 2; //username + password
    }
    if (section == self.loginOutButtonSectionIndex) {
        return 1; //login / logout cell
    }
    if (section == self.savePasswordSwitchSectionIndex) {
        return 1;
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    NSInteger nbSections = 0;
    if (self.credentialsSectionIndex >= 0) {
        nbSections++;
    }
    if (self.loginOutButtonSectionIndex >= 0) {
        nbSections++;
    }
    if (self.savePasswordSwitchSectionIndex >= 0) {
        nbSections++;
    }
    return nbSections;
}

#pragma mark - Private

- (void)recomputeSectionIndices {
    if (self.state == AuthenticationViewControllerStateLoggedIn) {
        self.credentialsSectionIndex = -1;
        self.loginOutButtonSectionIndex = 0;
        self.savePasswordSwitchSectionIndex = -1;
    } else {
        self.credentialsSectionIndex = 0;
        self.loginOutButtonSectionIndex = 1;
        self.savePasswordSwitchSectionIndex = self.showSavePasswordSwitch ? 2 : -1;
    }
}

- (void)updateDoneButtonVisibility {
    if ([self.showDoneButtonBoolForState[@(self.state)] boolValue]) {
        if (!self.navigationItem.rightBarButtonItem) {
            self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Done", @"PocketCampus", nil) style:UIBarButtonItemStylePlain target:self action:@selector(doneTapped)];
        }
    } else {
        self.navigationItem.rightBarButtonItem = nil;
    }
}

@end
