//
//  Service.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "ServiceRequest.h"

@protocol ServiceDelegate <NSObject>

- (void)serviceConnectionToServerTimedOut;

@end

@protocol ServiceProtocol <NSObject>

@required
+ (id)sharedInstanceToRetain; //MUST be retained !!!
- (id)thriftServiceClientInstance; //to implement : must return a thrift client (ex. TransportServiceClient)

@optional
- (id)thriftServiceClientInstanceWithCustomTimeoutInterval:(NSTimeInterval)timeoutInterval;

@end


@interface Service : NSObject

@property (nonatomic, readonly) NSString* serviceName;
@property (readonly) NSOperationQueue* operationQueue;


- (id)initWithServiceName:(NSString*)serviceName;

- (id)thriftProtocolInstance;
- (id)thriftProtocolInstanceWithCustomTimeoutInterval:(NSTimeInterval)timeoutInterval;

- (void)cancelOperationsForDelegate:(id<ServiceDelegate>)delegate;
- (void)cancelAllOperations;

@end