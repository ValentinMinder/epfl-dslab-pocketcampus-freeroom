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

- (id)initWithDelegate:(id<ABUnknownPersonViewControllerDelegate>)delegate;

@end
