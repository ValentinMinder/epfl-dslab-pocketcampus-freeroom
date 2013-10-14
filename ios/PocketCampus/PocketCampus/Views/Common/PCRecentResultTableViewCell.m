//
//  PCRecentResultTableViewCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 21.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PCRecentResultTableViewCell.h"

@implementation PCRecentResultTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.imageView.image = [UIImage imageNamed:@"ClockCell"];
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
    if (selected) {
        self.imageView.image = [UIImage imageNamed:@"ClockCellSelected"];
    } else {
        self.imageView.image = [UIImage imageNamed:@"ClockCell"];
    }
}

- (void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated {
    [super setHighlighted:highlighted animated:animated];
    if (self.highlighted) {
        self.imageView.image = [UIImage imageNamed:@"ClockCellSelected"];
    } else {
        self.imageView.image = [UIImage imageNamed:@"ClockCell"];
    }
}

@end
