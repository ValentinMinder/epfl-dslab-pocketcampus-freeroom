
#import "PluginController.h"

#import "AuthenticationService.h"

@class CredentialsAlertViewController;

@class GasparViewController;

@interface AuthenticationController : PluginController<PluginControllerProtocol> {
    CredentialsAlertViewController* credentialsAlertViewController;
    GasparViewController* gasparViewController;
}

- (void)authToken:(NSString*)token presentationViewController:(UIViewController*)presentationViewController delegate:(id<AuthenticationCallbackDelegate>)delegate;

@end
