//
//  GlobalSettingsViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 13.07.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GlobalSettingsViewController.h"

#import "PCValues.h"

@implementation GlobalSettingsViewController

@synthesize tableView;

- (id)init
{
    self = [super initWithNibName:@"GlobalSettingsView" bundle:nil];
    if (self) {
        // Custom initialization
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
    UIBarButtonItem* doneButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneBarButtonPressed)];
    self.navigationItem.rightBarButtonItem = doneButton;
    [doneButton release];
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

- (void)doneBarButtonPressed {
    if ([self.presentingViewController respondsToSelector:@selector(dismissViewControllerAnimated:completion:)]) { // >= iOS 5.0
        [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
    } else {
        [self.presentingViewController dismissModalViewControllerAnimated:YES];
    }  
}

/* UITableViewDelegate delegation */

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    switch (section) {
        case 0: //gaspar account
        {   
            BOOL isLoggedIn = YES; //TODO
            
            if (isLoggedIn) {
                NSString* text = NSLocalizedStringFromTable(@"LoggedInExplanationLong", @"PocketCampus", nil);
                UIFont* font = [UIFont systemFontOfSize:16.0];
                CGSize reqSize = [text sizeWithFont:font constrainedToSize:CGSizeMake(260.0, 600.0)];
                return reqSize.height;
            }
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
            BOOL isLoggedIn = YES; //TODO
            if (isLoggedIn) {
                UILabel* label = [[UILabel alloc] init];
                NSString* text = NSLocalizedStringFromTable(@"LoggedInExplanationLong", @"PocketCampus", nil);
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
            }
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
            BOOL isLoggedIn = YES; //TODO
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
            UITableViewCell* cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
            cell.textLabel.textAlignment = UITextAlignmentCenter;
            cell.selectionStyle = UITableViewCellSelectionStyleGray;
            BOOL isLoggedIn = YES; //TODO
            
            if (isLoggedIn) {
                cell.textLabel.text = NSLocalizedStringFromTable(@"Logout", @"PocketCampus", nil);
            } else {
                //TODO
            }
            return cell;
            break;   
        }
        case 1: //about
        {
            UITableViewCell* cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
            cell.textLabel.text = NSLocalizedStringFromTable(@"About", @"PocketCampus", nil);
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            cell.selectionStyle = UITableViewCellSelectionStyleGray;
            return cell;
        }
        default:
            return nil;
            break;
    }
    
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case 0: //gaspar account
            return 1; //TODO : different if logged out
            break;
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
