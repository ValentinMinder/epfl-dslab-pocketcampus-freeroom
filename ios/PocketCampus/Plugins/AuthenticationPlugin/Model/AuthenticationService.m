
#import "AuthenticationService.h"

#import "AFNetworking.h"

#import "ObjectArchiver.h"

#import "SSKeychain.h"

@implementation AuthenticationService

/* __unused are added to prevent unused warning, even though the variables ARE actually used */

static NSString* kTequilaLoginURL = @"https://tequila.epfl.ch/cgi-bin/tequila/login";
static NSString* kTequilaAuthURL = @"https://tequila.epfl.ch/cgi-bin/tequila/requestauth";

static NSString* kLastUsedUseramesKey __unused = @"lastUsedUsernames";
static NSString* kKeychainServiceKey = @"PCGasparPassword";
static NSString* kSavedUsernameKey = @"savedUsername";
static NSString* kSavePasswordSwitchStateKey = @"savePasswordSwitch";
static NSString* kLogoutNotificationKey = @"PCLogoutNotification";
static NSString* kDelayedUserInfoKey = @"PCDelayedUserInfoKey";

static AuthenticationService* instance __weak = nil;

#pragma mark - Init

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"AuthenticationService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"authentication" thriftServiceClientClassName:NSStringFromClass(AuthenticationServiceClient.class)];
        if (self) {
            instance = self;
        }
        return self;
    }
}

#pragma mark - ServiceProtocol

+ (id)sharedInstanceToRetain {
    @synchronized (self) {
        if (instance) {
            return instance;
        }
#if __has_feature(objc_arc)
        return [[[self class] alloc] init];
#else
        return [[[[self class] alloc] init] autorelease];
#endif
    }
}

#pragma mark -

+ (BOOL)isLoggedIn {
    return ([self savedPasswordForUsername:[self savedUsername]] != nil);
}

+ (NSString*)savedUsername {
    return (NSString*)[ObjectArchiver objectForKey:kSavedUsernameKey andPluginName:@"authentication"];
}

+ (BOOL)saveUsername:(NSString*)username {
    return [ObjectArchiver saveObject:username forKey:kSavedUsernameKey andPluginName:@"authentication"];
}

+ (NSString*)savedPasswordForUsername:(NSString*)username {
    NSError* error = nil;
    NSString* password = [SSKeychain passwordForService:kKeychainServiceKey account:username error:&error];
    if (error) {
        return nil;
    }
    return password;
}

+ (BOOL)savePassword:(NSString*)password forUsername:(NSString*)username {
    return [SSKeychain setPassword:password forService:kKeychainServiceKey account:username];
}

+ (BOOL)deleteSavedPasswordForUsername:(NSString*)username {
    return [SSKeychain deletePasswordForService:kKeychainServiceKey account:username];
}

+ (NSNumber*)savePasswordSwitchWasOn {
    return (NSNumber*)[ObjectArchiver objectForKey:kSavePasswordSwitchStateKey andPluginName:@"authentication"];
}

+ (BOOL)savePasswordSwitchState:(BOOL)isOn {
    return [ObjectArchiver saveObject:[NSNumber numberWithBool:isOn] forKey:kSavePasswordSwitchStateKey andPluginName:@"authentication"];
}

+ (NSString*)logoutNotificationName {
    return kLogoutNotificationKey;
}

+ (NSString*)delayedUserInfoKey {
    return kDelayedUserInfoKey;
}

+ (void)enqueueLogoutNotificationDelayed:(BOOL)delayed {
    NSDictionary* notifInfo = [NSDictionary dictionaryWithObject:[NSNumber numberWithBool:delayed] forKey:kDelayedUserInfoKey];
    NSNotification* notification = [NSNotification notificationWithName:kLogoutNotificationKey object:nil userInfo:notifInfo];
    [[NSNotificationQueue defaultQueue] enqueueNotification:notification postingStyle:NSPostASAP coalesceMask:NSNotificationCoalescingOnName forModes:nil]; //NSNotificationCoalescingOnName so that only 1 notif is added
}

- (void)loginToTequilaWithUser:(NSString*)user password:(NSString*)password delegate:(id)delegate {    
    [PCUtils throwExceptionIfObject:user notKindOfClass:[NSString class]];
    [PCUtils throwExceptionIfObject:password notKindOfClass:[NSString class]];
    NSMutableURLRequest* request = [[AFHTTPRequestSerializer serializer] requestWithMethod:@"POST" URLString:kTequilaLoginURL parameters:@{@"username":user, @"password":password}];
    request.cachePolicy = NSURLRequestReloadIgnoringLocalCacheData;
    AFHTTPRequestOperation* operation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
    id weakDelegate __weak = delegate;
    void (^failedBlock)(AuthenticationTequilaLoginFailureReason) = ^void (AuthenticationTequilaLoginFailureReason reason) {
        if ([weakDelegate respondsToSelector:@selector(loginToTequilaFailedWithReason:)]) {
            [weakDelegate loginToTequilaFailedWithReason:reason];
        }
    };
    [operation setRedirectResponseBlock:^NSURLRequest *(NSURLConnection *connection, NSURLRequest *request2, NSURLResponse *redirectResponse) {
        /*
         * Redirect block is actually called for original request as well.
         * This logic allows the first request to pursue but not the second one
         * => prevents redirect
         */
        if (!redirectResponse && connection.originalRequest == connection.currentRequest) {
            return request2;
        }
        return nil;
    }];
    [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSArray* allCookies = [NSHTTPCookie cookiesWithResponseHeaderFields:[operation.response allHeaderFields] forURL:operation.request.URL];
        NSHTTPCookie* tequilaCookie = nil;
        for (NSHTTPCookie* cookie in allCookies) {
            if ([cookie.name isEqualToString:kTequilaCookieName]) {
                tequilaCookie = cookie;
            }
        }
        if (tequilaCookie) {
            if ([weakDelegate respondsToSelector:@selector(loginToTequilaDidSuceedWithTequilaCookie:)]) {
                [weakDelegate loginToTequilaDidSuceedWithTequilaCookie:tequilaCookie];
            }
        } else {
            failedBlock(AuthenticationTequilaLoginFailureReasonBadCredentials);
        }
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        failedBlock(AuthenticationTequilaLoginFailureReasonOtherError);
    }];
    [self.operationQueue addOperation:operation];
}

- (void)authenticateToken:(NSString*)token withTequilaCookie:(NSHTTPCookie*)tequilaCookie delegate:(id)delegate {
    [PCUtils throwExceptionIfObject:token notKindOfClass:[NSString class]];
    [PCUtils throwExceptionIfObject:tequilaCookie notKindOfClass:[NSHTTPCookie class]];
    
    NSMutableURLRequest* request = [[AFHTTPRequestSerializer serializer] requestWithMethod:@"GET" URLString:kTequilaAuthURL parameters:@{@"requestkey":token}];
    request.cachePolicy = NSURLRequestReloadIgnoringLocalCacheData;
    [request addValue:[NSString stringWithFormat:@"%@=%@", kTequilaCookieName, tequilaCookie.value] forHTTPHeaderField:@"Cookie"];
    AFHTTPRequestOperation* operation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
    id weakDelegate __weak = delegate;
    
    VoidBlock failedBlock = ^() {
        if ([weakDelegate respondsToSelector:@selector(authenticateFailedForToken:tequilaCookie:)]) {
            [weakDelegate authenticateFailedForToken:token tequilaCookie:tequilaCookie];
        }
    };
    
    /*
     * We need the request to return 302 to succeed (=> token is authenticated and valid)
     * Any other code is considered as failed. Particularly, 200 means redirect to credentials page => failure
     */
    [operation setRedirectResponseBlock:^NSURLRequest *(NSURLConnection *connection, NSURLRequest *request2, NSURLResponse *redirectResponse) {
        /*
         * Redirect block is actually called for original request as well.
         * This logic allows the first request to pursue but not the second one
         * => prevents redirect
         */
        if (!redirectResponse && connection.originalRequest == connection.currentRequest) {
            return request2;
        }
        return nil;
    }];
    [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
        /*
         * Completion block handles all responses with status code in the range 2xx,
         * which is a failure in our case (see above)
         */
        failedBlock();
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        switch (operation.response.statusCode) {
            case 302:
                if ([weakDelegate respondsToSelector:@selector(authenticateDidSucceedForToken:tequilaCookie:)]) {
                    [weakDelegate authenticateDidSucceedForToken:token tequilaCookie:tequilaCookie];
                }
                break;
            default:
                failedBlock();
                break;
        }
    }];
    [self.operationQueue addOperation:operation];
}

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}

@end