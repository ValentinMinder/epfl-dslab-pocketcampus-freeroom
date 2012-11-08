//
//  MyEduModuleDetailViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import <QuartzCore/QuartzCore.h>

#import "MyEduService.h"

@interface MyEduModuleDetailViewController : UIViewController<UITabBarControllerDelegate>

@property (nonatomic, strong) IBOutlet UITabBarController* tabBarController;

- (id)initWithModule:(MyEduModule*)module section:(MyEduSection*)section course:(MyEduCourse*)course;

@end
