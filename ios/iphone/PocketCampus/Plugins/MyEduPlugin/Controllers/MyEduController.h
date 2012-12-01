//
//  MyEduController.h
//  PocketCampus
//

#import "PluginController.h"

#import "AuthenticationController.h"

#import "MyEduService.h"

@interface MyEduController : PluginController<PluginControllerProtocol, UISplitViewControllerDelegate, AuthenticationCallbackDelegate, MyEduServiceDelegate>

- (void)addLoginObserver:(id)observer operationIdentifier:(NSString*)identifier successBlock:(void (^)(void))successBlock userCancelledBlock:(void (^)(void))userCancelledblock failureBlock:(void (^)(void))failureBlock; //will start login procedure with Authentication plugin and MyEduService to get final MyEduSession. Observer uniquely identifier by combination of observer address and operationIdentifier string comparison
- (void)removeLoginObserver:(id)observer; //will remove all login operations observation linked with this observer
- (void)removeLoginObserver:(id)observer operationIdentifier:(NSString*)identifier; //will remove all logins operations linked with this observer and with this specific identifier

@end
