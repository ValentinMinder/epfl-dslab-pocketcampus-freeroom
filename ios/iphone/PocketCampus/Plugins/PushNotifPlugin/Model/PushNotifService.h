//
//  PushNotifService.h
//  PocketCampus
//
//

#import <Foundation/Foundation.h>

#import "Service.h"

#import "pushnotif.h"

@interface PushNotifService : Service <ServiceProtocol>

/*
 - (TequilaToken *) getTequilaTokenForPushNotif;  // throws TException
 - (PushNotifReply *) registerPushNotif: (PushNotifRegReq *) aPushNotifRequest;  // throws TException
*/

- (void)getTequilaTokenForPushNotifWithDelegate:(id)delegate;
- (void)registerPushNotif:(PushNotifRegReq*)request delegate:(id)delegate;

@end

@protocol PushNotifServiceDelegate <ServiceDelegate>

@optional
- (void)getTequilaTokenForPushNotifDidReturn:(TequilaToken*)token;
- (void)getTequilaTokenForPushNotifFailed;
- (void)registerPushNotifForRequest:(PushNotifRegReq*)request didReturn:(PushNotifReply*)reply;
- (void)registerPushNotifFailedForRequest:(PushNotifRegReq*)request;

@end
