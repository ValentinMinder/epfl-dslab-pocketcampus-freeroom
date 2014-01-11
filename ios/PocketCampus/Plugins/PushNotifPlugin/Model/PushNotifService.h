





#import <Foundation/Foundation.h>

#import "Service.h"

#import "pushnotif.h"

@interface PushNotifService : Service <ServiceProtocol>

/*
- (int32_t) deleteMapping: (NSString *) dummy;  // throws TException
*/

- (void)deleteMappingWithDummy:(NSString*)dummy delegate:(id)delegate;

@end

@protocol PushNotifServiceDelegate <ServiceDelegate>

@optional
- (void)deleteMappingForDummy:(NSString*)dummy didReturn:(int32_t)status;
- (void)deleteMappingFailedForDummy:(NSString*)dummy;

@end
