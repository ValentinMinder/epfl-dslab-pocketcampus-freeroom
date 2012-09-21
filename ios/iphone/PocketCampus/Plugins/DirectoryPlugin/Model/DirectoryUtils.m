//
//  DirectoryUtils.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 21.09.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "DirectoryUtils.h"

#import <AddressBook/AddressBook.h>
#import <AddressBookUI/AddressBookUI.h>

@implementation DirectoryUtils

+ (UIViewController*)viewControllerForPerson:(Person*)person {
    ABRecordRef abPerson = ABPersonCreate();
    
    if (![person isKindOfClass:[Person class]]) {
        goto error;
    }
    
    ABUnknownPersonViewController* viewController = [[ABUnknownPersonViewController alloc] init];
    
    viewController.displayedPerson = abPerson;
    viewController.allowsAddingToAddressBook = YES;
    viewController.allowsActions = YES;
    viewController.title = [NSString stringWithFormat:@"%@ %@", person.firstName, person.lastName];
    
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
    
    
    
    /*if (person.office) {
        ABMultiValueRef office = ABMultiValueCreateMutable(kABMultiDictionaryPropertyType);
        NSMutableDictionary *addressDictionary = [NSMutableDictionary dictionaryWithCapacity:2];
        NSString* label = NSLocalizedStringFromTable(@"OfficeLabel", @"DirectoryPlugin", @"Short name to describe label of office room");
        [addressDictionary setObject:person.office forKey:(NSString *)kABPersonAddressCityKey];
        [addressDictionary setObject:@"" forKey:(NSString *)kABPersonAddressCountryKey];
        couldCreate = ABMultiValueAddValueAndLabel(office, addressDictionary, (CFStringRef)label, NULL);
        if (couldCreate) {
            ABRecordSetValue(abPerson, kABPersonAddressProperty, office, &anError);
        }
        CFRelease(office);
    }*/
    
    if (person.web) {
        ABMultiValueRef web = ABMultiValueCreateMutable(kABStringPropertyType);
        couldCreate = ABMultiValueAddValueAndLabel(web, person.web, (CFStringRef)@"web", NULL);
        if (couldCreate) {
            ABRecordSetValue(abPerson, kABPersonURLProperty, web, &anError);
        }
        CFRelease(web);
    }
    
    
    
    if (anError != NULL) {
    error:
        NSLog(@"-> viewControllerForPerson: an error occured");
        CFRelease(abPerson);
        UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Impossible to display this person, sorry." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
        [alert release];
        return nil;

    }
    
    
    if (person.OrganisationalUnit) {
        NSString* message = @"";
        for (NSString* unit in person.OrganisationalUnit) {
            message = [message stringByAppendingFormat:@"%@ ", unit];
        }
        if (person.office) {
            message = [message stringByAppendingFormat:@"\n%@", person.office];
        }
        viewController.message = message;
    }
    CFRelease(abPerson);
    
    return [viewController autorelease];
}

@end
