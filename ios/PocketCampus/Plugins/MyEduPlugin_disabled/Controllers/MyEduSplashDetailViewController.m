//
//  MyEduSplashDetailViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MyEduSplashDetailViewController.h"

#import "PCValues.h"

@interface MyEduSplashDetailViewController ()

@end

@implementation MyEduSplashDetailViewController

- (id)init
{
    self = [super initWithNibName:@"MyEduSplashDetailView" bundle:nil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.view.backgroundColor = [PCValues backgroundColor1];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}



@end
