//
//  HelpViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 26.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface HelpViewController : UIViewController {
    UINavigationBar* navBar;
    UITextView* textView;
    
}

@property (nonatomic, assign) IBOutlet UINavigationBar* navBar;
@property (nonatomic, assign) IBOutlet UITextView* textView;

@end
