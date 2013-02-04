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
    //self.imageView.layer.cornerRadius = 3; // if you like rounded corners
    self.imageView.layer.shadowOffset = CGSizeMake(0, 0);
    self.imageView.layer.shadowRadius = 3;
    self.imageView.layer.shadowOpacity = 0.45;
    self.imageView.hidden = YES;
    
    self.imageView.image = self.image;
    self.imageView.contentMode = UIViewContentModeScaleAspectFit;
    self.imageView.center = self.view.center;
    self.contentSizeForViewInPopover = self.view.frame.size;
    
    self.imageView.userInteractionEnabled = YES;
    UITapGestureRecognizer* gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(saveProfilePictureToCameraRoll)];
    gesture.numberOfTouchesRequired = 2;
    gesture.numberOfTapsRequired = 3;
    [self.imageView addGestureRecognizer:gesture];
}

- (void)saveProfilePictureToCameraRoll {
    NSLog(@"-> Saving profile picture");
    UIImageWriteToSavedPhotosAlbum(self.image, self, @selector(image:didFinishSavingWithError:contextInfo:), NULL);
}

- (void)image:(UIImage *)image didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo; {
    if (error) {
        [[[UIAlertView alloc] initWithTitle:@"Error" message:@"Error when saving image\nto camera roll" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
    } else {
        [[[UIAlertView alloc] initWithTitle:@"Image saved" message:@"Image correctly saved\nto camera roll" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
    }
    
}

- (void)viewDidAppear:(BOOL)animated {
    if (self.imageView.hidden) {
        [NSTimer scheduledTimerWithTimeInterval:0.2 target:self selector:@selector(showImage) userInfo:nil repeats:NO];
    }
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

- (void)showImage {
    self.imageView.alpha = 0.0;
    self.imageView.hidden = NO;
    [UIView animateWithDuration:0.2 animations:^{
        self.imageView.alpha = 1.0;
    }];
}

@end
