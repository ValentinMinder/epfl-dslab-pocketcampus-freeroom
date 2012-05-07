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

@protocol ServiceDelegate <NSObject>

- (void)serviceConnectionToServerTimedOut;

@end

@protocol ServiceProtocol <NSObject>

@required
+ (id)sharedInstanceToRetain; //MUST be retained !!!
- (id)thriftServiceClientInstance; //to implement : must return a thrift client (ex. TransportServiceClient)

@end

@interface Service : NSObject {
    NSString* serviceName;
    NSURL* serverURL;
    TBinaryProtocol* thriftProtocol;
    NSOperationQueue* operationQueue;
    id thriftClient;
}

@property (readonly) TBinaryProtocol* thriftProtocol;
@property (retain) id thriftClient;

- (id)initWithServiceName:(NSString*)serviceName;
+ (NSTimeInterval)requestTimeoutInterval;
- (void)cancelOperationsForDelegate:(id<ServiceDelegate>)delegate;
- (id)thriftProtocolInstance;

+ (BOOL)serverIsReachable;

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

@interface NSOperationWithDelegate : NSOperation

@property (assign) id delegate;

- (BOOL)delegateRespondsToSelector:(SEL)selector;

@end

@interface ServiceRequest : NSOperationWithDelegate {
    NSMutableArray* arguments;
    NSTimer* timeoutTimer;
    NSTimeInterval customTimeout;
    BOOL executing;
    BOOL finished;
    BOOL canceled;
}

@property (retain) id thriftServiceClient;
@property BOOL timedOut;
@property NSTimeInterval customTimeout;
@property SEL serviceClientSelector;
@property SEL delegateDidReturnSelector;
@property SEL delegateDidFailSelector;
@property ReturnType returnType;


- (id)initWithThriftServiceClient:(id)serviceClient delegate:(id)delegate_;

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