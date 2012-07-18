
#import "AuthenticationController.h"
#import "CredentialsViewController.h"

#import "CredentialsAlertViewController.h"

#import "GasparViewController.h"

#import "PCValues.h"

@implementation AuthenticationController

static NSString* name = nil;

- (id)init
{
    self = [super init];
    if (self) {
        CredentialsViewController* credentialsViewController = [[CredentialsViewController alloc] initWithNibName:@"CredentialsView" bundle:nil];
        credentialsViewController.title = [[self class] localizedName];
        mainViewController = credentialsViewController;
        
        credentialsAlertViewController = [[CredentialsAlertViewController alloc] init];
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
    NSLog(@"token to auth %@", token);
    NSString* savedPassword = [AuthenticationService savedPassword];
    NSLog(@"savedPassword : %@", savedPassword);
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
        gasparViewController.showSavePasswordSwitch = YES;
        gasparViewController.hideGasparUsageAccountMessage = YES;
        gasparViewController.delegate = delegate;
        gasparViewController.token = token;
        UINavigationController* tmpNavController = [[UINavigationController alloc] initWithRootViewController:gasparViewController]; //so that nav bar is shown
        tmpNavController.navigationBar.tintColor = [PCValues pocketCampusRed];
        
        if ([presentationViewController respondsToSelector:@selector(presentViewController:animated:completion:)]) { // >= iOS 5.0
            [presentationViewController presentViewController:tmpNavController animated:YES completion:^{
                [gasparViewController focusOnInput];
            }];
        } else {
            [presentationViewController presentModalViewController:tmpNavController animated:YES];
        }
        
        [tmpNavController release];
    }
    
}

- (void)deleteSavedCredentials {
    [AuthenticationService saveUsername:nil];
    [AuthenticationService savePassword:nil];
}

+ (NSString*)localizedName {
    if (name != nil) {
        return name;
    }
    name = [NSLocalizedStringFromTable(@"PluginName", @"AuthenticationPlugin", @"") retain];
    return name;
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
    [name release];
    name = nil;
    [super dealloc];
}

@end
