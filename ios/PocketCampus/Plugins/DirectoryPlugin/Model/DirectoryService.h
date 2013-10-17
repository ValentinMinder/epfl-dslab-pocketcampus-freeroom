//
//  DirectoryService.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Service.h"
#import "directory.h"

#import "Person+Extras.h"

#import "ASIHTTPRequest.h"

#import "ASIDownloadCache.h"

@interface DirectoryService : Service <ServiceProtocol, ASIHTTPRequestDelegate>

/*
 - (NSArray *) searchPersons: (NSString *) nameOrSciper;  // throws LDAPException *, TException
 - (NSString *) getProfilePicture: (NSString *) sciper;  // throws NoPictureFound *, TException
 - (NSArray *) autocomplete: (NSString *) constraint;  // throws TException
 - (DirectoryToken *) getTequilaTokenForDirectory;  // throws TException
 - (NSString *) getDirectorySession: (DirectoryToken *) dirToken;  // throws TException
 - (DirectoryResponse *) searchDirectory: (DirectoryRequest *) req;  // throws TException
 */

- (void)searchForRequest:(DirectoryRequest*)request delegate:(id)delegate;
- (void)getProfilePicture:(Person *)person delegate:(id)delegate;

- (void)searchPersons:(NSString *)nameOrSciper delegate:(id)delegate __attribute__((deprecated)); //use search instead
- (void)autocomplete:(NSString *)constraint delegate:(id)delegate __attribute((deprecated)); //used searchPersons instead

@end

@protocol DirectoryServiceDelegate <ServiceDelegate>

@optional

- (void)searchForRequest:(DirectoryRequest*)request didReturn:(DirectoryResponse*)response;
- (void)searchFailedForRequest:(DirectoryRequest*)request;
- (void)profilePictureFor:(Person*)person didReturn:(NSData*)data;
- (void)profilePictureFailedFor:(Person*)person;

- (void)searchDirectoryFor:(NSString*)searchPattern didReturn:(NSArray*)results __attribute((deprecated));
- (void)searchDirectoryFailedFor:(NSString*)searchPattern __attribute((deprecated));
- (void)autocompleteFor:(NSString *)constraint didReturn:(NSArray*)results __attribute((deprecated));
- (void)autocompleteFailedFor:(NSString *)constraint __attribute((deprecated));

@end