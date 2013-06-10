//
//  myAnswerListViewController.h
//  PocketCampus
//
//  Created by Susheng on 12/12/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QAForumController.h"

#import "TableCell.h"

@interface MyAnswerListViewController : UIViewController <QAForumServiceDelegate> {
    NSDictionary *tableContents;
    NSArray *sortedKeys;
    QAForumService* qaforumService;
}

@property (retain, nonatomic) IBOutlet UITableView *tableview;
@property (nonatomic,retain) NSDictionary *tableContents;
@property (nonatomic,retain) NSArray *sortedKeys;

@property (strong, nonatomic) NSString* data;
- (IBAction)MyQuestionsList:(UIBarButtonItem *)sender;
- (IBAction)MyAnswersList:(UIBarButtonItem *)sender;

@end
