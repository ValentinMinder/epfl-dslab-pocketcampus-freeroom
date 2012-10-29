//
//  GlobalSettingsViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 13.07.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "GlobalSettingsViewController.h"

#import "PCValues.h"

#import "PCUtils.h"

#import "EditableTableViewCell.h"

#import "GasparViewController.h"

#import "AboutPCViewController.h"

static NSString* kStandardSettingDefaultCell = @"StandardSettingDefaultCell";

@implementation GlobalSettingsViewController

@synthesize tableView;

- (id)init
{
    self = [super initWithNibName:@"GlobalSettingsView" bundle:nil];
    if (self) {
        // Custom initialization
        textEditing = NO;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/dashboard/settings" withError:NULL];
    self.title = NSLocalizedStringFromTable(@"Settings", @"PocketCampus", nil);
    tableView.backgroundColor = [UIColor clearColor];
    UIView* backgroundView = [[UIView alloc] initWithFrame:tableView.frame];
    backgroundView.backgroundColor = [PCValues backgroundColor1];;
    tableView.backgroundView = backgroundView;
    [backgroundView release];
    UIBarButtonItem* button = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Done", @"PocketCampus", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(doneBarButtonPressed)];
    [self.navigationItem setRightBarButtonItem:button animated:YES];
    [button release];
    cancelButtonDisplayed = NO;
    
}

- (void)viewWillAppear:(BOOL)animated {
    [tableView deselectRowAtIndexPath:[tableView indexPathForSelectedRow] animated:animated];
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

- (void)doneBarButtonPressed {
    if ([self.presentingViewController respondsToSelector:@selector(dismissViewControllerAnimated:completion:)]) { // >= iOS 5.0
        [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
    } else {
        [self.presentingViewController dismissModalViewControllerAnimated:YES];
    }  
}

/* UITableViewDelegate delegation */

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case 0: //accounts
            switch (indexPath.row) {
                case 0: //gaspar account
                {
                    GasparViewController* viewController = [[GasparViewController alloc] init];
                    [self.navigationController pushViewController:viewController animated:YES];
                    [viewController release];
                    break;
                }
                default:
                    break;
            }
            break;
        case 1: //about
            switch (indexPath.row) {
                case 0: //About PC
                {
                    AboutPCViewController* viewController = [[AboutPCViewController alloc] init];
                    [self.navigationController pushViewController:viewController animated:YES];
                    [viewController release];
                    break;
                }
                default:
                    break;
            }
        default:
            break;
    }
}

/* UITableViewDataSource delegation */

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    switch (section) {
        case 0: //gaspar account
            return NSLocalizedStringFromTable(@"Accounts", @"PocketCampus", nil);
        case 1: //about
            return @"PocketCampus";
            break;
        default:
            return @"";
            break;
    }
}

- (UITableViewCell*)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell;
    
    switch (indexPath.section) {
        case 0: //gaspar account
        {
            cell = [tableView dequeueReusableCellWithIdentifier:kStandardSettingDefaultCell];
            if (!cell) {
                cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kStandardSettingDefaultCell] autorelease];
                cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            }
            cell.textLabel.text = [GasparViewController localizedTitle];
            return cell;
            
        }
        case 1: //about
        {
            cell = [tableView dequeueReusableCellWithIdentifier:kStandardSettingDefaultCell];
            if (!cell) {
                cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kStandardSettingDefaultCell] autorelease];
                cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            }
            cell.textLabel.text = NSLocalizedStringFromTable(@"About", @"PocketCampus", nil);
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
            return 1;
        case 1: //about
            return 1;
        default:
            return 0;
            break;
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

@end
