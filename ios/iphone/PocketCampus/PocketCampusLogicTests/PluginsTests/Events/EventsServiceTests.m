//
//  EventsServiceTests.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 28.02.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "EventsServiceTests.h"

#import "EventsService.h"

#import "PCUtils.h"

@interface EventsServiceTests ()

@property (nonatomic, strong) EventsService* service;

@end

@implementation EventsServiceTests

- (id)init
{
    self = [super init];
    if (self) {
        self.service = [EventsService sharedInstanceToRetain];
    }
    return self;
}

- (void)tmpTest {
    EventPoolRequest* req = [[EventPoolRequest alloc] initWithEventPoolId:-1 userToken:nil lang:[PCUtils userLanguageCode] period:EventsPeriods_ONE_WEEK];
    [self.service getEventPoolForRequest:req delegate:self];
}

- (void)getEventPoolForRequest:(EventPoolRequest *)request didReturn:(EventPoolReply *)reply {
    //NSLog(@"getEventPoolForRequest:didReturn: %@", reply);
    EventItemRequest* req = [[EventItemRequest alloc] initWithEventItemId:[reply.eventPool.childrenEvents[0] longLongValue] userToken:nil lang:[PCUtils userLanguageCode] period:EventsPeriods_ONE_WEEK];
    [self.service getEventItemForRequest:req delegate:self];
}

- (void)getEventPoolFailedForRequest:(EventPoolRequest *)request {
    NSLog(@"getEventFailedForRequest: %@", request);
}

- (void)getEventItemForRequest:(EventItemRequest *)request didReturn:(EventItemReply *)reply {
    NSLog(@"getEventItemForRequest:didReturn: %@", reply);
}

- (void)getEventItemFailedForRequest:(EventItemRequest *)request {
    
}

- (void)serviceConnectionToServerTimedOut {
    NSLog(@"serviceConnectionToServerTimedOut");
}

@end
