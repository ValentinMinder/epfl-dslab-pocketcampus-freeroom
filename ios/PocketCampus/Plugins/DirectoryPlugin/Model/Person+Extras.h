



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
