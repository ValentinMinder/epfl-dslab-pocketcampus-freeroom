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

@interface CloudPrintJob : NSObject

@property (nonatomic, strong) PrintDocumentRequest* request;
@property (nonatomic, copy) NSString* docName;
@property (nonatomic, copy) NSURL* documentURL;
@property (nonatomic, copy) NSURL* documentLocalURL;
@property (nonatomic) BOOL isDownloadingDocument;
@property (nonatomic, copy) CloudPrintCompletionBlock completion;

@property (nonatomic, strong) CloudPrintService* cloudPrintService;

@property (nonatomic, strong) PCNavigationController* navController;
@property (nonatomic, strong) CloudPrintStatusViewController* preRequestStatusViewController;
@property (nonatomic, strong) CloudPrintRequestViewController* requestViewController;
@property (nonatomic, strong) CloudPrintStatusViewController* postRequestStatusViewController;

@property (nonatomic, weak) UIViewController* currentViewController;

- (void)setCurrentViewController:(UIViewController *)currentViewController animated:(BOOL)animated;

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
    if (!self.navController) {
        self.navController = [[PCNavigationController alloc] initWithRootViewController:self.preRequestStatusViewController];
    }
    if (newCurrentViewController.navigationController) {
        [self.navController popToViewController:newCurrentViewController animated:animated];
    } else {
        if (newCurrentViewController == self.requestViewController) {
            [self.navController pushViewController:self.requestViewController animated:animated];
        } else if (newCurrentViewController == self.postRequestStatusViewController) {
            if (!self.requestViewController.navigationController) {
                [self.navController pushViewController:self.requestViewController animated:NO];
            }
            [self.navController pushViewController:self.postRequestStatusViewController animated:animated];
        } else {
            //unsupported view controller
        }
    }
}

@end

static CloudPrintController* instance __strong = nil;

static float const kUploadFileProgressStart = 5;
static float const kSendToPrinterProgressStart = 80;
static float const kProgressMax = 100;

@interface CloudPrintController ()<CloudPrintServiceDelegate>

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
    NSString* path = localURL.path;
    if (![[NSFileManager defaultManager] fileExistsAtPath:path]) {
        return NO;
    }

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

- (UIViewController*)viewControllerForPrintDocumentWithURL:(NSURL*)url docName:(NSString*)docName printDocumentRequestOrNil:(PrintDocumentRequest*)request completion:(CloudPrintCompletionBlock)completion {
    
    [PCUtils throwExceptionIfObject:localURL notKindOfClass:[NSURL class]];
    
    PrintDocumentRequest* request = requestOrNil ?: [PrintDocumentRequest createDefaultRequest];
    request.documentId = -1; //otherwise viewControllerForPrintWithDocumentName: complains...
    UIViewController* viewController = [self viewControllerForPrintWithDocumentName:docName printDocumentRequest:request completion:completion]; //will create the job and add it to jobForUniqueId
    
    CloudPrintJob* job = self.jobForJobUniqueId[request.jobUniqueId];
    
    __weak __typeof(job) wjob = job;
    __weak __typeof(self) welf = self;
    
    [job.requestViewController setUserValidatedRequestBlock:^(PrintDocumentRequest* request) {
        if (!wjob.statusViewController) {
            wjob.statusViewController = [CloudPrintStatusViewController new];
            
            [wjob.statusViewController setUserCancelledBlock:^{
                [welf.cloudPrintService cancelJobsWithUniqueId:wjob.request.jobUniqueId];
                [wjob.navController popToViewController:wjob.requestViewController animated:YES];
            }];
        }
        
        wjob.statusViewController.documentName = wjob.docName;
        wjob.statusViewController.statusMessage = CloudPrintStatusMessageUploadingFile;
        wjob.statusViewController.progress = [NSProgress progressWithTotalUnitCount:kProgressMax];
        wjob.statusViewController.progress.completedUnitCount = kUploadFileProgressStart;
        
        if (wjob.navController.topViewController != wjob.statusViewController) {
            [wjob.navController pushViewController:wjob.statusViewController animated:YES];
        }
        if (!welf.cloudPrintService) {
            welf.cloudPrintService = [CloudPrintService sharedInstanceToRetain];
        }
        
        
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            if (wjob.navController.topViewController == wjob.statusViewController) {
                // if user tapped cancel so quickly that it was before this dispatch triggered, we should
                // not start the request
            
                NSProgress* progress = [NSProgress progressWithTotalUnitCount:kProgressMax];
                
                [welf.cloudPrintService uploadForPrintDocumentWithLocalURL:localURL jobUniqueId:wjob.request.jobUniqueId success:^(int64_t documentId) {
                    wjob.statusViewController.statusMessage = CloudPrintStatusMessageSendingToPrinter;
                    wjob.statusViewController.progress.completedUnitCount = kSendToPrinterProgressStart;
                    wjob.request.documentId  = documentId;
                    [welf.cloudPrintService printDocumentWithRequest:request delegate:welf];
                } progress:&progress failure:^(CloudPrintUploadFailureReason failureReason) {
                    switch (failureReason) {
                        case CloudPrintUploadFailureReasonAuthenticationError:
                        {
                            [[AuthenticationController sharedInstance] addLoginObserver:welf success:^{
                                wjob.requestViewController.userValidatedRequestBlock(wjob.request);
                            } userCancelled:^{
                                wjob.statusViewController.progress.completedUnitCount = 0;
                                wjob.statusViewController.statusMessage = CloudPrintStatusMessageError;
                                [wjob.navController popToViewController:wjob.requestViewController animated:YES];
                            } failure:^(NSError *error) {
                                wjob.statusViewController.progress.completedUnitCount = 0;
                                wjob.statusViewController.statusMessage = CloudPrintStatusMessageError;
                                if (error.code == kAuthenticationErrorCodeCouldNotAskForCredentials) {
                                    [welf showErrorAlertWithMessage:NSLocalizedStringFromTable(@"LoginInAppRequired", @"PocketCampus", nil) onViewController:wjob.requestViewController];
                                } else {
                                    [welf showErrorAlertWithMessage:NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil) onViewController:wjob.requestViewController];
                                }
                                [wjob.navController popToViewController:wjob.requestViewController animated:YES];
                            }];
                            break;
                        }
                        case CloudPrintUploadFailureReasonNetworkError:
                            wjob.statusViewController.progress.completedUnitCount = 0;
                            wjob.statusViewController.statusMessage = CloudPrintStatusMessageError;
                            [welf showErrorAlertWithMessage:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutAlert", @"PocketCampus", nil) onViewController:wjob.requestViewController];
                            [wjob.navController popToViewController:wjob.requestViewController animated:YES];
                            break;
                        default:
                            wjob.statusViewController.progress.completedUnitCount = 0;
                            wjob.statusViewController.statusMessage = CloudPrintStatusMessageError;
                            [welf showErrorAlertWithMessage:NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil) onViewController:wjob.requestViewController];
                            [wjob.navController popToViewController:wjob.requestViewController animated:YES];
                            break;
                    }
                }];
                [progress addObserver:welf forKeyPath:NSStringFromSelector(@selector(fractionCompleted)) options:0 context:(__bridge void *)(wjob).request.jobUniqueId];
            }
        });
        
    }];
    return viewController;
}

- (UIViewController*)viewControllerForPrintWithDocumentName:(NSString*)docName printDocumentRequest:(PrintDocumentRequest*)request completion:(void (^)(CloudPrintCompletionStatusCode printStatusCode))completion {
    
    if (request.documentId == 0) {
        [NSException raise:@"Illegal argument" format:@"request.documentId cannot be 0"];
    }
    
    CloudPrintRequestViewController* requestViewController = [[CloudPrintRequestViewController alloc] initWithDocumentName:docName printRequest:request];
    PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:requestViewController];
    
    navController.preferredContentSize = CGSizeMake(360.0, 620.0);
    
    CloudPrintJob* job = [CloudPrintJob new];
    job.request = request;
    job.docName = docName;
    job.completion = completion;
    job.navController = navController;
    job.requestViewController = requestViewController;
    self.jobForJobUniqueId[request.jobUniqueId] = job;
    
    __weak __typeof(job) wjob = job;
    
    __weak __typeof(self) welf = self;
    [requestViewController setUserValidatedRequestBlock:^(PrintDocumentRequest* request) {
        if (!wjob.statusViewController) {
            wjob.statusViewController = [CloudPrintStatusViewController new];
            
            [wjob.statusViewController setUserCancelledBlock:^{
                [welf.cloudPrintService cancelJobsWithUniqueId:wjob.request.jobUniqueId];
                [wjob.navController popToViewController:wjob.requestViewController animated:YES];
            }];
        }
        
        wjob.statusViewController.documentName = wjob.docName;
        wjob.statusViewController.statusMessage = CloudPrintStatusMessageSendingToPrinter;
        wjob.statusViewController.progress = [NSProgress progressWithTotalUnitCount:kProgressMax];
        wjob.statusViewController.progress.completedUnitCount = kSendToPrinterProgressStart;
        
        if (wjob.navController.topViewController != wjob.statusViewController) {
            [wjob.navController pushViewController:wjob.statusViewController animated:YES];
        }
        
        if (!welf.cloudPrintService) {
            welf.cloudPrintService = [CloudPrintService sharedInstanceToRetain];
        }
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            if (wjob.navController.topViewController == wjob.statusViewController) {
                // if user tapped cancel so quickly that it was before this dispatch triggered, we should
                // not start the request
                [welf.cloudPrintService printDocumentWithRequest:request delegate:welf];
            }
        });
    }];
    [requestViewController setUserCancelledBlock:^{
        [welf job:wjob completedWithStatusCode:CloudPrintCompletionStatusCodeUserCancelled];
    }];
    return navController;
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
            job.statusViewController.progress.completedUnitCount = kProgressMax;
            job.statusViewController.statusMessage = CloudPrintStatusMessageSuccess;
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.75 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{ //give time for success message and animation
                [self job:job completedWithStatusCode:CloudPrintCompletionStatusCodePrintSuccess];
            });
            break;
        }
        case CloudPrintStatusCode_AUTHENTICATION_ERROR:
        {
            __weak __typeof(job) wjob = job;
            __weak __typeof(self) welf = self;
            [[AuthenticationController sharedInstance] addLoginObserver:self success:^{
                wjob.requestViewController.userValidatedRequestBlock(wjob.request);
            } userCancelled:^{
                wjob.statusViewController.progress.completedUnitCount = 0;
                wjob.statusViewController.statusMessage = CloudPrintStatusMessageError;
                [wjob.navController popToViewController:wjob.requestViewController animated:YES];
            } failure:^(NSError *error) {
                wjob.statusViewController.progress.completedUnitCount = 0;
                wjob.statusViewController.statusMessage = CloudPrintStatusMessageError;
                [wjob.navController popToViewController:wjob.requestViewController animated:YES];
                if (error.code == kAuthenticationErrorCodeCouldNotAskForCredentials) {
                    [welf showErrorAlertWithMessage:NSLocalizedStringFromTable(@"LoginInAppRequired", @"PocketCampus", nil) onViewController:wjob.requestViewController];
                } else {
                    [welf showErrorAlertWithMessage:NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil) onViewController:wjob.requestViewController];
                }
            }];
            break;
        }
        case CloudPrintStatusCode_PRINT_ERROR:
            job.statusViewController.progress.completedUnitCount = 0;
            job.statusViewController.statusMessage = CloudPrintStatusMessageError;
            [self showErrorAlertWithMessage:NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil) onViewController:job.requestViewController];
            [job.navController popToViewController:job.requestViewController animated:YES];
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
    job.statusViewController.progress.completedUnitCount = 0;
    job.statusViewController.statusMessage = CloudPrintStatusMessageError;
    [self showErrorAlertWithMessage:NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil) onViewController:job.requestViewController];
    [job.navController popToViewController:job.requestViewController animated:YES];
}

- (void)serviceConnectionToServerFailed {
    for (CloudPrintJob* job in self.jobForJobUniqueId.allValues) {
        job.statusViewController.progress.completedUnitCount = 0;
        job.statusViewController.statusMessage = CloudPrintStatusMessageError;
        [job.navController popToViewController:job.requestViewController animated:YES];
        [self showErrorAlertWithMessage:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutAlert", @"PocketCampus", nil) onViewController:job.requestViewController];
    }
}

#pragma mark - KVO

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    if (![object isKindOfClass:[NSProgress class]]) {
        return;
    }
    NSString* jobUniqueId = (__bridge NSString*)context;
    CloudPrintJob* job = self.jobForJobUniqueId[jobUniqueId];
    if (!job) {
        NSLog(@"!! ERROR: could not find job in KVO observeration for job id %@. Ignoring.", jobUniqueId);
        return;
    }
    NSProgress* progress = object;
    job.statusViewController.progress.completedUnitCount = kSendToPrinterProgressStart * progress.fractionCompleted;
}

#pragma mark - Private

- (void)handleJob:(CloudPrintJob*)job {
    __weak __typeof(self) welf = self;
    __weak __typeof(job) wjob = self;
    
    // Phase 1: download document if needed
    
    if (wjob.documentURL && !wjob.documentLocalURL) {
        // Document needs to be downloaded first
        if (wjob.isDownloadingDocument) {
            return; // Already started
        }
        
        wjob.preRequestStatusViewController.progress = [NSProgress progressWithTotalUnitCount:kProgressMax]
        [self downloadDocumentForJob:wjob progress:wjob.preRequestStatusViewController.progress completion:^(NSError *error) {
            wjob.isDownloadingDocument = NO;
            if (error) {
#warning show error message and propose to try again
                return;
            }
            [welf handleJob:wjob];
        }];
        wjob.preRequestStatusViewController.statusMessage = CloudPrintStatusMessageDownloadingFile;
        wjob.currentViewController = wjob.preRequestStatusViewController;
        return;
    }
    
    // Phase 2: upload document if needed
    
    if (wjob.request.documentNeedsToBeUploaded) {
        if (!wjob.documentLocalURL) {
            [NSException raise:@"Illegal state" format:@"Before being uploaded, document needs to be stored locally and documentLocalURL must be set"];
        }
        if (![self.class isSupportedFileWithLocalURL:wjob.documentLocalURL]) {
            [welf job:wjob completedWithStatusCode:CloudPrintCompletionStatusCodeUnsupportedFile];
            return;
        }
        [wjob.cloudPrintService uploadForPrintDocumentWithLocalURL:wjob.documentLocalURL jobUniqueId:wjob.request.jobUniqueId success:^(int64_t documentId) {
            wjob.request.documentId = documentId;
            [welf handleJob:wjob];
        } progress:wjob.preRequestStatusViewController.progress failure:^(CloudPrintUploadFailureReason failureReason) {
            switch (failureReason) {
                case CloudPrintUploadFailureReasonAuthenticationError:
                {
                    [[AuthenticationController sharedInstance] addLoginObserver:welf success:^{
                        [welf handleJob:wjob];
                    } userCancelled:^{
                        [wjob.cloudPrintService cancelJobsWithUniqueId:wjob.request.jobUniqueId];
                        [welf job:wjob completedWithStatusCode:CloudPrintCompletionStatusCodeUserCancelled];
                    } failure:^{
                        
                        [self showErrorAlertWithMessage:NSLocalizedStringFromTable(@"LoginInAppRequired", @"PocketCampus", nil) onViewController:job.requestViewController];
                        [wjob.navController popToViewController:wjob.requestViewController animated:YES];
                    }];
                    break;
                }
                case CloudPrintUploadFailureReasonNetworkError:
                    wjob.statusViewController.progress.completedUnitCount = 0;
                    wjob.statusViewController.statusMessage = CloudPrintStatusMessageError;
                    [self showErrorAlertWithMessage:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutAlert", @"PocketCampus", nil) onViewController:job.requestViewController];
                    [wjob.navController popToViewController:wjob.requestViewController animated:YES];
                    break;
                default:
                    wjob.statusViewController.progress.completedUnitCount = 0;
                    wjob.statusViewController.statusMessage = CloudPrintStatusMessageError;
                    [self showErrorAlertWithMessage:NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil) onViewController:job.requestViewController];
                    [job.navController popToViewController:wjob.requestViewController animated:YES];
                    break;
            }
        }];
        
    }
}

- (void)job:(CloudPrintJob*)job completedWithStatusCode:(CloudPrintCompletionStatusCode)statusCode {
    @synchronized (self) {
        if (job.completion) {
            job.completion(statusCode);
        }
        if (job.request.jobUniqueId) {
            [self.jobForJobUniqueId removeObjectForKey:job.request.jobUniqueId];
        }
    }
}

/**
 * Downloads the document at url job.documentURL.
 * At end download, if no error, job.documentLocalURL is set.
 */
- (void)downloadDocumentForJob:(CloudPrintJob*)job progress:(NSProgress* __autoreleasing*)progress completion:(void (^)(NSError* error))completion {
#warning TODO
}

- (void)showErrorAlertWithMessage:(NSString*)message onViewController:(UIViewController*)viewController {
    if ([UIAlertController class]) {
        UIAlertController* controller = [UIAlertController alertControllerWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:message preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction* okAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleCancel handler:NULL];
        [controller addAction:okAction];
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.4 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [viewController presentViewController:controller animated:YES completion:NULL];
        });
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
