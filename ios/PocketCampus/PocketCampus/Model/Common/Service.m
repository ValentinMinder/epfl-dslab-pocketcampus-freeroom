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


static const NSTimeInterval kDefaultThriftProtocolInstanceTimeoutInterval = 20.0;

@interface Service ()

@property (nonatomic, readwrite, strong) NSString* serviceName;
@property (nonatomic, readwrite, strong) NSString* thriftServiceClientClassName;
@property (nonatomic, readwrite, strong) NSURL* serviceURL;
@property (nonatomic, readwrite, strong) NSOperationQueue* operationQueue;

@end

@implementation Service

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
        self.serviceURL = [self.class serviceURLforServiceName:serviceName];
        NSLog(@"-> Initializing service '%@' with URL (%@)", serviceName, self.serviceURL.absoluteString);
        self.operationQueue = [NSOperationQueue new];
        self.operationQueue.maxConcurrentOperationCount = NSOperationQueueDefaultMaxConcurrentOperationCount;
    }
    return self;
}

#pragma mark - Config

+ (NSURL*)serviceURLforServiceName:(NSString*)serviceName {
    static NSURL* kServerURL;
    static NSString* kVersionURI;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        NSUserDefaults* defaults = [PCConfig defaults];
        NSString* serverProt = [defaults objectForKey:PC_CONFIG_SERVER_PROTOCOL_KEY];
        NSString* serverAddress = [defaults objectForKey:PC_CONFIG_SERVER_ADDRESS_KEY];
        NSString* serverPort = [defaults objectForKey:PC_CONFIG_SERVER_PORT_KEY];
        NSString* versionURI = [defaults objectForKey:PC_CONFIG_SERVER_URI_KEY];
        kServerURL = [NSURL URLWithString:[NSString stringWithFormat:@"%@://%@:%@", serverProt, serverAddress, serverPort]];
        kVersionURI = versionURI;
    });
    return [NSURL URLWithString:[kVersionURI stringByAppendingPathComponent:serviceName] relativeToURL:kServerURL];
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
