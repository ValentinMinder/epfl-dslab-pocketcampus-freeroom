//
//  PushNotifControllerTests.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PushNotifControllerTests.h"

#import "PushNotifController.h"

@interface PushNotifControllerTests ()

@property (nonatomic, strong) PushNotifController* pushNotifController;

@end

@implementation PushNotifControllerTests

- (id)init
{
    self = [super init];
    if (self) {
        self.pushNotifController = [PushNotifController sharedInstanceToRetain];
    }
    return self;
}

@end
