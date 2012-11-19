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

@interface MyEduModuleVideoViewController : UIViewController<UIWebViewDelegate, NSURLConnectionDownloadDelegate, UIActionSheetDelegate>

@property (nonatomic, weak) IBOutlet UIWebView* webView;
@property (nonatomic, weak) IBOutlet UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, weak) IBOutlet UILabel* centerMessageLabel;

- (id)initWithMyEduModule:(MyEduModule*)module;

@end
