//
//  MyEduModuleListViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 02.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MyEduService.h"

#import "PCMasterSplitDelegate.h"

@interface MyEduModuleListViewController : UITableViewController<PCMasterSplitDelegate, MyEduServiceDelegate>

- (id)initWithMyEduCourse:(MyEduCourse*)course andSection:(MyEduSection*)section;

@end
