//
//  acceptViewController.h
//  PocketCampus
//
//  Created by Susheng on 12/12/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QAForumController.h"



@interface AcceptViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, UIActionSheetDelegate, UIAlertViewDelegate, QAForumServiceDelegate>{
    QAForumService* qaforumService;
    NSString* data;
}

@property (strong, nonatomic) NSString* data;
@property int notificationid;


@property (retain, nonatomic) IBOutlet UILabel *notificationLable;
- (IBAction)acceptNotifiation:(UIButton *)sender;
- (IBAction)declineNotification:(UIButton *)sende;
@end
