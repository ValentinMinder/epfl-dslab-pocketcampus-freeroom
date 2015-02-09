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

//  Created by Loïc Gardiol on 25.09.13.

#import "Person+Extras.h"

@implementation Person (Extras)

- (NSString*)fullFirstnameLastname {
    return [self firstnameLastnameWithFirstName:self.firstName];
}

- (NSString*)firstnameLastname {
    NSString* firstNameOnly = self.firstName;
    NSArray* elems = [firstNameOnly componentsSeparatedByString:@" "];
    firstNameOnly = [elems firstObject];
    return [self firstnameLastnameWithFirstName:firstNameOnly];
}

- (NSString*)organizationalUnitsStrings {
    NSString* string __block = @"";
    [self.organisationalUnits enumerateObjectsUsingBlock:^(NSString* unit, NSUInteger index, BOOL *stop) {
        if (index < self.organisationalUnits.count - 1) {
            string = [string stringByAppendingFormat:@"%@ ", unit];
        } else {
            string = [string stringByAppendingString:unit];
        }
    }];
    return string;
}

- (NSString*)rolesString {
    NSString* string __block = nil;
    [self.roles enumerateKeysAndObjectsUsingBlock:^(NSString* unit, DirectoryPersonRole* role, BOOL *stop) {
        if (string) {
            string  = [string stringByAppendingString:@"\n"];
        } else {
            string = @"";
        }
        NSString* titleAndRole = nil;
        if (role.localizedTitle.length > 0 && role.extendedLocalizedUnit.length > 0) {
            titleAndRole = [NSString stringWithFormat:@"%@ - %@", role.localizedTitle, role.extendedLocalizedUnit];
        } else if (role.localizedTitle.length > 0) {
            titleAndRole = role.localizedTitle;
        } else if (role.extendedLocalizedUnit.length > 0) {
            titleAndRole = role.extendedLocalizedUnit;
        } else {
            titleAndRole = @"";
        }
        string = [string stringByAppendingString:titleAndRole];
    }];
    return string;
}

- (NSString*)emailPrefix {
    NSRange atRange = [self.email rangeOfString:@"@"];
    if (atRange.location == NSNotFound) {
        return nil;
    }
    return [self.email stringByReplacingCharactersInRange:NSMakeRange(atRange.location, self.email.length-atRange.location) withString:@""];
}

- (ABRecordRef)newABRecord {
    ABRecordRef abPerson = ABPersonCreate();
    
    CFErrorRef error = NULL;
	BOOL couldCreate = YES;
    
	ABRecordSetValue(abPerson, kABPersonFirstNameProperty, (__bridge CFTypeRef)(self.firstName), &error);
    
	ABRecordSetValue(abPerson, kABPersonLastNameProperty, (__bridge CFTypeRef)(self.lastName), &error);
    
    if (self.officePhoneNumber || self.privatePhoneNumber) {
        ABMultiValueRef phone = ABMultiValueCreateMutable(kABStringPropertyType);
        if (self.officePhoneNumber) {
            couldCreate = ABMultiValueAddValueAndLabel(phone, (__bridge CFTypeRef)(self.officePhoneNumber), (CFStringRef)@"EPFL", NULL);
        }
        if (self.privatePhoneNumber) {
            couldCreate = ABMultiValueAddValueAndLabel(phone, (__bridge CFTypeRef)(self.privatePhoneNumber), kABHomeLabel, NULL);
        }
        if (couldCreate) {
            ABRecordSetValue(abPerson, kABPersonPhoneProperty, phone, &error);
        }
        CFRelease(phone);
    }
    
	if (self.email) {
		ABMultiValueRef email = ABMultiValueCreateMutable(kABMultiStringPropertyType);
		couldCreate = ABMultiValueAddValueAndLabel(email, (__bridge CFTypeRef)(self.email), (CFStringRef)@"EPFL", NULL);
		if (couldCreate) {
			ABRecordSetValue(abPerson, kABPersonEmailProperty, email, &error);
		}
		CFRelease(email);
	}
    
    if (self.web) {
		ABMultiValueRef web = ABMultiValueCreateMutable(kABStringPropertyType);
		couldCreate = ABMultiValueAddValueAndLabel(web, (__bridge CFTypeRef)(self.web), (__bridge CFStringRef)(NSLocalizedStringFromTable(@"EPFLWebpage", @"DirectoryPlugin", nil)), NULL);
		if (couldCreate) {
			ABRecordSetValue(abPerson, kABPersonURLProperty, web, &error);
		}
		CFRelease(web);
	}
    
	/* WARNING : This property makes the app crash when the viewController is pushed onto the nav stack. Why ??? Seems to be a bug since iOS 5.1. Reported to Apple. */
	/*if (self.office) {
        ABMultiValueRef office = ABMultiValueCreateMutable(kABStringPropertyType);
        couldCreate = ABMultiValueAddValueAndLabel(office, (__bridge CFTypeRef)(self.office), (__bridge CFStringRef)(NSLocalizedStringFromTable(@"EPFLOffice", @"DirectoryPlugin", nil)), NULL);
        if (couldCreate) {
			ABRecordSetValue(abPerson, kABPersonNoteProperty, office, &anError);
		}
        //CFRelease(office);
    }*/

    
	if (self.office) {
		ABMultiValueRef office = ABMultiValueCreateMutable(kABMultiDictionaryPropertyType);
		NSMutableDictionary *addressDictionary = [NSMutableDictionary dictionaryWithCapacity:2];
        [addressDictionary setObject:self.office forKey:(NSString *)kABPersonAddressCityKey];
		[addressDictionary setObject:@"" forKey:(NSString *)kABPersonAddressCountryKey];
		couldCreate = ABMultiValueAddValueAndLabel(office, (__bridge CFTypeRef)(addressDictionary), (__bridge CFStringRef)(NSLocalizedStringFromTable(@"EPFLOffice", @"DirectoryPlugin", nil)), NULL);
		if (couldCreate) {
			ABRecordSetValue(abPerson, kABPersonAddressProperty, office, &error);
		}
		CFRelease(office);
	}
    
    
    
	/*NSString* message = @"";
     if (self.OrganisationalUnit) {
     for (NSString* unit in self.OrganisationalUnit) {
     message = [message stringByAppendingFormat:@"%@ ", unit];
     }
     }
     */
    
	if (error) {
        CFRelease(abPerson);
        return nil;
    }
    return abPerson;
}


- (ABRecordRef)newMergedWithABRecord:(ABRecordRef)abPerson addressBook:(ABAddressBookRef)addressBook {
    
    // Cannot directly work on abPerson, because might be an aggregate of contact info (for e.g. iCloud + Facebook)
    // ABRecordSetValue fails if working on an aggregate contact
    // So we have to find the corresponding "leaf" contat from ABAddressBookCopyDefaultSource(addressBook)
    // which returns for example iCloud for example if this is user's default address book
    // one cannot use the recordID for that because recordID between aggregate source and leaf record might not be the same
    
    CFStringRef originalFirstname = ABRecordCopyValue(abPerson, kABPersonFirstNameProperty);
    CFStringRef originalLastname = ABRecordCopyValue(abPerson, kABPersonLastNameProperty);
    ABRecordRef addressBookDefaultSource = ABAddressBookCopyDefaultSource(addressBook);
    CFArrayRef allPersons = ABAddressBookCopyArrayOfAllPeopleInSource(addressBook, addressBookDefaultSource);
    BOOL found = NO;
    for (int i = 0; i<CFArrayGetCount(allPersons); i++) {
        ABRecordRef person = CFArrayGetValueAtIndex(allPersons, i);
        CFStringRef firstname = ABRecordCopyValue(person, kABPersonFirstNameProperty);
        CFStringRef lastname = ABRecordCopyValue(person, kABPersonLastNameProperty);
        if (originalFirstname && originalLastname) {
            //selected person has both first and last names info,
            //so contact to find should have exactly the same info
            if (firstname && lastname
                && CFStringCompare(firstname, originalFirstname, 0) == kCFCompareEqualTo
                && CFStringCompare(lastname, originalLastname, 0) == kCFCompareEqualTo) {
                abPerson = CFRetain(person);
                found = YES;
            }
        } else if (originalFirstname) { //originalLastname is NULL
            //selected person has only firstname info,
            //so contact to find should have exactly the same info
            if (firstname && !lastname
                && CFStringCompare(firstname, originalFirstname, 0) == kCFCompareEqualTo) {
                abPerson = CFRetain(person);
                found = YES;
            }
        } else if (originalLastname) { //originalFirstname is NULL
            //selected person has only lastname info,
            //so contact to find should have exactly the same info
            if (!firstname && lastname
                && CFStringCompare(lastname, originalLastname, 0) == kCFCompareEqualTo) {
                abPerson = CFRetain(person);
                found = YES;
            }
        }
        if (firstname) {
            CFRelease(firstname);
        }
        if (lastname) {
            CFRelease(lastname);
        }
        if (found) {
            break;
        }
    }
    
    if (originalFirstname) {
        CFRelease(originalFirstname);
    }
    if (originalLastname) {
        CFRelease(originalLastname);
    }
    if (allPersons) {
        CFRelease(allPersons);
    }
    if (addressBookDefaultSource) {
        CFRelease(addressBookDefaultSource);
    }
    
    if (!found) {
        return nil;
    }
    
    //From this point, we have a valid, default source record, that we can work on
    
    BOOL couldCreate = YES;
    CFErrorRef error = NULL; 
    if (self.officePhoneNumber || self.privatePhoneNumber) {
        ABMultiValueRef phones = ABRecordCopyValue(abPerson, kABPersonPhoneProperty);
        CFAutorelease(phones);
        phones = phones && ABMultiValueGetCount(phones) > 0 ? ABMultiValueCreateMutableCopy(phones) : ABMultiValueCreateMutable(kABMultiStringPropertyType);
        NSArray* existingValues = (__bridge NSArray*)(ABMultiValueCopyArrayOfAllValues(phones));
        if (self.privatePhoneNumber && ![existingValues containsObject:self.privatePhoneNumber]) {
            couldCreate = ABMultiValueAddValueAndLabel(phones, (__bridge CFTypeRef)(self.privatePhoneNumber), kABHomeLabel, NULL);
        }
        if (self.officePhoneNumber && ![existingValues containsObject:self.officePhoneNumber]) {
            couldCreate = ABMultiValueAddValueAndLabel(phones, (__bridge CFTypeRef)(self.officePhoneNumber), kABWorkLabel, NULL);
        }
        if (couldCreate) {
            ABRecordSetValue(abPerson, kABPersonPhoneProperty, phones, &error);
        }
        if (existingValues) {
            CFRelease((CFArrayRef)(existingValues));
        }
        CFRelease(phones);
    }
    
    if (self.email) {
		ABMultiValueRef emails = ABRecordCopyValue(abPerson, kABPersonEmailProperty);
        CFAutorelease(emails);
        emails = emails && ABMultiValueGetCount(emails) > 0 ? ABMultiValueCreateMutableCopy(emails) : ABMultiValueCreateMutable(kABMultiStringPropertyType);
        NSArray* existingValues = (__bridge NSArray*)(ABMultiValueCopyArrayOfAllValues(emails));
        if (![existingValues containsObject:self.email]) {
            couldCreate = ABMultiValueAddValueAndLabel(emails, (__bridge CFTypeRef)(self.email), kABWorkLabel, NULL);
            if (couldCreate) {
                ABRecordSetValue(abPerson, kABPersonEmailProperty, emails, &error);
            }
        }
        if (existingValues) {
            CFRelease((CFArrayRef)(existingValues));
        }
		CFRelease(emails);
	}

    if (self.web) {
		ABMultiValueRef webUrls = ABRecordCopyValue(abPerson, kABPersonURLProperty);
        CFAutorelease(webUrls);
        webUrls = webUrls && ABMultiValueGetCount(webUrls) > 0 ? ABMultiValueCreateMutableCopy(webUrls) : ABMultiValueCreateMutable(kABMultiStringPropertyType);
        NSArray* existingValues = (__bridge NSArray*)(ABMultiValueCopyArrayOfAllValues(webUrls));
        if (![existingValues containsObject:self.web]) {
            couldCreate = ABMultiValueAddValueAndLabel(webUrls, (__bridge CFTypeRef)(self.web), kABPersonHomePageLabel, NULL);
            if (couldCreate) {
                ABRecordSetValue(abPerson, kABPersonURLProperty, webUrls, &error);
            }
        }
        if (existingValues) {
            CFRelease((CFArrayRef)(existingValues));
        }
		CFRelease(webUrls);
	}
    
    if (self.office) {
        ABMultiValueRef addresses = ABRecordCopyValue(abPerson, kABPersonAddressProperty);
        CFAutorelease(addresses);
        addresses = addresses && ABMultiValueGetCount(addresses) > 0 ? ABMultiValueCreateMutableCopy(addresses) : ABMultiValueCreateMutable(kABMultiDictionaryPropertyType);
        NSMutableDictionary* addressDictionary = [NSMutableDictionary dictionaryWithCapacity:2];
        [addressDictionary setObject:self.office forKey:(NSString *)kABPersonAddressCityKey];
		[addressDictionary setObject:@"" forKey:(NSString *)kABPersonAddressCountryKey];
        BOOL shouldAdd = YES;
        NSArray* existingDictionaries = (__bridge NSArray*)(ABMultiValueCopyArrayOfAllValues(addresses));
        for (NSDictionary* existingDic in existingDictionaries) {
            if ([existingDic isEqualToDictionary:addressDictionary]) {
                shouldAdd = NO;
                break;
            }
        }
		if (shouldAdd) {
            couldCreate = ABMultiValueAddValueAndLabel(addresses, (__bridge CFTypeRef)(addressDictionary), (__bridge CFStringRef)(NSLocalizedStringFromTable(@"EPFLOffice", @"DirectoryPlugin", nil)), NULL);
            if (couldCreate) {
                ABRecordSetValue(abPerson, kABPersonAddressProperty, addresses, &error);
            }
        }
        if (existingDictionaries) {
            CFRelease((CFArrayRef)(existingDictionaries));
        }
		CFRelease(addresses);
	}
    
    if (error) {
        CFRelease(abPerson);
        return nil;
    }
    return abPerson;
}

+ (NSURL*)directoryWebpageURLForUnit:(NSString*)unit {
    [PCUtils throwExceptionIfObject:unit notKindOfClass:[NSString class]];
    static NSString* const kUnitPageWithFormat = @"http://search.epfl.ch/search/ubrowse.action?acro=%@";
    NSString* urlString = [NSString stringWithFormat:kUnitPageWithFormat, unit];
    return [NSURL URLWithString:urlString];
}

#pragma mark - Private methods

- (NSString*)firstnameLastnameWithFirstName:(NSString*)firstname {
    if (firstname && self.lastName) {
        return [NSString stringWithFormat:@"%@ %@", firstname, self.lastName];
    } else if (firstname) {
        return firstname;
    } else if (self.lastName) {
        return self.lastName;
    } else {
        return @"";
    }
}


@end
