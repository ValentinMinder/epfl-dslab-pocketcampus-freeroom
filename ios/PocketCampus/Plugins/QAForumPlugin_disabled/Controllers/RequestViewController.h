//
//  requestViewController.h
//  PocketCampus
//
//  Created by Susheng on 12/24/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QAForumController.h"

@interface RequestViewController : UIViewController <QAForumServiceDelegate> {
    QAForumService* qaforumService;
}
@property (retain, nonatomic) IBOutlet UITextField *tagsTextView;
@property (retain, nonatomic) IBOutlet UISwitch *Travel;
@property (retain, nonatomic) IBOutlet UISwitch *Study;
@property (retain, nonatomic) IBOutlet UISwitch *Living;
@property (retain, nonatomic) IBOutlet UISwitch *Others;

- (IBAction)request:(UIButton *)sender;
@end
