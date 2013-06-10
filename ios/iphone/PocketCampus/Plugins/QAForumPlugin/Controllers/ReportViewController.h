//
//  reportViewController.h
//  PocketCampus
//
//  Created by Susheng on 1/19/13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>


#import "QAForumController.h"

@interface ReportViewController : UIViewController <UITextFieldDelegate,QAForumServiceDelegate>
{
    QAForumService* qaforumService;
    UITextField* activeField;
}
@property (retain, nonatomic) IBOutlet UIScrollView *scrollView;

- (IBAction)submit:(UIButton *)sender;
@property (retain, nonatomic) IBOutlet UITextView *contentTextView;
@property int forwardid;
@property int type;
@property (retain, nonatomic) UITextField *activeField;
@end
