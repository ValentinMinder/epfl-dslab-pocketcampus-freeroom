//
//  AnswerRecViewController.h
//  PocketCampus
//
//  Created by Susheng on 5/24/13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QAForumController.h"

@interface AnswerRecViewController : UIViewController<QAForumServiceDelegate> {
    QAForumService* qaforumService;
}

@property (retain, nonatomic) IBOutlet UITableView *tableview;

@property (nonatomic, copy) NSString * data;
@property int forwardid;
@property int pending;
@property float rate;

@end
