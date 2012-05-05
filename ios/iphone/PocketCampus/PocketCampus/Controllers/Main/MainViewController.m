//
//  MainViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 29.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MainViewController.h"
#import "HomeViewController.h"

@implementation MainViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    HomeViewController* homeViewController = [[HomeViewController alloc] initWithNibName:@"HomeView" bundle:nil];
    UINavigationController* navController = [[UINavigationController alloc] initWithRootViewController:homeViewController];
    navController.navigationBar.tintColor = [UIColor colorWithRed:0.66666 green:0 blue:0.101960 alpha:1.0];
    navController.toolbarHidden = NO;
    navController.toolbar.tintColor = [UIColor colorWithRed:0.26 green:0.305882 blue:0.321568  alpha:1.0];
    [self.view addSubview:navController.view];
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

@end
