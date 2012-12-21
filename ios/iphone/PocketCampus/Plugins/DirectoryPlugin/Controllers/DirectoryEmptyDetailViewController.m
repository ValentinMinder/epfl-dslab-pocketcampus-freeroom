//
//  NoContactDetailViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 21.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "DirectoryEmptyDetailViewController.h"

#import "PCValues.h"

#import <QuartzCore/QuartzCore.h>

@interface DirectoryEmptyDetailViewController ()

@end

@implementation DirectoryEmptyDetailViewController

- (id)init
{
    self = [super initWithNibName:@"DirectoryEmptyDetailView" bundle:nil];
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
    
    /* Crashes on device
     
    CAGradientLayer* gradient = [CAGradientLayer layer];
    gradient.frame = self.view.bounds;
    CGColorRef color1 = [UIColor colorWithRed:0.858824 green:0.870588 blue:0.898039 alpha:0.8].CGColor;
    CGColorRef color2 = [UIColor colorWithRed:0.772549 green:0.780392 blue:0.811765 alpha:0.8].CGColor;
    
    gradient.colors = [NSArray arrayWithObjects:(__bridge id)color1,(__bridge id)color2, nil];
    [self.view.layer insertSublayer:gradient atIndex:0];*/
    
    self.centerMessageLabel.text = NSLocalizedStringFromTable(@"NoContactSelected", @"DirectoryPlugin", nil);
    self.centerMessageLabel.shadowColor = [PCValues shadowColor1];
    self.centerMessageLabel.shadowOffset = [PCValues shadowOffset1];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
