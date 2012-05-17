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
    int typeOfService;
    NSString* tequilaCookie;
    TequilaKey* applicationTequilaKey;
    NSString* username;
}

@property (assign) id<AuthenticationCallbackDelegate> delegate;

- (void)askCredientialsForTypeOfService:(int)typeOfService_ message:(NSString*)messageOrNil prefillWithLastUsedUsername:(BOOL)prefillUsername delegate:(id<AuthenticationCallbackDelegate>)delegate_;

@end
