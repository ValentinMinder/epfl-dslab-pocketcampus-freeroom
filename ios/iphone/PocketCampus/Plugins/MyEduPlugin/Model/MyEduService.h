//
//  MyEduService.h
//  PocketCampus
//
//

#import <Foundation/Foundation.h>

#import "Service.h"

#import "myedu.h"

@interface MyEduService : Service <ServiceProtocol>

/* Service methods
- (MyEduTequilaToken *) getTequilaTokenForMyEdu;  // throws TException
- (MyEduSession *) getMyEduSession: (MyEduTequilaToken *) iTequilaToken;  // throws TException
- (SubscribedCoursesListReply *) getSubscribedCoursesList: (MyEduRequest *) iMyEduRequest;  // throws TException
*/

- (void)getTequilaTokenForMyEduWithDelegate:(id)delegate;
- (void)getMyEduSessionForTequilaToken:(MyEduTequilaToken*)tequilaToken delegate:(id)delegate;
- (void)getSubscribedCoursesListForRequest:(MyEduRequest*)request delegate:(id)delegate;

@end

@protocol MyEduServiceDelegate <ServiceDelegate>

@optional
- (void)getTequilaTokenForMyEduDidReturn:(MyEduTequilaToken*)tequilaToken;
- (void)getTequilaTokenForMyEduFailed;
- (void)getMyEduSessionForTequilaToken:(MyEduTequilaToken*)tequilaToken didReturn:(MyEduSession*)myEduSession;
- (void)getMyEduSessionFailedForTequilaToken:(MyEduTequilaToken*)tequilaToken;
- (void)getSubscribedCoursesListForRequest:(MyEduRequest*)request didReturn:(SubscribedCoursesListReply*)reply;
- (void)getSubscribedCoursesListFailedForRequest:(MyEduRequest*)request;

@end
