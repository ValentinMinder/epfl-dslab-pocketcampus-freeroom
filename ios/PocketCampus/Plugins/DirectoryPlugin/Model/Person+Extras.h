//
//  Person+Extras.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 25.09.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "directory.h"

@import AddressBook;

@interface Person (Extras)

@property (nonatomic, readonly) NSString* fullFirstnameLastname;

@property (nonatomic, readonly) NSString* organizationsString;

/*
 * Returns what is before the @ (loic.gardiol@epfl.ch => loic.gardiol)
 */
@property (nonatomic, readonly) NSString* emailPrefix;

/*
 * Creates and returns an ABRecordRef with info of self.
 * WARNING: this record should be released with CFRelease after use.
 */
- (ABRecordRef)createABRecord;

/*
 * Adds info of self to person.
 * Returns YES if info could be added, NO on error
 */
- (BOOL)addInfoToABRecord:(ABRecordRef)person;

@end
