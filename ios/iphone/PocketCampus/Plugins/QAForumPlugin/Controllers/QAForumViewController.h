//
//  QAForumViewController.h
//  PocketCampus
//
//  Created by Susheng on 12/5/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QAForumController.h"


@interface QAForumViewController : UIViewController<UITableViewDataSource, UITableViewDelegate, UIActionSheetDelegate, UIAlertViewDelegate, AuthenticationServiceDelegate,QAForumServiceDelegate>{
    QAForumService* qaforumService;
}

@property (nonatomic, strong) PushNotifController* pushController;

@property (retain, nonatomic) IBOutlet UIButton *bnAsk;
@property (retain, nonatomic) IBOutlet UIButton *bnForum;
@property (retain, nonatomic) IBOutlet UIButton *bnRecord;
@property (retain, nonatomic) IBOutlet UIButton *bnPending;
@property (retain, nonatomic) IBOutlet UILabel *labAsk;
@property (retain, nonatomic) IBOutlet UILabel *labForum;
@property (retain, nonatomic) IBOutlet UILabel *labRecord;
@property (retain, nonatomic) IBOutlet UILabel *labPending;

@property (retain, nonatomic) IBOutlet UISwitch *notificationswitch;
@property (nonatomic, strong) AuthenticationController* authController;
@property (nonatomic, strong) NSMutableArray* loginObservers;
@property (nonatomic, strong) QATequilaToken* tequilaToken;
@property (retain, nonatomic) IBOutlet UIBarButtonItem *bnSettings;
@property (retain, nonatomic) IBOutlet UIActivityIndicatorView *centerActivityIndicator;

@property (retain, nonatomic) IBOutlet UILabel *centerMessageLabel;
- (IBAction)askQuestionAction:(UIButton *)sender;

- (IBAction)forumAction:(UIButton *)sender;

- (IBAction)myQuestionListAction:(UIButton *)sender;

- (IBAction)PendingAction:(UIButton *)sender;

- (IBAction)settingAction:(UIBarButtonItem *)sender;

- (IBAction)notificationSwitch:(UISwitch *)sender;

- (void)addLoginObserver:(id)observer operationIdentifier:(NSString*)identifier successBlock:(VoidBlock)successBlock userCancelledBlock:(VoidBlock)userCancelledblock failureBlock:(VoidBlock)failureBlock; //will start login procedure with Authentication plugin and MyEduService to get final MyEduSession. Observer uniquely identifier by combination of observer address and operationIdentifier string comparison

@end
