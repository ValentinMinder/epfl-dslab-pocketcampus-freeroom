//
//  MyEduCourseInfoViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyEduCourseInfoViewController.h"

#import "PCValues.h"

@interface MyEduCourseInfoViewController ()

@end

@implementation MyEduCourseInfoViewController

- (id)initWithCourse:(MyEduCourse*)course
{
    self = [super initWithNibName:@"MyEduCourseInfoView" bundle:nil];
    if (self) {
        _course = course;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    //self.view.backgroundColor = [PCValues backgroundColor1];
    
    /*self.titleLabel.textColor = [PCValues textColor1];
    self.titleLabel.shadowColor = [PCValues shadowColor1];
    self.titleLabel.shadowOffset = [PCValues shadowOffset1];
    
    self.descriptionTextView.textColor = [UIColor colorWithRed:0.478431 green:0.478431 blue:0.478431 alpha:1.0];
    
    self.bottomLabel.textColor = [UIColor colorWithRed:0.000000 green:0.231373 blue:0.733333 alpha:1.0];
    self.bottomLabel.shadowColor = [UIColor whiteColor];
    self.bottomLabel.shadowOffset = [PCValues shadowOffset1];*/
    self.navBar.tintColor = [PCValues pocketCampusRed];
    self.course = _course;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)setCourse:(MyEduCourse *)course {
    _course = course;
    self.titleLabel.text = self.course.iTitle;
    self.descriptionTextView.text = self.course.iDescription;
    
    
    NSDate* creationDate = [NSDate dateWithTimeIntervalSince1970:self.course.iCreationTimestamp/1000.0];
    NSString* creationDateString = [NSDateFormatter localizedStringFromDate:creationDate dateStyle:NSDateFormatterLongStyle timeStyle:NSDateFormatterNoStyle];
    NSString* createdOn = [[NSBundle mainBundle] localizedStringForKey:@"CreatedOn" value:@"" table:@"MyEduPlugin"];
    self.bottomLabel.text = [NSString stringWithFormat:@"%@ %@", createdOn, creationDateString];
}

@end
