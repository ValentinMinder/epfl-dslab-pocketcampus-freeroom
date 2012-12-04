//
//  MoodleResourceViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MoodleController.h"

@interface MoodleResourceViewController : UIViewController<UIWebViewDelegate, UIDocumentInteractionControllerDelegate, UIActionSheetDelegate, MoodleServiceDelegate>

@property (nonatomic, weak) IBOutlet UIWebView* webView;
@property (nonatomic, weak) IBOutlet UILabel* centerMessageLabel;
@property (nonatomic, weak) IBOutlet UIProgressView* progressView;
@property (nonatomic, weak) IBOutlet UIActivityIndicatorView* loadingIndicator;

- (id)initWithMoodleResource:(MoodleResource*)moodleResource;

@end
