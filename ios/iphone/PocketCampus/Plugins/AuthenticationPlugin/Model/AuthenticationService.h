
#import "Service.h"

#import "authentication.h"

#import "ASIHTTPRequest.h"

#define TEQUILA_LOGIN_URL @"https://tequila.epfl.ch/cgi-bin/tequila/login"
#define TEQUILA_AUTH_URL @"https://tequila.epfl.ch/cgi-bin/tequila/requestauth?requestkey=%@"
#define TEQUILA_COOKIE_NAME @"tequila_key"

@interface AuthenticationService : Service<ServiceProtocol>

/*
 authentication service methods
 
 - (TequilaKey *) getTequilaKeyForService: (int) aService;  // throws TException
 - (SessionId *) getSessionIdForService: (TequilaKey *) aTequilaKey;  // throws TException
*/

- (void)getTequilaKeyForService:(int)service delegate:(id)delegate;
- (void)getSessionIdForServiceWithTequilaKey:(TequilaKey*)tequilaKey delegate:(id)delegate;

- (void)loginToTequilaWithUser:(NSString*)user password:(NSString*)password delegate:(id)delegate;
- (void)authenticateToken:(NSString*)token withTequilaCookie:(NSString*)tequilaCookie delegate:(id)delegate;

@end

@protocol AuthenticationServiceDelegate <ServiceDelegate>

@optional
- (void)getTequilaKeyForService:(int)service didReturn:(TequilaKey*)tequilaKey;
- (void)getTequilaKeyFailedForService:(int)service;
- (void)getSessionIdForServiceWithTequilaKey:(TequilaKey*)aTequilaKey didReturn:(SessionId*)aSessionId;
- (void)getSessionIdForServiceFailedForTequilaKey:(TequilaKey*)aTequilaKey;

- (void)loginToTequilaDidReturn:(ASIHTTPRequest*)request;
- (void)loginToTequilaFailed:(ASIHTTPRequest*)request;
- (void)authenticateTokenWithTequilaDidReturn:(ASIHTTPRequest*)request;
- (void)authenticateTokenWithTequilaFailed:(ASIHTTPRequest*)request;

@end

@protocol AuthenticationCallbackDelegate

@optional
- (int)getTypeOfService;
- (void)gotSessionId:(SessionId*)sessionId;
- (void)userCancelledAuthentication;
- (void)authenticationTimeout;

@end
