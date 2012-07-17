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
        username = nil;
        password = nil;
    }
    return self;
}

- (void)askForCredentialsToken:(NSString*)token_ withMessage:(NSString*)messageOrNil delegate:(id<AuthenticationCallbackDelegate>)delegate_ {
    [token release];
    token = [token_ retain];
    if (delegate_ == nil) {
        @throw [NSException exceptionWithName:@"askCredientialsForTypeOfService:delegate: bad delegate" reason:@"delegate cannot be nil" userInfo:nil];
    }
    self.delegate = delegate_;
    NSString* alertTitle;
    alertTitle = NSLocalizedStringFromTable(@"GasparLogin", @"AuthenticationPlugin", nil);
    
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
    NSString* lastUsername = [AuthenticationService savedUsername];
    if (lastUsername != nil) {
        [[alertView textFieldAtIndex:0] setText:lastUsername];
        hasPrefilledUsername = YES;
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

- (void)authenticateSilentlyToken:(NSString*)token_ delegate:(id<AuthenticationCallbackDelegate>)delegate_ {
    [token release];
    token = [token_ retain];
    if (delegate_ == nil) {
        @throw [NSException exceptionWithName:@"askCredientialsForTypeOfService:delegate: bad delegate" reason:@"delegate cannot be nil" userInfo:nil];
    }
    self.delegate = delegate_;
    [username release];
    username = [[AuthenticationService savedUsername] retain];
    [password release];
    password = [[AuthenticationService savedPassword] retain];
    [authenticationService loginToTequilaWithUser:username password:password delegate:self];
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
            [username release];
            username = [[[alertView textFieldAtIndex:0] text] retain];
            [password release];
            password = [[[alertView textFieldAtIndex:1] text] retain];
            
            [AuthenticationService saveUsername:username];
            
            savePassword = [(UISwitch*)[alertView viewWithTag:kSavePasswordSwitchTag] isOn];
            
            if (savePassword) {
                
                /* EXAMPLE of use */
                
                /*NSError* error = nil;
                
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
                }*/
                
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

/* STEP 1 */
- (void)loginToTequilaDidReturn:(ASIHTTPRequest*)request {
    NSLog(@"-> loginToTequilaDidReturn");
    [tequilaCookie release];
    tequilaCookie = nil;
    for(NSHTTPCookie* cookie in request.responseCookies) {
        if ([cookie.name isEqualToString:TEQUILA_COOKIE_NAME]) {
            tequilaCookie = [cookie.value retain];
        }
    }
    if (tequilaCookie == nil) { //means bad credentials
        [AuthenticationService savePassword:nil];
        [self askForCredentialsToken:token withMessage:NSLocalizedStringFromTable(@"BadCredentials", @"AuthenticationPlugin", nil) delegate:self.delegate];
    } else {
        if(savePassword) {
            [AuthenticationService savePassword:password];
        }
        [authenticationService authenticateToken:token withTequilaCookie:tequilaCookie delegate:self];
    }
    [username release];
    username = nil;
    [password release];
    password = nil;
}

- (void)loginToTequilaFailed:(ASIHTTPRequest*)request {
    NSLog(@"-> loginToTequilaFailed");
    [self connectionError];
}

/* STEP 3 */
- (void)authenticateTokenWithTequilaDidReturn:(ASIHTTPRequest*)request{
    NSLog(@"-> authenticateTokenWithTequilaDidReturn");
    NSString* redir = [request.responseHeaders objectForKey:@"Location"];
    if(redir == nil) {
        if ([(NSObject*)self.delegate respondsToSelector:@selector(invalidToken)]) {
            [(NSObject*)self.delegate performSelectorOnMainThread:@selector(invalidToken) withObject:nil waitUntilDone:NO];
        }
    } else {
        if ([(NSObject*)self.delegate respondsToSelector:@selector(authenticationSucceeded)]) {
            [(NSObject*)self.delegate performSelectorOnMainThread:@selector(authenticationSucceeded) withObject:nil waitUntilDone:NO];
        }
    }
}

- (void)authenticateTokenWithTequilaFailed:(ASIHTTPRequest*)request {
    NSLog(@"authenticateTokenWithTequilaFailed");
    [self connectionError];
}

- (void)connectionError {
    // TODO the below error string spans 2 lines, should fix
    [self askForCredentialsToken:token withMessage:NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil) delegate:self.delegate];
}

- (void)serviceConnectionToServerTimedOut {
    NSLog(@"serviceConnectionToServerTimedOut");
    [self connectionError];
}

- (void)dealloc
{
    [authenticationService cancelOperationsForDelegate:self];
    [authenticationService release];
    [tequilaCookie release];
    [username release];
    [password release];
    [super dealloc];
}

@end
