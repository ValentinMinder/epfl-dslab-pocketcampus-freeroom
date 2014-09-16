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

//  Created by Loïc Gardiol on 01.09.2014.
 
#import "CloudPrintService.h"

#import "AFNetworking.h"

static CloudPrintService* instance __weak = nil;

static NSString* const kCloudPrintServiceJobUniqueIdServiceRequestUserInfoKey = @"JobUniqueId";

static NSString* const kCloudPrintRawUploadJSONResponseDocumentIdKey = @"file_id";

static NSString* const kCloudPrintRawUploadFileParameterNameKey = @"file";

@interface CloudPrintService ()

@property (nonatomic, strong) AFHTTPSessionManager* filesUploadSessionManager;

@end

@implementation CloudPrintService

#pragma mark - Init

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"CloudPrintService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"cloudprint" thriftServiceClientClassName:NSStringFromClass(CloudPrintServiceClient.class)];
        if (self) {
            instance = self;
        }
        return self;
    }
}

#pragma mark - ServiceProtocol

+ (id)sharedInstanceToRetain {
    @synchronized (self) {
        if (instance) {
            return instance;
        }
        return [[[self class] alloc] init];
    }
}

#pragma mark - Service methods

- (void)printDocumentWithRequest:(PrintDocumentRequest*)request delegate:(id<CloudPrintServiceDelegate>)delegate {
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.userInfo = @{kCloudPrintServiceJobUniqueIdServiceRequestUserInfoKey:request.jobUniqueId};
    operation.serviceClientSelector = @selector(printDocument:);
    operation.delegateDidReturnSelector = @selector(printDocumentForRequest:didReturn:);
    operation.delegateDidFailSelector = @selector(printDocumentFailedForRequest:);
    [operation addObjectArgument:request];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

#pragma mark - Misc

- (void)uploadForPrintDocumentWithLocalURL:(NSURL*)localURL jobUniqueId:(NSString*)jobUniqueId success:(void (^)(int64_t documentId))success progress:(NSProgress* __autoreleasing*)progress failure:(void (^)(CloudPrintUploadFailureReason failureReason))failure {
    
    [PCUtils throwExceptionIfObject:localURL notKindOfClass:[NSURL class]];
    
    if (!self.filesUploadSessionManager) {
        self.filesUploadSessionManager = [[AFHTTPSessionManager alloc] initWithSessionConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];
        self.filesUploadSessionManager.responseSerializer = [AFJSONResponseSerializer serializer];
    }
    
    NSMutableURLRequest* rawRequest = [self pcProxiedRequest];
    
    NSError* error = nil;
    NSMutableURLRequest* finalRequest = [[self.filesUploadSessionManager requestSerializer] multipartFormRequestWithMethod:@"POST" URLString:rawRequest.URL.absoluteString parameters:nil constructingBodyWithBlock:^(id<AFMultipartFormData> formData) {
        [formData appendPartWithFileURL:localURL name:kCloudPrintRawUploadFileParameterNameKey fileName:[localURL lastPathComponent] mimeType:@"application/pdf" error:nil];
    } error:&error];
    
    if (error) {
        failure(CloudPrintUploadFailureReasonUnknown);
        return;
    }
    
    NSMutableDictionary* allHeaders = [finalRequest.allHTTPHeaderFields mutableCopy] ?: [NSMutableDictionary dictionary];
    for (NSString* key in rawRequest.allHTTPHeaderFields) {
        allHeaders[key] = rawRequest.allHTTPHeaderFields[key];
    }
    finalRequest.allHTTPHeaderFields = allHeaders;
    finalRequest.cachePolicy = NSURLRequestReloadIgnoringCacheData;
    
    NSURLSessionUploadTask* uploadTask = [self.filesUploadSessionManager uploadTaskWithStreamedRequest:finalRequest progress:progress completionHandler:^(NSURLResponse *response, id responseObject, NSError *error) {
        if (error.code == NSURLErrorCancelled) {
            return;
        }
        
        if (![response isKindOfClass:[NSHTTPURLResponse class]]) {
            failure(CloudPrintUploadFailureReasonNetworkError);
            return;
        }
        
        NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
        if (error && httpResponse.statusCode == 0) {
            failure(CloudPrintUploadFailureReasonUnknown);
        } else {
            switch (httpResponse.statusCode) {
                case 200:
                {
                    NSDictionary* responseDic = (NSDictionary*)responseObject;
                    if (![responseDic isKindOfClass:[NSDictionary class]]) {
                        failure(CloudPrintUploadFailureReasonUnknown);
                    }
                    NSString* documentIdString = responseDic[kCloudPrintRawUploadJSONResponseDocumentIdKey];
                    if (!documentIdString) {
                        failure(CloudPrintUploadFailureReasonUnknown);
                    }
                    int64_t documentId = [documentIdString longLongValue];
                    if (documentId <= 0) {
                        failure(CloudPrintUploadFailureReasonUnknown);
                    }
                    success(documentId);
                    break;
                }
                case 407:
                {
                    failure(CloudPrintUploadFailureReasonAuthenticationError);
                    break;
                }
                default:
                    failure(CloudPrintUploadFailureReasonUnknown);
                    break;
            }
            
        }
    }];
    
    uploadTask.taskDescription = jobUniqueId;
    [uploadTask resume];
}

- (void)cancelJobsWithUniqueId:(NSString*)jobUniqueId {
    if (!jobUniqueId) {
        return;
    }
    for (NSOperation* operation in self.operationQueue.operations) {
        if ([operation isKindOfClass:[PCServiceRequest class]]) {
            NSString* opJobUniqueId = [(PCServiceRequest*)operation userInfo][kCloudPrintServiceJobUniqueIdServiceRequestUserInfoKey];
            if (opJobUniqueId && [opJobUniqueId isEqualToString:jobUniqueId]) {
                [operation cancel];
                NSLog(@"-> Cancelled CloudPrint job service request operation (%@)", jobUniqueId);
            }
        }
    }
    for (NSURLSessionUploadTask* task in self.filesUploadSessionManager.uploadTasks) {
        if ([task.taskDescription isEqualToString:jobUniqueId]) {
            [task cancel];
            NSLog(@"-> Cancelled CloudPrint job upload task operation (%@)", jobUniqueId);
        }
    }
}

#pragma mark - Dealloc

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
}

@end
