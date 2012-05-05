//
//  PluginViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 29.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PluginViewController.h"

@implementation PluginViewController

@synthesize toolbarItems, homeViewIcon;

- (id)init
{
    self = [super init];
    if (self) {
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

@end
