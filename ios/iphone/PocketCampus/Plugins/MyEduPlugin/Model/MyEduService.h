//
//  MyEduService.h
//  PocketCampus
//
//

#import <Foundation/Foundation.h>

#import "Service.h"

#import "myedu.h"

#import "ASIHTTPRequest.h"

@interface MyEduMaterialData : NSObject<NSCoding>

@property (readonly, strong) MyEduMaterial* material;
@property (readonly, strong) NSURL* localURL;
@property (readonly, strong) NSData* data;
@property (readonly, copy) NSString* mimeType;
@property (readonly, copy) NSString* textEncoding;

@end

@interface MyEduService : Service <ServiceProtocol, ASIHTTPRequestDelegate>

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

/* Asynchronous methods - helpers */
- (void)downloadMaterial:(MyEduMaterial*)material progressView:(UIProgressView*)progressView delegate:(id)delegate;

/* Synchronous methods (from cache) return nil if not in cache */

- (MyEduSubscribedCoursesListReply*)getFromCacheSubscribedCoursesListForRequest:(MyEduRequest*)request;
- (MyEduCourseDetailsReply*)getFromCacheCourseDetailsForRequest:(MyEduCourseDetailsRequest*)request;
- (MyEduSectionDetailsReply*)getFromCacheSectionDetailsForRequest:(MyEduSectionDetailsRequest*)request;
- (MyEduModuleDetailsReply*)getFromCacheModuleDetailsForRequest:(MyEduModuleDetailsRequest*)request;

- (MyEduMaterialData*)materialDataIfExistsForMaterial:(MyEduMaterial*)material; //returns nil if not such stored material

/* Utiliy methods */

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
