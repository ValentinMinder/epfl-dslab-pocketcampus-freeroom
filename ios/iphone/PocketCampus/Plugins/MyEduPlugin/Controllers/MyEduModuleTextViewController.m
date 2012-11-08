//
//  MyEduModuleTextViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyEduModuleTextViewController.h"

@interface MyEduModuleTextViewController ()

@property (nonatomic, strong) MyEduModule* module;

@end

@implementation MyEduModuleTextViewController

- (id)initWithMyEduModule:(MyEduModule*)module
{
    self = [super initWithNibName:@"MyEduModuleTextView" bundle:nil];
    if (self) {
        _module = module;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    if (!self.module.iTextContent || [self.module.iTextContent isEqualToString:@""]) {
        self.centerMessageLabel.text = NSLocalizedStringFromTable(@"NoTextInModule", @"MyEduPlugin", nil);
        self.centerMessageLabel.hidden = NO;
    } else {
        self.textView.text = self.module.iTextContent;
        //self.textView.contentInset = UIEdgeInsetsMake(3.0, 5.0, 0.0, 10.0);
        self.textView.hidden = NO;
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
