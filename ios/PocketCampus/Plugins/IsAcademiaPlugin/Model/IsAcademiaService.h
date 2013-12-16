//
//  IsAcademiaService.h
//  PocketCampus
//
//  Created by Loïc Gardiol (loic.gardiol@gmail.com)
//

#import <Foundation/Foundation.h>

#import "Service.h"

#import "isacademia.h"

@protocol IsAcademiaServiceDelegate;

@interface IsAcademiaService : Service <ServiceProtocol>

/*
 - (ScheduleTokenResponse *) getScheduleToken;  // throws TException
 - (ScheduleResponse *) getSchedule: (ScheduleRequest *) req;  // throws TException
 */

- (void)getScheduleTokenWithDelegate:(id<IsAcademiaServiceDelegate>)delegate;
- (void)getScheduleWithRequest:(ScheduleRequest*)request delegate:(id<IsAcademiaServiceDelegate>)delegate;

@end

@protocol IsAcademiaServiceDelegate <ServiceDelegate>

@optional
- (void)getScheduleTokenDidReturn:(ScheduleTokenResponse*)scheduleTokenResponse;
- (void)getScheduleTokenFailed;
- (void)getScheduleForRequest:(ScheduleRequest*)request didReturn:(ScheduleResponse*)scheduleResponse;
- (void)getScheduleFailedForRequest:(ScheduleRequest*)request;

@end
