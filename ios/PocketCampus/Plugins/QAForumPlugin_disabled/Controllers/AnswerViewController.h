//
//  answerViewController.h
//  PocketCampus
//
//  Created by Susheng on 12/14/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QAForumController.h"

#import "RateView.h"

@interface AnswerViewController : UIViewController <QAForumServiceDelegate, RateViewDelegate> {
    QAForumService* qaforumService;
}
@property (retain, nonatomic) IBOutlet UILabel *lbfeedback;
@property (retain, nonatomic) IBOutlet UILabel *lbrate;

@property (retain, nonatomic) IBOutlet UILabel *questionLabel;
@property (retain, nonatomic) IBOutlet UILabel *answerLabel;
- (IBAction)submit:(UIButton *)sender;
- (IBAction)report:(UIButton *)sender;
@property (retain, nonatomic) IBOutlet UITextField *feedbackLabel;
@property (retain, nonatomic) IBOutlet RateView *rateView;
@property (retain, nonatomic) IBOutlet UIButton *bnUser;
@property (retain, nonatomic) IBOutlet UIButton *bnSubmit;
@property (retain, nonatomic) IBOutlet UIButton *bnReport;
@property (retain, nonatomic) IBOutlet UIActivityIndicatorView *centerIndicator;
- (IBAction)Relationship:(id)sender;

@property (nonatomic, copy) NSString *data;
@property (nonatomic, copy) NSString *othername;
@property int forwardid;
@property int pending;
@property float rate;

@end
