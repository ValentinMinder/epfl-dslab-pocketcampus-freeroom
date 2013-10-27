//
//  MoodleSplashDetailViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 06.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MoodleSplashDetailViewController.h"

#import "MoodleController.h"


@interface MoodleSplashDetailViewController ()

@end

@implementation MoodleSplashDetailViewController

- (id)init
{
    self = [super initWithNibName:@"MoodleSplashDetailView" bundle:nil];
    if (self) {
        self.title = [MoodleController localizedName];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
}

@end
