//
//  PCTableViewCellWithDownloadIndication.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PCTableViewCellWithDownloadIndication.h"

@interface PCTableViewCellWithDownloadIndication ()

@property (nonatomic, strong) UIImageView* icon;

@end

@implementation PCTableViewCellWithDownloadIndication

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.icon = [[UIImageView alloc]  initWithImage:[UIImage imageNamed:@"DownloadedCorner"]];
        self.icon.frame = CGRectMake(self.contentView.frame.size.width-self.icon.frame.size.width, 0, self.icon.frame.size.width, self.icon.frame.size.height);
        [self.contentView addSubview:self.icon];
        self.icon.hidden = YES; //not downloaded by default
    }
    return self;
}

- (void)setDownloaded:(BOOL)downloaded {
    _downloaded = downloaded;
    if (downloaded) {
        self.icon.hidden = NO;
    } else {
        self.icon.hidden = YES;
    }
    [self setNeedsDisplay];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.icon.frame = CGRectMake(self.contentView.frame.size.width-self.icon.frame.size.width, 0, self.icon.frame.size.width, self.icon.frame.size.height);
}

- (void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated {
    [super setHighlighted:highlighted animated:animated];
    if (self.highlighted) {
        self.icon.image = [UIImage imageNamed:@"DownloadedCornerSelected"];
    } else {
        self.icon.image = [UIImage imageNamed:@"DownloadedCorner"];
    }
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    if (self.selected) {
        self.icon.image = [UIImage imageNamed:@"DownloadedCornerSelected"];
    } else {
        self.icon.image = [UIImage imageNamed:@"DownloadedCorner"];
    }
}

- (void)willTransitionToState:(UITableViewCellStateMask)state {
    [super willTransitionToState:state];
    if (state != UITableViewCellStateDefaultMask) {
        self.icon.alpha = 0.0;
    }
}

- (void)didTransitionToState:(UITableViewCellStateMask)state {
    [super didTransitionToState:state];
    if (state == UITableViewCellStateDefaultMask) {
        self.icon.alpha = 1.0;
    }
}

@end
