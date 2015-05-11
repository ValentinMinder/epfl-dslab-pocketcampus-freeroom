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

#import "PCService.h"

#import "PushNotifController.h"

#import "THTTPClient.h"
#import "THTTPClient+MutableRequest.h"
#import "TBinaryProtocol.h"


static const NSTimeInterval kDefaultThriftProtocolInstanceTimeoutInterval = 20.0;

@interface PCService ()

@property (nonatomic, readwrite, strong) NSString* serviceName;
@property (nonatomic, readwrite, strong) NSString* thriftServiceClientClassName;
@property (nonatomic, readwrite, strong) NSURL* serviceURL;
@property (nonatomic, readwrite, strong) NSURL* serviceRawURL;
@property (nonatomic, readwrite, strong) NSOperationQueue* operationQueue;

@end

@implementation PCService

#pragma mark - Init

- (id)initWithServiceName:(NSString*)serviceName {
    return [self initWithServiceName:serviceName thriftServiceClientClassName:nil];
}

- (id)initWithServiceName:(NSString*)serviceName thriftServiceClientClassName:(NSString*)thriftServiceClientClassName; {
    [PCUtils throwExceptionIfObject:serviceName notKindOfClass:[NSString class]];
    self = [super init];
    if (self) {
        self.serviceName = serviceName;
        self.thriftServiceClientClassName = thriftServiceClientClassName;
        self.serviceURL = [self.class serviceURLforServiceName:serviceName raw:NO];
        self.serviceRawURL = [self.class serviceURLforServiceName:serviceName raw:YES];
        CLSNSLog(@"-> Initializing service '%@' with URL (%@)", serviceName, self.serviceURL.absoluteString);
        self.operationQueue = [NSOperationQueue new];
        self.operationQueue.maxConcurrentOperationCount = NSOperationQueueDefaultMaxConcurrentOperationCount;
    }
    return self;
}

#pragma mark - Config



+ (NSURL*)serviceURLforServiceName:(NSString*)serviceName raw:(BOOL)raw {
    static NSURL* kServerURL;
    static NSString* kVersionURI;
    if (!kServerURL || !kVersionURI) {
        NSUserDefaults* defaults = [PCConfig defaults];
        NSString* serverProt = [defaults objectForKey:PC_CONFIG_SERVER_PROTOCOL_KEY];
        NSString* serverAddress = [defaults objectForKey:PC_CONFIG_SERVER_ADDRESS_KEY];
        NSString* serverPort = [defaults objectForKey:PC_CONFIG_SERVER_PORT_KEY];
        NSString* versionURI = [defaults objectForKey:PC_CONFIG_SERVER_URI_KEY];
        kServerURL = [NSURL URLWithString:[NSString stringWithFormat:@"%@://%@:%@", serverProt, serverAddress, serverPort]];
        kVersionURI = versionURI;
    }
    if (raw) {
        serviceName = [@"raw-" stringByAppendingString:serviceName];
    }
    return [NSURL URLWithString:[kVersionURI stringByAppendingPathComponent:serviceName] relativeToURL:kServerURL];
}

#pragma mark - Cancelling operations

//pass nil to cancel all operations
- (void)cancelOperationsForDelegate:(id<PCServiceDelegate>)delegate {
    int nbOps = 0;
    for (NSOperation* operation in self.operationQueue.operations) {
        if (!delegate) {
            [operation cancel];
            nbOps++;
        } else if ([operation respondsToSelector:@selector(delegate)]) {
            if ([(NSOperationWithDelegate*)operation delegate] == delegate) {
                if ([operation respondsToSelector:@selector(setDelegate:)]) {
                    [(id)operation setDelegate:nil];
                }
                [operation cancel];
                nbOps++;
            }
        }
    }
    if (nbOps > 0 && delegate) {
        CLSNSLog(@"-> All operations cancelled for delegate %@ (%d cancelled)", delegate, nbOps);
    }
}

- (void)cancelAllOperations {
    [self cancelOperationsForDelegate:nil];
}

#pragma mark - Thrift

- (id)thriftServiceClientInstance {
    Class thriftClientClass = NSClassFromString(self.thriftServiceClientClassName);
    if (!thriftClientClass) {
        return nil;
    }
    return [[thriftClientClass alloc] initWithProtocol:[self thriftProtocolInstance]];
}

- (id)thriftServiceClientInstanceWithCustomTimeoutInterval:(NSTimeInterval)timeoutInterval {
    Class thriftClientClass = NSClassFromString(self.thriftServiceClientClassName);
    if (!thriftClientClass) {
        return nil;
    }
    return [[thriftClientClass alloc] initWithProtocol:[self thriftProtocolInstanceWithCustomTimeoutInterval:timeoutInterval]];
}

- (id)thriftProtocolInstance {
    return [self thriftProtocolInstanceWithCustomTimeoutInterval:kDefaultThriftProtocolInstanceTimeoutInterval];
}

- (id)thriftProtocolInstanceWithCustomTimeoutInterval:(NSTimeInterval)timeoutInterval {
    THTTPClient* client = [[THTTPClient alloc] initWithURL:self.serviceURL userAgent:nil timeout:timeoutInterval];
    [self addSpecificHeadersToRequest:client.mRequest];
    return [[TBinaryProtocol alloc] initWithTransport:client strictRead:YES strictWrite:YES];
}

- (NSMutableURLRequest*)pcProxiedRequest {
    NSMutableURLRequest* request = [[NSMutableURLRequest alloc] initWithURL:self.serviceRawURL];
    [self addSpecificHeadersToRequest:request];
    return request;
}

#pragma mark - Private utils

- (void)addSpecificHeadersToRequest:(NSMutableURLRequest*)request {
    NSString* deviceToken = [PushNotifController notificationsDeviceToken];
    if (deviceToken) {
        [request setValue:deviceToken forHTTPHeaderField:@"X-PC-PUSHNOTIF-TOKEN"];
    }
    [request setValue:@"IOS" forHTTPHeaderField:@"X-PC-PUSHNOTIF-OS"];
    NSString* pcAuthSessionid = [[AuthenticationController sharedInstance] pocketCampusAuthSessionId];
    if (pcAuthSessionid) {
        [request setValue:pcAuthSessionid forHTTPHeaderField:@"X-PC-AUTH-PCSESSID"];
    }
    NSString* langCode = [PCUtils userLanguageCode];
    if (langCode) {
        [request setValue:langCode forHTTPHeaderField:@"X-PC-LANG-CODE"];
    }
}

#pragma mark - Dealloc

- (void)dealloc
{
    [self.operationQueue setSuspended:YES];
    [self cancelAllOperations];
    CLSNSLog(@"-> Service '%@' released", self.serviceName);
}

@end
