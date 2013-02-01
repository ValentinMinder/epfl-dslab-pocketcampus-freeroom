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

@interface PCUnkownPersonViewController ()

@property (nonatomic, strong) UIImage* profilePictureImage;
@property (nonatomic, strong) UIPopoverController* profilePicturePopover;
@property (nonatomic, strong) DirectoryService* directoryService;

@end

@implementation PCUnkownPersonViewController

- (id)initWithDelegate:(id<ABUnknownPersonViewControllerDelegate>)delegate
{
    self = [super init];
    if (self) {
        // Custom initialization
        self.unknownPersonViewDelegate = delegate;
        self.directoryService = [DirectoryService sharedInstanceToRetain];
    }
    return self;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationItem.backBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Details", @"DirectoryPlugin", nil) style:UIBarButtonItemStyleBordered target:nil action:nil];
    if (!self.profilePictureImage) {
        [self.directoryService getProfilePicture:self.person.sciper delegate:self];
    }
}

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
}

#pragma mark DirectoryServiceDelegate

- (void)profilePictureFor:(NSString*)sciper didReturn:(NSData*)data {
    if (data) {
        ABPersonSetImageData(self.displayedPerson,(__bridge CFDataRef)data, nil);
        [self loadView]; //reload view content to update picture
        self.profilePictureImage = [UIImage imageWithData:data];
        self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"PhotoButtonTitle", @"DirectoryPlugin", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(photoButtonPressed)];
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
    NSLog(@"-> ProfilePicture request timed out");
    ABPersonSetImageData(self.displayedPerson, NULL, nil);
    [self loadView]; //reload view content to update picture
}

#pragma mark dealloc

- (void)dealloc {
    [self.directoryService cancelOperationsForDelegate:self];
}

@end

