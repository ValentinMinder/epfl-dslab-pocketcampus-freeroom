//
//  feedbackListViewController.h
//  PocketCampus
//
//  Created by Susheng on 12/14/12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//


#import <UIKit/UIKit.h>

#import "QAForumController.h"

@interface FeedbackListViewController : UIViewController <QAForumServiceDelegate> {
    NSDictionary *tableContents;
    NSArray *sortedKeys;
    QAForumService* qaforumService;
}

@property (nonatomic,retain) NSDictionary *tableContents;
@property (nonatomic,retain) NSArray *sortedKeys;

@property (strong, nonatomic) NSString* data;

@end
