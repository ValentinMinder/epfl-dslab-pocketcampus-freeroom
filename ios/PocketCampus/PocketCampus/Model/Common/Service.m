//
//  Service.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "Service.h"

#import "PushNotifController.h"

#import "THTTPClient.h"
#import "TBinaryProtocol.h"


static NSTimeInterval kDefaultThriftProtocolInstanceTimeoutInterval = 15.0;

@interface Service ()

@property (nonatomic, readwrite, strong) NSString* serviceName;
@property (nonatomic, strong) NSString* serverAddressWithPort; //example 128.178.0.1:9090
@property (nonatomic, strong) NSURL* serverURL; //full URL with service extension (example ../v3r1/transport/)
@property (nonatomic, strong) TBinaryProtocol* thriftProtocol;
@property (nonatomic, strong) NSOperationQueue* operationQueue;
@property (nonatomic, strong) id thriftClient;

@end

@implementation Service

#pragma mark - Init

- (id)initWithServiceName:(NSString*)serviceName {
    [PCUtils throwExceptionIfObject:serviceName notKindOfClass:[NSString class]];
    self = [super init];
    if (self) {
        self.serviceName = serviceName;
        
        NSUserDefaults* defaults = [PCConfig defaults];

        NSString* serverProto = [defaults objectForKey:PC_CONFIG_SERVER_PROTOCOL_KEY];
        NSString* serverAddress = [defaults objectForKey:PC_CONFIG_SERVER_ADDRESS_KEY];
        NSString* serverPort = [defaults objectForKey:PC_CONFIG_SERVER_PORT_KEY];
        NSString* serverVersion = [defaults objectForKey:PC_CONFIG_SERVER_URI_KEY];
        
        self.serverAddressWithPort = [NSString stringWithFormat:@"%@://%@:%@", serverProto, serverAddress, serverPort];
        NSString* serviceURLString = [NSString stringWithFormat:@"%@/%@/%@", self.serverAddressWithPort, serverVersion, serviceName];
        NSLog(@"-> Initializing service '%@' on server (%@)", serviceName, serviceURLString);
        self.serverURL = [NSURL URLWithString:serviceURLString];
        self.operationQueue = [NSOperationQueue new];
    }
    return self;
}


#pragma mark - Cancelling operations

//pass nil to cancel all operations
- (void)cancelOperationsForDelegate:(id<ServiceDelegate>)delegate {
    int nbOps = 0;
    for (NSOperation* operation in self.operationQueue.operations) {
        if (!delegate || ([operation respondsToSelector:@selector(delegate)])) {
            if ([(id)operation delegate] == delegate) {
                if ([operation respondsToSelector:@selector(setDelegate:)]) {
                    [(id)operation setDelegate:nil];
                }
                [operation cancel];
                nbOps++;
            }
        }
    }
    if (nbOps > 0 && delegate) {
        NSLog(@"-> All operations canceled for delegate %@ (%d cancelled)", delegate, nbOps);
    }
}

- (void)cancelAllOperations {
    [self cancelOperationsForDelegate:nil];
}

#pragma mark - Thrift

- (id)thriftProtocolInstance {
    return [self thriftProtocolInstanceWithCustomTimeoutInterval:kDefaultThriftProtocolInstanceTimeoutInterval];
}

- (id)thriftProtocolInstanceWithCustomTimeoutInterval:(NSTimeInterval)timeoutInterval {
    THTTPClient* client = [[THTTPClient alloc] initWithURL:self.serverURL userAgent:nil timeout:timeoutInterval];
    NSString* deviceToken = [PushNotifController notificationsDeviceToken];
    if (deviceToken) {
        [client->mRequest setValue:@"IOS" forHTTPHeaderField:@"X-PC-PUSHNOTIF-OS"];
        [client->mRequest setValue:deviceToken forHTTPHeaderField:@"X-PC-PUSHNOTIF-TOKEN"];
    }
    return [[TBinaryProtocol alloc] initWithTransport:client strictRead:YES strictWrite:YES];
}

#pragma mark - Dealloc

- (void)dealloc
{
    [self.operationQueue setSuspended:YES];
    [self cancelAllOperations];
    NSLog(@"-> Service '%@' released", self.serviceName);
}

@end
