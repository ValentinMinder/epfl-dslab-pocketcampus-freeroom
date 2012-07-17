
#import "AuthenticationService.h"

#import "ASIHTTPRequest.h"
#import "ASIFormDataRequest.h"

#import "ObjectArchiver.h"

@implementation AuthenticationService

static NSString* kLastUsedUseramesKey = @"lastUsedUsernames";

static NSString* kSavedUsernameKey = @"SAVED_USERNAME_KEY";
static NSString* kSavedPasswordKey = @"SAVED_PASSWORD_KEY";

static AuthenticationService* instance = nil;

+ (id)sharedInstanceToRetain {
    if (instance != nil) {
        return instance;
    }
    @synchronized(self) {
        if (instance == nil) {
            instance = [[[self class] alloc] initWithServiceName:@"authentication"];
        }
    }
    return [instance autorelease];
}

- (id)thriftServiceClientInstance {
    return [[[AuthenticationServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]] autorelease];
}

+ (NSString*)savedUsername {
    return (NSString*)[ObjectArchiver objectForKey:kSavedUsernameKey andPluginName:@"authentication"];
}

+ (BOOL)saveUsername:(NSString*)username {
    return [ObjectArchiver saveObject:username forKey:kSavedUsernameKey andPluginName:@"authentication"];
}

+ (NSString*)savedPassword {
    return (NSString*)[ObjectArchiver objectForKey:kSavedPasswordKey andPluginName:@"authentication"];
}

+ (BOOL)savePassword:(NSString*)password {
    return [ObjectArchiver saveObject:password forKey:kSavedPasswordKey andPluginName:@"authentication"];
}

- (void)loginToTequilaWithUser:(NSString*)user password:(NSString*)password delegate:(id)delegate {
    NSURL *url = [NSURL URLWithString:TEQUILA_LOGIN_URL];    
    ASIFormDataRequest * request = [ASIFormDataRequest requestWithURL:url];
    request.cachePolicy = NSURLRequestReloadIgnoringLocalAndRemoteCacheData;
    [request setRequestMethod:@"POST"];
    [request setPostValue:user forKey:@"username"];
    [request setPostValue:password forKey:@"password"];
    [request setDelegate:delegate];
    request.shouldRedirect = NO;
    [request setDidFinishSelector:@selector(loginToTequilaDidReturn:)];
    [request setDidFailSelector:@selector(loginToTequilaFailed:)];
    [operationQueue addOperation:request];
}

- (void)authenticateToken:(NSString*)token withTequilaCookie:(NSString*)tequilaCookie delegate:(id)delegate {
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:TEQUILA_AUTH_URL, token]];
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
    request.cachePolicy = NSURLRequestReloadIgnoringLocalAndRemoteCacheData;
    [request addRequestHeader:@"Cookie" value:[NSString stringWithFormat:@"%@=%@", TEQUILA_COOKIE_NAME, tequilaCookie]];
    [request setDelegate:delegate];
    request.shouldRedirect = NO;
    [request setDidFinishSelector:@selector(authenticateTokenWithTequilaDidReturn:)];
    [request setDidFailSelector:@selector(authenticateTokenWithTequilaFailed:)];
    [operationQueue addOperation:request];
}

- (void)dealloc
{
    instance = nil;
    [super dealloc];
}

@end