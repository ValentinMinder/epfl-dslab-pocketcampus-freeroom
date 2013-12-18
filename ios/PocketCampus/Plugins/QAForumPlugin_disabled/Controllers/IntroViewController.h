//
//  IntroViewController.h
//  PocketCampus
//
//  Created by Susheng on 5/5/13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QAForumController.h"

@interface IntroViewController : UIViewController<UIScrollViewDelegate, QAForumServiceDelegate>{
    QAForumService* qaforumService;
    UIScrollView* scrollView;
    UIPageControl* pageControl;
}
@property (retain, nonatomic) IBOutlet UIScrollView *scrollView;
@property (retain, nonatomic) IBOutlet UIPageControl *pageControl;

@property (nonatomic, strong) NSArray *pageArray;
@end
