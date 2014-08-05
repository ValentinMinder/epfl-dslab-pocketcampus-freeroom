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


#import "directory.h"

@import AddressBook;

@interface Person (Extras)

/*
 * Returns full "Firstname(s) Lastname"
 */
@property (nonatomic, readonly) NSString* fullFirstnameLastname;

/*
 * Returns "Firstname Lastname", taking only the first firstname if there are more than one
 */
@property (nonatomic, readonly) NSString* firstnameLastname;

@property (nonatomic, readonly) NSString* organizationalUnitsStrings;

@property (nonatomic, readonly) NSString* rolesString;

/*
 * Returns what is before the @ (loic.gardiol@epfl.ch => loic.gardiol)
 */
@property (nonatomic, readonly) NSString* emailPrefix;

/*
 * Creates and returns an ABRecordRef with info of self.
 * WARNING: this record should be released with CFRelease after use.
 */
- (ABRecordRef)newABRecord;

/*
 * Returns an ABRecordRef person that is contains info of self + person, or nil in case of failure
 */
- (ABRecordRef)newMergedWithABRecord:(ABRecordRef)person addressBook:(ABAddressBookRef)addressBook;

/**
 * @return url pointing to people.epfl.ch page of the unit
 * @param unit short unit abbreviation (e.g. DSLAB)
 */
+ (NSURL*)directoryWebpageURLForUnit:(NSString*)unit;

@end
