//
//  MyEduSectionListViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 31.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MyEduService.h"

#import "PCMasterSplitDelegate.h"

@interface MyEduSectionListViewController : UITableViewController<PCMasterSplitDelegate, MyEduServiceDelegate>

- (id)initWithMyEduCourse:(MyEduCourse*)course;

@end
