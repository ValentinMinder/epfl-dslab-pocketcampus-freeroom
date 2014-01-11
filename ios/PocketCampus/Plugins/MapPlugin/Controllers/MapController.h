

//  Created by Loïc Gardiol on 12.04.12.


#import "PluginController.h"

@class MapItem;

@interface MapController : PluginController<PluginControllerProtocol>

+ (UIViewController*)viewControllerWithInitialMapItem:(MapItem*)mapItem;
+ (UIViewController*)viewControllerWithInitialSearchQuery:(NSString*)query;
+ (UIViewController*)viewControllerWithInitialSearchQuery:(NSString*)query pinLabelText:(NSString*)pinLabelText;

@end
