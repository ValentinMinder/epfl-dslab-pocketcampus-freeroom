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

@property (retain, nonatomic) IBOutlet UILabel *reportContent;
- (IBAction)submit:(UIButton *)sender;
@property (retain, nonatomic) IBOutlet UITextView *contentTextView;
@property (retain, nonatomic) IBOutlet UIButton *bnSubmit;
@property int forwardid;
@property int type;
@property (retain, nonatomic) UITextField *activeField;
@end
