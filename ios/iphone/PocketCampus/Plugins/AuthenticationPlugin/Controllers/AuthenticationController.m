
#import "AuthenticationController.h"

#import "GasparViewController.h"

#import "PCValues.h"

@implementation AuthenticationController

- (id)init
{
    self = [super init];
    if (self) {
        gasparViewController = nil;
    }
    return self;
}

- (id)initWithMainController:(MainController *)mainController_
{
    self = [self init];
    if (self) {
        mainController = mainController_;
        
    }
    return self;
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
    [credentialsAlertViewController release];
    [gasparViewController release];
    [super dealloc];
}

@end
