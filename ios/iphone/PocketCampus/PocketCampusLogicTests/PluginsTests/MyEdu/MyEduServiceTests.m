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
    
    ASIHTTPRequest* req = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:@"https://myedu.epfl.ch/courses/SwEng.json"]];
    [req addRequestHeader:@"Cookie" value:myEduSession.iMyEduCookie];
    [req startSynchronous];
    
    NSLog(@"%@", req.responseString);
}

- (void)getMyEduSessionFailedForTequilaToken:(MyEduTequilaToken*)tequilaToken {
    NSLog(@"getSessionIdForServiceFailedForTequilaKey");
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
