//
//  MyEduController.h
//  PocketCampus
//

#import "PluginControllerAuthentified.h"

#import "AuthenticationController.h"

#import "MyEduService.h"

@interface MyEduController : PluginControllerAuthentified<PluginControllerProtocol, UISplitViewControllerDelegate, AuthenticationCallbackDelegate, MyEduServiceDelegate>


@end
