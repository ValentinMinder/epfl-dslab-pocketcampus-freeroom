//
//  LatestForumViewController.h
//  PocketCampus
//
//  Created by Susheng on 4/20/13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QAForumController.h"

@interface LatestForumViewController : UIViewController<QAForumServiceDelegate> {
    NSArray *tableData;
    QAForumService* qaforumService;
}
@property (retain, nonatomic) IBOutlet UITableView *tableview;

@property (strong, nonatomic) NSString* data;
@property (nonatomic,retain) NSArray *tableData;

@property int locked;

@end
