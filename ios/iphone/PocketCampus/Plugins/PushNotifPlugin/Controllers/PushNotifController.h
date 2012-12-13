//
//  PushNotifController.h
//  PocketCampus
//

#import "PluginController.h"

#import "PushNotifService.h"

#import "AuthenticationController.h"

typedef enum {
    PushNotifDeviceRegistrationErrorUnknown = 0,
    PushNotifDeviceRegistrationErrorServerCommunication = 1,
    PushNotifDeviceRegistrationErrorUserDeniedPush = 2,
    PushNotifDeviceRegistrationErrorUserCancelledAuthentication = 3,
} PushNotifDeviceRegistrationError;

typedef void (^PushNotifDeviceRegistrationFailureBlock)(PushNotifDeviceRegistrationError error);

typedef void (^NewNotificationBlock)(NSString* notificationMessage);

@interface PushNotifDeviceRegistrationObserver : NSObject

@property (nonatomic, unsafe_unretained) id observer;
@property (nonatomic) BOOL authentified;
@property (nonatomic, copy) VoidBlock successBlock;
@property (nonatomic, copy) PushNotifDeviceRegistrationFailureBlock failureBlock;

@end

@interface PushNotifController : PluginController<PluginControllerProtocol, PushNotifServiceDelegate, AuthenticationCallbackDelegate>

- (void)addDeviceRegistrationObserver:(id)observer successBlock:(VoidBlock)successBlock failureBlock:(PushNotifDeviceRegistrationFailureBlock)failureBlock; //will start registration procedure of device token on PC server. Gaspar will not be linked to device token on PC server.
- (void)addAuthentifiedUserDeviceRegistrationObserver:(id)observer presentationViewControllerForAutentication:(UIViewController*)presController successBlock:(VoidBlock)successBlock failureBlock:(PushNotifDeviceRegistrationFailureBlock)failureBlock; //will start registration procedure of device on PC server, and will link device token to gaspar on PC server. presentationViewControllerForAutentication (give self usually) will be used to present GasparViewController if credentials not saved.
- (void)addDeviceUnregistrationObserver:(id)observer successBlock:(VoidBlock)successBlock failureBlock:(PushNotifDeviceRegistrationFailureBlock)failureBlock; //will start unregistration procedure of device token on PC server.
- (void)removeObserver:(id)observer;

- (void)addNotificationObserverWithPluginLowerIdentifier:(NSString*)pluginLowerIdentifier newNotificationBlock:(NewNotificationBlock)newNotificationBlock;

@end
