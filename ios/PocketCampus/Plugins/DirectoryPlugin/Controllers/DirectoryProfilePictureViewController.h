//
//  DirectoryProfilePictureViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 15.01.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface DirectoryProfilePictureViewController : UIViewController

@property (nonatomic, strong) IBOutlet UIImageView* imageView;

- (id)initWithImage:(UIImage*)image;

@end
