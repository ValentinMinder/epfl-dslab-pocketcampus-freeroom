
#import "AuthenticationController.h"

#import "GasparViewController.h"

#import "PCValues.h"


#pragma mark - PCLoginObserver implementation

@implementation PCLoginObserver

@synthesize observer, operationIdentifier, successBlock, userCancelledBlock, failureBlock;

- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    return [self isEqualToPCLoginObserver:object];
}

- (BOOL)isEqualToPCLoginObserver:(PCLoginObserver*)loginObserver {
    return self.observer == loginObserver.observer && [self.operationIdentifier isEqual:loginObserver.operationIdentifier];
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    hash += [self.observer hash];
    hash += [self.operationIdentifier hash];
    return hash;
}

@end


#pragma mark - AuthenticationController implementation

static AuthenticationController* instance __weak = nil;

@implementation AuthenticationController

- (id)init
{
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"AuthenticationController cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super init];
        if (self) {
            instance = self;
        }
        return self;
    }
}

+ (id)sharedInstance {
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

- (void)authToken:(NSString*)token presentationViewController:(UIViewController*)presentationViewController delegate:(id<AuthenticationCallbackDelegate>)delegate; {
    NSString* savedPassword = [AuthenticationService savedPasswordForUsername:[AuthenticationService savedUsername]];
    [gasparViewController release];
    gasparViewController = [[GasparViewController alloc] init];
    if (savedPassword) {
        gasparViewController.presentationMode = PresentationModeTryHidden;
        gasparViewController.viewControllerForPresentation = presentationViewController;
        gasparViewController.showSavePasswordSwitch = YES;
        gasparViewController.hideGasparUsageAccountMessage = YES;
        [gasparViewController authenticateSilentlyToken:token delegate:delegate];
    } else {
        gasparViewController.presentationMode = PresentationModeModal;
        gasparViewController.viewControllerForPresentation = presentationViewController;
        gasparViewController.showSavePasswordSwitch = YES;
        gasparViewController.hideGasparUsageAccountMessage = YES;
        gasparViewController.delegate = delegate;
        gasparViewController.token = token;
        UINavigationController* tmpNavController = [[UINavigationController alloc] initWithRootViewController:gasparViewController]; //so that nav bar is shown
        tmpNavController.modalPresentationStyle = UIModalPresentationFormSheet;
        tmpNavController.navigationBar.tintColor = [PCValues pocketCampusRed];
        
        [presentationViewController presentViewController:tmpNavController animated:YES completion:^{
            [gasparViewController focusOnInput];
        }];
        
        [tmpNavController release];
    }
    
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"AuthenticationPlugin", @"");
}

+ (NSString*)identifierName {
    return @"Authentication";
}

- (NSString*)localizedStringForKey:(NSString*)key {
    return NSLocalizedStringFromTable(key, [[self class] identifierName], @"");
}

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
#if __has_feature(objc_arc)
#else
    [credentialsAlertViewController release];
    [gasparViewController release];
    [super dealloc];
#endif
}

@end
