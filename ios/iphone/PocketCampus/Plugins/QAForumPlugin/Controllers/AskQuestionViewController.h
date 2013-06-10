//
//  AskQuestionViewController.h
//  PocketCampus
//
//  Created by Susheng on 12/8/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QAForumController.h"
#import "MyQuestionViewController.h"
#import "UIPlaceHolderTextView.h"

@interface AskQuestionViewController : UIViewController <UIPickerViewDelegate, UIPickerViewDataSource, QAForumServiceDelegate, UIActionSheetDelegate> {
    QAForumService* qaforumService;
    UITextField* activeField;
    NSMutableDictionary *questionDic;
    UIActionSheet* sheet;
    s_ask* question;
}
@property (retain, nonatomic) IBOutlet UIButton *bnContinue;
@property (retain, nonatomic) IBOutlet UILabel *labQues;
@property (retain, nonatomic) IBOutlet UILabel *labTopic;
@property (retain, nonatomic) IBOutlet UILabel *labExpiry;
@property (retain, nonatomic) IBOutlet UIActivityIndicatorView *centerIndicator;

@property (retain, nonatomic) IBOutlet UITextField *topicTextView;
@property (retain, nonatomic) IBOutlet UILabel *labTags;

@property (retain, nonatomic) IBOutlet UIPlaceHolderTextView *QuestionText;
@property (retain, nonatomic) IBOutlet UITextField *QuestionTextView;
@property (retain, nonatomic) IBOutlet UITextField *expiryTimeTextView;
@property (retain, nonatomic) IBOutlet UITextField *tagsTextView;

- (IBAction)submitQuestion:(UIButton *)sender;
@property (retain, nonatomic) IBOutlet UIScrollView *scrollView;

@property (retain, nonatomic) UITextField *activeField;
@property (strong, nonatomic) NSArray* topicList;
@property (strong, nonatomic) s_ask* question;
@property int selectedRow;
@property int topicselectedRow;
@property int matchingNumbers;
@end
