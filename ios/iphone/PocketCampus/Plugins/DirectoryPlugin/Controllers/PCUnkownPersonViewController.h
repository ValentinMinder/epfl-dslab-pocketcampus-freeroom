//
//  PCUnkownPersonViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 22.09.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <AddressBook/AddressBook.h>
#import <AddressBookUI/AddressBookUI.h>

#import "directory.h"

@interface PCUnkownPersonViewController : ABUnknownPersonViewController {
    Person* person;
}

@property (nonatomic, retain) Person* person;

- (id)initWithDelegate:(id<ABUnknownPersonViewControllerDelegate>)delegate;
- (void)setProfilePictureData:(NSData*)data;

@end
