//
//  CredentialsAlertViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "CredentialsAlertViewController.h"

@implementation CredentialsAlertViewController

@synthesize delegate;

- (id)init {
    
    self = [super init];
    if (self) {
        alertView = nil;
        authenticationService = [[AuthenticationService sharedInstanceToRetain] retain];
        tequilaCookie = nil;
        applicationTequilaKey = nil;
    }
    return self;
}

- (void)askCredientialsForTypeOfService:(int)typeOfService_ message:(NSString*)messageOrNil delegate:(id<AuthenticationCallbackDelegate>)delegate_ {
    if (delegate_ == nil) {
        @throw [NSException exceptionWithName:@"askCredientialsForTypeOfService:delegate: bad delegate" reason:@"delegate cannot be nil" userInfo:nil];
    }
    self.delegate = delegate_;
    NSString* alertTitle;
    NSString* gasparLoginString = NSLocalizedStringFromTable(@"GasparLogin", @"AuthenticationPlugin", nil);
    typeOfService = typeOfService_;
    switch (typeOfService) {
        case TypeOfService_SERVICE_CAMIPRO:
            alertTitle = [NSString stringWithFormat:@"%@ (Camipro)", gasparLoginString];
            break;
        case TypeOfService_SERVICE_ISA:
            alertTitle = [NSString stringWithFormat:@"%@ (IS-Academia)", gasparLoginString];
            break;
        case TypeOfService_SERVICE_MOODLE:
            alertTitle = [NSString stringWithFormat:@"%@ (Moodle)", gasparLoginString];
            break;
        case TypeOfService_SERVICE_POCKETCAMPUS:
            alertTitle = [NSString stringWithFormat:@"%@ (PocketCampus)", gasparLoginString];
            break;
        default:
            @throw [NSException exceptionWithName:@"CredentialsAlertViewController bad typeOfService" reason:@"unknown typeOfService" userInfo:nil];
            break;
    }
    
    NSString* alertMessage = messageOrNil;
    if (alertMessage == nil) {
        alertMessage = @"";
    }
    alertView = [[UIAlertView alloc] initWithTitle:alertTitle message:alertMessage delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) otherButtonTitles:NSLocalizedStringFromTable(@"Login", @"", nil), nil];
    alertView.alertViewStyle = UIAlertViewStyleLoginAndPasswordInput;
    [alertView show];
}

/* UIAlertViewDelegate delegation */

- (void)alertView:(UIAlertView *)alertView_ didDismissWithButtonIndex:(NSInteger)buttonIndex {
    switch (buttonIndex) {
        case 0: //Cancel button
            if ([(NSObject*)self.delegate respondsToSelector:@selector(userCancelledAuthentication)]) {
                [(NSObject*)self.delegate performSelectorOnMainThread:@selector(userCancelledAuthentication) withObject:nil waitUntilDone:NO];
            }
            break;
        case 1: //OK button
        {
            NSString* username = [[alertView textFieldAtIndex:0] text];
            NSString* password = [[alertView textFieldAtIndex:1] text];
            NSLog(@"username : %@ pass : %@", username, password);
            [authenticationService loginToTequilaWithUser:username password:password delegate:self];
            break;
        }
        default:
            NSLog(@"!! Unsupported button index");
            break;
    }
}

/* AuthenticationServiceDelegate delegation */

/* STEP 2 */
- (void)getTequilaKeyForService:(int)aService didReturn:(TequilaKey*)tequilaKey {
    [applicationTequilaKey release];
    applicationTequilaKey = [tequilaKey retain];
    [authenticationService authenticateToken:applicationTequilaKey.iTequilaKey withTequilaCookie:tequilaCookie delegate:self];
}

- (void)getTequilaKeyFailedForService:(int)aService {
    NSLog(@"getTequilaKeyFailedForService:%d", aService);
    [self connectionError];
}

/* STEP 4 */
- (void)getSessionIdForServiceWithTequilaKey:(TequilaKey*)tequilaKey didReturn:(SessionId*)sessionId {
    if ([(NSObject*)self.delegate respondsToSelector:@selector(gotSessionId:)]) {
        [(NSObject*)self.delegate performSelectorOnMainThread:@selector(gotSessionId:) withObject:sessionId waitUntilDone:YES]; //need to wait because sessionId is autoreleased and must wait for delegate to retain it
    }
}

- (void)getSessionIdForServiceFailedForTequilaKey:(TequilaKey*)aTequilaKey {
    NSLog(@"getSessionIdForServiceFailedForTequilaKey");
    [self connectionError];
}

/* STEP 1 */
- (void)loginToTequilaDidReturn:(ASIHTTPRequest*)request {
    [tequilaCookie release];
    tequilaCookie = nil;
    for(NSHTTPCookie* cookie in request.responseCookies) {
        if ([cookie.name isEqualToString:TEQUILA_COOKIE_NAME]) {
            tequilaCookie = [cookie.value retain];
        }
    }
    if (tequilaCookie == nil) { //means bad credentials
        [self askCredientialsForTypeOfService:typeOfService message:NSLocalizedStringFromTable(@"BadCredentials", @"AuthenticationPlugin", nil) delegate:self.delegate];
    } else {
        [authenticationService getTequilaKeyForService:typeOfService delegate:self];
    }
}

- (void)loginToTequilaFailed:(ASIHTTPRequest*)request {
    NSLog(@"loginToTequilaFailed");
    [self connectionError];
}

/* STEP 3 */
- (void)authenticateTokenWithTequilaDidReturn:(ASIHTTPRequest*)request{
    NSString* redir = [request.responseHeaders objectForKey:@"Location"];
    if(redir == nil) {
        [self connectionError];
    } else {
        [authenticationService getSessionIdForServiceWithTequilaKey:applicationTequilaKey delegate:self];
    }
}

- (void)authenticateTokenWithTequilaFailed:(ASIHTTPRequest*)request {
    NSLog(@"authenticateTokenWithTequilaFailed");
    [self connectionError];
}

- (void)connectionError {
    [self askCredientialsForTypeOfService:typeOfService message:NSLocalizedStringFromTable(@"ErrorOccuredTryAgain", @"AuthenticationPlugin", nil) delegate:self.delegate];
}

- (void)serviceConnectionToServerTimedOut {
    if ([(NSObject*)self.delegate respondsToSelector:@selector(authenticationTimeout)]) {
        [(NSObject*)self.delegate performSelectorOnMainThread:@selector(authenticationTimeout) withObject:nil waitUntilDone:NO];
    }
}

- (void)dealloc
{
    [authenticationService cancelOperationsForDelegate:self];
    [authenticationService release];
    [tequilaCookie release];
    [applicationTequilaKey release];
    [super dealloc];
}

@end
