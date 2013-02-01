//
//  DirectoryService.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "DirectoryService.h"

@implementation DirectoryService

static DirectoryService* instance __weak = nil;

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"DirectoryService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"directory"];
        if (self) {
            instance = self;
        }
        return self;
    }
}

+ (id)sharedInstanceToRetain {
    @synchronized (self) {
        if (instance) {
            return instance;
        }
#if __has_feature(objc_arc)
        return [[[self class] alloc] init];
#else
        return [[[[self class] alloc] init] autorelease];
#endif
    }
}

- (id)thriftServiceClientInstance {
    return [[[DirectoryServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]] autorelease];
}

- (void)searchPersons:(NSString *)nameOrSciper delegate:(id)delegate {
    if (![nameOrSciper isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad nameOrSciper" reason:@"nameOrSciper is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.keepInCache = YES;
    operation.cacheValidity = 60; //1 min
    operation.serviceClientSelector = @selector(searchPersons:);
    operation.delegateDidReturnSelector = @selector(searchDirectoryFor:didReturn:);
    operation.delegateDidFailSelector = @selector(searchDirectoryFailedFor:);
    [operation addObjectArgument:nameOrSciper];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getProfilePicture:(NSString *)sciper delegate:(id)delegate {
    if (![sciper isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad sciper" reason:@"sciper is either nil or not of class NSString" userInfo:nil];
    }
    ProfilePictureRequest* operation = [[ProfilePictureRequest alloc] initWithDelegate:delegate];
    operation.delegateDidReturnSelector = @selector(profilePictureFor:didReturn:);
    operation.delegateDidFailSelector = @selector(profilePictureFailedFor:);
    operation.sciper = sciper;
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
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}

@end


/*@implementation ProfilePictureRequest

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

@end*/

@implementation ProfilePictureRequest

static NSString* kProfilePictureURLbase = @"http://people.epfl.ch/cgi-bin/people/getPhoto?id=";

@synthesize sciper;

- (id)initWithSciper:(NSString*)sciper_ delegate:(id)delegate_ {
    if (![sciper_ isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad sciper argument" reason:@"sciper is not kind of class NSString" userInfo:nil];
    }
    self = [super initWithDelegate:delegate_];
    if (self) {
        self.sciper = sciper;
    }
    return self;
}

- (void)main {
    [self willChangeValueForKey:@"isExecuting"];
    executing = YES;
    [self didChangeValueForKey:@"isExecuting"];
    NSString* fullURLStringWithSciper = [NSString stringWithFormat:@"%@%@", kProfilePictureURLbase, self.sciper];
    ASIHTTPRequest* pictureRequest = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:fullURLStringWithSciper]];
    pictureRequest.timeOutSeconds = 10;
    pictureRequest.delegate = self;
    pictureRequest.downloadCache = [ASIDownloadCache sharedCache];
    pictureRequest.cachePolicy = ASIOnlyLoadIfNotCachedCachePolicy;
    pictureRequest.cacheStoragePolicy = ASICachePermanentlyCacheStoragePolicy;
    pictureRequest.secondsToCache = 86400; //seconds == 1 day. Should not cache an profile picture too long if it was changed
    pictureRequest.timeOutSeconds = [Service requestTimeoutInterval];
    [pictureRequest startAsynchronous];
}

- (void)requestFinished:(ASIHTTPRequest *)request {
    request.delegate = nil;
    if (request.responseData.length == 0) {
        [self requestFailed:request];
        return;
    }
    if ([self.delegate respondsToSelector:self.delegateDidReturnSelector]) {
        UIImage* image = [UIImage imageWithData:request.responseData];
        image = [[UIImage alloc] initWithCGImage:image.CGImage scale:1.0 orientation:UIImageOrientationUp]; //returning to be sure it's in portrait mode
        NSData* imageData = UIImageJPEGRepresentation(image, 1.0);
        [image release];
        [self.delegate performSelector:self.delegateDidReturnSelector withObject:self.sciper withObject:imageData];
    }
    [self finish];
}

- (void)requestFailed:(ASIHTTPRequest *)request {
    request.delegate = nil;
    if ([self.delegate respondsToSelector:self.delegateDidFailSelector]) {
        [self.delegate performSelector:self.delegateDidFailSelector withObject:self.sciper];
    }
    [self finish];
}

- (void)finish {
    [self willChangeValueForKey:@"isFinished"];
    [self willChangeValueForKey:@"isExecuting"];
    executing = NO;
    finished = YES;
    [self didChangeValueForKey:@"isExecuting"];
    [self didChangeValueForKey:@"isFinished"];
}

- (void)dealloc
{
    [sciper release];
    [super dealloc];
}

@end