//
//  MyEduModuleMaterialsViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 06.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MyEduService.h"

@interface MyEduModuleMaterialsViewController : UIViewController<MyEduServiceDelegate>

- (id)initWithMyEduModule:(MyEduModule*)module section:(MyEduSection*)section course:(MyEduCourse*)course;

@end
