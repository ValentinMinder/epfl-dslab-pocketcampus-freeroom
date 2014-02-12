/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */






//  Created by Loïc Gardiol on 15.01.13.



#import "DirectoryProfilePictureViewController.h"

#import <QuartzCore/QuartzCore.h>

#import "PCValues.h"

@interface DirectoryProfilePictureViewController ()

@property (nonatomic, strong) UIImage* image;

@property (nonatomic, strong) IBOutlet NSLayoutConstraint* imageViewTopLayoutConstraint;

@end

@implementation DirectoryProfilePictureViewController

- (id)initWithImage:(UIImage*)image
{
    self = [super initWithNibName:@"DirectoryProfilePictureView" bundle:nil];
    if (self) {
        self.gaiScreenName = @"/directory/personPicture";
        self.image = image;
        self.title = NSLocalizedStringFromTable(@"Photo", @"DirectoryPlugin", nil);
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    if (!self.navigationController) {
        self.imageViewTopLayoutConstraint.constant = 0.0;
    }
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(dismiss)];
    self.preferredContentSize = CGSizeMake(self.image.size.width < 500 ? self.image.size.width : 500.0, self.image.size.height < 500 ? self.image.size.height : 500.0);
    self.imageView.image = self.image;
    self.imageView.contentMode = UIViewContentModeScaleAspectFit;
    self.preferredContentSize = self.imageView.frame.size;
    
    self.imageView.userInteractionEnabled = YES;
    UITapGestureRecognizer* gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(saveProfilePictureToCameraRoll)];
    gesture.numberOfTouchesRequired = 2;
    gesture.numberOfTapsRequired = 3;
    [self.imageView addGestureRecognizer:gesture];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
}

- (NSUInteger)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskPortrait;
}

- (void)dismiss {
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

- (void)saveProfilePictureToCameraRoll {
    CLSNSLog(@"-> Saving profile picture");
    UIImageWriteToSavedPhotosAlbum(self.image, self, @selector(image:didFinishSavingWithError:contextInfo:), NULL);
}

- (void)image:(UIImage *)image didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo; {
    if (error) {
        [[[UIAlertView alloc] initWithTitle:@"Error" message:@"Error when saving image\nto camera roll" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
    } else {
        [[[UIAlertView alloc] initWithTitle:@"Image saved" message:@"Image correctly saved\nto camera roll" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
    }
    
}

@end
