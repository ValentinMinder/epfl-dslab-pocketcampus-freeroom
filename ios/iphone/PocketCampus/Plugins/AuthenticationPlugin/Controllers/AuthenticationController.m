
#import "AuthenticationController.h"
#import "CredentialsViewController.h"

#import "CredentialsAlertViewController.h"

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

- (void)loginToService:(int)typeOfService prefillWithLastUsedUsername:(BOOL)prefillUsername delegate:(id<AuthenticationCallbackDelegate>)delegate {
    [credentialsAlertViewController askCredientialsForTypeOfService:typeOfService message:nil prefillWithLastUsedUsername:(BOOL)prefillUsername delegate:delegate];
}

- (void)authToken:(NSString*)token delegate:(id<AuthenticationCallbackDelegate>)delegate {
    NSLog(@"token to auth %@", token);
    NSString* savedPassword = [AuthenticationService savedPassword];
    if(savedPassword == nil) {
        [credentialsAlertViewController askForCredentialsToken:token withMessage:nil delegate:delegate];
    } else {
        [credentialsAlertViewController authenticateSilentlyToken:token delegate:delegate];
    }
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
    [name release];
    name = nil;
    [super dealloc];
}

@end
