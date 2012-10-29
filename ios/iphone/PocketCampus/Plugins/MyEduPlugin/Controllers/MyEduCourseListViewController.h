//
//  MyEduCourseListViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "AuthenticationController.h"

#import "MyEduService.h"

@interface MyEduCourseListViewController : UITableViewController<MyEduServiceDelegate, AuthenticationCallbackDelegate>

@end
