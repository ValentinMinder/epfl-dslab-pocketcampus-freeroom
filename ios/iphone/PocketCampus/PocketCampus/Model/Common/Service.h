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

+ (id)sharedInstanceToRetain; //MUST be retained !!!

@end

@interface Service : NSObject {
    NSString* serviceName;
    TBinaryProtocol* thriftProtocol;
    NSOperationQueue* operationQueue;
    id thriftClient;
}

@property (readonly) TBinaryProtocol* thriftProtocol;
@property (retain) id thriftClient;

- (id)initWithServiceName:(NSString*)serviceName;
+ (NSTimeInterval)requestTimeoutInterval;
- (void)cancelOperationsForDelegate:(id<ServiceDelegate>)delegate;
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

@interface ServiceRequest : NSOperation {
    NSMutableArray* arguments;
    NSTimer* timeoutTimer;
}

@property (assign) id delegate;
@property (retain) id thriftServiceClient;
@property BOOL timedOut;
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
- (void)didTimeout;

@end