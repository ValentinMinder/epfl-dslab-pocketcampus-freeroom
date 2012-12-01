//
//  MyEduService.h
//  PocketCampus
//
//

#import <Foundation/Foundation.h>

#import "Service.h"

#import "myedu.h"

#import "ASIHTTPRequest.h"


#pragma mark - MyEduMaterialData definition

@interface MyEduMaterialData : NSObject<NSCoding>

@property (readonly, strong) MyEduMaterial* material;
@property (readonly, strong) NSURL* localURL;
@property (readonly, strong) NSData* data;
@property (readonly, copy) NSString* mimeType;
@property (readonly, copy) NSString* textEncoding;

@end

#pragma mark - MyEduDownloadObserver definition

typedef void (^DownloadDidStartBlock)(void);
typedef void (^DownloadDidFinishBlock)(NSURL* fileLocalURL);
typedef void (^DownloadDidProgressBlock)(unsigned long long nbBytesDownloaded, unsigned long long nbBytesToDownload, float ratio);
typedef void (^DownloadWasCancelledBlock)(void);
typedef void (^DownloadDidFailBlock)(int statusCode);
typedef void (^DownloadWasDeletedBlock)(void); //called when downloaded file was deleted

@interface MyEduDownloadObserver : NSObject

@property (nonatomic, assign) id observer;
@property (nonatomic, copy) NSString* downloadIdentifier;
@property (nonatomic, copy) DownloadDidStartBlock startBlock; //this block will be called for any observer that was added before and after the download has started
@property (nonatomic, copy) DownloadDidFinishBlock finishBlock;
@property (nonatomic, copy) DownloadDidProgressBlock progressBlock;
@property (nonatomic, copy) DownloadWasCancelledBlock cancelledBlock;
@property (nonatomic, copy) DownloadDidFailBlock failureBlock;
@property (nonatomic, copy) DownloadWasDeletedBlock deletedBlock;

@end

#pragma mark - MyEduModuleVideoDownloadObserver definition

@interface MyEduModuleVideoDownloadObserver : MyEduDownloadObserver

- (void)initWithModule:(MyEduModule*)module;

@end

#pragma mark - MyEduService definition

@interface MyEduService : Service <ServiceProtocol, ASIHTTPRequestDelegate, ASIProgressDelegate>

/* Utilitiy methods */
- (MyEduRequest*)createMyEduRequest;
- (MyEduSession*)lastSession;
- (BOOL)saveSession:(MyEduSession*)session;
- (BOOL)deleteSession;

/* Service methods
 - (MyEduTequilaToken *) getTequilaTokenForMyEdu;  // throws TException
 - (MyEduSession *) getMyEduSession: (MyEduTequilaToken *) iTequilaToken;  // throws TException
 - (MyEduSubscribedCoursesListReply *) getSubscribedCoursesList: (MyEduRequest *) iMyEduRequest;  // throws TException
 - (MyEduCourseDetailsReply *) getCourseDetails: (MyEduCourseDetailsRequest *) iMyEduCourseDetailsRequest;  // throws TException
 - (MyEduSectionDetailsReply *) getSectionDetails: (MyEduSectionDetailsRequest *) iMyEduSectionDetailsRequest;  // throws TException
 - (MyEduModuleDetailsReply *) getModuleDetails: (MyEduModuleDetailsRequest *) iMyEduModuleDetailsRequest;  // throws TException
 - (MyEduSubmitFeedbackReply *) submitFeedback: (MyEduSubmitFeedbackRequest *) iMyEduSubmitFeedbackRequest;  // throws TException
*/

/* Asynchronous methods deriving from Thrift */
- (void)getTequilaTokenForMyEduWithDelegate:(id)delegate;
- (void)getMyEduSessionForTequilaToken:(MyEduTequilaToken*)tequilaToken delegate:(id)delegate;
- (void)getSubscribedCoursesListForRequest:(MyEduRequest*)request delegate:(id)delegate;
- (void)getCourseDetailsForRequest:(MyEduCourseDetailsRequest*)request delegate:(id)delegate;
- (void)getSectionDetailsForRequest:(MyEduSectionDetailsRequest*)request delegate:(id)delegate;
- (void)getModuleDetailsForRequest:(MyEduModuleDetailsRequest*)request delegate:(id)delegate;
- (void)submitFeedbackWithRequest:(MyEduSubmitFeedbackRequest*)request delegate:(id)delegate;

/* Material download */

- (void)downloadMaterial:(MyEduMaterial*)material progressView:(UIProgressView*)progressView delegate:(id)delegate;

/* Video download */

- (void)addDownloadObserver:(MyEduDownloadObserver*)downloadObserver forVideoOfModule:(MyEduModule*)module startDownload:(BOOL)startDownload;
- (void)addDownloadObserver:(id)observer forVideoOfModule:(MyEduModule*)module startDownload:(BOOL)startDownload startBlock:(DownloadDidStartBlock)startBlock finishBlock:(DownloadDidFinishBlock)finishBlock progressBlock:(DownloadDidProgressBlock)progressBlock cancelledBlock:(DownloadWasCancelledBlock)cancelledBlock failureBlock:(DownloadDidFailBlock)failureBlock deletedBlock:(DownloadWasDeletedBlock)deletedBlock;
- (void)removeDownloadObserver:(id)observer;
- (void)removeDownloadObserver:(id)observer forVideoModule:(MyEduModule*)module;
- (BOOL)downloadVideoOfModule:(MyEduModule*)module; //will start download (returns YES) if not started already (returns NO)
- (void)cancelVideoDownloadForModule:(MyEduModule*)module;
- (void)removeDownloadedVideoOfModule:(MyEduModule*)module;
- (BOOL)videoOfModuleIsDownloading:(MyEduModule*)module;

/* Synchronous methods (from cache) return nil if not in cache */

- (MyEduSubscribedCoursesListReply*)getFromCacheSubscribedCoursesListForRequest:(MyEduRequest*)request;
- (MyEduCourseDetailsReply*)getFromCacheCourseDetailsForRequest:(MyEduCourseDetailsRequest*)request;
- (MyEduSectionDetailsReply*)getFromCacheSectionDetailsForRequest:(MyEduSectionDetailsRequest*)request;
- (MyEduModuleDetailsReply*)getFromCacheModuleDetailsForRequest:(MyEduModuleDetailsRequest*)request;

- (MyEduMaterialData*)materialDataIfExistsForMaterial:(MyEduMaterial*)material; //returns nil if not such stored material

/* Utiliy methods */

+ (NSTimeInterval)lastPlaybackTimeForVideoForModule:(MyEduModule*)module;
+ (BOOL)saveLastPlaybackTime:(NSTimeInterval)time forVideoOfModule:(MyEduModule*)module;
+ (NSString*)keyForVideoOfModule:(MyEduModule*)module;
+ (NSString*)localPathForVideoOfModule:(MyEduModule*)module;
+ (NSString*)localPathOfVideoForModule:(MyEduModule*)module nilIfNoFile:(BOOL)nilIfNoFile;
+ (NSString*)videoHTMLCodeForMyEduModule:(MyEduModule*)module videoWidth:(int)width videoHeight:(int)height;

@end

@protocol MyEduServiceDelegate <ServiceDelegate>

@optional
- (void)getTequilaTokenForMyEduDidReturn:(MyEduTequilaToken*)tequilaToken;
- (void)getTequilaTokenForMyEduFailed;
- (void)getMyEduSessionForTequilaToken:(MyEduTequilaToken*)tequilaToken didReturn:(MyEduSession*)myEduSession;
- (void)getMyEduSessionFailedForTequilaToken:(MyEduTequilaToken*)tequilaToken;
- (void)getSubscribedCoursesListForRequest:(MyEduRequest*)request didReturn:(MyEduSubscribedCoursesListReply*)reply;
- (void)getSubscribedCoursesListFailedForRequest:(MyEduRequest*)request;
- (void)getCourseDetailsForRequest:(MyEduCourseDetailsRequest*)request didReturn:(MyEduCourseDetailsReply*)reply;
- (void)getCourseDetailsFailedForRequest:(MyEduCourseDetailsRequest *)request;
- (void)getSectionDetailsForRequest:(MyEduSectionDetailsRequest*)request didReturn:(MyEduSectionDetailsReply*)reply;
- (void)getSectionDetailsFailedForRequest:(MyEduSectionDetailsRequest *)request;
- (void)getModuleDetailsForRequest:(MyEduModuleDetailsRequest*)request didReturn:(MyEduModuleDetailsReply*)reply;
- (void)getModuleDetailsFailedForRequest:(MyEduModuleDetailsRequest *)request;
- (void)submitFeedbackForRequest:(MyEduSubmitFeedbackRequest*)request didReturn:(MyEduSubmitFeedbackReply*)reply;
- (void)submitFeedbackFailedForRequest:(MyEduSubmitFeedbackRequest *)request;

- (void)downloadOfMaterial:(MyEduMaterial *)meterial didFinish:(MyEduMaterialData*)materialData;
- (void)downloadFailedForMaterial:(MyEduMaterial *)meterial responseStatusCode:(int)statusCode;

@end
