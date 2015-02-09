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

//  Created by Loïc Gardiol on 22.09.12.

@import AddressBook;
@import AddressBookUI;

#import "DirectoryPersonViewController.h"

#import "DirectoryProfilePictureViewController.h"

#import "MapController.h"

#import "DirectoryController.h"

#import "DirectoryPersonBaseInfoCell.h"

#import "PCWebViewController.h"

static const int kPersonBaseInfoSection = 0;
static const int kPhonesSection = 1;
static const int kEmailSection = 2;
static const int kWebpageSection = 3;
static const int kOfficeSection = 4;

static const int kCreateNewContactActionIndex = 0;
static const int kAddToExistingContactActionIndex = 1;

//for all but first cell (DirectoryPersonBaseInfoCell)
static UITableViewCellStyle const kCellStyle = UITableViewCellStyleValue2;
//init at run-time in +initialize
static NSString* kCellTextLabelTextStyle;
static NSString* kCellDetailTextLabelTextStyle;

static CGFloat kRowHeight;

@interface DirectoryPersonViewController ()<DirectoryServiceDelegate, UITableViewDelegate, UITableViewDataSource, UIActionSheetDelegate, ABNewPersonViewControllerDelegate, ABPeoplePickerNavigationControllerDelegate>

@property (nonatomic, strong) NSString* fullNameToSearch;
@property (nonatomic, strong) UIImage* profilePictureImage;
@property (nonatomic, strong) UIPopoverController* profilePicturePopover;
@property (nonatomic, strong) DirectoryService* directoryService;

@property (nonatomic, strong) IBOutlet PCTableViewAdditions* tableView;
@property (nonatomic, strong) DirectoryPersonBaseInfoCell* personBaseInfoCell;
@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, strong) IBOutlet UILabel* centerMessageLabel;

@property (nonatomic, strong) UIActionSheet* actionSheet;

@property (nonatomic, strong) UIPopoverController* imagePopoverController;

@end

@implementation DirectoryPersonViewController

#pragma mark - Inits

+ (void)initialize {
    kCellTextLabelTextStyle = UIFontTextStyleFootnote;
    kCellDetailTextLabelTextStyle = UIFontTextStyleFootnote;
}

- (id)init {
    self = [super initWithNibName:@"DirectoryPersonView" bundle:nil];
    if (self) {
        self.directoryService = [DirectoryService sharedInstanceToRetain];
        self.allowShowOfficeOnMap = YES; //default
        self.gaiScreenName = @"/directory/person";
    }
    return self;
}

- (id)initWithPerson:(Person*)person {
    [PCUtils throwExceptionIfObject:person notKindOfClass:[Person class]];
    self = [self init];
    if (self) {
        self.person = person;
    }
    return self;
}

- (id)initAndLoadPersonWithFullName:(NSString*)fullName {
    [PCUtils throwExceptionIfObject:fullName notKindOfClass:[NSString class]];
    self = [self init];
    if (self) {
        self.fullNameToSearch = fullName;
    }
    return self;
}

#pragma mark - Standard view controller methods

- (void)viewDidLoad {
    [super viewDidLoad];
    self.tableView.contentSizeCategoryDidChangeBlock = ^(PCTableViewAdditions* tableView) {
        kRowHeight = 0.0;
    };
    self.navigationItem.backBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Details", @"DirectoryPlugin", nil) style:UIBarButtonItemStyleBordered target:nil action:nil];
    if (!self.person) {
        if (!self.fullNameToSearch) {
            self.tableView.hidden = YES;
            return;
        }
        DirectoryRequest* req = [[DirectoryRequest alloc] initWithQuery:self.fullNameToSearch language:[PCUtils userLanguageCode] resultSetCookie:nil];
        [self.directoryService searchForRequest:req delegate:self];
        [self.loadingIndicator startAnimating];
        self.tableView.hidden = YES;
        self.centerMessageLabel.hidden = NO;
        self.centerMessageLabel.text = NSLocalizedStringFromTable(@"LoadingPersonInfo", @"DirectoryPlugin", nil);
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
    [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:YES];
}

- (NSUInteger)supportedInterfaceOrientations {
    return [PCUtils isIdiomPad] ? UIInterfaceOrientationMaskAll : UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Person related

- (void)setPerson:(Person *)person {
    [PCUtils throwExceptionIfObject:person notKindOfClass:[Person class]];
    _person = person;
    UIBarButtonItem* actionButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAction target:self action:@selector(actionButtonPressed)];
    actionButton.accessibilityHint = NSLocalizedStringFromTable(@"ShowsOptionsToExportToContacts", @"DirectoryPlugin", nil);
    self.navigationItem.rightBarButtonItem = actionButton;
    self.tableView.hidden = NO;
    [self.tableView reloadData];
}

#pragma mark - Buttons and actions

- (void)actionButtonPressed {
    [self trackAction:PCGAITrackerActionActionButtonPressed];
    if (!self.actionSheet) {
        self.actionSheet = [[UIActionSheet alloc] initWithTitle:Nil delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:nil otherButtonTitles:NSLocalizedStringFromTable(@"CreateNewContact", @"DirectoryPlugin", nil), NSLocalizedStringFromTable(@"AddToExistingContact", @"DirectoryPlugin", nil), nil];
        self.actionSheet.delegate = self;
    }
    [self.actionSheet toggleFromBarButtonItem:self.navigationItem.rightBarButtonItem animated:YES];
}

- (void)openSecretPicture {
    [self trackAction:@"OpenSecretPicture"];
    NSString* urlString = [NSString stringWithFormat:@"http://people.epfl.ch/cgi-bin/people/getPhoto?id=%@&show=1", self.person.emailPrefix];
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:urlString]];
}

- (void)showPhotoViewController {
    if (!self.personBaseInfoCell.profilePicture) {
        return;
    }
    [self trackAction:@"ShowPictureLarge" contentInfo:self.person.fullFirstnameLastname];
    DirectoryProfilePictureViewController* viewController = [[DirectoryProfilePictureViewController alloc] initWithImage:self.personBaseInfoCell.profilePicture];
    if (self.splitViewController) {
        if (!self.imagePopoverController) {
            self.imagePopoverController = [[UIPopoverController alloc] initWithContentViewController:viewController];
            [self.imagePopoverController setPopoverContentSize:viewController.preferredContentSize];
        }
        [self.imagePopoverController togglePopoverFromRect:CGRectMake(55.0, 150.0, 1.0, 1.0) inView:self.view permittedArrowDirections:UIPopoverArrowDirectionUp animated:YES];
    } else {
        viewController.title = self.person.fullFirstnameLastname;
        PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:viewController];
        navController.modalPresentationStyle = UIModalPresentationCurrentContext;
        [self presentViewController:navController animated:YES completion:NULL];
    }
}

- (void)createAndPresentNewContactWithRecordOrNil:(ABRecordRef)person addressBookOrNil:(ABAddressBookRef)addressBook {
    ABNewPersonViewController* abPersonController = [ABNewPersonViewController new];
    abPersonController.newPersonViewDelegate = self;
    ABRecordRef abPerson = nil;
    if (person) {
        abPerson = person;
        abPersonController.title = NSLocalizedStringFromTable(@"UpdatedContact", @"DirectoryPlugin", nil);
    } else {
        abPerson = [self.person newABRecord];
        if (!abPerson) {
            [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ImpossibleToCreateContact", @"DirectoryPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
            return;
        }
    }
    
    if (self.personBaseInfoCell.profilePicture) {
        NSData* imageData = UIImagePNGRepresentation(self.personBaseInfoCell.profilePicture);
        ABPersonSetImageData(abPerson,(__bridge CFDataRef)imageData, nil);
    }
    
    abPersonController.displayedPerson = abPerson;
    if (addressBook) {
        abPersonController.addressBook = addressBook;
    }
    PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:abPersonController];
    navController.modalPresentationStyle = UIModalPresentationFormSheet;
    [self presentViewController:navController animated:YES completion:NULL];
    CFRelease(abPerson);
}

- (void)presentContactsPicker {
    ABPeoplePickerNavigationController* picker = [ABPeoplePickerNavigationController new];
    picker.peoplePickerDelegate = self;
    picker.modalPresentationStyle = UIModalPresentationFormSheet;
    [self presentViewController:picker animated:YES completion:NULL];
}

#pragma mark - ABNewPersonViewControllerDelegate

- (void)newPersonViewController:(ABNewPersonViewController *)newPersonViewController didCompleteWithNewPerson:(ABRecordRef)person {
    [self dismissViewControllerAnimated:YES completion:NULL];
}

#pragma mark - ABPeoplePickerNavigationControllerDelegate

- (BOOL)peoplePickerNavigationController:(ABPeoplePickerNavigationController *)peoplePicker shouldContinueAfterSelectingPerson:(ABRecordRef)person {
    ABAddressBookRef addressBook = peoplePicker.addressBook ?: ABAddressBookCreateWithOptions(NULL, nil);
    if (!addressBook) {
        [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"AddToExistingContactError", @"DirectoryPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
        return NO;
    }
    person = [self.person newMergedWithABRecord:person addressBook:addressBook];
    if (!person) {
        [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"AddToExistingContactError", @"DirectoryPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
        return NO;
    }
    [self dismissViewControllerAnimated:YES completion:^{
        [self createAndPresentNewContactWithRecordOrNil:person addressBookOrNil:addressBook];
    }];
    return NO;
}

- (BOOL)peoplePickerNavigationController:(ABPeoplePickerNavigationController *)peoplePicker shouldContinueAfterSelectingPerson:(ABRecordRef)person property:(ABPropertyID)property identifier:(ABMultiValueIdentifier)identifier {
    return NO;
}

/*
 * On iOS 8, this method is called instead of peoplePickerNavigationController:shouldContinueAfterSelectingPerson: so, redirecting.
 */
- (void)peoplePickerNavigationController:(ABPeoplePickerNavigationController *)peoplePicker didSelectPerson:(ABRecordRef)person {
    [self peoplePickerNavigationController:peoplePicker shouldContinueAfterSelectingPerson:person];
}

- (void)peoplePickerNavigationControllerDidCancel:(ABPeoplePickerNavigationController *)peoplePicker {
    [self dismissViewControllerAnimated:YES completion:NULL];
}

#pragma mark - UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet willDismissWithButtonIndex:(NSInteger)buttonIndex {
#warning ugly, see if updates of iOS 8 solve this problem
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        if (buttonIndex == actionSheet.cancelButtonIndex) {
            self.actionSheet = nil;
            return;
        } else if (buttonIndex == kCreateNewContactActionIndex) {
            [self trackAction:@"CreateNewContact" contentInfo:self.person.fullFirstnameLastname];
            [self createAndPresentNewContactWithRecordOrNil:nil addressBookOrNil:nil];
        } else if (buttonIndex == kAddToExistingContactActionIndex) {
            [self trackAction:@"AddToExistingContact"];
            [self presentContactsPicker];
        } else {
            //nothing
        }
        self.actionSheet = nil;
    });
}

#pragma mark - DirectoryServiceDelegate

- (void)searchForRequest:(DirectoryRequest *)request didReturn:(DirectoryResponse *)response {
    [self.loadingIndicator stopAnimating];
    NSArray* results = response.results;
    if (results.count == 0) {
        self.centerMessageLabel.hidden = NO;
        self.centerMessageLabel.text = NSLocalizedStringFromTable(@"NoResultPCUnknownViewControllerLoad", @"DirectoryPlugin", nil);
        return;
    }
    self.centerMessageLabel.hidden = YES;
    Person* person = results[0];
    self.person = person;
}

- (void)searchFailedForRequest:(DirectoryRequest *)request {
    [self.loadingIndicator stopAnimating];
    self.centerMessageLabel.hidden = NO;
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", nil);
}

- (void)serviceConnectionToServerFailed {
    //search for person timed out
    [self.loadingIndicator stopAnimating];
    self.centerMessageLabel.hidden = NO;
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil);
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (kRowHeight == 0.0) {
        kRowHeight = [PCTableViewCellAdditions preferredHeightForStyle:kCellStyle textLabelTextStyle:kCellTextLabelTextStyle detailTextLabelTextStyle:kCellDetailTextLabelTextStyle];
    }
    switch (indexPath.section) {
        case kPersonBaseInfoSection:
            return [DirectoryPersonBaseInfoCell preferredHeightForStyle:DirectoryPersonBaseInfoCellStyleLarge person:self.person inTableView:self.tableView];
    }
    return kRowHeight;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (section == kPersonBaseInfoSection) {
        return 4.0;
    }
    if (section == kPhonesSection) {
        return 1.0;
    }
    if (section == kEmailSection && (!self.person.privatePhoneNumber && !self.person.officePhoneNumber)) {
        return 1.0;
    }
    return 15.0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    if (section == kPersonBaseInfoSection) {
        return 5.0;
    }
    return 1.0;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case kPersonBaseInfoSection:
            [self.tableView deselectRowAtIndexPath:indexPath animated:NO];
            break;
        case kPhonesSection:
        {
            NSString* phone = nil;
            if (indexPath.row == [self privatePhoneNumberRowIndex]) {
                phone = self.person.privatePhoneNumber;
            } else if (indexPath.row == [self officePhoneNumberRowIndex]) {
                phone = self.person.officePhoneNumber;
            } else {
                //should not happen
                return;
            }
            [self trackAction:@"Call" contentInfo:phone];
            phone = [phone stringByReplacingOccurrencesOfString:@" " withString:@""];
            [[UIApplication sharedApplication] openURL:[NSURL URLWithString:[NSString stringWithFormat:@"tel://%@", phone]]];
            [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
            break;
        }
        case kEmailSection:
            [self trackAction:@"SendEmail" contentInfo:self.person.email];
            [[UIApplication sharedApplication] openURL:[NSURL URLWithString:[NSString stringWithFormat:@"mailto://%@", self.person.email]]];
            [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
            break;
        case kWebpageSection:
        {
            [self trackAction:@"ViewWebsite" contentInfo:self.person.web];
            PCWebViewController* webViewController = [[PCWebViewController alloc] initWithURL:[NSURL URLWithString:self.person.web] title:nil];
            [self.navigationController pushViewController:webViewController animated:YES];
            break;
        }
        case kOfficeSection:
        {
            if (self.allowShowOfficeOnMap) {
                [self trackAction:@"ViewOffice" contentInfo:self.person.office];
                UIViewController* viewController = [MapController viewControllerWithInitialSearchQuery:self.person.office pinLabelText:self.person.fullFirstnameLastname];
                [self.navigationController pushViewController:viewController animated:YES];
            } else {
                [self.tableView deselectRowAtIndexPath:indexPath animated:NO];
            }
            break;
        }
        default:
            break;
    }
}

- (BOOL)tableView:(UITableView *)tableView shouldShowMenuForRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES;
}

- (BOOL)tableView:(UITableView *)tableView canPerformAction:(SEL)action forRowAtIndexPath:(NSIndexPath *)indexPath withSender:(id)sender {
    if (indexPath.section == kPersonBaseInfoSection) {
        return NO;
    }
    if ([NSStringFromSelector(action) isEqualToString:@"copy:"]) {
        return YES;
    }
    return NO;
}

- (void)tableView:(UITableView *)tableView performAction:(SEL)action forRowAtIndexPath:(NSIndexPath *)indexPath withSender:(id)sender {
    UIPasteboard* pasteboard = [UIPasteboard generalPasteboard];
    switch (indexPath.section) {
        case kPersonBaseInfoSection:
            pasteboard.string = self.person.fullFirstnameLastname;
            break;
        case kPhonesSection:
            if (indexPath.row == [self privatePhoneNumberRowIndex]) {
                pasteboard.string = self.person.privatePhoneNumber;
            } else if (indexPath.row == [self officePhoneNumberRowIndex]) {
                pasteboard.string = self.person.officePhoneNumber;
            } else {
                //should not happen
                return;
            }
            break;
        case kEmailSection:
            pasteboard.string = self.person.email;
            break;
        case kWebpageSection:
            pasteboard.string = self.person.web;
            break;
        case kOfficeSection:
            pasteboard.string = self.person.office;
            break;
    }
    [self trackAction:PCGAITrackerActionCopy contentInfo:pasteboard.string];
    CLSNSLog(@"-> Copy '%@' to pasteboard.", pasteboard.string);
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (indexPath.section == kPersonBaseInfoSection) {
        if (!self.personBaseInfoCell) {
            self.personBaseInfoCell = [[DirectoryPersonBaseInfoCell alloc] initWithDirectoryPersonBaseInfoCellStyle:DirectoryPersonBaseInfoCellStyleLarge reuseIdentifer:nil];
            UITapGestureRecognizer* tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(showPhotoViewController)];
            self.personBaseInfoCell.profilePictureImageView.userInteractionEnabled = YES;
            [self.personBaseInfoCell.profilePictureImageView addGestureRecognizer:tapGesture];
            
            UITapGestureRecognizer* secretTapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(openSecretPicture)];
            secretTapGesture.numberOfTapsRequired = 3;
            secretTapGesture.numberOfTouchesRequired = 2;
            [self.personBaseInfoCell.contentView addGestureRecognizer:secretTapGesture];
            __weak __typeof(self) welf = self;
            [self.personBaseInfoCell setUnitTappedBlock:^(NSURL* unitURL) {
                [welf trackAction:@"ViewUnit"];
                PCWebViewController* webViewController = [[PCWebViewController alloc] initWithURL:unitURL title:nil];
                [welf.navigationController pushViewController:webViewController animated:YES];
            }];
        }
        self.personBaseInfoCell.person = self.person;
        return self.personBaseInfoCell;
    }
    
    UITableViewCell* cell = [[UITableViewCell alloc] initWithStyle:kCellStyle reuseIdentifier:nil];
    cell.textLabel.textColor = [PCValues pocketCampusRed];
    cell.textLabel.font = [UIFont preferredFontForTextStyle:kCellTextLabelTextStyle];
    cell.detailTextLabel.font = [UIFont preferredFontForTextStyle:kCellDetailTextLabelTextStyle];
    cell.detailTextLabel.adjustsFontSizeToFitWidth = YES;
    
    switch (indexPath.section) {
        case kPhonesSection:
            if (indexPath.row == [self privatePhoneNumberRowIndex]) {
                cell.textLabel.text = NSLocalizedStringFromTable(@"PrivatePhone", @"DirectoryPlugin", nil);
                cell.detailTextLabel.text = self.person.privatePhoneNumber;
            } else if (indexPath.row == [self officePhoneNumberRowIndex]) {
                cell.textLabel.text = NSLocalizedStringFromTable(@"OfficePhone", @"DirectoryPlugin", nil);
                cell.detailTextLabel.text = self.person.officePhoneNumber;
            }
            cell.accessibilityHint = NSLocalizedStringFromTable(@"CallsThisNumber", @"DirectoryPlugin", nil);
            break;
        case kEmailSection:
            cell.textLabel.text = NSLocalizedStringFromTable(@"Email", @"DirectoryPlugin", nil);
            cell.detailTextLabel.text = self.person.email;
            cell.accessibilityHint = NSLocalizedStringFromTable(@"SendsAnEmail", @"DirectoryPlugin", nil);
            break;
        case kWebpageSection:
            cell.textLabel.text = NSLocalizedStringFromTable(@"Webpage", @"DirectoryPlugin", nil);
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            cell.detailTextLabel.text = self.person.web;
            cell.detailTextLabel.lineBreakMode = NSLineBreakByTruncatingMiddle;
            cell.detailTextLabel.adjustsFontSizeToFitWidth = NO;
            cell.accessibilityHint = NSLocalizedStringFromTable(@"OpensInBrowser", @"DirectoryPlugin", nil);
            break;
        case kOfficeSection:
        {
            cell.accessoryType = self.allowShowOfficeOnMap ? UITableViewCellAccessoryDisclosureIndicator : UITableViewCellAccessoryNone;
            cell.textLabel.text = NSLocalizedStringFromTable(@"Office", @"DirectoryPlugin", nil);
            NSString* tapToSeeOnMap = self.allowShowOfficeOnMap ? NSLocalizedStringFromTable(@"TapToSeeOnMap", @"DirectoryPlugin", nil) : @"";
            NSString* finalString = [NSString stringWithFormat:@"%@  %@", self.person.office, tapToSeeOnMap];
            NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:finalString];
            [attrString addAttribute:NSForegroundColorAttributeName value:[UIColor grayColor] range:[finalString rangeOfString:tapToSeeOnMap]];
            cell.detailTextLabel.attributedText = attrString;
            cell.accessibilityHint = self.allowShowOfficeOnMap ? NSLocalizedStringFromTable(@"ShowsOnMap", @"DirectoryPlugin", nil) : nil;
            break;
        }
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case kPersonBaseInfoSection:
            return 1;
        case kPhonesSection:
            if (self.person.privatePhoneNumber && self.person.officePhoneNumber) {
                return 2;
            } else if (self.person.privatePhoneNumber || self.person.officePhoneNumber) {
                return 1;
            }
            return 0;
        case kEmailSection:
            return self.person.email ? 1 : 0;
        case kWebpageSection:
            return self.person.web ? 1 : 0;
        case kOfficeSection:
            return self.person.office ? 1 : 0;
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (!self.person) {
        return 0;
    }
    return 5; //see static ints defined at top of class
}

#pragma mark - Utils

- (NSInteger)privatePhoneNumberRowIndex {
    return self.person.privatePhoneNumber ? 0 : -1; //-1 means row does not exist
}

- (NSInteger)officePhoneNumberRowIndex {
    if (self.person.privatePhoneNumber && self.person.officePhoneNumber) {
        return 1;
    } else if (self.person.officePhoneNumber) {
        return 0;
    }
    return -1;
}


#pragma mark - Dealloc

- (void)dealloc {
    [self.directoryService cancelOperationsForDelegate:self];
}

@end

