
#import "Service.h"

#import "authentication.h"

#import "ASIHTTPRequest.h"

#define TEQUILA_LOGIN_URL @"https://tequila.epfl.ch/cgi-bin/tequila/login"
#define TEQUILA_AUTH_URL @"https://tequila.epfl.ch/cgi-bin/tequila/requestauth?requestkey=%@"
#define TEQUILA_COOKIE_NAME @"tequila_key"

@interface AuthenticationService : Service<ServiceProtocol>

/*
 authentication service methods
 */

+ (NSString*)savedUsername;
+ (BOOL)saveUsername:(NSString*)username;
+ (NSString*)savedPassword;
+ (BOOL)savePassword:(NSString*)password;
+ (NSNumber*)savePasswordSwitchWasOn;
+ (BOOL)savePasswordSwitchState:(BOOL)isOn;
+ (NSString*)logoutNotificationName;
+ (void)enqueueLogoutNotification;

- (void)loginToTequilaWithUser:(NSString*)user password:(NSString*)password delegate:(id)delegate;
- (void)authenticateToken:(NSString*)token withTequilaCookie:(NSString*)tequilaCookie delegate:(id)delegate;

@end

@protocol AuthenticationServiceDelegate <ServiceDelegate>

@optional
- (void)loginToTequilaDidReturn:(ASIHTTPRequest*)request;
- (void)loginToTequilaFailed:(ASIHTTPRequest*)request;
- (void)authenticateTokenWithTequilaDidReturn:(ASIHTTPRequest*)request;
- (void)authenticateTokenWithTequilaFailed:(ASIHTTPRequest*)request;

@end

@protocol AuthenticationCallbackDelegate

@required
- (void)authenticationSucceeded;
- (void)deleteSessionWhenFinished;
- (void)userCancelledAuthentication;
- (void)invalidToken;

@end
