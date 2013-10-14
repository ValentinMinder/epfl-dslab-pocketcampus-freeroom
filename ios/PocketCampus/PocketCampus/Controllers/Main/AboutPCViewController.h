//
//  AboutPCViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.08.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface AboutPCViewController : UIViewController<UIWebViewDelegate> {
    UIWebView* webView;
}

@property (nonatomic, assign) IBOutlet  UIWebView* webView;

@end
