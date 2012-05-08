//
//  DirectoryService.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "DirectoryService.h"

@implementation DirectoryService

static DirectoryService* instance = nil;

+ (id)sharedInstanceToRetain {
    if (instance != nil) {
        return instance;
    }
    @synchronized(self) {
        if (instance == nil) {
            instance = [[[self class] alloc] initWithServiceName:@"directory"];
        }
    }
    return [instance autorelease];
}

- (id)thriftServiceClientInstance {
    return [[[DirectoryServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]] autorelease];
}

- (void)searchPersons:(NSString *)nameOrSciper delegate:(id)delegate {
    if (![nameOrSciper isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad nameOrSciper" reason:@"nameOrSciper is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
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
    ProfilePictureRequest* operation = [[ProfilePictureRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
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
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
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
        [self retain];
        [self checkPrimariesAndScheduleTimeoutTimer];
        
        if ([self isCancelled])
        {
            @throw [NSException exceptionWithName:@"Operation cancelled" reason:@"operation was cancelled" userInfo:nil];
        }
        
        [self willChangeValueForKey:@"isExecuting"];
        executing = YES;
        [self didChangeValueForKey:@"isExecuting"];

        
        NSString* imageURLString = [self.thriftServiceClient performSelector:self.serviceClientSelector withObject:sciper];
        
        if ([self isCancelled])
        {
            @throw [NSException exceptionWithName:@"Operation cancelled" reason:@"operation was cancelled" userInfo:nil];
        }
        
        if (imageURLString == nil) {
            @throw [NSException exceptionWithName:@"No profile picture" reason:@"" userInfo:nil];
        }
        NSError* error = nil;
        NSData* imageData = [NSData dataWithContentsOfURL:[NSURL URLWithString:imageURLString] options:NSDataReadingMappedIfSafe error:&error];
        
        if ([self isCancelled])
        {
            @throw [NSException exceptionWithName:@"Operation cancelled" reason:@"operation was cancelled" userInfo:nil];
        }
        
        if (error != nil) {
            @throw [NSException exceptionWithName:@"Error while downloading profile picture" reason:@"" userInfo:nil];
        }
        
        UIImage* image = [UIImage imageWithData:imageData];
        image = [[UIImage alloc] initWithCGImage:image.CGImage scale:1.0 orientation:UIImageOrientationUp]; //returning to be sure it's in portrait mode
        imageData = [UIImageJPEGRepresentation(image, 1.0) retain];
        [image release];
        
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self delegateRespondsToSelector:self.delegateDidReturnSelector]) {
                [self.delegate performSelector:self.delegateDidReturnSelector withObject:sciper withObject:imageData];
                [imageData release];
            }
            [self finishAndRelease];
        });
    }
    @catch (NSException *exception) {
        NSLog(@"-> ProfilePictureRequest EXCEPTION caught in main : %@, %@", exception.name, exception.reason);
        @try {
            
            if (self.timedOut) {
                @throw [NSException exceptionWithName:@"-> Request returned after timeout. Ignoring." reason:@"" userInfo:nil];
            }
            
            if (![self delegateRespondsToSelector:self.delegateDidFailSelector]) {
                @throw [NSException exceptionWithName:@"-> Bad delegate response" reason:@"Delegate does not respond to didFail selector. Ignoring" userInfo:nil];
            }

            
            dispatch_async(dispatch_get_main_queue(), ^{
                if ([self delegateRespondsToSelector:self.delegateDidFailSelector]) {
                    [self.delegate performSelector:self.delegateDidFailSelector withObject:sciper];
                }
                [self finishAndRelease];
            });
        }
        @catch (NSException *exception) {
            NSLog(@"%@. %@", exception.name, exception.reason);
            [self finishAndRelease];
        }
    }
}

- (void)dealloc
{
    NSLog(@"-> ProfilePictureRequest released");
    [sciper release];
    [super dealloc];
}

@end