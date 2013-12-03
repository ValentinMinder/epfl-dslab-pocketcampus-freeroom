
#import "Service.h"

#import "authentication.h"

typedef enum {
    AuthenticationTequilaLoginFailureReasonBadCredentials,
    AuthenticationTequilaLoginFailureReasonOtherError
} AuthenticationTequilaLoginFailureReason;


static NSString* kTequilaCookieName __unused = @"tequila_key";

@interface AuthenticationService : Service<ServiceProtocol>

/*
 authentication service methods
 */

+ (BOOL)isLoggedIn;
+ (NSString*)savedUsername;
+ (BOOL)saveUsername:(NSString*)username;
+ (NSString*)savedPasswordForUsername:(NSString*)username;
+ (BOOL)savePassword:(NSString*)password forUsername:(NSString*)username;
+ (BOOL)deleteSavedPasswordForUsername:(NSString*)username;
+ (NSNumber*)savePasswordSwitchWasOn;
+ (BOOL)savePasswordSwitchState:(BOOL)isOn;
+ (NSString*)logoutNotificationName;
+ (NSString*)delayedUserInfoKey;
+ (void)enqueueLogoutNotificationDelayed:(BOOL)delayed; //set delayed to YES to inform the receiver of the notif. that it should logout only when user has finished (leaving plugin)

- (void)loginToTequilaWithUser:(NSString*)user password:(NSString*)password delegate:(id)delegate;
- (void)authenticateToken:(NSString*)token withTequilaCookie:(NSHTTPCookie*)tequilaCookie delegate:(id)delegate;

@end

@protocol AuthenticationServiceDelegate <ServiceDelegate>

@optional
- (void)loginToTequilaDidSuceedWithTequilaCookie:(NSHTTPCookie*)tequilaCookie;
- (void)loginToTequilaFailedWithReason:(AuthenticationTequilaLoginFailureReason)reason;

- (void)authenticateDidSucceedForToken:(NSString*)token tequilaCookie:(NSHTTPCookie*)tequilaCookie;
- (void)authenticateFailedForToken:(NSString*)token tequilaCookie:(NSHTTPCookie*)tequilaCookie;

@end

@protocol AuthenticationCallbackDelegate

@required
- (void)authenticationSucceeded;
- (void)userCancelledAuthentication;
- (void)invalidToken;

@end
