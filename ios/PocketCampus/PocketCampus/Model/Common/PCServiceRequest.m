/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

//  Created by Loïc Gardiol on 08.12.13.

#import "PCServiceRequest.h"

#import "NSOperationWithDelegate_Protected.h"

#import "PCService.h"

#import "TTransportException.h"

#import <float.h>

#import <CommonCrypto/CommonDigest.h>

@interface PCServiceRequest ()

@property (nonatomic, strong) NSMutableArray* arguments;
@property (nonatomic, readwrite, strong) id thriftServiceClient;
@property (nonatomic, readwrite, weak) PCService* service;

@property (nonatomic, strong) NSString* hashIdentifier;

@end

@implementation PCServiceRequest

#pragma mark - Init

- (id)initWithThriftServiceClient:(id)serviceClient service:(PCService*)service delegate:(id)delegate
{
    self = [super initWithDelegate:delegate];
    if (self) {
        self.thriftServiceClient = serviceClient;
        self.service = service;
        self.arguments = [NSMutableArray array];
        self.cacheValidityInterval = 100.0 * 365 * 24 * 60 * 60; // hundred years in seconds (equivalent to the old skipCache = NO)
    }
    return self;
}

- (id)initForCachedResponseOnlyWithService:(PCService*)service {
    self = [super init];
    if (self) {
        self.service = service;
        self.arguments = [NSMutableArray array];
    }
    return self;
}

#pragma mark - Identification and description

- (NSString*)hashIdentifier {
    if (!_hashIdentifier) {
        NSString* tempHashIdentifier = [PCServiceRequest md5HexDigestForString:NSStringFromSelector(self.serviceClientSelector)];
        for (NSDictionary* argDic in self.arguments) {
            tempHashIdentifier =  [PCServiceRequest md5HexDigestForString:[NSString stringWithFormat:@"%@%@", tempHashIdentifier, argDic[kWrappedElementDictValueKey]]];
        }
        _hashIdentifier = tempHashIdentifier;
    }
    return _hashIdentifier;
}

- (NSString*)description {
    return [NSString stringWithFormat:@"<ServiceRequest %p (%@ - %@)>", self, self.service.serviceName, NSStringFromSelector(self.serviceClientSelector)];
}

static inline void ServiceRequestLog(PCServiceRequest* serviceRequest, NSString* format, ...) {
    va_list args;
    va_start(args, format);
    NSString* message = [[NSString alloc] initWithFormat:format arguments:args];
    CLSNSLog(@"%@ %@", serviceRequest.description, message);
}

#pragma mark - Cached response only

- (id)cachedResponseObjectEvenIfStale:(BOOL)evenIfStale {
    if (self.returnType != ReturnTypeObject) {
        ServiceRequestLog(self, @"WARNING: Unsupported operation, cachedResponseObjectEvenIfStale is not supported when returnType is not ReturnTypeObject. Returning nil.");
        return nil;
    }
    
    NSDictionary* cachedArgDic;
    if(evenIfStale) {
        cachedArgDic = (NSDictionary*) [PCPersistenceManager objectForKey:self.hashIdentifier pluginName:self.service.serviceName isCache:YES];
    } else {
        cachedArgDic = (NSDictionary*) [PCPersistenceManager objectForKey:self.hashIdentifier pluginName:self.service.serviceName nilIfDiffIntervalLargerThan:self.cacheValidityInterval isCache:YES];
    }
    
    if ([cachedArgDic[kWrappedElementDictPrimitiveKey] boolValue]) {
        ServiceRequestLog(self, @"WARNING: Unsupported operation, cachedResponseObjectEvenIfStale is not supported when returnType is not ReturnTypeObject. Returning nil.");
        return nil;
    }
    
    id object = [PCServiceRequest unwrapArgument:cachedArgDic];
    
    return object;
}

#pragma mark - Main

- (void)throwIfBadState {
    if (!self.thriftServiceClient) {
        [NSException raise:@"Illegal state" format:@"thriftServiceClient cannot be nil"];
    }
    if (!self.serviceClientSelector || ![self.thriftServiceClient respondsToSelector:self.serviceClientSelector]) {
        [NSException raise:@"Illegal state" format:@"thristServiceClient does not respond to serviceClientSelector (%@)", NSStringFromSelector(self.serviceClientSelector)];
    }
    if (!self.delegate) {
        [NSException raise:@"Illegal state" format:@"delegate cannot be nil"];
    }
    if (![self.delegate respondsToSelector:self.delegateDidReturnSelector]) {
        [NSException raise:@"Bad delegate" format:@"delegate does not respond to didReturn selector"];
    }
}

- (void)throwIfCancelled {
    if ([self isCancelled]) {
        [NSException raise:@"Operation cancelled" format:nil];
    }
}

- (void)main {
    
    self.executing = YES;
    @try {
        [self throwIfCancelled];
        [self throwIfBadState];
        
        //Checking if should try to return from cache
        if (self.keepInCache && !self.skipCache) {
            NSDictionary* cachedResponseDic = nil;
            if (self.returnEvenStaleCacheIfNoInternetConnection && ![PCUtils hasDeviceInternetConnection]) {
                cachedResponseDic = (NSDictionary*)[PCPersistenceManager objectForKey:self.hashIdentifier pluginName:self.service.serviceName isCache:YES];
            } else {
                cachedResponseDic = (NSDictionary*) [PCPersistenceManager objectForKey:self.hashIdentifier pluginName:self.service.serviceName nilIfDiffIntervalLargerThan:self.cacheValidityInterval isCache:YES];
            }
            if (cachedResponseDic) {
                ServiceRequestLog(self, @"will return from cache.");
                if([self returnToDelegateWithCachedResponseDic:cachedResponseDic completion:^{
                    [self finish];
                }]) {
                    return;
                }
            }
        }
        
        [self throwIfCancelled];
        
        if (![PCUtils hasDeviceInternetConnection]) {
            ServiceRequestLog(self, @"no internet connection. Stopping request and indicating connection error to delegate.");
            [self indicateConnectionErrorToDelegateCompletion:^{
                [self finish];
            }];
            return;
        }
        
        [self startRequestAndReturnToDelegateCompletion:^{
            [self finish];
        }];
        
    }
    @catch (NSException *exception) {
        
        if ([self isCancelled]) {
            ServiceRequestLog(self, @"cancelled operation handled.");
            [self finish];
            return;
        }
        
        TTransportException* texception = [exception isKindOfClass:TTransportException.class] ? (TTransportException*)exception : nil;
        if (!texception) {
            ServiceRequestLog(self, @"EXCEPTION caught in main : %@, %@", exception.name, exception.reason);
            [self indicateFailureToDelegateCompletion:^{
                [self finish];
            }];
            return;
        }
        
        NSError* error = texception.userInfo[@"error"];
        
        // Checking if server could not be reached
        // See http://stackoverflow.com/a/10644008
        NSInteger errorCode = error.code;
        if (   errorCode == NSURLErrorCannotFindHost
            || errorCode == NSURLErrorTimedOut
            || errorCode == NSURLErrorCannotConnectToHost
            || errorCode == NSURLErrorNetworkConnectionLost
            || errorCode == NSURLErrorDNSLookupFailed
            || errorCode == NSURLErrorResourceUnavailable
            || errorCode == NSURLErrorNotConnectedToInternet
            || errorCode == NSURLErrorRedirectToNonExistentLocation
            || errorCode == NSURLErrorInternationalRoamingOff
            || errorCode == NSURLErrorCallIsActive
            || errorCode == NSURLErrorDataNotAllowed
            || errorCode == NSURLErrorSecureConnectionFailed
            || errorCode == NSURLErrorCannotLoadFromNetwork
            )
        {
            ServiceRequestLog(self, @"connection to server failed.");
            [self indicateConnectionErrorToDelegateCompletion:^{
                [self finish];
            }];
            return;
        }
        
        ServiceRequestLog(self, @"other error: %@ Thrift exception: %@", error, texception);
        [self indicateFailureToDelegateCompletion:^{
            [self finish];
        }];
    }
}

//returns NO if there is a mismatch between reponseDic and expected return type, in which case completion is not executed
//returns YES otherwise
- (BOOL)returnToDelegateWithCachedResponseDic:(NSDictionary*)responseDic completion:(VoidBlock)completion {
    [PCUtils throwExceptionIfObject:responseDic notKindOfClass:[NSDictionary class]];
    NSInvocation* delegateInv = [NSInvocation invocationWithMethodSignature:[[self.delegate class] instanceMethodSignatureForSelector:self.delegateDidReturnSelector]];
    [delegateInv setSelector:self.delegateDidReturnSelector];
    [self setWrappedArgumentsForInvocation:delegateInv];
    
    BOOL primitive = [responseDic[kWrappedElementDictPrimitiveKey] boolValue];
    if (primitive && self.returnType == ReturnTypeObject) {
        return NO;
    }
    if (primitive) {
        void* result = [self.class unwrapArgument:responseDic];
        [delegateInv setArgument:result atIndex:self.arguments.count+2];
    } else {
        id object = [PCServiceRequest unwrapArgument:responseDic];
        [delegateInv setArgument:&object atIndex:self.arguments.count+2];
    }
    dispatch_sync(dispatch_get_main_queue(), ^{
        if (self.delegate) {
            [delegateInv invokeWithTarget:self.delegate];
        }
    });
    if (completion) {
        completion();
    }
    return YES;
}

- (void)startRequestAndReturnToDelegateCompletion:(VoidBlock)completion {
    NSInvocation* serviceInv = [NSInvocation invocationWithMethodSignature:[[self.thriftServiceClient class] instanceMethodSignatureForSelector:self.serviceClientSelector]];
    [serviceInv setSelector:self.serviceClientSelector]; //must also be set
    [self setWrappedArgumentsForInvocation:serviceInv];
    //self.serviceInv = serviceInv;
    // SYNCHRONOUS (BLOCKING) CALL ON THRIFT CLIENT
    [serviceInv invokeWithTarget:self.thriftServiceClient];
    
    
    NSDictionary* responseDic = nil;
    __unsafe_unretained id resultObj = nil; //see http://stackoverflow.com/a/11874258/1423774
    void* resultPrim = NULL;
    switch (self.returnType) {
        case ReturnTypeObject:
        {
            [serviceInv getReturnValue:&resultObj];
            responseDic = [self.class wrapArgumentObject:resultObj];
            break;
        }
        case ReturnTypeInt:
        {
            int* result = malloc(sizeof(int));
            [serviceInv getReturnValue:result];
            responseDic = [self.class wrapArgumentInt:*result];
            resultPrim = result;
            break;
        }
        case ReturnTypeDouble:
        {
            double* result = malloc(sizeof(double));
            [serviceInv getReturnValue:result];
            responseDic = [self.class wrapArgumentDouble:*result];
            resultPrim = result;
            break;
        }
        case ReturnTypeFloat:
        {
            float* result = malloc(sizeof(float));
            [serviceInv getReturnValue:result];
            responseDic = [self.class wrapArgumentFloat:*result];
            resultPrim = result;
            break;
        }
        case ReturnTypeBool:
        {
            BOOL* result = malloc(sizeof(BOOL));
            [serviceInv getReturnValue:result];
            responseDic = [self.class wrapArgumentBool:*result];
            resultPrim = result;
            break;
        }
        case ReturnTypeChar:
        {
            char* result = malloc(sizeof(char));
            [serviceInv getReturnValue:result];
            responseDic = [self.class wrapArgumentChar:*result];
            resultPrim = result;
            break;
        }
        case ReturnTypeLong:
        {
            long* result = malloc(sizeof(long));
            [serviceInv getReturnValue:result];
            responseDic = [self.class wrapArgumentLong:*result];
            resultPrim = result;
            break;
        }
        case ReturnTypeLongLong:
        {
            long long* result = malloc(sizeof(long long));
            [serviceInv getReturnValue:result];
            responseDic = [self.class wrapArgumentLongLong:*result];
            resultPrim = result;
            break;
        }
        case ReturnTypeShort:
        {
            short* result = malloc(sizeof(short));
            [serviceInv getReturnValue:result];
            responseDic = [self.class wrapArgumentShort:*result];
            resultPrim = result;
            break;
        }
        case ReturnTypeUnsignedInt:
        {
            unsigned int* result = malloc(sizeof(unsigned int));
            [serviceInv getReturnValue:result];
            responseDic = [self.class wrapArgumentUnsignedInt:*result];
            resultPrim = result;
            break;
        }
        case ReturnTypeUnsignedChar:
        {
            unsigned char* result = malloc(sizeof(unsigned char));
            [serviceInv getReturnValue:result];
            responseDic = [self.class wrapArgumentUnsignedChar:*result];
            resultPrim = result;
            break;
        }
        case ReturnTypeUnsignedLong:
        {
            unsigned long* result = malloc(sizeof(unsigned long));
            [serviceInv getReturnValue:result];
            responseDic = [self.class wrapArgumentUnsignedLong:*result];
            resultPrim = result;
            break;
        }
        case ReturnTypeUnsignedLongLong:
        {
            unsigned long long* result = malloc(sizeof(unsigned long long));
            [serviceInv getReturnValue:result];
            responseDic = [self.class wrapArgumentUnsignedLongLong:*result];
            resultPrim = result;
            break;
        }
        case ReturnTypeUnsignedShort:
        {
            unsigned short* result = malloc(sizeof(unsigned short));
            [serviceInv getReturnValue:result];
            responseDic = [self.class wrapArgumentUnsignedShort:*result];
            resultPrim = result;
            break;
        }
        default:
            [NSException raise:@"Illegal returnType" format:@"returnType %d is not supported.", self.returnType];
            break;
    }

    [self throwIfCancelled];
    
    if (self.keepInCache) {
        BOOL saveToCache = YES;
        if (self.keepInCacheBlock) {
            saveToCache = self.keepInCacheBlock([self.class unwrapArgument:responseDic]);
        }
        if (saveToCache) {
            [PCPersistenceManager saveObject:responseDic forKey:self.hashIdentifier pluginName:self.service.serviceName isCache:YES];
        }
    }
    
    NSInvocation* delegateInv = [NSInvocation invocationWithMethodSignature:[[self.delegate class] instanceMethodSignatureForSelector:self.delegateDidReturnSelector]];
    [delegateInv setSelector:self.delegateDidReturnSelector];
    [self setWrappedArgumentsForInvocation:delegateInv];
    
    if (resultObj) {
        [delegateInv setArgument:&resultObj atIndex:self.arguments.count+2];
    } else if (resultPrim) {
        [delegateInv setArgument:resultPrim atIndex:self.arguments.count+2];
    }

    [self throwIfCancelled];
    
    dispatch_sync(dispatch_get_main_queue(), ^{
        if ([self isCancelled]) {
            ServiceRequestLog(self, @"cancelled operation handled.");
        } else {
            if ([self.delegate respondsToSelector:self.delegateDidReturnSelector]) {
                [delegateInv invokeWithTarget:self.delegate];
            }
        }
    });
    if (completion) {
        completion();
    }
}

- (void)indicateConnectionErrorToDelegateCompletion:(VoidBlock)completion {
    dispatch_sync(dispatch_get_main_queue(), ^{
        if ([self isCancelled]) {
            ServiceRequestLog(self, @"cancelled operation handled.");
        } else {
            if ([self.delegate respondsToSelector:@selector(serviceConnectionToServerFailed)]) {
                [self.delegate serviceConnectionToServerFailed];
            }
        }
    });
    if (completion) {
        completion();
    }
}

- (void)indicateFailureToDelegateCompletion:(VoidBlock)completion {
    if ([self isCancelled]) {
        ServiceRequestLog(self, @"cancelled operation handled.");
        if (completion) {
            completion();
        }
        return;
    }
    if (![self.delegate respondsToSelector:self.delegateDidFailSelector]) {
        ServiceRequestLog(self, @"operation failed but delegate does not respond to selector %@. Ignoring.", NSStringFromSelector(self.delegateDidFailSelector));
        if (completion) {
            completion();
        }
        return;
    }
    @try {
        NSInvocation* delegateFailInv = [NSInvocation invocationWithMethodSignature:[[self.delegate class] instanceMethodSignatureForSelector:self.delegateDidFailSelector]];
        [delegateFailInv setSelector:self.delegateDidFailSelector];
        [self setWrappedArgumentsForInvocation:delegateFailInv];
        dispatch_sync(dispatch_get_main_queue(), ^{
            if ([self isCancelled]) {
                ServiceRequestLog(self, @"cancelled operation handled.");
            } else {
                if (self.delegate) {
                    [delegateFailInv invokeWithTarget:self.delegate];
                }
            }
        });
    }
    @catch (NSException *exception) {
        ServiceRequestLog(self, @"EXCEPTION while trying to indicate failure to delegate");
    }
    if (completion) {
        completion();
    }
}

- (void)finish {
    self.executing = NO;
    self.finished = YES;
}

#pragma mark - NSOperation overrides

- (BOOL)isConcurrent {
    return YES;
}

- (void)cancel {
    [super cancel];
    self.delegate = nil;
    self.cancelled = YES;
}

#pragma mark - Arguments management

#pragma mark Public

- (void)addObjectArgument:(id)object {
    [self addWrappedArgument:[self.class wrapArgumentObject:object]];
}

- (void)addBoolArgument:(BOOL)val {
    [self addWrappedArgument:[self.class wrapArgumentBool:val]];
}

- (void)addCharArgument:(char)val {
    [self addWrappedArgument:[self.class wrapArgumentChar:val]];
}

- (void)addUnsignedCharArgument:(unsigned char)val {
    [self addWrappedArgument:[self.class wrapArgumentUnsignedChar:val]];
}

- (void)addDoubleArgument:(double)val {
    [self addWrappedArgument:[self.class wrapArgumentDouble:val]];
}

- (void)addFloatArgument:(float)val {
    [self addWrappedArgument:[self.class wrapArgumentFloat:val]];
}

- (void)addIntArgument:(int)val {
    [self addWrappedArgument:[self.class wrapArgumentInt:val]];
}

- (void)addUnsignedIntArgument:(unsigned int)val {
    [self addWrappedArgument:[self.class wrapArgumentUnsignedInt:val]];
}

- (void)addLongArgument:(long)val {
    [self addWrappedArgument:[self.class wrapArgumentLong:val]];
}

- (void)addUnsignedLongArgument:(unsigned long) val {
    [self addWrappedArgument:[self.class wrapArgumentUnsignedLong:val]];
}

- (void)addLongLongArgument:(long long)val {
    [self addWrappedArgument:[self.class wrapArgumentLongLong:val]];
}

- (void)addUnsignedLongLongArgument:(unsigned long long)val {
    [self addWrappedArgument:[self.class wrapArgumentUnsignedLongLong:val]];
}

- (void)addShortArgument:(short)val {
    [self addWrappedArgument:[self.class wrapArgumentShort:val]];
}

- (void)addUnsignedShortArgument:(unsigned short)val {
    [self addWrappedArgument:[self.class wrapArgumentUnsignedShort:val]];
}

#pragma mark Private

static NSString* const kWrappedElementDictPrimitiveKey = @"primitive";
static NSString* const kWrappedElementDictValueKey = @"value";
static NSString* const kWrappedElementDictNSNumberSelectorStringKey = @"nsNumberSelectorString";

- (void)addWrappedArgument:(NSDictionary*)argDic {
    [self.arguments addObject:argDic];
}

+ (NSDictionary*)wrapArgumentObject:(id)object {
    return @{kWrappedElementDictPrimitiveKey:@NO,
             kWrappedElementDictValueKey:object};
}

+ (NSDictionary*)wrapArgumentBool:(BOOL)val {
    return @{kWrappedElementDictPrimitiveKey:@YES,
             kWrappedElementDictValueKey:[NSNumber numberWithBool:val],
             kWrappedElementDictNSNumberSelectorStringKey:NSStringFromSelector(@selector(boolValue))};
}

+ (NSDictionary*)wrapArgumentChar:(char)val {
    return @{kWrappedElementDictPrimitiveKey:@YES,
             kWrappedElementDictValueKey:[NSNumber numberWithChar:val],
             kWrappedElementDictNSNumberSelectorStringKey:NSStringFromSelector(@selector(charValue))};
}

+ (NSDictionary*)wrapArgumentUnsignedChar:(unsigned char)val {
    return @{kWrappedElementDictPrimitiveKey:@YES,
             kWrappedElementDictValueKey:[NSNumber numberWithUnsignedChar:val],
             kWrappedElementDictNSNumberSelectorStringKey:NSStringFromSelector(@selector(unsignedCharValue))};
}

+ (NSDictionary*)wrapArgumentDouble:(double)val {
    return @{kWrappedElementDictPrimitiveKey:@YES,
             kWrappedElementDictValueKey:[NSNumber numberWithDouble:val],
             kWrappedElementDictNSNumberSelectorStringKey:NSStringFromSelector(@selector(doubleValue))};
}

+ (NSDictionary*)wrapArgumentFloat:(float)val {
    return @{kWrappedElementDictPrimitiveKey:@YES,
             kWrappedElementDictValueKey:[NSNumber numberWithFloat:val],
             kWrappedElementDictNSNumberSelectorStringKey:NSStringFromSelector(@selector(floatValue))};
}

+ (NSDictionary*)wrapArgumentInt:(int)val {
    return @{kWrappedElementDictPrimitiveKey:@YES,
             kWrappedElementDictValueKey:[NSNumber numberWithInt:val],
             kWrappedElementDictNSNumberSelectorStringKey:NSStringFromSelector(@selector(intValue))};
}

+ (NSDictionary*)wrapArgumentUnsignedInt:(unsigned int)val {
    return @{kWrappedElementDictPrimitiveKey:@YES,
             kWrappedElementDictValueKey:[NSNumber numberWithUnsignedInt:val],
             kWrappedElementDictNSNumberSelectorStringKey:NSStringFromSelector(@selector(unsignedIntValue))};
}

+ (NSDictionary*)wrapArgumentLong:(long)val {
    return @{kWrappedElementDictPrimitiveKey:@YES,
             kWrappedElementDictValueKey:[NSNumber numberWithLong:val],
             kWrappedElementDictNSNumberSelectorStringKey:NSStringFromSelector(@selector(unsignedIntValue))};
}

+ (NSDictionary*)wrapArgumentUnsignedLong:(unsigned long)val {
    return @{kWrappedElementDictPrimitiveKey:@YES,
             kWrappedElementDictValueKey:[NSNumber numberWithUnsignedLong:val],
             kWrappedElementDictNSNumberSelectorStringKey:NSStringFromSelector(@selector(unsignedLongValue))};
}

+ (NSDictionary*)wrapArgumentLongLong:(long long)val {
    return @{kWrappedElementDictPrimitiveKey:@YES,
             kWrappedElementDictValueKey:[NSNumber numberWithLongLong:val],
             kWrappedElementDictNSNumberSelectorStringKey:NSStringFromSelector(@selector(longLongValue))};
}

+ (NSDictionary*)wrapArgumentUnsignedLongLong:(unsigned long long)val {
    return @{kWrappedElementDictPrimitiveKey:@YES,
             kWrappedElementDictValueKey:[NSNumber numberWithUnsignedLongLong:val],
             kWrappedElementDictNSNumberSelectorStringKey:NSStringFromSelector(@selector(unsignedLongLongValue))};
}

+ (NSDictionary*)wrapArgumentShort:(short)val {
    return @{kWrappedElementDictPrimitiveKey:@YES,
             kWrappedElementDictValueKey:[NSNumber numberWithShort:val],
             kWrappedElementDictNSNumberSelectorStringKey:NSStringFromSelector(@selector(shortValue))};
}

+ (NSDictionary*)wrapArgumentUnsignedShort:(unsigned short)val {
    return @{kWrappedElementDictPrimitiveKey:@YES,
             kWrappedElementDictValueKey:[NSNumber numberWithUnsignedShort:val],
             kWrappedElementDictNSNumberSelectorStringKey:NSStringFromSelector(@selector(unsignedShortValue))};
}

+ (void*)unwrapArgument:(NSDictionary*)argDic {
    if (![argDic[kWrappedElementDictPrimitiveKey] boolValue]) {
        return (__bridge void *)(argDic[kWrappedElementDictValueKey]);
    }
    
    NSNumber* val = argDic[kWrappedElementDictValueKey];
    NSString* selectorString = argDic[kWrappedElementDictNSNumberSelectorStringKey];
    SEL selector = NSSelectorFromString(selectorString);
    
    NSInvocation* inv = [NSInvocation invocationWithMethodSignature:[NSNumber instanceMethodSignatureForSelector:selector]];
    [inv setSelector:selector];
    [inv invokeWithTarget:val];
    void* ret = malloc(8);
    [inv getReturnValue:ret];
    return ret;
}

- (void)setWrappedArgumentsForInvocation:(NSInvocation*)inv {
    [self.arguments enumerateObjectsUsingBlock:^(NSDictionary* argDic, NSUInteger index, BOOL *stop) {
        if ([argDic[kWrappedElementDictPrimitiveKey] boolValue]) {
            void* ptr = [PCServiceRequest unwrapArgument:argDic];
            [inv setArgument:ptr atIndex:index+2];
            free(ptr);
        } else {
            id object = [PCServiceRequest unwrapArgument:argDic];
            [inv setArgument:&object atIndex:index+2];
        }
    }];
}

#pragma mark - Utils

+ (NSString*)md5HexDigestForString:(NSString*)input {
    const char* str = [input UTF8String];
    unsigned char result[CC_MD5_DIGEST_LENGTH];
    CC_MD5(str, (CC_LONG)strlen(str), result);
    NSMutableString* ret = [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH*2];
    for(int i = 0; i<CC_MD5_DIGEST_LENGTH; i++) {
        [ret appendFormat:@"%02x",result[i]];
    }
    return ret;
}

@end
