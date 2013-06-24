//
//  myAnswerViewController.h
//  PocketCampus
//
//  Created by Susheng on 12/23/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QAForumController.h"

@interface MyAnswerViewController : UIViewController<QAForumServiceDelegate> {
    NSDictionary *tableContents;
    NSArray *sortedKeys;
}
@property (retain, nonatomic) IBOutlet UITableView *tableview;

@property (strong, nonatomic) NSString* data;

@property (nonatomic,retain) NSDictionary *tableContents;
@property (nonatomic,retain) NSArray *sortedKeys;

@end
