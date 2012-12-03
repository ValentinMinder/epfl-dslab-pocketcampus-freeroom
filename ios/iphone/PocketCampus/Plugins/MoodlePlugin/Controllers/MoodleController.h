
#import "PluginController.h"

#import "AuthenticationController.h"

#import "MoodleService.h"

@interface MoodleController : PluginController<PluginControllerProtocol, AuthenticationCallbackDelegate, MoodleServiceDelegate>

- (void)addLoginObserver:(id)observer operationIdentifier:(NSString*)identifier successBlock:(VoidBlock)successBlock userCancelledBlock:(VoidBlock)userCancelledblock failureBlock:(VoidBlock)failureBlock; //will start login procedure with Authentication plugin and MyEduService to get final MyEduSession. Observer uniquely identifier by combination of observer address and operationIdentifier string comparison
- (void)removeLoginObserver:(id)observer; //will remove all login operations observation linked with this observer
- (void)removeLoginObserver:(id)observer operationIdentifier:(NSString*)identifier; //will remove all logins operations linked with this observer and with this specific identifier

@end
