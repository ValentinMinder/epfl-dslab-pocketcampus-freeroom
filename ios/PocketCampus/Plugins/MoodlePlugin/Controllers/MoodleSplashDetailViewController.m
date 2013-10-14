//
//  MoodleSplashDetailViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 06.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MoodleSplashDetailViewController.h"

#import "PCValues.h"

@interface MoodleSplashDetailViewController ()

@end

@implementation MoodleSplashDetailViewController

- (id)init
{
    self = [super initWithNibName:@"MoodleSplashDetailView" bundle:nil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    self.view.backgroundColor = [PCValues backgroundColor1];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
