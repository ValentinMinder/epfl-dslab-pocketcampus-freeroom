//
//  myQuestionViewController.h
//  PocketCampus
//
//  Created by Susheng on 12/21/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QAForumController.h"

@interface MyQuestionViewController : UIViewController<QAForumServiceDelegate> {
    NSDictionary *tableContents;
    NSArray *sortedKeys;
}

@property (retain, nonatomic) IBOutlet UITableView *tableview;
@property (strong, nonatomic) NSString* data;
@property (strong, nonatomic) NSString* content;

@property (nonatomic,retain) NSDictionary *tableContents;
@property (nonatomic,retain) NSArray *sortedKeys;
@end
