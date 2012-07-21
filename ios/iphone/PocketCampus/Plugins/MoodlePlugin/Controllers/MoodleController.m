
#import "MoodleController.h"
#import "CoursesListViewController.h"

@implementation MoodleController

static NSString* name = nil;
static BOOL initObserversDone = NO;

- (id)init
{
    self = [super init];
    if (self) {
        CoursesListViewController* coursesListViewController = [[CoursesListViewController alloc] init];
        coursesListViewController.title = [[self class] localizedName];
        mainViewController = coursesListViewController;
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

+ (void)initObservers {
    @synchronized(self) {
        if (initObserversDone) {
            return;
        }
        [[NSNotificationCenter defaultCenter] addObserverForName:[AuthenticationService logoutNotificationName] object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *note) {
            NSLog(@"-> Moodle received %@ notification", [AuthenticationService logoutNotificationName]);
            [[MoodleService sharedInstanceToRetain] saveMoodleCookie:nil];
        }];
        initObserversDone = YES;
    }
}

+ (NSString*)localizedName {
    if (name != nil) {
        return name;
    }
    name = [NSLocalizedStringFromTable(@"PluginName", @"MoodlePlugin", @"") retain];
    return name;
}

+ (NSString*)identifierName {
    return @"Moodle";
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
