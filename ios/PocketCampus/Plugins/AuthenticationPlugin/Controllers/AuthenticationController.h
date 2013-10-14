
#import "PluginController.h"

#import "AuthenticationService.h"

#import "GasparViewController.h"

@class GasparViewController;

@interface PCLoginObserver : NSObject

@property (nonatomic, weak) id observer;
@property (nonatomic, copy) NSString* operationIdentifier;
@property (nonatomic, copy) VoidBlock successBlock;
@property (nonatomic, copy) VoidBlock userCancelledBlock;
@property (nonatomic, copy) VoidBlock failureBlock;

@end

@interface AuthenticationController : PluginController<PluginControllerProtocol>

/*
 * Use this method to authenticate a tequila token. Caller is called back
 * by implementing methods of protocol AuthenticationCallbackDelegate
 * defined in AuthenticationService.h
 * 
 * WARNING: this method cannot be called by multiple instances 
 * at the same time (1 delegate at a time). CRASH might occur if so.
 * This weakness should be corrected in future release by using
 * an observer pattern.
 */
- (void)authToken:(NSString*)token presentationViewController:(UIViewController*)presentationViewController delegate:(id<AuthenticationCallbackDelegate>)delegate;

@end
