//
//  MyEduCourseListViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MyEduService.h"

#import "PCMasterSplitDelegate.h"

@interface MyEduCourseListViewController : UITableViewController<PCMasterSplitDelegate, MyEduServiceDelegate>

@end
