//
//  questionViewController.h
//  PocketCampus
//
//  Created by Susheng on 12/14/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QAForumController.h"

@interface QuestionViewController : UIViewController <UITextFieldDelegate,UITableViewDataSource, UITableViewDelegate, UIActionSheetDelegate, UIAlertViewDelegate, QAForumServiceDelegate>{
    QAForumService* qaforumService;
    UITextField* activeField;
}
@property (retain, nonatomic) IBOutlet UITextView *answerText;
@property (retain, nonatomic) IBOutlet UIScrollView *scrollView;
@property (retain, nonatomic) IBOutlet UIActivityIndicatorView *centerIndicator;
@property (retain, nonatomic) IBOutlet UIButton *bnReport;
@property (retain, nonatomic) IBOutlet UILabel *lbAnswer;

@property (retain, nonatomic) IBOutlet UIButton *bnSubmit;
@property (nonatomic, copy) NSString *data;
@property (nonatomic, copy) NSString *othername;
@property int notificationid;
@property int pending;
@property int type;
//type=0: forwardid, type=1: questionid

- (IBAction)userAction:(UIButton *)sender;

@property (retain, nonatomic) IBOutlet UILabel *questionLabel;
@property (retain, nonatomic) IBOutlet UIButton *username;

@property (retain, nonatomic) UITextField *activeField;

- (IBAction)report:(UIButton *)sender;
- (IBAction)submit:(UIButton *)sender;
@end
