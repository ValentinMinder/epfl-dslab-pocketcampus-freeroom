
#import "PluginController.h"

#import "AuthenticationService.h"

@class CredentialsAlertViewController;

@interface AuthenticationController : PluginController<PluginControllerProtocol> {
    CredentialsAlertViewController* credentialsAlertViewController;
}

- (void)loginToService:(int)typeOfService prefillWithLastUsedUsername:(BOOL)prefillUsername delegate:(id<AuthenticationCallbackDelegate>)delegate;
- (void)authToken:(NSString*)token delegate:(id<AuthenticationCallbackDelegate>)delegate;

@end
