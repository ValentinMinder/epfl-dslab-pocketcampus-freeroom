//
//  MyEduCourseInfoViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MyEduService.h"

@interface MyEduCourseInfoViewController : UIViewController

@property (nonatomic, strong) MyEduCourse* course;

@property (nonatomic, weak) IBOutlet UINavigationBar* navBar;
@property (nonatomic, weak) IBOutlet UIView* containerView;
@property (nonatomic, weak) IBOutlet UILabel* titleLabel;
@property (nonatomic, weak) IBOutlet UITextView* descriptionTextView;
@property (nonatomic, weak) IBOutlet UILabel* bottomLabel1;
@property (nonatomic, weak) IBOutlet UILabel* bottomLabel2;

- (id)initWithCourse:(MyEduCourse*)course;

@end
