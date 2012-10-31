//
//  MyEduService.h
//  PocketCampus
//
//

#import <Foundation/Foundation.h>

#import "Service.h"

#import "myedu.h"

@interface MyEduService : Service <ServiceProtocol>

/* Utilitiy methods */
- (MyEduRequest*)createMyEduRequest;
- (MyEduSession*)lastSession;
- (BOOL)saveSession:(MyEduSession*)session;
- (BOOL)deleteSession;

/* Service methods
 - (MyEduTequilaToken *) getTequilaTokenForMyEdu;  // throws TException
 - (MyEduSession *) getMyEduSession: (MyEduTequilaToken *) iTequilaToken;  // throws TException
 - (MyEduSubscribedCoursesListReply *) getSubscribedCoursesList: (MyEduRequest *) iMyEduRequest;  // throws TException
 - (MyEduCourseDetailsReply *) getCourseDetails: (MyEduRequest *) iMyEduRequest : (MyEduCourseDetailsRequest *) iMyEduCourseDetailsRequest;  // throws TException
 - (MyEduSectionDetailsReply *) getSectionDetails: (MyEduRequest *) iMyEduRequest : (MyEduSectionDetailsRequest *) iMyEduSectionDetailsRequest;  // throws TException
 - (MyEduModuleDetailsReply *) getModuleDetails: (MyEduRequest *) iMyEduRequest : (MyEduModuleDetailsRequest *) iMyEduModuleDetailsRequest;  // throws TException
 - (MyEduSubmitFeedbackReply *) submitFeedback: (MyEduRequest *) iMyEduRequest : (MyEduSubmitFeedbackRequest *) iMyEduSubmitFeedbackRequest;  // throws TException
*/

/* Asynchronous methods */
- (void)getTequilaTokenForMyEduWithDelegate:(id)delegate;
- (void)getMyEduSessionForTequilaToken:(MyEduTequilaToken*)tequilaToken delegate:(id)delegate;
- (void)getSubscribedCoursesListForRequest:(MyEduRequest*)request delegate:(id)delegate;
- (void)getCourseDetailsForRequest:(MyEduCourseDetailsRequest*)request myeduRequest:(MyEduRequest*)myeduRequest delegate:(id)delegate;
- (void)getSectionDetailsForRequest:(MyEduSectionDetailsRequest*)request myeduRequest:(MyEduRequest*)myeduRequest delegate:(id)delegate;
- (void)getModuleDetailsForRequest:(MyEduModuleDetailsRequest*)request myeduRequest:(MyEduRequest*)myeduRequest delegate:(id)delegate;
- (void)submitFeedbackWithRequest:(MyEduSubmitFeedbackRequest*)request myeduRequest:(MyEduRequest*)myeduRequest delegate:(id)delegate;

/* Synchronous methods (from cache) return nil if not in cache */

- (MyEduSubscribedCoursesListReply*)getFromCacheSubscribedCoursesListForRequest:(MyEduRequest*)myeduRequest;
- (MyEduCourseDetailsReply*)getFromCacheCourseDetailsForRequest:(MyEduCourseDetailsRequest*)request myeduRequest:(MyEduRequest*)myeduRequest;

@end

@protocol MyEduServiceDelegate <ServiceDelegate>

@optional
- (void)getTequilaTokenForMyEduDidReturn:(MyEduTequilaToken*)tequilaToken;
- (void)getTequilaTokenForMyEduFailed;
- (void)getMyEduSessionForTequilaToken:(MyEduTequilaToken*)tequilaToken didReturn:(MyEduSession*)myEduSession;
- (void)getMyEduSessionFailedForTequilaToken:(MyEduTequilaToken*)tequilaToken;
- (void)getSubscribedCoursesListForRequest:(MyEduRequest*)request didReturn:(MyEduSubscribedCoursesListReply*)reply;
- (void)getSubscribedCoursesListFailedForRequest:(MyEduRequest*)request myeduRequest:(MyEduRequest*)myeduRequest;
- (void)getCourseDetailsForRequest:(MyEduCourseDetailsRequest*)request myeduRequest:(MyEduRequest*)myeduRequest didReturn:(MyEduCourseDetailsReply*)reply;
- (void)getCourseDetailsFailedForRequest:(MyEduCourseDetailsRequest *)request myeduRequest:(MyEduRequest*)myeduRequest;
- (void)getSectionDetailsForRequest:(MyEduSectionDetailsRequest*)request myeduRequest:(MyEduRequest*)myeduRequest didReturn:(MyEduSectionDetailsReply*)reply;
- (void)getSectionDetailsFailedForRequest:(MyEduSectionDetailsRequest *)request myeduRequest:(MyEduRequest*)myeduRequest;
- (void)getModuleDetailsForRequest:(MyEduModuleDetailsRequest*)request myeduRequest:(MyEduRequest*)myeduRequest didReturn:(MyEduModuleDetailsReply*)reply;
- (void)getModuleDetailsFailedForRequest:(MyEduModuleDetailsRequest *)request myeduRequest:(MyEduRequest*)myeduRequest;
- (void)submitFeedbackForRequest:(MyEduSubmitFeedbackRequest*)request myeduRequest:(MyEduRequest*)myeduRequest didReturn:(MyEduSubmitFeedbackReply*)reply;
- (void)submitFeedbackFailedForRequest:(MyEduSubmitFeedbackRequest *)request myeduRequest:(MyEduRequest*)myeduRequest;

@end
