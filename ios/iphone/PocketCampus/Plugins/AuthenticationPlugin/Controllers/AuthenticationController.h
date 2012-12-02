
#import "PluginController.h"

#import "AuthenticationService.h"

@class CredentialsAlertViewController;

@class GasparViewController;

@interface PCLoginObserver : NSObject

@property (nonatomic, assign) id observer;
@property (nonatomic, copy) NSString* operationIdentifier;
@property (nonatomic, copy) VoidBlock successBlock;
@property (nonatomic, copy) VoidBlock userCancelledBlock;
@property (nonatomic, copy) VoidBlock failureBlock;

@end

@interface AuthenticationController : PluginController<PluginControllerProtocol> {
    CredentialsAlertViewController* credentialsAlertViewController;
    GasparViewController* gasparViewController;
}

- (void)authToken:(NSString*)token presentationViewController:(UIViewController*)presentationViewController delegate:(id<AuthenticationCallbackDelegate>)delegate;

@end
