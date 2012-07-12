//
//  CredentialsAlertViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "CredentialsAlertViewController.h"

#import "STUtils.h"

static NSInteger kSavePasswordSwitchTag = 5;

@implementation CredentialsAlertViewController

@synthesize delegate;

- (id)init {
    
    self = [super init];
    if (self) {
        alertView = nil;
        authenticationService = [[AuthenticationService sharedInstanceToRetain] retain];
        tequilaCookie = nil;
        applicationTequilaKey = nil;
        username = nil;
    }
    return self;
}

- (void)askCredientialsForTypeOfService:(int)typeOfService_ message:(NSString*)messageOrNil prefillWithLastUsedUsername:(BOOL)prefillUsername delegate:(id<AuthenticationCallbackDelegate>)delegate_ {
    if (delegate_ == nil) {
        @throw [NSException exceptionWithName:@"askCredientialsForTypeOfService:delegate: bad delegate" reason:@"delegate cannot be nil" userInfo:nil];
    }
    self.delegate = delegate_;
    NSString* alertTitle;
    NSString* gasparLoginString = NSLocalizedStringFromTable(@"GasparLogin", @"AuthenticationPlugin", nil);
    typeOfService = typeOfService_;
    switch (typeOfService) {
        case TypeOfService_SERVICE_CAMIPRO:
            alertTitle = [NSString stringWithFormat:@"%@", gasparLoginString];
            break;
        case TypeOfService_SERVICE_ISA:
            alertTitle = [NSString stringWithFormat:@"%@", gasparLoginString];
            break;
        case TypeOfService_SERVICE_MOODLE:
            alertTitle = [NSString stringWithFormat:@"%@", gasparLoginString];
            break;
        case TypeOfService_SERVICE_POCKETCAMPUS:
            alertTitle = [NSString stringWithFormat:@"%@", gasparLoginString];
            break;
        default:
            @throw [NSException exceptionWithName:@"CredentialsAlertViewController bad typeOfService" reason:@"unknown typeOfService" userInfo:nil];
            break;
    }
    
    NSString* alertMessage = messageOrNil;
    if (alertMessage == nil) {
        alertMessage = @"";
    }
    alertView = [[UIAlertView alloc] initWithTitle:alertTitle message:alertMessage delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) otherButtonTitles:NSLocalizedStringFromTable(@"Login", @"AuthenticationPlugin", nil), nil];
    alertView.alertViewStyle = UIAlertViewStyleLoginAndPasswordInput;
    [[alertView textFieldAtIndex:0] setPlaceholder:NSLocalizedStringFromTable(@"Username", @"AuthenticationPlugin", nil)];
    [[alertView textFieldAtIndex:1] setPlaceholder:NSLocalizedStringFromTable(@"Password", @"AuthenticationPlugin", nil)];
    
    CGFloat offset = 0.0;
    
    if (![alertMessage isEqualToString:@""]) {
        offset = 20.0;
    }
    
    UILabel* keepPasswordLabel = [[UILabel alloc] initWithFrame:CGRectMake(15.0, 124.0+offset, 170.0, 25.0)];
    keepPasswordLabel.text = NSLocalizedStringFromTable(@"SavePassword", @"AuthenticationPlugin", nil);
    keepPasswordLabel.textColor = [UIColor whiteColor];
    keepPasswordLabel.backgroundColor = [UIColor clearColor];
    keepPasswordLabel.adjustsFontSizeToFitWidth = YES;
    keepPasswordLabel.font = [UIFont boldSystemFontOfSize:14.0];
    
    [alertView addSubview:keepPasswordLabel];
    [keepPasswordLabel release];
    
    UISwitch* keepPasswordSwitch = [[UISwitch alloc] initWithFrame:CGRectNull];
    keepPasswordSwitch.center = CGPointMake(233.0, 138.0+offset);
    keepPasswordSwitch.on = YES;
    keepPasswordSwitch.tag = kSavePasswordSwitchTag;
    if ([keepPasswordSwitch respondsToSelector:@selector(setOnTintColor:)]) { //only available in iOS 5 and later
        keepPasswordSwitch.onTintColor = [UIColor colorWithRed:0.000000 green:0.490196 blue:0.639216 alpha:1.0];
    }
    
    [alertView addSubview:keepPasswordSwitch];
    [keepPasswordSwitch release];
    
    BOOL hasPrefilledUsername = NO;
    if (prefillUsername) {
        NSString* lastUsername = [AuthenticationService lastUsedUsernameForService:typeOfService_];
        if (lastUsername != nil) {
            [[alertView textFieldAtIndex:0] setText:lastUsername];
            hasPrefilledUsername = YES;
        }
    }
    [alertView show];
    
    CGFloat offset2 = 40.0;
    
    for (UIView* view in [alertView subviews]) {
        if ([view isKindOfClass:[UIButton class]]) { //make place for the "keep password" switch
            view.frame = CGRectMake(view.frame.origin.x, view.frame.origin.y+offset2, view.frame.size.width, view.frame.size.height);
        }
    }
    
    alertView.frame = CGRectMake(alertView.frame.origin.x, alertView.frame.origin.y, alertView.frame.size.width, alertView.frame.size.height+offset2+5.0);
    if (hasPrefilledUsername) {
        [[alertView textFieldAtIndex:1] becomeFirstResponder];
    }
}

/* UIAlertViewDelegate delegation */

- (void)alertView:(UIAlertView *)alertView_ didDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (alertView_ == connectionErrorAlertView) {
        [self askCredientialsForTypeOfService:typeOfService message:nil prefillWithLastUsedUsername:YES delegate:self.delegate];
        return;
    }
    switch (buttonIndex) {
        case 0: //Cancel button
            if ([(NSObject*)self.delegate respondsToSelector:@selector(userCancelledAuthentication)]) {
                [(NSObject*)self.delegate performSelectorOnMainThread:@selector(userCancelledAuthentication) withObject:nil waitUntilDone:NO];
            }
            break;
        case 1: //OK button
        {
            [username release];
            username = [[[alertView textFieldAtIndex:0] text] retain];
            NSString* password = [[alertView textFieldAtIndex:1] text];
            
            BOOL savePassword = [(UISwitch*)[alertView viewWithTag:kSavePasswordSwitchTag] isOn];
            
            if (savePassword) {
                
                /* EXAMPLE of use */
                
                NSError* error = nil;
                
                NSString* password = [STKeychain getPasswordForUsername:username andServiceName:@"Gaspar" error:&error];
                
                if (error) {
                    NSLog(@"Error while retrieving password");  
                } else if (!password) {
                    NSLog(@"No previously saved password");
                } else {
                    NSLog(@"Retrieved password : %@", password);
                }
                
                [STKeychain storeUsername:username andPassword:password forServiceName:@"Gaspar" updateExisting:YES error:&error];
                if (error) {
                    NSLog(@"Error while storing password");
                } else {
                    NSLog(@"Password saved");
                }
                
                /* END */
            }
            
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
    NSLog(@"-> getTequilaKeyFailedForService:%d", aService);
    [authenticationService release];
    authenticationService = [[AuthenticationService sharedInstanceToRetain] retain];
    [self connectionError];
}

/* STEP 4 */
- (void)getSessionIdForServiceWithTequilaKey:(TequilaKey*)tequilaKey didReturn:(SessionId*)sessionId {
    if ([(NSObject*)self.delegate respondsToSelector:@selector(gotSessionId:)]) {
        [(NSObject*)self.delegate performSelectorOnMainThread:@selector(gotSessionId:) withObject:sessionId waitUntilDone:YES]; //need to wait because sessionId is autoreleased and must wait for delegate to retain it
    }
}

- (void)getSessionIdForServiceFailedForTequilaKey:(TequilaKey*)tequilaKey {
    NSLog(@"-> getSessionIdForServiceFailedForTequilaKey:%@", tequilaKey);
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
        [username release];
        username = nil;
        [self askCredientialsForTypeOfService:typeOfService message:NSLocalizedStringFromTable(@"BadCredentials", @"AuthenticationPlugin", nil) prefillWithLastUsedUsername:NO delegate:self.delegate];
    } else {
        [AuthenticationService saveLastUsedUsername:username forService:typeOfService]; //successfully authentified => save username for future prefill
        [authenticationService getTequilaKeyForService:typeOfService delegate:self];
    }
}

- (void)loginToTequilaFailed:(ASIHTTPRequest*)request {
    NSLog(@"-> loginToTequilaFailed");
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
    UIAlertView* alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil) delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
    connectionErrorAlertView = alert;
    [alert show];
    [alert release];
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
    [username release];
    [super dealloc];
}

@end
