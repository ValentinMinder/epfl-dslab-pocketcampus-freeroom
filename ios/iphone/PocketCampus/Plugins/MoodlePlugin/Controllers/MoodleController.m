
#import "MoodleController.h"
#import "CoursesListViewController.h"

#import "ObjectArchiver.h"

@implementation MoodleController

static BOOL initObserversDone = NO;
static NSString* kDeleteSessionAtInitKey = @"DeleteSessionAtInit";

- (id)init
{
    self = [super init];
    if (self) {
        [[self class] deleteSessionIfNecessary];
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

+ (void)deleteSessionIfNecessary {
    NSNumber* deleteSession = (NSNumber*)[ObjectArchiver objectForKey:kDeleteSessionAtInitKey andPluginName:@"moodle"];
    if (deleteSession && [deleteSession boolValue]) {
        NSLog(@"-> Delayed logout notification on Moodle now applied : deleting sessionId");
        [MoodleService deleteMoodleCookie];
        [ObjectArchiver saveObject:nil forKey:kDeleteSessionAtInitKey andPluginName:@"moodle"];
    }
}

+ (void)initObservers {
    @synchronized(self) {
        if (initObserversDone) {
            return;
        }
        [[NSNotificationCenter defaultCenter] addObserverForName:[AuthenticationService logoutNotificationName] object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification *notification) {
            NSNumber* delayed = [notification.userInfo objectForKey:[AuthenticationService delayedUserInfoKey]];
            if ([delayed boolValue]) {
                NSLog(@"-> Moodle received %@ notification delayed", [AuthenticationService logoutNotificationName]);
                [ObjectArchiver saveObject:[NSNumber numberWithBool:YES] forKey:kDeleteSessionAtInitKey andPluginName:@"moodle"];
            } else {
                NSLog(@"-> Moodle received %@ notification", [AuthenticationService logoutNotificationName]);
                [MoodleService deleteMoodleCookie]; //removing stored session
            }
        }];
        initObserversDone = YES;
    }
}

+ (NSString*)localizedName {
    return NSLocalizedStringFromTable(@"PluginName", @"MoodlePlugin", @"");
}

+ (NSString*)identifierName {
    return @"Moodle";
}

- (NSString*)localizedStringForKey:(NSString*)key {
    return NSLocalizedStringFromTable(key, [[self class] identifierName], @"");
}

- (void)dealloc
{
    [[self class] deleteSessionIfNecessary];
    [super dealloc];
}

@end
