//
//  PluginViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 29.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

// Asbtract class and protocol for plugins view controllers

@protocol PluginViewControllerProtocol <NSObject>

@optional
- (void)cancelAllOperations;
- (void)refresh;

@end

@interface PluginViewController : UIViewController<PluginViewControllerProtocol> {
    @private 
    NSArray* toolbarItems;
    UIImage* homeViewIcon;
}

@property (readonly) NSArray* toolbarItems;
@property (readonly) UIImage* homeViewIcon;

@end
