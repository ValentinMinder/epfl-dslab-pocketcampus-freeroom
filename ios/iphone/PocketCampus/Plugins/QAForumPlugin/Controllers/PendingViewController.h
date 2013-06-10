//
//  PendingViewController.h
//  PocketCampus
//
//  Created by Susheng on 4/19/13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QAForumController.h"

#import "TableCell.h"

@interface PendingViewController : UIViewController <QAForumServiceDelegate> {
    NSMutableDictionary *tableContents;
    NSArray *sortedKeys;
    QAForumService* qaforumService;
}

@property (retain, nonatomic) IBOutlet UITableView *tableView;
@property (nonatomic,retain) NSMutableDictionary *tableContents;
@property (nonatomic,retain) NSArray *sortedKeys;
@property (strong, nonatomic) NSString* data;

@property int forwardid;
@property int typeint;
@property int rowint;
@property int sectionint;




@end
