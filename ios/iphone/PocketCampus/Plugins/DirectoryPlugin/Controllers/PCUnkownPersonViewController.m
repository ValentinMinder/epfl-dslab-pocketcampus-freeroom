//
//  PCUnkownPersonViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 22.09.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PCUnkownPersonViewController.h"

#import "DirectoryProfilePictureViewController.h"

#import "UIPopoverController+Additions.h"

#import "DirectoryService.h"

#import "PCUtils.h"

#import "PCValues.h"

@interface PCUnkownPersonViewController ()

@property (nonatomic, strong) NSString* fullNameToSearch;
@property (nonatomic, strong) UIImage* profilePictureImage;
@property (nonatomic, strong) UIPopoverController* profilePicturePopover;
@property (nonatomic, strong) DirectoryService* directoryService;

/* Will only be instantied and added to view if initAndLoadPersonWithFullName:delegate: constructor used */
@property (nonatomic, strong) UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, strong) UILabel* centerMessageLabel;

@end

@implementation PCUnkownPersonViewController

#pragma mark - Inits

- (id)initWithPerson:(Person*)person delegate:(id<ABUnknownPersonViewControllerDelegate>)delegate;
{
    [PCUtils throughExceptionIfObject:person notKindOfClass:[Person class]];
    self = [super init];
    if (self) {
        self.unknownPersonViewDelegate = delegate;
        self.directoryService = [DirectoryService sharedInstanceToRetain];
        self.person = person;
    }
    return self;
}

- (id)initAndLoadPersonWithFullName:(NSString*)fullName delegate:(id<ABUnknownPersonViewControllerDelegate>)delegate {
    [PCUtils throughExceptionIfObject:fullName notKindOfClass:[NSString class]];
    self = [super init];
    if (self) {
        self.unknownPersonViewDelegate = delegate;
        self.directoryService = [DirectoryService sharedInstanceToRetain];
        self.fullNameToSearch = fullName;
    }
    return self;
}

#pragma mark - Standard view controller methods

- (void)viewDidLoad {
    [super viewDidLoad];
    if (!self.person) {
        [self.directoryService searchPersons:self.fullNameToSearch delegate:self];
        
        self.centerMessageLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 280.0, 100.0)];
        self.centerMessageLabel.numberOfLines = 0; //auto
        self.centerMessageLabel.font = [UIFont boldSystemFontOfSize:16.0];
        self.centerMessageLabel.textAlignment = UITextAlignmentCenter;
        self.centerMessageLabel.backgroundColor = [UIColor clearColor];
        self.centerMessageLabel.textColor = [PCValues textColor1];
        self.centerMessageLabel.shadowColor = [UIColor whiteColor];
        self.centerMessageLabel.shadowOffset = [PCValues shadowOffset1];
        self.centerMessageLabel.hidden = NO;
        [self.view addSubview:self.centerMessageLabel];
        self.centerMessageLabel.center = CGPointMake(self.view.center.x, self.view.center.y-35.0);
        
        self.loadingIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
        self.loadingIndicator.color = [UIColor colorWithWhite:0.3 alpha:1.0];
        [self.view addSubview:self.loadingIndicator];
        self.loadingIndicator.center = CGPointMake(self.view.center.x, self.view.center.y-35.0);
        self.loadingIndicator.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleBottomMargin;
        [self.loadingIndicator startAnimating];
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationItem.backBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Details", @"DirectoryPlugin", nil) style:UIBarButtonItemStyleBordered target:nil action:nil];
}


#pragma mark - Button actions

- (void)photoButtonPressed {
    DirectoryProfilePictureViewController* viewController = [[DirectoryProfilePictureViewController alloc] initWithImage:self.profilePictureImage];
    
    if (self.splitViewController) {
        if (!self.profilePicturePopover) {
            self.profilePicturePopover = [[UIPopoverController alloc] initWithContentViewController:viewController];
            //[self.profilePicturePopover setPopoverContentSize:CGSizeMake(320.0, 480.0)];
            [self.profilePicturePopover setPopoverContentSize:viewController.contentSizeForViewInPopover];
        }
        [self.profilePicturePopover togglePopoverFromBarButtonItem:self.navigationItem.rightBarButtonItem permittedArrowDirections:UIPopoverArrowDirectionAny animated:YES];
    } else {
        [self.navigationController pushViewController:viewController animated:YES];
    }
}

#pragma mark - Data related

- (void)setPerson:(Person*)person_ {
    _person = person_;
    ABRecordRef abPerson = ABPersonCreate();
    
    if (![self.person isKindOfClass:[Person class]]) {
        @throw [NSException exceptionWithName:@"Illegal argument" reason:@"person is not kind of class Person" userInfo:nil];
    }
    
    self.displayedPerson = abPerson;
    self.allowsAddingToAddressBook = YES;
    self.allowsActions = YES;
    self.title = [NSString stringWithFormat:@"%@ %@", self.person.firstName, self.person.lastName];
    
    UIImage* loadingImage = [UIImage imageNamed:@"LoadingIndicator"];
    NSData* imageData = UIImagePNGRepresentation(loadingImage);
    ABPersonSetImageData(self.displayedPerson,(__bridge CFDataRef)imageData, nil);
    
	CFErrorRef anError = NULL;
    BOOL couldCreate = true;
    
    ABRecordSetValue(abPerson, kABPersonFirstNameProperty, (__bridge CFTypeRef)(self.person.firstName), &anError);
    
    ABRecordSetValue(abPerson, kABPersonLastNameProperty, (__bridge CFTypeRef)(self.person.lastName), &anError);
    
    ABMultiValueRef phone = ABMultiValueCreateMutable(kABStringPropertyType);
    if (self.person.officePhoneNumber) {
        couldCreate = ABMultiValueAddValueAndLabel(phone, (__bridge CFTypeRef)(self.person.officePhoneNumber), kABWorkLabel, NULL);
        
    }
    if (self.person.privatePhoneNumber) {
        couldCreate = ABMultiValueAddValueAndLabel(phone, (__bridge CFTypeRef)(self.person.privatePhoneNumber), kABHomeLabel, NULL);
        
    }
    if (couldCreate) {
        ABRecordSetValue(abPerson, kABPersonPhoneProperty, phone, &anError);
    }
    CFRelease(phone);
    
    
    if (self.person.email) {
        ABMultiValueRef email = ABMultiValueCreateMutable(kABMultiStringPropertyType);
        couldCreate = ABMultiValueAddValueAndLabel(email, (__bridge CFTypeRef)(self.person.email), (CFStringRef)@"email", NULL);
        if (couldCreate) {
            ABRecordSetValue(abPerson, kABPersonEmailProperty, email, &anError);
        }
        CFRelease(email);
    }
    
    /* WARNING : This property makes the app crash when the viewController is pushed onto the nav stack. Why ??? Seems to be a bug on iOS 5.1. Reported to Apple. */
    /*ABMultiValueRef office = ABMultiValueCreateMutable(kABStringPropertyType);
     if (person.office) {
     NSMutableString* label = [NSLocalizedStringFromTable(@"OfficeLabel", @"DirectoryPlugin", @"Short name to describe label of office room") mutableCopy];
     couldCreate = ABMultiValueAddValueAndLabel(office, [person.office mutableCopy], (CFStringRef)label, NULL);
     //couldCreate = ABMultiValueAddValueAndLabel(office, person.sciper, (CFStringRef)@"sciper", NULL);
     if (couldCreate) {
     ABRecordSetValue(abPerson, kABPersonInstantMessageProperty, office, &anError);
     CFRelease(office);
     }
     }*/
    
    if (self.person.web) {
        ABMultiValueRef web = ABMultiValueCreateMutable(kABStringPropertyType);
        couldCreate = ABMultiValueAddValueAndLabel(web, (__bridge CFTypeRef)(self.person.web), (__bridge CFStringRef)@"web", NULL);
        if (couldCreate) {
            ABRecordSetValue(abPerson, kABPersonURLProperty, web, &anError);
        }
        CFRelease(web);
    }
    
    if (self.person.office && self.unknownPersonViewDelegate) { //won't show office as row if no delegate to receive the map call
        ABMultiValueRef office = ABMultiValueCreateMutable(kABMultiDictionaryPropertyType);
        NSMutableDictionary *addressDictionary = [NSMutableDictionary dictionaryWithCapacity:2];
        NSString* label = NSLocalizedStringFromTable(@"OfficeLabel", @"DirectoryPlugin", @"Short name to describe label of office room");
        [addressDictionary setObject:[NSString stringWithFormat:@"%@ %@", self.person.office, NSLocalizedStringFromTable(@"(showOnMap)", @"DirectoryPlugin", nil)] forKey:(NSString *)kABPersonAddressCityKey];
        [addressDictionary setObject:@"" forKey:(NSString *)kABPersonAddressCountryKey];
        couldCreate = ABMultiValueAddValueAndLabel(office, (__bridge CFTypeRef)(addressDictionary), (__bridge CFStringRef)label, NULL);
        if (couldCreate) {
            ABRecordSetValue(abPerson, kABPersonAddressProperty, office, &anError);
        }
        CFRelease(office);
    }
    
    NSString* message = @"";
    if (self.person.OrganisationalUnit) {
        for (NSString* unit in self.person.OrganisationalUnit) {
            message = [message stringByAppendingFormat:@"%@ ", unit];
        }
    }
    if (self.person.office && !self.unknownPersonViewDelegate) {
        message = [message stringByAppendingFormat:@"\n%@", self.person.office];
    }
    self.message = message;
    
    
    if (anError != NULL) {
    error:
        CFRelease(abPerson);
        @throw [NSException exceptionWithName:@"ABPerson error" reason:@"could not create ABUnknownPersonViewController" userInfo:nil];
    }
    
    CFRelease(abPerson);
    
    [self loadView];
    
    [self.directoryService getProfilePicture:self.person.sciper delegate:self];
}

#pragma mark - DirectoryServiceDelegate

- (void)searchDirectoryFor:(NSString *)searchPattern didReturn:(NSArray *)results {
    [self.loadingIndicator stopAnimating];
    if ([results count] == 0) {
        self.centerMessageLabel.hidden = NO;
        self.centerMessageLabel.text = NSLocalizedStringFromTable(@"NoResultPCUnknownViewControllerLoad", @"DirectoryPlugin", nil);
        return;
    }
    Person* person = results[0];
    self.person = person;
}

- (void)searchDirectoryFailedFor:(NSString *)searchPattern {
    [self.loadingIndicator stopAnimating];
    self.centerMessageLabel.hidden = NO;
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", @"Message that says that connection to server throw an error");
}

- (void)profilePictureFor:(NSString*)sciper didReturn:(NSData*)data {
    if (data) {
        ABPersonSetImageData(self.displayedPerson,(__bridge CFDataRef)data, nil);
        [self loadView]; //reload view content to update picture
        self.profilePictureImage = [UIImage imageWithData:data];
        UIBarButtonItem* photoButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"PhotoButtonTitle", @"DirectoryPlugin", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(photoButtonPressed)];
        [self.navigationItem setRightBarButtonItem:photoButton animated:YES];
    } else {
        [self profilePictureFailedFor:sciper];
    }
}

- (void)profilePictureFailedFor:(NSString*)sciper {
    NSLog(@"-> Profile picture request failed (possibly no picture available)");
    ABPersonSetImageData(self.displayedPerson,NULL, nil);
    [self loadView]; //reload view content to update picture
}

- (void)serviceConnectionToServerTimedOut {
    if (self.person) {
        NSLog(@"-> ProfilePicture request timed out");
        ABPersonSetImageData(self.displayedPerson, NULL, nil);
        [self loadView]; //reload view content to update picture
    } else {
        [self.loadingIndicator stopAnimating];
        self.centerMessageLabel.hidden = NO;
        self.centerMessageLabel.text = NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", @"Message that says that connection to server is impossible and that internet connection must be checked.");
    }
}

#pragma mark dealloc

- (void)dealloc {
    [self.directoryService cancelOperationsForDelegate:self];
}

@end

