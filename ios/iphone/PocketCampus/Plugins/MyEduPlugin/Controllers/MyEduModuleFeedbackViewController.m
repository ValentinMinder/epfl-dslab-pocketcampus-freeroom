//
//  MyEduModuleFeedbackViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 09.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyEduModuleFeedbackViewController.h"

@interface MyEduModuleFeedbackViewController ()

@end

@implementation MyEduModuleFeedbackViewController

- (id)initWithMyEduModule:(MyEduModule*)module section:(MyEduSection*)section course:(MyEduCourse*)course
{
    self = [super initWithNibName:@"MyEduModuleFeedbackView" bundle:nil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
