//
//  DirectoryService.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "DirectoryService.h"

@implementation DirectoryService

//static dispatch_once_t pred = 0;
//static DirectoryService* _sharedObject = nil;

static DirectoryService* instance = nil;

/*+ (id)sharedInstance {
    dispatch_once(&pred, ^{
        _sharedObject = [[[self class] alloc] initWithServiceName:@"directory"];
        _sharedObject.thriftClient =  v;
    });
    return _sharedObject;
}*/

+ (id)sharedInstanceToRetain {
    if (instance != nil) {
        return instance;
    }
    @synchronized(self) {
        if (instance == nil) {
            instance = [[[self class] alloc] initWithServiceName:@"directory"];
            [instance setThriftClient:[[[DirectoryServiceClient alloc] initWithProtocol:instance.thriftProtocol] autorelease]];
        }
    }
    return [instance autorelease];
}



- (void)searchPersons:(NSString *)nameOrSciper delegate:(id)delegate {
    if (![nameOrSciper isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad nameOrSciper" reason:@"nameOrSciper is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:self.thriftClient delegate:delegate];
    operation.serviceClientSelector = @selector(searchPersons:);
    operation.delegateDidReturnSelector = @selector(searchFor:didReturn:);
    operation.delegateDidFailSelector = @selector(searchFailedFor:);
    [operation addObjectArgument:nameOrSciper];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getProfilePicture:(NSString *)sciper delegate:(id)delegate {
    if (![sciper isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad sciper" reason:@"sciper is either nil or not of class NSString" userInfo:nil];
    }
    ProfilePictureRequest* operation = [[ProfilePictureRequest alloc] initWithThriftServiceClient:self.thriftClient delegate:delegate];
    operation.serviceClientSelector = @selector(getProfilePicture:);
    operation.delegateDidReturnSelector = @selector(profilePictureFor:didReturn:);
    operation.delegateDidFailSelector = @selector(profilePictureFailedFor:);
    operation.sciper = sciper;
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)autocomplete:(NSString *)constraint delegate:(id)delegate {
    if (![constraint isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad constraint" reason:@"constraint is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:self.thriftClient delegate:delegate];
    operation.serviceClientSelector = @selector(autocomplete:);
    operation.delegateDidReturnSelector = @selector(autocompleteFor:didReturn:);
    operation.delegateDidFailSelector = @selector(autocompleteFailedFor:);
    [operation addObjectArgument:constraint];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}


- (void)dealloc
{
    instance = nil;
    [super dealloc];
}

@end


@implementation ProfilePictureRequest

@synthesize sciper;

- (void)main {
    @try {
        [self checkPrimariesAndScheduleTimeoutTimer];
        
        NSString* imageURLString = [self.thriftServiceClient performSelector:self.serviceClientSelector withObject:sciper];
        
        if (imageURLString == nil) {
            @throw [NSException exceptionWithName:@"No profile picture" reason:@"" userInfo:nil];
        }
        
        NSError* error = nil;
        NSData* imageData = [NSData dataWithContentsOfURL:[NSURL URLWithString:imageURLString] options:NSDataReadingMappedIfSafe error:&error];
        
        if (error != nil) {
            @throw [NSException exceptionWithName:@"Error while downloading profile picture" reason:@"" userInfo:nil];
        }
        
        if (![self.delegate respondsToSelector:self.delegateDidReturnSelector]) {
            NSLog(@"WARNING : delegate does not respond to returnSelector. Ignoring.");
        } else {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.delegate performSelector:self.delegateDidReturnSelector withObject:sciper withObject:imageData];
            });
        }
    }
    @catch (NSException *exception) {
        NSLog(@"-> ProfilePictureRequest EXCEPTION caught main : %@", exception);
        if (![self.delegate respondsToSelector:self.delegateDidFailSelector]) {
            NSLog(@"WARNING : delegate does not respond to failSelector. Ignoring.");
        } else {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.delegate performSelector:self.delegateDidFailSelector withObject:sciper];
            });
        }
    }
    @finally {
        self.timedOut = YES;
    }
}

- (void)dealloc
{
    [sciper release];
    [super dealloc];
}

@end