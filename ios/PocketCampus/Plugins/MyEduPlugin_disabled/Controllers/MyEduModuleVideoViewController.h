//
//  MyEduModuleVideoViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import <QuartzCore/QuartzCore.h>

#import "MyEduService.h"

@interface MyEduModuleVideoViewController : UIViewController<UIWebViewDelegate, ASIHTTPRequestDelegate, UIActionSheetDelegate>

@property (nonatomic, weak) IBOutlet UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, weak) IBOutlet UILabel* centerMessageLabel;
@property (nonatomic, weak) IBOutlet UIButton* playbackRateButton;

- (id)initWithMyEduModule:(MyEduModule*)module;

- (void)destroyVideoPlayer; //call when MyEduModuleVideoViewController should be deallocated to be sure player does not prevent it (when playing)

@end
