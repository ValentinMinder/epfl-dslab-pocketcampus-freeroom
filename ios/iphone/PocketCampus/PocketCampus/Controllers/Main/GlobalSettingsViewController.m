//
//  GlobalSettingsViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 13.07.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GlobalSettingsViewController.h"

#import "PCValues.h"

#import "EditableTableViewCell.h"

@implementation GlobalSettingsViewController

static BOOL isLoggedInTest = NO;

@synthesize tableView;

- (id)init
{
    self = [super initWithNibName:@"GlobalSettingsView" bundle:nil];
    if (self) {
        // Custom initialization
        cancelButtonDisplayed = NO;
        textEditing = NO;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    self.title = NSLocalizedStringFromTable(@"Settings", @"PocketCampus", nil);
    tableView.backgroundColor = [UIColor clearColor];
    UIView* backgroundView = [[UIView alloc] initWithFrame:tableView.frame];
    backgroundView.backgroundColor = [PCValues backgroundColor1];;
    tableView.backgroundView = backgroundView;
    [backgroundView release];
    [self setRightBarButtonItemDone];
    
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void)setRightBarButtonItemDone {
    UIBarButtonItem* button = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Done", @"PocketCampus", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(doneBarButtonPressed)];
    [self.navigationItem setRightBarButtonItem:button animated:YES];
    [button release];
    cancelButtonDisplayed = NO;
}

- (void)setRightBarButtonItemCancel {
    if (cancelButtonDisplayed) {
        return;
    }
    UIBarButtonItem* button = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelBarButtonPressed)];
    [self.navigationItem setRightBarButtonItem:button animated:YES];
    [button release];
    cancelButtonDisplayed = YES;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)doneBarButtonPressed {
    if ([self.presentingViewController respondsToSelector:@selector(dismissViewControllerAnimated:completion:)]) { // >= iOS 5.0
        [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
    } else {
        [self.presentingViewController dismissModalViewControllerAnimated:YES];
    }  
}

- (void)cancelBarButtonPressed {
    if (textEditing) {
        [self.view endEditing:YES]; //resigns first responder (focus) from any text field
        [self setRightBarButtonItemDone];
    } else {
        //TODO : cancel authentication and dismiss settings
    }
}

/* UITextFieldDelegate delegation */

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    textEditing = YES;
    [self setRightBarButtonItemCancel];
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    textEditing = NO;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    if (textField == usernameTextField) {
        [passwordTextField becomeFirstResponder];
    } else if (textField == passwordTextField) {
        //TODO login
    } else {
        //nothing, unknown
    }
    return YES;
}

/* UITableViewDelegate delegation */

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    //TODO
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    switch (section) {
        case 0: //gaspar account
        {               
            NSString* text;
            if (isLoggedInTest) {
                text = NSLocalizedStringFromTable(@"LoggedInExplanationLong", @"PocketCampus", nil);
            } else {
                text = NSLocalizedStringFromTable(@"GasparAccountRequiredFor", @"PocketCampus", nil);
            }
            UIFont* font = [UIFont systemFontOfSize:16.0];
            CGSize reqSize = [text sizeWithFont:font constrainedToSize:CGSizeMake(260.0, 600.0)];
            return reqSize.height+15.0;
            break;
        }
            
        default:
            break;
    }
    
    return 0.0;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section {
    
    switch (section) {
        case 0:
        {

            NSString* text;
            if (isLoggedInTest) {
                text = NSLocalizedStringFromTable(@"LoggedInExplanationLong", @"PocketCampus", nil);
            } else {
                text = NSLocalizedStringFromTable(@"GasparAccountRequiredFor", @"PocketCampus", nil);
            }
            
            UILabel* label = [[UILabel alloc] init];
            label.text = text;
            UIFont* font = [UIFont systemFontOfSize:16.0];
            CGSize reqSize = [text sizeWithFont:font constrainedToSize:CGSizeMake(260.0, 600.0)];
            label.frame = CGRectMake(0, 0, 260.0, reqSize.height);
            label.numberOfLines = 5;
            label.textAlignment = UITextAlignmentCenter;
            label.backgroundColor = [UIColor clearColor];
            label.font = font;
            label.textColor = [PCValues textColor1];
            label.shadowOffset = [PCValues shadowOffset1];
            label.shadowColor = [UIColor whiteColor];
            label.adjustsFontSizeToFitWidth = NO;
            label.text = text;
            return [label autorelease];
            break;
        }
        default:
            break;
    }
    
    return nil;
}

/* UITableViewDataSource delegation */

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    switch (section) {
        case 0: //gaspar account
        {
            BOOL isLoggedIn = isLoggedInTest; //TODO
            NSString* username = @"test"; //TODO
            if (isLoggedIn) {
                return [NSString stringWithFormat:@"%@ - %@",NSLocalizedStringFromTable(@"GasparAccount", @"PocketCampus", nil), username];
            } else {
                return NSLocalizedStringFromTable(@"GasparAccount", @"PocketCampus", nil);
            }
            break;
        }
        case 1: //about
            return @"PocketCampus";
            break;
        default:
            return @"";
            break;
    }
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case 0: //gaspar account
        {
            
            
            BOOL isLoggedIn = isLoggedInTest; //TODO
            
            if (isLoggedIn) { //logout button
                UITableViewCell* cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
                cell.textLabel.text = NSLocalizedStringFromTable(@"Logout", @"PocketCampus", nil);
                cell.textLabel.textAlignment = UITextAlignmentCenter;
                return cell;
            } else {
                switch (indexPath.row) {
                    case 0: //username
                    {
                        EditableTableViewCell* cell = [EditableTableViewCell editableCellWithPlaceholder:NSLocalizedStringFromTable(@"Username", @"AuthenticationPlugin", nil)];
                        usernameTextField = cell.textField;
                        usernameTextField.autocorrectionType = UITextAutocorrectionTypeNo;
                        usernameTextField.autocapitalizationType = UITextAutocapitalizationTypeNone;
                        usernameTextField.returnKeyType = UIReturnKeyNext;
                        usernameTextField.clearButtonMode = UITextFieldViewModeWhileEditing;
                        usernameTextField.delegate = self;
                        return cell;
                        break;
                    }
                    case 1: //password
                    {
                        EditableTableViewCell* cell = [EditableTableViewCell editableCellWithPlaceholder:NSLocalizedStringFromTable(@"Password", @"AuthenticationPlugin", nil)];
                        passwordTextField = cell.textField;
                        passwordTextField.secureTextEntry = YES;
                        passwordTextField.returnKeyType = UIReturnKeyGo;
                        cell.textField.delegate = self;
                        return cell;
                        break;
                    }
                    case 2: //login button
                    {
                        UITableViewCell* cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
                        cell.textLabel.text = NSLocalizedStringFromTable(@"Login", @"PocketCampus", nil);
                        cell.textLabel.textAlignment = UITextAlignmentCenter;
                        //cell.textLabel.textColor = [UIColor cyanColor];
                        return cell;
                        break;
                    }
                    default:
                        break;
                }
            }
            break;   
        }
        case 1: //about
        {
            UITableViewCell* cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
            cell.textLabel.text = NSLocalizedStringFromTable(@"About", @"PocketCampus", nil);
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            return cell;
        }
        default:
            break;
    }
    return nil;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case 0: //gaspar account
        {
            if (isLoggedInTest) {
                return 1; //only logout button
            } else {
                return 3; //username, password, login button
            }
            break;
        }
        case 1: //about
            return 1;
        default:
            return 0;
            break;
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2; //TODO
}

@end
