//
//  MyEduModuleMaterialsViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 06.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyEduModuleMaterialsViewController.h"

@interface MyEduModuleMaterialsViewController ()

@property (nonatomic, strong) MyEduService* service;
@property (nonatomic, strong) MyEduModule* module;
@property (nonatomic, strong) MyEduSection* section;
@property (nonatomic, strong) MyEduCourse* course;

@end

@implementation MyEduModuleMaterialsViewController

- (id)initWithMyEduModule:(MyEduModule*)module section:(MyEduSection*)section course:(MyEduCourse*)course
{
    self = [super initWithNibName:@"MyEduModuleMaterialsView" bundle:nil];
    if (self) {
        // Custom initialization
        _module = module;
        _section = section;
        _course = course;
        self.service = [MyEduService sharedInstanceToRetain];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    //MyEduModuleDetailsRequest* request = [[MyEduModuleDetailsRequest alloc] initWithICourseCode:self.course.iCode iSectionId:self.section.iId iModuleId:self.module.iId];
    //[self.service getModuleDetailsForRequest:request myeduRequest:[self.service createMyEduRequest] delegate:self];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - MyEduService Delegate

- (void)getModuleDetailsForRequest:(MyEduModuleDetailsRequest*)request myeduRequest:(MyEduRequest*)myeduRequest didReturn:(MyEduModuleDetailsReply*)reply {
    NSLog(@"%@", reply);
}

- (void)getModuleDetailsFailedForRequest:(MyEduModuleDetailsRequest *)request myeduRequest:(MyEduRequest*)myeduRequest {
    NSLog(@"Failed");
}

- (void)dealloc {
    [self.service cancelOperationsForDelegate:self];
}

@end
