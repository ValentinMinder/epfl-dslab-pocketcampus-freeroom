//
//  CredentialsAlertViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "AuthenticationService.h"

#import "authentication.h"

@interface CredentialsAlertViewController : NSObject<UIAlertViewDelegate, AuthenticationServiceDelegate> {
    UIAlertView* alertView;
    UIAlertView* connectionErrorAlertView;
    AuthenticationService* authenticationService;
    //int typeOfService;
    BOOL savePassword;
    NSString* tequilaCookie;
    //TequilaKey* applicationTequilaKey;
    NSString* username;
    NSString* password;
    NSString* token;
}

@property (assign) id<AuthenticationCallbackDelegate> delegate;

- (void)askForCredentialsToken:(NSString*)token_ withMessage:(NSString*)messageOrNil delegate:(id<AuthenticationCallbackDelegate>)delegate_;
- (void)authenticateSilentlyToken:(NSString*)token_ delegate:(id<AuthenticationCallbackDelegate>)delegate_;

@end
