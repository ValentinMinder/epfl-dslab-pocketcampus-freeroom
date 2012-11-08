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

#import "ASIHTTPRequest.h"

#import "ASIDownloadCache.h"

@interface DirectoryService : Service <ServiceProtocol, ASIHTTPRequestDelegate>

- (void)searchPersons:(NSString *)nameOrSciper delegate:(id)delegate;
- (void)getProfilePicture:(NSString *)sciper delegate:(id)delegate;
- (void)autocomplete:(NSString *)constraint delegate:(id)delegate;

@end

@protocol DirectoryServiceDelegate <ServiceDelegate>

@optional
- (void)searchDirectoryFor:(NSString*)searchPattern didReturn:(NSArray*)results;
- (void)searchDirectoryFailedFor:(NSString*)searchPattern;
- (void)profilePictureFor:(NSString*)sciper didReturn:(NSData*)data;
- (void)profilePictureFailedFor:(NSString*)sciper;
- (void)autocompleteFor:(NSString *)constraint didReturn:(NSArray*)results;
- (void)autocompleteFailedFor:(NSString *)constraint;

@end


/* Internal class with modified main to generate NSData from image's URL returned by server */

@interface ProfilePictureRequest : NSOperationWithDelegate<ASIHTTPRequestDelegate> {
    NSString* sciper;
}

- (id)initWithSciper:(NSString*)sciper delegate:(id)delegate;

@property (retain) NSString* sciper;

@end