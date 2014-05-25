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

#import "AuthenticationViewController2.h"

#import "PCEditableTableViewCell.h"

static NSInteger credentialsSectionIndex = 0;
static NSInteger loginOutButtonSectionIndex = 1;
static NSInteger savePasswordSwitchSectionIndex = -1;

static NSInteger const kUsernameRowIndex = 0;
static NSInteger const kPasswordRowIndex = 1;

@interface AuthenticationViewController2 ()

@property (nonatomic, strong) PCEditableTableViewCell* usernameCell;
@property (nonatomic, strong) PCEditableTableViewCell* passwordCell;
@property (nonatomic, strong) PCTableViewCellAdditions* loginCell;
@property (nonatomic, strong) UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, strong) UISwitch* savePasswordSwitch;

@end

@implementation AuthenticationViewController2

#pragma mark - Init

- (id)init {
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.savePasswordSwitchValue = YES; //Default
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
}

#pragma mark - Public

- (void)setState:(AuthenticationViewControllerState)state {
    if (state == _state) {
        return;
    }
    _state = state;
    [self.tableView reloadData];
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

- (void)setShowDoneButton:(BOOL)showDoneButton {
    if (showDoneButton) {
        if (!self.navigationItem.rightBarButtonItem) {
            self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneTapped)];
        }
    } else {
        self.navigationItem.rightBarButtonItem = nil;
    }
}

- (void)setShowSavePasswordSwitch:(BOOL)showSavePasswordSwitch {
    _showSavePasswordSwitch = showSavePasswordSwitch;
    [self.tableView reloadData];
}

- (void)setSavePasswordSwitchValue:(BOOL)savePasswordSwitchValue {
    _savePasswordSwitchValue = savePasswordSwitchValue;
    [self.savePasswordSwitch setOn:savePasswordSwitchValue];
}

- (void)setUsername:(NSString *)username {
    _username = username;
    self.usernameCell.textField.text = username;
}

- (void)setPassword:(NSString *)password {
    _password = password;
    self.passwordCell.textField.text = password;
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

- (void)inputsValueChanged {
    if (self.usernameCell.textField.text.length == 0 || self.passwordCell.textField.text.length == 0) {
        if (self.userClearedUsernameBlock) {
            self.userClearedUsernameBlock();
        }
        self.loginCell.textLabel.enabled = NO;
        self.loginCell.selectionStyle = UITableViewCellSelectionStyleNone;
    } else {
        self.loginCell.textLabel.enabled = YES;
        self.loginCell.selectionStyle = UITableViewCellSelectionStyleGray;
    }
}

- (void)savePasswordSwitchValueChanged {
    _savePasswordSwitchValue = self.savePasswordSwitch.isOn;
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if (indexPath.section == loginOutButtonSectionIndex) {
        if (self.state == AuthenticationViewControllerStateAskCredentials
            || self.state == AuthenticationViewControllerStateWrongCredentials) {
            if (!self.loginBlock) {
                CLSNSLog(@"ERROR: User pressed login but loginBlock is nil");
                return;
            }
            //_ because no need to update cells values
            _username = self.usernameCell.textField.text;
            _password = self.passwordCell.textField.text;
            _savePasswordSwitchValue = self.savePasswordSwitch.isOn;
            self.loginBlock(self.username, self.password, self.savePasswordSwitchValue);
        } else if (self.state == AuthenticationViewControllerStateLoggedIn) {
            if (!self.logoutBlock) {
                CLSNSLog(@"ERROR: User pressed logout but logoutBlock is nil");
                return;
            }
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

- (NSString*)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section {
#warning TODO
    return nil;
}

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    if (self.state == AuthenticationViewControllerStateLoggedIn && self.username && section == loginOutButtonSectionIndex) {
        return [NSString stringWithFormat:NSLocalizedStringFromTable(@"LoggedInAsWithFormat", @"AuthenticationPlugin", nil), self.username];
    }
    return nil;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell = nil;
    if (indexPath.section == credentialsSectionIndex) {
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
                    //self.usernameCell.textField.delegate = self;
                    [self.usernameCell.textField addTarget:self action:@selector(inputsValueChanged) forControlEvents:UIControlEventEditingChanged];
                    self.usernameCell.textField.text = self.username;
                }
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
                    //self.passwordCell.textField.delegate = self;
                    [self.passwordCell.textField addTarget:self action:@selector(inputsValueChanged) forControlEvents:UIControlEventEditingChanged];
                    self.passwordCell.textField.text = self.password;
                }
                cell = self.passwordCell;
                break;
            }
        }
    } else if (indexPath.section == loginOutButtonSectionIndex) {
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
                [self.loginCell.contentView addSubview:self.loadingIndicator];
                self.loadingIndicator.center = CGPointMake(294.0, 22.0);
                self.loadingIndicator.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin;
            }
            cell = self.loginCell;
            [self inputsValueChanged]; //make it enable or not
        }
    } else if (indexPath.section == savePasswordSwitchSectionIndex) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        cell.textLabel.text = NSLocalizedStringFromTable(@"SavePassword", @"AuthenticationPlugin", nil);
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        self.savePasswordSwitch = [[UISwitch alloc] init];
        self.savePasswordSwitch.on = self.savePasswordSwitchValue;
        [self.savePasswordSwitch addTarget:self action:@selector(savePasswordSwitchValueChanged) forControlEvents:UIControlEventValueChanged];
        cell.accessoryView = self.savePasswordSwitch;
    } else {
        //unsupported
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == credentialsSectionIndex) {
        return 2; //username + password
    }
    if (section == loginOutButtonSectionIndex) {
        return 1; //login / logout cell
    }
    if (section == savePasswordSwitchSectionIndex) {
        return 1;
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    NSInteger nbSections = 0;
    if (credentialsSectionIndex >= 0) {
        nbSections++;
    }
    if (loginOutButtonSectionIndex >= 0) {
        nbSections++;
    }
    if (savePasswordSwitchSectionIndex >= 0) {
        nbSections++;
    }
    return nbSections;
}

#pragma mark - Private

- (void)recomputeSectionIndices {
    if (self.state == AuthenticationViewControllerStateLoggedIn) {
        credentialsSectionIndex = -1;
        loginOutButtonSectionIndex = 0;
        savePasswordSwitchSectionIndex = -1;
    } else {
        credentialsSectionIndex = 0;
        loginOutButtonSectionIndex = 1;
        savePasswordSwitchSectionIndex = self.showSavePasswordSwitch ? 2 : -1;
    }
}


@end
