//
//  PCUnkownPersonViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 22.09.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PCUnkownPersonViewController.h"

@implementation PCUnkownPersonViewController

@synthesize person;

- (id)initWithDelegate:(id<ABUnknownPersonViewControllerDelegate>)delegate
{
    self = [super init];
    if (self) {
        // Custom initialization
        person = nil;
        self.unknownPersonViewDelegate = delegate;
    }
    return self;
}

- (void)setProfilePictureData:(NSData*)data {
    ABPersonSetImageData(self.displayedPerson,(CFDataRef)data, nil);
    [self loadView]; //reload view content to update picture
}

- (void)setPerson:(Person*)person_ {
    [person release];
    person = [person_ retain];
    ABRecordRef abPerson = ABPersonCreate();
    
    if (![person isKindOfClass:[Person class]]) {
        goto error;
    }
    
    self.displayedPerson = abPerson;
    self.allowsAddingToAddressBook = YES;
    self.allowsActions = YES;
    self.title = [NSString stringWithFormat:@"%@ %@", person.firstName, person.lastName];
    
	CFErrorRef anError = NULL;
    BOOL couldCreate = true;
    
    ABRecordSetValue(abPerson, kABPersonFirstNameProperty, person.firstName, &anError);
    
    ABRecordSetValue(abPerson, kABPersonLastNameProperty, person.lastName, &anError);
    
    ABMultiValueRef phone = ABMultiValueCreateMutable(kABStringPropertyType);
    if (person.officePhoneNumber) {
        couldCreate = ABMultiValueAddValueAndLabel(phone, person.officePhoneNumber, kABWorkLabel, NULL);
        
    }
    if (person.privatePhoneNumber) {
        couldCreate = ABMultiValueAddValueAndLabel(phone, person.privatePhoneNumber, kABHomeLabel, NULL);
        
    }
    if (couldCreate) {
        ABRecordSetValue(abPerson, kABPersonPhoneProperty, phone, &anError);
    }
    CFRelease(phone);
    
    
    if (person.email) {
        ABMultiValueRef email = ABMultiValueCreateMutable(kABMultiStringPropertyType);
        couldCreate = ABMultiValueAddValueAndLabel(email, person.email, (CFStringRef)@"email", NULL);
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
    
    if (person.web) {
        ABMultiValueRef web = ABMultiValueCreateMutable(kABStringPropertyType);
        couldCreate = ABMultiValueAddValueAndLabel(web, person.web, (CFStringRef)@"web", NULL);
        if (couldCreate) {
            ABRecordSetValue(abPerson, kABPersonURLProperty, web, &anError);
        }
        CFRelease(web);
    }
    
    if (person.office && self.unknownPersonViewDelegate) { //won't show office as row if no delegate to receive the map call
        ABMultiValueRef office = ABMultiValueCreateMutable(kABMultiDictionaryPropertyType);
        NSMutableDictionary *addressDictionary = [NSMutableDictionary dictionaryWithCapacity:2];
        NSString* label = NSLocalizedStringFromTable(@"OfficeLabel", @"DirectoryPlugin", @"Short name to describe label of office room");
        [addressDictionary setObject:[NSString stringWithFormat:@"%@ %@", person.office, NSLocalizedStringFromTable(@"(showOnMap)", @"DirectoryPlugin", nil)] forKey:(NSString *)kABPersonAddressCityKey];
        [addressDictionary setObject:@"" forKey:(NSString *)kABPersonAddressCountryKey];
        couldCreate = ABMultiValueAddValueAndLabel(office, addressDictionary, (CFStringRef)label, NULL);
        if (couldCreate) {
            ABRecordSetValue(abPerson, kABPersonAddressProperty, office, &anError);
        }
        CFRelease(office);
    }
    
    NSString* message = @"";
    if (person.OrganisationalUnit) {
        for (NSString* unit in person.OrganisationalUnit) {
            message = [message stringByAppendingFormat:@"%@ ", unit];
        }
    }
    if (person.office && !self.unknownPersonViewDelegate) {
        message = [message stringByAppendingFormat:@"\n%@", person.office];
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

- (void)dealloc
{
    [person release];
    [super dealloc];
}

@end

