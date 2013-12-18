//
//  feedbackViewController.h
//  PocketCampus
//
//  Created by Susheng on 12/14/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QAForumController.h"

#import "RateView.h"

@interface FeedbackViewController : UIViewController <QAForumServiceDelegate>
{
    QAForumService* qaforumService;
}
@property (retain, nonatomic) IBOutlet UILabel *questionLabel;
@property (retain, nonatomic) IBOutlet UILabel *answerLabel;
@property (retain, nonatomic) IBOutlet UIButton *bnUser;
- (IBAction)Relationship:(id)sender;
@property (retain, nonatomic) IBOutlet UIButton *bnReport;
@property (retain, nonatomic) IBOutlet UIActivityIndicatorView *centerIndicator;
@property (retain, nonatomic) IBOutlet UIScrollView *scrollView;

@property (nonatomic, copy) NSString *othername;

@property (retain, nonatomic) IBOutlet UILabel *rateLabel;
@property (retain, nonatomic) IBOutlet UILabel *feedbackLabel;
@property (retain, nonatomic) IBOutlet RateView *rateView;
@property (nonatomic, copy) NSString *data;
@property int notificationid;
- (IBAction)report:(UIButton *)sender;
@property float rate;
@property int pending;
@end
