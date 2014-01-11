//  Created by Loïc Gardiol on 28.02.12.

#import <Foundation/Foundation.h>
#import "Service.h"
#import "directory.h"

#import "Person+Extras.h"

@interface DirectoryService : Service <ServiceProtocol>

/*
 - (NSArray *) searchPersons: (NSString *) nameOrSciper;  // throws LDAPException *, TException
 - (NSString *) getProfilePicture: (NSString *) sciper;  // throws NoPictureFound *, TException
 - (NSArray *) autocomplete: (NSString *) constraint;  // throws TException
 - (DirectoryToken *) getTequilaTokenForDirectory;  // throws TException
 - (NSString *) getDirectorySession: (DirectoryToken *) dirToken;  // throws TException
 - (DirectoryResponse *) searchDirectory: (DirectoryRequest *) req;  // throws TException
 */

- (void)searchForRequest:(DirectoryRequest*)request delegate:(id)delegate;

- (void)searchPersons:(NSString *)nameOrSciper delegate:(id)delegate __attribute__((deprecated)); //use search instead
- (void)autocomplete:(NSString *)constraint delegate:(id)delegate __attribute((deprecated)); //used searchPersons instead

@end

@protocol DirectoryServiceDelegate <ServiceDelegate>

@optional

- (void)searchForRequest:(DirectoryRequest*)request didReturn:(DirectoryResponse*)response;
- (void)searchFailedForRequest:(DirectoryRequest*)request;

- (void)searchDirectoryFor:(NSString*)searchPattern didReturn:(NSArray*)results __attribute((deprecated));
- (void)searchDirectoryFailedFor:(NSString*)searchPattern __attribute((deprecated));
- (void)autocompleteFor:(NSString *)constraint didReturn:(NSArray*)results __attribute((deprecated));
- (void)autocompleteFailedFor:(NSString *)constraint __attribute((deprecated));

@end