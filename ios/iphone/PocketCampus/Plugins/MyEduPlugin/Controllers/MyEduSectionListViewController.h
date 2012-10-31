//
//  MyEduSectionListViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 31.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MyEduService.h"

#import "AuthenticationController.h"

@interface MyEduSectionListViewController : UITableViewController<MyEduServiceDelegate, AuthenticationCallbackDelegate>

- (id)initWithMyEduCourse:(MyEduCourse*)course;

@end
