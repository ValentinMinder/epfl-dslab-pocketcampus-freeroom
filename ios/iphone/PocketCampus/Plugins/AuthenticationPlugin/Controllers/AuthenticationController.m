
#import "AuthenticationController.h"
#import "CredentialsViewController.h"

@implementation AuthenticationController

static NSString* name = nil;

- (id)init
{
    self = [super init];
    if (self) {
        CredentialsViewController* credentialsViewController = [[CredentialsViewController alloc] initWithNibName:@"CredentialsView" bundle:nil];
        credentialsViewController.title = [[self class] localizedName];
        mainViewController = credentialsViewController;
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
    [name release];
    name = nil;
    [super dealloc];
}

@end
