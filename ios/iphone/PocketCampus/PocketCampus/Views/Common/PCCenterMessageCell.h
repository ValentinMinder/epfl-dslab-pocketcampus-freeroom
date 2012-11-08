//
//  PCCenterMessageCell.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 03.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PCCenterMessageCell : UITableViewCell

@property (nonatomic, readonly, strong) UILabel* messageTextLabel;

- (id)initWithMessage:(NSString*)message;

@end
