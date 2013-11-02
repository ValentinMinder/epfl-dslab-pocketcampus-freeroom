//
//  PCTableViewCellWithDownloadIndication.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PCTableViewCellAdditions.h"

@interface PCTableViewCellAdditions ()

@property (nonatomic, strong) UIImageView* icon;
@property (nonatomic, strong) UIColor* originalBackgroundColor;
@property (nonatomic, strong) UIColor* originalTextLabelColor;
@property (nonatomic, strong) UIColor* originalDetailTextLabelColor;

@end

@implementation PCTableViewCellAdditions

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.icon = [[UIImageView alloc]  initWithImage:[UIImage imageNamed:@"DownloadedCorner"]];
        self.icon.frame = CGRectMake(self.frame.size.width-self.icon.frame.size.width, 0, self.icon.frame.size.width, self.icon.frame.size.height);
        [self addSubview:self.icon];
        self.icon.hidden = YES; //no indication by default
        self.originalBackgroundColor = self.backgroundColor;
        self.originalTextLabelColor = self.textLabel.textColor;
        self.originalDetailTextLabelColor = self.detailTextLabel.textColor;
        self.textLabel.backgroundColor = [UIColor clearColor];
        self.detailTextLabel.backgroundColor = [UIColor clearColor];
        self.backgroundView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 1, 1)];
    }
    return self;
}

- (void)setDownloadedIndicationVisible:(BOOL)visible {
    _downloadedIndicationVisible = visible;
    if (visible) {
        self.icon.hidden = NO;
    } else {
        self.icon.hidden = YES;
    }
    [self setNeedsDisplay];
}

- (void)setFavoriteIndicationVisible:(BOOL)visible {
    _favoriteIndicationVisible = visible;
    if (visible) {
        self.icon.hidden = NO;
    } else {
        self.icon.hidden = YES;
    }
    [self setNeedsDisplay];
    
}

- (void)setDurablySelected:(BOOL)durablySelected {
    _durablySelected = durablySelected;
    if (durablySelected) {
        self.backgroundView.backgroundColor = [UIColor colorWithWhite:0.85 alpha:1.0];
    } else {
        self.backgroundView.backgroundColor = self.originalBackgroundColor;
        self.textLabel.textColor = self.originalTextLabelColor;
        self.detailTextLabel.textColor = self.originalDetailTextLabelColor;
    }
    [self updateCornerIcon];
    [self setNeedsDisplay];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.icon.frame = CGRectMake(self.frame.size.width-self.icon.frame.size.width, 0, self.icon.frame.size.width, self.icon.frame.size.height);
}

- (void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated {
    [super setHighlighted:highlighted animated:animated];
    [self updateCornerIcon];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    [self updateCornerIcon];
}

- (void)updateCornerIcon {
    if (self.selected || self.highlighted || self.durablySelected) {
        if (self.favoriteIndicationVisible) {
            self.icon.image = [UIImage imageNamed:@"FavoriteCornerSelected"];
        } else {
            self.icon.image = [UIImage imageNamed:@"DownloadedCornerSelected"];
        }
    } else {
        if (self.favoriteIndicationVisible) {
            self.icon.image = [UIImage imageNamed:@"FavoriteCorner"];
        } else {
            self.icon.image = [UIImage imageNamed:@"DownloadedCorner"];
        }
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
