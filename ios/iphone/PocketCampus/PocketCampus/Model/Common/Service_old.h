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

- (void)serviceConnectionToServerFailed;

@end

@protocol ServiceProtocol <NSObject>

+ (id)sharedInstance; //MUST be retained !!!

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
    ReturnTypeObject = 1,
    ReturnTypePrimitive = 2
} ReturnType;

@interface ServiceRequest : NSOperation {
    @public void** primitive_arg1;
    @public void** primitive_arg2;
}

@property (assign) id delegate;
@property (assign) id thriftServiceClient;
@property BOOL finished;
@property SEL serviceClientSelector;
@property SEL delegateDidReturnSelector;
@property SEL delegateDidFailSelector;
@property int nbArgs; //To be specified
@property (retain) id arg1;
@property (retain) id arg2;
@property ReturnType returnType;


- (id)initWithThriftServiceClient:(id)serviceClient delegate:(id)delegate_;
- (void)scheduleTimeoutTimer;
- (void)checkPrimariesAndScheduleTimeoutTimer;
- (void)didTimeout;

@end