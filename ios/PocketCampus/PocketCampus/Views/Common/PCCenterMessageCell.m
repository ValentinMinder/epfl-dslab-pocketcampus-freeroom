//
//  PCCenterMessageCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 03.11.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PCCenterMessageCell.h"

static CGFloat kLeftRightMargins = 10.0;
static CGFloat kTopBottomMargins = 6.0;

@interface PCCenterMessageCell ()

@property (nonatomic, strong) UILabel* messageTextLabel;

@end

@implementation PCCenterMessageCell

- (id)initWithMessage:(NSString*)message
{
    self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
    if (self) {
        self.messageTextLabel = [[UILabel alloc] init];
        self.messageTextLabel.text = message;
        self.messageTextLabel.textColor = [UIColor colorWithRed:0.521569 green:0.521569 blue:0.521569 alpha:1.0]; //pretty light gray
        self.messageTextLabel.font = [UIFont boldSystemFontOfSize:16.0];
        self.messageTextLabel.textAlignment = UITextAlignmentCenter;
        self.messageTextLabel.numberOfLines = 0;
        self.messageTextLabel.adjustsFontSizeToFitWidth = YES;
        self.messageTextLabel.backgroundColor = [UIColor clearColor];
        [self.contentView addSubview:self.messageTextLabel];
        self.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.messageTextLabel.frame = CGRectMake(kLeftRightMargins, kTopBottomMargins, self.frame.size.width-(2.0*kLeftRightMargins), self.frame.size.height-(2.0*kTopBottomMargins));
}

@end
