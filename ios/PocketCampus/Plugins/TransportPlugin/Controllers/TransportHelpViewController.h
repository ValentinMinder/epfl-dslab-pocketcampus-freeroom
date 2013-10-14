//
//  HelpViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 26.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TransportHelpViewController : UIViewController {
    UIWebView* webView;
    NSString* htmlFilePath;
}

- (id)initWithHTMLFilePath:(NSString*)htmlFilePath;

@property (nonatomic, assign) IBOutlet UIWebView* webView;

@end
