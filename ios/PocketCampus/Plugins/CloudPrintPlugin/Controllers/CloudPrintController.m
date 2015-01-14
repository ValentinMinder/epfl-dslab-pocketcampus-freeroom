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

#import "CloudPrintController.h"

@import MobileCoreServices;

#import "CloudPrintRequestViewController.h"

#import "CloudPrintStatusViewController.h"

#import "CloudPrintService.h"

#import "AuthenticationController.h"

#import <AFNetworking/AFNetworking.h>

@interface CloudPrintJob : NSObject

@property (nonatomic, strong) PrintDocumentRequest* request;
@property (nonatomic, copy) NSString* docName;
@property (nonatomic, copy) NSURL* documentURL;
@property (nonatomic, copy) NSURL* documentLocalURL;
@property (nonatomic) NSURLSessionDownloadTask* documentDownloadTask;
@property (nonatomic, copy) CloudPrintCompletionBlock completion;

@property (nonatomic, strong) CloudPrintService* cloudPrintService;

@property (nonatomic, strong) PCNavigationController* navController;
@property (nonatomic, strong) CloudPrintStatusViewController* preRequestStatusViewController;
@property (nonatomic, strong) CloudPrintRequestViewController* requestViewController;
@property (nonatomic, strong) CloudPrintStatusViewController* postRequestStatusViewController;

@property (nonatomic, strong) NSTimer* pushCurrentViewControllerTimer;

@property (nonatomic, weak) UIViewController* currentViewController;

- (void)setCurrentViewController:(UIViewController*)currentViewController animated:(BOOL)animated;

@end

@implementation CloudPrintJob

- (CloudPrintService*)cloudPrintService {
    if (!_cloudPrintService) {
        _cloudPrintService = [CloudPrintService sharedInstanceToRetain];
    }
    return _cloudPrintService;
}

- (PCNavigationController*)navController {
    if (!_navController) {
        _navController = [[PCNavigationController alloc] initWithRootViewController:self.preRequestStatusViewController];
        _navController.preferredContentSize = CGSizeMake(360.0, 620.0);
    }
    return _navController;
}

- (CloudPrintStatusViewController*)preRequestStatusViewController {
    if (!_preRequestStatusViewController) {
        _preRequestStatusViewController = [CloudPrintStatusViewController new];
    }
    return _preRequestStatusViewController;
}

- (CloudPrintRequestViewController*)requestViewController {
    if (!_requestViewController) {
        _requestViewController = [CloudPrintRequestViewController new];
    }
    return _requestViewController;
}

- (CloudPrintStatusViewController*)postRequestStatusViewController {
    if (!_postRequestStatusViewController) {
        _postRequestStatusViewController = [CloudPrintStatusViewController new];
    }
    return _postRequestStatusViewController;
}

- (UIViewController*)currentViewController {
    return self.navController.topViewController;
}

- (void)setCurrentViewController:(UIViewController *)currentViewController {
    [self setCurrentViewController:currentViewController animated:NO];
}

- (void)setCurrentViewController:(UIViewController *)newCurrentViewController animated:(BOOL)animated {
    if (newCurrentViewController == self.currentViewController) {
        return;
    }
    
    NSArray* viewControllers = nil;
    if (newCurrentViewController == self.preRequestStatusViewController) {
        viewControllers = @[self.preRequestStatusViewController];
    } else if (newCurrentViewController == self.requestViewController) {
        viewControllers = @[self.preRequestStatusViewController, self.requestViewController];
    } else if (newCurrentViewController == self.postRequestStatusViewController) {
        viewControllers = @[self.preRequestStatusViewController, self.requestViewController, self.postRequestStatusViewController];
    } else {
        //unsupported view controller
        return;
    }
    
    if ([PCUtils isOSVersionSmallerThan:8.0]) {
        // iOS 7 does not like when pushing/setting view controllers
        // multiple times before animations are finished.
        // So we need to wait and replace push/set operations.
        NSTimeInterval interval = self.pushCurrentViewControllerTimer ? 1.0 : 0.0;
        [self.pushCurrentViewControllerTimer invalidate];
        __weak __typeof(self) welf = self;
        self.pushCurrentViewControllerTimer = [NSTimer scheduledTimerWithTimeInterval:interval block:^{
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                welf.pushCurrentViewControllerTimer = nil;
            });
            [welf.navController setViewControllers:viewControllers animated:animated];
        } repeats:NO];
    } else {
        [self.navController setViewControllers:viewControllers animated:animated];
    }
}

@end

static CloudPrintController* instance __strong = nil;

static float const kSendToPrinterProgressStart = 80;
static float const kProgressMax = 100;

@interface CloudPrintController ()<CloudPrintServiceDelegate>

@property (nonatomic, strong) AFHTTPSessionManager* documentsDownloadSessionManager;
@property (nonatomic, strong) NSMutableDictionary* jobForJobUniqueId;

@end

@implementation CloudPrintController

#pragma mark - Init

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"CloudPrintController cannot be instancied more than once at a time, use sharedInstance instead." userInfo:nil];
        }
        self = [super init];
        if (self) {
            instance = self;
            instance.jobForJobUniqueId = [NSMutableDictionary dictionary];
        }
        return self;
    }
}

+ (instancetype)sharedInstance {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

#pragma mark - PluginController

+ (id)sharedInstanceToRetain {
    return [self sharedInstance];
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"CloudPrintPlugin", @"");
}

+ (NSString*)identifierName {
    return @"CloudPrint";
}

#pragma mark - Public
    
+ (BOOL)isSupportedFileWithLocalURL:(NSURL*)localURL {
    if (!localURL) {
        return NO;
    }
    NSString* path = localURL.path;
    if (![[NSFileManager defaultManager] fileExistsAtPath:path]) {
        return NO;
    }

    // Method 1: try to find the "%PDF" header in the file
    NSError* error = nil;
    NSFileHandle* fileHandle = [NSFileHandle fileHandleForReadingFromURL:localURL error:&error];
    if (error) {
        return NO;
    }
    NSData* data = [fileHandle readDataOfLength:8]; //will read up to length or end of file if file size is smaller than length (see doc)
    NSString* string = [[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding];
    if (string.length > 0) {
        return ([string rangeOfString:@"%PDF"].location != NSNotFound);
    }
    
    // Method 2 if method 1 failed: rely on file extension
    CFStringRef UTI = UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension, (__bridge CFStringRef)[path pathExtension], NULL);
    if (!UTI) {
        return NO;
    }
    CFStringRef mimeType = UTTypeCopyPreferredTagWithClass (UTI, kUTTagClassMIMEType);
    CFRelease(UTI);
    if (!mimeType) {
        return NO;
    }
    BOOL isSupported = NO;
    if ([(__bridge NSString*)mimeType isEqualToString:@"application/pdf"]) {
        isSupported = YES;
    }
    CFRelease(mimeType);
    return isSupported;
}

- (UIViewController*)viewControllerForPrintDocumentWithURL:(NSURL*)url docName:(NSString*)docName printDocumentRequestOrNil:(PrintDocumentRequest*)requestOrNil completion:(CloudPrintCompletionBlock)completion {
    [PCUtils throwExceptionIfObject:url notKindOfClass:[NSURL class]];
    
    CloudPrintJob* job = [CloudPrintJob new];
    job.request = requestOrNil ?: [PrintDocumentRequest createDefaultRequest];
    job.docName = docName;
    job.completion = completion;
    
    if ([url isFileURL]) {
        if (![self.class isSupportedFileWithLocalURL:url]) {
            if (completion) {
                completion(CloudPrintCompletionStatusCodeUnsupportedFile);
                return nil;
            }
        }
        job.documentLocalURL = url;
    } else {
        job.documentURL = url;
    }
    
    self.jobForJobUniqueId[job.request.jobUniqueId] = job;
    
    [self handleJob:job];
    
    return job.navController;
}

- (UIViewController*)viewControllerForPrintWithDocumentName:(NSString*)docName printDocumentRequest:(PrintDocumentRequest*)request completion:(CloudPrintCompletionBlock)completion {
    [PCUtils throwExceptionIfObject:request notKindOfClass:[PrintDocumentRequest class]];
    if (request.documentId == 0) {
        [NSException raise:@"Illegal argument" format:@"request.documentId cannot be 0"];
    }
    
    CloudPrintJob* job = [CloudPrintJob new];
    job.request = request;
    job.docName = docName;
    job.completion = completion;
    self.jobForJobUniqueId[request.jobUniqueId] = job;
    
    [self handleJob:job];
    
    return job.navController;
}

- (void)cancelPrintWithViewController:(UIViewController*)viewController {
    for (NSString* jobUniqueId in [self.jobForJobUniqueId copy]) {
        CloudPrintJob* job = self.jobForJobUniqueId[jobUniqueId];
        if (job.navController == viewController) {
            [self cleanJob:job];
        }
    }
}

#pragma mark - CloudPrintService

- (void)printDocumentForRequest:(PrintDocumentRequest *)request didReturn:(PrintDocumentResponse *)response {
    CloudPrintJob* job = self.jobForJobUniqueId[request.jobUniqueId];
    if (!job) {
        NSLog(@"!! ERROR: could not find job in printDocumentForRequest:didReturn for job id: %@. Returning.", request.jobUniqueId);
        return;
    }
    
    switch (response.statusCode) {
        case CloudPrintStatusCode_OK:
        {
            job.postRequestStatusViewController.userCancelledBlock = nil; //hide Cancel button
            job.postRequestStatusViewController.progress.completedUnitCount = kProgressMax;
            job.postRequestStatusViewController.statusMessage = CloudPrintStatusMessageSuccess;
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.75 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{ //give time for success message and animation
                [self job:job completedWithStatusCode:CloudPrintCompletionStatusCodePrintSuccess];
            });
            break;
        }
        case CloudPrintStatusCode_AUTHENTICATION_ERROR:
        {
            __weak __typeof(self) welf = self;
            __weak __typeof(job) wjob = job;
            [[AuthenticationController sharedInstance] addLoginObserver:wjob success:^{
                wjob.requestViewController.userValidatedRequestBlock(wjob.request);
            } userCancelled:^{
                [welf showErrorAlertWithMessage:NSLocalizedStringFromTable(@"LoginInAppRequired", @"PocketCampus", nil) forJob:wjob];
                [welf handleJob:wjob];
            } failure:^(NSError *error) {
                if (error.code == kAuthenticationErrorCodeCouldNotAskForCredentials) {
                    [welf showErrorAlertWithMessage:NSLocalizedStringFromTable(@"LoginInAppRequired", @"PocketCampus", nil) forJob:wjob];
                } else {
                    [welf showErrorAlertWithMessage:NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil) forJob:wjob];
                }
                [welf handleJob:wjob];
            }];
            break;
        }
        case CloudPrintStatusCode_PRINT_ERROR:
            [self showErrorAlertWithMessage:NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil) forJob:job];
            [self handleJob:job];
            break;
        default:
            break;
    }
}

- (void)printDocumentFailedForRequest:(PrintDocumentRequest *)request {
    CloudPrintJob* job = self.jobForJobUniqueId[request.jobUniqueId];
    if (!job) {
        NSLog(@"!! ERROR: could not find job in printDocumentFailedForRequest: for job id: %@. Returning.", request.jobUniqueId);
        return;
    }
    [self showErrorAlertWithMessage:NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil) forJob:job];
    [self handleJob:job];
}

- (void)serviceConnectionToServerFailed {
    for (CloudPrintJob* job in self.jobForJobUniqueId.allValues) {
        [self showErrorAlertWithMessage:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutAlert", @"PocketCampus", nil) forJob:job];
        [job setCurrentViewController:job.requestViewController animated:YES];
    }
}

#pragma mark - Private

- (void)handleJob:(CloudPrintJob*)job {
    
    __weak __typeof(self) welf = self;
    __weak __typeof(job) wjob = job;

    // Generic
    
    [wjob.preRequestStatusViewController setUserCancelledBlock:^{
        [wjob.documentDownloadTask cancel];
        [wjob.cloudPrintService cancelJobsWithUniqueId:wjob.request.jobUniqueId];
        [welf job:wjob completedWithStatusCode:CloudPrintCompletionStatusCodeUserCancelled];
    }];
    
    // Phase 1: download document if needed
    
    if (wjob.documentURL && !wjob.documentLocalURL) {
        // Document needs to be downloaded first
        if (wjob.documentDownloadTask) {
            return; // Already started
        }
        
        NSProgress* progress;
        [welf downloadDocumentForJob:wjob progress:&progress completion:^(NSError *error) {
            wjob.documentDownloadTask = nil;
            if (error) {
                [welf showErrorAlertWithMessage:error.localizedDescription forJob:wjob];
                wjob.preRequestStatusViewController.statusMessage = CloudPrintStatusMessageError;
                [wjob.preRequestStatusViewController setShowTryAgainButtonWithTappedBlock:^{
                    [welf handleJob:wjob];
                }];
                return;
            }
            [welf handleJob:wjob];
        }];
        wjob.preRequestStatusViewController.progress = progress;
        wjob.preRequestStatusViewController.documentName = wjob.docName ?: wjob.documentURL.absoluteString;
        wjob.preRequestStatusViewController.showTryAgainButtonWithTappedBlock = nil;
        wjob.preRequestStatusViewController.statusMessage = CloudPrintStatusMessageDownloadingFile;
        wjob.currentViewController = wjob.preRequestStatusViewController;
        
        return;
    }
    
    
    // Phase 2: upload document if needed
    
    if (wjob.request.documentIdIsDefault) {
        CloudPrintJobUploadStatus uploadStatus = [wjob.cloudPrintService uploadStatusForJobWithUniqueId:wjob.request.jobUniqueId];
        if (uploadStatus == CloudPrintJobUploadStatusUploading) {
            // upload already started
            return;
        }
        if (uploadStatus == CloudPrintJobUploadStatusUploaded) {
            [NSException raise:@"Illegal state" format:@"Document upload status is uploaded, yet its documentId has default value."];
        }
        if (!wjob.documentLocalURL) {
            [NSException raise:@"Illegal state" format:@"Before being uploaded, document needs to be stored locally and documentLocalURL must be set"];
        }
        if (![self.class isSupportedFileWithLocalURL:wjob.documentLocalURL]) {
            [welf job:wjob completedWithStatusCode:CloudPrintCompletionStatusCodeUnsupportedFile];
            return;
        }
        
        NSProgress* progress;
        [wjob.cloudPrintService uploadForPrintDocumentWithLocalURL:wjob.documentLocalURL desiredFilename:wjob.docName ?: [wjob.documentURL lastPathComponent] jobUniqueId:wjob.request.jobUniqueId success:^(int64_t documentId) {
            wjob.request.documentId = documentId;
            
            [welf handleJob:wjob];
        } progress:&progress failure:^(CloudPrintUploadFailureReason failureReason) {
            switch (failureReason) {
                case CloudPrintUploadFailureReasonAuthenticationError:
                {
                    [[AuthenticationController sharedInstance] addLoginObserver:wjob success:^{
                        [welf handleJob:wjob];
                    } userCancelled:^{
                        [wjob.cloudPrintService cancelJobsWithUniqueId:wjob.request.jobUniqueId];
                        [welf job:wjob completedWithStatusCode:CloudPrintCompletionStatusCodeUserCancelled];
                    } failure:^(NSError *error){
                        if (error.code == kAuthenticationErrorCodeCouldNotAskForCredentials) {
                            [welf showErrorAlertWithMessage:NSLocalizedStringFromTable(@"LoginInAppRequired", @"PocketCampus", nil) forJob:wjob];
                        } else {
                            [welf showErrorAlertWithMessage:NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil) forJob:wjob];
                        }
                        wjob.preRequestStatusViewController.statusMessage = CloudPrintStatusMessageError;
                        [wjob.preRequestStatusViewController setShowTryAgainButtonWithTappedBlock:^{
                            [welf handleJob:wjob];
                        }];
                    }];
                    break;
                }
                case CloudPrintUploadFailureReasonNetworkError:
                {
                    [welf showErrorAlertWithMessage:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutAlert", @"PocketCampus", nil) forJob:wjob];
                    wjob.preRequestStatusViewController.statusMessage = CloudPrintStatusMessageError;
                    [wjob.preRequestStatusViewController setShowTryAgainButtonWithTappedBlock:^{
                        [welf handleJob:wjob];
                    }];
                    break;
                }
                default:
                {
                    [welf showErrorAlertWithMessage:NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil) forJob:wjob];
                    wjob.preRequestStatusViewController.statusMessage = CloudPrintStatusMessageError;
                    [wjob.preRequestStatusViewController setShowTryAgainButtonWithTappedBlock:^{
                        [welf handleJob:wjob];
                    }];
                    break;
                }
            }
        }];
        
        wjob.preRequestStatusViewController.progress = progress;
        wjob.preRequestStatusViewController.documentName = wjob.docName ?: wjob.documentLocalURL.lastPathComponent;
        wjob.preRequestStatusViewController.showTryAgainButtonWithTappedBlock = nil;
        wjob.preRequestStatusViewController.statusMessage = CloudPrintStatusMessageUploadingFile;
        wjob.currentViewController = wjob.preRequestStatusViewController;
        
        return;
    }
    
    
    // Phase 3: document is now known by server
    
    [wjob.cloudPrintService cancelJobsWithUniqueId:wjob.request.jobUniqueId];

    wjob.requestViewController.documentName = wjob.docName ?: wjob.documentLocalURL.lastPathComponent;
    wjob.requestViewController.printRequest = wjob.request;
    [wjob.requestViewController setUserCancelledBlock:^{
        [wjob.cloudPrintService cancelJobsWithUniqueId:wjob.request.jobUniqueId];
        [welf job:wjob completedWithStatusCode:CloudPrintCompletionStatusCodeUserCancelled];
    }];
    [wjob.requestViewController setUserValidatedRequestBlock:^(PrintDocumentRequest* request) {
        wjob.postRequestStatusViewController.documentName = wjob.docName ?: wjob.documentLocalURL.lastPathComponent;
        wjob.postRequestStatusViewController.showTryAgainButtonWithTappedBlock = nil;
        wjob.postRequestStatusViewController.statusMessage = CloudPrintStatusMessageSendingToPrinter;
        wjob.postRequestStatusViewController.progress = [NSProgress progressWithTotalUnitCount:kProgressMax];
        wjob.postRequestStatusViewController.progress.completedUnitCount = kSendToPrinterProgressStart;
        
        [wjob.postRequestStatusViewController setUserCancelledBlock:^{
            [wjob.cloudPrintService cancelJobsWithUniqueId:wjob.request.jobUniqueId];
            [wjob setCurrentViewController:wjob.requestViewController animated:YES];
        }];
        
        [wjob setCurrentViewController:wjob.postRequestStatusViewController animated:YES];

        [wjob.cloudPrintService printDocumentWithRequest:wjob.request delegate:welf];
    }];
    
    [wjob setCurrentViewController:wjob.requestViewController animated:YES];
    
}

- (void)job:(CloudPrintJob*)job completedWithStatusCode:(CloudPrintCompletionStatusCode)statusCode {
    @synchronized (self) {
        [self cleanJob:job];
        if (job.completion) {
            job.completion(statusCode);
        }
    }
}

- (void)cleanJob:(CloudPrintJob*)job {
    [[AuthenticationController sharedInstance] removeLoginObserver:job];
    [self deleteIfNecessaryDownloadedDocumentForJob:job];
    if (job.request.jobUniqueId) {
        [self.jobForJobUniqueId removeObjectForKey:job.request.jobUniqueId];
    }
}

/**
 * Downloads the document pointed by job.documentURL (cancels and start new request if exists)
 * Sets job.documentDownloadOperation.
 * At end download, if no error, job.documentLocalURL is set.
 */
- (void)downloadDocumentForJob:(CloudPrintJob*)job progress:(NSProgress* __autoreleasing*)progress completion:(void (^)(NSError* error))completion {
    [job.documentDownloadTask cancel];
    [PCUtils throwExceptionIfObject:job.documentURL notKindOfClass:[NSURL class]];
    
    if (!self.documentsDownloadSessionManager) {
        self.documentsDownloadSessionManager = [[AFHTTPSessionManager alloc] initWithSessionConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];
    }
    
    NSMutableURLRequest* request = [[NSURLRequest requestWithURL:job.documentURL] mutableCopy];
    
    request.allHTTPHeaderFields = [NSHTTPCookie requestHeaderFieldsWithCookies:[NSHTTPCookieStorage sharedHTTPCookieStorage].cookies];
    
    NSString* filename = [NSString stringWithFormat:@"%@-%@", job.request.jobUniqueId, request.URL.lastPathComponent];
    NSString* finalPath = [NSTemporaryDirectory() stringByAppendingPathComponent:filename];
    
    __weak __typeof(job) wjob = job;
    job.documentDownloadTask = [self.documentsDownloadSessionManager downloadTaskWithRequest:request progress:progress destination:^NSURL *(NSURL *targetPath, NSURLResponse *response) {
        return [NSURL fileURLWithPath:finalPath];
    } completionHandler:^(NSURLResponse *response, NSURL *filePath, NSError *error) {
        if (error) {
            wjob.documentLocalURL = nil;
        } else {
            wjob.documentLocalURL = filePath;
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            completion(error);
        });
    }];
    
    [job.documentDownloadTask resume];
}

- (void)deleteIfNecessaryDownloadedDocumentForJob:(CloudPrintJob*)job {
    if (job.documentURL && job.documentLocalURL) { //means document was downloaded
        if (![[NSFileManager defaultManager] fileExistsAtPath:job.documentLocalURL.path]) {
            return;
        }
        NSError* error = nil;
        [[NSFileManager defaultManager] removeItemAtURL:job.documentLocalURL error:&error];
        if (error) {
            CLSNSLog(@"-> ERROR CloudPrint job completed but could not delete downloaded file. Ignoring. (file: %@ error: %@)", job.documentLocalURL, error);
        }
    }
}

- (void)showErrorAlertWithMessage:(NSString*)message forJob:(CloudPrintJob*)job {
    if ([UIAlertController class]) {
        UIAlertController* alertController = [UIAlertController alertControllerWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:message preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction* okAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleCancel handler:NULL];
        [alertController addAction:okAction];
        [job.navController presentViewController:alertController animated:YES completion:NULL];
    } else {
#ifndef TARGET_IS_EXTENSION
        [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
#endif
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
