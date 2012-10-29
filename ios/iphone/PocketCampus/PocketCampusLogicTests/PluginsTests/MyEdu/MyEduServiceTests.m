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

- (void)tempTest {
    service = [[MyEduService sharedInstanceToRetain] retain];
    authController = [[AuthenticationController alloc] init];
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
    MyEduRequest* req = [[[MyEduRequest alloc] initWithIMyEduSession:myEduSession iLanguage:@"en"] autorelease];
    [service getSubscribedCoursesListForRequest:req delegate:self];

}

- (void)getMyEduSessionFailedForTequilaToken:(MyEduTequilaToken*)tequilaToken {
    NSLog(@"getSessionIdForServiceFailedForTequilaKey");
}

- (void)getSubscribedCoursesListForRequest:(MyEduRequest *)request didReturn:(SubscribedCoursesListReply *)reply {
    NSLog(@"getSubscribedCoursesListForRequest:didReturn:");
    NSLog(@"statusCode:%d coursesList:%@", reply.iStatus, reply.iSubscribedCourses);
}

- (void)getSubscribedCoursesListFailedForRequest:(MyEduRequest *)request {
    NSLog(@"getSubscribedCoursesListFailedForRequest");
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
