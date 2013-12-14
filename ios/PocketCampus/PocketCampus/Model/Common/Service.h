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

- (void)serviceConnectionToServerFailed;

@end

@protocol ServiceProtocol <NSObject>

/*
 * Conforming services must implement this method and implement a weak singleton.
 * i.e. only one instance can live a time, but it can be relased.
 * This can be done with a dispatch_once and a weak static instance pointer.
 */
@required
+ (id)sharedInstanceToRetain;

@end


/*
 * Abstract class
 * Plugin services should sublcass it and conform to ServiceProtocol
 */
@interface Service : NSObject

@property (nonatomic, readonly) NSString* serviceName;

@property (nonatomic, readonly) NSString* thriftServiceClientClassName;

/*
 * Full URL of service. Initialized automatically at init, using [PCConfig default].
 * For e.g. : https://pocketcampus.epfl.ch:4433/v3r1/news
 */
@property (nonatomic, readonly) NSURL* serviceURL;

/*
 * Queue on which ServiceRequest (NSOperation) are scheduled.
 * You can add to this queue any NSOperation related to the service.
 */
@property (nonatomic, readonly) NSOperationQueue* operationQueue;

/*
 * Pass nil thriftServiceClientClassName if your service does not talk to a thrift server
 */
- (id)initWithServiceName:(NSString*)serviceName thriftServiceClientClassName:(NSString*)thriftServiceClientClassName;

/*
 * Returns nil if thriftServiceClientClassName passed at init is invalid or nil
 */
- (id)thriftServiceClientInstance;
- (id)thriftServiceClientInstanceWithCustomTimeoutInterval:(NSTimeInterval)timeoutInterval;

- (id)thriftProtocolInstance;
- (id)thriftProtocolInstanceWithCustomTimeoutInterval:(NSTimeInterval)timeoutInterval;


- (void)cancelOperationsForDelegate:(id<ServiceDelegate>)delegate;
- (void)cancelAllOperations;

@end