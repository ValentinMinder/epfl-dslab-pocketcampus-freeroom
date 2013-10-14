//
//  MyEduServiceTests.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 20.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyEduServiceTests.h"

#import "ASIHTTPRequest.h"

@implementation MyEduServiceTests

@synthesize myEduRequest;

- (void)tempTest {
    service = [[MyEduService sharedInstanceToRetain] retain];
    authController = [[AuthenticationController sharedInstanceToRetain] retain];
    //[service getSubscribedCoursesListForRequest:[[[MyEduRequest alloc] initWithIMyEduSession:[[[MyEduSession alloc] initWithIMyEduCookie:@"fsdfsdf"] autorelease] iLanguage:@""] autorelease] delegate:self];
    [service getTequilaTokenForMyEduWithDelegate:self];
}

/* MyEduServiceDelegate delegation */

- (void)getTequilaTokenForMyEduDidReturn:(MyEduTequilaToken*)tequilaToken_ {
    NSLog(@"getTequilaTokenForMyEduDidReturn:%@", tequilaToken_);
    tequilaToken = [tequilaToken_ retain];
    UIViewController* viewController = [[[[UIApplication sharedApplication] windows] objectAtIndex:0] rootViewController];
    [authController authToken:tequilaToken.iTequilaKey presentationViewController:viewController delegate:self];
}

- (void)getTequilaTokenForMyEduFailed {
    NSLog(@"getTequilaTokenForMyEduFailed");
}

- (void)getMyEduSessionForTequilaToken:(MyEduTequilaToken*)tequilaToken didReturn:(MyEduSession*)myEduSession {
    NSLog(@"getSessionIdForServiceWithTequilaKey:didReturn: %@", myEduSession);
    self.myEduRequest = [[[MyEduRequest alloc] initWithIMyEduSession:myEduSession iLanguage:@"en"] autorelease];
    [service getSubscribedCoursesListForRequest:[service createMyEduRequest] delegate:self];

}

- (void)getMyEduSessionFailedForTequilaToken:(MyEduTequilaToken*)tequilaToken {
    NSLog(@"getSessionIdForServiceFailedForTequilaKey");
}

- (void)getSubscribedCoursesListForRequest:(MyEduRequest *)request didReturn:(MyEduSubscribedCoursesListReply *)reply {
    NSLog(@"getSubscribedCoursesListForRequest:didReturn: %@", reply);
    for (MyEduCourse* course in reply.iSubscribedCourses) {
        [service getCourseDetailsForRequest:[[MyEduCourseDetailsRequest alloc] initWithIMyEduRequest:[service createMyEduRequest] iCourseCode:course.iCode] delegate:self];
    }
    
}

- (void)getSubscribedCoursesListFailedForRequest:(MyEduRequest *)request {
    NSLog(@"getSubscribedCoursesListFailedForRequest");
}

- (void)getCourseDetailsForRequest:(MyEduCourseDetailsRequest *)request myeduRequest:(MyEduRequest*)myeduRequest didReturn:(MyEduCourseDetailsReply *)reply {
    NSLog(@"getSubscribedCoursesListForRequest:didReturn: %@", reply);
}

- (void)getCourseDetailsFailedForRequest:(MyEduCourseDetailsRequest *)request myeduRequest:(MyEduRequest*)myeduRequest {
    NSLog(@"getCourseDetailsFailedForRequest");
}

- (void)serviceConnectionToServerTimedOut {
    NSLog(@"Timeout");
}

/* AuthenticationCallbackDelegate delegation */

- (void)authenticationSucceeded {
    NSLog(@"auth succeded");
    [service getMyEduSessionForTequilaToken:tequilaToken delegate:self];
}

- (void)userCancelledAuthentication {
    
}

- (void)invalidToken {
    NSLog(@"invalid token");
}

- (void)dealloc
{
    [service release];
    [super dealloc];
}

@end
