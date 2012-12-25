//
//  NewsSplashViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "NewsSplashViewController.h"

#import "PCValues.h"

@interface NewsSplashViewController ()

@end

@implementation NewsSplashViewController

- (id)init
{
    self = [super initWithNibName:@"NewsSplashView" bundle:nil];
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

@end
