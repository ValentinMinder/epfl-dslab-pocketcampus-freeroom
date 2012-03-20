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
            NSLog(@"-> Initializing service '%@' on DEV server (%@)", serviceName, urlString);
        } else {
            urlString = [config objectForKey:@"PROD_SERVER_URL"];
            NSLog(@"-> Initializing service '%@' on PRODUCTION server (%@)", serviceName, urlString);
        }
        NSString* stringURL = [NSString stringWithFormat:@"%@%@", urlString, serviceName];
        NSURL* url = [NSURL URLWithString:stringURL];
        THTTPClient* client = [[THTTPClient alloc] initWithURL:url userAgent:nil timeout:10];
        thriftProtocol = [[TBinaryProtocol alloc] initWithTransport:client strictRead:YES strictWrite:YES];
        [client release];
        operationQueue = [[NSOperationQueue alloc] init];
        requestTimeoutInterval = [(NSNumber*)[config objectForKey:@"THRIFT_REQUEST_TIMEOUT"] floatValue];
    }
    return self;
}

+ (NSTimeInterval)requestTimeoutInterval {
    return requestTimeoutInterval;
}

- (void)cancelOperationsForDelegate:(id<ServiceDelegate>)delegate {
    for (ServiceRequest* operation in operationQueue.operations) {
        if ([operation isKindOfClass:[ServiceRequest class]]) {
            if(operation.delegate == delegate) {
                operation.delegate = nil;
                [operation cancel];
            }
        }
    }
}

- (void)dealloc
{
    [operationQueue release];
    [thriftProtocol release];
    [self.thriftClient release];
    NSLog(@"-> Service '%@' released", serviceName);
    [serviceName release];
    [super dealloc];
}

@end

@implementation ServiceRequest

@synthesize delegate, thriftServiceClient, finished, delegateDidReturnSelector, delegateDidFailSelector, arg1, arg2, nbArgs, serviceClientSelector;

- (id)initWithThriftServiceClient:(id)serviceClient delegate:(id)delegate_
{
    self = [super init];
    if (self) {
        self.thriftServiceClient = serviceClient;
        self.delegate = delegate_;
        self.finished = NO;
        self.serviceClientSelector = nil;
        self.arg1 = nil;
        self.arg2 = nil;
        self.nbArgs = -1;
        primitive_arg1 = NULL;
        primitive_arg2 = NULL;
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
        @throw [NSException exceptionWithName:@"Nil delegate" reason:@"" userInfo:nil];
    }
    if (![self.delegate respondsToSelector:self.delegateDidReturnSelector]) {
        @throw [NSException exceptionWithName:@"Bad delegate response" reason:@"delegate does not respond to didReturn selector" userInfo:nil];
    }
    if (nbArgs < 0) {
        @throw [NSException exceptionWithName:@"Bad nbArgs" reason:@"Property nbArgs must be specified" userInfo:nil];
    }
    
    [self scheduleTimeoutTimer];
}

- (void)main {
    @try {
        
        [self checkPrimariesAndScheduleTimeoutTimer];
        
        id result = nil;
        /*if (self.arg1 != nil && self.arg2 != nil) {
            result = [self.thriftServiceClient performSelector:self.serviceClientSelector withObject:self.arg1 withObject:self.arg2];
        } else if (self.arg1 != nil && self.arg2 == nil) {
            result = [self.thriftServiceClient performSelector:self.serviceClientSelector withObject:self.arg1];
        } else {
            //request with no argument
            result = [self.thriftServiceClient performSelector:self.serviceClientSelector];
        }*/

        switch (self.nbArgs) {
            case 0:
                if (self.arg1 != nil || self.arg2 != nil || primitive_arg1 != NULL || primitive_arg2 != NULL) {
                    @throw [NSException exceptionWithName:@"Bad arguments" reason:@"specified number of arguments nbArg does not match assigned values to args." userInfo:nil];
                }
                result = [self.thriftServiceClient performSelector:self.serviceClientSelector];
                break;
            
            case 1:
                if (self.arg1 != nil && primitive_arg1 != NULL) {
                    @throw [NSException exceptionWithName:@"Bad argument" reason:@"Both arg1 and primitive_arg1 were set. Cannot be both." userInfo:nil];
                }
                
                if (self.arg1 == nil && primitive_arg1 == NULL) {
                    @throw [NSException exceptionWithName:@"Argument not set" reason:@"request argument is nil/NULL" userInfo:nil];
                }
                
                if (self.arg1 != nil) {
                    result = [self.thriftServiceClient performSelector:self.serviceClientSelector withObject:self.arg1];
                } else if (primitive_arg1 != NULL) {
                    result = [self.thriftServiceClient performSelector:self.serviceClientSelector withObject:*primitive_arg1];
                } else {
                    //Impossible
                }
                break;
                
            case 2:
                if (self.arg1 != nil && primitive_arg1 != NULL) {
                    @throw [NSException exceptionWithName:@"Bad argument 1" reason:@"Both arg1 and primitive_arg1 were set. Cannot be both." userInfo:nil];
                }
                
                if (self.arg2 != nil && primitive_arg2 != NULL) {
                    @throw [NSException exceptionWithName:@"Bad argument 2" reason:@"Both arg2 and primitive_arg2 were set. Cannot be both." userInfo:nil];
                }
                
                if (self.arg1 != nil && self.arg2 != nil) {
                    
                    result = [self.thriftServiceClient performSelector:self.serviceClientSelector withObject:self.arg1 withObject:self.arg2];
                    
                } else if (self.arg1 == nil && self.arg2 != nil) {
                    
                    if (primitive_arg1 == NULL) {
                        @throw [NSException exceptionWithName:@"Bad argument 1" reason:@"Both arg1 and primitive_arg1 were nil/NULL on 2 args request." userInfo:nil];
                    }
                    result = [self.thriftServiceClient performSelector:self.serviceClientSelector withObject:*primitive_arg1 withObject:self.arg2];
                    
                } else if (self.arg1 != nil && self.arg2 == nil) {
                    if (primitive_arg2 == NULL) {
                        @throw [NSException exceptionWithName:@"Bad argument 2" reason:@"Both arg2 and primitive_arg2 were nil/NULL on 2 args request." userInfo:nil];
                    }
                    result = [self.thriftServiceClient performSelector:self.serviceClientSelector withObject:self.arg1 withObject:*primitive_arg2];
                } else if (self.arg1 == nil && self.arg2 == nil) {
                    if (primitive_arg1 == NULL && primitive_arg2 == NULL) {
                        @throw [NSException exceptionWithName:@"Bad argument 1 and 2" reason:@"Both arg1, primitive_arg1 and arg2, primitive_arg2 were nil/NULL on 2 args request." userInfo:nil];
                    }
                    result = [self.thriftServiceClient performSelector:self.serviceClientSelector withObject:*primitive_arg1 withObject:*primitive_arg2];
                } else {
                    //Impossible
                }
                break;
                
            default:
                @throw [NSException exceptionWithName:@"Unsupported number of arguments" reason:@"ServiceRequest supports requests with up to 2 arguments. If you need more, subclass ServiceRequest and implement main again." userInfo:nil];
                break;
        }
        
        
        if (result == nil) {
            @throw [NSException exceptionWithName:@"Nil result" reason:@"" userInfo:nil];
        }
    
        if (![self.delegate respondsToSelector:self.delegateDidReturnSelector]) {
            NSLog(@"WARNING : delegate does not respond to returnSelector. Ignoring.");
        } else {
            switch (self.nbArgs) {
                case 0:
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self.delegate performSelector:self.delegateDidReturnSelector withObject:result];
                    });
                    break;
                    
                default:
                    if (self.arg1 != nil) {
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [self.delegate performSelector:self.delegateDidReturnSelector withObject:self.arg1 withObject:result]; //performSelector can only take maximum 2 arguments for the objects. Thus returning only the first argument along with the result in case of nbArgs = 1 or 2
                        });
                    } else if (primitive_arg1 != NULL) {
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [self.delegate performSelector:self.delegateDidReturnSelector withObject:*primitive_arg1 withObject:result]; //performSelector can only take maximum 2 arguments for the objects. Thus returning only the first argument along with the result in case of nbArgs = 1 or 2
                        });
                    } else {
                        //Impossible after previous checks
                    }
                    break;
            }
        }
    }
    @catch (NSException *exception) {
        NSLog(@"!!! EXCEPTION caught in ServiceRequest main : %@", exception);
        if (![self.delegate respondsToSelector:self.delegateDidFailSelector]) {
            NSLog(@"WARNING : delegate does not respond to fail selector. Ignoring.");
        } else {
            switch (self.nbArgs) {
                case 0:
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self.delegate performSelector:self.delegateDidFailSelector];
                    });
                    break;
                    
                default:
                    if (self.arg1 != nil) {
                        
                    } else if (primitive_arg1 != NULL) {
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [self.delegate performSelector:self.delegateDidFailSelector withObject:*primitive_arg1];
                        });
                    } else {
                        //Impossible after previous checks
                    }
                    break;
            }
        }
        
    }
    @finally {
        self.finished = YES;
    }
}

- (void)scheduleTimeoutTimer {
    dispatch_async(dispatch_get_main_queue(), ^{
        [NSTimer scheduledTimerWithTimeInterval:[Service requestTimeoutInterval] target:self selector:@selector(didTimeout) userInfo:nil repeats:NO];
    });
}

- (void)didTimeout {
    if (self.finished) { 
        return;
    }
    if ([self.delegate respondsToSelector:@selector(serviceConnectionToServerFailed)]) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate serviceConnectionToServerFailed];
        });
    }
}

- (void)dealloc
{
    if (primitive_arg1 != NULL) {
        free(primitive_arg1);
    }
    if (primitive_arg2 != NULL) {
        free(primitive_arg2);
    }
    [arg1 release];
    [arg2 release];
    [super dealloc];
}

@end
