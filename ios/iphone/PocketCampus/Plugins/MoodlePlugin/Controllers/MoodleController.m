
#import "MoodleController.h"
#import "CoursesListViewController.h"

@implementation MoodleController

static NSString* name = nil;
static BOOL initObserversDone = NO;

- (id)init
{
    self = [super init];
    if (self) {
        CoursesListViewController* coursesListViewController = [[CoursesListViewController alloc] initWithNibName:@"CoursesListView" bundle:nil];
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
            [[NSUserDefaults standardUserDefaults] setObject:nil forKey:@"moodleCookie"];
            [[NSUserDefaults standardUserDefaults] synchronize];
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

- (void)cancelAllOperations {
    //TODO
}

- (void)refresh {
    //TODO
}

- (void)dealloc
{
    [name release];
    name = nil;
    [super dealloc];
}

@end
