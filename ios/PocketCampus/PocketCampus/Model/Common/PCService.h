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

//  Created by Loïc Gardiol on 28.02.12.

@import Foundation;

#import "PCServiceRequest.h"

@protocol PCServiceDelegate <NSObject>

- (void)serviceConnectionToServerFailed;

@end

@protocol PCServiceProtocol <NSObject>

/**
 * Conforming services must implement this method and implement a weak singleton.
 * i.e. only one instance can live a time, but it can be relased.
 * This can be done with a dispatch_once and a weak static instance pointer.
 */
@required
+ (id)sharedInstanceToRetain;

@end


/**
 * Abstract class
 * Plugin services should sublcass it and conform to ServiceProtocol
 */
@interface PCService : NSObject

@property (nonatomic, readonly) NSString* serviceName;

@property (nonatomic, readonly) NSString* thriftServiceClientClassName;

/**
 * Full URL of service. Initialized automatically at init, using [PCConfig defaults].
 * For e.g. : https://pocketcampus.epfl.ch:4433/v3r1/news
 */
@property (nonatomic, readonly) NSURL* serviceURL;

/**
 * Queue on which ServiceRequest (NSOperation) are scheduled.
 * You can add to this queue any NSOperation related to the service.
 */
@property (nonatomic, readonly) NSOperationQueue* operationQueue;

/**
 * Pass nil thriftServiceClientClassName if your service does not talk to a thrift server
 */
- (id)initWithServiceName:(NSString*)serviceName thriftServiceClientClassName:(NSString*)thriftServiceClientClassName;

/**
 * Returns nil if thriftServiceClientClassName passed at init is invalid or nil
 */
- (id)thriftServiceClientInstance;
- (id)thriftServiceClientInstanceWithCustomTimeoutInterval:(NSTimeInterval)timeoutInterval;

- (id)thriftProtocolInstance;
- (id)thriftProtocolInstanceWithCustomTimeoutInterval:(NSTimeInterval)timeoutInterval;


- (void)cancelOperationsForDelegate:(id<PCServiceDelegate>)delegate;
- (void)cancelAllOperations;

/**
 * @return a mutable URL request pointing to raw service
 * You can then add parameters to this request, corresponding
 * to the action you want to execute.
 * @discussion WARNING: you must NOT remove the request headers.
 */
- (NSMutableURLRequest*)pcProxiedRequest;

@end