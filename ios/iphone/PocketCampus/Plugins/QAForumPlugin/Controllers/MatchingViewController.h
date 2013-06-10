//
//  MatchingViewController.h
//  PocketCampus
//
//  Created by Susheng on 5/19/13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QAForumController.h"

@interface MatchingViewController : UIViewController<QAForumServiceDelegate> {
    NSArray *tableData;
    QAForumService* qaforumService;
    s_ask* question;
}

@property (retain, nonatomic) IBOutlet UITableView *tableview;
@property (strong, nonatomic) NSMutableDictionary* data;
@property (nonatomic,retain) NSArray *tableData;
@property (strong, nonatomic) s_ask* question;
@end
