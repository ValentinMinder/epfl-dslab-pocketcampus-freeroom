//
//  DocumentViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 20.07.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MoodleService.h"

@interface DocumentViewController : UIViewController<UIWebViewDelegate, UIDocumentInteractionControllerDelegate, UIActionSheetDelegate, MoodleServiceDelegate> {
    UIWebView* webView;
    UIActivityIndicatorView* centerActivityIndicator;
    UILabel* centerMessageLabel;
    UIDocumentInteractionController* docInteractionController;
    UIActionSheet* deleteActionSheet;
    NSString* documentRemoteURLString;
    NSURL* documentLocalURL;
    MoodleService* moodleService;
}

@property (nonatomic, assign) IBOutlet UIWebView* webView;
@property (nonatomic, assign) IBOutlet UIActivityIndicatorView* centerActivityIndicator;
@property (nonatomic, assign) IBOutlet UILabel* centerMessageLabel;

- (id)initWithDocumentRemoteURLString:(NSString*)documentRemoteURL;

@end
