//
//  MyEduModuleTextViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MyEduService.h"

@interface MyEduModuleTextViewController : UIViewController

@property (nonatomic, weak) IBOutlet UITextView* textView;
@property (nonatomic, weak) IBOutlet UILabel* centerMessageLabel;

- (id)initWithMyEduModule:(MyEduModule*)module;

@end
