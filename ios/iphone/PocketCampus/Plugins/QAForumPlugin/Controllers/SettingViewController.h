//
//  settingViewController.h
//  PocketCampus
//
//  Created by Susheng on 12/13/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QAForumController.h"

@interface SettingViewController : UIViewController<QAForumServiceDelegate> {
        QAForumService* qaforumService;
}
@property (retain, nonatomic) IBOutlet UIButton *bn_English;
@property (retain, nonatomic) IBOutlet UIButton *bnFrench;
@property (retain, nonatomic) IBOutlet UIButton *bnDeutsch;
@property (retain, nonatomic) IBOutlet UIButton *bnItalian;
@property (retain, nonatomic) IBOutlet UIButton *bnTravel;
@property (retain, nonatomic) IBOutlet UIButton *bnStudy;

@property (retain, nonatomic) IBOutlet UIButton *bnLiving;

@property (retain, nonatomic) IBOutlet UIButton *bnOther;
@property (retain, nonatomic) IBOutlet UILabel *labLang;
@property (retain, nonatomic) IBOutlet UILabel *labTopic;
@property (retain, nonatomic) IBOutlet UILabel *labNotif;

- (IBAction)bnEnglishAction:(UIButton *)sender;
- (IBAction)bnFrenchAction:(UIButton *)sender;
- (IBAction)bnDeutsch:(UIButton *)sender;
- (IBAction)bnItalianAction:(UIButton *)sender;

- (IBAction)bnTravelAction:(UIButton *)sender;
- (IBAction)bnStudyAction:(UIButton *)sender;
- (IBAction)bnLivingAction:(UIButton *)sender;
- (IBAction)bnOtherAction:(UIButton *)sender;
- (IBAction)bnConfirmAction:(UIButton *)sender;

@property (retain, nonatomic) IBOutlet UIButton *bnConfirm;
@property int selectedRow;
@property int topicselectedRow;
@property (retain, nonatomic) IBOutlet UITextField *tvIterval;

@end
