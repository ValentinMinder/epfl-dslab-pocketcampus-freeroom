//
//  Service.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "THTTPClient.h"
#import "TBinaryProtocol.h"

#import "ASIHTTPRequest.h"

@protocol ServiceDelegate <NSObject>

- (void)serviceConnectionToServerTimedOut;

@end

@protocol ServiceProtocol <NSObject>

@required
+ (id)sharedInstanceToRetain; //MUST be retained !!!
- (id)thriftServiceClientInstance; //to implement : must return a thrift client (ex. TransportServiceClient)

@end


@interface Service : NSObject<ASIHTTPRequestDelegate> {
    NSString* serviceName;
    NSString* serverAddressWithPort; //example 128.178.0.1:9090
    NSURL* serverURL; //full URL with service extension (example ../v3r1/transport/)
    TBinaryProtocol* thriftProtocol;
    NSOperationQueue* operationQueue;
    id thriftClient;
    dispatch_semaphore_t semaphore;
    ASIHTTPRequest* checkServerRequest;
    BOOL serverIsReachable;
}

@property (readonly) NSOperationQueue* operationQueue;
@property (readonly) TBinaryProtocol* thriftProtocol;
@property BOOL serviceWillBeReleased;


- (id)initWithServiceName:(NSString*)serviceName;
+ (NSTimeInterval)requestTimeoutInterval;
- (void)cancelOperationsForDelegate:(id<ServiceDelegate>)delegate;
- (void)cancelAllOperations;
- (id)thriftProtocolInstance;

- (BOOL)serverIsReachable;
- (NSString*) serviceName;

@end

typedef enum {
    ReturnTypeNotSet = 0,
    ReturnTypeObject,
    ReturnTypeBool,
    ReturnTypeChar,
    ReturnTypeUnsignedChar,
    ReturnTypeDouble,
    ReturnTypeFloat,
    ReturnTypeInt,
    ReturnTypeUnsignedInt,
    ReturnTypeLong,
    ReturnTypeUnsignedLong,
    ReturnTypeLongLong,
    ReturnTypeUnsignedLongLong,
    ReturnTypeShort,
    ReturnTypeUnsignedShort
} ReturnType;

/*----------------------------------------------------------------------------------------------------------*/

@interface NSOperationWithDelegate : NSOperation {
    BOOL executing;
    BOOL finished;
    BOOL canceled;
}

@property (assign) id delegate;
@property SEL delegateDidReturnSelector;
@property SEL delegateDidFailSelector;

- (id)initWithDelegate:(id)delegate;
- (BOOL)delegateRespondsToSelector:(SEL)selector;

@end

@interface ServiceRequest : NSOperationWithDelegate {
    NSMutableArray* arguments;
    NSTimer* timeoutTimer;
    NSTimeInterval customTimeout;
    NSString* hashCode;
    id thriftServiceClient;
}

@property (retain) id thriftServiceClient;
@property BOOL timedOut;
@property BOOL shouldRestart; //will be checked if server availaility returns NO. If shouldRestart==YES, operation will be restarted
@property NSTimeInterval customTimeout;
@property BOOL keepInCache;
@property BOOL skipCache;
@property BOOL returnEvenStaleCacheIfServerIsUnreachable;
@property NSTimeInterval cacheValidity;
@property SEL serviceClientSelector;
@property ReturnType returnType;
@property NSUInteger nbTrimmedArgumentsFromLeftInDelegateCall;
@property (nonatomic, assign) Service* service;


- (id)initWithThriftServiceClient:(id)serviceClient service:(Service*)service delegate:(id)delegate_;

- (id)initForCachedResponseOnlyWithService:(Service*)service_;
- (id)cachedResponseObjectEvenIfStale:(BOOL)evenIfStale;

- (void)addObjectArgument:(id)object;

- (void)addBoolArgument:(BOOL)val;
- (void)addCharArgument:(char)val;
- (void)addUnsignedCharArgument:(unsigned char)val;
- (void)addDoubleArgument:(double)val;
- (void)addFloatArgument:(float)val;
- (void)addIntArgument:(int)val;
- (void)addUnsignedIntArgument:(unsigned int)val;
- (void)addLongArgument:(long)val;
- (void)addUnsignedLongArgument:(unsigned long) val;
- (void)addLongLongArgument:(long long)val;
- (void)addUnsignedLongLongArgument:(unsigned long long)val;
- (void)addShortArgument:(short)val;
- (void)addUnsignedShortArgument:(unsigned short)val;

- (void)checkPrimariesAndScheduleTimeoutTimer;
- (void)finishAndRelease; //protected, should not be called by other classes
- (void)didTimeout;

@end