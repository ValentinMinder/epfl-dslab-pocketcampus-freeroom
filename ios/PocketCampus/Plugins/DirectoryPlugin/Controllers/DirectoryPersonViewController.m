//
//  PCUnkownPersonViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 22.09.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

@import AddressBook;
@import AddressBookUI;

#import "DirectoryPersonViewController.h"

#import "DirectoryProfilePictureViewController.h"

#import "MapController.h"

#import "DirectoryPersonBaseInfoCell.h"

static const int kPersonBaseInfoSection = 0;
static const int kPhonesSection = 1;
static const int kEmailSection = 2;
static const int kWebpageSection = 3;
static const int kOfficeSection = 4;

static const int kPrivatePhoneRow = 0;
static const int kOfficePhoneRow = 1;

static const int kCreateNewContactActionIndex = 0;
static const int kAddToExistingContactActionIndex = 1;

//for all but first cell (DirectoryPersonBaseInfoCell)
static UITableViewCellStyle const kCellStyle = UITableViewCellStyleValue2;
//init at run-time in +initialize
static NSString* kCellTextLabelTextStyle;
static NSString* kCellDetailTextLabelTextStyle;
//init at each instantiation in init to reflect possible user change of preferred content size
static CGFloat kCellHeight;

@interface DirectoryPersonViewController ()<DirectoryServiceDelegate, UITableViewDelegate, UITableViewDataSource, UIActionSheetDelegate, ABNewPersonViewControllerDelegate, ABPeoplePickerNavigationControllerDelegate>

@property (nonatomic, strong) NSString* fullNameToSearch;
@property (nonatomic, strong) UIImage* profilePictureImage;
@property (nonatomic, strong) UIPopoverController* profilePicturePopover;
@property (nonatomic, strong) DirectoryService* directoryService;

@property (nonatomic, strong) IBOutlet UITableView* tableView;
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
        kCellHeight = [PCTableViewCellAdditions preferredHeightForStyle:kCellStyle textLabelTextStyle:kCellTextLabelTextStyle detailTextLabelTextStyle:kCellDetailTextLabelTextStyle];
        self.directoryService = [DirectoryService sharedInstanceToRetain];
        self.allowShowOfficeOnMap = YES; //default
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
    self.navigationItem.backBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Details", @"DirectoryPlugin", nil) style:UIBarButtonItemStyleBordered target:nil action:nil];
    if (!self.person) {
        if (!self.fullNameToSearch) {
            self.tableView.hidden = YES;
            return;
        }
        DirectoryRequest* req = [[DirectoryRequest alloc] initWithQuery:self.fullNameToSearch directorySession:nil resultSetCookie:nil];
        [self.directoryService searchForRequest:req delegate:self];
        [self.loadingIndicator startAnimating];
        self.tableView.hidden = YES;
        self.centerMessageLabel.hidden = NO;
        self.centerMessageLabel.text = NSLocalizedStringFromTable(@"LoadingPersonInfo", @"DirectoryPlugin", nil);
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[PCGAITracker sharedTracker] trackScreenWithName:@"/directory/personDetails"];
    [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:YES];
    //NSLog(@"Person picture url: %@", self.person.pictureUrl);
}

- (NSUInteger)supportedInterfaceOrientations {
    return [PCUtils isIdiomPad] ? UIInterfaceOrientationMaskAll : UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Person related

- (void)setPerson:(Person *)person {
    [PCUtils throwExceptionIfObject:person notKindOfClass:[Person class]];
    _person = person;
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAction target:self action:@selector(actionButtonPressed)];
    self.tableView.hidden = NO;
    [self.tableView reloadData];
}

#pragma mark - Buttons and actions

- (void)actionButtonPressed {
    if (!self.actionSheet) {
        self.actionSheet = [[UIActionSheet alloc] initWithTitle:Nil delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:nil otherButtonTitles:NSLocalizedStringFromTable(@"CreateNewContact", @"DirectoryPlugin", nil), NSLocalizedStringFromTable(@"AddToExistingContact", @"DirectoryPlugin", nil), nil];
        self.actionSheet.delegate = self;
    }
    [self.actionSheet toggleFromBarButtonItem:self.navigationItem.rightBarButtonItem animated:YES];
}

- (void)openSecretPicture {
    NSString* urlString = [NSString stringWithFormat:@"http://people.epfl.ch/cgi-bin/people/getPhoto?id=%@&show=1", self.person.emailPrefix];
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:urlString]];
}

- (void)showPhotoViewController {
    if (!self.personBaseInfoCell.profilePicture) {
        return;
    }
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
        [self presentViewController:navController animated:YES completion:NO];
    }
}

- (void)createAndPresentNewContact {
    ABRecordRef abPerson = [self.person createABRecord];
    if (!abPerson) {
        [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ImpossibleToCreateContact", @"DirectoryPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
        return;
    }
    
    if (self.personBaseInfoCell.profilePicture) {
        NSData* imageData = UIImagePNGRepresentation(self.personBaseInfoCell.profilePicture);
        ABPersonSetImageData(abPerson,(__bridge CFDataRef)imageData, nil);
    }
    ABNewPersonViewController* abPersonController = [ABNewPersonViewController new];
    abPersonController.newPersonViewDelegate = self;
    abPersonController.displayedPerson = abPerson;
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
    [self.person addInfoToABRecord:person];
    ABAddressBookRef ab = peoplePicker.addressBook;
    CFErrorRef* error = NULL;
    ABAddressBookSave(ab, error);
    if (error) {
        [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ImpossibleToUpdateExistingContact", @"DirectoryPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
    } else {
        [self dismissViewControllerAnimated:YES completion:NULL];
    }
    return NO;
}

- (BOOL)peoplePickerNavigationController:(ABPeoplePickerNavigationController *)peoplePicker shouldContinueAfterSelectingPerson:(ABRecordRef)person property:(ABPropertyID)property identifier:(ABMultiValueIdentifier)identifier {
    return NO;
}

- (void)peoplePickerNavigationControllerDidCancel:(ABPeoplePickerNavigationController *)peoplePicker {
    [self dismissViewControllerAnimated:YES completion:NULL];
}

#pragma mark - UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet willDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (buttonIndex == actionSheet.cancelButtonIndex) {
        self.actionSheet = nil;
        return;
    } else if (buttonIndex == kCreateNewContactActionIndex) {
        [self createAndPresentNewContact];
    } else if (buttonIndex == kAddToExistingContactActionIndex) {
        [self presentContactsPicker];
    } else {
        //nothing
    }
    self.actionSheet = nil;
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
    CGFloat kDefaultRowHeight = kCellHeight;
    switch (indexPath.section) {
        case kPersonBaseInfoSection:
            return [DirectoryPersonBaseInfoCell heightForStyle:DirectoryPersonBaseInfoCellStyleLarge];
        case kPhonesSection:
            switch (indexPath.row) {
                case kPrivatePhoneRow:
                    return self.person.privatePhoneNumber ? kDefaultRowHeight : 0.0;
                case kOfficePhoneRow:
                    return self.person.officePhoneNumber ? kDefaultRowHeight : 0.0;
            }
    }
    return kDefaultRowHeight;
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
            switch (indexPath.row) {
                case kPrivatePhoneRow:
                    phone = self.person.privatePhoneNumber;
                    break;
                case kOfficePhoneRow:
                    phone = self.person.officePhoneNumber;
                    break;
            }
            phone = [phone stringByReplacingOccurrencesOfString:@" " withString:@""];
            [[UIApplication sharedApplication] openURL:[NSURL URLWithString:[NSString stringWithFormat:@"tel://%@", phone]]];
            [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
            break;
        }
        case kEmailSection:
            [[UIApplication sharedApplication] openURL:[NSURL URLWithString:[NSString stringWithFormat:@"mailto://%@", self.person.email]]];
            [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
            break;
        case kWebpageSection:
            [[UIApplication sharedApplication] openURL:[NSURL URLWithString:self.person.web]];
            [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
            break;
        case kOfficeSection:
        {
            if (self.allowShowOfficeOnMap) {
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
            switch (indexPath.row) {
                case kPrivatePhoneRow:
                    pasteboard.string = self.person.privatePhoneNumber;
                    break;
                case kOfficePhoneRow:
                    pasteboard.string = self.person.officePhoneNumber;
                    break;
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
    NSLog(@"-> Copied '%@' to pasteboard", pasteboard.string);
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
            switch (indexPath.row) {
                case kPrivatePhoneRow:
                {
                    cell.textLabel.text = NSLocalizedStringFromTable(@"PrivatePhone", @"DirectoryPlugin", nil);
                    cell.detailTextLabel.text = self.person.privatePhoneNumber;
                    break;
                }
                case kOfficePhoneRow:
                {
                    cell.textLabel.text = NSLocalizedStringFromTable(@"OfficePhone", @"DirectoryPlugin", nil);
                    cell.detailTextLabel.text = self.person.officePhoneNumber;
                    break;
                }
            }
            break;
        case kEmailSection:
            cell.textLabel.text = NSLocalizedStringFromTable(@"Email", @"DirectoryPlugin", nil);
            cell.detailTextLabel.text = self.person.email;
            break;
        case kWebpageSection:
            cell.textLabel.text = NSLocalizedStringFromTable(@"Webpage", @"DirectoryPlugin", nil);
            cell.detailTextLabel.text = self.person.web;
            cell.detailTextLabel.lineBreakMode = NSLineBreakByTruncatingMiddle;
            cell.detailTextLabel.adjustsFontSizeToFitWidth = NO;
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
            return (self.person.privatePhoneNumber || self.person.officePhoneNumber) ? 2 : 0;
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


#pragma mark - Dealloc

- (void)dealloc {
    [self.directoryService cancelOperationsForDelegate:self];
}

@end

