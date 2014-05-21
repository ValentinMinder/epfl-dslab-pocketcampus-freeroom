//
//  PendingTableCell.h
//  PocketCampus
//
//  Created by Susheng on 4/21/13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PendingTableCell : UITableViewCell
@property (strong, nonatomic) IBOutlet UILabel *content;

@property (strong, nonatomic) IBOutlet UILabel *username;
@property (strong, nonatomic) IBOutlet UILabel *time;
@property (retain, nonatomic) IBOutlet UILabel *orderNumber;
@end