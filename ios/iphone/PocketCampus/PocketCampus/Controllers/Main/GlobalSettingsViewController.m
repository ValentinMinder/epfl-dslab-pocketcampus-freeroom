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

#import "AuthenticationService.h"

#import "MainController.h"

#import "MainMenuViewController.h"


static const int kAccountsSection = 0;
static const int kMainMenuSection = 1;
static const int kAboutSection = 2;

static const int kEditMainMenuRow = 0;
static const int kRestoreDefaultMainMenuRow = 1;

static const int kRatePCRow = 0;
static const int kLikePCFBRow = 1;
static const int kAboutRow = 2;

@interface GlobalSettingsViewController ()

@property (nonatomic, weak) MainController* mainController;

@end

@implementation GlobalSettingsViewController

- (id)initWithMainController:(MainController*)mainController
{
    self = [super initWithNibName:@"GlobalSettingsView" bundle:nil];
    if (self) {
        self.mainController = mainController;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [[GANTracker sharedTracker] trackPageview:@"/v3r1/dashboard/settings" withError:NULL];
    self.title = NSLocalizedStringFromTable(@"Settings", @"PocketCampus", nil);
    self.tableView.backgroundColor = [UIColor clearColor];
    UIView* backgroundView = [[UIView alloc] initWithFrame:self.tableView.frame];
    backgroundView.backgroundColor = [PCValues backgroundColor1];;
    self.tableView.backgroundView = backgroundView;
    UIBarButtonItem* button = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Done", @"PocketCampus", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(doneBarButtonPressed)];
    [self.navigationItem setRightBarButtonItem:button animated:YES];

    
}

- (void)viewWillAppear:(BOOL)animated {
    [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationAutomatic];
    [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:animated];
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
        case kAccountsSection: //accounts
            switch (indexPath.row) {
                case 0: //gaspar account
                {
                    GasparViewController* viewController = [[GasparViewController alloc] init];
                    [self.navigationController pushViewController:viewController animated:YES];
                    break;
                }
                default:
                    break;
            }
            break;
        case kMainMenuSection:
            switch (indexPath.row) {
                case kEditMainMenuRow:
                {
                    [self.mainController.mainMenuViewController setEditing:YES];
                    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
                    break;
                }
                case kRestoreDefaultMainMenuRow:
                {
                    [self.mainController restoreDefaultMainMenu];
                    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
                    break;
                }
                default:
                    break;
            }
            break;
        case kAboutSection: //about
            switch (indexPath.row) {
                case kRatePCRow:
                {
                    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
                    NSURL *url = [NSURL URLWithString:@"https://itunes.apple.com/ch/app/epfl/id549799535"];
                    [[UIApplication sharedApplication] openURL:url];
                    break;
                }
                case kLikePCFBRow:
                {
                    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
                    //
                    NSURL *url = [NSURL URLWithString:@"http://facebook.com/pocketcampus"];
                    [[UIApplication sharedApplication] openURL:url];
                    break;
                }
                case kAboutRow:
                {
                    AboutPCViewController* viewController = [[AboutPCViewController alloc] init];
                    [self.navigationController pushViewController:viewController animated:YES];
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
        case kAccountsSection: //gaspar account
            return NSLocalizedStringFromTable(@"Accounts", @"PocketCampus", nil);
        case kMainMenuSection:
            return NSLocalizedStringFromTable(@"MainMenu", @"PocketCampus", nil);
        case kAboutSection: //about
            return @"PocketCampus";
        default:
            return @"";
            break;
    }
}

- (UITableViewCell*)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    UITableViewCell* cell = nil;
    
    switch (indexPath.section) {
        case kAccountsSection:
        {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:nil];
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            cell.detailTextLabel.textColor = [UIColor colorWithWhite:0.4 alpha:1.0];
            cell.textLabel.text = [GasparViewController localizedTitle];
            NSString* username = [AuthenticationService savedUsername];
            if (username) {
                cell.detailTextLabel.text = username;
            } else {
                cell.detailTextLabel.text = @"";
            }
            break;
        }
        case kMainMenuSection:
        {
            switch (indexPath.row) {
                case kEditMainMenuRow:
                    cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
                    cell.textLabel.text = NSLocalizedStringFromTable(@"EditMainMenu", @"PocketCampus", nil);
                    break;
                case kRestoreDefaultMainMenuRow:
                    cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
                    cell.textLabel.text = NSLocalizedStringFromTable(@"RestoreDefaultMainMenu", @"PocketCampus", nil);
                    break;
                default:
                    break;
            }
            break;
        }
        case kAboutSection:
        {
            switch (indexPath.row) {
                case kRatePCRow:
                    cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
                    cell.textLabel.text = NSLocalizedStringFromTable(@"RatePCAppStore", @"PocketCampus", nil);
                    //cell.detailTextLabel.text = NSLocalizedStringFromTable(@"RatePCAppStoreSubtitle", @"PocketCampus", nil);
                    cell.imageView.image = [UIImage imageNamed:@"AppLogoCellImage"];
                    break;
                case kRestoreDefaultMainMenuRow:
                    cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
                    cell.textLabel.text = NSLocalizedStringFromTable(@"LikePConFB", @"PocketCampus", nil);
                    //cell.detailTextLabel.text = NSLocalizedStringFromTable(@"LikePConFBSubtitle", @"PocketCampus", nil);
                    cell.imageView.image = [UIImage imageNamed:@"FacebookLikeCellImage"];
                    break;
                case kAboutRow:
                    cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
                    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
                    cell.textLabel.text = NSLocalizedStringFromTable(@"About", @"PocketCampus", nil);
                    break;
                default:
                    break;
            }
            break;
        }
        default:
            break;
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case kAccountsSection:
            return 1;
        case kMainMenuSection:
            return 2;
        case kAboutSection:
            return 3;
        default:
            return 0;
            break;
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 3;
}

@end
