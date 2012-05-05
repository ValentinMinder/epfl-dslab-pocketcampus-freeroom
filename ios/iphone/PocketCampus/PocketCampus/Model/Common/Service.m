//
//  Service.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "Service.h"

static NSTimeInterval requestTimeoutInterval;

@implementation Service

@synthesize thriftProtocol, thriftClient;

- (id)initWithServiceName:(NSString*)serviceName_
{
    if (![serviceName_ isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"Bad serviceName" reason:@"serviceName argument is not of type NSString or is nil" userInfo:nil];
    }
    self = [super init];
    if (self) {
        serviceName = [serviceName_ retain];
        NSDictionary* config = [NSDictionary dictionaryWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"Config" ofType:@"plist"]];
        NSString* urlString = nil;
        
        if ([[config objectForKey:@"DEV_MODE"] isEqual:[NSNumber numberWithInt:1]]) {
            urlString = [config objectForKey:@"DEV_SERVER_URL"];
            NSLog(@"-> Initializing service '%@' on DEV server (%@%@)", serviceName, urlString, serviceName);
        } else {
            urlString = [config objectForKey:@"PROD_SERVER_URL"];
            NSLog(@"-> Initializing service '%@' on PRODUCTION server (%@%@)", serviceName, urlString, serviceName);
        }
        NSString* stringURL = [NSString stringWithFormat:@"%@%@", urlString, serviceName];
        serverURL = [[NSURL URLWithString:stringURL] retain];
        THTTPClient* client = [[THTTPClient alloc] initWithURL:serverURL userAgent:nil timeout:10];
        thriftProtocol = [[TBinaryProtocol alloc] initWithTransport:client strictRead:YES strictWrite:YES];
        [client release];
        operationQueue = [[NSOperationQueue alloc] init];
        //[operationQueue setMaxConcurrentOperationCount:1];
        requestTimeoutInterval = [(NSNumber*)[config objectForKey:@"THRIFT_REQUEST_TIMEOUT"] floatValue];
    }
    return self;
}

+ (NSTimeInterval)requestTimeoutInterval {
    return requestTimeoutInterval;
}

- (void)cancelOperationsForDelegate:(id<ServiceDelegate>)delegate {
    int nbOps = 0;
    for (NSOperationWithDelegate* operation in operationQueue.operations) {
        if(operation.delegate == delegate) {
            operation.delegate = nil;
            [operation cancel];
            nbOps++;
        }
    }
    NSLog(@"-> All (%d) operations canceled for delegate %@", nbOps, delegate);
}

- (id)thriftProtocolInstance {
    THTTPClient* client = [[THTTPClient alloc] initWithURL:serverURL userAgent:nil timeout:12.3];
    TBinaryProtocol* thriftProtocol_ = [[TBinaryProtocol alloc] initWithTransport:client strictRead:YES strictWrite:YES];
    [client release];
    return [thriftProtocol_ autorelease];
}

- (void)dealloc
{
    [operationQueue release];
    [thriftProtocol release];
    [self.thriftClient release];
    [serverURL release];
    NSLog(@"-> Service '%@' released. Some ServiceRequest may still be waiting for replies though. Those will be killed and released after thrift request timeout.", serviceName);
    [serviceName release];
    [super dealloc];
}

@end

/*----------------------------------------------------------------------------------------------*/

@implementation NSOperationWithDelegate

@synthesize delegate;

- (BOOL)delegateRespondsToSelector:(SEL)selector {
    if (self.delegate == nil) {
        return NO;
    }
    if (![self.delegate respondsToSelector:selector]) {
        return NO;
    }
    return YES;
}

@end

@implementation ServiceRequest

@synthesize thriftServiceClient, timedOut, delegateDidReturnSelector, delegateDidFailSelector, serviceClientSelector, returnType, customTimeout;

- (id)initWithThriftServiceClient:(id)serviceClient delegate:(id)delegate_
{
    self = [super init];
    if (self) {
        self.thriftServiceClient = serviceClient;
        self.delegate = delegate_;
        self.timedOut = NO;
        self.serviceClientSelector = nil;
        arguments = [[NSMutableArray alloc] init];
        self.returnType = ReturnTypeNotSet;
        finished = NO;
        executing = NO;
        canceled = NO;
        customTimeout = 0.0;
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
    
    dispatch_async(dispatch_get_main_queue(), ^{
        NSTimeInterval timeout = [Service requestTimeoutInterval];
        if (customTimeout != 0.0) {
            timeout = customTimeout;
        }
        timeoutTimer = [[NSTimer scheduledTimerWithTimeInterval:timeout target:self selector:@selector(didTimeout) userInfo:nil repeats:NO] retain];
    });
}

- (void)main {
    
    @try {
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
        [self setArgumentsForInvocation:serviceInv];
        [serviceInv invokeWithTarget:self.thriftServiceClient];
        if ([self isCancelled])
        {
            @throw [NSException exceptionWithName:@"Operation cancelled" reason:@"operation was cancelled" userInfo:nil];
        }
        
        NSInvocation* delegateInv = [[NSInvocation invocationWithMethodSignature:[[self.delegate class] instanceMethodSignatureForSelector:self.delegateDidReturnSelector]] retain];
        [delegateInv setSelector:self.delegateDidReturnSelector];
        [self setArgumentsForInvocation:delegateInv];
        
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
                    short* result = malloc(sizeof(short));
                    [serviceInv getReturnValue:result];
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
            
            if (![self delegateRespondsToSelector:self.delegateDidFailSelector]) {
                @throw [NSException exceptionWithName:@"-> Bad delegate response" reason:@"Delegate does not respond to didFail selector. Ignoring" userInfo:nil];
            }
            
            NSInvocation* failInv = [[NSInvocation invocationWithMethodSignature:[[self.delegate class] instanceMethodSignatureForSelector:self.delegateDidFailSelector]] retain];
            [failInv setSelector:self.delegateDidFailSelector];
            [self setArgumentsForInvocation:failInv];
            
            dispatch_async(dispatch_get_main_queue(), ^{
                if ([self delegateRespondsToSelector:self.delegateDidFailSelector]) {
                    [failInv invokeWithTarget:self.delegate];
                }
                [failInv release];
                [self finishAndRelease];
            });
        }
        @catch (NSException *exception) {
            NSLog(@"%@. %@", exception.name, exception.reason);
            [self finishAndRelease];
        }
        
    }
    
}

- (void)addObjectArgument:(id)object {
    //OK, object not checked => can be nil
    NSMutableDictionary* argDic = [NSMutableDictionary dictionary];
    [argDic setObject:object forKey:@"value"];
    [argDic setObject:[NSNumber numberWithBool:NO] forKey:@"primitive"];
    [arguments addObject:argDic];
}

//private
- (void)addPrimitiveArgument:(NSNumber*)nsNumber withNSNumberValueSelector:(SEL)nsNumberSelector {
    NSMutableDictionary* argDic = [NSMutableDictionary dictionary];
    [argDic setObject:nsNumber forKey:@"value"];
    [argDic setObject:[NSNumber numberWithBool:YES] forKey:@"primitive"];
    [argDic setObject:NSStringFromSelector(nsNumberSelector) forKey:@"nsNumberSelectorString"];
    [arguments addObject:argDic];
}

- (void)addBoolArgument:(BOOL)val {
    [self addPrimitiveArgument:[NSNumber numberWithBool:val] withNSNumberValueSelector:@selector(boolValue)];
}

- (void)addCharArgument:(char)val {
    [self addPrimitiveArgument:[NSNumber numberWithChar:val] withNSNumberValueSelector:@selector(charValue)];
}

- (void)addUnsignedCharArgument:(unsigned char)val {
    [self addPrimitiveArgument:[NSNumber numberWithUnsignedChar:val] withNSNumberValueSelector:@selector(unsignedCharValue)];
}

- (void)addDoubleArgument:(double)val {
    [self addPrimitiveArgument:[NSNumber numberWithDouble:val] withNSNumberValueSelector:@selector(doubleValue)];
}

- (void)addFloatArgument:(float)val {
    [self addPrimitiveArgument:[NSNumber numberWithFloat:val] withNSNumberValueSelector:@selector(floatValue)];
}

- (void)addIntArgument:(int)val {
    [self addPrimitiveArgument:[NSNumber numberWithInt:val] withNSNumberValueSelector:@selector(intValue)];
}

- (void)addUnsignedIntArgument:(unsigned int)val {
    [self addPrimitiveArgument:[NSNumber numberWithUnsignedInt:val] withNSNumberValueSelector:@selector(unsignedIntValue)];
}

- (void)addLongArgument:(long)val {
    [self addPrimitiveArgument:[NSNumber numberWithLong:val] withNSNumberValueSelector:@selector(longValue)];
}

- (void)addUnsignedLongArgument:(unsigned long) val {
    [self addPrimitiveArgument:[NSNumber numberWithUnsignedLong:val] withNSNumberValueSelector:@selector(unsignedLongValue)];
}

- (void)addLongLongArgument:(long long)val {
    [self addPrimitiveArgument:[NSNumber numberWithLongLong:val] withNSNumberValueSelector:@selector(longLongValue)];
}

- (void)addUnsignedLongLongArgument:(unsigned long long)val {
    [self addPrimitiveArgument:[NSNumber numberWithUnsignedLongLong:val] withNSNumberValueSelector:@selector(unsignedLongLongValue)];
}

- (void)addShortArgument:(short)val {
    [self addPrimitiveArgument:[NSNumber numberWithShort:val] withNSNumberValueSelector:@selector(shortValue)];
}

- (void)addUnsignedShortArgument:(unsigned short)val {
    [self addPrimitiveArgument:[NSNumber numberWithUnsignedShort:val] withNSNumberValueSelector:@selector(unsignedShortValue)];
}


- (void)setArgumentsForInvocation:(NSInvocation*)inv {
    int i;
    for (i = 0; i < arguments.count; i++) {
        NSDictionary* argDic = [arguments objectAtIndex:i];
        if ([[argDic objectForKey:@"primitive"] boolValue]) {
            [inv setArgument:[self argumentPrimitivePointerForIndex:i] atIndex:i+2];
            //WARNING : memory leak : argument is copied by NSInvocation and should thus bee freed with free(), cannot here here do not know type of argument at this place.
        } else {
            id object = [argDic objectForKey:@"value"];
            [inv setArgument:&object atIndex:i+2];
        }
    }
}

- (void*)argumentPrimitivePointerForIndex:(NSUInteger)index {
    NSDictionary* argDic = [arguments objectAtIndex:index];
    if (![[argDic objectForKey:@"primitive"] boolValue]) {
        @throw [NSException exceptionWithName:@"Argument is not primitive" reason:[NSString stringWithFormat:@"calling argumentPrimitivePointerForIndex:%d on not primitive argument", index] userInfo:nil];
    }
    
    NSNumber* val = [argDic objectForKey:@"value"];
    NSString* selectorString = [argDic objectForKey:@"nsNumberSelectorString"];
    SEL selector = NSSelectorFromString(selectorString);
    
    
    NSInvocation* inv = [NSInvocation invocationWithMethodSignature:[NSNumber instanceMethodSignatureForSelector:selector]];
    [inv setSelector:selector];
    [inv invokeWithTarget:val];
    
    if ([selectorString isEqualToString:@"boolValue"]) {
        BOOL* ret = malloc(sizeof(BOOL));
        [inv getReturnValue:ret];
        return ret;
    } else if ([selectorString isEqualToString:@"charValue"]) {
        char* ret = malloc(sizeof(char));
        [inv getReturnValue:ret];
        return ret;
    } else if ([selectorString isEqualToString:@"unsignedCharValue"]) {
        unsigned char* ret = malloc(sizeof(unsigned char));
        [inv getReturnValue:ret];
        return ret;
    } else if ([selectorString isEqualToString:@"doubleValue"]) {
        double* ret = malloc(sizeof(double));
        [inv getReturnValue:ret];
        return ret;
    } else if ([selectorString isEqualToString:@"floatValue"]) {
        float* ret = malloc(sizeof(float));
        [inv getReturnValue:ret];
        return ret;
    } else if ([selectorString isEqualToString:@"intValue"]) {
        int* ret = malloc(sizeof(int));
        [inv getReturnValue:ret];
        return ret;
    } else if ([selectorString isEqualToString:@"unsignedIntValue"]) {
        unsigned int* ret = malloc(sizeof(unsigned int));
        [inv getReturnValue:ret];
        return ret;
    } else if ([selectorString isEqualToString:@"longValue"]) {
        long* ret = malloc(sizeof(long));
        [inv getReturnValue:ret];
        return ret;
    } else if ([selectorString isEqualToString:@"unsignedLongValue"]) {
        unsigned long* ret = malloc(sizeof(unsigned long));
        [inv getReturnValue:ret];
        return ret;
    } else if ([selectorString isEqualToString:@"longLongValue"]) {
        long long* ret = malloc(sizeof(long long));
        [inv getReturnValue:ret];
        return ret;
    } else if ([selectorString isEqualToString:@"unsignedLongLongValue"]) {
        unsigned long long* ret = malloc(sizeof(unsigned long long));
        [inv getReturnValue:ret];
        return ret;
    } else if ([selectorString isEqualToString:@"shortValue"]) {
        short* ret = malloc(sizeof(short));
        [inv getReturnValue:ret];
        return ret;
    } else if ([selectorString isEqualToString:@"unsignedShortValue"]) {
        unsigned short* ret = malloc(sizeof(unsigned short));
        [inv getReturnValue:ret];
        return ret;
    } else {
        @throw [NSException exceptionWithName:@"Internal : bad selector set" reason:@"Unsupported <type>Value NSNumber selector" userInfo:nil];
    }
    
    void* error = NULL;
    return error;
}


- (BOOL)isCancelled {
    return canceled;
}

- (BOOL)isConcurrent {
    return NO;
}

- (BOOL)isExecuting {
    return executing;
}

- (BOOL)isFinished {
    return finished;
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

- (void)didTimeout {
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
    NSLog(@"-> ServiceRequest released");
    [self.thriftServiceClient release];
    [arguments release];
    [super dealloc];
}

@end
