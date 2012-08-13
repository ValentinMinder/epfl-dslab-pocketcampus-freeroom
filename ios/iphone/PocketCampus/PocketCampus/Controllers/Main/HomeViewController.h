//
//  HomeViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 29.02.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MainController.h"

@interface HomeViewController : UIViewController {
    @private MainController* mainController;
    UIButton* settingsButton;
}

- (id)initWithMainController:(MainController*)mainController_;
- (void)iconPressedWithIndex:(NSUInteger)index;

@property (nonatomic, assign) IBOutlet UIButton* settingsButton;

@end
