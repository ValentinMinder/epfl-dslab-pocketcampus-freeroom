//
//  EventsSplashDetailViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 09.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "EventsSplashDetailViewController.h"

#import "PCValues.h"

@interface EventsSplashDetailViewController ()

@end

@implementation EventsSplashDetailViewController

- (id)init
{
    self = [super initWithNibName:@"EventsSplashDetailView" bundle:nil];
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
