
#import "PluginController.h"

#import "AuthenticationService.h"

@class CredentialsAlertViewController;

@interface AuthenticationController : PluginController<PluginControllerProtocol> {
    CredentialsAlertViewController* credentialsAlertViewController;
}

- (void)authToken:(NSString*)token delegate:(id<AuthenticationCallbackDelegate>)delegate;
- (void)deleteSavedCredentials;

@end
