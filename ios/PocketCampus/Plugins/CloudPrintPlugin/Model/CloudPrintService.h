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

@import Foundation;

#import "PCService.h"

#import "CloudPrintModelAdditions.h"

typedef NS_ENUM(NSInteger, CloudPrintUploadFailureReason) {
    CloudPrintUploadFailureReasonAuthenticationError,
    CloudPrintUploadFailureReasonNetworkError,
    CloudPrintUploadFailureReasonUnknown
};

@protocol CloudPrintServiceDelegate <PCServiceDelegate>

@optional

- (void)printDocumentForRequest:(PrintDocumentRequest*)request didReturn:(PrintDocumentResponse*)response;
- (void)printDocumentFailedForRequest:(PrintDocumentRequest*)request;

@end

@interface CloudPrintService : PCService <PCServiceProtocol>

/*
 * Thrift service methods
 *
 * - (PrintDocumentResponse *) printDocument: (PrintDocumentRequest *) request;  // throws TException
 */

- (void)printDocumentWithRequest:(PrintDocumentRequest*)request delegate:(id<CloudPrintServiceDelegate>)delegate;

/**
 * @param localURL must be the URL of a readable local file. Cannot be nil.
 * @param jobUniqueId this parameter is optional, it's there only to give the opportunity to cancel it afterwards using cancelJobsWithUniqueId:
 * @param success executed when upload is successfull. documentId can be then used in PrintDocumentRequest to trigger the request.
 * @param progress [0.0, 1.0] regularly executed with new progress of operation
 * @param failure executed when the operation fails.
 */
- (void)uploadForPrintDocumentWithLocalURL:(NSURL*)localURL jobUniqueId:(NSString*)jobUniqueId success:(void (^)(int64_t documentId))success progress:(NSProgress*)progress failure:(void (^)(CloudPrintUploadFailureReason failureReason))failure;

/**
 * @discussion Loops through self.operationQueue.operations and cancels all operations
 * with userInfo key-value corresponding to jobUniqueId
 */
- (void)cancelJobsWithUniqueId:(NSString*)jobUniqueId;

@end


