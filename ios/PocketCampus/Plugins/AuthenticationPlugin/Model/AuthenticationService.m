
#import "AuthenticationService.h"

#import "ASIHTTPRequest.h"
#import "ASIFormDataRequest.h"

#import "ObjectArchiver.h"

#import "STKeychain.h"

@implementation AuthenticationService

/* __unused are added to prevent unused warning, even though the variables ARE actually used */

static NSString* kLastUsedUseramesKey __unused = @"lastUsedUsernames";
static NSString* kKeychainServiceKey = @"PCGasparPassword";
static NSString* kSavedUsernameKey = @"savedUsername";
static NSString* kSavePasswordSwitchStateKey = @"savePasswordSwitch";
static NSString* kLogoutNotificationKey = @"PCLogoutNotification";
static NSString* kDelayedUserInfoKey = @"PCDelayedUserInfoKey";

static AuthenticationService* instance __weak = nil;

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"AuthenticationService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"authentication"];
        if (self) {
            instance = self;
        }
        return self;
    }
}

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

- (id)thriftServiceClientInstance {
    return [[[AuthenticationServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]] autorelease];
}

+ (NSString*)savedUsername {
    return (NSString*)[ObjectArchiver objectForKey:kSavedUsernameKey andPluginName:@"authentication"];
}

+ (BOOL)saveUsername:(NSString*)username {
    return [ObjectArchiver saveObject:username forKey:kSavedUsernameKey andPluginName:@"authentication"];
}

+ (NSString*)savedPasswordForUsername:(NSString*)username {
    NSError* error = nil;
    NSString* password = [STKeychain getPasswordForUsername:username andServiceName:kKeychainServiceKey error:&error];
    if (error) {
        return nil;
    }
    return password;
}

+ (BOOL)savePassword:(NSString*)password forUsername:(NSString*)username {
    //return [ObjectArchiver saveObject:password forKey:kSavedPasswordKey andPluginName:@"authentication"];
    NSError* error = nil;
    [STKeychain storeUsername:username andPassword:password forServiceName:kKeychainServiceKey updateExisting:YES error:&error];
    if (error) {
        return NO;
    }
    return YES;
}

+ (BOOL)deleteSavedPasswordForUsername:(NSString*)username {
    NSError* error = nil;
    [STKeychain deleteItemForUsername:username andServiceName:kKeychainServiceKey error:&error];
    if (error) {
        return NO;
    }
    return YES;
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
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [super dealloc];
#endif
}

@end