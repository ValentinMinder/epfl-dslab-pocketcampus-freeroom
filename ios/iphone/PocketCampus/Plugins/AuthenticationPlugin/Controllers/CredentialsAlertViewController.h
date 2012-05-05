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

@interface CredentialsAlertViewController : NSObject<AuthenticationServiceDelegate>

- (id)initWithTypeOfService:(int)service;

@end
