//
//  MyEduModuleMaterialsViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 06.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MyEduService.h"

@interface MyEduModuleMaterialsViewController : UIViewController<MyEduServiceDelegate, UITableViewDelegate, UITableViewDataSource, UIWebViewDelegate, UIDocumentInteractionControllerDelegate>

@property (nonatomic, weak) IBOutlet UITableView* materialsTableView;
@property (nonatomic, weak) IBOutlet UIWebView* webView;
@property (nonatomic, weak) IBOutlet UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, weak) IBOutlet UILabel* centerMessageLabel;
@property (nonatomic, weak) IBOutlet UIProgressView* progressView;

- (id)initWithMyEduModule:(MyEduModule*)module section:(MyEduSection*)section course:(MyEduCourse*)course;

@end
