




#import "PluginController.h"

#import "PushNotifService.h"

#import "AuthenticationController.h"

typedef enum {
    PushNotifRegistrationErrorInternal = 0,
} PushNotifDeviceRegistrationError;

typedef void (^PushNotifDeviceRegistrationFailureBlock)(PushNotifDeviceRegistrationError error);

typedef void (^NewNotificationBlock)(NSString* notifMessage, NSDictionary* notifFullDictionary);

/*
 * This class is used both as observer for registration and delegate for unregistration
 */
@interface PushNotifDeviceRegistrationObserver: NSObject<PushNotifServiceDelegate> 

@property (nonatomic, copy) VoidBlock successBlock;
@property (nonatomic, copy) PushNotifDeviceRegistrationFailureBlock failureBlock;

@end

@interface PushNotifController : PluginController<PluginControllerProtocol, UIAlertViewDelegate>


/*
 * Returns token of the device, when notifications were accepted
 * nil otherwise (or in simulator)
 */
+ (NSString*)notificationsDeviceToken;

/*
 * Each plugin that needs push notifications should call this method to be sure that the
 * device is registered for push notifications. If the plugin is the first one to register
 * and <reason> != nil, <reason> will be displayed to the user before prompting the 
 * accept/reject iOS notifications message. If plugin is not the first, the device is already
 * registered and success or failure is immediately executed, user having accepted or 
 * rejected the first time respectively.
 * You may display a message telling the user what he is missing in your failure message.
 */
- (void)registerDeviceForPushNotificationsWithPluginLowerIdentifier:(NSString*)pluginLowerIdentifier reason:(NSString*)reason success:(VoidBlock)success failure:(PushNotifDeviceRegistrationFailureBlock)failure;

/*
 * Any class from <pluginLowerIdentifier> can observe arrival of new notifications destinated
 * to <pluginLowerIdentifier>.
 * observer and pluginLowerIdentifier cannot be nil.
 * WARNING: classes must remove themselves with removeObserver:forPluginLowerIdentifier:
 * when being deallocated.
 */
- (void)addPushNotificationObserver:(id)observer forPluginLowerIdentifier:(NSString*)pluginLowerIdentifier newNotificationBlock:(NewNotificationBlock)newNotificationBlock;

/*
 * Classes that observe arrival of notifications (previous method) must use this method
 * to remove themselves as observers before being deallocated.
 */
- (void)removeObserver:(id)observer forPluginLowerIdentifier:(NSString*)pluginLowerIdentifier;

@end
