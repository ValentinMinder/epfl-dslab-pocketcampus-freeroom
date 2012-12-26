
#import "PluginControllerAuthentified.h"

#import "AuthenticationController.h"

#import "MoodleService.h"

@interface MoodleController : PluginControllerAuthentified<UISplitViewControllerDelegate, PluginControllerProtocol, AuthenticationCallbackDelegate, MoodleServiceDelegate>

@end
