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




//  Created by Loïc Gardiol on 13.07.12.


@import StoreKit;

#import "PCGlobalSettingsViewController.h"

#import "PCEditableTableViewCell.h"

#import "AuthenticationController.h"

#import "PCAboutViewController.h"

#import "AuthenticationService.h"

#import "MainController.h"

#import "MainMenuViewController.h"

#import "PCEPFLMailProfileViewController.h"

#import "PCEPFLVPNProfileViewController.h"

#import "PCWhatsNewViewController.h"

#import "PCUsageViewController.h"

#import "PCDebugConfigSelectionViewController.h"


static const int kAccountsSection = 0;
static const int kMainMenuSection = 1;
static const int kAutoConfigsSection = 2;
static const int kAboutSection = 3;
static const int kMiscSection = 4;
static const int kDebugSection = 5;

static const int kMailConfigRow = 0;
static const int kVPNConfigRow = 1;

static const int kEditMainMenuRow = 0;
static const int kRestoreDefaultMainMenuRow = 1;

static const int kRatePCRow = 0;
static const int kLikePCFBRow = 1;
static const int kAboutRow = 2;
static const int kWhatsNewRow = 3;

static const int kUsageRow = 0;

@interface PCGlobalSettingsViewController () <UITextFieldDelegate, SKStoreProductViewControllerDelegate, UIActionSheetDelegate>

@property (nonatomic, weak) MainController* mainController;

@property (nonatomic, strong) UIActionSheet* restoreDefaultMainMenuActionSheet;

@end

#pragma mark - Init

@implementation PCGlobalSettingsViewController

- (id)initWithMainController:(MainController*)mainController
{
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.gaiScreenName = @"/dashboard/settings";
        self.mainController = mainController;
        self.title = NSLocalizedStringFromTable(@"Settings", @"PocketCampus", nil);
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    UIBarButtonItem* doneButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Done", @"PocketCampus", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(doneBarButtonPressed)];
    doneButton.accessibilityHint = NSLocalizedStringFromTable(@"ClosesSettings", @"PocketCampus", nil);
    [self.navigationItem setRightBarButtonItem:doneButton animated:YES];

    
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
    [self.tableView reloadData];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    if ([PCUtils isIdiomPad]) {
        return UIInterfaceOrientationMaskAll;
    } else {
        return UIInterfaceOrientationMaskPortrait;
    }
    
}

#pragma mark - Actions

- (void)doneBarButtonPressed {
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case kAccountsSection: //accounts
            switch (indexPath.row) {
                case 0: //gaspar account
                {
                    [self trackAction:@"OpenAuthentication"];
                    AuthenticationViewController* viewController = [[AuthenticationController sharedInstance] statusViewController];
                    [viewController setShowDoneButton:YES forState:AuthenticationViewControllerStateLoggedIn];
                    __weak __typeof(self) welf = self;
                    [viewController setUserTappedDoneBlock:^{
                        [welf.navigationController.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
                    }];
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
                    [self trackAction:@"EditDashboard"];
                    [self.mainController.mainMenuViewController setEditing:YES];
                    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
                    break;
                }
                case kRestoreDefaultMainMenuRow:
                {
                    self.restoreDefaultMainMenuActionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedStringFromTable(@"RestoreDefaultMainMenuExplanation", @"PocketCampus", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:nil otherButtonTitles:NSLocalizedStringFromTable(@"RestoreDefaultMainMenu", @"PocketCampus", nil), nil];
                    [self.restoreDefaultMainMenuActionSheet showInView:self.tableView];
                    break;
                }
                default:
                    break;
            }
            break;
        case kAutoConfigsSection:
        {
            switch (indexPath.row) {
                case kMailConfigRow:
                {
                    [self trackAction:@"OpenEmailConfig"];
                    PCEPFLMailProfileViewController* viewController = [PCEPFLMailProfileViewController new];
                    [self.navigationController pushViewController:viewController animated:YES];
                    break;
                }
                case kVPNConfigRow:
                {
                    [self trackAction:@"OpenVPNConfig"];
                    PCEPFLVPNProfileViewController* viewController = [PCEPFLVPNProfileViewController new];
                    [self.navigationController pushViewController:viewController animated:YES];
                    break;
                }
                default:
                    break;
            }
            break;
        }
        case kAboutSection: //about
            switch (indexPath.row) {
                case kRatePCRow:
                {
                    [self trackAction:@"RateOnStore"];
                    //iOS 7 bug makes that "Write review" button is not available when using SKStoreProductViewController
                    //Thus simply opening app in App Store app instead
                    /*SKStoreProductViewController* productViewController = [SKStoreProductViewController new];
                    productViewController.delegate = self;
                    [productViewController loadProductWithParameters:@{SKStoreProductParameterITunesItemIdentifier:@549799535} completionBlock:NULL];
                    [self presentViewController:productViewController animated:YES completion:NULL];*/
                    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
                    NSURL *url = [NSURL URLWithString:@"https://itunes.apple.com/ch/app/epfl/id549799535"];
                    [[UIApplication sharedApplication] openURL:url];
                    break;
                }
                case kLikePCFBRow:
                {
                    [self trackAction:@"LikeOnFacebook"];
                    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
                    NSURL *facebookURL = [NSURL URLWithString:@"fb://profile/188616577853493"];
                    if ([[UIApplication sharedApplication] canOpenURL:facebookURL]) {
                        [[UIApplication sharedApplication] openURL:facebookURL];
                    } else {
                        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:@"https://facebook.com/pocketcampus"]];
                    }
                    
                    break;
                }
                case kAboutRow:
                {
                    [self trackAction:@"OpenAbout"];
                    PCAboutViewController* viewController = [PCAboutViewController new];
                    [self.navigationController pushViewController:viewController animated:YES];
                    break;
                }
                case kWhatsNewRow:
                {
                    [self trackAction:@"OpenWhatsNew"];
                    PCWhatsNewViewController* viewController = [PCWhatsNewViewController new];
                    [self.navigationController pushViewController:viewController animated:YES];
                    break;
                }
                default:
                    break;
            }
            break;
        case kMiscSection:
            switch (indexPath.row) {
                case kUsageRow:
                {
                    [self trackAction:@"OpenUsage"];
                    PCUsageViewController* viewController = [PCUsageViewController new];
                    [self.navigationController pushViewController:viewController animated:YES];
                    break;
                }
                default:
                    break;
            }
            break;
        case kDebugSection:
        {
            PCDebugConfigSelectionViewController* viewController = [PCDebugConfigSelectionViewController new];
            [self.navigationController pushViewController:viewController animated:YES];
            break;
        }
        default:
            break;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == kAutoConfigsSection) {
        return 65.0;
    }
    return 44.0;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == kMainMenuSection) {
        UIEdgeInsets insets = cell.separatorInset;
        insets.right = insets.left;
        cell.separatorInset = insets;
        //cell.textLabel.backgroundColor = [UIColor grayColor];
        // Prevent the cell from inheriting the Table View's margin settings
        if ([cell respondsToSelector:@selector(setPreservesSuperviewLayoutMargins:)]) {
            [cell setPreservesSuperviewLayoutMargins:NO];
        }
        // Explictly set your cell's layout margins
        if ([cell respondsToSelector:@selector(setLayoutMargins:)]) {
            cell.layoutMargins = insets;
        }
    }
}

#pragma mark - UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet didDismissWithButtonIndex:(NSInteger)buttonIndex {
#warning ugly, see if updates of iOS 8 solve this problem
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        if (buttonIndex == actionSheet.cancelButtonIndex) {
            [self.tableView deselectRowAtIndexPath:self.tableView.indexPathForSelectedRow animated:YES];
            return;
        }
        [self trackAction:@"RestoreDefaultDashboard"];
        [self.mainController restoreDefaultMainMenu];
        [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
        self.restoreDefaultMainMenuActionSheet = nil;
    });
}

#pragma mark - SKStoreProductViewControllerDelegate

- (void)productViewControllerDidFinish:(SKStoreProductViewController *)viewController {
    [self dismissViewControllerAnimated:YES completion:NULL];
}

#pragma mark - UITableViewDataSource

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    switch (section) {
        case kAccountsSection:
            return NSLocalizedStringFromTable(@"Accounts", @"PocketCampus", nil);
        case kAutoConfigsSection:
            return NSLocalizedStringFromTable(@"AutomaticConfigurations", @"PocketCampus", nil);
        case kMainMenuSection:
            return NSLocalizedStringFromTable(@"MainMenu", @"PocketCampus", nil);
        case kAboutSection:
            return @"PocketCampus";
        case kMiscSection:
            return NSLocalizedStringFromTable(@"Misc", @"PocketCampus", nil);
    }
    return nil;
}

- (UITableViewCell*)tableView:(UITableView *)tableView_ cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    UITableViewCell* cell = nil;
    
    switch (indexPath.section) {
        case kAccountsSection:
        {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:nil];
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            cell.textLabel.text = [AuthenticationViewController localizedTitle];
            cell.detailTextLabel.text = [[AuthenticationController sharedInstance] loggedInUsername];
            break;
        }
        case kMainMenuSection:
        {
            switch (indexPath.row) {
                case kEditMainMenuRow:
                    cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
                    cell.textLabel.text = NSLocalizedStringFromTable(@"EditMainMenu", @"PocketCampus", nil);
                    cell.textLabel.textColor = self.view.tintColor;
                    cell.textLabel.textAlignment = NSTextAlignmentCenter;
                    break;
                case kRestoreDefaultMainMenuRow:
                    cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
                    cell.textLabel.text = NSLocalizedStringFromTable(@"RestoreDefaultMainMenu...", @"PocketCampus", nil);
                    cell.textLabel.textColor = self.view.tintColor;
                    cell.textLabel.textAlignment = NSTextAlignmentCenter;
                    break;
                default:
                    break;
            }
            break;
        }
        case kAutoConfigsSection:
        {
            switch (indexPath.row) {
                case kMailConfigRow:
                    cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
                    cell.imageView.image = [UIImage imageNamed:@"iOSMail_40"];
                    cell.textLabel.text = NSLocalizedStringFromTable(@"EPFLMail", @"PocketCampus", nil);
                    cell.detailTextLabel.text = NSLocalizedStringFromTable(@"ConfigureEPFLEmailInMailApp", @"PocketCampus", nil);
                    cell.detailTextLabel.textColor = [UIColor grayColor];
                    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
                    cell.separatorInset = UIEdgeInsetsZero;
                    break;
                case kVPNConfigRow:
                    cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
                    cell.imageView.image = [UIImage imageNamed:@"iOSVPN_40"];
                    cell.textLabel.text = NSLocalizedStringFromTable(@"EPFLVPN", @"PocketCampus", nil);
                    cell.detailTextLabel.text = NSLocalizedStringFromTable(@"ConfigureEPFLVPN", @"PocketCampus", nil);
                    cell.detailTextLabel.textColor = [UIColor grayColor];
                    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
                    cell.separatorInset = UIEdgeInsetsZero;
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
                    cell.detailTextLabel.text = NSLocalizedStringFromTable(@"RatePCAppStoreSubtitle", @"PocketCampus", nil);
                    cell.detailTextLabel.textColor = [UIColor grayColor];
                    cell.imageView.image = [[UIImage imageNamed:@"AppStoreBarButtonSelected"] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
                    cell.imageView.tintColor = [UIColor colorWithRed:0.407843 green:0.615686 blue:0.960784 alpha:1.0];
                    break;
                case kLikePCFBRow:
                    cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
                    cell.textLabel.text = NSLocalizedStringFromTable(@"LikePConFB", @"PocketCampus", nil);
                    cell.detailTextLabel.text = NSLocalizedStringFromTable(@"LikePConFBSubtitle", @"PocketCampus", nil);
                    cell.detailTextLabel.textColor = [UIColor grayColor];
                    cell.imageView.image = [[UIImage imageNamed:@"FacebookBarButtonSelected"] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
                    cell.imageView.tintColor = [UIColor colorWithRed:0.278431 green:0.345098 blue:0.592157 alpha:1.0];
                    break;
                case kAboutRow:
                    cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
                    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
                    cell.textLabel.text = NSLocalizedStringFromTable(@"About", @"PocketCampus", nil);
                    cell.detailTextLabel.text = NSLocalizedStringFromTable(@"AboutPocketCampusSubtitle", @"PocketCampus", nil);
                    cell.detailTextLabel.textColor = [UIColor grayColor];
                    cell.imageView.image = [[UIImage imageNamed:@"InfoBarButtonSelected"] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
                    cell.imageView.tintColor = [UIColor colorWithWhite:0.5 alpha:1.0];
                    break;
                case kWhatsNewRow:
                    cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
                    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
                    cell.textLabel.text = NSLocalizedStringFromTable(@"WhatsNewInUpdate", @"PocketCampus", nil);
                    cell.textLabel.adjustsFontSizeToFitWidth = YES;
                    cell.imageView.image = [UIImage imageNamed:@"MagicWandBarButton"];
                    break;
                default:
                    break;
            }
            break;
        }
        case kMiscSection:
        {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.textLabel.text = NSLocalizedStringFromTable(@"Usage", @"PocketCampus", nil);
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            break;
        }
        case kDebugSection:
        {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.textLabel.text = @"Debug";
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
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
        case kAutoConfigsSection:
            return 2;
        case kMainMenuSection:
            return 2;
        case kAboutSection:
            return 4;
        case kMiscSection:
            return 1;
        case kDebugSection:
            return 1;
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
#ifdef DEBUG
    return 6;
#else
    return 5;
#endif
}

@end
