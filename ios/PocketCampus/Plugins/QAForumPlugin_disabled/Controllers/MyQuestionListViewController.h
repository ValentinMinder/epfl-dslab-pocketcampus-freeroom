//
//  myQuestionListViewController.h
//  PocketCampus
//
//  Created by Susheng on 12/12/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QAForumController.h"

#import "TableCell.h"

@interface MyQuestionListViewController : UIViewController <QAForumServiceDelegate> {
    NSDictionary *tableContents;
    NSArray *sortedKeys;
    QAForumService* qaforumService;
}

@property (retain, nonatomic) IBOutlet UITableView *tableview;
@property (nonatomic,retain) NSDictionary *tableContents;
@property (nonatomic,retain) NSArray *sortedKeys;
@property (retain, nonatomic) IBOutlet UIBarButtonItem *lbMyanswer;
@property (retain, nonatomic) IBOutlet UIBarButtonItem *lbMyquestion;

@property (strong, nonatomic) NSString* data;
- (IBAction)MyQuestions:(UIBarButtonItem *)sender;
- (IBAction)MyAnswers:(UIBarButtonItem *)sender;

@end
