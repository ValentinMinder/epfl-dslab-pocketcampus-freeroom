//
//  PluginController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import "MainController.h"

/*Each plugin must have a controller named <plugin_name>Controller, and that subclasses PluginController. Is it not an instance of UIViewController*/

@interface PluginController : NSObject {
    MainController* mainController;
    UIViewController* mainViewController;
}

@property (readonly) NSArray* toolbarItems;
@property (readonly) UIViewController* mainViewController;

@end

@interface PluginController (PluginControllerWithObservers)

+ (void)initObservers;

@end

/* Protocol that each PluginController subclass should conform to */

@protocol PluginControllerProtocol <NSObject>

@required
- (id)initWithMainController:(MainController*)mainController_;
+ (NSString*)localizedName;
+ (NSString*)identifierName;
@optional
- (void)refresh;

@end