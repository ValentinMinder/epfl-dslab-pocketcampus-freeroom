//
//  DirectoryProfilePictureViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 15.01.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "DirectoryProfilePictureViewController.h"

#import <QuartzCore/QuartzCore.h>

#import "PCValues.h"

@interface DirectoryProfilePictureViewController ()

@property (nonatomic, strong) UIImage* image;

@end

@implementation DirectoryProfilePictureViewController

- (id)initWithImage:(UIImage*)image
{
    self = [super initWithNibName:@"DirectoryProfilePictureView" bundle:nil];
    if (self) {
        self.image = image;
        self.title = NSLocalizedStringFromTable(@"Photo", @"DirectoryPlugin", nil);
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.view.backgroundColor = [PCValues backgroundColor1];
    
    self.imageView.layer.masksToBounds = NO;
    //self.imageView.layer.cornerRadius = 8; // if you like rounded corners
    self.imageView.layer.shadowOffset = CGSizeMake(0, 0);
    self.imageView.layer.shadowRadius = 3;
    self.imageView.layer.shadowOpacity = 0.4;
    
    self.imageView.image = self.image;
    self.imageView.contentMode = UIViewContentModeScaleAspectFit;
    self.imageView.center = self.view.center;
    self.contentSizeForViewInPopover = self.view.frame.size;
    //[self.view sizeToFit];
}

- (void)closeButtonPressed {
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation // iOS 5 and earlier
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

@end
