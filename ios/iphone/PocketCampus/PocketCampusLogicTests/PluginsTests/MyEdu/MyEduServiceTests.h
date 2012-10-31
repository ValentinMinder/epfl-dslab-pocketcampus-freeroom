//
//  MyEduServiceTests.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 20.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "MyEduService.h"

#import "AuthenticationController.h"

#import "AuthenticationService.h"

@interface MyEduServiceTests : NSObject<MyEduServiceDelegate, AuthenticationCallbackDelegate> {
    AuthenticationController* authController;
    MyEduService* service;
    MyEduTequilaToken* tequilaToken;
}

@property (nonatomic, retain) MyEduRequest* myEduRequest;

- (void)tempTest;

@end
