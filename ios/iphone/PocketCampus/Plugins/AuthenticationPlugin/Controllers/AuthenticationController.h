
#import "PluginController.h"

#import "AuthenticationService.h"

@class CredentialsAlertViewController;

@interface AuthenticationController : PluginController<PluginControllerProtocol> {
    CredentialsAlertViewController* credentialsAlertViewController;
}

- (void)loginToService:(int)typeOfService delegate:(id<AuthenticationCallbackDelegate>)delegate;

@end
