//
//  Service.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <CommonCrypto/CommonDigest.h>

#import "ObjectArchiver.h"

#import "Service.h"

#import "Reachability.h"

#import "PCConfig.h"

static NSTimeInterval kThriftRequestTimeout = 75.0; //is the minimum for POST request prior the iOS 6. A timer with requestTimeoutInterval is used to remove this limitation and timeout () a request before the system API times out
static NSTimeInterval kRequestTimeout = 15.0; //is official timeout time for all ServiceRequest that do not have customTimeout specified
static NSTimeInterval kConnectivityCheckTimeout = 15.0;

@implementation Service

@synthesize operationQueue, thriftProtocol, serviceWillBeReleased;

- (id)initWithServiceName:(NSString*)serviceName_
{
    if (![serviceName_ isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"Bad serviceName" reason:@"serviceName argument is not of type NSString or is nil" userInfo:nil];
    }
    self = [super init];
    if (self) {
        serviceName = [serviceName_ retain];
        
        NSUserDefaults* defaults = [PCConfig defaults];

        NSString* serverProto = [defaults objectForKey:PC_CONFIG_SERVER_PROTOCOL_KEY];
        NSString* serverAddress = [defaults objectForKey:PC_CONFIG_SERVER_ADDRESS_KEY];
        NSString* serverPort = [defaults objectForKey:PC_CONFIG_SERVER_PORT_KEY];
        NSString* serverVersion = [defaults objectForKey:PC_CONFIG_SERVER_URI_KEY];
        
        
        serverAddressWithPort = [[NSString stringWithFormat:@"%@://%@:%@", serverProto, serverAddress, serverPort] retain];
        NSString* serviceURLString = [NSString stringWithFormat:@"%@/%@/%@", serverAddressWithPort, serverVersion, serviceName];
        NSLog(@"-> Initializing service '%@' on server (%@)", serviceName, serviceURLString);
        serverURL = [[NSURL URLWithString:serviceURLString] retain];
        THTTPClient* client = [[THTTPClient alloc] initWithURL:serverURL userAgent:nil timeout:10];
        thriftProtocol = [[TBinaryProtocol alloc] initWithTransport:client strictRead:YES strictWrite:YES];
        [client release];
        operationQueue = [[NSOperationQueue alloc] init];
        //[operationQueue setMaxConcurrentOperationCount:1];
        semaphore = dispatch_semaphore_create(0);
        checkServerRequest = nil;
        serverIsReachable = NO; //NO by default, before the check
        serviceWillBeReleased = NO;
    }
    return self;
}

+ (NSTimeInterval)requestTimeoutInterval {
    return kRequestTimeout;
}

- (BOOL)serverIsReachable {
    if (![[Reachability reachabilityForInternetConnection] isReachable]) { //check internet connection first
        return NO;
    }
    [self retain];
    @synchronized(self) {
        if (checkServerRequest == nil) {
            checkServerRequest = [[ASIHTTPRequest requestWithURL:[NSURL URLWithString:serverAddressWithPort]] retain];
            //NSLog(@"-> Checking server connectivity : %@", serverAddressWithPort);
            checkServerRequest.cachePolicy = NSURLRequestReloadIgnoringLocalAndRemoteCacheData;
            checkServerRequest.timeOutSeconds = kConnectivityCheckTimeout;
            //checkServerRequest.requestMethod = @"HEAD";
            checkServerRequest.delegate = self;
            checkServerRequest.didFinishSelector = @selector(checkServerRequestFinished:);
            checkServerRequest.didFailSelector = @selector(checkServerRequestFailed:);
            [checkServerRequest startAsynchronous];
        }
    }
    //NSLog(@"req : %@, cancelled : %d", checkServerRequest, checkServerRequest.isCancelled);
    dispatch_semaphore_wait(semaphore,  DISPATCH_TIME_FOREVER); //timeout is managed by request
    if (self.serviceWillBeReleased) {
        return NO;
    }
    @synchronized(self) {
        if (checkServerRequest != nil) {
            [checkServerRequest release];
            checkServerRequest = nil;
        }
    }
    [self release];
    return serverIsReachable;
}

- (NSString*) serviceName {
    return serviceName;
}

/* ASIHTTPRequestDelegate delegation */

- (void)checkServerRequestFinished:(ASIHTTPRequest *)request {
    if (self.serviceWillBeReleased) {
        return;
    }
    if (request.responseStatusCode == 404) { //correct. Means the server has responded
        serverIsReachable = YES;
    } else {
        NSLog(@"-> Server reachability test failed. Replied %d instead of 404. Returning timeout to delegate.", request.responseStatusCode);
        serverIsReachable = NO;
    }
    [self notifyAll];
}

- (void)checkServerRequestFailed:(ASIHTTPRequest *)request {
    if (self.serviceWillBeReleased) {
        return;
    }
    NSLog(@"-> Server reachability test failed. Returning timeout to delegate.");
    serverIsReachable = NO;
    [self notifyAll];
}

/* END OF ASIHTTPRequestDelegate delegation */

/* pass nil to cancel all operations
 * KNOW BUG : if checkServerRequest is still in progress, calling cancelOperationsForDelegate will cancel operations for ALL delegates !!
 */
- (void)cancelOperationsForDelegate:(id<ServiceDelegate>)delegate {
    int nbOps = 0;
    for (NSOperation* operation in operationQueue.operations) {        
        if (delegate == nil || ([operation respondsToSelector:@selector(delegate)])) {
            if ([(id)operation delegate] == delegate) {
                if ([operation respondsToSelector:@selector(setDelegate:)]) {
                    [(id)operation setDelegate:nil];
                }
                if ([operation respondsToSelector:@selector(setService:)]) {
                    [(id)operation setService:nil];
                }
                [operation cancel];
                nbOps++;
            } else {
                if ([operation isKindOfClass:[ServiceRequest class]]) {
                    [(ServiceRequest*)operation setShouldRestart:YES]; //the operation needs to recheck server availability because it
                }
            }
        }
    }
    if (nbOps > 0 && delegate) {
        NSLog(@"-> All operations canceled for delegate %@ (%d cancelled)", delegate, nbOps);
    }
    @synchronized(self) {
        if (checkServerRequest) {
            checkServerRequest.delegate = nil;
            [checkServerRequest cancel];
            checkServerRequest = nil;
        }
    }
    
    serverIsReachable = NO;
    [self notifyAll];
    
}

- (void)cancelAllOperations {
    [self cancelOperationsForDelegate:nil];
}

- (void)notifyAll {
    while (dispatch_semaphore_signal(semaphore) != 0); //notify all
    dispatch_semaphore_wait(semaphore, 0); //the while has incremented the counter one too much, so must decrement it once
    /*dispatch_release(semaphore);
    semaphore = nil;
    semaphore = dispatch_semaphore_create(0);*/
}

- (id)thriftProtocolInstance {
    THTTPClient* client = [[THTTPClient alloc] initWithURL:serverURL userAgent:nil timeout:kThriftRequestTimeout];
    TBinaryProtocol* thriftProtocol_ = [[TBinaryProtocol alloc] initWithTransport:client strictRead:YES strictWrite:YES];
    [client release];
    return [thriftProtocol_ autorelease];
}

- (void)dealloc
{
    self.serviceWillBeReleased = YES;
    [operationQueue setSuspended:YES];
    [self cancelAllOperations];
    if (checkServerRequest != nil) {
        checkServerRequest.delegate = nil;
        [checkServerRequest cancel];
        [checkServerRequest release];
        checkServerRequest = nil;
    }
    if (semaphore != nil) {
        dispatch_release(semaphore);
        semaphore = nil;
    }
    [operationQueue release];
    [thriftProtocol release];
    [serverAddressWithPort release];
    [serverURL release];
    NSLog(@"-> Service '%@' released. Some ServiceRequest may still be waiting for replies though. Those will be killed and released after thrift request timeout.", serviceName);
    [serviceName release];
    [super dealloc];
}

@end

/*----------------------------------------------------------------------------------------------*/

@implementation NSOperationWithDelegate

@synthesize delegate, delegateDidReturnSelector, delegateDidFailSelector;

- (id)initWithDelegate:(id)delegate_ {
    self = [super init];
    if (self) {
        self.delegate = delegate_;
    }
    return self;
}

- (BOOL)delegateRespondsToSelector:(SEL)selector {
    if ([self isCancelled]) {
        return NO;
    }
    if (self.delegate == nil) {
        return NO;
    }
    if (![self.delegate respondsToSelector:selector]) {
        return NO;
    }
    return YES;
}

- (BOOL)isCancelled {
    return canceled;
}

- (BOOL)isExecuting {
    return executing;
}

- (BOOL)isFinished {
    return finished;
}

@end

/*----------------------------------------------------------------------------------------------*/

@implementation ServiceRequest

@synthesize thriftServiceClient, timedOut, shouldRestart, serviceClientSelector, returnType, customTimeout, service, keepInCache, returnCacheIfServerIsUnreachable, skipCache, cacheValidity;

- (id)initWithThriftServiceClient:(id)serviceClient service:(Service*)service_ delegate:(id)delegate_
{
    self = [super initWithDelegate:delegate_];
    if (self) {
        self.thriftServiceClient = serviceClient;
        self.service = service_;
        self.timedOut = NO;
        self.shouldRestart = NO;
        self.serviceClientSelector = nil;
        arguments = [[NSMutableArray alloc] init];
        self.returnType = ReturnTypeNotSet;
        finished = NO;
        executing = NO;
        canceled = NO;
        customTimeout = 0.0;
        keepInCache = NO;
        returnCacheIfServerIsUnreachable = NO;
        skipCache = NO;
        cacheValidity = 100.0 * 365 * 24 * 60 * 60; // hundred years in seconds (equivalent to the old skipCache = NO)
        _nbTrimmedArgumentsFromLeftInDelegateCall = 0;
    }
    return self;
}

- (id)initForCachedResponseOnlyWithService:(Service*)service_ {
    self = [super init];
    if (self) {
        self.service = service_;
        arguments = [[NSMutableArray alloc] init];
        self.returnType = ReturnTypeNotSet;
    }
    return self;
}

- (void)checkPrimariesAndScheduleTimeoutTimer {
    if (self.thriftServiceClient == nil) {
        @throw [NSException exceptionWithName:@"Nil thriftServiceClient" reason:@"" userInfo:nil];
    }
    if (self.serviceClientSelector == nil || ![self.thriftServiceClient respondsToSelector:self.serviceClientSelector]) {
        @throw [NSException exceptionWithName:@"Nil serviceClientSelector or thriftServiceClient does not respond to it" reason:@"" userInfo:nil];
    }
    if (self.delegate == nil) {
        @throw [NSException exceptionWithName:@"Nil delegate" reason:@"request delegate cannot be nil" userInfo:nil];
    }
    if (![self delegateRespondsToSelector:self.delegateDidReturnSelector]) {
        @throw [NSException exceptionWithName:@"Bad delegate" reason:@"delegate does not respond to didReturn selector" userInfo:nil];
    }
    
    NSTimeInterval timeout = [self timeoutTime];
    dispatch_async(dispatch_get_main_queue(), ^{
        timeoutTimer = [[NSTimer scheduledTimerWithTimeInterval:timeout target:self selector:@selector(didTimeout) userInfo:nil repeats:NO] retain];
    });
}

- (NSTimeInterval)timeoutTime {
    if (customTimeout != 0.0) {
        return customTimeout;
    }
    return [Service requestTimeoutInterval];
}

- (id)cachedResponseObjectEvenIfStale:(BOOL)evenIfStale {
    if (self.returnType != ReturnTypeObject) {
        @throw [NSException exceptionWithName:@"unsupported operation" reason:@"cachedResponseObject is not supported when returnType is not ReturnTypeObject" userInfo:nil];
    }
    [self computeHashCode];
    
    NSDictionary* cached;
    if(evenIfStale) {
        cached = (NSDictionary*) [ObjectArchiver objectForKey:hashCode andPluginName:[service serviceName]];
    } else {
        cached = (NSDictionary*) [ObjectArchiver objectForKey:hashCode andPluginName:[service serviceName] nilIfDiffIntervalLargerThan:cacheValidity];
    }
    
    if ([[cached objectForKey:@"primitive"] boolValue]) {
        @throw [NSException exceptionWithName:@"unsupported operation" reason:@"cachedResponseObject is not supported when returnType is not ReturnTypeObject" userInfo:nil];
    }
    
    id object = [ServiceRequest unwrapArgument:cached];
    
    return object;
}

- (void)main {
    
    @try {
        [self computeHashCode];

        NSDictionary* cached = nil;
        
        if (!self.skipCache) {
            if (self.returnCacheIfServerIsUnreachable) {
                cached = (NSDictionary*)[ObjectArchiver objectForKey:hashCode andPluginName:[service serviceName]];
            } else {
                cached = (NSDictionary*) [ObjectArchiver objectForKey:hashCode andPluginName:[service serviceName] nilIfDiffIntervalLargerThan:cacheValidity];
            }
        }
        
        if(cached) {
            [self retain];
            NSLog(@"-> Will return ServiceRequest from cache.");
            
            NSInvocation* delegateInv = [[NSInvocation invocationWithMethodSignature:[[self.delegate class] instanceMethodSignatureForSelector:self.delegateDidReturnSelector]] retain];
            [delegateInv setSelector:self.delegateDidReturnSelector];
            [self setWrappedArgumentsForInvocation:delegateInv];
            
            if ([[cached objectForKey:@"primitive"] boolValue]) {
                void* result = [ServiceRequest unwrapArgument:cached];
                [delegateInv setArgument:result atIndex:arguments.count+2];
                dispatch_async(dispatch_get_main_queue(), ^{
                    if ([self delegateRespondsToSelector:self.delegateDidReturnSelector]) {
                        [delegateInv invokeWithTarget:self.delegate];
                    }
                    [delegateInv release];
                    free(result);
                    [self finishAndRelease];
                });
            } else {
                id object = [ServiceRequest unwrapArgument:cached];
                [object retain];
                [delegateInv setArgument:&object atIndex:arguments.count+2];
                dispatch_async(dispatch_get_main_queue(), ^{
                    if (self.delegate != nil && [self delegateRespondsToSelector:self.delegateDidReturnSelector]) {
                        [delegateInv invokeWithTarget:self.delegate];
                    }
                    [delegateInv release];
                    [object release];
                    [self finishAndRelease];
                });
            }
            
            return;
        }
        
        if (self.service != nil && ![self.service serverIsReachable]) {
            if (self.shouldRestart) {
                self.shouldRestart = NO;
                [self main];
                return;
            }
            NSLog(@"-> Server not reachable");
            [self didTimeout];
            return;
        }
        [self retain]; //So that the NSOperation is kept alive to receive service timeout (POST timeout) even after service release
        
        if ([self isCancelled])
        {
            @throw [NSException exceptionWithName:@"Operation cancelled" reason:@"operation was cancelled" userInfo:nil];
        }
        
        [self willChangeValueForKey:@"isExecuting"];
        executing = YES;
        [self didChangeValueForKey:@"isExecuting"];
        
        [self checkPrimariesAndScheduleTimeoutTimer];
        
        NSInvocation* serviceInv = [NSInvocation invocationWithMethodSignature:[[self.thriftServiceClient class] instanceMethodSignatureForSelector:self.serviceClientSelector]];
        [serviceInv setSelector:self.serviceClientSelector]; //must also be set
        [self setWrappedArgumentsForInvocation:serviceInv];
        [serviceInv invokeWithTarget:self.thriftServiceClient];
        if ([self isCancelled])
        {
            @throw [NSException exceptionWithName:@"Operation cancelled" reason:@"operation was cancelled" userInfo:nil];
        }
        
        NSInvocation* delegateInv = [[NSInvocation invocationWithMethodSignature:[[self.delegate class] instanceMethodSignatureForSelector:self.delegateDidReturnSelector]] retain];
        [delegateInv setSelector:self.delegateDidReturnSelector];
        [self setWrappedArgumentsForInvocation:delegateInv];
        
        if ([self isCancelled])
        {
            @throw [NSException exceptionWithName:@"Operation cancelled" reason:@"operation was cancelled" userInfo:nil];
        }
        
        switch (returnType) {
            case ReturnTypeObject:
                {
                    id object;
                    [serviceInv getReturnValue:&object];
                    [object retain];
                    if(keepInCache) [ObjectArchiver saveObject:[ServiceRequest wrapArgumentObject:object] forKey:hashCode andPluginName:[service serviceName]];
                    [delegateInv setArgument:&object atIndex:arguments.count+2];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        if (self.delegate != nil && [self delegateRespondsToSelector:self.delegateDidReturnSelector]) {
                            [delegateInv invokeWithTarget:self.delegate];
                        }
                        [delegateInv release];
                        [object release];
                        [self finishAndRelease];
                    });
                }
                break;
            case ReturnTypeInt:
                {
                    int* result = malloc(sizeof(int));
                    [serviceInv getReturnValue:result];
                    if(keepInCache) [ObjectArchiver saveObject:[ServiceRequest wrapArgumentInt:*result] forKey:hashCode andPluginName:[service serviceName]];
                    [delegateInv setArgument:result atIndex:arguments.count+2];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        if ([self delegateRespondsToSelector:self.delegateDidReturnSelector]) {
                            [delegateInv invokeWithTarget:self.delegate];
                        }
                        [delegateInv release];
                        free(result);
                        [self finishAndRelease];
                    });
                }
                break;
            case ReturnTypeDouble:
                {
                    double* result = malloc(sizeof(double));
                    [serviceInv getReturnValue:result];
                    if(keepInCache) [ObjectArchiver saveObject:[ServiceRequest wrapArgumentDouble:*result] forKey:hashCode andPluginName:[service serviceName]];
                    [delegateInv setArgument:result atIndex:arguments.count+2];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        if ([self delegateRespondsToSelector:self.delegateDidReturnSelector]) {
                            [delegateInv invokeWithTarget:self.delegate];
                        }
                        [delegateInv release];
                        free(result);
                        [self finishAndRelease];
                    });
                }
                break;
            case ReturnTypeFloat:
                {
                    float* result = malloc(sizeof(float));
                    [serviceInv getReturnValue:result];
                    if(keepInCache) [ObjectArchiver saveObject:[ServiceRequest wrapArgumentFloat:*result] forKey:hashCode andPluginName:[service serviceName]];
                    [delegateInv setArgument:result atIndex:arguments.count+2];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        if ([self delegateRespondsToSelector:self.delegateDidReturnSelector]) {
                            [delegateInv invokeWithTarget:self.delegate];
                        }
                        [delegateInv release];
                        free(result);
                        [self finishAndRelease];
                    });
                }
                break;
            case ReturnTypeBool:
                {
                    BOOL* result = malloc(sizeof(BOOL));
                    [serviceInv getReturnValue:result];
                    if(keepInCache) [ObjectArchiver saveObject:[ServiceRequest wrapArgumentBool:*result] forKey:hashCode andPluginName:[service serviceName]];
                    [delegateInv setArgument:result atIndex:arguments.count+2];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        if ([self delegateRespondsToSelector:self.delegateDidReturnSelector]) {
                            [delegateInv invokeWithTarget:self.delegate];
                        }
                        [delegateInv release];
                        free(result);
                        [self finishAndRelease];
                    });
                }
                break;
            case ReturnTypeChar:
                {
                    char* result = malloc(sizeof(char));
                    [serviceInv getReturnValue:result];
                    if(keepInCache) [ObjectArchiver saveObject:[ServiceRequest wrapArgumentChar:*result] forKey:hashCode andPluginName:[service serviceName]];
                    [delegateInv setArgument:result atIndex:arguments.count+2];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        if ([self delegateRespondsToSelector:self.delegateDidReturnSelector]) {
                            [delegateInv invokeWithTarget:self.delegate];
                        }
                        [delegateInv release];
                        free(result);
                        [self finishAndRelease];
                    });
                }
                break;
            case ReturnTypeLong:
                {
                    long* result = malloc(sizeof(long));
                    [serviceInv getReturnValue:result];
                    if(keepInCache) [ObjectArchiver saveObject:[ServiceRequest wrapArgumentLong:*result] forKey:hashCode andPluginName:[service serviceName]];
                    [delegateInv setArgument:result atIndex:arguments.count+2];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        if ([self delegateRespondsToSelector:self.delegateDidReturnSelector]) {
                            [delegateInv invokeWithTarget:self.delegate];
                        }
                        [delegateInv release];
                        free(result);
                        [self finishAndRelease];
                    });
                }
                break;
            case ReturnTypeLongLong:
                {
                    long long* result = malloc(sizeof(long long));
                    [serviceInv getReturnValue:result];
                    if(keepInCache) [ObjectArchiver saveObject:[ServiceRequest wrapArgumentLongLong:*result] forKey:hashCode andPluginName:[service serviceName]];
                    [delegateInv setArgument:result atIndex:arguments.count+2];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        if ([self delegateRespondsToSelector:self.delegateDidReturnSelector]) {
                            [delegateInv invokeWithTarget:self.delegate];
                        }
                        [delegateInv release];
                        free(result);
                        [self finishAndRelease];
                    });
                }
                break;
            case ReturnTypeShort:
                {
                    short* result = malloc(sizeof(short));
                    [serviceInv getReturnValue:result];
                    if(keepInCache) [ObjectArchiver saveObject:[ServiceRequest wrapArgumentShort:*result] forKey:hashCode andPluginName:[service serviceName]];
                    [delegateInv setArgument:result atIndex:arguments.count+2];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        if ([self delegateRespondsToSelector:self.delegateDidReturnSelector]) {
                            [delegateInv invokeWithTarget:self.delegate];
                        }
                        [delegateInv release];
                        free(result);
                        [self finishAndRelease];
                    });
                }
                break;
            case ReturnTypeUnsignedInt:
                {
                    unsigned int* result = malloc(sizeof(unsigned int));
                    [serviceInv getReturnValue:result];
                    if(keepInCache) [ObjectArchiver saveObject:[ServiceRequest wrapArgumentUnsignedInt:*result] forKey:hashCode andPluginName:[service serviceName]];
                    [delegateInv setArgument:result atIndex:arguments.count+2];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        if ([self delegateRespondsToSelector:self.delegateDidReturnSelector]) {
                            [delegateInv invokeWithTarget:self.delegate];
                        }
                        [delegateInv release];
                        free(result);
                        [self finishAndRelease];
                    });
                }
                break;
            case ReturnTypeUnsignedChar:
                {
                    unsigned char* result = malloc(sizeof(unsigned char));
                    [serviceInv getReturnValue:result];
                    if(keepInCache) [ObjectArchiver saveObject:[ServiceRequest wrapArgumentUnsignedChar:*result] forKey:hashCode andPluginName:[service serviceName]];
                    [delegateInv setArgument:result atIndex:arguments.count+2];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        if ([self delegateRespondsToSelector:self.delegateDidReturnSelector]) {
                            [delegateInv invokeWithTarget:self.delegate];
                        }
                        [delegateInv release];
                        free(result);
                        [self finishAndRelease];
                    });
                }
                break;
            case ReturnTypeUnsignedLong:
                {
                    unsigned long* result = malloc(sizeof(unsigned long));
                    [serviceInv getReturnValue:result];
                    if(keepInCache) [ObjectArchiver saveObject:[ServiceRequest wrapArgumentUnsignedLong:*result] forKey:hashCode andPluginName:[service serviceName]];
                    [delegateInv setArgument:result atIndex:arguments.count+2];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        if ([self delegateRespondsToSelector:self.delegateDidReturnSelector]) {
                            [delegateInv invokeWithTarget:self.delegate];
                        }
                        [delegateInv release];
                        free(result);
                        [self finishAndRelease];
                    });
                }
                break;
            case ReturnTypeUnsignedLongLong:
                {
                    unsigned long long* result = malloc(sizeof(unsigned long long));
                    [serviceInv getReturnValue:result];
                    if(keepInCache) [ObjectArchiver saveObject:[ServiceRequest wrapArgumentUnsignedLongLong:*result] forKey:hashCode andPluginName:[service serviceName]];
                    [delegateInv setArgument:result atIndex:arguments.count+2];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        if ([self delegateRespondsToSelector:self.delegateDidReturnSelector]) {
                            [delegateInv invokeWithTarget:self.delegate];
                        }
                        [delegateInv release];
                        free(result);
                        [self finishAndRelease];
                    });
                }
                break;
            case ReturnTypeUnsignedShort:
                {
                    unsigned short* result = malloc(sizeof(unsigned short));
                    [serviceInv getReturnValue:result];
                    if(keepInCache) [ObjectArchiver saveObject:[ServiceRequest wrapArgumentUnsignedShort:*result] forKey:hashCode andPluginName:[service serviceName]];
                    [delegateInv setArgument:result atIndex:arguments.count+2];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        if ([self delegateRespondsToSelector:self.delegateDidReturnSelector]) {
                            [delegateInv invokeWithTarget:self.delegate];
                        }
                        [delegateInv release];
                        free(result);
                        [self finishAndRelease];
                    });
                }
                break;
            default:
                @throw [NSException exceptionWithName:@"Unsupported return type" reason:@"Specified return type is not supported" userInfo:nil];
                break;
        }
        
    }
    @catch (NSException *exception) {
        NSLog(@"-> ServiceRequest EXCEPTION caught in main : %@, %@", exception.name, exception.reason);
        @try {
            
            if (self.timedOut) {
                @throw [NSException exceptionWithName:@"-> Service request returned after timeout" reason:@"Thrift timeout is longer than ServiceRequest timeout." userInfo:nil];
            }
            
            if ([self isCancelled]) {
                @throw [NSException exceptionWithName:@"-> Cancelled operation handled" reason:@"" userInfo:nil];
            }
            
            if (![self delegateRespondsToSelector:self.delegateDidFailSelector]) {
                @throw [NSException exceptionWithName:@"-> Bad delegate response" reason:@"Delegate does not respond to didFail selector. Ignoring" userInfo:nil];
            }
            
            NSInvocation* failInv = [[NSInvocation invocationWithMethodSignature:[[self.delegate class] instanceMethodSignatureForSelector:self.delegateDidFailSelector]] retain];
            [failInv setSelector:self.delegateDidFailSelector];
            [self setWrappedArgumentsForInvocation:failInv];
            
            dispatch_async(dispatch_get_main_queue(), ^{
                if ([self delegateRespondsToSelector:self.delegateDidFailSelector]) {
                    [failInv invokeWithTarget:self.delegate];
                }
                [failInv release];
                [self finishAndRelease];
            });
        }
        @catch (NSException *exception) {
            NSLog(@"%@ %@", exception.name, exception.reason);
            [self finishAndRelease];
        }
        
    }
    
}


// all below are deprecated

- (void)addObjectArgument:(id)object {
    [self addWrappedArgument:[ServiceRequest wrapArgumentObject:object]];
}

- (void)addBoolArgument:(BOOL)val {
    [self addWrappedArgument:[ServiceRequest wrapArgumentBool:val]];
}

- (void)addCharArgument:(char)val {
    [self addWrappedArgument:[ServiceRequest wrapArgumentChar:val]];
}

- (void)addUnsignedCharArgument:(unsigned char)val {
    [self addWrappedArgument:[ServiceRequest wrapArgumentUnsignedChar:val]];
}

- (void)addDoubleArgument:(double)val {
    [self addWrappedArgument:[ServiceRequest wrapArgumentDouble:val]];
}

- (void)addFloatArgument:(float)val {
    [self addWrappedArgument:[ServiceRequest wrapArgumentFloat:val]];
}

- (void)addIntArgument:(int)val {
    [self addWrappedArgument:[ServiceRequest wrapArgumentInt:val]];
}

- (void)addUnsignedIntArgument:(unsigned int)val {
    [self addWrappedArgument:[ServiceRequest wrapArgumentUnsignedInt:val]];
}

- (void)addLongArgument:(long)val {
    [self addWrappedArgument:[ServiceRequest wrapArgumentLong:val]];
}

- (void)addUnsignedLongArgument:(unsigned long) val {
    [self addWrappedArgument:[ServiceRequest wrapArgumentUnsignedLong:val]];
}

- (void)addLongLongArgument:(long long)val {
    [self addWrappedArgument:[ServiceRequest wrapArgumentLongLong:val]];
}

- (void)addUnsignedLongLongArgument:(unsigned long long)val {
    [self addWrappedArgument:[ServiceRequest wrapArgumentUnsignedLongLong:val]];
}

- (void)addShortArgument:(short)val {
    [self addWrappedArgument:[ServiceRequest wrapArgumentShort:val]];
}

- (void)addUnsignedShortArgument:(unsigned short)val {
    [self addWrappedArgument:[ServiceRequest wrapArgumentUnsignedShort:val]];
}



//////////////////
//////////////////

- (void)addWrappedArgument:(NSDictionary*)argDic {
    [arguments addObject:argDic];
}

+ (NSDictionary*)wrapArgumentObject:(id)object {
    //OK, object not checked => can be nil
    NSMutableDictionary* argDic = [NSMutableDictionary dictionary];
    [argDic setObject:[NSNumber numberWithBool:NO] forKey:@"primitive"];
    [argDic setObject:object forKey:@"value"];
    return argDic;
}

+ (NSDictionary*)wrapArgumentBool:(BOOL)val {
    NSMutableDictionary* argDic = [NSMutableDictionary dictionary];
    [argDic setObject:[NSNumber numberWithBool:YES] forKey:@"primitive"];
    [argDic setObject:[NSNumber numberWithBool:val] forKey:@"value"];
    [argDic setObject:NSStringFromSelector(@selector(boolValue)) forKey:@"nsNumberSelectorString"];
    return argDic;
}

+ (NSDictionary*)wrapArgumentChar:(char)val {
    NSMutableDictionary* argDic = [NSMutableDictionary dictionary];
    [argDic setObject:[NSNumber numberWithBool:YES] forKey:@"primitive"];
    [argDic setObject:[NSNumber numberWithChar:val] forKey:@"value"];
    [argDic setObject:NSStringFromSelector(@selector(charValue)) forKey:@"nsNumberSelectorString"];
    return argDic;
}

+ (NSDictionary*)wrapArgumentUnsignedChar:(unsigned char)val {
    NSMutableDictionary* argDic = [NSMutableDictionary dictionary];
    [argDic setObject:[NSNumber numberWithBool:YES] forKey:@"primitive"];
    [argDic setObject:[NSNumber numberWithUnsignedChar:val] forKey:@"value"];
    [argDic setObject:NSStringFromSelector(@selector(unsignedCharValue)) forKey:@"nsNumberSelectorString"];
    return argDic;
}

+ (NSDictionary*)wrapArgumentDouble:(double)val {
    NSMutableDictionary* argDic = [NSMutableDictionary dictionary];
    [argDic setObject:[NSNumber numberWithBool:YES] forKey:@"primitive"];
    [argDic setObject:[NSNumber numberWithDouble:val] forKey:@"value"];
    [argDic setObject:NSStringFromSelector(@selector(doubleValue)) forKey:@"nsNumberSelectorString"];
    return argDic;
}

+ (NSDictionary*)wrapArgumentFloat:(float)val {
    NSMutableDictionary* argDic = [NSMutableDictionary dictionary];
    [argDic setObject:[NSNumber numberWithBool:YES] forKey:@"primitive"];
    [argDic setObject:[NSNumber numberWithFloat:val] forKey:@"value"];
    [argDic setObject:NSStringFromSelector(@selector(floatValue)) forKey:@"nsNumberSelectorString"];
    return argDic;
}

+ (NSDictionary*)wrapArgumentInt:(int)val {
    NSMutableDictionary* argDic = [NSMutableDictionary dictionary];
    [argDic setObject:[NSNumber numberWithBool:YES] forKey:@"primitive"];
    [argDic setObject:[NSNumber numberWithInt:val] forKey:@"value"];
    [argDic setObject:NSStringFromSelector(@selector(intValue)) forKey:@"nsNumberSelectorString"];
    return argDic;
}

+ (NSDictionary*)wrapArgumentUnsignedInt:(unsigned int)val {
    NSMutableDictionary* argDic = [NSMutableDictionary dictionary];
    [argDic setObject:[NSNumber numberWithBool:YES] forKey:@"primitive"];
    [argDic setObject:[NSNumber numberWithUnsignedInt:val] forKey:@"value"];
    [argDic setObject:NSStringFromSelector(@selector(unsignedIntValue)) forKey:@"nsNumberSelectorString"];
    return argDic;
}

+ (NSDictionary*)wrapArgumentLong:(long)val {
    NSMutableDictionary* argDic = [NSMutableDictionary dictionary];
    [argDic setObject:[NSNumber numberWithBool:YES] forKey:@"primitive"];
    [argDic setObject:[NSNumber numberWithLong:val] forKey:@"value"];
    [argDic setObject:NSStringFromSelector(@selector(longValue)) forKey:@"nsNumberSelectorString"];
    return argDic;
}

+ (NSDictionary*)wrapArgumentUnsignedLong:(unsigned long)val {
    NSMutableDictionary* argDic = [NSMutableDictionary dictionary];
    [argDic setObject:[NSNumber numberWithBool:YES] forKey:@"primitive"];
    [argDic setObject:[NSNumber numberWithUnsignedLong:val] forKey:@"value"];
    [argDic setObject:NSStringFromSelector(@selector(unsignedLongValue)) forKey:@"nsNumberSelectorString"];
    return argDic;
}

+ (NSDictionary*)wrapArgumentLongLong:(long long)val {
    NSMutableDictionary* argDic = [NSMutableDictionary dictionary];
    [argDic setObject:[NSNumber numberWithBool:YES] forKey:@"primitive"];
    [argDic setObject:[NSNumber numberWithLongLong:val] forKey:@"value"];
    [argDic setObject:NSStringFromSelector(@selector(longLongValue)) forKey:@"nsNumberSelectorString"];
    return argDic;
}

+ (NSDictionary*)wrapArgumentUnsignedLongLong:(unsigned long long)val {
    NSMutableDictionary* argDic = [NSMutableDictionary dictionary];
    [argDic setObject:[NSNumber numberWithBool:YES] forKey:@"primitive"];
    [argDic setObject:[NSNumber numberWithUnsignedLongLong:val] forKey:@"value"];
    [argDic setObject:NSStringFromSelector(@selector(unsignedLongLongValue)) forKey:@"nsNumberSelectorString"];
    return argDic;
}

+ (NSDictionary*)wrapArgumentShort:(short)val {
    NSMutableDictionary* argDic = [NSMutableDictionary dictionary];
    [argDic setObject:[NSNumber numberWithBool:YES] forKey:@"primitive"];
    [argDic setObject:[NSNumber numberWithShort:val] forKey:@"value"];
    [argDic setObject:NSStringFromSelector(@selector(shortValue)) forKey:@"nsNumberSelectorString"];
    return argDic;
}

+ (NSDictionary*)wrapArgumentUnsignedShort:(unsigned short)val {
    NSMutableDictionary* argDic = [NSMutableDictionary dictionary];
    [argDic setObject:[NSNumber numberWithBool:YES] forKey:@"primitive"];
    [argDic setObject:[NSNumber numberWithUnsignedShort:val] forKey:@"value"];
    [argDic setObject:NSStringFromSelector(@selector(unsignedShortValue)) forKey:@"nsNumberSelectorString"];
    return argDic;
}

+ (void*)unwrapArgument:(NSDictionary*)argDic {
    if (![[argDic objectForKey:@"primitive"] boolValue])
        return [argDic objectForKey:@"value"];
    
    NSNumber* val = [argDic objectForKey:@"value"];
    NSString* selectorString = [argDic objectForKey:@"nsNumberSelectorString"];
    SEL selector = NSSelectorFromString(selectorString);
    
    NSInvocation* inv = [NSInvocation invocationWithMethodSignature:[NSNumber instanceMethodSignatureForSelector:selector]];
    [inv setSelector:selector];
    [inv invokeWithTarget:val];
    
    void* ret = malloc(8);
    [inv getReturnValue:ret];
    return ret;

}

- (void)computeHashCode {
    hashCode = [ServiceRequest md5HexDigest:NSStringFromSelector(serviceClientSelector)];
    for (int i = 0; i < arguments.count; i++) {
        hashCode = [ServiceRequest md5HexDigest:[NSString stringWithFormat:@"%@%@", hashCode, [[arguments objectAtIndex:i] objectForKey:@"value"]]];
    }
    //NSLog(@"-> Hash computed for request %@ %@",NSStringFromSelector(self.serviceClientSelector), hashCode);
}

+ (NSString*)md5HexDigest:(NSString*)input {
    const char* str = [input UTF8String];
    unsigned char result[CC_MD5_DIGEST_LENGTH];
    CC_MD5(str, strlen(str), result);
    
    NSMutableString* ret = [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH*2];
    for(int i = 0; i<CC_MD5_DIGEST_LENGTH; i++) {
        [ret appendFormat:@"%02x",result[i]];
    }
    return ret;
}

- (void)setWrappedArgumentsForInvocation:(NSInvocation*)inv {
    /*NSMethodSignature* methodSignature = [[[NSMethodSignature alloc] init] methodSignatureForSelector:inv.selector];
    NSUInteger invNbArguments = [methodSignature numberOfArguments];*/
    for (int i = 0; i < arguments.count; i++) {
        NSDictionary* argDic = [arguments objectAtIndex:i];
        if ([[argDic objectForKey:@"primitive"] boolValue]) {
            void* ptr = [ServiceRequest unwrapArgument:argDic];
            [inv setArgument:ptr atIndex:i+2];
            free(ptr);
        } else {
            id object = [ServiceRequest unwrapArgument:argDic];
            [inv setArgument:&object atIndex:i+2];
        }
    }
}

//override
- (BOOL)isConcurrent {
    return NO;
}

//override
- (void)cancel {
    self.delegate = nil;
    [super cancel];
    [self willChangeValueForKey:@"isFinished"];
    [self willChangeValueForKey:@"isExecuting"];
    [self willChangeValueForKey:@"isCanceled"];
    executing = NO;
    finished = YES;
    canceled = YES;
    [self didChangeValueForKey:@"isCanceled"];
    [self didChangeValueForKey:@"isExecuting"];
    [self didChangeValueForKey:@"isFinished"];
}

//WARNING : release only when thrift has returned
- (void)finishAndRelease {
    if (timeoutTimer != nil) {
        [timeoutTimer invalidate];
        [timeoutTimer release];
    }
    [self willChangeValueForKey:@"isFinished"];
    [self willChangeValueForKey:@"isExecuting"];
    executing = NO;
    finished = YES;
    [self didChangeValueForKey:@"isExecuting"];
    [self didChangeValueForKey:@"isFinished"];
    [self autorelease];
}

- (void)didTimeout { //server not reachable or thrift request timer timed out
    NSLog(@"-> ServiceRequest timeout");
    if (self.timedOut) { 
        return;
    }
    self.timedOut = YES;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self delegateRespondsToSelector:@selector(serviceConnectionToServerTimedOut)]) {
            [self.delegate serviceConnectionToServerTimedOut];
        }
        [self cancel];
    });
}

- (void)dealloc
{
    [thriftServiceClient release];
    [arguments release];
    [super dealloc];
}

@end
