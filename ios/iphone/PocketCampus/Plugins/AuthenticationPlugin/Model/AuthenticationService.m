
#import "AuthenticationService.h"

#import "ASIHTTPRequest.h"
#import "ASIFormDataRequest.h"

#import "ObjectArchiver.h"

@implementation AuthenticationService

static NSString* kLastUsedUseramesKey = @"lastUsedUsernames";

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

+ (NSString*)lastUsedUsernameForService:(int)service {
    NSMutableDictionary* lastUsernamesForService = (NSMutableDictionary*)[ObjectArchiver objectForKey:kLastUsedUseramesKey andPluginName:@"authentication"];
    if (lastUsernamesForService == nil) {
        return nil;
    }
    return [lastUsernamesForService objectForKey:[NSString stringWithFormat:@"%d", service]];
}

+ (BOOL)saveLastUsedUsername:(NSString*)username forService:(int)service {
    NSMutableDictionary* lastUsernamesForService = (NSMutableDictionary*)[ObjectArchiver objectForKey:kLastUsedUseramesKey andPluginName:@"authentication"];
    if (lastUsernamesForService == nil) {
        lastUsernamesForService = [NSMutableDictionary dictionary];
    }
    [lastUsernamesForService setObject:username forKey:[NSString stringWithFormat:@"%d", service]];
    return [ObjectArchiver saveObject:lastUsernamesForService forKey:kLastUsedUseramesKey andPluginName:@"authentication"];
}

- (void)getTequilaKeyForService:(int)service delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getTequilaKeyForService:);
    operation.delegateDidReturnSelector = @selector(getTequilaKeyForService:didReturn:);
    operation.delegateDidFailSelector = @selector(getTequilaKeyFailedForService:);
    [operation addIntArgument:service];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getSessionIdForServiceWithTequilaKey:(TequilaKey*)tequilaKey delegate:(id)delegate {
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getSessionIdForService:);
    operation.delegateDidReturnSelector = @selector(getSessionIdForServiceWithTequilaKey:didReturn:);
    operation.delegateDidFailSelector = @selector(getSessionIdForServiceFailedForTequilaKey:);
    [operation addObjectArgument:tequilaKey];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)loginToTequilaWithUser:(NSString*)user password:(NSString*)password delegate:(id)delegate {
    NSURL *url = [NSURL URLWithString:TEQUILA_LOGIN_URL];    
    ASIFormDataRequest * request = [ASIFormDataRequest requestWithURL:url];
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