//
//  PCUnkownPersonViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 22.09.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <AddressBook/AddressBook.h>
#import <AddressBookUI/AddressBookUI.h>

#import "DirectoryService.h"

@interface PCUnkownPersonViewController : ABUnknownPersonViewController<DirectoryServiceDelegate>

@property (nonatomic, strong) Person* person;


/*
 * If delegate nil, special actions on rows (like tapping on office to see on map) are disabled
 */
- (id)initWithPerson:(Person*)person delegate:(id<ABUnknownPersonViewControllerDelegate>)delegate;

/*
 * Tries to load person with fullName, as first result returned by directory search.
 * If no result, will display a message saying so.
 * WARNING: YOU take the responsability for providing a fullName that is precise enough so that the first result is the correct one.
 */
- (id)initAndLoadPersonWithFullName:(NSString*)fullName delegate:(id<ABUnknownPersonViewControllerDelegate>)delegate;

@end
