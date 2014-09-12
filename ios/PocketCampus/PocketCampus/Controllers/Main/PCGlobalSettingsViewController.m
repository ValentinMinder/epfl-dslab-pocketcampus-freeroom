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

#import "PCUsageViewController.h"


static const int kAccountsSection = 0;
static const int kMainMenuSection = 1;
static const int kAboutSection = 2;

static const int kMiscSection = 3;

static const int kEditMainMenuRow = 0;
static const int kRestoreDefaultMainMenuRow = 1;

static const int kRatePCRow = 0;
static const int kLikePCFBRow = 1;
static const int kAboutRow = 2;

static const int kUsageRow = 0;

@interface PCGlobalSettingsViewController () <UITextFieldDelegate, SKStoreProductViewControllerDelegate, UIActionSheetDelegate>

@property (nonatomic, weak) MainController* mainController;

@property (nonatomic, strong) UIActionSheet* restoreDefaultMainMenuActionSheet;

@end

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
                    //
                    NSURL *url = [NSURL URLWithString:@"http://facebook.com/pocketcampus"];
                    [[UIApplication sharedApplication] openURL:url];
                    break;
                }
                case kAboutRow:
                {
                    [self trackAction:@"OpenAbout"];
                    PCAboutViewController* viewController = [PCAboutViewController new];
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
        default:
            break;
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
                    break;
                case kRestoreDefaultMainMenuRow:
                    cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
                    cell.textLabel.text = NSLocalizedStringFromTable(@"RestoreDefaultMainMenu...", @"PocketCampus", nil);
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
                case kLikePCFBRow:
                    cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
                    cell.textLabel.text = NSLocalizedStringFromTable(@"LikePConFB", @"PocketCampus", nil);
                    cell.imageView.image = [UIImage imageNamed:@"FacebookLikeCellImage"];
                    break;
                case kAboutRow:
                    cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
                    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
                    cell.textLabel.text = NSLocalizedStringFromTable(@"About", @"PocketCampus", nil);
                    cell.imageView.image = [UIImage imageNamed:@"Info"];
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
        case kMiscSection:
            return 1;
        default:
            return 0;
            break;
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 4;
}

@end
