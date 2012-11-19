//
//  MyEduModuleVideoDownloadViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 18.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MyEduService.h"

@interface MyEduModuleVideoDownloadViewController : UIViewController

@property (nonatomic, weak) IBOutlet UILabel* titleLabel;
@property (nonatomic, weak) IBOutlet UILabel* progressionLabel;
@property (nonatomic, weak) IBOutlet UIProgressView* progressView;
@property (nonatomic, weak) IBOutlet UIButton* cancelButton;

- (id)initWithModule:(MyEduModule*)module;

@end
